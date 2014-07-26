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

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.ExternalGatewayInfo;
import org.jclouds.openstack.neutron.v2.domain.NetworkStatus;
import org.jclouds.openstack.neutron.v2.domain.Router;
import org.jclouds.openstack.neutron.v2.domain.RouterInterface;
import org.jclouds.openstack.neutron.v2.domain.Routers;
import org.jclouds.openstack.neutron.v2.extensions.RouterApi;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiMockTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Tests NetworkApi Guice wiring and parsing
 *
 */
@Test
public class RouterApiMockTest extends BaseNeutronApiMockTest {

   public void testCreateRouter() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/router_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         Router.CreateOptions createRouter = Router.createOptions().name("another_router").adminStateUp(true)
               .externalGatewayInfo(ExternalGatewayInfo.builder().networkId("8ca37218-28ff-41cb-9b10-039601ea7e6b").build())
               .build();

         Router router = api.create(createRouter);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/routers", "/router_create_request.json");

         /*
          * Check response
          */
         assertNotNull(router);
         assertEquals(router.getName(), "another_router");
         assertEquals(router.getExternalGatewayInfo().getNetworkId(), "8ca37218-28ff-41cb-9b10-039601ea7e6b");
         assertEquals(router.getStatus(), NetworkStatus.ACTIVE);
         assertEquals(router.isAdminStateUp().booleanValue(), true);
         assertEquals(router.getId(), "8604a0de-7f6b-409a-a47c-a1cc7bc77b2e");
         assertEquals(router.getTenantId(), "6b96ff0cb17a4b859e1e575d221683d3");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateRouterFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         Router.CreateOptions createRouter = Router.createOptions().name("another_router").adminStateUp(true)
               .externalGatewayInfo(ExternalGatewayInfo.builder().networkId("8ca37218-28ff-41cb-9b10-039601ea7e6b").build())
               .build();

         Router router = api.create(createRouter);
         fail("Should have failed with not found exception");

      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPagePort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/router_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         Routers routers = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/routers?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(routers);
         assertEquals(routers.size(), 2);
         assertEquals(routers.first().get().getId(), "a9254bdb-2613-4a13-ac4c-adc581fba50d");
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
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         Routers routers = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/routers?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(routers);
         assertTrue(routers.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedRouter() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/router_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/router_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<Router> routers = api.list().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/routers");
         assertRequest(server.takeRequest(), "GET", "/v2.0/routers?marker=71c1e68c-171a-4aa2-aca5-50ea153a3718");

         /*
          * Check response
          */
         assertNotNull(routers);
         assertEquals(routers.size(), 4);
         assertEquals(routers.get(0).getId(), "a9254bdb-2613-4a13-ac4c-adc581fba50d");
         assertEquals(routers.get(3).getId(), "a9254bdb-2613-4a13-ac4c-adc581fba50d_4");
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedRouterFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<Router> routers = api.list().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/routers");

         /*
          * Check response
          */
         assertNotNull(routers);
         assertTrue(routers.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetRouter() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/router_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         Router router = api.get("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/routers/12345");

         /*
          * Check response
          */
         assertNotNull(router);
         assertEquals(router.getName(), "router1");
         assertEquals(router.getExternalGatewayInfo().getNetworkId(), "3c5bcddd-6af9-4e6b-9c3e-c153e521cab8");
         assertEquals(router.getStatus(), NetworkStatus.ACTIVE);
         assertEquals(router.isAdminStateUp().booleanValue(), true);
         assertEquals(router.getId(), "a9254bdb-2613-4a13-ac4c-adc581fba50d");
         assertEquals(router.getTenantId(), "33a40233088643acb66ff6eb0ebea679");
      } finally {
         server.shutdown();
      }
   }

   public void testGetRouterFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         Router router = api.get("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/routers/12345");

         /*
          * Check response
          */
         assertNull(router);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateRouter() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/router_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         Router.UpdateOptions updateOptions = Router.updateOptions()
               .externalGatewayInfo(
                     ExternalGatewayInfo.builder().networkId("8ca37218-28ff-41cb-9b10-039601ea7e6b").build())
               .build();

         Router router = api.update("12345", updateOptions);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345", "/router_update_request.json");

         /*
          * Check response
          */
         assertNotNull(router);
         assertEquals(router.getName(), "another_router");
         assertEquals(router.getExternalGatewayInfo().getNetworkId(), "8ca37218-28ff-41cb-9b10-039601ea7e6b");
         assertEquals(router.getStatus(), NetworkStatus.ACTIVE);
         assertEquals(router.isAdminStateUp().booleanValue(), true);
         assertEquals(router.getId(), "8604a0de-7f6b-409a-a47c-a1cc7bc77b2e");
         assertEquals(router.getTenantId(), "6b96ff0cb17a4b859e1e575d221683d3");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateRouterFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         Router.UpdateOptions updateOptions = Router.updateOptions()
               .externalGatewayInfo(
                     ExternalGatewayInfo.builder().networkId("8ca37218-28ff-41cb-9b10-039601ea7e6b").build())
               .build();

         Router router = api.update("12345", updateOptions);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345", "/router_update_request.json");

         /*
          * Check response
          */
         assertNull(router);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteRouter() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/routers/12345");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteRouterFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/routers/12345");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testAddRouterInterfaceForSubnet() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/router_add_interface_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         RouterInterface routerInterface = api.addInterfaceForSubnet("12345", "a2f1f29d-571b-4533-907f-5803ab96ead1");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345/add_router_interface", "/router_add_interface_request.json");

         /*
          * Check response
          */
         assertNotNull(routerInterface);
         assertEquals(routerInterface.getSubnetId(), "a2f1f29d-571b-4533-907f-5803ab96ead1");
         assertEquals(routerInterface.getPortId(), "3a44f4e5-1694-493a-a1fb-393881c673a4");
      } finally {
         server.shutdown();
      }
   }

   public void testAddRouterInterfaceForSubnetFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         RouterInterface routerInterface = api.addInterfaceForSubnet("12345", "a2f1f29d-571b-4533-907f-5803ab96ead1");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345/add_router_interface", "/router_add_interface_request.json");

         /*
          * Check response
          */
         assertNull(routerInterface);
      } finally {
         server.shutdown();
      }
   }

   public void testAddRouterInterfaceForPort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/router_add_interface_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         RouterInterface routerInterface = api.addInterfaceForPort("12345", "portid");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345/add_router_interface", "/router_add_interface_port_request.json");

         /*
          * Check response
          */
         assertNotNull(routerInterface);
         assertEquals(routerInterface.getSubnetId(), "a2f1f29d-571b-4533-907f-5803ab96ead1");
         assertEquals(routerInterface.getPortId(), "3a44f4e5-1694-493a-a1fb-393881c673a4");
      } finally {
         server.shutdown();
      }
   }

   public void testAddRouterInterfaceForPortFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         RouterInterface routerInterface = api.addInterfaceForPort("12345", "portid");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345/add_router_interface", "/router_add_interface_port_request.json");

         /*
          * Check response
          */
         assertNull(routerInterface);
      } finally {
         server.shutdown();
      }
   }

   public void testRemoveRouterInterfaceForSubnet() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         boolean result = api.removeInterfaceForSubnet("12345", "a2f1f29d-571b-4533-907f-5803ab96ead1");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345/remove_router_interface", "/router_remove_interface_subnet_request.json");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testRemoveRouterInterfaceForSubnetFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         boolean result = api.removeInterfaceForSubnet("12345", "a2f1f29d-571b-4533-907f-5803ab96ead1");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345/remove_router_interface", "/router_remove_interface_subnet_request.json");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testRemoveRouterInterfaceForPort() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         boolean result = api.removeInterfaceForPort("12345", "portid");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345/remove_router_interface", "/router_remove_interface_port_request.json");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testRemoveRouterInterfaceForPortFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         RouterApi api = neutronApi.getRouterExtensionApi("RegionOne").get();

         boolean result = api.removeInterfaceForPort("12345", "portid");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/routers/12345/remove_router_interface", "/router_remove_interface_port_request.json");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }
}
