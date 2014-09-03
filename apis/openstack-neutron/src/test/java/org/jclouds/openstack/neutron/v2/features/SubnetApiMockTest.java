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
import org.jclouds.openstack.neutron.v2.domain.IPv6DHCPMode;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.domain.Subnets;
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
public class SubnetApiMockTest extends BaseNeutronApiMockTest {

   public void testCreateSubnet() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/subnet_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnet.CreateSubnet createSubnet = Subnet.createBuilder("1234567890", "10.0.3.0/24")
               .name("jclouds-wibble")
               .ipVersion(4)
               .build();

         Subnet subnet = api.create(createSubnet);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/subnets", "/subnet_create_request.json");

         /*
          * Check response
          */
         assertNotNull(subnet);
         assertEquals(subnet.getName(), "jclouds-wibble");
         assertEquals(subnet.getIpVersion().intValue(), 4);
         assertEquals(subnet.getCidr(), "10.0.3.0/24");
         assertEquals(subnet.getTenantId(), "1234567890");
         assertEquals(subnet.getId(), "624312ff-d14b-4ba3-9834-1c78d23d574d");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateSubnetFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnet.CreateSubnet createSubnet = Subnet.createBuilder("1234567890", "cidr")
               .name("jclouds-wibble")
               .ipVersion(4)
               .build();

         Subnet subnet = api.create(createSubnet);
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageSubnet() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/subnet_list_response_pages1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnets subnets = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/subnets?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(subnets);
         assertEquals(subnets.first().get().getId(), "16dba3bc-f3fa-4775-afdc-237e12c72f6a");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageSubnetFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnets subnets = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/subnets?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertTrue(subnets.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedSubnet() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/subnet_list_response_pages1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/subnet_list_response_pages2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         // Note: Lazy! Have to actually look at the collection.
         List<Subnet> subnets = api.list().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/subnets");
         assertRequest(server.takeRequest(), "GET", "/v2.0/subnets?marker=71c1e68c-171a-4aa2-aca5-50ea153a3718");

         /*
          * Check response
          */
         assertNotNull(subnets);
         assertEquals(subnets.size(), 4);
         assertEquals(subnets.get(0).getId(), "16dba3bc-f3fa-4775-afdc-237e12c72f6a");
         assertEquals(subnets.get(3).getId(), "6ba4c788-661f-49ab-9bf8-5f10cbbb2f57");
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedSubnetFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         // Note: Lazy! Have to actually look at the collection.
         List<Subnet> subnets = api.list().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/subnets");

         /*
          * Check response
          */
         assertTrue(subnets.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetSubnet() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/subnet_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnet subnet = api.get("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/subnets/12345");

         /*
          * Check response
          */
         assertNotNull(subnet);
         assertEquals(subnet.getName(), "jclouds-wibble");
         assertEquals(subnet.getId(), "624312ff-d14b-4ba3-9834-1c78d23d574d");
         assertEquals(subnet.getTenantId(), "1234567890");
         assertEquals(subnet.getIPv6AddressMode(), IPv6DHCPMode.SLAAC);
      } finally {
         server.shutdown();
      }
   }

   public void testGetSubnetFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnet subnet = api.get("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/subnets/12345");

         /*
          * Check response
          */
         assertNull(subnet);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateBulkSubnet() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/subnet_bulk_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnet.CreateSubnet createSubnet1 = Subnet.createBuilder("e6031bc2-901a-4c66-82da-f4c32ed89406",
               "192.168.199.0/24")
               .ipVersion(4)
               .build();

         Subnet.CreateSubnet createSubnet2 = Subnet.createBuilder("64239a54-dcc4-4b39-920b-b37c2144effa",
               "10.56.4.0/22")
               .ipVersion(4)
               .build();

         FluentIterable<Subnet> subnets = api.createBulk(ImmutableList.of(createSubnet1, createSubnet2));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/subnets", "/subnet_bulk_create_request.json");

         /*
          * Check response
          */
         assertNotNull(subnets);
         assertEquals(subnets.size(), 2);
         assertEquals(subnets.get(0).getName(), "");
         assertEquals(subnets.get(0).getIpVersion().intValue(), 4);
         assertEquals(subnets.get(0).getCidr(), "192.168.199.0/24");
         assertTrue(subnets.get(0).getDnsNameservers().isEmpty());
         assertTrue(subnets.get(0).getEnableDhcp());
         assertTrue(subnets.get(0).getHostRoutes().isEmpty());
         assertEquals(subnets.get(0).getTenantId(), "d19231fc08ec4bc4829b668040d34512");
         assertEquals(subnets.get(0).getId(), "0468a7a7-290d-4127-aedd-6c9449775a24");
         assertEquals(subnets.get(0).getNetworkId(), "e6031bc2-901a-4c66-82da-f4c32ed89406");
         assertEquals(subnets.get(0).getAllocationPools().iterator().next().getStart(), "192.168.199.2");
         assertEquals(subnets.get(0).getAllocationPools().iterator().next().getEnd(), "192.168.199.254");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateBulkSubnetFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnet.CreateSubnet createSubnet1 = Subnet.createBuilder("e6031bc2-901a-4c66-82da-f4c32ed89406",
               "192.168.199.0/24")
               .ipVersion(4)
               .build();

         Subnet.CreateSubnet createSubnet2 = Subnet.createBuilder("64239a54-dcc4-4b39-920b-b37c2144effa",
               "10.56.4.0/22")
               .ipVersion(4)
               .build();

         FluentIterable<Subnet> subnets = api.createBulk(ImmutableList.of(createSubnet1, createSubnet2));
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateSubnet() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/subnet_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnet.UpdateSubnet updateSubnet = Subnet.updateBuilder()
               .name("new_name")
               .gatewayIp("10.0.3.254")
               .build();

         Subnet subnet = api.update("12345", updateSubnet);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/subnets/12345", "/subnet_update_request.json");

         /*
          * Check response
          */
         assertNotNull(subnet);
         assertEquals(subnet.getName(), "new_name");
         assertEquals(subnet.getId(), "9436e561-47bf-436a-b1f1-fe23a926e031");
         assertEquals(subnet.getTenantId(), "c1210485b2424d48804aad5d39c61b8f");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateSubnetFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         Subnet.UpdateSubnet updateSubnet = Subnet.updateBuilder()
               .name("new_name")
               .gatewayIp("10.0.3.254")
               .build();

         Subnet subnet = api.update("12345", updateSubnet);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v2.0/subnets/12345", "/subnet_update_request.json");

         /*
          * Check response
          */
         assertNull(subnet);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteSubnet() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/subnets/12345");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteSubnetFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SubnetApi api = neutronApi.getSubnetApi("RegionOne");

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/subnets/12345");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }
}
