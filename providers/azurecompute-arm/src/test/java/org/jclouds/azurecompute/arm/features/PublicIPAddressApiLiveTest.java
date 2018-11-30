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

import static org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddress.SKU.SKUName.Basic;
import static org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddress.SKU.SKUName.Standard;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddress.SKU;
import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.util.Predicates2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

@Test(groups = "live", singleThreaded = true)
public class PublicIPAddressApiLiveTest extends BaseAzureComputeApiLiveTest {

   private final String publicIpAddressName = "myipaddress";
   private final String publicIpAddressNameStandard = "myipaddressStandard";
   private String subscriptionid;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      subscriptionid = getSubscriptionId();
   }

   @Test(groups = "live")
   public void deletePublicIPAddressResourceDoesNotExist() {
      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourceGroupName);
      boolean status = ipApi.delete(publicIpAddressName);
      assertFalse(status);
   }

   @Test(groups = "live", dependsOnMethods = "deletePublicIPAddressResourceDoesNotExist")
   public void createPublicIPAddress() {

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourceGroupName);

      final Map<String, String> tags = ImmutableMap.of("testkey", "testvalue");

      PublicIPAddressProperties properties =
              PublicIPAddressProperties.builder()
                  .publicIPAllocationMethod("Static")
                  .idleTimeoutInMinutes(4)
                  .build();

      PublicIPAddress ip = ipApi.createOrUpdate(publicIpAddressName, LOCATION, tags, null, properties);

      assertNotNull(ip);
      assertEquals(ip.name(), publicIpAddressName);
      assertEquals(ip.location(), LOCATION);
      assertEquals(ip.id(), String.format("/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/publicIPAddresses/%s", subscriptionid, resourceGroupName, publicIpAddressName));
      assertEquals(ip.tags().get("testkey"), "testvalue");
      assertNotNull(ip.properties());
      assertEquals(ip.properties().provisioningState(), "Updating");
      assertNull(ip.properties().ipAddress()); // as we don't get IP address until Succeeded state
      assertEquals(ip.properties().publicIPAllocationMethod(), "Static");
      assertEquals(ip.properties().idleTimeoutInMinutes().intValue(), 4);
      assertEquals(ip.sku().name(), Basic);
   }

   @Test(groups = "live", dependsOnMethods = "deletePublicIPAddressResourceDoesNotExist")
   public void createPublicIPAddressStandard() {
      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourceGroupName);

      final Map<String, String> tags = ImmutableMap.of("testkey", "testvalue");

      PublicIPAddressProperties properties = PublicIPAddressProperties.builder().publicIPAllocationMethod("Static")
            .idleTimeoutInMinutes(4).build();

      PublicIPAddress ip = ipApi
            .createOrUpdate(publicIpAddressNameStandard, LOCATION, tags, SKU.create(Standard), properties);

      assertNotNull(ip);
      assertEquals(ip.name(), publicIpAddressNameStandard);
      assertEquals(ip.location(), LOCATION);
      assertEquals(ip.id(),
            String.format("/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/publicIPAddresses/%s",
                  subscriptionid, resourceGroupName, publicIpAddressNameStandard));
      assertEquals(ip.tags().get("testkey"), "testvalue");
      assertNotNull(ip.properties());
      assertEquals(ip.properties().provisioningState(), "Updating");
      assertNull(ip.properties().ipAddress()); // as we don't get IP address until Succeeded state
      assertEquals(ip.properties().publicIPAllocationMethod(), "Static");
      assertEquals(ip.properties().idleTimeoutInMinutes().intValue(), 4);
      assertEquals(ip.sku().name(), Standard);
   }

   @Test(groups = "live", dependsOnMethods = "createPublicIPAddress")
   public void getPublicIPAddress() {

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourceGroupName);

      //Poll until resource is ready to be used
      boolean jobDone = Predicates2.retry(new Predicate<String>() {
         @Override public boolean apply(String name) {
            return ipApi.get(name).properties().provisioningState().equals("Succeeded");
         }
      }, 10 * 1000).apply(publicIpAddressName);
      assertTrue(jobDone, "get operation did not complete in the configured timeout");

      PublicIPAddress ip = ipApi.get(publicIpAddressName);
      assertNotNull(ip);
      assertEquals(ip.name(), publicIpAddressName);
      assertEquals(ip.location(), LOCATION);
      assertEquals(ip.id(), String.format("/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/publicIPAddresses/%s", subscriptionid, resourceGroupName, publicIpAddressName));
      assertEquals(ip.tags().get("testkey"), "testvalue");
      assertNotNull(ip.properties());
      assertEquals(ip.properties().provisioningState(), "Succeeded");
      assertNotNull(ip.properties().ipAddress()); // by this time we should have IP address already
      assertEquals(ip.properties().publicIPAllocationMethod(), "Static");
      assertEquals(ip.properties().idleTimeoutInMinutes().intValue(), 4);
   }

   @Test(groups = "live", dependsOnMethods = "createPublicIPAddress")
   public void listPublicIPAddresses() {

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourceGroupName);

      List<PublicIPAddress> ipList = ipApi.list();

      assertTrue(ipList.size() > 0);
   }

   @Test(groups = "live", dependsOnMethods = "createPublicIPAddress")
   public void listAllPublicIPAddressesInSubscription() {

      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(null);

      List<PublicIPAddress> ipList = ipApi.listAllInSubscription();

      assertTrue(ipList.size() > 0);
   }

   @Test(groups = "live", dependsOnMethods = { "listPublicIPAddresses", "listAllPublicIPAddressesInSubscription",
         "getPublicIPAddress" }, alwaysRun = true)
   public void deletePublicIPAddress() {
      final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(resourceGroupName);
      boolean status = ipApi.delete(publicIpAddressName);
      assertTrue(status);
   }

}
