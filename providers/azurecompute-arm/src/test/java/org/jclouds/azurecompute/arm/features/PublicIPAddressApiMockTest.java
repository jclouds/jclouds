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
import org.jclouds.azurecompute.arm.domain.DnsSettings;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;


@Test(groups = "unit", testName = "NetworkInterfaceCardApiMockTest", singleThreaded = true)
public class PublicIPAddressApiMockTest extends BaseAzureComputeApiMockTest {

   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String apiVersion = "api-version=2015-06-15";
   private final String location = "northeurope";
   private final String publicIpName = "mypublicaddress";

   public void getPublicIPAddressInfo() throws InterruptedException {
      server.enqueue(jsonResponse("/PublicIPAddressGetInfo.json"));

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourcegroup);
      PublicIPAddress ip = ipApi.get(publicIpName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses/%s?%s", subscriptionid, resourcegroup, publicIpName, apiVersion);
      assertSent(server, "GET", path);

      assertNotNull(ip);
      assertEquals(ip.name(), "mypublicaddress");
      assertEquals(ip.location(), "northeurope");
      assertEquals(ip.id(), "/subscriptions/fakeb2f5-4710-4e93-bdf4-419edbde2178/resourceGroups/myresourcegroup/providers/Microsoft.Network/publicIPAddresses/mypublicaddress");
      assertEquals(ip.tags().get("testkey"), "testvalue");
      assertNotNull(ip.properties());
      assertEquals(ip.properties().provisioningState(), "Succeeded");
      assertEquals(ip.properties().ipAddress(), "12.123.12.123");
      assertEquals(ip.properties().publicIPAllocationMethod(), "Static");
      assertEquals(ip.properties().idleTimeoutInMinutes().intValue(), 4);
      assertNotNull(ip.properties().dnsSettings());
      assertEquals(ip.properties().dnsSettings().domainNameLabel(), "foobar");
      assertEquals(ip.properties().dnsSettings().fqdn(), "foobar.northeurope.cloudapp.azure.com");
      assertNotNull(ip.properties().ipConfiguration());
      assertEquals(ip.properties().ipConfiguration().id(), "/subscriptions/fakeb2f5-4710-4e93-bdf4-419edbde2178/resourceGroups/myresourcegroup/providers/Microsoft.Network/networkInterfaces/myNic/ipConfigurations/myip1");
   }

   public void getPublicIPAddressInfoEmpty() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourcegroup);
      PublicIPAddress ip = ipApi.get(publicIpName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses/%s?%s", subscriptionid, resourcegroup, publicIpName, apiVersion);
      assertSent(server, "GET", path);

      assertNull(ip);
   }

   public void listPublicIPAddresses() throws InterruptedException {
      server.enqueue(jsonResponse("/PublicIPAddressList.json"));

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourcegroup);
      List<PublicIPAddress> ipList = ipApi.list();

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses?%s", subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);
      assertEquals(ipList.size(), 4);
   }

   public void listPublicIPAddressesEmpty() throws InterruptedException {
      server.enqueue(new MockResponse().setResponseCode(404));

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourcegroup);
      List<PublicIPAddress> ipList = ipApi.list();

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses?%s", subscriptionid, resourcegroup, apiVersion);
      assertSent(server, "GET", path);
      assertEquals(ipList.size(), 0);
   }

   public void createPublicIPAddress() throws InterruptedException {

      server.enqueue(jsonResponse("/PublicIPAddressCreate.json").setStatus("HTTP/1.1 201 Created"));

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourcegroup);

      final Map<String, String> tags = ImmutableMap.of("testkey", "testvalue");

      PublicIPAddressProperties properties = PublicIPAddressProperties.create(null, null, "Static", 4, null,
              DnsSettings.create("foobar", "foobar.northeurope.cloudapp.azure.com", null));

      PublicIPAddress ip = ipApi.createOrUpdate(publicIpName, location, tags, properties);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses/%s?%s", subscriptionid, resourcegroup, publicIpName, apiVersion);
      String json = String.format("{ \"location\": \"%s\", \"tags\": { \"testkey\": \"testvalue\" }, \"properties\": { \"publicIPAllocationMethod\": \"Static\", \"idleTimeoutInMinutes\": 4, \"dnsSettings\": { \"domainNameLabel\": \"foobar\", \"fqdn\": \"foobar.northeurope.cloudapp.azure.com\" } } }", location);
      assertSent(server, "PUT", path, json);

      assertNotNull(ip);
      assertEquals(ip.name(), "mypublicaddress");
      assertEquals(ip.location(), "northeurope");
      assertEquals(ip.id(), "/subscriptions/fakeb2f5-4710-4e93-bdf4-419edbde2178/resourceGroups/myresourcegroup/providers/Microsoft.Network/publicIPAddresses/mypublicaddress");
      assertEquals(ip.tags().get("testkey"), "testvalue");
      assertNotNull(ip.properties());
      assertEquals(ip.properties().provisioningState(), "Updating");
      assertNull(ip.properties().ipAddress()); // as we don't get IP address until Succeeded state
      assertEquals(ip.properties().publicIPAllocationMethod(), "Static");
      assertEquals(ip.properties().idleTimeoutInMinutes().intValue(), 4);
      assertNotNull(ip.properties().dnsSettings());
      assertEquals(ip.properties().dnsSettings().domainNameLabel(), "foobar");
      assertEquals(ip.properties().dnsSettings().fqdn(), "foobar.northeurope.cloudapp.azure.com");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void createPublicIPAddressDnsRecordInUse() throws IllegalArgumentException, InterruptedException {

      server.enqueue(jsonResponse("/PublicIPAddressCreateDnsRecordInUse.json").setStatus("HTTP/1.1 400 Bad Request"));

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourcegroup);

      final Map<String, String> tags = ImmutableMap.of("testkey", "testvalue");

      PublicIPAddressProperties properties = PublicIPAddressProperties.create(null, null, "Static", 4, null,
              DnsSettings.create("foobar", "foobar.northeurope.cloudapp.azure.com", null));

      ipApi.createOrUpdate(publicIpName, location, tags, properties);
   }

   public void deletePublicIPAddress() throws InterruptedException {

      server.enqueue(response202());

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourcegroup);
      boolean status = ipApi.delete(publicIpName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses/%s?%s", subscriptionid, resourcegroup, publicIpName, apiVersion);
      assertSent(server, "DELETE", path);

      assertTrue(status);
   }

   public void deletePublicIPAddressResourceDoesNotExist() throws InterruptedException {

      server.enqueue(response204());

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourcegroup);
      boolean status = ipApi.delete(publicIpName);

      String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses/%s?%s", subscriptionid, resourcegroup, publicIpName, apiVersion);
      assertSent(server, "DELETE", path);

      assertFalse(status);
   }
}
