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
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", singleThreaded = true)
public class NetworkSecurityGroupApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static String nsgName = "testNetworkSecurityGroup";

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      nsgName = String.format("nsg-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));
   }

   @Test
   public void deleteNetworkSecurityGroupDoesNotExist() {
      URI uri = api().delete(nsgName);
      assertNull(uri);
   }

   @Test(dependsOnMethods = "deleteNetworkSecurityGroupDoesNotExist")
   public void createNetworkSecurityGroup() {
      final NetworkSecurityGroup nsg = newNetworkSecurityGroup(nsgName, LOCATION);
      assertNotNull(nsg);

      NetworkSecurityGroup result = api().createOrUpdate(nsgName,
                                                  nsg.location(),
                                                  nsg.tags(),
                                                  nsg.properties());
      assertNotNull(result);
   }

   @Test(dependsOnMethods = "createNetworkSecurityGroup")
   public void listNetworkSecurityGroups() {
      List<NetworkSecurityGroup> result = api().list();

      // verify we have something
      assertNotNull(result);
      assertEquals(result.size(), 1);

      // check that the network security group matches the one we originally passed in
      NetworkSecurityGroup original = newNetworkSecurityGroup(nsgName, LOCATION);
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

   @Test(dependsOnMethods = "createNetworkSecurityGroup")
   public void listAllNetworkSecurityGroups() {
      List<NetworkSecurityGroup> result = api().listAll();

      // verify we have at least the original created SG. We could retrieve here any other SGs in different RGs
      assertNotNull(result);
      assertTrue(result.size() > 1);

      NetworkSecurityGroup original = newNetworkSecurityGroup(nsgName, LOCATION);
      boolean found = false;
      for (NetworkSecurityGroup networkSecurityGroup : result) {
         if (networkSecurityGroup.name().equalsIgnoreCase(original.name())) {
            assertEquals(original.name(), networkSecurityGroup.name());
            assertEquals(original.location(), networkSecurityGroup.location());
            assertEquals(original.tags(), networkSecurityGroup.tags());

            // check the network security rule in the group
            assertEquals(networkSecurityGroup.properties().securityRules().size(), 1);
            NetworkSecurityRule originalRule = original.properties().securityRules().get(0);
            NetworkSecurityRule nsgRule = networkSecurityGroup.properties().securityRules().get(0);
            assertEquals(originalRule.name(), nsgRule.name());
            assertTrue(originalRule.properties().equals(nsgRule.properties()));
            found = true;
            break;
         }
      }

      assertTrue(found, "NSG created in test was not found in subscription");
   }

   @Test(dependsOnMethods = { "listNetworkSecurityGroups", "listAllNetworkSecurityGroups", "getNetworkSecurityGroup" }, alwaysRun = true)
   public void deleteNetworkSecurityGroup() {
      URI uri = api().delete(nsgName);
      assertResourceDeleted(uri);
   }

   @Test(dependsOnMethods = "createNetworkSecurityGroup")
   public void getNetworkSecurityGroup() {
      NetworkSecurityGroup nsg = api().get(nsgName);
      assertNotNull(nsg);
      assertNotNull(nsg.etag());
      assertEquals(nsg.name(), nsgName);
   }

   private NetworkSecurityGroupApi api() {
      return api.getNetworkSecurityGroupApi(resourceGroupName);
   }

}

