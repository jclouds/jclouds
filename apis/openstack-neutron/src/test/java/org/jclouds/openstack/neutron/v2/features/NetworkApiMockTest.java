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
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.NetworkStatus;
import org.jclouds.openstack.neutron.v2.domain.NetworkType;
import org.jclouds.openstack.neutron.v2.domain.Networks;
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

/**
 * Tests NetworkApi Guice wiring and parsing
 *
 */
@Test
public class NetworkApiMockTest extends BaseNeutronApiMockTest {

   public void testCreateNetwork() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/network_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Network.CreateNetwork createNetwork = Network.createBuilder("jclouds-wibble")
               .networkType(NetworkType.LOCAL)
               .build();

         Network network = api.create(createNetwork);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/networks", "/network_create_request.json");

         /*
          * Check response
          */
         assertNotNull(network);
         assertEquals(network.getName(), "jclouds-wibble");
         assertEquals(network.getNetworkType(), NetworkType.LOCAL);
         assertEquals(network.getTenantId(), "1234567890");
         assertEquals(network.getStatus(), NetworkStatus.ACTIVE);
         assertEquals(network.getId(), "624312ff-d14b-4ba3-9834-1c78d23d574d");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateNetworkFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/network_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Network.CreateNetwork createNetwork = Network.createBuilder("jclouds-wibble")
               .networkType(NetworkType.LOCAL)
               .build();

         Network network = api.create(createNetwork);
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageNetwork() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/network_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Networks networks = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/networks?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(networks);
         assertEquals(networks.first().get().getId(), "396f12f8-521e-4b91-8e21-2e003500433a");
      } finally {
         server.shutdown();
      }
   }

   // These fail tests uncover issues with the fallback annotations.
   public void testListSpecificPageNetworkFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/network_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Networks networks = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/networks?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertTrue(networks.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedNetwork() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/network_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/network_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         // Note: Lazy! Have to actually look at the collection.
         List<Network> networks = api.list().concat().toList();
         assertEquals(networks.size(), 4);
         // look at last element
         assertEquals(networks.get(3).getId(), "71c1e68c-171a-4aa2-aca5-50ea153a3718_2");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/networks");
         assertRequest(server.takeRequest(), "GET", "/v2.0/networks?marker=71c1e68c-171a-4aa2-aca5-50ea153a3718");

         /*
          * Check response
          */
         assertNotNull(networks);
         assertEquals(networks.get(0).getId(), "396f12f8-521e-4b91-8e21-2e003500433a");
         assertEquals(networks.get(3).getId(), "71c1e68c-171a-4aa2-aca5-50ea153a3718_2");
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedNetworkFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404).setBody(stringFromResource("/network_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         // Note: Lazy! Have to actually look at the collection.
         List<Network> networks = api.list().concat().toList();


         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/networks");

         /*
          * Check response
          */
         assertTrue(networks.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetNetwork() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/network_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Network network = api.get("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/networks/12345");

         /*
          * Check response
          */
         assertNotNull(network);
         assertEquals(network.getName(), "jclouds-wibble");
         assertEquals(network.getId(), "624312ff-d14b-4ba3-9834-1c78d23d574d");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNetworkFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Network network = api.get("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/networks/12345");

         /*
          * Check response
          */
         assertNull(network);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateNetworkBulk() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/network_bulk_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Network.CreateNetwork createNetwork1 = Network.createBuilder("jclouds-wibble")
               .networkType(NetworkType.LOCAL)
               .build();

         Network.CreateNetwork createNetwork2 = Network.createBuilder("jclouds-wibble2")
               .networkType(NetworkType.LOCAL)
               .build();

         FluentIterable<Network> networks = api.createBulk(ImmutableList.of(createNetwork1, createNetwork2));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/networks", "/network_bulk_create_request.json");

         /*
          * Check response
          */
         assertNotNull(networks);
         assertEquals(networks.size(), 2);
         assertEquals(networks.get(0).getName(), "jclouds-wibble");
         assertEquals(networks.get(0).getNetworkType(), NetworkType.LOCAL);
         assertEquals(networks.get(0).getTenantId(), "1234567890");
         assertEquals(networks.get(0).getStatus(), NetworkStatus.ACTIVE);
         assertEquals(networks.get(0).getId(), "624312ff-d14b-4ba3-9834-1c78d23d574d");

         assertEquals(networks.get(1).getName(), "jclouds-wibble2");
         assertEquals(networks.get(1).getNetworkType(), NetworkType.LOCAL);
         assertEquals(networks.get(1).getTenantId(), "1234567890");
         assertEquals(networks.get(1).getStatus(), NetworkStatus.ACTIVE);
         assertEquals(networks.get(1).getId(), "624312ff-d14b-4ba3-9834-1c78d23d574e");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateNetworkBulkFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Network.CreateNetwork createNetwork1 = Network.createBuilder("jclouds-wibble")
               .networkType(NetworkType.LOCAL)
               .build();

         Network.CreateNetwork createNetwork2 = Network.createBuilder("jclouds-wibble2")
               .networkType(NetworkType.LOCAL)
               .build();

         FluentIterable<Network> networks = api.createBulk(ImmutableList.of(createNetwork1, createNetwork2));
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateNetwork() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/network_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Network.UpdateNetwork updateNetwork = Network.updateBuilder()
               .name("jclouds-wibble-updated")
               .networkType(NetworkType.LOCAL)
               .build();

         Network network = api.update("123456", updateNetwork);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/networks/123456", "/network_update_request.json");

         /*
          * Check response
          */
         assertNotNull(network);
         assertEquals(network.getName(), "updated_name");
         assertEquals(network.getId(), "fc68ea2c-b60b-4b4f-bd82-94ec81110766");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateNetworkFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         Network.UpdateNetwork updateNetwork = Network.updateBuilder()
               .name("jclouds-wibble-updated")
               .networkType(NetworkType.LOCAL)
               .build();

         Network network = api.update("123456", updateNetwork);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/networks/123456");

         /*
          * Check response
          */
         assertNull(network);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteNetwork() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         boolean result = api.delete("123456");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/networks/123456");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteNetworkFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         NetworkApi api = neutronApi.getNetworkApi("RegionOne");

         boolean result = api.delete("123456");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/networks/123456");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }
}
