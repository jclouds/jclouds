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
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceInZone;
import org.jclouds.googlecomputeengine.domain.SlashEncodedIds;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * Transforms a google compute domain Instance into a generic NodeMetatada object.
 */
public class InstanceInZoneToNodeMetadata implements Function<InstanceInZone, NodeMetadata> {

   private final Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus;
   private final GroupNamingConvention nodeNamingConvention;
   private final Supplier<Map<URI, ? extends Image>> images;
   private final Supplier<Map<URI, ? extends Hardware>> hardwares;
   private final Supplier<Map<URI, ? extends Location>> locations;
   private final FirewallTagNamingConvention.Factory firewallTagNamingConvention;
   private final GoogleComputeEngineApi api;
   private final Supplier<String> userProject;

   @Inject
   public InstanceInZoneToNodeMetadata(Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus,
                                 GroupNamingConvention.Factory namingConvention,
                                 @Memoized Supplier<Map<URI, ? extends Image>> images,
                                 @Memoized Supplier<Map<URI, ? extends Hardware>> hardwares,
                                 @Memoized Supplier<Map<URI, ? extends Location>> locations,
                                 FirewallTagNamingConvention.Factory firewallTagNamingConvention,
                                 GoogleComputeEngineApi api,
                                 @UserProject Supplier<String> userProject) {
      this.toPortableNodeStatus = toPortableNodeStatus;
      this.nodeNamingConvention = namingConvention.createWithoutPrefix();
      this.images = images;
      this.hardwares = hardwares;
      this.locations = locations;
      this.firewallTagNamingConvention = checkNotNull(firewallTagNamingConvention, "firewallTagNamingConvention");
      this.api = checkNotNull(api, "api");
      this.userProject = checkNotNull(userProject, "userProject");
   }

   @Override
   public NodeMetadata apply(InstanceInZone instanceInZone) {
      Instance input = instanceInZone.getInstance();

      String group = groupFromMapOrName(input.getMetadata().getItems(),
                                               input.getName(), nodeNamingConvention);
      FluentIterable<String> tags = FluentIterable.from(input.getTags().getItems());
      if (group != null) {
         tags = tags.filter(Predicates.not(firewallTagNamingConvention.get(group).isFirewallTag()));
      }

      NodeMetadataBuilder builder = new NodeMetadataBuilder();

      builder.id(SlashEncodedIds.fromTwoIds(checkNotNull(locations.get().get(input.getZone()),
                                                                "location for %s", input.getZone())
                                                    .getId(), input.getName()).slashEncode())
              .name(input.getName())
              .providerId(input.getId())
              .hostname(input.getName())
              .location(checkNotNull(locations.get().get(input.getZone()), "location for %s", input.getZone()))
              .hardware(hardwares.get().get(input.getMachineType()))
              .status(toPortableNodeStatus.get(input.getStatus()))
              .tags(tags)
              .uri(input.getSelfLink())
              .userMetadata(input.getMetadata().getItems())
              .group(group)
              .privateAddresses(collectPrivateAddresses(input))
              .publicAddresses(collectPublicAddresses(input));

      if (input.getMetadata().getItems().containsKey(GCE_IMAGE_METADATA_KEY)) {
         try {
            URI imageUri = URI.create(input.getMetadata().getItems()
                                              .get(GCE_IMAGE_METADATA_KEY));

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
