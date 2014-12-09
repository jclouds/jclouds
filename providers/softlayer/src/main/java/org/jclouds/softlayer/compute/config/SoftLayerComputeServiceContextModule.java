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
package org.jclouds.softlayer.compute.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.compute.functions.DatacenterToLocation;
import org.jclouds.softlayer.compute.functions.OperatingSystemToImage;
import org.jclouds.softlayer.compute.functions.VirtualGuestToHardware;
import org.jclouds.softlayer.compute.functions.VirtualGuestToNodeMetadata;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.compute.strategy.SoftLayerComputeServiceAdapter;
import org.jclouds.softlayer.domain.ContainerVirtualGuestConfiguration;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class SoftLayerComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<VirtualGuest, Hardware, OperatingSystem, Datacenter> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<VirtualGuest, Hardware, OperatingSystem, Datacenter>>() {
      }).to(SoftLayerComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<VirtualGuest, NodeMetadata>>() {
      }).to(VirtualGuestToNodeMetadata.class);
      bind(new TypeLiteral<Function<OperatingSystem, org.jclouds.compute.domain.Image>>() {
      }).to(OperatingSystemToImage.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<VirtualGuest, org.jclouds.compute.domain.Hardware>>() {
      }).to(VirtualGuestToHardware.class);
      bind(new TypeLiteral<Function<Datacenter, Location>>() {
      }).to(DatacenterToLocation.class);
      bind(TemplateOptions.class).to(SoftLayerTemplateOptions.class);
      // to have the compute service adapter override default locations
      install(new LocationsFromComputeServiceAdapterModule<VirtualGuest, Hardware, OperatingSystem, Datacenter>() {
      });

   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<ContainerVirtualGuestConfiguration> getCreateObjectOptions(
           AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
           final SoftLayerApi api) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
              new Supplier<ContainerVirtualGuestConfiguration>() {
                 @Override
                 public ContainerVirtualGuestConfiguration get() {
                    return api.getVirtualGuestApi().getCreateObjectOptions();
                 }

                 @Override
                 public String toString() {
                    return Objects.toStringHelper(api)
                            .add("method", "virtualGuestApi.getCreateObjectOptions")
                            .toString();
                 }
              }, seconds, TimeUnit.SECONDS);
   }

}
