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
package org.jclouds.azurecompute.arm.compute.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.azurecompute.arm.compute.AzureComputeServiceAdapter.GROUP_KEY;
import static org.jclouds.azurecompute.arm.compute.domain.LocationAndName.fromLocationAndName;
import static org.jclouds.azurecompute.arm.compute.domain.ResourceGroupAndName.fromResourceGroupAndName;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractName;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractResourceGroup;
import static org.jclouds.compute.util.ComputeServiceUtils.addMetadataAndParseTagsFromCommaDelimitedValue;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.domain.LocationAndName;
import org.jclouds.azurecompute.arm.compute.functions.VirtualMachineToStatus.StatusAndBackendStatus;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkProfile.NetworkInterface;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.suppliers.ImageCacheSupplier;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AzureComputeApi api;
   private final GroupNamingConvention nodeNamingConvention;
   private final Supplier<Set<? extends Location>> locations;
   private final Supplier<Map<String, ? extends Hardware>> hardwares;
   private final ImageCacheSupplier imageCache;
   private final VirtualMachineToStatus virtualMachineToStatus;

   @Inject
   VirtualMachineToNodeMetadata(AzureComputeApi api, GroupNamingConvention.Factory namingConvention,
         Supplier<Map<String, ? extends Hardware>> hardwares, @Memoized Supplier<Set<? extends Location>> locations,
         Map<String, Credentials> credentialStore, @Memoized Supplier<Set<? extends Image>> imageCache,
         VirtualMachineToStatus virtualMachineToStatus) {
      this.api = api;
      this.nodeNamingConvention = namingConvention.createWithoutPrefix();
      this.locations = locations;
      this.hardwares = hardwares;
      this.virtualMachineToStatus = virtualMachineToStatus;
      checkArgument(imageCache instanceof ImageCacheSupplier,
            "This provider needs an instance of the ImageCacheSupplier");
      this.imageCache = (ImageCacheSupplier) imageCache;
   }

   @Override
   public NodeMetadata apply(VirtualMachine virtualMachine) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.id(fromResourceGroupAndName(extractResourceGroup(virtualMachine.id()), virtualMachine.name())
            .slashEncode());
      builder.providerId(virtualMachine.id());
      builder.name(virtualMachine.name());
      builder.hostname(virtualMachine.name());

      StatusAndBackendStatus status = virtualMachineToStatus.apply(virtualMachine);
      builder.status(status.status());
      builder.backendStatus(status.backendStatus());

      builder.publicAddresses(getPublicIpAddresses(virtualMachine.properties().networkProfile().networkInterfaces()));
      builder.privateAddresses(getPrivateIpAddresses(virtualMachine.properties().networkProfile().networkInterfaces()));

      String groupFromMetadata = null;
      if (virtualMachine.tags() != null) {
         addMetadataAndParseTagsFromCommaDelimitedValue(builder, virtualMachine.tags());
         groupFromMetadata = virtualMachine.tags().get(GROUP_KEY);
      }

      // Try to read the group from the virtual machine tags, and parse the name
      // if missing
      builder.group(groupFromMetadata != null ? groupFromMetadata : nodeNamingConvention.extractGroup(virtualMachine
            .name()));

      String locationName = virtualMachine.location();
      builder.location(getLocation(locations, locationName));

      Optional<? extends Image> image = findImage(virtualMachine.properties().storageProfile(), locationName);
      
      if (image.isPresent()) {
         builder.imageId(image.get().getId());
         builder.operatingSystem(image.get().getOperatingSystem());
      } else {
         logger.info(">> image with id %s for virtualmachine %s was not found. "
               + "This might be because the image that was used to create the virtualmachine has a new id.",
               virtualMachine.id(), virtualMachine.id());
      }

      builder.hardware(getHardware(fromLocationAndName(virtualMachine.location(), virtualMachine.properties()
            .hardwareProfile().vmSize())));

      return builder.build();
   }

   private Iterable<String> getPrivateIpAddresses(List<NetworkInterface> networkInterfaces) {
      List<String> privateIpAddresses = Lists.newArrayList();
      for (NetworkInterface networkInterfaceCardIdReference : networkInterfaces) {
         NetworkInterfaceCard networkInterfaceCard = getNetworkInterfaceCard(networkInterfaceCardIdReference);
         if (networkInterfaceCard != null && networkInterfaceCard.properties() != null
               && networkInterfaceCard.properties().ipConfigurations() != null) {
            for (IpConfiguration ipConfiguration : networkInterfaceCard.properties().ipConfigurations()) {
               if (ipConfiguration.properties().privateIPAddress() != null) {
                  privateIpAddresses.add(ipConfiguration.properties().privateIPAddress());
               }
            }
         }
      }
      return privateIpAddresses;
   }

   private NetworkInterfaceCard getNetworkInterfaceCard(NetworkInterface nic) {
      return api.getNetworkInterfaceCardApi(extractResourceGroup(nic.id())).get(extractName(nic.id()));
   }

   private Iterable<String> getPublicIpAddresses(List<NetworkInterface> networkInterfaces) {
      List<String> publicIpAddresses = Lists.newArrayList();
      for (NetworkInterface networkInterfaceCardIdReference : networkInterfaces) {
         NetworkInterfaceCard networkInterfaceCard = getNetworkInterfaceCard(networkInterfaceCardIdReference);
         if (networkInterfaceCard != null && networkInterfaceCard.properties() != null
               && networkInterfaceCard.properties().ipConfigurations() != null) {
            for (IpConfiguration ipConfiguration : networkInterfaceCard.properties().ipConfigurations()) {
               if (ipConfiguration.properties().publicIPAddress() != null) {
                  IdReference publicIpId = ipConfiguration.properties().publicIPAddress();
                  PublicIPAddress publicIp = api.getPublicIPAddressApi(publicIpId.resourceGroup()).get(
                        publicIpId.name());
                  if (publicIp != null && publicIp.properties().ipAddress() != null) {
                     publicIpAddresses.add(publicIp.properties().ipAddress());
                  }
               }
            }
         }
      }
      return publicIpAddresses;
   }

   protected static Location getLocation(Supplier<Set<? extends Location>> locations, final String locationName) {
      return find(locations.get(), idEquals(nullToEmpty(locationName)), null);
   }

   protected Optional<? extends Image> findImage(final StorageProfile storageProfile, String locatioName) {
      if (storageProfile.imageReference() != null) {
         String imageId = storageProfile.imageReference().customImageId() != null ?
               storageProfile.imageReference().encodeFieldsToUniqueIdCustom(locatioName) :
               storageProfile.imageReference().encodeFieldsToUniqueId(locatioName); 
         return imageCache.get(imageId);
      } else {
         logger.warn("could not find image for storage profile %s", storageProfile);
         return Optional.absent();
      }
   }

   protected Hardware getHardware(final LocationAndName hardwareId) {
      final String slashEncoded = hardwareId.slashEncode();
      return find(hardwares.get().values(), new Predicate<Hardware>() {
         @Override
         public boolean apply(Hardware input) {
            return input.getId().equals(slashEncoded);
         }
      });
   }

}
