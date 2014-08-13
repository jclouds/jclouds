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
package org.jclouds.openstack.neutron.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.neutron.v2_0.domain.AllocationPool;
import org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet;
import org.jclouds.openstack.neutron.v2_0.domain.HostRoute;
import org.jclouds.openstack.neutron.v2_0.domain.NetworkType;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.domain.Subnet;
import org.jclouds.openstack.neutron.v2_0.internal.BaseNeutronApiLiveTest;
import org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreateSubnetBulkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdateSubnetOptions;
import org.jclouds.openstack.neutron.v2_0.util.PredicateUtil;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Tests subnet api in combination with the network api
 *
 */
@Test(groups = "live", testName = "SubnetApiLiveTest")
public class SubnetApiLiveTest extends BaseNeutronApiLiveTest {

   public void testGetAndListSubnets() {
      for (String zone : api.getConfiguredZones()) {
         Set<? extends ReferenceWithName> references = api.getSubnetApiForZone(zone).list().concat().toSet();
         Set<? extends Subnet> subnets = api.getSubnetApiForZone(zone).listInDetail().concat().toSet();

         assertNotNull(references);
         assertNotNull(subnets);
         assertEquals(references.size(), subnets.size());

         for (Subnet subnet : subnets) {
            assertNotNull(subnet.getNetworkId());
            assertTrue(references.contains(ReferenceWithName.builder().id(subnet.getId()).tenantId(subnet.getTenantId()).name(subnet.getName()).build()));

            Subnet retrievedSubnet = api.getSubnetApiForZone(zone).get(subnet.getId());
            assertEquals(retrievedSubnet, subnet);
         }
      }
   }

   public void testCreateUpdateAndDeleteSubnet() {
      for (String zone : api.getConfiguredZones()) {
         NetworkApi networkApi = api.getNetworkApiForZone(zone);
         String networkId = networkApi.create(CreateNetworkOptions.builder().name("jclouds-live-test").networkType(NetworkType.LOCAL).build()).getId();

         SubnetApi subnetApi = api.getSubnetApiForZone(zone);
         Set<AllocationPool> allocationPools = ImmutableSet.of(
            AllocationPool.builder().start("a3:bc00::10").end("a3:bc00::20").build(),
            AllocationPool.builder().start("a3:bc00::50").end("a3:bc00::90").build()
         );
         Set<HostRoute> hostRoutes = ImmutableSet.of(
            HostRoute.builder().destinationCidr("a3:bc00::/48").nextHop("a3:bc00::0004").build()
         );
         Subnet subnet = subnetApi.create(networkId, 6, "a3:bc00::/48", CreateSubnetOptions.builder().allocationPools(allocationPools).hostRoutes(hostRoutes).build());
         assertNotNull(subnet);

         Subnet retrievedSubnet = subnetApi.get(subnet.getId());

         assertEquals(retrievedSubnet.getId(), subnet.getId());
         assertEquals(retrievedSubnet.getCidr(), "a3:bc00::/48");
         assertTrue(retrievedSubnet.getDnsNameServers().isEmpty());
         assertEquals(retrievedSubnet.getAllocationPools().size(), 2);
         assertEquals(retrievedSubnet.getHostRoutes().size(), 1);
         assertTrue(subnetApi.update(retrievedSubnet.getId(), UpdateSubnetOptions.builder().name("jclouds-live-test-update").build()));

         retrievedSubnet = subnetApi.get(retrievedSubnet.getId());

         assertEquals(retrievedSubnet.getId(), subnet.getId());
         assertEquals(retrievedSubnet.getName(), "jclouds-live-test-update");
         assertTrue(retrievedSubnet.getDnsNameServers().isEmpty());

         Subnet subnet2 = subnetApi.create(networkId, 6, "a3:bd01::/48");
         assertNotNull(subnet2);

         assertTrue(subnetApi.delete(subnet.getId()));
         assertTrue(subnetApi.delete(subnet2.getId()));
         assertTrue(networkApi.delete(networkId));
      }
   }

   public void testBulkCreateSubnet() {
      for (String zone : api.getConfiguredZones()) {
         NetworkApi networkApi = api.getNetworkApiForZone(zone);
         String networkId = networkApi.create(CreateNetworkOptions.builder().name("jclouds-live-test").networkType(NetworkType.LOCAL).build()).getId();

         SubnetApi subnetApi = api.getSubnetApiForZone(zone);
         Set<? extends Subnet> subnets = subnetApi.createBulk(
            CreateSubnetBulkOptions.builder().subnets(
               ImmutableList.of(
                  BulkSubnet.builder().name("jclouds-live-test-1").cidr("a3:bd01::/48").ipVersion(6).networkId(networkId).build(),
                  BulkSubnet.builder().name("jclouds-live-test-2").cidr("a3:bd02::/48").ipVersion(6).networkId(networkId).build(),
                  BulkSubnet.builder().name("jclouds-live-test-3").cidr("a3:bd03::/48").ipVersion(6).networkId(networkId).build()
               )
            ).build()
         ).toSet();
         Set<? extends Subnet> existingSubnets = subnetApi.listInDetail().concat().toSet();

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
