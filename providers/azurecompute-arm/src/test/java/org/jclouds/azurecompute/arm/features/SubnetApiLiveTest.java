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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", singleThreaded = true)
public class SubnetApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String virtualNetworkName;
   private String subnetName;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      virtualNetworkName = String.format("vn-%s-%s", this.getClass().getSimpleName().toLowerCase(),
            System.getProperty("user.name"));
      subnetName = "jclouds-" + RAND;

      // Subnets belong to a virtual network so that needs to be created first
      // VN will be deleted when resource group is deleted
      VirtualNetwork vn = createDefaultVirtualNetwork(resourceGroupName, virtualNetworkName, "10.2.0.0/16", LOCATION);
      assertNotNull(vn);
   }

   @Test
   public void deleteSubnetResourceDoesNotExist() {
      assertFalse(api().delete(subnetName));
   }

   @Test(dependsOnMethods = "deleteSubnetResourceDoesNotExist")
   public void createSubnet() {
      //Create properties object
      //addressPrefix must match Virtual network address space!
      Subnet.SubnetProperties properties = Subnet.SubnetProperties.builder().addressPrefix("10.2.0.0/23").build();

      Subnet subnet = api().createOrUpdate(subnetName, properties);

      assertEquals(subnet.name(), subnetName);
      assertEquals(subnet.properties().addressPrefix(), "10.2.0.0/23");
   }

   @Test(dependsOnMethods = "createSubnet")
   public void getSubnet() {
      Subnet subnet = api().get(subnetName);
      assertNotNull(subnet.name());
      assertNotNull(subnet.properties().addressPrefix());
   }

   @Test(dependsOnMethods = "createSubnet")
   public void listSubnets() {
      List<Subnet> subnets = api().list();
      assertTrue(subnets.size() > 0);
   }

   @Test(dependsOnMethods = {"listSubnets", "getSubnet"}, alwaysRun = true)
   public void deleteSubnet() {
      boolean status = api().delete(subnetName);
      assertTrue(status);
   }

   private SubnetApi api() {
      return api.getSubnetApi(resourceGroupName, virtualNetworkName);
   }

}
