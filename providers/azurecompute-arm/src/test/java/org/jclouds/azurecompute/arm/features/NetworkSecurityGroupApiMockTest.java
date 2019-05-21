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
import java.util.ArrayList;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroupProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Protocol;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.google.gson.Gson;

@Test(groups = "unit", testName = "NetworkSecurityGroupApiMockTest", singleThreaded = true)
public class NetworkSecurityGroupApiMockTest extends BaseAzureComputeApiMockTest {
   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String apiVersion = "api-version=2016-03-30";
   private static String DEFAULT_NSG_NAME = "testNetworkSecurityGroup";

   private NetworkSecurityGroup createGroup() {
      NetworkSecurityRule rule = NetworkSecurityRule.create("denyallout", null, null,
              NetworkSecurityRuleProperties.builder()
                      .description("deny all out")
                      .protocol(Protocol.Tcp)
                      .sourcePortRange("*")
                      .destinationPortRange("*")
                      .sourceAddressPrefix("*")
                      .destinationAddressPrefix("*")
                      .access(NetworkSecurityRuleProperties.Access.Deny)
                      .priority(4095)
                      .direction(NetworkSecurityRuleProperties.Direction.Outbound)
                      .provisioningState("Succeeded")
                      .build());
      ArrayList<NetworkSecurityRule> ruleList = new ArrayList<NetworkSecurityRule>();
      ruleList.add(rule);
      NetworkSecurityGroup nsg = NetworkSecurityGroup.create("id", "samplensg", "westus", null,
              NetworkSecurityGroupProperties.builder()
                      .securityRules(ruleList)
                      .build(),
              null);
      return nsg;
   }

   public void createNetworkSecurityGroup() throws InterruptedException {
      NetworkSecurityGroup nsg = createGroup();

      server.enqueue(jsonResponse("/networksecuritygroupcreate.json").setResponseCode(200));
      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, apiVersion);
      String json = String.format("{\"location\":\"%s\",\"properties\":%s}", "westus", new Gson().toJson(nsg.properties()));
      NetworkSecurityGroup result = nsgApi.createOrUpdate(DEFAULT_NSG_NAME, "westus", null, nsg.properties());
      assertSent(server, "PUT", path, json);

      assertEquals(result.name(), DEFAULT_NSG_NAME);
      assertEquals(result.location(), "westus");
      assertEquals(result.properties().securityRules().size(), 1);
      assertEquals(result.properties().securityRules().get(0).properties().protocol(), Protocol.Tcp);
   }

   public void getNetworkSecurityGroup() throws InterruptedException {
      server.enqueue(jsonResponse("/networksecuritygroupget.json").setResponseCode(200));

      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      NetworkSecurityGroup result = nsgApi.get(DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertEquals(result.name(), DEFAULT_NSG_NAME);
      assertEquals(result.location(), "westus");
      assertEquals(result.properties().securityRules().size(), 1);
      assertEquals(result.properties().securityRules().get(0).properties().protocol(), Protocol.Tcp);
   }

   public void getNetworkSecurityGroupReturns404() throws InterruptedException {
      server.enqueue(response404());

      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      NetworkSecurityGroup result = nsgApi.get(DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, apiVersion);
      assertSent(server, "GET", path);

      assertNull(result);
   }

   public void listNetworkSecurityGroups() throws InterruptedException {
      server.enqueue(jsonResponse("/networksecuritygrouplist.json").setResponseCode(200));

      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      List<NetworkSecurityGroup> result = nsgApi.list();

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups?%s", subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(result);
      assertTrue(result.size() > 0);
   }

   public void listAllNetworkSecurityGroups() throws InterruptedException {
      server.enqueue(jsonResponse("/networksecuritygrouplistall.json").setResponseCode(200));

      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      List<NetworkSecurityGroup> result = nsgApi.listAll();

      String path = String.format("/subscriptions/%s/providers/Microsoft.Network/networkSecurityGroups?%s", subscriptionid, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(result);
      assertEquals(result.size(), 2);
   }

   public void listNetworkSecurityGroupsReturns404() throws InterruptedException {
      server.enqueue(response404());

      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      List<NetworkSecurityGroup> result = nsgApi.list();

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups?%s", subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(isEmpty(result));
   }

   public void deleteNetworkSecurityGroup() throws InterruptedException {
      server.enqueue(response202WithHeader());

      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      URI uri = nsgApi.delete(DEFAULT_NSG_NAME);

      assertEquals(server.getRequestCount(), 1);
      assertNotNull(uri);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, apiVersion);
      assertSent(server, "DELETE", path);

      assertTrue(uri.toString().contains("api-version"));
      assertTrue(uri.toString().contains("operationresults"));
   }

   public void deleteNetworkSecurityGroupDoesNotExist() throws InterruptedException {
      server.enqueue(response404());

      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      URI uri = nsgApi.delete(DEFAULT_NSG_NAME);
      assertNull(uri);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, apiVersion);
      assertSent(server, "DELETE", path);
   }
}
