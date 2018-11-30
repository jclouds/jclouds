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
import static java.util.Collections.singletonList;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayType.Vpn;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Collections;

import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpAllocationMethod;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.domain.vpn.SKU;
import org.jclouds.azurecompute.arm.domain.vpn.SKU.SKUName;
import org.jclouds.azurecompute.arm.domain.vpn.SKU.SKUTier;
import org.jclouds.azurecompute.arm.domain.vpn.VPNType;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGateway;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayProperties;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayProperties.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayProperties.IpConfiguration.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

@Test(groups = "live", testName = "VirtualNetworkGatewayApiLiveTest", singleThreaded = true)
public class VirtualNetworkGatewayApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String name;
   private String subnetId;
   private PublicIPAddress publicIp;
   private Predicate<String> virtualNetworkGatewayAvailable;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      name = "jclouds-" + RAND;
      virtualNetworkGatewayAvailable = virtualNetworkGatewayStatus.create(resourceGroupName);

      VirtualNetwork vnet = createDefaultVirtualNetwork(resourceGroupName, name + "-net", "10.2.0.0/16", LOCATION);
      Subnet subnet = createDefaultSubnet(resourceGroupName, Subnet.GATEWAY_SUBNET_NAME, vnet.name(), "10.2.0.0/23");
      subnetId = subnet.id();

      PublicIPAddressProperties props = PublicIPAddressProperties.builder()
            .publicIPAllocationMethod(IpAllocationMethod.Dynamic.name()).idleTimeoutInMinutes(4).build();
      publicIp = api.getPublicIPAddressApi(resourceGroupName).createOrUpdate(name + "-publicip", LOCATION,
            Collections.<String, String> emptyMap(), null, props);
   }

   @Test
   public void createVirtualNetworkGateway() {
      IpConfigurationProperties ipprops = IpConfigurationProperties.builder(IpAllocationMethod.Dynamic)
            .subnet(IdReference.create(subnetId)).publicIPAddress(IdReference.create(publicIp.id())).build();
      IpConfiguration ipconf = IpConfiguration.create(null, name + "-ipconf", null, ipprops);

      VirtualNetworkGatewayProperties props = VirtualNetworkGatewayProperties
            .builder(false, Vpn, SKU.create(1, SKUName.Basic, SKUTier.Basic)).vpnType(VPNType.PolicyBased)
            .ipConfigurations(singletonList(ipconf)).build();

      getAnonymousLogger().info(String.format("Creating virtual network gateway %s. This may take a while...", name));
      VirtualNetworkGateway gw = api().createOrUpdate(name, LOCATION, null, props);

      assertNotNull(gw);
      assertEquals(gw.name(), name);
      assertNotNull(gw.properties());
   }

   @Test(dependsOnMethods = "createVirtualNetworkGateway")
   public void getVirtualNetworkGateway() {
      assertNotNull(api().get(name));
   }

   @Test(dependsOnMethods = "createVirtualNetworkGateway")
   public void listVirtualNetworkGateways() {
      assertTrue(any(api().list(), new Predicate<VirtualNetworkGateway>() {
         @Override
         public boolean apply(VirtualNetworkGateway input) {
            return name.equals(input.name());
         }
      }));
   }

   @Test(dependsOnMethods = "createVirtualNetworkGateway")
   public void updateVirtualNetworkGateway() {
      // Make sure the resource is fully provisioned before modifying it
      assertTrue(virtualNetworkGatewayAvailable.apply(name));

      VirtualNetworkGateway gw = api().get(name);
      gw = api().createOrUpdate(name, LOCATION, ImmutableMap.of("foo", "bar"), gw.properties());

      assertNotNull(gw);
      assertTrue(gw.tags().containsKey("foo"));
      assertEquals(gw.tags().get("foo"), "bar");
   }

   @Test(dependsOnMethods = { "getVirtualNetworkGateway", "listVirtualNetworkGateways", "updateVirtualNetworkGateway" })
   public void deleteVirtualNetworkGateway() {
      // Make sure the resource is fully provisioned before deleting it
      assertTrue(virtualNetworkGatewayAvailable.apply(name));
      URI uri = api().delete(name);
      assertResourceDeleted(uri);
   }

   private VirtualNetworkGatewayApi api() {
      return api.getVirtualNetworkGatewayApi(resourceGroupName);
   }
}
