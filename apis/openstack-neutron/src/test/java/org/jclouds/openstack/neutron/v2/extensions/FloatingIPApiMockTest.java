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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.FloatingIP;
import org.jclouds.openstack.neutron.v2.domain.FloatingIPs;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiMockTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests Floating Api Guice wiring and parsing
 *
 */
@Test
public class FloatingIPApiMockTest extends BaseNeutronApiMockTest {

   public void testCreateFloatingIP() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/floatingip_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIP.CreateFloatingIP createFip = FloatingIP.createBuilder("376da547-b977-4cfe-9cba-275c80debf57")
               .portId("ce705c24-c1ef-408a-bda3-7bbd946164ab")
               .build();

         FloatingIP floatingIP = api.create(createFip);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/floatingips", "/floatingip_create_request.json");

         /*
          * Check response
          */
         assertNotNull(floatingIP);
         assertEquals(floatingIP.getRouterId(), "d23abc8d-2991-4a55-ba98-2aaea84cc72f");
         assertEquals(floatingIP.getTenantId(), "4969c491a3c74ee4af974e6d800c62de");
         assertEquals(floatingIP.getFloatingNetworkId(), "376da547-b977-4cfe-9cba-275c80debf57");
         assertEquals(floatingIP.getFixedIpAddress(), "10.0.0.3");
         assertEquals(floatingIP.getFloatingIpAddress(), "172.24.4.228");
         assertEquals(floatingIP.getPortId(), "ce705c24-c1ef-408a-bda3-7bbd946164ab");
         assertEquals(floatingIP.getId(), "2f245a7b-796b-4f26-9cf9-9e82d248fda7");

      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateFloatingIPFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIP.CreateFloatingIP createFip = FloatingIP.createBuilder("376da547-b977-4cfe-9cba-275c80debf57")
               .portId("ce705c24-c1ef-408a-bda3-7bbd946164ab")
               .build();

         FloatingIP floatingIP = api.create(createFip);
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageFloatingIP() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/floatingip_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIPs floatingIPs = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/floatingips?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(floatingIPs);
         assertEquals(floatingIPs.size(), 2);
         assertEquals(floatingIPs.first().get().getId(), "2f245a7b-796b-4f26-9cf9-9e82d248fda7");
         assertEquals(floatingIPs.get(1).getId(), "61cea855-49cb-4846-997d-801b70c71bdd");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageFloatingIPFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIPs floatingIPs = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/floatingips?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(floatingIPs);
         assertTrue(floatingIPs.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedFloatingIP() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/floatingip_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/floatingip_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<FloatingIP> floatingIPs = api.list().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/floatingips");
         assertRequest(server.takeRequest(), "GET", "/v2.0/floatingips?marker=71c1e68c-171a-4aa2-aca5-50ea153a3718");

         /*
          * Check response
          */
         assertNotNull(floatingIPs);
         assertEquals(floatingIPs.size(), 4);
         assertEquals(floatingIPs.get(0).getId(), "2f245a7b-796b-4f26-9cf9-9e82d248fda7");
         assertEquals(floatingIPs.get(3).getId(), "61cea855-49cb-4846-997d-801b70c71bdd2");
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedFloatingIPFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<FloatingIP> floatingIPs = api.list().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/floatingips");

         /*
          * Check response
          */
         assertNotNull(floatingIPs);
         assertTrue(floatingIPs.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetFloatingIP() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/floatingip_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIP floatingIP = api.get("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/floatingips/12345");

         /*
          * Check response
          */
         assertNotNull(floatingIP);
         assertEquals(floatingIP.getId(), "2f245a7b-796b-4f26-9cf9-9e82d248fda7");
         assertEquals(floatingIP.getPortId(), "ce705c24-c1ef-408a-bda3-7bbd946164ab");
         assertEquals(floatingIP.getFloatingIpAddress(), "172.24.4.228");
         assertEquals(floatingIP.getFixedIpAddress(), "10.0.0.3");
         assertEquals(floatingIP.getFloatingNetworkId(), "376da547-b977-4cfe-9cba-275c80debf57");
         assertEquals(floatingIP.getRouterId(), "d23abc8d-2991-4a55-ba98-2aaea84cc72f");
         assertEquals(floatingIP.getTenantId(), "4969c491a3c74ee4af974e6d800c62de");

      } finally {
         server.shutdown();
      }
   }

   public void testGetFloatingIPFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIP floatingIP = api.get("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/floatingips/12345");

         /*
          * Check response
          */
         assertNull(floatingIP);

      } finally {
         server.shutdown();
      }
   }

   public void testUpdateFloatingIP() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/floatingip_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIP.UpdateFloatingIP updateFloatingIP = FloatingIP.updateBuilder()
               .portId("fc861431-0e6c-4842-a0ed-e2363f9bc3a8")
               .build();

         FloatingIP floatingIP = api.update("12345", updateFloatingIP);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/floatingips/12345", "/floatingip_update_request.json");

         /*
          * Check response
          */
         assertNotNull(floatingIP);
         assertEquals(floatingIP.getPortId(), "fc861431-0e6c-4842-a0ed-e2363f9bc3a8");

      } finally {
         server.shutdown();
      }
   }

   public void testUpdateFloatingIPDissociate() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/floatingip_update_dissociate_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIP.UpdateFloatingIP updateFloatingIP = FloatingIP.updateBuilder().build();

         FloatingIP floatingIP = api.update("12345", updateFloatingIP);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/floatingips/12345", "/floatingip_update_dissociate_request.json");

         /*
          * Check response
          */
         assertNotNull(floatingIP);
         assertNull(floatingIP.getPortId());

      } finally {
         server.shutdown();
      }
   }

   public void testUpdateFloatingIPFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIP.UpdateFloatingIP updateFloatingIP = FloatingIP.updateBuilder()
               .portId("fc861431-0e6c-4842-a0ed-e2363f9bc3a8")
               .build();

         FloatingIP floatingIP = api.update("12345", updateFloatingIP);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/floatingips/12345", "/floatingip_update_request.json");

         /*
          * Check response
          */
         assertNull(floatingIP);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteFloatingIP() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/floatingips/12345");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteFloatingIPFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/floatingips/12345");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }
}
