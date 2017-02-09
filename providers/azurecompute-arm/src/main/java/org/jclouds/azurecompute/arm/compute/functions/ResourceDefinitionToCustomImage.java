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

import static org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension.CUSTOM_IMAGE_OFFER;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.RegionAndId;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.functions.StorageProfileToStorageAccountName;
import org.jclouds.compute.domain.Image;

import com.google.common.base.Function;
import com.google.common.cache.LoadingCache;
import com.google.inject.assistedinject.Assisted;

public class ResourceDefinitionToCustomImage implements Function<ResourceDefinition, Image> {

   public interface Factory {
      ResourceDefinitionToCustomImage create(@Assisted("nodeId") String nodeId, @Assisted("imageName") String imageName);
   }

   private final Function<VMImage, Image> vmImageToImage;
   private final String imageName;
   private final String nodeId;
   private final AzureComputeApi api;
   private final StorageProfileToStorageAccountName storageProfileToStorageAccountName;
   private final LoadingCache<String, ResourceGroup> resourceGroupMap;

   @Inject
   ResourceDefinitionToCustomImage(AzureComputeApi api,
         StorageProfileToStorageAccountName storageProfileToStorageAccountName,
         Function<VMImage, Image> vmImageToImage, LoadingCache<String, ResourceGroup> resourceGroupMap,
         @Assisted("nodeId") String nodeId, @Assisted("imageName") String imageName) {
      this.api = api;
      this.vmImageToImage = vmImageToImage;
      this.nodeId = nodeId;
      this.imageName = imageName;
      this.storageProfileToStorageAccountName = storageProfileToStorageAccountName;
      this.resourceGroupMap = resourceGroupMap;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Image apply(ResourceDefinition input) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(nodeId);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());
      
      VirtualMachine vm = api.getVirtualMachineApi(resourceGroup.name()).get(regionAndId.id());
      if (vm == null) {
         return null;
      }
      String storageAccountName = storageProfileToStorageAccountName.apply(vm.properties().storageProfile());

      VMImage.Builder builder = VMImage.customImage().group(resourceGroup.name()).storage(storageAccountName)
            .name(imageName).offer(CUSTOM_IMAGE_OFFER).location(vm.location());

      Map<String, String> properties = (Map<String, String>) input.properties();

      Object storageObject = properties.get("storageProfile");
      Map<String, String> storageProperties = (Map<String, String>) storageObject;

      Object osDiskObject = storageProperties.get("osDisk");
      Map<String, String> osProperties = (Map<String, String>) osDiskObject;
      builder.vhd1(osProperties.get("name"));

      return vmImageToImage.apply(builder.build());
   }

}
