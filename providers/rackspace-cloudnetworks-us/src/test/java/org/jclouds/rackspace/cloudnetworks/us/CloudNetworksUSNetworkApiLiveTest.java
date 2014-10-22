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

import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.NetworkApiLiveTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "CloudNetworksUSNetworkApiLiveTest", singleThreaded = true)
public class CloudNetworksUSNetworkApiLiveTest extends NetworkApiLiveTest {
   public CloudNetworksUSNetworkApiLiveTest() {
      provider = "rackspace-cloudnetworks-us";
   }

   @Override
   public void testCreateUpdateAndDeleteNetwork() {
      for (String region : api.getConfiguredRegions()) {
         NetworkApi networkApi = api.getNetworkApi(region);
         Network net = networkApi.create(Network.createBuilder("jclouds-test").build());
         Network test = networkApi.create(Network.createBuilder("jclouds-test").build());
         assertNotNull(net);

         /* List and get tests */
         Network networkList = api.getNetworkApi(region).list().concat().toSet().iterator().next();
         assertNotNull(networkList);
         Network networkGet = api.getNetworkApi(region).get(networkList.getId());
         assertEquals(networkList, networkGet);
         /****/

         Network network = networkApi.get(net.getId());

         assertEquals(network.getId(), net.getId());
         assertEquals(network.getName(), "jclouds-test");
         assertTrue(network.getSubnets().isEmpty());
         assertNotNull(networkApi.update(net.getId(), Network.updateBuilder().name("jclouds-live-test").build()));

         network = networkApi.get(net.getId());

         assertEquals(network.getId(), net.getId());
         assertEquals(network.getName(), "jclouds-live-test");
         assertTrue(network.getSubnets().isEmpty());

         Network net2 = networkApi.create(Network.createBuilder("jclouds-test2").build());
         assertNotNull(net2);

         assertTrue(networkApi.delete(net.getId()));
         assertTrue(networkApi.delete(net2.getId()));
         assertTrue(networkApi.delete(test.getId()));
      }
   }

   @Override
   public void testBulkCreateNetwork() {
      throw new SkipException("unsupported functionality");
   }
}
