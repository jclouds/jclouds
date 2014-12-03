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
package org.jclouds.rackspace.cloudnetworks.uk;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.PortApi;
import org.jclouds.openstack.neutron.v2.features.PortApiLiveTest;
import org.jclouds.openstack.neutron.v2.features.SubnetApi;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "live", testName = "CloudNetworksUKPortApiLiveTest", singleThreaded = true)
public class CloudNetworksUKPortApiLiveTest extends PortApiLiveTest {
   public CloudNetworksUKPortApiLiveTest() {
      provider = "rackspace-cloudnetworks-uk";
   }

   public void testCreateUpdateAndDeletePort() {
      for (String region : api.getConfiguredRegions()) {
         NetworkApi networkApi = api.getNetworkApi(region);
         SubnetApi subnetApi = api.getSubnetApi(region);
         PortApi portApi = api.getPortApi(region);
         String networkId = networkApi.create(
               Network.createBuilder("JClouds-Live-Network").build()).getId();
         String ipv4SubnetId = subnetApi.create(Subnet.createBuilder(networkId, "192.168.0.0/30").ipVersion(4)
               .name("JClouds-Live-IPv4-Subnet").build()).getId();

         assertNotNull(networkId);
         assertNotNull(ipv4SubnetId);

         String ipv4PortId = portApi.create(Port.createBuilder(networkId).name("JClouds-Live-IPv4-Port")
               .fixedIps(ImmutableSet.copyOf(getFixedAddresses(ipv4SubnetId))).build()).getId();

         /* List and get test */
         Port portList = api.getPortApi(region).list().concat().toSet().iterator().next();
         assertNotNull(portList);
         Port portGet = api.getPortApi(region).get(portList.getId());
         assertEquals(portList, portGet);
         /****/

         assertNotNull(ipv4PortId);

         Port ipv4Port = portApi.get(ipv4PortId);
         assertNotNull(ipv4Port);
         assertEquals(ipv4Port.getId(), ipv4PortId);
         assertEquals(ipv4Port.getName(), "JClouds-Live-IPv4-Port");

         assertNotNull(portApi.update(ipv4PortId, Port.updateBuilder().name("Updated").build()));
         Port updatedIpv4Port = portApi.get(ipv4PortId);
         assertEquals(updatedIpv4Port.getName(), "Updated");

         assertTrue(portApi.delete(ipv4PortId));
         assertTrue(subnetApi.delete(ipv4SubnetId));
         assertTrue(networkApi.delete(networkId));
      }
   }

   @Override
   public void testBulkCreatePort() {
      throw new SkipException("unsupported functionality");
   }
}
