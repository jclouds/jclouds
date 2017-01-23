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

import java.util.Arrays;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", singleThreaded = true)
public class VirtualNetworkApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String virtualNetworkName;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      virtualNetworkName = String.format("vn-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));
   }

   @Test
   public void deleteVirtualNetworkResourceDoesNotExist() {
      boolean status = api().delete(virtualNetworkName);
      assertFalse(status);
   }

   @Test(dependsOnMethods = "deleteVirtualNetworkResourceDoesNotExist")
   public void createVirtualNetwork() {

      final VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties =
              VirtualNetwork.VirtualNetworkProperties.builder().addressSpace(
                      VirtualNetwork.AddressSpace.create(Arrays.asList(DEFAULT_VIRTUALNETWORK_ADDRESS_PREFIX))).build();

      VirtualNetwork vn = api().createOrUpdate(virtualNetworkName, LOCATION, virtualNetworkProperties);

      assertEquals(vn.name(), virtualNetworkName);
      assertEquals(vn.location(), LOCATION);
   }

   @Test(dependsOnMethods = "createVirtualNetwork")
   public void getVirtualNetwork() {
      VirtualNetwork vn = api().get(virtualNetworkName);

      assertNotNull(vn.name());
      assertNotNull(vn.location());
      assertNotNull(vn.properties().addressSpace().addressPrefixes());
   }

   @Test(dependsOnMethods = "createVirtualNetwork")
   public void listVirtualNetworks() {
      List<VirtualNetwork> vnList = api().list();
      assertTrue(vnList.size() > 0);
   }

   @Test(dependsOnMethods = {"listVirtualNetworks", "getVirtualNetwork"}, alwaysRun = true)
   public void deleteVirtualNetwork() {
      boolean status = api().delete(virtualNetworkName);
      assertTrue(status);
   }

   private VirtualNetworkApi api() {
      return api.getVirtualNetworkApi(resourceGroupName);
   }

}
