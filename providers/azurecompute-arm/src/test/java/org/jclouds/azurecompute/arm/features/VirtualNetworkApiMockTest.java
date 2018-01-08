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

import org.jclouds.azurecompute.arm.domain.VirtualNetwork;

import org.testng.annotations.Test;

import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Iterables.isEmpty;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

@Test(groups = "unit", testName = "VirtualNetworkApiMockTest", singleThreaded = true)
public class VirtualNetworkApiMockTest extends BaseAzureComputeApiMockTest {

   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String virtualNetwork = "mockvirtualnetwork";
   private final String apiVersion = "api-version=2015-06-15";
   private final String location = "westeurope";

   public void getVirtualNetwork() throws InterruptedException {
      server.enqueue(jsonResponse("/virtualnetwork.json"));

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);
      VirtualNetwork vn = vnApi.get(virtualNetwork);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
      assertSent(server, "GET", path);
      assertEquals(vn.name(), "mockvirtualnetwork");
      assertEquals(vn.properties().resourceGuid(), "1568c76a-73a4-4a60-8dfb-53b823197ccb");
      assertEquals(vn.properties().addressSpace().addressPrefixes().get(0), "10.2.0.0/16");
      assertEquals(vn.tags().get("tagkey"), "tagvalue");
   }

   public void getVirtualNetworkReturns404() throws InterruptedException {
      server.enqueue(response404());

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);
      VirtualNetwork vn = vnApi.get(virtualNetwork);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
      assertSent(server, "GET", path);

      assertNull(vn);
   }

   public void listVirtualNetworks() throws InterruptedException {
      server.enqueue(jsonResponse("/listvirtualnetworks.json"));

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);
      List<VirtualNetwork> vnList = vnApi.list();
      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks?%s", subscriptionid, resourcegroup, apiVersion);

      assertSent(server, "GET", path);
      assertEquals(vnList.size(), 3);
   }

   public void listVirtualNetworkReturns404() throws InterruptedException {
      server.enqueue(response404());

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);
      List<VirtualNetwork> vnList = vnApi.list();
      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks?%s", subscriptionid, resourcegroup, apiVersion);

      assertSent(server, "GET", path);

      assertTrue(isEmpty(vnList));
   }

   public void createVirtualNetwork() throws InterruptedException {

      server.enqueue(jsonResponse("/createvirtualnetwork.json").setStatus("HTTP/1.1 201 Created"));

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);

      final VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties =
              VirtualNetwork.VirtualNetworkProperties.create(null, null,
                      VirtualNetwork.AddressSpace.create(Arrays.asList("10.2.0.0/16")), null);


      vnApi.createOrUpdate(virtualNetwork, location, null, virtualNetworkProperties);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
      String json = String.format("{\"location\":\"%s\",\"properties\":{\"addressSpace\":{\"addressPrefixes\":[\"%s\"]}}}", location, "10.2.0.0/16");
      assertSent(server, "PUT", path, json);
   }

   public void deleteVirtualNetwork() throws InterruptedException {

      server.enqueue(response202());

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);

      boolean status = vnApi.delete(virtualNetwork);
      assertTrue(status);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
      assertSent(server, "DELETE", path);

   }

   public void deleteVirtualNetworkResourceDoesNotExist() throws InterruptedException {

      server.enqueue(response204());

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);

      boolean status = vnApi.delete(virtualNetwork);
      assertFalse(status);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
      assertSent(server, "DELETE", path);
   }
}
