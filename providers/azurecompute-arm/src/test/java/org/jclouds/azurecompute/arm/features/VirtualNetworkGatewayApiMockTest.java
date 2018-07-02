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
import static java.util.Collections.singletonList;
import static org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayType.Vpn;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpAllocationMethod;
import org.jclouds.azurecompute.arm.domain.vpn.SKU;
import org.jclouds.azurecompute.arm.domain.vpn.SKU.SKUName;
import org.jclouds.azurecompute.arm.domain.vpn.SKU.SKUTier;
import org.jclouds.azurecompute.arm.domain.vpn.VPNType;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGateway;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayProperties;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayProperties.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayProperties.IpConfiguration.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayType;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "VirtualNetworkGatewayApiMockTest", singleThreaded = true)
public class VirtualNetworkGatewayApiMockTest extends BaseAzureComputeApiMockTest {

   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String name = "myvirtualgw";
   private final String apiVersion = "api-version=2018-02-01";

   private static final String SUBNET_ID = "/subscriptions/SUBSCRIPTIONID/resourceGroups/rg-virtualnetworkgatewayapilivetest-nacx/providers/Microsoft.Network/virtualNetworks/myvirtualgw-net/subnets/GatewaySubnet";
   private static final String PUBLIC_IP = "/subscriptions/SUBSCRIPTIONID/resourceGroups/rg-virtualnetworkgatewayapilivetest-nacx/providers/Microsoft.Network/publicIPAddresses/myvirtualgw-publicip";

   public void createOrUpdateVirtualNetworkGateway() throws InterruptedException {
      server.enqueue(jsonResponse("/virtualnetworkgatewaycreate.json").setResponseCode(200));
      VirtualNetworkGatewayApi gwapi = api.getVirtualNetworkGatewayApi(resourcegroup);

      IpConfigurationProperties ipprops = IpConfigurationProperties.builder(IpAllocationMethod.Dynamic)
            .subnet(IdReference.create(SUBNET_ID)).publicIPAddress(IdReference.create(PUBLIC_IP)).build();
      IpConfiguration ipconf = IpConfiguration.create(null, name + "-ipconf", null, ipprops);

      VirtualNetworkGatewayProperties props = VirtualNetworkGatewayProperties
            .builder(false, Vpn, SKU.create(1, SKUName.Basic, SKUTier.Basic)).vpnType(VPNType.PolicyBased)
            .ipConfigurations(singletonList(ipconf)).build();

      VirtualNetworkGateway gw = gwapi.createOrUpdate(name, "westeurope", null, props);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      String json = "{\"location\":\"westeurope\",\"properties\":{\"enableBgp\":false,\"gatewayType\":\"Vpn\",\"ipConfigurations\":[{\"name\":\"myvirtualgw-ipconf\",\"properties\":{\"privateIPAllocationMethod\":\"Dynamic\",\"publicIPAddress\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/rg-virtualnetworkgatewayapilivetest-nacx/providers/Microsoft.Network/publicIPAddresses/myvirtualgw-publicip\"},\"subnet\":{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/rg-virtualnetworkgatewayapilivetest-nacx/providers/Microsoft.Network/virtualNetworks/myvirtualgw-net/subnets/GatewaySubnet\"}}}],\"sku\":{\"capacity\":1,\"name\":\"Basic\",\"tier\":\"Basic\"},\"vpnType\":\"PolicyBased\"}}";
      assertSent(server, "PUT", path, json);

      assertEquals(gw.name(), name);
      assertNotNull(gw.properties());
      assertEquals(gw.properties().gatewayType(), VirtualNetworkGatewayType.Vpn);
   }

   public void getVirtualNetworkGateway() throws InterruptedException {
      server.enqueue(jsonResponse("/virtualnetworkgatewayget.json").setResponseCode(200));
      VirtualNetworkGatewayApi gwapi = api.getVirtualNetworkGatewayApi(resourcegroup);

      VirtualNetworkGateway gw = gwapi.get(name);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "GET", path);

      assertEquals(gw.name(), name);
   }

   public void getVirtualNetworkGatewayReturns404() throws InterruptedException {
      server.enqueue(response404());
      VirtualNetworkGatewayApi gwapi = api.getVirtualNetworkGatewayApi(resourcegroup);

      VirtualNetworkGateway gw = gwapi.get(name);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "GET", path);

      assertNull(gw);
   }

   public void listVirtualNetworkGateways() throws InterruptedException {
      server.enqueue(jsonResponse("/virtualnetworkgatewaylist.json").setResponseCode(200));
      VirtualNetworkGatewayApi gwapi = api.getVirtualNetworkGatewayApi(resourcegroup);

      List<VirtualNetworkGateway> gws = gwapi.list();

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworkGateways?%s",
            subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(gws.size() > 0);
   }

   public void listVirtualNetworkGatewaysReturns404() throws InterruptedException {
      server.enqueue(response404());
      VirtualNetworkGatewayApi gwapi = api.getVirtualNetworkGatewayApi(resourcegroup);

      List<VirtualNetworkGateway> gws = gwapi.list();

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworkGateways?%s",
            subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(isEmpty(gws));
   }

   public void deleteVirtualNetworkGateway() throws InterruptedException {
      server.enqueue(response202WithHeader());
      VirtualNetworkGatewayApi gwapi = api.getVirtualNetworkGatewayApi(resourcegroup);

      URI uri = gwapi.delete(name);
      assertNotNull(uri);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "DELETE", path);
   }

   public void deleteVirtualNetworkGatewayDoesNotExist() throws InterruptedException {
      server.enqueue(response204());
      VirtualNetworkGatewayApi gwapi = api.getVirtualNetworkGatewayApi(resourcegroup);

      URI uri = gwapi.delete(name);
      assertNull(uri);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworkGateways/%s?%s",
            subscriptionid, resourcegroup, name, apiVersion);
      assertSent(server, "DELETE", path);
   }
}
