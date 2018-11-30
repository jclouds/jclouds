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
import static org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnection.Type.IPsec;
import static org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayType.Vpn;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Collections;

import org.jclouds.azurecompute.arm.domain.AddressSpace;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpAllocationMethod;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.domain.vpn.LocalNetworkGateway;
import org.jclouds.azurecompute.arm.domain.vpn.LocalNetworkGatewayProperties;
import org.jclouds.azurecompute.arm.domain.vpn.SKU;
import org.jclouds.azurecompute.arm.domain.vpn.SKU.SKUName;
import org.jclouds.azurecompute.arm.domain.vpn.SKU.SKUTier;
import org.jclouds.azurecompute.arm.domain.vpn.VPNType;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGateway;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnection;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayConnectionProperties;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayProperties;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayProperties.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGatewayProperties.IpConfiguration.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "live", testName = "VirtualNetworkGatewayConnectionApiLiveTest", singleThreaded = true)
public class VirtualNetworkGatewayConnectionApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String name;
   private LocalNetworkGateway localGateway;
   private VirtualNetworkGateway virtualGateway;

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      createTestResourceGroup();
      name = "jclouds-" + RAND;
      setupLocalGateway();
      setupVirtualGateway();
   }

   private void setupLocalGateway() {
      AddressSpace localAddresses = AddressSpace.create(ImmutableList.of("192.168.0.0/24"));
      LocalNetworkGatewayProperties props = LocalNetworkGatewayProperties.builder("1.2.3.4")
            .localNetworkAddressSpace(localAddresses).build();

      final LocalNetworkGatewayApi lgwApi = api.getLocalNetworkGatewayApi(resourceGroupName);
      localGateway = lgwApi.createOrUpdate(name + "-lgw", LOCATION, null, props);

      assertTrue(resourceAvailable.apply(new Supplier<Provisionable>() {
         @Override
         public Provisionable get() {
            LocalNetworkGateway gw = lgwApi.get(localGateway.name());
            return gw == null ? null : gw.properties();
         }
      }));
   }

   private void setupVirtualGateway() {
      VirtualNetwork vnet = createDefaultVirtualNetwork(resourceGroupName, name + "-net", "10.2.0.0/16", LOCATION);
      Subnet subnet = createDefaultSubnet(resourceGroupName, Subnet.GATEWAY_SUBNET_NAME, vnet.name(), "10.2.0.0/23");

      PublicIPAddressProperties props = PublicIPAddressProperties.builder()
            .publicIPAllocationMethod(IpAllocationMethod.Dynamic.name()).idleTimeoutInMinutes(4).build();
      PublicIPAddress publicIp = api.getPublicIPAddressApi(resourceGroupName).createOrUpdate(name + "-publicip",
            LOCATION, Collections.<String, String> emptyMap(), null, props);

      IpConfigurationProperties ipprops = IpConfigurationProperties.builder(IpAllocationMethod.Dynamic)
            .subnet(IdReference.create(subnet.id())).publicIPAddress(IdReference.create(publicIp.id())).build();
      IpConfiguration ipconf = IpConfiguration.create(null, name + "-ipconf", null, ipprops);

      VirtualNetworkGatewayProperties gwProps = VirtualNetworkGatewayProperties
            .builder(false, Vpn, SKU.create(1, SKUName.Basic, SKUTier.Basic)).vpnType(VPNType.PolicyBased)
            .ipConfigurations(singletonList(ipconf)).build();

      getAnonymousLogger().info(
            String.format("Creating virtual network gateway %s-vgw. This may take a while...", name));
      virtualGateway = api.getVirtualNetworkGatewayApi(resourceGroupName).createOrUpdate(name + "-vgw", LOCATION, null,
            gwProps);
      assertTrue(virtualNetworkGatewayStatus.create(resourceGroupName).apply(virtualGateway.name()));
   }

   @Test
   public void createVirtualNetworkGatewayConnection() {
      VirtualNetworkGatewayConnectionProperties props = VirtualNetworkGatewayConnectionProperties
            .builder(IPsec, false, false, IdReference.create(localGateway.id()),
                  IdReference.create(virtualGateway.id())).sharedKey("jcl0uds").build();

      VirtualNetworkGatewayConnection conn = api().createOrUpdate(name, LOCATION, null, props);

      assertNotNull(conn);
      assertEquals(conn.name(), name);
      assertNotNull(conn.properties());
   }

   @Test(dependsOnMethods = "createVirtualNetworkGatewayConnection")
   public void getVirtualNetworkGatewayConnection() {
      assertNotNull(api().get(name));
   }

   @Test(dependsOnMethods = "createVirtualNetworkGatewayConnection")
   public void listVirtualNetworkGatewayConnections() {
      assertTrue(any(api().list(), new Predicate<VirtualNetworkGatewayConnection>() {
         @Override
         public boolean apply(VirtualNetworkGatewayConnection input) {
            return name.equals(input.name());
         }
      }));
   }

   @Test(dependsOnMethods = "createVirtualNetworkGatewayConnection")
   public void updateVirtualNetworkGatewayConnection() {
      // Make sure the resource is fully provisioned before modifying it
      waitUntilAvailable(name);

      VirtualNetworkGatewayConnection conn = api().get(name);
      conn = api().createOrUpdate(name, LOCATION, ImmutableMap.of("foo", "bar"), conn.properties());

      assertNotNull(conn);
      assertTrue(conn.tags().containsKey("foo"));
      assertEquals(conn.tags().get("foo"), "bar");
   }

   @Test(dependsOnMethods = { "getVirtualNetworkGatewayConnection", "listVirtualNetworkGatewayConnections",
         "updateVirtualNetworkGatewayConnection" })
   public void deleteVirtualNetworkGatewayConnection() {
      // Make sure the resource is fully provisioned before deleting it
      waitUntilAvailable(name);
      URI uri = api().delete(name);
      assertResourceDeleted(uri);
   }

   private void waitUntilAvailable(final String name) {
      assertTrue(resourceAvailable.apply(new Supplier<Provisionable>() {
         @Override
         public Provisionable get() {
            VirtualNetworkGatewayConnection gw = api().get(name);
            return gw == null ? null : gw.properties();
         }
      }));
   }

   private VirtualNetworkGatewayConnectionApi api() {
      return api.getVirtualNetworkGatewayConnectionApi(resourceGroupName);
   }
}
