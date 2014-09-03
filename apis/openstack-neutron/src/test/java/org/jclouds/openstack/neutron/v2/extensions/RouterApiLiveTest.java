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

import org.jclouds.openstack.neutron.v2.domain.ExternalGatewayInfo;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.NetworkType;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.jclouds.openstack.neutron.v2.domain.Router;
import org.jclouds.openstack.neutron.v2.domain.RouterInterface;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.PortApi;
import org.jclouds.openstack.neutron.v2.features.SubnetApi;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests parsing and Guice wiring of RouterApi
 */
@Test(groups = "live", testName = "RouterApiLiveTest")
public class RouterApiLiveTest extends BaseNeutronApiLiveTest {

   public void testCreateUpdateAndDeleteRouter() {
      for (String region : api.getConfiguredRegions()) {
         RouterApi routerApi = api.getRouterApi(region).get();
         NetworkApi networkApi = api.getNetworkApi(region);
         SubnetApi subnetApi = api.getSubnetApi(region);

         Network network = networkApi.create(
               Network.createBuilder("jclouds-network-test").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network);

         Subnet subnet = subnetApi.create(Subnet.createBuilder(network.getId(), "192.168.0.0/16").ipVersion(4).build());
         assertNotNull(subnet);

         Router router = routerApi.create(Router.createBuilder().name("jclouds-router-test")
            .externalGatewayInfo(ExternalGatewayInfo.builder().networkId(network.getId()).build()).build());
         assertNotNull(router);

         /* List and Get test */
         Set<Router> routers = api.getRouterApi(region).get().list().concat().toSet();
         Router routerList = routers.iterator().next();
         Router routerGet = api.getRouterApi(region).get().get(routerList.getId());

         assertNotNull(routerGet);
         assertEquals(routerGet, routerList);
         /***/

         routerGet = routerApi.get(router.getId());

         assertEquals(routerGet.getName(), router.getName());
         assertEquals(routerGet.getId(), router.getId());
         assertEquals(routerGet.getExternalGatewayInfo(), router.getExternalGatewayInfo());

         Router routerUpdate = routerApi.update(router.getId(), Router.updateBuilder().name("jclouds-router-test-rename").build());
         assertNotNull(routerUpdate);
         assertEquals(routerUpdate.getName(), "jclouds-router-test-rename");

         routerGet = routerApi.get(router.getId());

         assertEquals(routerGet.getId(), router.getId());
         assertEquals(routerGet.getName(), "jclouds-router-test-rename");

         assertTrue(routerApi.delete(router.getId()));
         assertTrue(subnetApi.delete(subnet.getId()));
         assertTrue(networkApi.delete(network.getId()));
      }
   }

   public void testCreateAndDeleteRouterInterfaceForSubnet() {
      for (String region : api.getConfiguredRegions()) {
         RouterApi routerApi = api.getRouterApi(region).get();
         NetworkApi networkApi = api.getNetworkApi(region);
         SubnetApi subnetApi = api.getSubnetApi(region);

         Network network = networkApi.create(Network.createBuilder("jclouds-network-test").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network);

         Subnet subnet = subnetApi.create(Subnet.createBuilder(network.getId(), "192.168.0.0/16").ipVersion(4).build());
         assertNotNull(subnet);

         Network network2 = networkApi.create(Network.createBuilder("jclouds-network-test2").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network2);

         Subnet subnet2 = subnetApi.create(Subnet.createBuilder(network2.getId(), "192.169.0.0/16").ipVersion(4).build());
         assertNotNull(subnet2);

         Router router = routerApi.create(Router.createBuilder().name("jclouds-router-test").build());
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
      for (String region : api.getConfiguredRegions()) {
         RouterApi routerApi = api.getRouterApi(region).get();
         NetworkApi networkApi = api.getNetworkApi(region);
         SubnetApi subnetApi = api.getSubnetApi(region);
         PortApi portApi = api.getPortApi(region);

         Network network = networkApi.create(Network.createBuilder("jclouds-network-test").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network);

         Subnet subnet = subnetApi.create(Subnet.createBuilder(network.getId(), "192.168.0.0/16").ipVersion(4).build());
         assertNotNull(subnet);

         Network network2 = networkApi.create(Network.createBuilder("jclouds-network-test2").external(true).networkType(NetworkType.LOCAL).build());
         assertNotNull(network2);

         Subnet subnet2 = subnetApi.create(Subnet.createBuilder(network2.getId(), "192.169.0.0/16").ipVersion(4).build());
         assertNotNull(subnet2);

         Port port = portApi.create(Port.createBuilder(network.getId()).build());
         assertNotNull(port);

         Port port2 = portApi.create(Port.createBuilder(network2.getId()).build());
         assertNotNull(port2);

         Router router = routerApi.create(Router.createBuilder().name("jclouds-router-test").build());
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
