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

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;
import org.jclouds.Constants;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.features.ResourceGroupApi;
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

   @Inject
   protected CreateResourceGroupThenCreateNodes(
           CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
           ListNodesStrategy listNodesStrategy,
           GroupNamingConvention.Factory namingConvention,
           @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
           CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
           AzureComputeApi api) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
              customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = checkNotNull(api, "api cannot be null");
      checkNotNull(userExecutor, "userExecutor cannot be null");
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
                                                 Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
                                                 Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      ResourceGroupApi resourceGroupApi = api.getResourceGroupApi();
      ResourceGroup resourceGroup = resourceGroupApi.get(group);
      final String location = template.getLocation().getId();
      final String resourceGroupName;

      if (resourceGroup == null){

         final Map<String, String> tags = ImmutableMap.of("description", "jClouds managed VMs");
         resourceGroupName = resourceGroupApi.create(group, location, tags).name();
      } else {
         resourceGroupName = resourceGroup.name();
      }

      Map<?, ListenableFuture<Void>> responses = super.execute(resourceGroupName, count, template, goodNodes, badNodes,
              customizationResponses);

      return responses;
   }

}
