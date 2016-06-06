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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.util.ArrayList;
import java.util.List;
import java.net.URI;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

@Test(groups = "live", singleThreaded = true)
public class NetworkSecurityGroupApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String resourcegroup;
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

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      resourcegroup = getResourceGroupName();
   }

   @Test(groups = "live")
   public void deleteNetworkSecurityGroupDoesNotExist() {
      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      URI uri = nsgApi.delete(DEFAULT_NSG_NAME);
      assertNull(uri);
   }

   @Test(groups = "live", dependsOnMethods = "deleteNetworkSecurityGroupDoesNotExist")
   public void createNetworkSecurityGroup() {
      final NetworkSecurityGroup nsg = createGroup();
      assertNotNull(nsg);

      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      NetworkSecurityGroup result = nsgApi.createOrUpdate(DEFAULT_NSG_NAME,
                                                  nsg.location(),
                                                  nsg.tags(),
                                                  nsg.properties());
      assertNotNull(result);
   }

   @Test(groups = "live", dependsOnMethods = "createNetworkSecurityGroup")
   public void listNetworkSecurityGroups() {
      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      List<NetworkSecurityGroup> result = nsgApi.list();

      // verify we have something
      assertNotNull(result);
      assertEquals(result.size(), 1);

      // check that the nework security group matches the one we originally passed in
      NetworkSecurityGroup original = createGroup();
      NetworkSecurityGroup nsg = result.get(0);
      assertEquals(original.name(), nsg.name());
      assertEquals(original.location(), nsg.location());
      assertEquals(original.tags(), nsg.tags());

      // check the network security rule in the group
      assertEquals(nsg.properties().securityRules().size(), 1);
      NetworkSecurityRule originalRule = original.properties().securityRules().get(0);
      NetworkSecurityRule nsgRule = nsg.properties().securityRules().get(0);
      assertEquals(originalRule.name(), nsgRule.name());
      assertTrue(originalRule.properties().equals(nsgRule.properties()));
   }

   @Test(groups = "live", dependsOnMethods = {"listNetworkSecurityGroups", "getNetworkSecurityGroup"}, alwaysRun = true)
   public void deleteNetworkSecurityGroup() {
      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      URI uri = nsgApi.delete(DEFAULT_NSG_NAME);
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

   @Test(groups = "live", dependsOnMethods = "createNetworkSecurityGroup")
   public void getNetworkSecurityGroup() {
      final NetworkSecurityGroupApi nsgApi = api.getNetworkSecurityGroupApi(resourcegroup);
      NetworkSecurityGroup nsg = nsgApi.get(DEFAULT_NSG_NAME);
      assertNotNull(nsg);
      assertNotNull(nsg.etag());
      assertEquals(nsg.name(), DEFAULT_NSG_NAME);
   }
}

