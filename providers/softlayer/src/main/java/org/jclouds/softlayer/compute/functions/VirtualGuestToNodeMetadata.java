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
package org.jclouds.softlayer.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.domain.Password;
import org.jclouds.softlayer.domain.TagReference;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Singleton
public class VirtualGuestToNodeMetadata implements Function<VirtualGuest, NodeMetadata> {

   @Resource
   protected Logger logger = Logger.NULL;
    
   public static final Map<VirtualGuest.State, Status> serverStateToNodeStatus = ImmutableMap
         .<VirtualGuest.State, Status> builder().put(VirtualGuest.State.HALTED, Status.PENDING)
         .put(VirtualGuest.State.PAUSED, Status.SUSPENDED).put(VirtualGuest.State.RUNNING, Status.RUNNING)
         .put(VirtualGuest.State.UNRECOGNIZED, Status.UNRECOGNIZED).build();

   private final Supplier<Set<? extends Location>> locations;
   private final GroupNamingConvention nodeNamingConvention;
   private final VirtualGuestToImage virtualGuestToImage;
   private final VirtualGuestToHardware virtualGuestToHardware;


   @Inject
   VirtualGuestToNodeMetadata(@Memoized Supplier<Set<? extends Location>> locations,
         GroupNamingConvention.Factory namingConvention, VirtualGuestToImage virtualGuestToImage,
         VirtualGuestToHardware virtualGuestToHardware) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.locations = checkNotNull(locations, "locations");
      this.virtualGuestToImage = checkNotNull(virtualGuestToImage, "virtualGuestToImage");
      this.virtualGuestToHardware = checkNotNull(virtualGuestToHardware, "virtualGuestToHardware");
   }

   @Override
   public NodeMetadata apply(VirtualGuest from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getHostname());
      builder.hostname(from.getFullyQualifiedDomainName());
      if (from.getDatacenter() != null) {
         builder.location(from(locations.get()).firstMatch(
                 LocationPredicates.idEquals(from.getDatacenter().getName())).orNull());
      }
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getHostname()));
      builder.hardware(virtualGuestToHardware.apply(from));
      Image image = virtualGuestToImage.apply(from);
      if (image != null) {
         builder.imageId(image.getId());
         builder.operatingSystem(image.getOperatingSystem());
      }
      if (from.getPowerState() != null) {
         builder.status(serverStateToNodeStatus.get(from.getPowerState().getKeyName()));
      }
      if (from.getPrimaryIpAddress() != null)
         builder.publicAddresses(ImmutableSet.of(from.getPrimaryIpAddress()));
      if (from.getPrimaryBackendIpAddress() != null)
         builder.privateAddresses(ImmutableSet.of(from.getPrimaryBackendIpAddress()));
      // TODO simplify once we move domain classes to AutoValue
      if (from.getOperatingSystem() != null && from.getOperatingSystem().getPasswords() != null && !from.getOperatingSystem().getPasswords().isEmpty()) {
         Password password = getBestPassword(from.getOperatingSystem().getPasswords(), from);
         builder.credentials(LoginCredentials.builder().identity(password.getUsername()).credential(password.getPassword()).build());
      }
      if (from.getTagReferences() != null && !from.getTagReferences().isEmpty()) {
         List<String> tags = Lists.newArrayList();
         for (TagReference tagReference : from.getTagReferences()) {
            if (tagReference != null) {
               tags.add(tagReference.getTag().getName());
            }
         }
         builder.tags(tags);
      }
      return builder.build();
   }

   @VisibleForTesting
   Password getBestPassword(Set<Password> passwords, VirtualGuest context) {
      if (passwords == null || passwords.isEmpty()) {
         throw new IllegalStateException("No credentials declared for " + context);
      }
      if (passwords.size() == 1) {
          // usual path
          return Iterables.getOnlyElement(passwords);
      }
      // in some setups a there may be multiple passwords; pick the best
      Password bestPassword = null;
      Set<Password> alternates = Sets.newLinkedHashSet();
      int bestScore = -1;
      for (Password p : passwords) {
         int score = -1;
         if ("root".equals(p.getUsername())) score = 10;
         else if ("root".equalsIgnoreCase(p.getUsername())) score = 4;
         else if ("ubuntu".equals(p.getUsername())) score = 8;
         else if ("ubuntu".equalsIgnoreCase(p.getUsername())) score = 3;
         else if ("administrator".equals(p.getUsername())) score = 5;
         else if ("administrator".equalsIgnoreCase(p.getUsername())) score = 2;
         else if (p.getUsername() != null && p.getUsername().length() > 1) score = 1;
         
         if (score > 0) {
            if (score > bestScore) {
                bestPassword = p;
                alternates.clear();
                bestScore = score;
            } else if (score == bestScore) {
                alternates.add(p);
            }
         }
      }
      if (bestPassword == null) {
          throw new IllegalStateException("No valid credentials available for " + context + "; found: " + passwords);
      }
      if (!alternates.isEmpty()) {
         logger.warn("Multiple credentials for " + bestPassword.getUsername() + "@" + context + "; using first declared " + bestPassword + " and ignoring " + alternates);
      } else {
         logger.debug("Multiple credentials for " + context + "; using preferred username " + bestPassword.getUsername());
      }
      return bestPassword;
   }

}
