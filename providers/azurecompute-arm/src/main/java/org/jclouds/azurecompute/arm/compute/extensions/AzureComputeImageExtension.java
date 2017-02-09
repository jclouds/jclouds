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
package org.jclouds.azurecompute.arm.compute.extensions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.azurecompute.arm.compute.functions.VMImageToImage.decodeFieldsFromUniqueId;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.util.Closeables2.closeQuietly;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Resource;

import org.jclouds.Constants;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.VirtualMachineInStatePredicateFactory;
import org.jclouds.azurecompute.arm.compute.functions.ResourceDefinitionToCustomImage;
import org.jclouds.azurecompute.arm.compute.strategy.CleanupResources;
import org.jclouds.azurecompute.arm.domain.RegionAndId;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.StorageServiceKeys;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.util.BlobHelper;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AzureComputeImageExtension implements ImageExtension {
   public static final String CONTAINER_NAME = "jclouds";
   public static final String CUSTOM_IMAGE_OFFER = "custom";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AzureComputeApi api;
   private final ListeningExecutorService userExecutor;
   private final Predicate<URI> imageAvailablePredicate;
   private final VirtualMachineInStatePredicateFactory nodeSuspendedPredicate;
   private final ResourceDefinitionToCustomImage.Factory resourceDefinitionToImage;
   private final CleanupResources cleanupResources;
   private final LoadingCache<String, ResourceGroup> resourceGroupMap;

   @Inject
   AzureComputeImageExtension(AzureComputeApi api,
         @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<URI> imageAvailablePredicate,
         @Named(TIMEOUT_NODE_SUSPENDED) VirtualMachineInStatePredicateFactory nodeSuspendedPredicate,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         ResourceDefinitionToCustomImage.Factory resourceDefinitionToImage, CleanupResources cleanupResources,
         LoadingCache<String, ResourceGroup> resourceGroupMap) {
      this.api = api;
      this.imageAvailablePredicate = imageAvailablePredicate;
      this.nodeSuspendedPredicate = nodeSuspendedPredicate;
      this.userExecutor = userExecutor;
      this.resourceDefinitionToImage = resourceDefinitionToImage;
      this.cleanupResources = cleanupResources;
      this.resourceGroupMap = resourceGroupMap;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      return new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name.toLowerCase()).build();
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      final CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;

      final RegionAndId regionAndId = RegionAndId.fromSlashEncoded(cloneTemplate.getSourceNodeId());
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());
      final String resourceGroupName = resourceGroup.name();

      logger.debug(">> stopping node %s...", regionAndId.slashEncode());
      api.getVirtualMachineApi(resourceGroupName).stop(regionAndId.id());
      checkState(nodeSuspendedPredicate.create(resourceGroupName).apply(regionAndId.id()),
            "Node %s was not suspended within the configured time limit", regionAndId.slashEncode());

      return userExecutor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
            logger.debug(">> generalizing virtal machine %s...", regionAndId.id());
            api.getVirtualMachineApi(resourceGroupName).generalize(regionAndId.id());

            logger.debug(">> capturing virtual machine %s to container %s...", regionAndId.id(), CONTAINER_NAME);
            URI uri = api.getVirtualMachineApi(resourceGroupName)
                  .capture(regionAndId.id(), cloneTemplate.getName(), CONTAINER_NAME);
            checkState(uri != null && imageAvailablePredicate.apply(uri),
                  "Image for node %s was not created within the configured time limit", cloneTemplate.getName());

            List<ResourceDefinition> definitions = api.getJobApi().captureStatus(uri);
            checkState(definitions.size() == 1,
                  "Expected one resource definition after creating the image but %s were returned", definitions.size());

            Image image = resourceDefinitionToImage.create(cloneTemplate.getSourceNodeId(), cloneTemplate.getName())
                  .apply(definitions.get(0));
            checkState(image != null, "Image for node %s was not created", cloneTemplate.getSourceNodeId());
            logger.debug(">> created %s", image);
            return image;
         }
      });
   }

   @Override
   public boolean deleteImage(String id) {
      VMImage image = decodeFieldsFromUniqueId(id);
      checkArgument(image.custom(), "Only custom images can be deleted");

      logger.debug(">> deleting image %s", id);

      StorageServiceKeys keys = api.getStorageAccountApi(image.group()).getKeys(image.storage());
      BlobHelper blobHelper = new BlobHelper(image.storage(), keys.key1());

      try {
         // This removes now all the images in this storage. At least in theory,
         // there should be just one and if there is
         // more, they should be copies of each other.
         blobHelper.deleteContainerIfExists("system");
         boolean result = !blobHelper.customImageExists();

         if (!blobHelper.hasContainers()) {
            logger.debug(">> storage account is empty after deleting the custom image. Deleting the storage account...");
            api.getStorageAccountApi(image.group()).delete(image.storage());
            cleanupResources.deleteResourceGroupIfEmpty(image.group());
         }

         return result;
      } finally {
         closeQuietly(blobHelper);
      }
   }
}
