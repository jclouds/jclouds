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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.neutron.v2_0.domain.BulkPort;
import org.jclouds.openstack.neutron.v2_0.domain.IP;
import org.jclouds.openstack.neutron.v2_0.domain.NetworkType;
import org.jclouds.openstack.neutron.v2_0.domain.Port;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.internal.BaseNeutronApiLiveTest;
import org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreatePortBulkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreatePortOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdatePortOptions;
import org.jclouds.openstack.neutron.v2_0.util.PredicateUtil;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Tests PortApi in combination with the Network & SubnetApi
 *
 */
@Test(groups = "live", testName = "PortApiLiveTest")
public class PortApiLiveTest extends BaseNeutronApiLiveTest {

   public void testGetAndListPorts() {
      for (String zone : api.getConfiguredZones()) {
         Set<? extends ReferenceWithName> references = api.getPortApiForZone(zone).list().concat().toSet();
         Set<? extends Port> ports = api.getPortApiForZone(zone).listInDetail().concat().toSet();

         assertNotNull(references);
         assertNotNull(ports);
         assertEquals(references.size(), ports.size());

         for (Port port : ports) {
            assertNotNull(port.getName());
            assertTrue(references.contains(ReferenceWithName.builder().id(port.getId()).tenantId(port.getTenantId()).name(port.getName()).build()));

            Port retrievedPort = api.getPortApiForZone(zone).get(port.getId());
            assertEquals(port, retrievedPort);
         }
      }
   }

   public void testCreateUpdateAndDeletePort() {
      for (String zone : api.getConfiguredZones()) {
         NetworkApi networkApi = api.getNetworkApiForZone(zone);
         SubnetApi subnetApi = api.getSubnetApiForZone(zone);
         PortApi portApi = api.getPortApiForZone(zone);
         String networkId = networkApi.create(CreateNetworkOptions.builder().name("JClouds-Live-Network").networkType(NetworkType.LOCAL).build()).getId();
         String ipv4SubnetId = subnetApi.create(networkId, 4, "198.51.100.0/24", CreateSubnetOptions.builder().name("JClouds-Live-IPv4-Subnet").build()).getId();
         String ipv6SubnetId = subnetApi.create(networkId, 6, "a1ca:1e1:c:107d::/96", CreateSubnetOptions.builder().name("JClouds-Live-IPv6-Subnet").build()).getId();

         assertNotNull(networkId);
         assertNotNull(ipv4SubnetId);
         assertNotNull(ipv6SubnetId);

         String ipv4PortId = portApi.create(networkId, CreatePortOptions.builder().name("JClouds-Live-IPv4-Port").fixedIps(getFixedAddresses(ipv4SubnetId)).build()).getId();
         String ipv6PortId = portApi.create(networkId, CreatePortOptions.builder().name("JClouds-Live-IPv6-Port").fixedIps(getFixedAddresses(ipv6SubnetId)).build()).getId();

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

         assertTrue(portApi.update(ipv4PortId, UpdatePortOptions.builder().name("Updated").build()));
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
      for (String zone : api.getConfiguredZones()) {
         NetworkApi networkApi = api.getNetworkApiForZone(zone);
         SubnetApi subnetApi = api.getSubnetApiForZone(zone);
         PortApi portApi = api.getPortApiForZone(zone);

         String networkId = networkApi.create(CreateNetworkOptions.builder().name("JClouds-Live-Network").networkType(NetworkType.LOCAL).build()).getId();
         String ipv4SubnetId = subnetApi.create(networkId, 4, "198.51.100.0/24", CreateSubnetOptions.builder().name("JClouds-Live-IPv4-Subnet").build()).getId();
         String ipv6SubnetId = subnetApi.create(networkId, 6, "a1ca:1e1:c:107d::/96", CreateSubnetOptions.builder().name("JClouds-Live-IPv6-Subnet").build()).getId();

         assertNotNull(networkId);
         assertNotNull(ipv4SubnetId);
         assertNotNull(ipv6SubnetId);

         Set<? extends Port> ports = portApi.createBulk(
            CreatePortBulkOptions.builder().ports(
               ImmutableSet.of(
                  BulkPort.builder().networkId(networkId).name("JClouds-Live-IPv4-Subnet-1").fixedIps(getFixedAddresses(ipv4SubnetId)).build(),
                  BulkPort.builder().networkId(networkId).name("JClouds-Live-IPv4-Subnet-2").fixedIps(getFixedAddresses(ipv4SubnetId)).build(),
                  BulkPort.builder().networkId(networkId).name("JClouds-Live-IPv6-Subnet-1").fixedIps(getFixedAddresses(ipv6SubnetId)).build(),
                  BulkPort.builder().networkId(networkId).name("JClouds-Live-IPv6-Subnet-2").fixedIps(getFixedAddresses(ipv6SubnetId)).build()
               )
            ).build()
         ).toSet();
         Set<? extends Port> existingPorts = portApi.listInDetail().concat().toSet();

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
