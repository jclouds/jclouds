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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.AddressSpace;
import org.jclouds.azurecompute.arm.domain.vpn.LocalNetworkGateway;
import org.jclouds.azurecompute.arm.domain.vpn.LocalNetworkGatewayProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "LocalNetworkGatewayApiMockTest", singleThreaded = true)
public class LocalNetworkGatewayApiMockTest extends BaseAzureComputeApiMockTest {

   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String name = "mylocalgw";
   private final String apiVersion = "api-version=2018-02-01";

   public void createOrUpdateLocalNetworkGateway() throws InterruptedException {
      server.enqueue(jsonResponse("/localnetworkgatewaycreate.json").setResponseCode(200));
      LocalNetworkGatewayApi gwapi = api.getLocalNetworkGatewayApi(resourcegroup);

      AddressSpace localAddresses = AddressSpace.create(ImmutableList.of("192.168.0.0/24"));
      LocalNetworkGatewayProperties props = LocalNetworkGatewayProperties.builder("1.2.3.4")
            .localNetworkAddressSpace(localAddresses).build();
      LocalNetworkGateway gw = gwapi.createOrUpdate(name, "westeurope", null, props);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/localNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      String json = "{\"location\":\"westeurope\",\"properties\":{\"gatewayIpAddress\":\"1.2.3.4\",\"localNetworkAddressSpace\":{\"addressPrefixes\":[\"192.168.0.0/24\"]}}}";
      assertSent(server, "PUT", path, json);

      assertEquals(gw.name(), name);
      assertNotNull(gw.properties());
      assertNotNull(gw.properties().gatewayIpAddress());
      assertEquals(gw.properties().gatewayIpAddress(), "1.2.3.4");
      assertNotNull(gw.properties().localNetworkAddressSpace());
      assertTrue(gw.properties().localNetworkAddressSpace().addressPrefixes().contains("192.168.0.0/24"));
   }

   public void getLocalNetworkGateway() throws InterruptedException {
      server.enqueue(jsonResponse("/localnetworkgatewayget.json").setResponseCode(200));
      LocalNetworkGatewayApi gwapi = api.getLocalNetworkGatewayApi(resourcegroup);

      LocalNetworkGateway gw = gwapi.get(name);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/localNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "GET", path);

      assertEquals(gw.name(), name);
   }

   public void getLocalNetworkGatewayReturns404() throws InterruptedException {
      server.enqueue(response404());
      LocalNetworkGatewayApi gwapi = api.getLocalNetworkGatewayApi(resourcegroup);

      LocalNetworkGateway gw = gwapi.get(name);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/localNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "GET", path);

      assertNull(gw);
   }

   public void listLocalNetworkGateways() throws InterruptedException {
      server.enqueue(jsonResponse("/localnetworkgatewaylist.json").setResponseCode(200));
      LocalNetworkGatewayApi gwapi = api.getLocalNetworkGatewayApi(resourcegroup);

      List<LocalNetworkGateway> gws = gwapi.list();

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/localNetworkGateways?%s",
            subscriptionid, resourcegroup,  apiVersion);
      assertSent(server, "GET", path);

      assertTrue(gws.size() > 0);
   }

   public void listLocalNetworkGatewaysReturns404() throws InterruptedException {
      server.enqueue(response404());
      LocalNetworkGatewayApi gwapi = api.getLocalNetworkGatewayApi(resourcegroup);

      List<LocalNetworkGateway> gws = gwapi.list();

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/localNetworkGateways?%s",
            subscriptionid, resourcegroup,  apiVersion);
      assertSent(server, "GET", path);

      assertTrue(isEmpty(gws));
   }

   public void deleteLocalNetworkGateway() throws InterruptedException {
      server.enqueue(response202WithHeader());
      LocalNetworkGatewayApi gwapi = api.getLocalNetworkGatewayApi(resourcegroup);

      URI uri = gwapi.delete(name);
      assertNotNull(uri);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/localNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "DELETE", path);
   }

   public void deleteLocalNetworkGatewayDoesNotExist() throws InterruptedException {
      server.enqueue(response204());
      LocalNetworkGatewayApi gwapi = api.getLocalNetworkGatewayApi(resourcegroup);

      URI uri = gwapi.delete(name);
      assertNull(uri);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/localNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "DELETE", path);
   }
}
