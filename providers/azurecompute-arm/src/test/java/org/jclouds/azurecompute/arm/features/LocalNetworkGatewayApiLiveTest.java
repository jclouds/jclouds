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

import static com.google.common.collect.Iterables.any;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.azurecompute.arm.domain.AddressSpace;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.domain.vpn.LocalNetworkGateway;
import org.jclouds.azurecompute.arm.domain.vpn.LocalNetworkGatewayProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "live", testName = "LocalNetworkGatewayApiLiveTest", singleThreaded = true)
public class LocalNetworkGatewayApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String name;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      name = "jclouds-" + RAND;
   }

   @Test
   public void createLocalNetworkGateway() {
      AddressSpace localAddresses = AddressSpace.create(ImmutableList.of("192.168.0.0/24"));
      LocalNetworkGatewayProperties props = LocalNetworkGatewayProperties.builder("1.2.3.4")
            .localNetworkAddressSpace(localAddresses).build();

      LocalNetworkGateway gw = api().createOrUpdate(name, LOCATION, null, props);

      assertNotNull(gw);
      assertEquals(gw.name(), name);
      assertNotNull(gw.properties());
      assertNotNull(gw.properties().gatewayIpAddress());
      assertEquals(gw.properties().gatewayIpAddress(), "1.2.3.4");
      assertNotNull(gw.properties().localNetworkAddressSpace());
      assertTrue(gw.properties().localNetworkAddressSpace().addressPrefixes().contains("192.168.0.0/24"));
   }

   @Test(dependsOnMethods = "createLocalNetworkGateway")
   public void getLocalNetworkGateway() {
      assertNotNull(api().get(name));
   }

   @Test(dependsOnMethods = "createLocalNetworkGateway")
   public void listLocalNetworkGateways() {
      assertTrue(any(api().list(), new Predicate<LocalNetworkGateway>() {
         @Override
         public boolean apply(LocalNetworkGateway input) {
            return name.equals(input.name());
         }
      }));
   }

   @Test(dependsOnMethods = "createLocalNetworkGateway")
   public void updateLocalNetworkGateway() {
      // Make sure the resource is fully provisioned before modifying it
      waitUntilAvailable(name);
      
      LocalNetworkGateway gw = api().get(name);
      AddressSpace localAddresses = AddressSpace.create(ImmutableList.of("192.168.0.0/24", "192.168.1.0/24"));
      gw = api().createOrUpdate(name, LOCATION, ImmutableMap.of("foo", "bar"),
            gw.properties().toBuilder().localNetworkAddressSpace(localAddresses).build());

      assertNotNull(gw);
      assertTrue(gw.tags().containsKey("foo"));
      assertEquals(gw.tags().get("foo"), "bar");
      assertTrue(gw.properties().localNetworkAddressSpace().addressPrefixes().contains("192.168.0.0/24"));
      assertTrue(gw.properties().localNetworkAddressSpace().addressPrefixes().contains("192.168.1.0/24"));
   }

   @Test(dependsOnMethods = { "getLocalNetworkGateway", "listLocalNetworkGateways", "updateLocalNetworkGateway" })
   public void deleteLocalNetworkGateway() {
      // Make sure the resource is fully provisioned before deleting it
      waitUntilAvailable(name);
      URI uri = api().delete(name);
      assertResourceDeleted(uri);
   }

   private LocalNetworkGatewayApi api() {
      return api.getLocalNetworkGatewayApi(resourceGroupName);
   }

   private void waitUntilAvailable(final String name) {
      assertTrue(resourceAvailable.apply(new Supplier<Provisionable>() {
         @Override
         public Provisionable get() {
            LocalNetworkGateway gw = api().get(name);
            return gw == null ? null : gw.properties();
         }
      }));
   }
}
