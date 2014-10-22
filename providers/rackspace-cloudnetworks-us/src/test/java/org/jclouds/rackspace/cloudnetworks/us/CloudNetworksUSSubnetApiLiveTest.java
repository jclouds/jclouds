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
package org.jclouds.rackspace.cloudnetworks.us;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.neutron.v2.domain.AllocationPool;
import org.jclouds.openstack.neutron.v2.domain.HostRoute;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.SubnetApi;
import org.jclouds.openstack.neutron.v2.features.SubnetApiLiveTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "live", testName = "CloudNetworksUSSubnetApiLiveTest", singleThreaded = true)
public class CloudNetworksUSSubnetApiLiveTest extends SubnetApiLiveTest {
   public CloudNetworksUSSubnetApiLiveTest() {
      provider = "rackspace-cloudnetworks-us";
   }

   @Override
   public void testCreateUpdateAndDeleteSubnet() {
      for (String region : api.getConfiguredRegions()) {
         NetworkApi networkApi = api.getNetworkApi(region);
         String networkId = networkApi.create(
               Network.createBuilder("jclouds-live-test").build()).getId();

         SubnetApi subnetApi = api.getSubnetApi(region);
         ImmutableSet<AllocationPool> allocationPools = ImmutableSet.of(
               AllocationPool.builder().start("192.168.100.0").end("192.168.100.2").build()
         );
         ImmutableSet<HostRoute> hostRoutes = ImmutableSet.of(
               HostRoute.builder().destinationCidr("192.168.100.0/30").nextHop("192.168.100.4").build()
         );
         Subnet subnet = subnetApi.create(Subnet.createBuilder(networkId, "192.168.100.0/30").ipVersion(4).allocationPools(allocationPools).hostRoutes(hostRoutes).build());
         assertNotNull(subnet);

         /* Test list and get */
         Subnet subnetList = api.getSubnetApi(region).list().concat().toSet().iterator().next();
         assertNotNull(subnetList);
         Subnet subnetGet = api.getSubnetApi(region).get(subnetList.getId());
         assertEquals(subnetList, subnetGet);
         /***/

         Subnet retrievedSubnet = subnetApi.get(subnet.getId());

         assertEquals(retrievedSubnet.getId(), subnet.getId());
         assertEquals(retrievedSubnet.getCidr(), "192.168.100.0/30");
         assertTrue(retrievedSubnet.getDnsNameservers().isEmpty());
         assertEquals(retrievedSubnet.getAllocationPools().size(), 1);
         assertEquals(retrievedSubnet.getHostRoutes().size(), 1);
         assertNotNull(subnetApi.update(retrievedSubnet.getId(), Subnet.updateBuilder().name("jclouds-live-test-update").build()));

         retrievedSubnet = subnetApi.get(retrievedSubnet.getId());

         assertEquals(retrievedSubnet.getId(), subnet.getId());
         assertEquals(retrievedSubnet.getName(), "jclouds-live-test-update");
         assertTrue(retrievedSubnet.getDnsNameservers().isEmpty());

         assertTrue(subnetApi.delete(subnet.getId()));
         assertTrue(networkApi.delete(networkId));
      }
   }

   @Override
   public void testBulkCreateSubnet() {
      throw new SkipException("unsupported functionality");
   }
}
