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

import org.jclouds.azurecompute.arm.domain.Resource;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "ResourceGroupApiMockTest", singleThreaded = true)
public class ResourceGroupApiMockTest extends BaseAzureComputeApiMockTest {

   final String subscriptionid = "SUBSCRIPTIONID";
   final String requestUrl = "/subscriptions/" + subscriptionid + "/resourcegroups";
   final String version = "?api-version=2015-01-01";

   public void testListResourceGroups() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroups.json"));

      List<ResourceGroup> resourceGroups = api.getResourceGroupApi().list();

      assertEquals(size(resourceGroups), 2);

      assertSent(server, "GET", requestUrl + version);
   }

   public void testListResourceGroupsReturns404() throws InterruptedException {
      server.enqueue(response404());

      List<ResourceGroup> resourceGroups = api.getResourceGroupApi().list();

      assertTrue(isEmpty(resourceGroups));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", requestUrl + version);
   }

   public void testCreateResourceGroup() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroup.json").setStatus("HTTP/1.1 201 Created"));

      ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder().put("tagname1", "tagvalue1").build();

      ResourceGroup resourceGroup = api.getResourceGroupApi().create("jcloudstest", "West US", tags);

      assertEquals(resourceGroup.name(), "jcloudstest");
      assertEquals(resourceGroup.location(), "westus");
      assertEquals(resourceGroup.tags().size(), 1);
      assertTrue(resourceGroup.id().contains("jcloudstest"));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", requestUrl + "/jcloudstest" + version, String.format("{\"location\":\"%s\", \"tags\":{\"tagname1\":\"tagvalue1\"}}", "West US"));
   }

   public void testCreateResourceGroupWithNoTag() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroup.json").setStatus("HTTP/1.1 201 Created"));

      ResourceGroup resourceGroup = api.getResourceGroupApi().create("jcloudstest", "West US", null);

      assertEquals(resourceGroup.name(), "jcloudstest");
      assertEquals(resourceGroup.location(), "westus");
      assertTrue(resourceGroup.id().contains("jcloudstest"));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", requestUrl + "/jcloudstest" + version, String.format("{\"location\":\"%s\"}", "West US"));
   }

   public void testGetResourceGroup() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroup.json"));

      ResourceGroup resourceGroup = api.getResourceGroupApi().get("jcloudstest");

      assertEquals(resourceGroup.name(), "jcloudstest");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", requestUrl + "/jcloudstest" + version);
   }

   public void testGetResourceGroupReturns404() throws InterruptedException {
      server.enqueue(response404());

      ResourceGroup resourceGroup = api.getResourceGroupApi().get("jcloudstest");

      assertNull(resourceGroup);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", requestUrl + "/jcloudstest" + version);
   }

   public void testUpdateResourceGroupTags() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroupupdated.json"));

      ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder().build();

      ResourceGroup resourceGroup = api.getResourceGroupApi().update("jcloudstest", tags);


      assertEquals(resourceGroup.tags().size(), 0);
      assertEquals(resourceGroup.properties().provisioningState(), "Succeeded");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PATCH", requestUrl + "/jcloudstest" + version, "{\"tags\":{}}");
   }

   public void testDeleteResourceGroup() throws InterruptedException {
      server.enqueue(response202WithHeader());

      URI uri = api.getResourceGroupApi().delete("jcloudstest");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", requestUrl + "/jcloudstest" + version);
      assertNotNull(uri);

      assertTrue(uri.toString().contains("api-version"));
      assertTrue(uri.toString().contains("operationresults"));
   }

   public void testDeleteResourceGroupReturns404() throws InterruptedException {
      server.enqueue(response404());

      URI uri = api.getResourceGroupApi().delete("jcloudstest");
      assertNull(uri);
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", requestUrl + "/jcloudstest" + version);
   }

   public void testListResourceGroupResources() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroup-resources.json"));

      List<Resource> resources = api.getResourceGroupApi().resources("jcloudstest");

      assertEquals(size(resources), 6);

      assertSent(server, "GET", requestUrl + "/jcloudstest/resources" + version);
   }

   public void testListResourceGroupResourcesReturns404() throws InterruptedException {
      server.enqueue(response404());

      List<Resource> resources = api.getResourceGroupApi().resources("jcloudstest");

      assertTrue(isEmpty(resources));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", requestUrl + "/jcloudstest/resources" + version);
   }

}
