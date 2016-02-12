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

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus.JobStatus;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import org.jclouds.util.Predicates2;


@Test(groups = "live", testName = "ResourceGroupApiLiveTest")
public class ResourceGroupApiLiveTest extends BaseAzureComputeApiLiveTest {
   private String resourcegroup;

   @BeforeClass
   @Override
   public void setup(){
      super.setup();
      resourcegroup = getResourceGroupName();
   }

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
            return resourcegroup.equals(group.name());
         }
      }));
   }

   @Test(dependsOnMethods = "testCreate")
   public void testRead() {
      final ResourceGroup group = api().get(resourcegroup);
      assertNotNull(group);
      assertEquals(group.name(), resourcegroup);
      assertEquals(group.location(), LOCATION);
   }

   public void testCreate() {

      final ResourceGroup resourceGroup = api().create("jcloudstest", LOCATION, null);
      assertEquals(resourceGroup.name(), "jcloudstest");
      assertEquals(resourceGroup.location(), LOCATION);
      assertEquals(resourceGroup.tags().size(), 0);
      assertTrue(resourceGroup.id().contains("jcloudstest"));
      assertEquals(resourceGroup.properties().provisioningState(), "Succeeded");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdateWithEmptyTag() {
      ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder().build();

      final ResourceGroup resourceGroup = api().update("jcloudstest", tags);

      assertEquals(resourceGroup.tags().size(), 0);
      assertEquals(resourceGroup.properties().provisioningState(), "Succeeded");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdateWithTag() {
      ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder().put("test1", "value1").build();

      final ResourceGroup resourceGroup = api().update("jcloudstest", tags);

      assertEquals(resourceGroup.tags().size(), 1);
      assertEquals(resourceGroup.properties().provisioningState(), "Succeeded");
   }

   @AfterClass(alwaysRun = true)
   public void testDelete() throws Exception {
      URI uri =  api().delete(resourcegroup);

      if (uri != null){
         assertTrue(uri.toString().contains("api-version"));
         assertTrue(uri.toString().contains("operationresults"));

         boolean jobDone = Predicates2.retry(new Predicate<URI>() {
            @Override public boolean apply(URI uri) {
               return JobStatus.DONE == api.getJobApi().jobStatus(uri);
            }
         }, 60 * 1 * 1000 /* 1 minute timeout */).apply(uri);
         assertTrue(jobDone, "delete operation did not complete in the configured timeout");
      }

      uri =  api().delete("jcloudstest");
      if (uri != null){
         assertTrue(uri.toString().contains("api-version"));
         assertTrue(uri.toString().contains("operationresults"));

         boolean jobDone = Predicates2.retry(new Predicate<URI>() {
            @Override public boolean apply(URI uri) {
               return JobStatus.DONE == api.getJobApi().jobStatus(uri);
            }
         }, 60 * 1 * 1000 /* 1 minute timeout */).apply(uri);
         assertTrue(jobDone, "delete operation did not complete in the configured timeout");
      }
   }
}
