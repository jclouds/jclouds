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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.azurecompute.arm.compute.functions.VMImageToImage.decodeFieldsFromUniqueId;
import static org.jclouds.util.Predicates2.retry;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule;
import org.jclouds.azurecompute.arm.compute.functions.LocationToResourceGroupName;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.features.ResourceGroupApi;
import org.jclouds.azurecompute.arm.features.SubnetApi;
import org.jclouds.azurecompute.arm.features.VirtualNetworkApi;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

@Singleton
public class CreateResourceGroupThenCreateNodes extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AzureComputeApi api;
   private final AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants;
   private final LocationToResourceGroupName locationToResourceGroupName;

   @Inject
   protected CreateResourceGroupThenCreateNodes(
           CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
           ListNodesStrategy listNodesStrategy,
           GroupNamingConvention.Factory namingConvention,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
         AzureComputeApi api, AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants,
         LocationToResourceGroupName locationToResourceGroupName) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
              customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = checkNotNull(api, "api cannot be null");
      checkNotNull(userExecutor, "userExecutor cannot be null");
      this.azureComputeConstants = azureComputeConstants;
      this.locationToResourceGroupName = locationToResourceGroupName;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
                                                 Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
                                                 Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      // If there is a script to be run on the node and public key
      // authentication has been configured, warn users if the private key
      // is not present
      if (hasRunScriptWithKeyAuthAndNoPrivateKey(template)) {
         logger.warn(">> a runScript was configured but no SSH key has been provided. " +
                 "Authentication will delegate to the ssh-agent");
      }
      String azureGroupName = locationToResourceGroupName.apply(template.getLocation().getId());

      AzureTemplateOptions options = template.getOptions().as(AzureTemplateOptions.class);
      // create resource group for jclouds group if it does not already exist
      ResourceGroupApi resourceGroupApi = api.getResourceGroupApi();
      ResourceGroup resourceGroup = resourceGroupApi.get(azureGroupName);
      final String location = template.getLocation().getId();

      if (resourceGroup == null){
         final Map<String, String> tags = ImmutableMap.of("description", "jclouds managed VMs");
         resourceGroupApi.create(azureGroupName, location, tags).name();
      }

      String vnetName = azureGroupName + "virtualnetwork";
      String subnetName = azureGroupName + "subnet";

      if (options.getVirtualNetworkName() != null) {
         vnetName = options.getVirtualNetworkName();
      }

      this.getOrCreateVirtualNetworkWithSubnet(vnetName, subnetName, location, options, azureGroupName);

      StorageService storageService = getOrCreateStorageService(group, azureGroupName, location, template.getImage());
      String blob = storageService.storageServiceProperties().primaryEndpoints().get("blob");
      options.blob(blob);
      
      Map<?, ListenableFuture<Void>> responses = super.execute(group, count, template, goodNodes, badNodes,
              customizationResponses);

      return responses;
   }

   protected synchronized void getOrCreateVirtualNetworkWithSubnet(
           final String virtualNetworkName, final String subnetName, final String location,
           AzureTemplateOptions options, final String azureGroupName) {

      //Subnets belong to a virtual network so that needs to be created first
      VirtualNetworkApi vnApi = api.getVirtualNetworkApi(azureGroupName);
      VirtualNetwork vn = vnApi.get(virtualNetworkName);

      if (vn == null) {
         VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties = VirtualNetwork.VirtualNetworkProperties.builder()
                 .addressSpace(VirtualNetwork.AddressSpace.create(Arrays.asList(this.azureComputeConstants.azureDefaultVnetAddressPrefixProperty())))
                 .subnets(
                         Arrays.asList(
                                 Subnet.create(subnetName, null, null,
                                         Subnet.SubnetProperties.builder().addressPrefix(this.azureComputeConstants.azureDefaultSubnetAddressPrefixProperty()).build())))
                 .build();
         vn = vnApi.createOrUpdate(virtualNetworkName, location, virtualNetworkProperties);
      }

      SubnetApi subnetApi = api.getSubnetApi(azureGroupName, virtualNetworkName);
      Subnet subnet = subnetApi.get(subnetName);

      options.virtualNetworkName(virtualNetworkName);
      options.subnetId(subnet.id());
   }

   private static boolean hasRunScriptWithKeyAuthAndNoPrivateKey(Template template) {
      return template.getOptions().getRunScript() != null && template.getOptions().getPublicKey() != null
              && !template.getOptions().hasLoginPrivateKeyOption();
   }

   public StorageService getOrCreateStorageService(String name, String resourceGroupName, String locationName, Image image) {
      String storageAccountName = null;
      VMImage imageRef = decodeFieldsFromUniqueId(image.getId());
      if (imageRef.custom()) {
         storageAccountName = imageRef.storage();
      }

      if (Strings.isNullOrEmpty(storageAccountName)) {
         storageAccountName = generateStorageAccountName(name);
      }

      StorageService storageService = api.getStorageAccountApi(resourceGroupName).get(storageAccountName);
      if (storageService != null) return storageService;

      URI uri = api.getStorageAccountApi(resourceGroupName).create(storageAccountName, locationName, ImmutableMap.of("jclouds",
              name), ImmutableMap.of("accountType", StorageService.AccountType.Standard_LRS.toString()));
      boolean starageAccountCreated = retry(new Predicate<URI>() {
         @Override
         public boolean apply(URI uri) {
            return ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri);
         }
      }, 60 * 2 * 1000 /* 2 minutes timeout */).apply(uri);
      // TODO check provisioning state of the primary
      checkState(starageAccountCreated, "Storage account %s was not created in the configured timeout",
            storageAccountName);
      return api.getStorageAccountApi(resourceGroupName).get(storageAccountName);
   }

   /**
    * Generates a valid storage account
    *
    * Storage account names must be between 3 and 24 characters in length and may contain numbers and lowercase letters only.
    *
    * @param name the node name
    * @return the storage account name starting from a sanitized name (with only numbers and lowercase letters only ).
    * If sanitized name is between 3 and 24 characters, storage account name is equals to sanitized name.
    * If sanitized name is less than 3 characters, storage account is sanitized name plus 4 random chars.
    * If sanitized name is more than 24 characters, storage account is first 10 chars of sanitized name plus 4 random chars plus last 10 chars of sanitized name.
    */
   public static String generateStorageAccountName(String name) {
      String random = UUID.randomUUID().toString().substring(0, 4);
      String storageAccountName = new StringBuilder().append(name).append(random).toString();
      String sanitizedStorageAccountName = storageAccountName.replaceAll("[^a-z0-9]", "");
      int nameLength = sanitizedStorageAccountName.length();
      if (nameLength >= 3 && nameLength <= 24) {
         return sanitizedStorageAccountName;
      }

      if (nameLength > 24) {
         sanitizedStorageAccountName = shorten(sanitizedStorageAccountName, random);
      }
      return sanitizedStorageAccountName;
   }

   private static String shorten(String storageAccountName, String random) {
      String prefix = storageAccountName.substring(0, 10);
      String suffix = storageAccountName.substring(storageAccountName.length() - 10, storageAccountName.length());
      return String.format("%s%s%s", prefix, random, suffix);
   }
}
