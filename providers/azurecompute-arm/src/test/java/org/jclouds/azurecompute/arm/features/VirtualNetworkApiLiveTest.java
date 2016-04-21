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

import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

@Test(groups = "live", singleThreaded = true)
public class VirtualNetworkApiLiveTest extends BaseAzureComputeApiLiveTest {

   private final String subscriptionid = "subscriptionid";
   private String resourcegroup;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      resourcegroup = getResourceGroupName();
   }

   @Test(groups = "live")
   public void deleteVirtualNetworkResourceDoesNotExist() {

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);

      boolean status = vnApi.delete(VIRTUAL_NETWORK_NAME);
      assertFalse(status);

   }

   @Test(groups = "live", dependsOnMethods = "deleteVirtualNetworkResourceDoesNotExist")
   public void createVirtualNetwork() {

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);

      //Create properties object

      final VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties =
              VirtualNetwork.VirtualNetworkProperties.builder().addressSpace(
                      VirtualNetwork.AddressSpace.create(Arrays.asList(DEFAULT_VIRTUALNETWORK_ADDRESS_PREFIX))).build();

      VirtualNetwork vn = vnApi.createOrUpdate(VIRTUAL_NETWORK_NAME, LOCATION, virtualNetworkProperties);

      assertEquals(VIRTUAL_NETWORK_NAME, vn.name());
      assertEquals(LOCATION, vn.location());
   }

   @Test(groups = "live", dependsOnMethods = "createVirtualNetwork")
   public void getVirtualNetwork() {

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);
      VirtualNetwork vn = vnApi.get(VIRTUAL_NETWORK_NAME);

      assertNotNull(vn.name());
      assertNotNull(vn.location());
      assertNotNull(vn.properties().addressSpace().addressPrefixes());
   }

   @Test(groups = "live", dependsOnMethods = "createVirtualNetwork")
   public void listVirtualNetworks() {

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);
      List<VirtualNetwork> vnList = vnApi.list();

      assertTrue(vnList.size() > 0);
   }

   @Test(groups = "live", dependsOnMethods = {"listVirtualNetworks", "getVirtualNetwork"}, alwaysRun = true)
   public void deleteVirtualNetwork() {

      final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(resourcegroup);

      boolean status = vnApi.delete(VIRTUAL_NETWORK_NAME);
      assertTrue(status);
   }

}
