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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Access;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Direction;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Protocol;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", singleThreaded = true)
public class NetworkSecurityRuleApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static String UNKNOWN_RULE_NAME = "ruledoesntexist";
   private String nsgName;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      nsgName = String.format("nsg-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));

      // a network security group is needed
      final NetworkSecurityGroup nsg = newNetworkSecurityGroup(nsgName, LOCATION);
      assertNotNull(api.getNetworkSecurityGroupApi(resourceGroupName).createOrUpdate(nsgName,
              nsg.location(),
              nsg.tags(),
              nsg.properties()));
   }

   @Test
   public void deleteNetworkSecurityRuleDoesNotExist() {
      URI uri = api().delete(UNKNOWN_RULE_NAME);
      assertNull(uri);
   }

   @Test(dependsOnMethods = "deleteNetworkSecurityRuleDoesNotExist")
   public void createNetworkSecurityRule() {
      final NetworkSecurityRule rule = createRule();
      assertNotNull(rule);

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourceGroupName, nsgName);
      NetworkSecurityRule result = ruleApi.createOrUpdate(rule.name(), rule.properties());
      assertNotNull(result);
      assertEquals(result.name(), rule.name());
   }

   @Test(dependsOnMethods = "createNetworkSecurityRule")
   public void getNetworkSecurityRule() {
      final NetworkSecurityRule rule = createRule();
      assertNotNull(rule);

      NetworkSecurityRule result = api().get(rule.name());
      assertNotNull(result);
      assertNotNull(result.etag());
      assertEquals(result.name(), rule.name());
   }

   @Test(dependsOnMethods = "createNetworkSecurityRule")
   public void getNetworkSecurityDefaultRule() {
      String defaultRuleName = "AllowVnetInBound";

      NetworkSecurityRule result = api().getDefaultRule(defaultRuleName);

      assertNotNull(result);
      assertNotNull(result.etag());
      assertEquals(result.name(), defaultRuleName);
   }

   @Test(dependsOnMethods = "createNetworkSecurityRule")
   public void listNetworkSecurityRules() {
      final NetworkSecurityRule rule = createRule();
      assertNotNull(rule);

      List<NetworkSecurityRule> result = api().list();

      assertNotNull(result);
      assertEquals(result.size(), 2);

      boolean rulePresent = Iterables.any(result, new Predicate<NetworkSecurityRule>() {
         public boolean apply(NetworkSecurityRule input) {
            return input.name().equals(rule.name());
         }
      });

      assertTrue(rulePresent);
   }

   @Test(dependsOnMethods = "createNetworkSecurityRule")
   public void listDefaultSecurityRules() {
      List<NetworkSecurityRule> result = api().listDefaultRules();
      assertNotNull(result);
      assertTrue(result.size() > 0);
   }

   @Test(dependsOnMethods = {"listNetworkSecurityRules", "listDefaultSecurityRules", "getNetworkSecurityRule"})
   public void deleteNetworkSecurityRule() {
      final NetworkSecurityRule rule = createRule();
      assertNotNull(rule);

      URI uri = api().delete(rule.name());
      assertResourceDeleted(uri);
   }

   private NetworkSecurityRule createRule() {
      NetworkSecurityRule rule = NetworkSecurityRule.create("allowalludpin", null, null,
              NetworkSecurityRuleProperties.builder()
                      .description("allow all udp in")
                      .protocol(Protocol.Udp)
                      .sourcePortRange("*")
                      .destinationPortRange("*")
                      .sourceAddressPrefix("*")
                      .destinationAddressPrefix("*")
                      .access(Access.Allow)
                      .priority(4094)
                      .direction(Direction.Inbound)
                      .build());
      return rule;
   }

   private NetworkSecurityRuleApi api() {
      return api.getNetworkSecurityRuleApi(resourceGroupName, nsgName);
   }

}

