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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static org.jclouds.compute.util.ComputeServiceUtils.addMetadataAndParseTagsFromCommaDelimitedValue;
import static org.jclouds.compute.util.ComputeServiceUtils.groupFromMapOrName;
import static org.jclouds.openstack.nova.v2_0.domain.Address.createV4;
import static org.jclouds.openstack.nova.v2_0.domain.Address.createV6;

import java.net.Inet4Address;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.ServerInRegion;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.util.InetAddresses2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.net.InetAddresses;

/**
 * A function for transforming a nova-specific Server into a generic
 * NodeMetadata object.
 */
public class ServerInRegionToNodeMetadata implements Function<ServerInRegion, NodeMetadata> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected Map<Status, org.jclouds.compute.domain.NodeMetadata.Status> toPortableNodeStatus;
   protected final Supplier<Map<String, Location>> locationIndex;
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwares;
   protected final GroupNamingConvention nodeNamingConvention;

   @Inject
   public ServerInRegionToNodeMetadata(Map<Server.Status, NodeMetadata.Status> toPortableNodeStatus,
            Supplier<Map<String, Location>> locationIndex, @Memoized Supplier<Set<? extends Image>> images,
            @Memoized Supplier<Set<? extends Hardware>> hardwares, GroupNamingConvention.Factory namingConvention) {
      this.toPortableNodeStatus = checkNotNull(toPortableNodeStatus, "toPortableNodeStatus");
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.locationIndex = checkNotNull(locationIndex, "locationIndex");
      this.images = checkNotNull(images, "images");
      this.hardwares = checkNotNull(hardwares, "hardwares");
   }

   @Override
   public NodeMetadata apply(ServerInRegion serverInRegion) {
      Location region = locationIndex.get().get(serverInRegion.getRegion());
      checkState(region != null, "location %s not in locationIndex: %s", serverInRegion.getRegion(), locationIndex.get());
      Server from = serverInRegion.getServer();

      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.id(serverInRegion.slashEncode());
      builder.providerId(from.getId());
      builder.name(from.getName());
      builder.hostname(from.getName());
      builder.location(from.getHostId() != null ? new LocationBuilder().scope(LocationScope.HOST).id(from.getHostId())
            .description(from.getHostId()).parent(region).build() : region);
      builder.group(groupFromMapOrName(from.getMetadata(), from.getName(), nodeNamingConvention));
      addMetadataAndParseTagsFromCommaDelimitedValue(builder, from.getMetadata());

      if (from.getImage() != null) {
         builder.imageId(RegionAndId.fromRegionAndId(serverInRegion.getRegion(), from.getImage().getId()).slashEncode());
      }

      builder.operatingSystem(findOperatingSystemForServerOrNull(serverInRegion));
      builder.hardware(findHardwareForServerOrNull(serverInRegion));
      builder.status(toPortableNodeStatus.get(from.getStatus()));

      Set<Address> addresses = newHashSet(from.getAddresses().values());
      if (from.getAccessIPv4() != null) {
         addresses.add(createV4(from.getAccessIPv4()));
      }
      if (from.getAccessIPv6() != null) {
         addresses.add(createV6(from.getAccessIPv6()));
      }

      builder.publicAddresses(
            filter(
                  transform(
                        filter(addresses, not(isPrivateAddress)),
                        AddressToStringTransformationFunction.INSTANCE),
                  isInet4Address));

      builder.privateAddresses(
            filter(
                  transform(
                        filter(addresses, isPrivateAddress),
                        AddressToStringTransformationFunction.INSTANCE),
                  isInet4Address));

      for (Link link : from.getLinks()) {
         if (link.getRelation().equals(Link.Relation.SELF)) {
            builder.uri(link.getHref());
         }
      }

      return builder.build();
   }

   public static final Predicate<Address> isPrivateAddress = new Predicate<Address>() {
      public boolean apply(Address in) {
         return InetAddresses2.IsPrivateIPAddress.INSTANCE.apply(in.getAddr());
      }
   };

   public static final Predicate<String> isInet4Address = new Predicate<String>() {
      @Override
      public boolean apply(String input) {
         try {
            // Note we can do this, as InetAddress is now on the white list
            return InetAddresses.forString(input) instanceof Inet4Address;
         } catch (IllegalArgumentException e) {
            // could be a hostname
            return true;
         }
      }

   };

   public enum AddressToStringTransformationFunction implements Function<Address, String> {
      INSTANCE;
      @Override
      public String apply(Address address) {
         return address.getAddr();
      }
   }

   protected Hardware findHardwareForServerOrNull(ServerInRegion serverInRegion) {
      return findObjectOfTypeForServerOrNull(hardwares.get(), "hardware", serverInRegion.getServer().getFlavor().getId(),
            serverInRegion);
   }

   protected OperatingSystem findOperatingSystemForServerOrNull(ServerInRegion serverInRegion) {
      if (serverInRegion.getServer().getImage() != null) {
         Image image = findObjectOfTypeForServerOrNull(
               images.get(), "image", serverInRegion.getServer().getImage().getId(), serverInRegion);

         return (image != null) ? image.getOperatingSystem() : null;
      } else {
         return null;
      }

   }

   public <T extends ComputeMetadata> T findObjectOfTypeForServerOrNull(Set<? extends T> supply, String type,
         final String objectId, final RegionAndId serverInRegion) {
      try {
         return find(supply, new Predicate<T>() {
            @Override
            public boolean apply(T input) {
               return input.getId().equals(RegionAndId.fromRegionAndId(serverInRegion.getRegion(), objectId).slashEncode());
            }
         });
      } catch (NoSuchElementException e) {
         logger.trace("could not find %s with id(%s) for server(%s)", type, objectId, serverInRegion);
      }
      return null;
   }

}
