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
import com.google.common.collect.Iterables;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroupProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Access;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Direction;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Protocol;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;

import org.jclouds.util.Predicates2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test(groups = "live", singleThreaded = true)
public class NetworkSecurityRuleApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String resourcegroup;
   private static String DEFAULT_NSG_NAME = "testNetworkSecurityGroup";
   private static String UNKNOWN_RULE_NAME = "ruledoesntexist";

   private NetworkSecurityGroup createGroup() {
      NetworkSecurityRule rule = NetworkSecurityRule.create("denyallout", null, null,
              NetworkSecurityRuleProperties.builder()
                      .description("deny all out")
                      .protocol(Protocol.Tcp)
                      .sourcePortRange("*")
                      .destinationPortRange("*")
                      .sourceAddressPrefix("*")
                      .destinationAddressPrefix("*")
                      .access(Access.Deny)
                      .priority(4095)
                      .direction(Direction.Outbound)
                      .build());
      ArrayList<NetworkSecurityRule> ruleList = new ArrayList<NetworkSecurityRule>();
      ruleList.add(rule);
      NetworkSecurityGroup nsg = NetworkSecurityGroup.create("samplensg", "westus", null,
              NetworkSecurityGroupProperties.builder()
                      .securityRules(ruleList)
                      .build(),
              null);
      return nsg;
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

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      resourcegroup = getResourceGroupName();

      // a network security group is needed
      final NetworkSecurityGroup nsg = createGroup();
      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      NetworkSecurityGroup result = nsgApi.createOrUpdate(DEFAULT_NSG_NAME,
              nsg.location(),
              nsg.tags(),
              nsg.properties());
   }

   @AfterClass(alwaysRun = true)
   @Override
   public void tearDown() {
      // remove the security group we created
      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      URI uri = nsgApi.delete(DEFAULT_NSG_NAME);
      if (uri != null) {
         boolean jobDone = Predicates2.retry(new Predicate<URI>() {
            @Override
            public boolean apply(URI uri) {
               return ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri);
            }
         }, 60 * 2 * 1000 /* 2 minute timeout */).apply(uri);
      }

      super.tearDown();
   }

   @Test(groups = "live")
   public void deleteNetworkSecurityRuleDoesNotExist() {
      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      URI uri = ruleApi.delete(UNKNOWN_RULE_NAME);
      assertNull(uri);
   }

   @Test(groups = "live", dependsOnMethods = "deleteNetworkSecurityRuleDoesNotExist")
   public void createNetworkSecurityRule() {
      final NetworkSecurityRule rule = createRule();
      assertNotNull(rule);

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      NetworkSecurityRule result = ruleApi.createOrUpdate(rule.name(), rule.properties());
      assertNotNull(result);
      assertEquals(result.name(), rule.name());
   }

   @Test(groups = "live", dependsOnMethods = "createNetworkSecurityRule")
   public void getNetworkSecurityRule() {
      final NetworkSecurityRule rule = createRule();
      assertNotNull(rule);

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      NetworkSecurityRule result = ruleApi.get(rule.name());
      assertNotNull(result);
      assertNotNull(result.etag());
      assertEquals(result.name(), rule.name());
   }

   @Test(groups = "live", dependsOnMethods = "createNetworkSecurityRule")
   public void getNetworkSecurityDefaultRule() {
      String defaultRuleName = "AllowVnetInBound";

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      NetworkSecurityRule result = ruleApi.getDefaultRule(defaultRuleName);

      assertNotNull(result);
      assertNotNull(result.etag());
      assertEquals(result.name(), defaultRuleName);
   }

   @Test(groups = "live", dependsOnMethods = "createNetworkSecurityRule")
   public void listNetworkSecurityRules() {
      final NetworkSecurityRule rule = createRule();
      assertNotNull(rule);

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      List<NetworkSecurityRule> result = ruleApi.list();

      assertNotNull(result);
      assertEquals(result.size(), 2);

      boolean rulePresent = Iterables.any(result, new Predicate<NetworkSecurityRule>() {
         public boolean apply(NetworkSecurityRule input) {
            return input.name().equals(rule.name());
         }
      });

      assertTrue(rulePresent);
   }

   @Test(groups = "live", dependsOnMethods = "createNetworkSecurityRule")
   public void listDefaultSecurityRules() {
      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      List<NetworkSecurityRule> result = ruleApi.listDefaultRules();

      assertNotNull(result);
      assertTrue(result.size() > 0);
   }

   @Test(groups = "live", dependsOnMethods = {"listNetworkSecurityRules", "listDefaultSecurityRules", "getNetworkSecurityRule"}, alwaysRun = true)
   public void deleteNetworkSecurityRule() {
      final NetworkSecurityRule rule = createRule();
      assertNotNull(rule);

      final NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourcegroup, DEFAULT_NSG_NAME);
      URI uri = ruleApi.delete(rule.name());
      if (uri != null) {
         assertTrue(uri.toString().contains("api-version"));
         assertTrue(uri.toString().contains("operationresults"));

         boolean jobDone = Predicates2.retry(new Predicate<URI>() {
            @Override
            public boolean apply(URI uri) {
               return ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri);
            }
         }, 60 * 2 * 1000 /* 2 minute timeout */).apply(uri);
         assertTrue(jobDone, "delete operation did not complete in the configured timeout");
      }
   }

}

