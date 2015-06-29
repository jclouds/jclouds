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
package org.jclouds.profitbricks.compute.config;

import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_PERIOD;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_MAX_PERIOD;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_PREDICATE_DATACENTER;
import static org.jclouds.profitbricks.config.ProfitBricksComputeProperties.POLL_TIMEOUT;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Volume;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.lifecycle.Closer;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstZone;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.compute.ProfitBricksComputeServiceAdapter;
import org.jclouds.profitbricks.compute.concurrent.ProvisioningJob;
import org.jclouds.profitbricks.compute.concurrent.ProvisioningManager;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.compute.function.DataCenterToLocation;
import org.jclouds.profitbricks.compute.function.LocationToLocation;
import org.jclouds.profitbricks.compute.function.ProvisionableToImage;
import org.jclouds.profitbricks.compute.function.ServerToNodeMetadata;
import org.jclouds.profitbricks.compute.function.StorageToVolume;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusAware;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusPollingPredicate;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.internal.Provisionable;
import org.jclouds.util.Predicates2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ProfitBricksComputeServiceContextModule extends
        ComputeServiceAdapterContextModule<Server, Hardware, Provisionable, DataCenter> {

   @Override
   protected void configure() {
      super.configure();

      install(new LocationsFromComputeServiceAdapterModule<Server, Hardware, Provisionable, DataCenter>() {
      });

      install(new FactoryModuleBuilder().build(ProvisioningJob.Factory.class));

      bind(ImplicitLocationSupplier.class).to(OnlyLocationOrFirstZone.class).in(Singleton.class);

      bind(new TypeLiteral<ComputeServiceAdapter<Server, Hardware, Provisionable, DataCenter>>() {
      }).to(ProfitBricksComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<org.jclouds.profitbricks.domain.Location, Location>>() {
      }).to(LocationToLocation.class);

      bind(new TypeLiteral<Function<DataCenter, Location>>() {
      }).to(DataCenterToLocation.class);

      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);

      bind(new TypeLiteral<Function<Provisionable, Image>>() {
      }).to(ProvisionableToImage.class);

      bind(new TypeLiteral<Function<Storage, Volume>>() {
      }).to(StorageToVolume.class);

      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));
   }

   @Provides
   @Singleton
   @Named(POLL_PREDICATE_DATACENTER)
   Predicate<String> provideWaitDataCenterUntilAvailablePredicate(
           final ProfitBricksApi api, ComputeConstants constants) {
      return Predicates2.retry(new ProvisioningStatusPollingPredicate(
              api, ProvisioningStatusAware.DATACENTER, ProvisioningState.AVAILABLE),
              constants.pollTimeout(), constants.pollPeriod(), constants.pollMaxPeriod(), TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   ProvisioningManager provideProvisioningManager(Closer closer) {
      ProvisioningManager provisioningManager = new ProvisioningManager();
      closer.addToClose(provisioningManager);

      return provisioningManager;
   }

   @Singleton
   public static class ComputeConstants {

      @Inject
      @Named(POLL_TIMEOUT)
      private String pollTimeout;

      @Inject
      @Named(POLL_PERIOD)
      private String pollPeriod;

      @Inject
      @Named(POLL_MAX_PERIOD)
      private String pollMaxPeriod;

      public long pollTimeout() {
         return Long.parseLong(pollTimeout);
      }

      public long pollPeriod() {
         return Long.parseLong(pollPeriod);
      }

      public long pollMaxPeriod() {
         return Long.parseLong(pollMaxPeriod);
      }
   }
}
