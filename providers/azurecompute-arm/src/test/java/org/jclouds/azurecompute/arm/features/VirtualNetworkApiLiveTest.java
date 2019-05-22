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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.AddressSpace;
import org.jclouds.azurecompute.arm.domain.FrontendIPConfigurations;
import org.jclouds.azurecompute.arm.domain.FrontendIPConfigurationsProperties;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpAddressAvailabilityResult;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.Subnet.SubnetProperties;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.domain.loadbalancer.LoadBalancer;
import org.jclouds.azurecompute.arm.domain.loadbalancer.LoadBalancerProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

@Test(groups = "live", singleThreaded = true)
public class VirtualNetworkApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String TEST_VIRTUALNETWORK_ADDRESS_PREFIX = "10.20.0.0/16";
   private static final String TEST_IP_ADDRESS_AVAILABLE = "10.20.0.15";
   private static final String TEST_IP_ADDRESS_USED_IN_PROVIDER = "10.20.0.7";

   private String virtualNetworkName;
   private VirtualNetwork vn;

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

      Subnet subnet = Subnet.builder().name("subnetName")
            .properties(SubnetProperties.builder().addressPrefix(TEST_VIRTUALNETWORK_ADDRESS_PREFIX).build()).build();

      final VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties = VirtualNetwork.VirtualNetworkProperties
            .builder().subnets(ImmutableList.<Subnet> of(subnet))
            .addressSpace(AddressSpace.create(Arrays.asList(TEST_VIRTUALNETWORK_ADDRESS_PREFIX))).build();

      vn = api().createOrUpdate(virtualNetworkName, LOCATION, null, virtualNetworkProperties);

      networkAvailablePredicate.create(resourceGroupName).apply(virtualNetworkName);

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

      assertNotNull(vnList);
      assertTrue(vnList.size() > 0);

      assertTrue(any(vnList, new Predicate<VirtualNetwork>() {
         @Override
         public boolean apply(VirtualNetwork input) {
            return vn.name().equals(input.name());
         }
      }));
   }

   @Test(dependsOnMethods = "createVirtualNetwork")
   public void listAllVirtualNetworks() {
      List<VirtualNetwork> vnList = api.getVirtualNetworkApi(null).listAll();

      assertNotNull(vnList);
      assertTrue(vnList.size() > 0);

      assertTrue(any(vnList, new Predicate<VirtualNetwork>() {
         @Override
         public boolean apply(VirtualNetwork input) {
            return vn.name().equals(input.name());
         }
      }));
   }

   @Test(dependsOnMethods = "getVirtualNetwork")
   public void checkIpAvailability() {
      final IpAddressAvailabilityResult checkResultAvailable = api()
            .checkIPAddressAvailability(virtualNetworkName, TEST_IP_ADDRESS_AVAILABLE);
      assertTrue(checkResultAvailable.available());
      assertTrue(checkResultAvailable.availableIPAddresses().isEmpty());

      LoadBalancer lbCreated = createLoadBalancerWithPrivateIP(TEST_IP_ADDRESS_USED_IN_PROVIDER);

      final IpAddressAvailabilityResult checkResultUnavailable = api()
            .checkIPAddressAvailability(virtualNetworkName, TEST_IP_ADDRESS_USED_IN_PROVIDER);
      assertFalse(checkResultUnavailable.available());
      assertFalse(checkResultUnavailable.availableIPAddresses().isEmpty());

      deleteLoadBalancer(lbCreated);
   }

   @Test(dependsOnMethods = { "listVirtualNetworks", "listAllVirtualNetworks", "getVirtualNetwork", "checkIpAvailability" }, alwaysRun = true)
   public void deleteVirtualNetwork() {
      boolean status = api().delete(virtualNetworkName);
      assertTrue(status);
   }

   private VirtualNetworkApi api() {
      return api.getVirtualNetworkApi(resourceGroupName);
   }

   private LoadBalancerApi lbApi() {
      return api.getLoadBalancerApi(resourceGroupName);
   }

   private LoadBalancer createLoadBalancerWithPrivateIP(final String ipAddress) {

      FrontendIPConfigurationsProperties frontendProps = FrontendIPConfigurationsProperties.builder()
            .privateIPAddress(ipAddress).privateIPAllocationMethod("Static")
            .subnet(IdReference.create(api().get(virtualNetworkName).properties().subnets().get(0).id())).build();
      FrontendIPConfigurations frontendIps = FrontendIPConfigurations.create("ipConfigs", null, frontendProps, null);
      LoadBalancerProperties props = LoadBalancerProperties.builder()
            .frontendIPConfigurations(ImmutableList.of(frontendIps)).build();

      LoadBalancer lbCreated = lbApi().createOrUpdate("lbName", LOCATION, null, null, props);
      assertNotNull(lbCreated);
      return lbCreated;
   }

   private void deleteLoadBalancer(LoadBalancer lbCreated) {
      URI lbDeletedURI = lbApi().delete(lbCreated.name());
      assertResourceDeleted(lbDeletedURI);
   }
}
