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
package org.jclouds.softlayer.features;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.jclouds.softlayer.domain.Network;
import org.jclouds.softlayer.domain.Subnet;
import org.jclouds.softlayer.domain.Datacenter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

/**
 * Tests behavior of {@code NetworkApi}
 */
@Test(groups = "live")
public class NetworkApiLiveTest extends BaseSoftLayerApiLiveTest {

   private NetworkApi networkApi;

   private Network network = null;
   private Subnet subnet = null;

   private Datacenter datacenter = null;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();

      networkApi = api.getNetworkApi();

      datacenter = Iterables.get(api.getDatacenterApi().listDatacenters(), 0);
      assertNotNull(datacenter, "Datacenter must not be null");
   }

   @Test
   public void testCreateNetwork() throws Exception {
      Network.CreateNetwork createNetwork = Network.CreateNetwork.builder()
              .networkIdentifier("192.168.253.0")
              .name("NetworkApiLiveTestNetwork")
              .cidr(24)
              .notes("this is a test network from jclouds NetworkApiLiveTest.")
              .build();

      network = networkApi.createNetwork(ImmutableList.of(createNetwork));

      checkNetwork(network);
   }

   @Test(dependsOnMethods = "testCreateNetwork")
   public void testListNetworks() throws Exception {
      List<Network> response = networkApi.listNetworks();
      assertNotNull(response, "listNetworks returns null");

      boolean found = false;

      for (Network n : response) {
         Network newDetails = networkApi.getNetwork(n.id());
         assertEquals(n.id(), newDetails.id());
         checkNetwork(newDetails);
         if (n.id() == network.id()) {
            found = true;
            assertEquals(n.cidr(), network.cidr());
            assertEquals(n.networkIdentifier(), network.networkIdentifier());
            assertEquals(n.name(), n.name());
         }
      }

      assertTrue(found, "List Networks didn't return created network.");
   }

   @Test(dependsOnMethods = "testCreateNetwork")
   public void testGetNetwork() throws Exception {
      Network found = networkApi.getNetwork(network.id());
      assertEquals(found, network);
      checkNetwork(found);
      assertEquals(found.networkIdentifier(), "192.168.253.0");
      assertEquals(found.name(), "NetworkApiLiveTestNetwork");
      assertEquals(found.cidr(), 24);
      assertEquals(found.notes(), "this is a test network from jclouds NetworkApiLiveTest.");
   }

   @Test(dependsOnMethods = "testCreateNetwork")
   public void testGetName() throws Exception {
      String name = networkApi.getName(network.id());

      assertEquals(name, "NetworkApiLiveTestNetwork");
   }

   @Test(dependsOnMethods = "testCreateNetwork")
   public void testGetNotes() throws Exception {
      String notes = networkApi.getNotes(network.id());

      assertEquals(notes, "this is a test network from jclouds NetworkApiLiveTest.");
   }

   @Test(dependsOnMethods = "testCreateNetwork")
   public void testCreateSubnet() throws Exception {
      Subnet.CreateSubnet createSubnet = Subnet.CreateSubnet.builder()
              .networkIdentifier("192.168.253.0")
              .cidr(28)
              .note("This is a test subnet from NetworkApiLiveTest")
              .build();

      Subnet.CreateDatacenterName createDatacenterName = Subnet.CreateDatacenterName.builder()
              .name("dal10.pod01")
              .build();

      List<Object> parameters = new ArrayList<Object>();
      parameters.add(createSubnet);
      parameters.add(createDatacenterName);

      subnet = networkApi.createSubnet(network.id(), parameters);

      network = networkApi.getNetwork(network.id());
      assertEquals(network.subnets().size(), 1);
   }

   @Test(dependsOnMethods = "testCreateSubnet")
   public void testGetSubnets() throws Exception {
      List<Subnet> subnets = networkApi.getSubnets(network.id());

      assertEquals(subnets.size(), 1);
      assertEquals(subnets.get(0).id(), subnet.id());
      assertEquals(subnets.get(0).cidr(), subnet.cidr());
      assertEquals(subnets.get(0).networkIdentifier(), subnet.networkIdentifier());
      assertEquals(subnets.get(0).note(), subnet.note());
   }

   @Test(dependsOnMethods = "testGetSubnets")
   public void testDeleteSubnet() throws Exception {
      Subnet.DeleteSubnet deleteSubnet = Subnet.DeleteSubnet.builder()
              .id(subnet.id())
              .build();


      List<Subnet.DeleteSubnet> parameters = new ArrayList<Subnet.DeleteSubnet>();
      parameters.add(deleteSubnet);

      boolean deletion = networkApi.deleteSubnet(network.id(), parameters);

      assertTrue(deletion, "deletion of subnet failed");

      List<Subnet> subnets = networkApi.getSubnets(network.id());
      assertEquals(subnets.size(), 0);

      subnet = null;
   }

   @Test(dependsOnMethods = "testGetNetwork")
   public void testEditNetwork() throws Exception {
      Network.EditNetwork editNetwork = Network.EditNetwork.builder()
              .name("newNetworkApiLiveTestNetwork")
              .notes("new notes")
              .id(network.id())
              .build();

      boolean edit = networkApi.editNetwork(network.id(), ImmutableList.of(editNetwork));
      assertTrue(edit, "edition failed");

      Network newFound = networkApi.getNetwork(network.id());
      assertEquals(newFound.name(), "newNetworkApiLiveTestNetwork");
      assertEquals(newFound.notes(), "new notes");

      network = newFound;
   }

   @Test(dependsOnMethods = "testDeleteSubnet")
   public void testDeleteNetwork() throws Exception {
      boolean deletion = networkApi.deleteNetwork(network.id());

      Network found = networkApi.getNetwork(network.id());
      assertNull(found, "deletion failed");

      network = null;
   }

   private void checkNetwork(Network n) {
      assertNotNull(n.id());
      assertNotNull(n.name());
      assertNotNull(n.networkIdentifier());
      assertNotNull(n.cidr());
   }
}
