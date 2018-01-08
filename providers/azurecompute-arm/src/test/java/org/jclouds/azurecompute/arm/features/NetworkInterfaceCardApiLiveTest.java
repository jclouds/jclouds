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
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

@Test(groups = "live", singleThreaded = true)
public class NetworkInterfaceCardApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String subnetId;
   private String nicName;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      assertNotNull(api.getResourceGroupApi().create(resourceGroupName, LOCATION, ImmutableMap.<String, String>of()));
      String virtualNetworkName = String.format("vn-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));
      nicName = String.format("nic-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));
      String subnetName = String.format("s-%s-%s", this.getClass().getSimpleName().toLowerCase(), System.getProperty("user.name"));

      //Subnets belong to a virtual network so that needs to be created first
      assertNotNull(createDefaultVirtualNetwork(resourceGroupName, virtualNetworkName, "10.2.0.0/16", LOCATION));

      //Subnet needs to be up & running before NIC can be created
      Subnet subnet = createDefaultSubnet(resourceGroupName, subnetName, virtualNetworkName, "10.2.0.0/23");
      assertNotNull(subnet);
      assertNotNull(subnet.id());
      subnetId = subnet.id();
   }

   @Test
   public void createNetworkInterfaceCard() {
      //Create properties object
      final NetworkInterfaceCardProperties networkInterfaceCardProperties =
              NetworkInterfaceCardProperties.builder().ipConfigurations(
                      Arrays.asList(IpConfiguration.builder()
                              .name("myipconfig")
                              .properties(IpConfigurationProperties.builder()
                                      .privateIPAllocationMethod("Dynamic")
                                      .subnet(IdReference.create(subnetId)).build()
                              ).build()
                      )).build();

      final Map<String, String> tags = ImmutableMap.of("jclouds", "livetest");
      NetworkInterfaceCard nic = api().createOrUpdate(nicName, LOCATION, networkInterfaceCardProperties, tags);

      assertEquals(nic.name(), nicName);
      assertEquals(nic.location(), LOCATION);
      assertTrue(nic.properties().ipConfigurations().size() > 0);
      assertEquals(nic.properties().ipConfigurations().get(0).name(), "myipconfig");
      assertEquals(nic.properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Dynamic");
      assertEquals(nic.properties().ipConfigurations().get(0).properties().subnet().id(), subnetId);
      assertEquals(nic.tags().get("jclouds"), "livetest");
   }

   @Test(dependsOnMethods = "createNetworkInterfaceCard")
   public void getNetworkInterfaceCard() {
      NetworkInterfaceCard nic = api().get(nicName);

      assertEquals(nic.name(), nicName);
      assertEquals(nic.location(), LOCATION);
      assertTrue(nic.properties().ipConfigurations().size() > 0);
      assertEquals(nic.properties().ipConfigurations().get(0).name(), "myipconfig");
      assertEquals(nic.properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Dynamic");
      assertEquals(nic.properties().ipConfigurations().get(0).properties().subnet().id(), subnetId);
   }

   @Test(dependsOnMethods = "createNetworkInterfaceCard")
   public void listNetworkInterfaceCards() {
      List<NetworkInterfaceCard> nicList = api().list();
      assertTrue(nicList.contains(api().get(nicName)));
   }

   @Test(dependsOnMethods = {"listNetworkInterfaceCards", "getNetworkInterfaceCard"})
   public void deleteNetworkInterfaceCard() {
      URI uri = api().delete(nicName);
      assertResourceDeleted(uri);
   }

   private NetworkInterfaceCardApi api() {
      return api.getNetworkInterfaceCardApi(resourceGroupName);
   }

}
