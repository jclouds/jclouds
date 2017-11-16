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
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.config.SoftLayerParserModule;
import org.jclouds.softlayer.domain.Network;
import org.jclouds.softlayer.domain.Subnet;
import org.jclouds.softlayer.internal.BaseSoftLayerMockTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
/**
 * Mock tests for the {@link NetworkApi} class.
 */
@Test(groups = "unit", testName = "SecurityGroupApiMockTest")
public class NetworkApiMockTest extends BaseSoftLayerMockTest {

   public void testListNetworks() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/network_list.json")));
      NetworkApi api = getNetworkApi(server);

      Iterable<Network> networks = api.listNetworks();

      try {
         assertEquals(size(networks), 5);
         assertEquals(server.getRequestCount(), 1);

         assertSent(server, "GET", "/SoftLayer_Network/getAllObjects?objectMask=mask.subnets");
      } finally {
         server.shutdown();
      }
   }

   public void testEmptyListNetworks() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = getNetworkApi(server);

      try {
         assertTrue(api.listNetworks().isEmpty());
         assertSent(server, "GET", "/SoftLayer_Network/getAllObjects?objectMask=mask.subnets");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/network_createObject.json")));
      NetworkApi api = getNetworkApi(server);

      Network.CreateNetwork createNetwork = Network.CreateNetwork.builder()
              .networkIdentifier("192.168.0.0")
              .name("testNet")
              .cidr(16)
              .notes("testNotes")
              .build();

      Network network = api.createNetwork(ImmutableList.of(createNetwork));

      try {
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network/createObject");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/network_get_153001.json")));
      NetworkApi api = getNetworkApi(server);

      Network network = api.getNetwork(153001);

      try {
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network/153001/getObject?objectMask=mask.subnets");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = getNetworkApi(server);

      try {
         assertNull(api.getNetwork(153001));
         assertSent(server, "GET", "/SoftLayer_Network/153001/getObject?objectMask=mask.subnets");
      } finally {
         server.shutdown();
      }
   }

   public void testGetName() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("testNet"));
      NetworkApi api = getNetworkApi(server);

      String name = api.getName(153001);

      try {
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network/153001/getName");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullName() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = getNetworkApi(server);

      try {
         assertNull(api.getName(153001));
         assertSent(server, "GET", "/SoftLayer_Network/153001/getName");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNotes() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("testNotes"));
      NetworkApi api = getNetworkApi(server);

      String notes = api.getNotes(153001);

      try {
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network/153001/getNotes");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullNotes() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = getNetworkApi(server);

      try {
         assertNull(api.getNotes(153001));
         assertSent(server, "GET", "/SoftLayer_Network/153001/getNotes");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateSubnet() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/network_createSubnet.json")));
      NetworkApi api = getNetworkApi(server);

      Subnet.CreateSubnet createSubnet = Subnet.CreateSubnet.builder()
              .networkIdentifier("192.168.1.0")
              .cidr(24)
              .note("testNet test create subnet")
              .build();

      Subnet.CreateDatacenterName createDatacenterName = Subnet.CreateDatacenterName.builder()
              .name("dal10.pod01")
              .build();

      List<Object> parameters = new ArrayList<Object>();
      parameters.add(createSubnet);
      parameters.add(createDatacenterName);

      Subnet subnet = api.createSubnet(153001, parameters);

      try {
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network/153001/createSubnet");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateSubnetOnNonExistingNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = getNetworkApi(server);

      Subnet.CreateSubnet createSubnet = Subnet.CreateSubnet.builder()
              .networkIdentifier("192.168.1.0")
              .cidr(24)
              .note("testNet test create subnet")
              .build();

      Subnet.CreateDatacenterName createDatacenterName = Subnet.CreateDatacenterName.builder()
              .name("dal10.pod01")
              .build();

      List<Object> parameters = new ArrayList<Object>();
      parameters.add(createSubnet);
      parameters.add(createDatacenterName);

      Subnet subnet = api.createSubnet(153001, parameters);

      try {
         assertNull(subnet);
         assertSent(server, "POST", "/SoftLayer_Network/153001/createSubnet");
      } finally {
         server.shutdown();
      }
   }

   public void testGetSubnets() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/network_subnet_get_1592631.json")));
      NetworkApi api = getNetworkApi(server);

      List<Subnet> subnets = api.getSubnets(153001);

      try {
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network/153001/getSubnets");
         assertEquals(subnets.size(), 1);
         assertEquals(subnets.get(0).id(), 1592631);
         assertEquals(subnets.get(0).cidr(), 28);
         assertEquals(subnets.get(0).networkIdentifier(), "192.168.253.0");
         assertEquals(subnets.get(0).note(), "This is a test subnet from NetworkApiMockTest");
      } finally {
         server.shutdown();
      }
   }

   public void testGetSubnetsOnNonExistingNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = getNetworkApi(server);

      try {
         assertTrue(api.getSubnets(153001).isEmpty());
         assertSent(server, "GET", "/SoftLayer_Network/153001/getSubnets");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteSubnet() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("true"));
      NetworkApi api = getNetworkApi(server);

      Subnet.DeleteSubnet deleteSubnet = Subnet.DeleteSubnet.builder()
              .id(1592631)
              .build();

      List<Subnet.DeleteSubnet> parameters = new ArrayList<Subnet.DeleteSubnet>();
      parameters.add(deleteSubnet);
      boolean deletion = api.deleteSubnet(153001, parameters);

      try {
         assertTrue(deletion, "deletion failed");

         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network/153001/deleteSubnet");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteNonExistingSubnet() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = getNetworkApi(server);

      Subnet.DeleteSubnet deleteSubnet = Subnet.DeleteSubnet.builder()
              .id(1592631)
              .build();

      List<Subnet.DeleteSubnet> parameters = new ArrayList<Subnet.DeleteSubnet>();
      parameters.add(deleteSubnet);
      boolean deletion = api.deleteSubnet(153001, parameters);

      try {
         assertFalse(deletion);
         assertSent(server, "POST", "/SoftLayer_Network/153001/deleteSubnet");
      } finally {
         server.shutdown();
      }
   }

   public void testEditNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("true"));
      NetworkApi api = getNetworkApi(server);

      Network.EditNetwork editNetwork = Network.EditNetwork.builder()
              .name("newNetworkApiLiveTestNetwork")
              .notes("new notes")
              .id(153001)
              .build();

      boolean edit = api.editNetwork(153001, ImmutableList.of(editNetwork));

      try {
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "PUT", "/SoftLayer_Network/153001/editObject");
      } finally {
         server.shutdown();
      }
   }

   public void testEditNullNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = getNetworkApi(server);

      Network.EditNetwork editNetwork = Network.EditNetwork.builder()
              .name("newNetworkApiLiveTestNetwork")
              .notes("new notes")
              .id(153001)
              .build();

      boolean edit = api.editNetwork(153001, ImmutableList.of(editNetwork));

      try {
         assertFalse(edit);
         assertSent(server, "PUT", "/SoftLayer_Network/153001/editObject");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("true"));
      NetworkApi api = getNetworkApi(server);

      boolean deletion = api.deleteNetwork(153001);

      try {
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "DELETE", "/SoftLayer_Network/153001");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteNonExistingNetwork() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      NetworkApi api = getNetworkApi(server);

      try {
         assertFalse(api.deleteNetwork(153001));
         assertSent(server, "DELETE", "/SoftLayer_Network/153001");
      } finally {
         server.shutdown();
      }
   }

   private NetworkApi getNetworkApi(MockWebServer server) {
      return api(SoftLayerApi.class, server.getUrl("/").toString(), new
              JavaUrlHttpCommandExecutorServiceModule(), new SoftLayerParserModule()).getNetworkApi();
   }
}
