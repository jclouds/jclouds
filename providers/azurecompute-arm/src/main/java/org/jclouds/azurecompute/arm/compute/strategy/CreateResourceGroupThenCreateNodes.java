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

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;
import org.jclouds.Constants;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.features.ResourceGroupApi;
import org.jclouds.azurecompute.arm.features.SubnetApi;
import org.jclouds.azurecompute.arm.features.VirtualNetworkApi;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.logging.Logger;
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

   @Inject
   protected CreateResourceGroupThenCreateNodes(
           CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
           ListNodesStrategy listNodesStrategy,
           GroupNamingConvention.Factory namingConvention,
           @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
           CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
           AzureComputeApi api, AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
              customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = checkNotNull(api, "api cannot be null");
      checkNotNull(userExecutor, "userExecutor cannot be null");
      this.azureComputeConstants = azureComputeConstants;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
                                                 Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
                                                 Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      String azureGroupName = this.azureComputeConstants.azureResourceGroup();

      AzureTemplateOptions options = template.getOptions().as(AzureTemplateOptions.class);
      // create resource group for jclouds group if it does not already exist
      ResourceGroupApi resourceGroupApi = api.getResourceGroupApi();
      ResourceGroup resourceGroup = resourceGroupApi.get(azureGroupName);
      final String location = template.getLocation().getId();

      if (resourceGroup == null){
         final Map<String, String> tags = ImmutableMap.of("description", "jClouds managed VMs");
         resourceGroupApi.create(azureGroupName, location, tags).name();
      }

      String vnetName = azureGroupName + "virtualnetwork";
      String subnetName = azureGroupName + "subnet";

      if (options.getVirtualNetworkName() != null) {
         vnetName = options.getVirtualNetworkName();
      }

      this.getOrCreateVirtualNetworkWithSubnet(vnetName, subnetName, location, options, azureGroupName);


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
}
