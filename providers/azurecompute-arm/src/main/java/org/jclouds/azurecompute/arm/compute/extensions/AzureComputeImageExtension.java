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

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.azurecompute.arm.compute.functions.VMImageToImage.decodeFieldsFromUniqueId;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

import java.net.URI;
import java.util.concurrent.Callable;

import javax.annotation.Resource;

import org.jclouds.Constants;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.ImageAvailablePredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.VirtualMachineInStatePredicateFactory;
import org.jclouds.azurecompute.arm.compute.functions.CustomImageToVMImage;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageProperties;
import org.jclouds.azurecompute.arm.domain.RegionAndId;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AzureComputeImageExtension implements ImageExtension {
   public static final String CUSTOM_IMAGE_OFFER = "custom";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AzureComputeApi api;
   private final ListeningExecutorService userExecutor;
   private final ImageAvailablePredicateFactory imageAvailablePredicate;
   private final VirtualMachineInStatePredicateFactory nodeSuspendedPredicate;
   private final LoadingCache<String, ResourceGroup> resourceGroupMap;
   private final Function<VMImage, Image> vmImageToImage;
   private final Predicate<URI> resourceDeleted;
   private final CustomImageToVMImage customImagetoVmImage;

   @Inject
   AzureComputeImageExtension(AzureComputeApi api,
         ImageAvailablePredicateFactory imageAvailablePredicate,
         @Named(TIMEOUT_NODE_SUSPENDED) VirtualMachineInStatePredicateFactory nodeSuspendedPredicate,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         Function<VMImage, Image> vmImageToImage, LoadingCache<String, ResourceGroup> resourceGroupMap,
         @Named(TIMEOUT_RESOURCE_DELETED) Predicate<URI> resourceDeleted,
         CustomImageToVMImage customImagetoVmImage) {
      this.api = api;
      this.imageAvailablePredicate = imageAvailablePredicate;
      this.nodeSuspendedPredicate = nodeSuspendedPredicate;
      this.userExecutor = userExecutor;
      this.vmImageToImage = vmImageToImage;
      this.resourceGroupMap = resourceGroupMap;
      this.resourceDeleted = resourceDeleted;
      this.customImagetoVmImage = customImagetoVmImage;
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

      final VirtualMachine vm = api.getVirtualMachineApi(resourceGroupName).get(regionAndId.id());
      final IdReference vmIdRef = IdReference.create(vm.id());

      logger.debug(">> stopping node %s...", regionAndId.slashEncode());
      api.getVirtualMachineApi(resourceGroupName).stop(regionAndId.id());
      checkState(nodeSuspendedPredicate.create(resourceGroupName).apply(regionAndId.id()),
            "Node %s was not suspended within the configured time limit", regionAndId.slashEncode());

      return userExecutor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
            logger.debug(">> generalizing virtal machine %s...", regionAndId.id());

            api.getVirtualMachineApi(resourceGroupName).generalize(regionAndId.id());

            org.jclouds.azurecompute.arm.domain.Image imageFromVM = api.getVirtualMachineImageApi(resourceGroupName)
                  .createOrUpdate(cloneTemplate.getName(), regionAndId.region(),
                        ImageProperties.builder().sourceVirtualMachine(vmIdRef).build());

            checkState(imageAvailablePredicate.create(resourceGroupName).apply(imageFromVM.name()),
                  "Image for node %s was not created within the configured time limit", cloneTemplate.getName());

            return compose(vmImageToImage, customImagetoVmImage).apply(imageFromVM);
         }
      });
   }

   @Override
   public boolean deleteImage(String id) {
      VMImage image = decodeFieldsFromUniqueId(id);
      checkArgument(image.custom(), "Only custom images can be deleted");

      logger.debug(">> deleting image %s", id);

      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(image.location());
      URI uri = api.getVirtualMachineImageApi(resourceGroup.name()).delete(image.name());
      return resourceDeleted.apply(uri);
   }
}
