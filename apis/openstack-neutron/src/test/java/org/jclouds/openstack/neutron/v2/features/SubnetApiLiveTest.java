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
package org.jclouds.openstack.neutron.v2.features;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jclouds.openstack.neutron.v2.domain.AllocationPool;
import org.jclouds.openstack.neutron.v2.domain.HostRoute;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.NetworkType;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiLiveTest;
import org.jclouds.openstack.neutron.v2.util.PredicateUtil;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests subnet api in combination with the network api
 */
@Test(groups = "live", testName = "SubnetApiLiveTest")
public class SubnetApiLiveTest extends BaseNeutronApiLiveTest {

   public void testCreateUpdateAndDeleteSubnet() {
      for (String region : api.getConfiguredRegions()) {
         NetworkApi networkApi = api.getNetworkApi(region);
         String networkId = networkApi.create(Network.createBuilder("jclouds-live-test").networkType(NetworkType.LOCAL).build()).getId();

         SubnetApi subnetApi = api.getSubnetApi(region);
         ImmutableSet<AllocationPool> allocationPools = ImmutableSet.of(
            AllocationPool.builder().start("a3:bc00::10").end("a3:bc00::20").build(),
            AllocationPool.builder().start("a3:bc00::50").end("a3:bc00::90").build()
         );
         ImmutableSet<HostRoute> hostRoutes = ImmutableSet.of(
            HostRoute.builder().destinationCidr("a3:bc00::/48").nextHop("a3:bc00::0004").build()
         );
         Subnet subnet = subnetApi.create(Subnet.createBuilder(networkId, "a3:bc00::/48").ipVersion(6).allocationPools(allocationPools).hostRoutes(hostRoutes).build());
         assertNotNull(subnet);

         /* Test list and get */
         Subnet subnetList = api.getSubnetApi(region).list().concat().toSet().iterator().next();
         assertNotNull(subnetList);
         Subnet subnetGet = api.getSubnetApi(region).get(subnetList.getId());
         assertEquals(subnetList, subnetGet);
         /***/

         Subnet retrievedSubnet = subnetApi.get(subnet.getId());

         assertEquals(retrievedSubnet.getId(), subnet.getId());
         assertEquals(retrievedSubnet.getCidr(), "a3:bc00::/48");
         assertTrue(retrievedSubnet.getDnsNameservers().isEmpty());
         assertEquals(retrievedSubnet.getAllocationPools().size(), 2);
         assertEquals(retrievedSubnet.getHostRoutes().size(), 1);
         assertNotNull(subnetApi.update(retrievedSubnet.getId(), Subnet.updateBuilder().name("jclouds-live-test-update").build()));

         retrievedSubnet = subnetApi.get(retrievedSubnet.getId());

         assertEquals(retrievedSubnet.getId(), subnet.getId());
         assertEquals(retrievedSubnet.getName(), "jclouds-live-test-update");
         assertTrue(retrievedSubnet.getDnsNameservers().isEmpty());

         Subnet subnet2 = subnetApi.create(Subnet.createBuilder(networkId, "a3:bd01::/48").ipVersion(6).build());
         assertNotNull(subnet2);

         assertTrue(subnetApi.delete(subnet.getId()));
         assertTrue(subnetApi.delete(subnet2.getId()));
         assertTrue(networkApi.delete(networkId));
      }
   }

   public void testBulkCreateSubnet() {
      for (String region : api.getConfiguredRegions()) {
         NetworkApi networkApi = api.getNetworkApi(region);
         String networkId = networkApi.create(Network.createBuilder("jclouds-live-test").networkType(NetworkType.LOCAL).build()).getId();

         SubnetApi subnetApi = api.getSubnetApi(region);
         Set<? extends Subnet> subnets = subnetApi.createBulk(
               ImmutableList.of(
                  Subnet.createBuilder("jclouds-live-test-1", "a3:bd01::/48").ipVersion(6).networkId(networkId).build(),
                  Subnet.createBuilder("jclouds-live-test-2", "a3:bd02::/48").ipVersion(6).networkId(networkId).build(),
                  Subnet.createBuilder("jclouds-live-test-3", "a3:bd03::/48").ipVersion(6).networkId(networkId).build()
               )
         ).toSet();
         Set<Subnet> existingSubnets = subnetApi.list().concat().toSet();

         assertNotNull(subnets);
         assertTrue(!subnets.isEmpty());
         assertEquals(subnets.size(), 3);

         for (Subnet net : subnets) {
            Predicate<Subnet> idEqualsPredicate = PredicateUtil.createIdEqualsPredicate(net.getId());
            assertEquals(1, Sets.filter(existingSubnets, idEqualsPredicate).size());
            assertTrue(subnetApi.delete(net.getId()));
         }
         assertTrue(networkApi.delete(networkId));
      }
   }
}
