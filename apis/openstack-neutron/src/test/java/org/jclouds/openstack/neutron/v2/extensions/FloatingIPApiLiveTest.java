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

package org.jclouds.openstack.neutron.v2.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.neutron.v2.domain.FloatingIP;
import org.jclouds.openstack.neutron.v2.domain.IP;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.NetworkType;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.SubnetApi;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of RouterApi
 */
@Test(groups = "live", testName = "FloatingIPApiLiveTest")
public class FloatingIPApiLiveTest extends BaseNeutronApiLiveTest {

   public void testCreateUpdateAndDeleteFloatingIP() {
      for (String region : api.getConfiguredRegions()) {

         SubnetApi subnetApi = api.getSubnetApi(region);
         FloatingIPApi floatingIPApi = api.getFloatingIPApi(region).get();
         NetworkApi networkApi = api.getNetworkApi(region);

         FloatingIP floatingIPGet = null;
         String ipv4SubnetId = null;
         Network network = null;

         try {
            network = networkApi.create(
                  Network.createBuilder("jclouds-network-test").external(true).networkType(NetworkType.LOCAL).build());
            assertNotNull(network);

            ipv4SubnetId = subnetApi.create(Subnet.createBuilder(network.getId(), "198.51.100.0/24").ipVersion(4)
                  .name("JClouds-Live-IPv4-Subnet").build()).getId();

            floatingIPApi.create(FloatingIP.createBuilder(network.getId()).build());

            /* List and Get test */
            Set<FloatingIP> floatingIPs = floatingIPApi.list().concat().toSet();
            FloatingIP floatingIPList = floatingIPs.iterator().next();
            floatingIPGet = floatingIPApi.get(floatingIPList.getId());

            assertNotNull(floatingIPGet);
            assertEquals(floatingIPGet, floatingIPList);
         }
         finally {
            try {
               assertTrue(floatingIPApi.delete(floatingIPGet.getId()));
            }
            finally {
               try {
                  assertTrue(subnetApi.delete(ipv4SubnetId));
               }
               finally {
                  assertTrue(networkApi.delete(network.getId()));
               }
            }
         }
      }
   }

   public Set<IP> getFixedAddresses(String subnetId) {
      return ImmutableSet.of(IP.builder().subnetId(subnetId).build());
   }
}
