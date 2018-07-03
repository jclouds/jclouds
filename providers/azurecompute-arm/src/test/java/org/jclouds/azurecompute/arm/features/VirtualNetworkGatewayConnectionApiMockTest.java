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

import static com.google.common.collect.Iterables.isEmpty;
import static org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnection.Type.IPsec;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnection;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnection.Type;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnectionProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "VirtualNetworkGatewayConnectionApiMockTest", singleThreaded = true)
public class VirtualNetworkGatewayConnectionApiMockTest extends BaseAzureComputeApiMockTest {

   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String name = "myconn";
   private final String apiVersion = "api-version=2018-02-01";

   private static final String LG_ID = "/subscriptions/SUBSCRIPTIONID/resourceGroups/rg-virtualnetworkgatewayconnectionapilivetest-nacx/providers/Microsoft.Network/localNetworkGateways/jclouds-827-lgw";
   private static final String VG_ID = "/subscriptions/SUBSCRIPTIONID/resourceGroups/rg-virtualnetworkgatewayconnectionapilivetest-nacx/providers/Microsoft.Network/virtualNetworkGateways/jclouds-827-vgw";

   public void createOrUpdateVirtualNetworkGateway() throws InterruptedException {
      server.enqueue(jsonResponse("/connectioncreate.json").setResponseCode(200));
      VirtualNetworkGatewayConnectionApi connapi = api.getVirtualNetworkGatewayConnectionApi(resourcegroup);

      VirtualNetworkGatewayConnectionProperties props = VirtualNetworkGatewayConnectionProperties
            .builder(IPsec, false, false, IdReference.create(LG_ID), IdReference.create(VG_ID)).sharedKey("jcl0uds")
            .build();

      VirtualNetworkGatewayConnection conn = connapi.createOrUpdate(name, "westeurope", null, props);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/connections/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      String json = "{\"location\":\"westeurope\",\"properties\":{\"connectionType\":\"IPsec\",\"enableBGP\":false,\"ipsecPolicies\":[],\"sharedKey\":\"jcl0uds\",\"tunnelConnectionStatus\":[],\"usePolicyBasedTrafficSelectors\":false,\"localNetworkGateway2\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/rg-virtualnetworkgatewayconnectionapilivetest-nacx/providers/Microsoft.Network/localNetworkGateways/jclouds-827-lgw\"},\"virtualNetworkGateway1\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/rg-virtualnetworkgatewayconnectionapilivetest-nacx/providers/Microsoft.Network/virtualNetworkGateways/jclouds-827-vgw\"}}}";
      assertSent(server, "PUT", path, json);

      assertEquals(conn.name(), name);
      assertNotNull(conn.properties());
      assertEquals(conn.properties().connectionType(), Type.IPsec);
   }

   public void getVirtualNetworkGateway() throws InterruptedException {
      server.enqueue(jsonResponse("/connectionget.json").setResponseCode(200));
      VirtualNetworkGatewayConnectionApi connapi = api.getVirtualNetworkGatewayConnectionApi(resourcegroup);

      VirtualNetworkGatewayConnection conn = connapi.get(name);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/connections/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "GET", path);

      assertEquals(conn.name(), name);
   }

   public void getVirtualNetworkGatewayReturns404() throws InterruptedException {
      server.enqueue(response404());
      VirtualNetworkGatewayConnectionApi connapi = api.getVirtualNetworkGatewayConnectionApi(resourcegroup);

      VirtualNetworkGatewayConnection conn = connapi.get(name);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/connections/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "GET", path);

      assertNull(conn);
   }

   public void listVirtualNetworkGateways() throws InterruptedException {
      server.enqueue(jsonResponse("/connectionlist.json").setResponseCode(200));
      VirtualNetworkGatewayConnectionApi connapi = api.getVirtualNetworkGatewayConnectionApi(resourcegroup);

      List<VirtualNetworkGatewayConnection> conns = connapi.list();

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/connections?%s",
            subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(conns.size() > 0);
   }

   public void listVirtualNetworkGatewaysReturns404() throws InterruptedException {
      server.enqueue(response404());
      VirtualNetworkGatewayConnectionApi connapi = api.getVirtualNetworkGatewayConnectionApi(resourcegroup);

      List<VirtualNetworkGatewayConnection> conns = connapi.list();

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/connections?%s",
            subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(isEmpty(conns));
   }

   public void deleteVirtualNetworkGateway() throws InterruptedException {
      server.enqueue(response202WithHeader());
      VirtualNetworkGatewayConnectionApi connapi = api.getVirtualNetworkGatewayConnectionApi(resourcegroup);

      URI uri = connapi.delete(name);
      assertNotNull(uri);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/connections/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "DELETE", path);
   }

   public void deleteVirtualNetworkGatewayDoesNotExist() throws InterruptedException {
      server.enqueue(response204());
      VirtualNetworkGatewayConnectionApi connapi = api.getVirtualNetworkGatewayConnectionApi(resourcegroup);

      URI uri = connapi.delete(name);
      assertNull(uri);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/connections/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "DELETE", path);
   }
}
