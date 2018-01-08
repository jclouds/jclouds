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
package org.jclouds.azurecompute.arm.compute.loaders;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.functions.LocationToResourceGroupName;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.features.ResourceGroupApi;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;

@Singleton
public class DefaultResourceGroup extends CacheLoader<String, ResourceGroup> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ResourceGroupApi api;
   private final LocationToResourceGroupName locationToResourceGroupName;

   @Inject
   DefaultResourceGroup(AzureComputeApi api, LocationToResourceGroupName locationToResourceGroupName) {
      this.api = api.getResourceGroupApi();
      this.locationToResourceGroupName = locationToResourceGroupName;
   }

   @Override
   public ResourceGroup load(String locationId) throws Exception {
      String azureGroupName = locationToResourceGroupName.apply(locationId);
      ResourceGroup resourceGroup = api.get(azureGroupName);
      if (resourceGroup == null) {
         logger.debug(">> creating resource group %s", azureGroupName);
         final Map<String, String> tags = ImmutableMap.of("description", "jclouds default resource group");
         resourceGroup = api.create(azureGroupName, locationId, tags);
      }
      return resourceGroup;
   }
}
