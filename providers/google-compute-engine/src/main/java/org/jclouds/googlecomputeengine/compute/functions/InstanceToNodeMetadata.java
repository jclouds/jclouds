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
package org.jclouds.googlecomputeengine.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;

import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.jclouds.compute.util.ComputeServiceUtils.groupFromMapOrName;

public final class InstanceToNodeMetadata implements Function<Instance, NodeMetadata> {

   private final Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus;
   private final GroupNamingConvention nodeNamingConvention;
   private final LoadingCache<URI, Optional<Image>> diskURIToImage;
   private final Supplier<Map<URI, Hardware>> hardwares;
   private final Supplier<Map<URI, Location>> locationsByUri;

   @Inject InstanceToNodeMetadata(Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus,
                                  GroupNamingConvention.Factory namingConvention,
                                  LoadingCache<URI, Optional<Image>> diskURIToImage,
                                  @Memoized Supplier<Map<URI, Hardware>> hardwares,
                                  @Memoized Supplier<Map<URI, Location>> locationsByUri) {
      this.toPortableNodeStatus = toPortableNodeStatus;
      this.nodeNamingConvention = namingConvention.createWithoutPrefix();
      this.diskURIToImage = diskURIToImage;
      this.hardwares = hardwares;
      this.locationsByUri = locationsByUri;
   }

   @Override public NodeMetadata apply(Instance input) {
      String group = groupFromMapOrName(input.metadata().asMap(), input.name(), nodeNamingConvention);
      NodeMetadataBuilder builder = new NodeMetadataBuilder();

      Location zone = locationsByUri.get().get(input.zone());
      if (zone == null) {
         throw new IllegalStateException(
               String.format("zone %s not present in %s", input.zone(), locationsByUri.get().keySet()));
      }

      // The boot disk is the first disk. It may have been created from an image, so look it up.
      //
      // Note: This will be present if we created the node. In the future we could choose to make diskToSourceImage
      // a loading cache. That would be more expensive, but could ensure this isn't null.

      URI diskSource = input.disks().get(0).source();
      Optional<Image> image = diskURIToImage.getUnchecked(diskSource);

      Hardware hardware;
      if (isCustomMachineTypeURI(input.machineType())) {
         hardware = machineTypeURIToCustomHardware(input.machineType());
      }
      else {
         hardware = hardwares.get().get(input.machineType());
      }

      builder.id(input.selfLink().toString())
             .name(input.name())
             .providerId(input.id())
             .hostname(input.name())
             .location(zone)
             .imageId(image.isPresent() ? image.get().selfLink().toString() : null)
             .hardware(hardware)
             .status(input.status() != null ? toPortableNodeStatus.get(input.status()) : Status.UNRECOGNIZED)
             .tags(input.tags().items())
             .uri(input.selfLink())
             .userMetadata(input.metadata().asMap())
             .group(group)
             .privateAddresses(collectPrivateAddresses(input))
             .publicAddresses(collectPublicAddresses(input));
      return builder.build();
   }

   private List<String> collectPrivateAddresses(Instance input) {
      ImmutableList.Builder<String> privateAddressesBuilder = ImmutableList.builder();
      for (Instance.NetworkInterface networkInterface : input.networkInterfaces()) {
         if (networkInterface.networkIP() != null) {
            privateAddressesBuilder.add(networkInterface.networkIP());
         }
      }
      return privateAddressesBuilder.build();
   }

   private List<String> collectPublicAddresses(Instance input) {
      ImmutableList.Builder<String> publicAddressesBuilder = ImmutableList.builder();
      for (Instance.NetworkInterface networkInterface : input.networkInterfaces()) {
         for (Instance.NetworkInterface.AccessConfig accessConfig : networkInterface.accessConfigs()) {
            if (accessConfig.natIP() != null) {
               publicAddressesBuilder.add(accessConfig.natIP());
            }
         }
      }
      return publicAddressesBuilder.build();
   }

   public static boolean isCustomMachineTypeURI(URI machineType) {
      return machineType.toString().contains("machineTypes/custom");
   }

   public static Hardware machineTypeURIToCustomHardware(URI machineType) {
      String uri = machineType.toString();
      String values = uri.substring(uri.lastIndexOf('/') + 8);
      List<String> hardwareValues = Splitter.on('-')
            .trimResults()
            .splitToList(values);
      return new HardwareBuilder()
            .id(uri)
            .providerId(uri)
            .processor(new Processor(Double.parseDouble(hardwareValues.get(0)), 1.0))
            .ram(Integer.parseInt(hardwareValues.get(1)))
            .uri(machineType)
            .build();
   }
}
