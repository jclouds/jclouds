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
package org.jclouds.packet.compute.config;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.location.Provider;
import org.jclouds.packet.PacketApi;
import org.jclouds.packet.compute.PacketComputeServiceAdapter;
import org.jclouds.packet.compute.functions.DeviceStateToStatus;
import org.jclouds.packet.compute.functions.DeviceToNodeMetadata;
import org.jclouds.packet.compute.functions.FacilityToLocation;
import org.jclouds.packet.compute.functions.OperatingSystemToImage;
import org.jclouds.packet.compute.functions.PlanToHardware;
import org.jclouds.packet.compute.options.PacketTemplateOptions;
import org.jclouds.packet.compute.strategy.CreateSshKeysThenCreateNodes;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.Facility;
import org.jclouds.packet.domain.OperatingSystem;
import org.jclouds.packet.domain.Plan;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.util.Predicates2.retry;

public class PacketComputeServiceContextModule extends
        ComputeServiceAdapterContextModule<Device, Plan, OperatingSystem, Facility> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<ComputeServiceAdapter<Device, Plan, OperatingSystem, Facility>>() {
      }).to(PacketComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<Device, NodeMetadata>>() {
      }).to(DeviceToNodeMetadata.class);
      bind(new TypeLiteral<Function<Plan, Hardware>>() {
      }).to(PlanToHardware.class);
      bind(new TypeLiteral<Function<OperatingSystem, Image>>() {
      }).to(OperatingSystemToImage.class);
      bind(new TypeLiteral<Function<Facility, Location>>() {
      }).to(FacilityToLocation.class);
      bind(new TypeLiteral<Function<Device.State, NodeMetadata.Status>>() {
      }).to(DeviceStateToStatus.class);
      install(new LocationsFromComputeServiceAdapterModule<Device, Plan, OperatingSystem, Facility>() {
      });
      bind(TemplateOptions.class).to(PacketTemplateOptions.class);
      bind(CreateNodesInGroupThenAddToSet.class).to(CreateSshKeysThenCreateNodes.class);
   }

   @Provides
   @Named(TIMEOUT_NODE_RUNNING)
   protected Predicate<String> provideDeviceRunningPredicate(final PacketApi api,
                                                             @Provider final Supplier<Credentials> creds,
                                                             ComputeServiceConstants.Timeouts timeouts,
                                                             ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new DeviceInStatusPredicate(api, creds.get().identity, Device.State.ACTIVE), timeouts.nodeRunning,
              pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected Predicate<String> provideDeviceSuspendedPredicate(final PacketApi api, @Provider final Supplier<Credentials> creds, ComputeServiceConstants.Timeouts timeouts,
                                                                 ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new DeviceInStatusPredicate(api, creds.get().identity, Device.State.INACTIVE), timeouts.nodeSuspended,
              pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }
   
   @Provides
   @Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<String> provideDeviceTerminatedPredicate(final PacketApi api, @Provider final Supplier<Credentials> creds, ComputeServiceConstants.Timeouts timeouts,
                                                                 ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new DeviceTerminatedPredicate(api, creds.get().identity), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
              pollPeriod.pollMaxPeriod);
   }

   @VisibleForTesting
   static class DeviceInStatusPredicate implements Predicate<String> {

      private final PacketApi api;
      private final String projectId;
      private final Device.State state;

      public DeviceInStatusPredicate(PacketApi api, String projectId, Device.State state) {
         this.api = checkNotNull(api, "api must not be null");
         this.projectId = checkNotNull(projectId, "projectId must not be null");
         this.state = checkNotNull(state, "state must not be null");
      }

      @Override
      public boolean apply(String input) {
         checkNotNull(input, "device id");
         Device device = api.deviceApi(projectId).get(input);
         return device != null && state == device.state();
      }
   }

   @VisibleForTesting
   static class DeviceTerminatedPredicate implements Predicate<String> {

      private final PacketApi api;
      private final String projectId;

      public DeviceTerminatedPredicate(PacketApi api, String projectId) {
         this.api = checkNotNull(api, "api must not be null");
         this.projectId = checkNotNull(projectId, "projectId must not be null");
      }

      @Override
      public boolean apply(String input) {
         checkNotNull(input, "device id");
         Device device = api.deviceApi(projectId).get(input);
         return device == null;
      }
   }

}
