/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.elasticstack.compute.config;

import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.util.Predicates2.retry;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.domain.Location;
import org.jclouds.elasticstack.ElasticStackApi;
import org.jclouds.elasticstack.compute.ElasticStackComputeServiceAdapter;
import org.jclouds.elasticstack.compute.functions.ServerInfoToNodeMetadata;
import org.jclouds.elasticstack.compute.functions.ServerInfoToNodeMetadata.DeviceToVolume;
import org.jclouds.elasticstack.compute.functions.ServerInfoToNodeMetadata.GetImageIdFromServer;
import org.jclouds.elasticstack.compute.functions.StandardDriveToWellKnownImage;
import org.jclouds.elasticstack.compute.functions.WellKnownImageToImage;
import org.jclouds.elasticstack.domain.Device;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.elasticstack.domain.StandardDrive;
import org.jclouds.elasticstack.domain.WellKnownImage;
import org.jclouds.elasticstack.predicates.DriveClaimed;
import org.jclouds.elasticstack.suppliers.WellKnownImageSupplier;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class ElasticStackComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<ServerInfo, Hardware, DriveInfo, Location> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<ServerInfo, Hardware, DriveInfo, Location>>() {
      }).to(ElasticStackComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<ServerInfo, NodeMetadata>>() {
      }).to(ServerInfoToNodeMetadata.class);
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Device, Volume>>() {
      }).to(DeviceToVolume.class);
      bind(new TypeLiteral<Function<Server, String>>() {
      }).to(GetImageIdFromServer.class);
      bind(new TypeLiteral<Function<DriveInfo, Image>>() {
      }).to(WellKnownImageToImage.class);
      bind(new TypeLiteral<Function<StandardDrive, WellKnownImage>>() {
      }).to(StandardDriveToWellKnownImage.class);
      bind(new TypeLiteral<Supplier<List<WellKnownImage>>>() {
      }).to(WellKnownImageSupplier.class);
   }

   @Provides
   @Singleton
   protected LoadingCache<String, DriveInfo> cache(GetDrive getDrive) {
      return CacheBuilder.newBuilder().build(getDrive);
   }

   @Singleton
   public static class GetDrive extends CacheLoader<String, DriveInfo> {
      private final ElasticStackApi client;

      @Inject
      public GetDrive(ElasticStackApi client) {
         this.client = client;
      }

      @Override
      public DriveInfo load(String input) {
         return client.getDriveInfo(input);
      }
   }

   @Singleton
   @Provides
   @Memoized
   protected Supplier<Map<String, WellKnownImage>> provideImages(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         @Memoized final Supplier<List<WellKnownImage>> wellKnownImageSupplier) throws IOException {
      // The image map won't change. Memoize it during the session.
      // This map can't be created directly as a singleton, as Guice needs it to construct the ElasticStackComputeServiceAdapter
      // and a misconfiguration such as invalid credentials, etc would cause the Guice injection to fail
      return memoizeWithExpiration(new Supplier<Map<String, WellKnownImage>>() {
         @Override
         public Map<String, WellKnownImage> get() {
            return Maps.uniqueIndex(wellKnownImageSupplier.get(), new Function<WellKnownImage, String>() {
               @Override
               public String apply(WellKnownImage input) {
                  return input.getUuid();
               }
            });
         }
      }, seconds, TimeUnit.SECONDS);
   }
   
   @Singleton
   @Provides
   @Memoized
   protected Supplier<List<WellKnownImage>> provideWellKnownImageSupplier(AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, WellKnownImageSupplier uncached)
         throws IOException {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, uncached, seconds,
            TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Predicate<DriveInfo> supplyDriveUnclaimed(DriveClaimed driveClaimed, Timeouts timeouts) {
      return retry(Predicates.not(driveClaimed), timeouts.nodeRunning, 1000, MILLISECONDS);
   }
}
