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
package org.jclouds.azurecompute.arm.compute.strategy;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.AvailabilitySet;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.RegionAndId;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.features.NetworkSecurityGroupApi;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Singleton
public class CleanupResources {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AzureComputeApi api;
   private final Predicate<URI> resourceDeleted;
   private final LoadingCache<String, ResourceGroup> resourceGroupMap;
   private final GroupNamingConvention.Factory namingConvention;

   @Inject
   CleanupResources(AzureComputeApi azureComputeApi, @Named(TIMEOUT_RESOURCE_DELETED) Predicate<URI> resourceDeleted,
         LoadingCache<String, ResourceGroup> resourceGroupMap, GroupNamingConvention.Factory namingConvention) {
      this.api = azureComputeApi;
      this.resourceDeleted = resourceDeleted;
      this.resourceGroupMap = resourceGroupMap;
      this.namingConvention = namingConvention;
   }

   public boolean cleanupNode(final String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());
      String resourceGroupName = resourceGroup.name();

      VirtualMachine virtualMachine = api.getVirtualMachineApi(resourceGroupName).get(regionAndId.id());
      if (virtualMachine == null) {
         return true;
      }

      logger.debug(">> destroying %s ...", regionAndId.slashEncode());
      boolean vmDeleted = deleteVirtualMachine(resourceGroupName, virtualMachine);

      // We don't delete the network here, as it is global to the resource
      // group. It will be deleted when the resource group is deleted

      cleanupVirtualMachineNICs(resourceGroupName, virtualMachine);
      cleanupAvailabilitySetIfOrphaned(resourceGroupName, virtualMachine);

      return vmDeleted;
   }

   public void cleanupVirtualMachineNICs(String group, VirtualMachine virtualMachine) {
      for (String nicName : getNetworkCardInterfaceNames(virtualMachine)) {
         NetworkInterfaceCard nic = api.getNetworkInterfaceCardApi(group).get(nicName);
         Iterable<String> publicIps = getPublicIps(group, nic);

         logger.debug(">> destroying nic %s...", nicName);
         URI nicDeletionURI = api.getNetworkInterfaceCardApi(group).delete(nicName);
         resourceDeleted.apply(nicDeletionURI);

         for (String publicIp : publicIps) {
            logger.debug(">> deleting public ip nic %s...", publicIp);
            api.getPublicIPAddressApi(group).delete(publicIp);
         }
      }
   }

   public boolean cleanupSecurityGroupIfOrphaned(String resourceGroup, String group) {
      String name = namingConvention.create().sharedNameForGroup(group);
      NetworkSecurityGroupApi sgapi = api.getNetworkSecurityGroupApi(resourceGroup);

      boolean deleted = false;

      try {
         NetworkSecurityGroup securityGroup = sgapi.get(name);
         if (securityGroup != null) {
            List<NetworkInterfaceCard> nics = securityGroup.properties().networkInterfaces();
            if (nics == null || nics.isEmpty()) {
               logger.debug(">> deleting orphaned security group %s from %s...", name, resourceGroup);
               try {
                  deleted = resourceDeleted.apply(sgapi.delete(name));
               } catch (Exception ex) {
                  logger.warn(ex, ">> error deleting orphaned security group %s from %s...", name, resourceGroup);
               }
            }
         }
      } catch (Exception ex) {
         logger.warn(ex, "Error deleting security groups for %s and group %s", resourceGroup, group);
      }

      return deleted;
   }

   public boolean cleanupAvailabilitySetIfOrphaned(String resourceGroup, VirtualMachine virtualMachine) {
      boolean deleted = false;
      IdReference availabilitySetRef = virtualMachine.properties().availabilitySet();

      if (availabilitySetRef != null) {
         String name = Iterables.getLast(Splitter.on("/").split(availabilitySetRef.id()));
         AvailabilitySet availabilitySet = api.getAvailabilitySetApi(resourceGroup).get(name);

         if (isOrphanedJcloudsAvailabilitySet(availabilitySet)) {
            logger.debug(">> deleting orphaned availability set %s from %s...", name, resourceGroup);
            URI uri = api.getAvailabilitySetApi(resourceGroup).delete(name);
            deleted = uri == null || resourceDeleted.apply(uri);
         }
      }

      return deleted;
   }

   public boolean deleteResourceGroupIfEmpty(String group) {
      boolean deleted = false;
      if (api.getResourceGroupApi().resources(group).isEmpty()) {
         logger.debug(">> the resource group %s is empty. Deleting...", group);
         deleted = resourceDeleted.apply(api.getResourceGroupApi().delete(group));
      }
      return deleted;
   }

   private Iterable<String> getPublicIps(String group, NetworkInterfaceCard nic) {
      return transform(
            filter(transform(nic.properties().ipConfigurations(), new Function<IpConfiguration, IdReference>() {
               @Override
               public IdReference apply(IpConfiguration input) {
                  return input.properties().publicIPAddress();
               }
            }), notNull()), new Function<IdReference, String>() {
               @Override
               public String apply(IdReference input) {
                  return Iterables.getLast(Splitter.on("/").split(input.id()));
               }
            });
   }

   private static boolean isOrphanedJcloudsAvailabilitySet(AvailabilitySet availabilitySet) {
      // We check for the presence of the 'jclouds' tag to make sure we only
      // delete availability sets that were automatically created by jclouds
      return availabilitySet != null
            && availabilitySet.tags() != null
            && availabilitySet.tags().containsKey("jclouds")
            && (availabilitySet.properties().virtualMachines() == null || availabilitySet.properties()
                  .virtualMachines().isEmpty());
   }

   private List<String> getNetworkCardInterfaceNames(VirtualMachine virtualMachine) {
      List<String> nics = Lists.newArrayList();
      for (IdReference idReference : virtualMachine.properties().networkProfile().networkInterfaces()) {
         nics.add(Iterables.getLast(Splitter.on("/").split(idReference.id())));
      }
      return nics;
   }

   private boolean deleteVirtualMachine(String group, VirtualMachine virtualMachine) {
      return resourceDeleted.apply(api.getVirtualMachineApi(group).delete(virtualMachine.name()));
   }

}
