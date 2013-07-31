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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceInZone;
import org.jclouds.googlecomputeengine.domain.SlashEncodedIds;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * Transforms a google compute domain Instance into a generic NodeMetatada object.
 *
 * @author David Alves
 */
public class InstanceInZoneToNodeMetadata implements Function<InstanceInZone, NodeMetadata> {

   private final Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus;
   private final GroupNamingConvention nodeNamingConvention;
   private final Supplier<Map<URI, ? extends Image>> images;
   private final Supplier<Map<URI, ? extends Hardware>> hardwares;
   private final Supplier<Map<URI, ? extends Location>> locations;

   @Inject
   public InstanceInZoneToNodeMetadata(Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus,
                                 GroupNamingConvention.Factory namingConvention,
                                 @Memoized Supplier<Map<URI, ? extends Image>> images,
                                 @Memoized Supplier<Map<URI, ? extends Hardware>> hardwares,
                                 @Memoized Supplier<Map<URI, ? extends Location>> locations) {
      this.toPortableNodeStatus = toPortableNodeStatus;
      this.nodeNamingConvention = namingConvention.createWithoutPrefix();
      this.images = images;
      this.hardwares = hardwares;
      this.locations = locations;
   }

   @Override
   public NodeMetadata apply(InstanceInZone instanceInZone) {
      Instance input = instanceInZone.getInstance();
      Map<URI, ? extends Image> imagesMap = images.get();
      Image image = checkNotNull(imagesMap.get(checkNotNull(input.getImage(), "image")),
              "no image for %s. images: %s", input.getImage(), imagesMap.values());

      return new NodeMetadataBuilder()
              .id(SlashEncodedIds.fromTwoIds(checkNotNull(locations.get().get(input.getZone()), "location for %s", input.getZone()).getId(),
                      input.getName()).slashEncode())
              .name(input.getName())
              .providerId(input.getId())
              .hostname(input.getName())
              .imageId(image.getId())
              .location(checkNotNull(locations.get().get(input.getZone()), "location for %s", input.getZone()))
              .hardware(checkNotNull(hardwares.get().get(input.getMachineType()), "hardware type for %s",
                      input.getMachineType().toString()))
              .operatingSystem(image.getOperatingSystem())
              .status(toPortableNodeStatus.get(input.getStatus()))
              .tags(input.getTags().getItems())
              .uri(input.getSelfLink())
              .userMetadata(input.getMetadata().getItems())
              .group(nodeNamingConvention.groupInUniqueNameOrNull(input.getName()))
              .privateAddresses(collectPrivateAddresses(input))
              .publicAddresses(collectPublicAddresses(input))
              .build();
   }

   private Set<String> collectPrivateAddresses(Instance input) {
      ImmutableSet.Builder<String> privateAddressesBuilder = ImmutableSet.builder();
      for (Instance.NetworkInterface networkInterface : input.getNetworkInterfaces()) {
         if (networkInterface.getNetworkIP().isPresent()) {
            privateAddressesBuilder.add(networkInterface.getNetworkIP().get());
         }
      }
      return privateAddressesBuilder.build();
   }

   private Set<String> collectPublicAddresses(Instance input) {
      ImmutableSet.Builder<String> publicAddressesBuilder = ImmutableSet.builder();
      for (Instance.NetworkInterface networkInterface : input.getNetworkInterfaces()) {
         for (Instance.NetworkInterface.AccessConfig accessConfig : networkInterface.getAccessConfigs()) {
            if (accessConfig.getNatIP().isPresent()) {
               publicAddressesBuilder.add(accessConfig.getNatIP().get());
            }
         }
      }
      return publicAddressesBuilder.build();
   }
}
