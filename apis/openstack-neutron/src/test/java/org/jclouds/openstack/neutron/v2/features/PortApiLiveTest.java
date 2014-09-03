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
import org.jclouds.openstack.neutron.v2.domain.IP;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.NetworkType;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiLiveTest;
import org.jclouds.openstack.neutron.v2.util.PredicateUtil;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests PortApi in combination with the Network & SubnetApi
 */
@Test(groups = "live", testName = "PortApiLiveTest")
public class PortApiLiveTest extends BaseNeutronApiLiveTest {

   public void testCreateUpdateAndDeletePort() {
      for (String region : api.getConfiguredRegions()) {
         NetworkApi networkApi = api.getNetworkApi(region);
         SubnetApi subnetApi = api.getSubnetApi(region);
         PortApi portApi = api.getPortApi(region);
         String networkId = networkApi.create(
               Network.createBuilder("JClouds-Live-Network").networkType(NetworkType.LOCAL).build()).getId();
         String ipv4SubnetId = subnetApi.create(Subnet.createBuilder(networkId, "198.51.100.0/24").ipVersion(4)
               .name("JClouds-Live-IPv4-Subnet").build()).getId();
         String ipv6SubnetId = subnetApi.create(Subnet.createBuilder(networkId, "a1ca:1e1:c:107d::/96").ipVersion(6)
               .name("JClouds-Live-IPv6-Subnet").build()).getId();

         assertNotNull(networkId);
         assertNotNull(ipv4SubnetId);
         assertNotNull(ipv6SubnetId);

         String ipv4PortId = portApi.create(Port.createBuilder(networkId).name("JClouds-Live-IPv4-Port")
               .fixedIps(ImmutableSet.copyOf(getFixedAddresses(ipv4SubnetId))).build()).getId();
         String ipv6PortId = portApi.create(Port.createBuilder(networkId).name("JClouds-Live-IPv6-Port")
               .fixedIps(ImmutableSet.copyOf(getFixedAddresses(ipv6SubnetId))).build()).getId();

         /* List and get test */
         Port portList = api.getPortApi(region).list().concat().toSet().iterator().next();
         assertNotNull(portList);
         Port portGet = api.getPortApi(region).get(portList.getId());
         assertEquals(portList, portGet);
         /****/

         assertNotNull(ipv4PortId);
         assertNotNull(ipv6PortId);

         Port ipv4Port = portApi.get(ipv4PortId);
         assertNotNull(ipv4Port);
         assertEquals(ipv4Port.getId(), ipv4PortId);
         assertEquals(ipv4Port.getName(), "JClouds-Live-IPv4-Port");

         Port ipv6Port = portApi.get(ipv6PortId);
         assertNotNull(ipv6Port);
         assertEquals(ipv6Port.getId(), ipv6PortId);
         assertEquals(ipv6Port.getName(), "JClouds-Live-IPv6-Port");

         assertNotNull(portApi.update(ipv4PortId, Port.updateBuilder().name("Updated").build()));
         Port updatedIpv4Port = portApi.get(ipv4PortId);
         assertEquals(updatedIpv4Port.getName(), "Updated");

         assertTrue(portApi.delete(ipv4PortId));
         assertTrue(portApi.delete(ipv6PortId));
         assertTrue(subnetApi.delete(ipv4SubnetId));
         assertTrue(subnetApi.delete(ipv6SubnetId));
         assertTrue(networkApi.delete(networkId));
      }
   }

   public void testBulkCreatePort() {
      for (String region : api.getConfiguredRegions()) {
         NetworkApi networkApi = api.getNetworkApi(region);
         SubnetApi subnetApi = api.getSubnetApi(region);
         PortApi portApi = api.getPortApi(region);

         String networkId = networkApi.create(
               Network.createBuilder("JClouds-Live-Network").networkType(NetworkType.LOCAL).build()).getId();
         String ipv4SubnetId = subnetApi.create(Subnet.createBuilder(networkId, "198.51.100.0/24").ipVersion(4)
               .name("JClouds-Live-IPv4-Subnet").build()).getId();
         String ipv6SubnetId = subnetApi.create(Subnet.createBuilder(networkId, "a1ca:1e1:c:107d::/96").ipVersion(6)
               .name("JClouds-Live-IPv6-Subnet").build()).getId();

         assertNotNull(networkId);
         assertNotNull(ipv4SubnetId);
         assertNotNull(ipv6SubnetId);

         Set<? extends Port> ports = portApi.createBulk(
               ImmutableList.of(
                     Port.createBuilder(networkId).name("JClouds-Live-IPv4-Subnet-1")
                           .fixedIps(ImmutableSet.copyOf(getFixedAddresses(ipv4SubnetId))).build(),
                     Port.createBuilder(networkId).name("JClouds-Live-IPv4-Subnet-2")
                           .fixedIps(ImmutableSet.copyOf(getFixedAddresses(ipv4SubnetId))).build(),
                     Port.createBuilder(networkId).name("JClouds-Live-IPv6-Subnet-1")
                           .fixedIps(ImmutableSet.copyOf(getFixedAddresses(ipv6SubnetId))).build(),
                     Port.createBuilder(networkId).name("JClouds-Live-IPv6-Subnet-2")
                           .fixedIps(ImmutableSet.copyOf(getFixedAddresses(ipv6SubnetId))).build()
               )
         ).toSet();
         Set<? extends Port> existingPorts = portApi.list().concat().toSet();

         assertNotNull(ports);
         assertFalse(ports.isEmpty());
         assertEquals(ports.size(), 4);

         for (Port port : ports) {
            Predicate<Port> idEqualsPredicate = PredicateUtil.createIdEqualsPredicate(port.getId());
            assertEquals(1, Sets.filter(existingPorts, idEqualsPredicate).size());
            assertTrue(portApi.delete(port.getId()));
         }
         assertTrue(subnetApi.delete(ipv4SubnetId));
         assertTrue(subnetApi.delete(ipv6SubnetId));
         assertTrue(networkApi.delete(networkId));
      }
   }

   public Set<IP> getFixedAddresses(String subnetId) {
      return ImmutableSet.of(
         IP.builder().subnetId(subnetId).build()
      );
   }
}
