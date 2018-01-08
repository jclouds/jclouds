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
package org.jclouds.azurecompute.arm.features;

import java.util.List;

import org.jclouds.azurecompute.arm.domain.ResourceProviderMetaData;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "ResourceProviderApiLiveTest")
public class ResourceProviderApiLiveTest extends BaseAzureComputeApiLiveTest {

   private final String PROVIDER = "Microsoft.Compute";
   private final String VM_RESOURCE_TYPE = "virtualMachines";

   private ResourceProviderApi api() {
      return api.getResourceProviderApi();
   }

   @Test
   public void testGetComputeProviderMetadata() {

      List<ResourceProviderMetaData> resourceProviderMetaDatas = api().get(PROVIDER);

      assertNotNull(resourceProviderMetaDatas);

      assertTrue(Iterables.any(resourceProviderMetaDatas, new Predicate<ResourceProviderMetaData>() {
         @Override
         public boolean apply(final ResourceProviderMetaData providerMetaData) {
            return providerMetaData.resourceType().equals(VM_RESOURCE_TYPE);
         }
      }));
   }
}
