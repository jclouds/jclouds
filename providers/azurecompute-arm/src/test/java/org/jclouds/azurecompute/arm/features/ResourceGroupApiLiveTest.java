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

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "ResourceGroupApiLiveTest")
public class ResourceGroupApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String RESOURCE_GROUP_NAME = "jcloudstest";

   private ResourceGroupApi api() {
      return api.getResourceGroupApi();
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      final List<ResourceGroup> resourceGroups = api().list();

      assertTrue(resourceGroups.size() > 0);

      assertTrue(Iterables.any(resourceGroups, new Predicate<ResourceGroup>() {

         @Override
         public boolean apply(final ResourceGroup group) {
            return RESOURCE_GROUP_NAME.equals(group.name());
         }
      }));
   }

   @Test(dependsOnMethods = "testCreate")
   public void testRead() {
      final ResourceGroup resourceGroup = api().get(RESOURCE_GROUP_NAME);
      assertNotNull(resourceGroup);
      assertEquals(resourceGroup.name(), RESOURCE_GROUP_NAME);
      assertEquals(resourceGroup.location(), LOCATION);
   }

   public void testCreate() {

      final ResourceGroup resourceGroup = api().create(RESOURCE_GROUP_NAME, LOCATION, null);
      assertEquals(resourceGroup.name(), RESOURCE_GROUP_NAME);
      assertEquals(resourceGroup.location(), LOCATION);
      assertNull(resourceGroup.tags());
      assertTrue(resourceGroup.id().contains(RESOURCE_GROUP_NAME));
      assertEquals(resourceGroup.properties().provisioningState(), "Succeeded");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdateWithEmptyTag() {
      ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder().build();

      final ResourceGroup resourceGroup = api().update(RESOURCE_GROUP_NAME, tags);

      assertEquals(resourceGroup.tags().size(), 0);
      assertEquals(resourceGroup.properties().provisioningState(), "Succeeded");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdateWithTag() {
      ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder().put("test1", "value1").build();

      final ResourceGroup resourceGroup = api().update(RESOURCE_GROUP_NAME, tags);

      assertEquals(resourceGroup.tags().size(), 1);
      assertEquals(resourceGroup.properties().provisioningState(), "Succeeded");
   }

   @AfterClass(alwaysRun = true)
   public void testDelete() throws Exception {
      URI uri =  api().delete(RESOURCE_GROUP_NAME);
      assertResourceDeleted(uri);
   }
}
