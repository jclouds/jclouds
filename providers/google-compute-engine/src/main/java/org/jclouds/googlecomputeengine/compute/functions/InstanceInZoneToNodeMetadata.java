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
import static org.jclouds.compute.util.ComputeServiceUtils.groupFromMapOrName;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.GCE_IMAGE_METADATA_KEY;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.compute.domain.InstanceInZone;
import org.jclouds.googlecomputeengine.compute.domain.SlashEncodedIds;
import org.jclouds.googlecomputeengine.domain.Instance;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

public final class InstanceInZoneToNodeMetadata implements Function<InstanceInZone, NodeMetadata> {

   private final Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus;
   private final GroupNamingConvention nodeNamingConvention;
   private final Supplier<Map<URI, ? extends Image>> images;
   private final Supplier<Map<URI, ? extends Hardware>> hardwares;
   private final Supplier<Map<URI, ? extends Location>> locations;
   private final FirewallTagNamingConvention.Factory firewallTagNamingConvention;

   @Inject InstanceInZoneToNodeMetadata(Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus,
                                        GroupNamingConvention.Factory namingConvention,
                                        @Memoized Supplier<Map<URI, ? extends Image>> images,
                                        @Memoized Supplier<Map<URI, ? extends Hardware>> hardwares,
                                        @Memoized Supplier<Map<URI, ? extends Location>> locations,
                                        FirewallTagNamingConvention.Factory firewallTagNamingConvention) {
      this.toPortableNodeStatus = toPortableNodeStatus;
      this.nodeNamingConvention = namingConvention.createWithoutPrefix();
      this.images = images;
      this.hardwares = hardwares;
      this.locations = locations;
      this.firewallTagNamingConvention = checkNotNull(firewallTagNamingConvention, "firewallTagNamingConvention");
   }

   @Override public NodeMetadata apply(InstanceInZone instanceInZone) {
      Instance input = instanceInZone.instance();

      String group = groupFromMapOrName(input.metadata().items(), input.name(), nodeNamingConvention);
      FluentIterable<String> tags = FluentIterable.from(input.tags().items());
      if (group != null) {
         tags = tags.filter(Predicates.not(firewallTagNamingConvention.get(group).isFirewallTag()));
      }

      NodeMetadataBuilder builder = new NodeMetadataBuilder();

      Location location = checkNotNull(locations.get().get(input.zone()), "location for %s", input.zone());
      builder.id(SlashEncodedIds.from(location.getId(), input.name()).slashEncode())
              .name(input.name())
              .providerId(input.id())
              .hostname(input.name())
              .location(location)
              .hardware(hardwares.get().get(input.machineType()))
              .status(toPortableNodeStatus.get(input.status()))
              .tags(tags)
              .uri(input.selfLink())
              .userMetadata(input.metadata().items())
              .group(group)
              .privateAddresses(collectPrivateAddresses(input))
              .publicAddresses(collectPublicAddresses(input));

      if (input.metadata().items().containsKey(GCE_IMAGE_METADATA_KEY)) {
         try {
            URI imageUri = URI.create(input.metadata().items().get(GCE_IMAGE_METADATA_KEY));

            Map<URI, ? extends Image> imagesMap = images.get();

            Image image = checkNotNull(imagesMap.get(imageUri),
                                       "no image for %s. images: %s", imageUri,
                                       imagesMap.values());
            builder.imageId(image.getId());
         } catch (IllegalArgumentException e) {
            // Swallow any exception here - it just means we don't actually have a valid image URI, so we skip it.
         }
      }

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
}
