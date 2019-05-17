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

import com.google.gson.Gson;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Protocol;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.List;

import static com.google.common.collect.Iterables.isEmpty;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;


@Test(groups = "unit", testName = "NetworkSecurityRuleApiMockTest", singleThreaded = true)
public class NetworkSecurityRuleApiMockTest extends BaseAzureComputeApiMockTest {
   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String apiVersion = "api-version=2016-03-30";
   private static String DEFAULT_NSG_NAME = "testNetworkSecurityGroup";

   private NetworkSecurityRule createRule() {
      NetworkSecurityRule rule = NetworkSecurityRule.create("allowalludpin", null, null,
              NetworkSecurityRuleProperties.builder()
                      .description("allow all udp in")
                      .protocol(Protocol.Udp)
                      .sourcePortRange("*")
                      .destinationPortRange("*")
                      .sourceAddressPrefix("*")
                      .destinationAddressPrefix("*")
                      .access(NetworkSecurityRuleProperties.Access.Allow)
                      .priority(4094)
                      .direction(NetworkSecurityRuleProperties.Direction.Inbound)
                      .provisioningState("Succeeded")
                      .build());
      return rule;
   }

   public void createNetworkSecurityRule() throws InterruptedException {
      NetworkSecurityRule rule = createRule();

      server.enqueue(jsonResponse("/networksecurityrulecreate.json").setResponseCode(200));
      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/securityRules/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, rule.name(), apiVersion);
      NetworkSecurityRule result = ruleApi.createOrUpdate(rule.name(), rule.properties());
      String json = String.format("{\"properties\":%s}", new Gson().toJson(rule.properties()));

      assertSent(server, "PUT", path, json);

      assertNotNull(result);
      assertEquals(result.name(), rule.name());
   }

   public void getNetworkSecurityRule() throws InterruptedException {
      NetworkSecurityRule rule = createRule();

      server.enqueue(jsonResponse("/networksecurityruleget.json").setResponseCode(200));
      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/securityRules/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, rule.name(), apiVersion);
      NetworkSecurityRule result = ruleApi.get(rule.name());
      assertSent(server, "GET", path);

      assertEquals(result.name(), rule.name());
   }

   public void getNetworkSecurityRuleReturns404() throws InterruptedException {
      server.enqueue(response404());

      String missingRuleName = "ruleismissing";
      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/securityRules/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, missingRuleName, apiVersion);
      NetworkSecurityRule result = ruleApi.get(missingRuleName);
      assertSent(server, "GET", path);

      assertNull(result);
   }

   public void getNetworkSecurityDefaultRule() throws InterruptedException {
      server.enqueue(jsonResponse("/networksecurityrulegetdefault.json").setResponseCode(200));
      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      String ruleName = "AllowVnetInBound";

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/defaultSecurityRules/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, ruleName, apiVersion);
      NetworkSecurityRule result = ruleApi.getDefaultRule(ruleName);
      assertSent(server, "GET", path);

      assertNotNull(result);
      assertEquals(result.name(), ruleName);
   }

   public void getNetworkSecurityDefaultRuleReturns404() throws InterruptedException {
      server.enqueue(response404());

      String missingRuleName = "ruleismissing";
      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/defaultSecurityRules/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, missingRuleName, apiVersion);
      NetworkSecurityRule result = ruleApi.getDefaultRule(missingRuleName);
      assertSent(server, "GET", path);

      assertNull(result);
   }

   public void listNetworkSecurityRules() throws InterruptedException {
      server.enqueue(jsonResponse("/networksecurityrulelist.json").setResponseCode(200));

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/securityRules?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, apiVersion);
      List<NetworkSecurityRule> result = ruleApi.list();
      assertSent(server, "GET", path);

      assertNotNull(result);
      assertTrue(result.size() > 0);
   }

   public void listNetworkSecurityRulesReturns404() throws InterruptedException {
      server.enqueue(response404());

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/securityRules?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, apiVersion);
      List<NetworkSecurityRule> result = ruleApi.list();
      assertSent(server, "GET", path);

      assertTrue(isEmpty(result));
   }

   public void listNetworkSecurityDefaultRules() throws InterruptedException {
      server.enqueue(jsonResponse("/networksecurityrulelistdefault.json").setResponseCode(200));

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/defaultSecurityRules?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, apiVersion);
      List<NetworkSecurityRule> result = ruleApi.listDefaultRules();
      assertSent(server, "GET", path);

      assertNotNull(result);
      assertTrue(result.size() > 0);
   }

   public void listNetworkSecurityDefaultRulesReturns404() throws InterruptedException {
      server.enqueue(response404());

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/defaultSecurityRules?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, apiVersion);
      List<NetworkSecurityRule> result = ruleApi.listDefaultRules();
      assertSent(server, "GET", path);

      assertTrue(isEmpty(result));
   }

   public void deleteNetworkSecurityRule() throws InterruptedException {
      server.enqueue(response202WithHeader());

      NetworkSecurityRule rule = createRule();

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      URI uri = ruleApi.delete(rule.name());

      assertEquals(server.getRequestCount(), 1);
      assertNotNull(uri);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/securityRules/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, rule.name(), apiVersion);
      assertSent(server, "DELETE", path);

      assertTrue(uri.toString().contains("api-version"));
      assertTrue(uri.toString().contains("operationresults"));
   }

   public void deleteNetworkSecurityRuleDoesNotExist() throws InterruptedException {
      server.enqueue(response404());

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      String dummyname = "dummyrulename";
      URI uri = ruleApi.delete(dummyname);
      assertNull(uri);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkSecurityGroups/%s/securityRules/%s?%s", subscriptionid, resourcegroup, DEFAULT_NSG_NAME, dummyname, apiVersion);
      assertSent(server, "DELETE", path);
   }
}
