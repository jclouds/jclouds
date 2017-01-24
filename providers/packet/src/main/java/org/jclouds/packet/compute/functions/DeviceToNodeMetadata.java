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
package org.jclouds.packet.compute.functions;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.IpAddress;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static com.google.common.collect.FluentIterable.from;

/**
 * Transforms an {@link Device} to the jclouds portable model.
 */
@Singleton
public class DeviceToNodeMetadata implements Function<Device, NodeMetadata> {

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final PlanToHardware planToHardware;
    private final OperatingSystemToImage operatingSystemToImage;
    private final FacilityToLocation facilityToLocation;
    private final Function<Device.State, NodeMetadata.Status> toPortableStatus;
    private final GroupNamingConvention groupNamingConvention;

    @Inject
    DeviceToNodeMetadata(PlanToHardware planToHardware, OperatingSystemToImage operatingSystemToImage, FacilityToLocation facilityToLocation,
                         Function<Device.State, NodeMetadata.Status> toPortableStatus,
                         GroupNamingConvention.Factory groupNamingConvention) {
        this.planToHardware = planToHardware;
        this.operatingSystemToImage = operatingSystemToImage;
        this.facilityToLocation = facilityToLocation;
        this.toPortableStatus = toPortableStatus;
        this.groupNamingConvention = groupNamingConvention.createWithoutPrefix();
    }

   @Override
   public NodeMetadata apply(Device input) {
      return new NodeMetadataBuilder()
              .ids(input.id())
              .name(input.hostname())
              .hostname(input.hostname())
              .group(groupNamingConvention.extractGroup(input.hostname()))
              .location(facilityToLocation.apply(input.facility()))
              .hardware(planToHardware.apply(input.plan()))
              .imageId(input.operatingSystem().slug())
              .operatingSystem(operatingSystemToImage.apply(input.operatingSystem()).getOperatingSystem())
              .status(toPortableStatus.apply(input.state()))
              .publicAddresses(getPublicIpAddresses(input.ipAddresses()))
              .privateAddresses(getPrivateIpAddresses(input.ipAddresses()))
              .tags(input.tags())
              .build();
   }

   private Iterable<String> getPublicIpAddresses(List<IpAddress> input) {
      return filterAndTransformIpAddresses(input, new IsPublicIpAddress());
   }

   private Iterable<String> getPrivateIpAddresses(List<IpAddress> input) {
      return filterAndTransformIpAddresses(input, Predicates.not(new IsPublicIpAddress()));
   }

   private Iterable<String> filterAndTransformIpAddresses(List<IpAddress> input, Predicate<IpAddress> filter) {
      return from(input).filter(filter).transform(new IpAddressToIp());
   }

   private static class IpAddressToIp implements Function<IpAddress, String> {
        @Override
        public String apply(final IpAddress input) {
            return input.address();
        }
    }

    private static class IsPublicIpAddress implements Predicate<IpAddress> {
        @Override
        public boolean apply(IpAddress input) {
            return input.publicAddress();
        }
    }
}
