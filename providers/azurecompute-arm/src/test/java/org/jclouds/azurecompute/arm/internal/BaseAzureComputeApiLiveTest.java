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
package org.jclouds.azurecompute.arm.internal;
import static org.testng.Assert.assertNotNull;

import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;

import org.testng.annotations.AfterClass;

public class BaseAzureComputeApiLiveTest extends AbstractAzureComputeApiLiveTest {
   public static final String LOCATION = "westeurope";
   private String resourceGroupName = null;

   protected String getEndpoint() {
      String endpoint = null;
      if (System.getProperty("test.azurecompute-arm.endpoint") != null) {
         endpoint = System.getProperty("test.azurecompute-arm.endpoint");
      }
      assertNotNull(endpoint);
      return endpoint;
   }

   protected String getResourceGroupName() {
      if (resourceGroupName == null) {
         resourceGroupName = String.format("%3.24s",
                 System.getProperty("user.name") + RAND + "groupjclouds");
         createResourceGroup(resourceGroupName);
      }
      return resourceGroupName;
   }

   private void createResourceGroup(String name) {
      ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder().build();

      final ResourceGroup resourceGroup = api.getResourceGroupApi().create(
              name, LOCATION, tags);
   }

   private void deleteResourceGroup(String name) {
      api.getResourceGroupApi().delete(name);
   }


   @AfterClass(alwaysRun = true)
   @Override
   protected void tearDown() {
      super.tearDown();
      deleteResourceGroup(getResourceGroupName());
   }
}
