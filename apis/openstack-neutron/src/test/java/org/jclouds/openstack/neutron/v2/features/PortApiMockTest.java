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

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.AddressPair;
import org.jclouds.openstack.neutron.v2.domain.NetworkStatus;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.jclouds.openstack.neutron.v2.domain.Ports;
import org.jclouds.openstack.neutron.v2.domain.VIFType;
import org.jclouds.openstack.neutron.v2.domain.VNICType;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiMockTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests NetworkApi Guice wiring and parsing
 *
 */
@Test
public class PortApiMockTest extends BaseNeutronApiMockTest {

   public void testCreatePort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/port_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Port.CreatePort createPort = Port.createBuilder("6aeaf34a-c482-4bd3-9dc3-7faf36412f12")
               .name("port1")
               .adminStateUp(Boolean.TRUE)
               .deviceId("d6b4d3a5-c700-476f-b609-1493dd9dadc0")
               .allowedAddressPairs(ImmutableSet.of(AddressPair.builder("12", "111.222.333.444").build()))
               .build();

         Port port = api.create(createPort);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/ports", "/port_create_request.json");

         /*
          * Check response
          */
         assertNotNull(port);
         assertEquals(port.getAllowedAddressPairs().iterator().next().getIpAddress(), "192.168.1.1");
         assertEquals(port.getAllowedAddressPairs().iterator().next().getMacAddress(), "12:12");
         assertEquals(port.getName(), "port1");
         assertEquals(port.getStatus(), NetworkStatus.ACTIVE);
         assertEquals(port.getId(), "ebe69f1e-bc26-4db5-bed0-c0afb4afe3db");
         assertEquals(port.getDeviceId(), "d6b4d3a5-c700-476f-b609-1493dd9dadc0");
         assertEquals(port.getDeviceOwner(), "");
         assertEquals(port.getMacAddress(), "fa:16:3e:a6:50:c1");
         assertEquals(port.getNetworkId(), "6aeaf34a-c482-4bd3-9dc3-7faf36412f12");
         assertEquals(port.getFixedIps().iterator().next().getIpAddress(), "192.168.111.4");
         assertEquals(port.getFixedIps().iterator().next().getSubnetId(), "22b44fc2-4ffb-4de4-b0f9-69d58b37ae27");
         assertEquals(port.getTenantId(), "cf1a5775e766426cb1968766d0191908");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreatePortFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Port.CreatePort createPort = Port.createBuilder("6aeaf34a-c482-4bd3-9dc3-7faf36412f12")
               .name("port1")
               .adminStateUp(Boolean.TRUE)
               .deviceId("d6b4d3a5-c700-476f-b609-1493dd9dadc0")
               .allowedAddressPairs(ImmutableSet.of(AddressPair.builder("12", "111.222.333.444").build()))
               .build();

         Port port = api.create(createPort);
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPagePort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/port_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Ports ports = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/ports?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(ports);
         assertEquals(ports.size(), 2);
         assertEquals(ports.first().get().getId(), "24e6637e-c521-45fc-8b8b-d7331aa3c99f");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPagePortFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Ports ports = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/ports?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertTrue(ports.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedPort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/port_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/port_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         // Note: Lazy! Have to actually look at the collection.
         List<Port> ports = api.list().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/ports");
         assertRequest(server.takeRequest(), "GET", "/v2.0/ports?marker=71c1e68c-171a-4aa2-aca5-50ea153a3718");

         /*
          * Check response
          */
         assertNotNull(ports);
         assertEquals(ports.size(), 4);
         assertEquals(ports.get(0).getId(), "24e6637e-c521-45fc-8b8b-d7331aa3c99f");
         assertEquals(ports.get(3).getId(), "e54dfd9b-ce6e-47f7-af47-1609cfd1cdb0_4");
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedPortFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         // Note: Lazy! Have to actually look at the collection.
         List<Port> ports = api.list().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/ports");

         /*
          * Check response
          */
         assertTrue(ports.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testCreateBulkPort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/port_create_bulk_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Port.CreatePort createPort1 = Port.createBuilder("64239a54-dcc4-4b39-920b-b37c2144effa")
               .name("port1")
               .adminStateUp(Boolean.TRUE)
               .deviceId("24df1d04-d5cb-41e1-8de5-61ed77c558df")
               .securityGroups(ImmutableSet.of("dbc107f4-afcd-4d5a-9352-f68f82241d5b"))
               .build();

         Port.CreatePort createPort2 = Port.createBuilder("e6031bc2-901a-4c66-82da-f4c32ed89406")
               .name("port2")
               .adminStateUp(Boolean.FALSE)
               .securityGroups(
                     ImmutableSet.of("8bf3f7cc-8471-40b1-815f-9da47e79775b", "dbc107f4-afcd-4d5a-9352-f68f82241d5b"))
               .build();

         FluentIterable<Port> ports = api.createBulk(ImmutableList.of(createPort1, createPort2));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/ports", "/port_create_bulk_request.json");

         /*
          * Check response
          */
         assertNotNull(ports);
         assertEquals(ports.size(), 2);
         assertEquals(ports.get(0).getName(), "port1");
         assertEquals(ports.get(1).getName(), "port2");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateBulkPortFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Port.CreatePort createPort1 = Port.createBuilder("64239a54-dcc4-4b39-920b-b37c2144effa")
               .name("port1")
               .adminStateUp(Boolean.TRUE)
               .deviceId("24df1d04-d5cb-41e1-8de5-61ed77c558df")
               .securityGroups(ImmutableSet.of("dbc107f4-afcd-4d5a-9352-f68f82241d5b"))
               .build();

         Port.CreatePort createPort2 = Port.createBuilder("e6031bc2-901a-4c66-82da-f4c32ed89406")
               .name("port2")
               .adminStateUp(Boolean.FALSE)
               .securityGroups(
                     ImmutableSet.of("8bf3f7cc-8471-40b1-815f-9da47e79775b", "dbc107f4-afcd-4d5a-9352-f68f82241d5b"))
               .build();

         FluentIterable<Port> ports = api.createBulk(ImmutableList.of(createPort1, createPort2));
      } finally {
         server.shutdown();
      }
   }

   public void testGetPort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/port_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Port port = api.get("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/ports/12345");

         /*
          * Check response
          */
         assertNotNull(port);
         assertEquals(port.getName(), "jclouds-wibble");
         assertEquals(port.getStatus(), NetworkStatus.ACTIVE);
         assertEquals(port.getId(), "624312ff-d14b-4ba3-9834-1c78d23d574d");
         assertEquals(port.getTenantId(), "1234567890");
         assertEquals(port.getNetworkId(), "1234567890");
         assertEquals(port.getVnicType(), VNICType.NORMAL);
         assertEquals(port.getVifType(), VIFType.HYPERV);
         assertEquals(port.getVifDetails().get("name1"), "value1");
         assertEquals(((Map<String, Double>)port.getVifDetails().get("name2")).get("mapname2").intValue(), 3);
      } finally {
         server.shutdown();
      }
   }

   public void testGetPortFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Port port = api.get("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/ports/12345");

         /*
          * Check response
          */
         assertNull(port);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdatePort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/port_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Port.UpdatePort updatePort = Port.updateBuilder()
               .securityGroups(
                     ImmutableSet.of("85cc3048-abc3-43cc-89b3-377341426ac5", "c5ab5c29-2c99-44cb-a4b8-e70a88b77799"))
               .build();

         Port port = api.update("12345", updatePort);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/ports/12345", "/port_update_request.json");

         /*
          * Check response
          */
         assertNotNull(port);
         assertEquals(port.getId(), "1d8591f4-7b62-428e-857d-e82a15e5a7f1");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdatePortFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         Port.UpdatePort updatePort = Port.updateBuilder()
               .securityGroups(ImmutableSet.of("85cc3048-abc3-43cc-89b3-377341426ac5", "c5ab5c29-2c99-44cb-a4b8-e70a88b77799"))
               .build();

         Port port = api.update("12345", updatePort);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/ports/12345", "/port_update_request.json");

         /*
          * Check response
          */
         assertNull(port);
      } finally {
         server.shutdown();
      }
   }

   public void testDeletePort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/ports/12345");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeletePortFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         PortApi api = neutronApi.getPortApi("RegionOne");

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/ports/12345");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }
}
