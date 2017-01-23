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

import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "NetworkInterfaceCardApiMockTest", singleThreaded = true)
public class NetworkInterfaceCardApiMockTest extends BaseAzureComputeApiMockTest {

   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String apiVersion = "api-version=2015-06-15";
   private final String location = "northeurope";
   private final String nicName = "myNic";

   public void getNetworkInterfaceCard() throws InterruptedException {
      server.enqueue(jsonResponse("/getnetworkinterfacecard.json"));

      final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);
      NetworkInterfaceCard nic = nicApi.get(nicName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces/%s?%s", subscriptionid, resourcegroup, nicName, apiVersion);
      assertSent(server, "GET", path);
      assertNotNull(nic);
      assertEquals(nic.name(), nicName);
      assertEquals(nic.properties().ipConfigurations().get(0).name(), "myip1");
      assertEquals(nic.tags().get("mycustomtag"), "foobar");
   }

   public void getNetworkInterfaceCardEmpty() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);

      assertNull(nicApi.get(nicName));

      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourcegroups/myresourcegroup/providers/Microsoft.Network/networkInterfaces/myNic?api-version=2015-06-15");
   }

   public void listNetworkInterfaceCards() throws InterruptedException {
      server.enqueue(jsonResponse("/listnetworkinterfaces.json"));

      final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);
      List<NetworkInterfaceCard> nicList = nicApi.list();
      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces?%s", subscriptionid, resourcegroup, apiVersion);

      assertSent(server, "GET", path);
      assertTrue(nicList.size() == 2);
      assertTrue(nicList.get(0).properties().ipConfigurations().size() > 0);
      assertEquals(nicList.get(0).properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Dynamic");
      assertTrue(nicList.get(1).properties().ipConfigurations().size() > 0);
      assertEquals(nicList.get(1).properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Static");
   }

   public void listNetworkInterfaceCardsEmpty() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);

      assertTrue(nicApi.list().isEmpty());
      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces?%s", subscriptionid, resourcegroup, apiVersion);

      assertSent(server, "GET", path);
   }

   public void createNetworkInterfaceCard() throws InterruptedException {

      server.enqueue(jsonResponse("/createnetworkinterfacecard.json").setStatus("HTTP/1.1 201 Created"));

      final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);


      final String SubnetID = "/subscriptions/" + subscriptionid + "/resourceGroups/azurearmtesting/providers/Microsoft.Network/virtualNetworks/myvirtualnetwork/subnets/mysubnet";
      //Create properties object
      final NetworkInterfaceCardProperties networkInterfaceCardProperties = NetworkInterfaceCardProperties.create(null,
            null, null, Arrays.asList(IpConfiguration.create("myipconfig", null, null, null, IpConfigurationProperties
                  .create(null, null, "Dynamic", IdReference.create(SubnetID), null, null, null))), null);

      final Map<String, String> tags = ImmutableMap.of("mycustomtag", "foobar");

      NetworkInterfaceCard nic = nicApi.createOrUpdate(nicName, location, networkInterfaceCardProperties, tags);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces/%s?%s", subscriptionid, resourcegroup, nicName, apiVersion);
      String json = String.format("{ \"location\":\"%s\", \"tags\": { \"mycustomtag\": \"foobar\" }, \"properties\":{ \"ipConfigurations\":[ { \"name\":\"%s\", \"properties\":{ \"subnet\":{ \"id\": \"%s\" }, \"privateIPAllocationMethod\":\"%s\" } } ] } }", location, "myipconfig", SubnetID, "Dynamic");
      assertSent(server, "PUT", path, json);
      assertEquals(nic.tags().get("mycustomtag"), "foobar");
   }

   public void deleteNetworkInterfaceCard() throws InterruptedException {

      server.enqueue(response202WithHeader());

      final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);

      nicApi.delete(nicName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces/%s?%s", subscriptionid, resourcegroup, nicName, apiVersion);
      assertSent(server, "DELETE", path);

   }

   public void deleteNetworkInterfaceCardResourceDoesNotExist() throws InterruptedException {

      server.enqueue(response404());

      final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);

      nicApi.delete(nicName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces/%s?%s", subscriptionid, resourcegroup, nicName, apiVersion);
      assertSent(server, "DELETE", path);
   }
}
