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

package org.jclouds.openstack.neutron.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.neutron.v2_0.domain.ExternalGatewayInfo;
import org.jclouds.openstack.neutron.v2_0.domain.Network;
import org.jclouds.openstack.neutron.v2_0.domain.NetworkType;
import org.jclouds.openstack.neutron.v2_0.domain.Port;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.domain.Router;
import org.jclouds.openstack.neutron.v2_0.domain.RouterInterface;
import org.jclouds.openstack.neutron.v2_0.domain.Subnet;
import org.jclouds.openstack.neutron.v2_0.features.NetworkApi;
import org.jclouds.openstack.neutron.v2_0.features.PortApi;
import org.jclouds.openstack.neutron.v2_0.features.SubnetApi;
import org.jclouds.openstack.neutron.v2_0.internal.BaseNeutronApiLiveTest;
import org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreateRouterOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdateRouterOptions;
import org.testng.annotations.Test;

/**
 * Tests parsing and Guice wiring of RouterApi
 *
 */
@Test(groups = "live", testName = "RouterApiLiveTest")
public class RouterApiLiveTest extends BaseNeutronApiLiveTest {

   public void testGetAndListRouters() {
      for (String zone : api.getConfiguredZones()) {
         Set<? extends ReferenceWithName> references = api.getRouterExtensionForZone(zone).get().list().concat().toSet();
         Set<? extends Router> routers = api.getRouterExtensionForZone(zone).get().listInDetail().concat().toSet();

         assertNotNull(references);
         assertNotNull(routers);
         assertEquals(references.size(), routers.size());

         for (Router router : routers) {
            assertNotNull(router.getName());
            assertTrue(references.contains(ReferenceWithName.builder().id(router.getId()).tenantId(router.getTenantId()).name(router.getName()).build()));

            Router retrievedRouter = api.getRouterExtensionForZone(zone).get().get(router.getId());
            assertEquals(router, retrievedRouter);
         }
      }
   }

   public void testCreateUpdateAndDeleteRouter() {
      for (String zone : api.getConfiguredZones()) {
         RouterApi routerApi = api.getRouterExtensionForZone(zone).get();
         NetworkApi networkApi = api.getNetworkApiForZone(zone);
         SubnetApi subnetApi = api.getSubnetApiForZone(zone);

         Network network = networkApi.create(CreateNetworkOptions.builder().name("jclouds-network-test").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network);

         Subnet subnet = subnetApi.create(network.getId(), 4, "192.168.0.0/16");
         assertNotNull(subnet);

         Router ref = routerApi.create(CreateRouterOptions.builder().name("jclouds-router-test")
            .externalGatewayInfo(ExternalGatewayInfo.builder().networkId(network.getId()).build()).build());
         assertNotNull(ref);

         Router router = routerApi.get(ref.getId());

         assertEquals(router.getId(), ref.getId());
         assertEquals(router.getName(), "jclouds-router-test");
         assertEquals(router.getExternalGatewayInfo().getNetworkId(), network.getId());
         assertTrue(routerApi.update(router.getId(), UpdateRouterOptions.builder().name("jclouds-router-test-rename").build()));

         router = routerApi.get(ref.getId());

         assertEquals(router.getId(), ref.getId());
         assertEquals(router.getName(), "jclouds-router-test-rename");

         ReferenceWithName ref2 = routerApi.create(CreateRouterOptions.builder().name("jclouds-router-test2")
            .externalGatewayInfo(ExternalGatewayInfo.builder().networkId(network.getId()).build()).build());
         assertNotNull(ref2);

         assertTrue(routerApi.delete(ref.getId()));
         assertTrue(routerApi.delete(ref2.getId()));
         assertTrue(subnetApi.delete(subnet.getId()));
         assertTrue(networkApi.delete(network.getId()));
      }
   }

   public void testCreateAndDeleteRouterInterfaceForSubnet() {
      for (String zone : api.getConfiguredZones()) {
         RouterApi routerApi = api.getRouterExtensionForZone(zone).get();
         NetworkApi networkApi = api.getNetworkApiForZone(zone);
         SubnetApi subnetApi = api.getSubnetApiForZone(zone);

         Network network = networkApi.create(CreateNetworkOptions.builder().name("jclouds-network-test").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network);

         Subnet subnet = subnetApi.create(network.getId(), 4, "192.168.0.0/16");
         assertNotNull(subnet);

         Network network2 = networkApi.create(CreateNetworkOptions.builder().name("jclouds-network-test2").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network2);

         Subnet subnet2 = subnetApi.create(network2.getId(), 4, "192.169.0.0/16");
         assertNotNull(subnet2);

         Router router = routerApi.create(CreateRouterOptions.builder().name("jclouds-router-test").build());
         assertNotNull(router);

         RouterInterface routerInterface = routerApi.addInterfaceForSubnet(router.getId(), subnet.getId());
         assertNotNull(routerInterface);

         RouterInterface routerInterface2 = routerApi.addInterfaceForSubnet(router.getId(), subnet2.getId());
         assertNotNull(routerInterface2);

         assertTrue(routerApi.removeInterfaceForSubnet(router.getId(), subnet.getId()));
         assertTrue(routerApi.removeInterfaceForSubnet(router.getId(), subnet2.getId()));
         assertTrue(routerApi.delete(router.getId()));
         assertTrue(subnetApi.delete(subnet.getId()));
         assertTrue(networkApi.delete(network.getId()));
         assertTrue(subnetApi.delete(subnet2.getId()));
         assertTrue(networkApi.delete(network2.getId()));
      }
   }

   public void testCreateAndDeleteRouterInterfaceForPort() {
      for (String zone : api.getConfiguredZones()) {
         RouterApi routerApi = api.getRouterExtensionForZone(zone).get();
         NetworkApi networkApi = api.getNetworkApiForZone(zone);
         SubnetApi subnetApi = api.getSubnetApiForZone(zone);
         PortApi portApi = api.getPortApiForZone(zone);

         Network network = networkApi.create(CreateNetworkOptions.builder().name("jclouds-network-test").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network);

         Subnet subnet = subnetApi.create(network.getId(), 4, "192.168.0.0/16");
         assertNotNull(subnet);

         Network network2 = networkApi.create(CreateNetworkOptions.builder().name("jclouds-network-test2").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network2);

         Subnet subnet2 = subnetApi.create(network2.getId(), 4, "192.169.0.0/16");
         assertNotNull(subnet2);

         Port port = portApi.create(network.getId());
         assertNotNull(port);

         Port port2 = portApi.create(network2.getId());
         assertNotNull(port2);

         Router router = routerApi.create(CreateRouterOptions.builder().name("jclouds-router-test").build());
         assertNotNull(router);

         RouterInterface routerInterface = routerApi.addInterfaceForPort(router.getId(), port.getId());
         assertNotNull(routerInterface);

         RouterInterface routerInterface2 = routerApi.addInterfaceForPort(router.getId(), port2.getId());
         assertNotNull(routerInterface2);

         assertTrue(routerApi.removeInterfaceForPort(router.getId(), port.getId()));
         assertTrue(routerApi.removeInterfaceForPort(router.getId(), port2.getId()));
         assertTrue(routerApi.delete(router.getId()));
         assertTrue(subnetApi.delete(subnet.getId()));
         assertTrue(networkApi.delete(network.getId()));
         assertTrue(subnetApi.delete(subnet2.getId()));
         assertTrue(networkApi.delete(network2.getId()));

      }
   }

}
