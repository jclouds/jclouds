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

import org.jclouds.openstack.neutron.v2_0.domain.BulkNetwork;
import org.jclouds.openstack.neutron.v2_0.domain.Network;
import org.jclouds.openstack.neutron.v2_0.domain.NetworkType;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.internal.BaseNeutronApiLiveTest;
import org.jclouds.openstack.neutron.v2_0.options.CreateNetworkBulkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdateNetworkOptions;
import org.jclouds.openstack.neutron.v2_0.util.PredicateUtil;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Tests parsing and Guice wiring of NetworkApi
 *
 */
@Test(groups = "live", testName = "NetworkApiLiveTest")
public class NetworkApiLiveTest extends BaseNeutronApiLiveTest {

   public void testGetAndListNetworks() {
      for (String zone : api.getConfiguredZones()) {
         Set<? extends ReferenceWithName> references = api.getNetworkApiForZone(zone).list().concat().toSet();
         Set<? extends Network> networks = api.getNetworkApiForZone(zone).listInDetail().concat().toSet();

         assertNotNull(references);
         assertNotNull(networks);
         assertEquals(references.size(), networks.size());

         for (Network network : networks) {
            assertNotNull(network.getName());
            assertTrue(references.contains(ReferenceWithName.builder().id(network.getId()).tenantId(network.getTenantId()).name(network.getName()).build()));

            Network retrievedNetwork = api.getNetworkApiForZone(zone).get(network.getId());
            assertEquals(network, retrievedNetwork);
         }
      }
   }

   public void testCreateUpdateAndDeleteNetwork() {
      for (String zone : api.getConfiguredZones()) {
         NetworkApi networkApi = api.getNetworkApiForZone(zone);
         Network net = networkApi.create(CreateNetworkOptions.builder().name("jclouds-test").networkType(NetworkType.LOCAL).build());
         Network test = networkApi.create();
         assertNotNull(net);

         Network network = networkApi.get(net.getId());

         assertEquals(network.getId(), net.getId());
         assertEquals(network.getName(), "jclouds-test");
         assertEquals(network.getNetworkType(), NetworkType.LOCAL);
         assertTrue(network.getSubnets().isEmpty());
         assertTrue(networkApi.update(net.getId(), UpdateNetworkOptions.builder().name("jclouds-live-test").build()));

         network = networkApi.get(net.getId());

         assertEquals(network.getId(), net.getId());
         assertEquals(network.getName(), "jclouds-live-test");
         assertTrue(network.getSubnets().isEmpty());

         Network net2 = networkApi.create(CreateNetworkOptions.builder().name("jclouds-test2").networkType(NetworkType.LOCAL).build());
         assertNotNull(net2);

         assertTrue(networkApi.delete(net.getId()));
         assertTrue(networkApi.delete(net2.getId()));
         assertTrue(networkApi.delete(test.getId()));
      }
   }

   public void testBulkCreateNetwork() {
      for (String zone : api.getConfiguredZones()) {
         NetworkApi networkApi = api.getNetworkApiForZone(zone);
         Set<? extends Network> nets = networkApi.createBulk(
            CreateNetworkBulkOptions.builder().networks(
               ImmutableList.of(
                  BulkNetwork.builder().networkType(NetworkType.LOCAL).name("jclouds-live-test-1").adminStateUp(true).build(),
                  BulkNetwork.builder().networkType(NetworkType.LOCAL).name("jclouds-live-test-2").adminStateUp(false).build(),
                  BulkNetwork.builder().networkType(NetworkType.LOCAL).name("jclouds-live-test-3").adminStateUp(false).build()
               )
            ).build()
         ).toSet();
         Set<? extends Network> existingNets = networkApi.listInDetail().concat().toSet();

         assertNotNull(nets);
         assertTrue(!nets.isEmpty());
         assertEquals(nets.size(), 3);

         for (Network net : nets) {
            Predicate<Network> idEqualsPredicate = PredicateUtil.createIdEqualsPredicate(net.getId());
            assertEquals(1, Sets.filter(existingNets, idEqualsPredicate).size());
            assertTrue(networkApi.delete(net.getId()));
         }
      }
   }
}
