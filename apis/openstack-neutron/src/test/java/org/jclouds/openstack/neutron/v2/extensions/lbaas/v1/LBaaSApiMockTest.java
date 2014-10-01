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
package org.jclouds.openstack.neutron.v2.extensions.lbaas.v1;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.HealthMonitor;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.HealthMonitors;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.HttpMethod;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.LBaaSStatus;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Member;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Members;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Pool;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Pools;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.ProbeType;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Protocol;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.SessionPersistence;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.VIP;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.VIPs;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiMockTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test
public class LBaaSApiMockTest extends BaseNeutronApiMockTest {

   public void testWhenNamespaceInExtensionsLBaaSPresent() throws IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         Optional<LBaaSApi> lbaasApiExtension = neutronApi.getLBaaSApi("RegionOne");

         assertAuthentication(server);

         /*
          * Check response
          */
         assertNotNull(lbaasApiExtension);
         assertEquals(lbaasApiExtension.isPresent(), true, "LBaaS API Version 1 is expected to be available");
      } finally {
         server.shutdown();
      }
   }

   public void testWhenNamespaceNotInExtensionsListLBaaSNotPresent() throws IOException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_without_lbaas_v1_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         Optional<LBaaSApi> lbaasApiExtension = neutronApi.getLBaaSApi("RegionOne");

         assertAuthentication(server);

         /*
          * Check response
          */
         assertNotNull(lbaasApiExtension);
         assertEquals(lbaasApiExtension.isPresent(), false, "LBaaS API Version 1 is expected to be unavailable");
      } finally {
         server.shutdown();
      }
   }

   public void testCreatePool() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/pool_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Pool.CreatePool createPool = Pool.createBuilder("8032909d-47a1-4715-90af-5153ffe39861", Protocol.TCP, Pool.ROUND_ROBIN)
               .name("NewPool").description(null).healthMonitors(null).provider(null).adminStateUp(null).build();

         Pool pool = lbaasApi.createPool(createPool);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "POST", "/v2.0/lb/pools", "/lbaas/v1/pool_create_request.json");

         /*
          * Check response
          */
         assertNotNull(pool);
         assertNotNull(pool.getId());
         assertEquals(pool.getTenantId(), "83657cfcdfe44cd5920adaf26c48ceea");
         assertNull(pool.getVIPId());
         assertEquals(pool.getName(), "NewPool");
         assertEquals(pool.getDescription(), "");
         assertEquals(pool.getSubnetId(), "8032909d-47a1-4715-90af-5153ffe39861");
         assertEquals(pool.getProtocol(), Protocol.TCP);
         assertEquals(pool.getProvider(), "HAPROXY");
         assertEquals(pool.getLBMethod(), Pool.ROUND_ROBIN);
         assertNotNull(pool.getMembers());
         assertTrue(pool.getMembers().isEmpty());
         assertNotNull(pool.getHealthMonitors());
         assertTrue(pool.getHealthMonitors().isEmpty());
         assertNotNull(pool.getHealthMonitorsStatus());
         assertTrue(pool.getHealthMonitorsStatus().isEmpty());
         assertEquals(pool.getAdminStateUp(), Boolean.TRUE);
         assertEquals(pool.getStatus(), LBaaSStatus.PENDING_CREATE);
         assertNull(pool.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreatePoolFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Pool.CreatePool createPool = Pool.createBuilder("8032909d-47a1-4715-90af-5153ffe39861", Protocol.TCP, Pool.ROUND_ROBIN)
               .name("NewPool").description(null).healthMonitors(null).provider(null).adminStateUp(null).build();

         lbaasApi.createPool(createPool);

         fail("Should have failed with not found exception");

      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPagePool() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/pool_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Pools pools = lbaasApi.listPools(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/pools?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(pools);
         assertEquals(pools.size(), 1);
         assertEquals(pools.first().get().getId(), "72741b06-df4d-4715-b142-276b6bce75ab");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPagePoolFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Pools pools = lbaasApi.listPools(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/pools?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(pools);
         assertTrue(pools.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedPool() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/lbaas/v1/pool_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/lbaas/v1/pool_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<Pool> pools = lbaasApi.listPools().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 4);
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/pools");
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/pools?marker=71c1e68c-171a-4aa2-aca5-50ea153a3718");

         /*
          * Check response
          */
         assertNotNull(pools);
         assertEquals(pools.size(), 2);
         assertEquals(pools.get(0).getId(), "72741b06-df4d-4715-b142-276b6bce75ab");
         assertEquals(pools.get(1).getId(), "72741b06-df4d-4715-b142-276b6bce75ab_2");
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedPoolFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<Pool> pools = lbaasApi.listPools().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/pools");

         /*
          * Check response
          */
         assertNotNull(pools);
         assertTrue(pools.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetPool() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/pool_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Pool pool = lbaasApi.getPool("72741b06-df4d-4715-b142-276b6bce75ab");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/pools/72741b06-df4d-4715-b142-276b6bce75ab");

         /*
          * Check response
          */
         assertNotNull(pool);
         assertEquals(pool.getId(), "72741b06-df4d-4715-b142-276b6bce75ab");
         assertEquals(pool.getTenantId(), "83657cfcdfe44cd5920adaf26c48ceea");
         assertNotNull(pool.getVIPId());
         assertEquals(pool.getName(), "app_pool");
         assertEquals(pool.getDescription(), "");
         assertEquals(pool.getSubnetId(), "8032909d-47a1-4715-90af-5153ffe39861");
         assertEquals(pool.getProtocol(), Protocol.HTTP);
         assertEquals(pool.getProvider(), "HAPROXY");
         assertEquals(pool.getLBMethod(), Pool.ROUND_ROBIN);
         assertNotNull(pool.getMembers());
         assertEquals(pool.getMembers().size(), 2);
         assertNotNull(pool.getHealthMonitors());
         assertEquals(pool.getHealthMonitors().size(), 2);
         assertNotNull(pool.getHealthMonitorsStatus());
         assertEquals(pool.getHealthMonitorsStatus().size(), 2);
         assertEquals(pool.getAdminStateUp(), Boolean.TRUE);
         assertEquals(pool.getStatus(), LBaaSStatus.ACTIVE);
         assertNull(pool.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   public void testGetPoolFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Pool pool = lbaasApi.getPool("72741b06-df4d-4715-b142-276b6bce75ab");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/pools/72741b06-df4d-4715-b142-276b6bce75ab");

         /*
          * Check response
          */
         assertNull(pool);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdatePool() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/pool_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Pool.UpdatePool updatePool = Pool.updateBuilder().name("new_name").description("new description").lbMethod("NEW_LB_METHOD")
               .healthMonitors(ImmutableSet.of("5d4b5228-33b0-4e60-b225-9b727c1a20e7")).adminStateUp(Boolean.FALSE).build();

         Pool pool = lbaasApi.updatePool("72741b06-df4d-4715-b142-276b6bce75ab", updatePool);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "PUT", "/v2.0/lb/pools/72741b06-df4d-4715-b142-276b6bce75ab", "/lbaas/v1/pool_update_request.json");

         /*
          * Check response
          */
         assertNotNull(pool);
         assertEquals(pool.getId(), "72741b06-df4d-4715-b142-276b6bce75ab");
         assertEquals(pool.getTenantId(), "83657cfcdfe44cd5920adaf26c48ceea");
         assertNotNull(pool.getVIPId());
         assertEquals(pool.getName(), "new_name");
         assertEquals(pool.getDescription(), "new description");
         assertEquals(pool.getSubnetId(), "8032909d-47a1-4715-90af-5153ffe39861");
         assertEquals(pool.getProtocol(), Protocol.HTTP);
         assertEquals(pool.getProvider(), "HAPROXY");
         assertEquals(pool.getLBMethod(), "NEW_LB_METHOD");
         assertNotNull(pool.getMembers());
         assertEquals(pool.getMembers().size(), 2);
         assertNotNull(pool.getHealthMonitors());
         assertEquals(pool.getHealthMonitors().size(), 1);
         assertEquals(pool.getHealthMonitors().iterator().next(), "5d4b5228-33b0-4e60-b225-9b727c1a20e7");
         assertNotNull(pool.getHealthMonitorsStatus());
         assertEquals(pool.getHealthMonitorsStatus().size(), 1);
         assertEquals(pool.getHealthMonitorsStatus().iterator().next().getId(), "5d4b5228-33b0-4e60-b225-9b727c1a20e7");
         assertEquals(pool.getAdminStateUp(), Boolean.FALSE);
         assertEquals(pool.getStatus(), LBaaSStatus.ACTIVE);
         assertNull(pool.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   public void testUpdatePoolFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Pool.UpdatePool updatePool = Pool.updateBuilder().name("new_name").description("new description").lbMethod("NEW_LB_METHOD")
               .healthMonitors(ImmutableSet.of("5d4b5228-33b0-4e60-b225-9b727c1a20e7")).adminStateUp(Boolean.FALSE).build();

         Pool pool = lbaasApi.updatePool("72741b06-df4d-4715-b142-276b6bce75ab", updatePool);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "PUT", "/v2.0/lb/pools/72741b06-df4d-4715-b142-276b6bce75ab", "/lbaas/v1/pool_update_request.json");

         /*
          * Check response
          */
         assertNull(pool);
      } finally {
         server.shutdown();
      }
   }

   public void testDeletePool() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.deletePool("72741b06-df4d-4715-b142-276b6bce75ab");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/pools/72741b06-df4d-4715-b142-276b6bce75ab");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeletePoolFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.deletePool("72741b06-df4d-4715-b142-276b6bce75ab");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/pools/72741b06-df4d-4715-b142-276b6bce75ab");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateMember() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/member_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Member.CreateMember createMember = Member.createBuilder("72741b06-df4d-4715-b142-276b6bce75ab", null, 80)
               .weight(null).adminStateUp(null).build();

         Member member = lbaasApi.createMember(createMember);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "POST", "/v2.0/lb/members", "/lbaas/v1/member_create_request.json");

         /*
          * Check response
          */
         assertNotNull(member);
         assertNotNull(member.getId());
         assertEquals(member.getTenantId(), "83657cfcdfe44cd5920adaf26c48ceea");
         assertEquals(member.getPoolId(), "72741b06-df4d-4715-b142-276b6bce75ab");
         assertNotNull(member.getAddress());
         assertEquals(member.getProtocolPort(), Integer.valueOf(80));
         assertEquals(member.getWeight(), Integer.valueOf(1));
         assertEquals(member.getAdminStateUp(), Boolean.TRUE);
         assertEquals(member.getStatus(), LBaaSStatus.PENDING_CREATE);
         assertNull(member.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateMemberFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Member.CreateMember createMember = Member.createBuilder("72741b06-df4d-4715-b142-276b6bce75ab", null, 80)
               .weight(null).adminStateUp(null).build();

         lbaasApi.createMember(createMember);

         fail("Should have failed with not found exception");

      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageMember() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/member_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Members members = lbaasApi.listMembers(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/members?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(members);
         assertEquals(members.size(), 1);
         assertEquals(members.first().get().getId(), "48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageMemberFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Members members = lbaasApi.listMembers(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/members?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(members);
         assertTrue(members.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedMember() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/lbaas/v1/member_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/lbaas/v1/member_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<Member> members = lbaasApi.listMembers().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 4);
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/members");
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/members?marker=396f12f8-521e-4b91-8e21-2e003500433a");

         /*
          * Check response
          */
         assertNotNull(members);
         assertEquals(members.size(), 2);
         assertEquals(members.get(0).getId(), "48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");
         assertEquals(members.get(1).getId(), "701b531b-111a-4f21-ad85-4795b7b12af6");
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedMemberFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<Member> members = lbaasApi.listMembers().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/members");

         /*
          * Check response
          */
         assertNotNull(members);
         assertTrue(members.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetMember() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/member_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Member member = lbaasApi.getMember("48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/members/48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");

         /*
          * Check response
          */
         assertNotNull(member);
         assertEquals(member.getId(), "48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");
         assertEquals(member.getTenantId(), "83657cfcdfe44cd5920adaf26c48ceea");
         assertEquals(member.getPoolId(), "72741b06-df4d-4715-b142-276b6bce75ab");
         assertEquals(member.getAddress(), "10.0.0.5");
         assertEquals(member.getProtocolPort(), Integer.valueOf(80));
         assertEquals(member.getWeight(), Integer.valueOf(1));
         assertEquals(member.getAdminStateUp(), Boolean.TRUE);
         assertEquals(member.getStatus(), LBaaSStatus.ACTIVE);
         assertNull(member.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   public void testGetMemberFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Member member = lbaasApi.getMember("48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/members/48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");

         /*
          * Check response
          */
         assertNull(member);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateMember() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/member_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Member.UpdateMember updateMember = Member.updateBuilder().poolId("new_pool_id").weight(2)
               .adminStateUp(Boolean.FALSE).build();

         Member member = lbaasApi.updateMember("48a471ea-64f1-4eb6-9be7-dae6bbe40a0f", updateMember);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "PUT", "/v2.0/lb/members/48a471ea-64f1-4eb6-9be7-dae6bbe40a0f", "/lbaas/v1/member_update_request.json");

         /*
          * Check response
          */
         assertNotNull(member);
         assertEquals(member.getId(), "48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");
         assertEquals(member.getTenantId(), "83657cfcdfe44cd5920adaf26c48ceea");
         assertEquals(member.getPoolId(), "new_pool_id");
         assertEquals(member.getAddress(), "10.0.0.5");
         assertEquals(member.getProtocolPort(), Integer.valueOf(80));
         assertEquals(member.getWeight(), Integer.valueOf(2));
         assertEquals(member.getAdminStateUp(), Boolean.FALSE);
         assertEquals(member.getStatus(), LBaaSStatus.ACTIVE);
         assertNull(member.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateMemberFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         Member.UpdateMember updateMember = Member.updateBuilder().poolId("new_pool_id").weight(2)
               .adminStateUp(Boolean.FALSE).build();

         Member member = lbaasApi.updateMember("48a471ea-64f1-4eb6-9be7-dae6bbe40a0f", updateMember);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "PUT", "/v2.0/lb/members/48a471ea-64f1-4eb6-9be7-dae6bbe40a0f", "/lbaas/v1/member_update_request.json");

         /*
          * Check response
          */
         assertNull(member);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteMember() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.deleteMember("48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/members/48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteMemberFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.deleteMember("48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/members/48a471ea-64f1-4eb6-9be7-dae6bbe40a0f");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateVIP() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/vip_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         VIP.CreateVIP createVIP = VIP.createBuilder("8032909d-47a1-4715-90af-5153ffe39861", Protocol.HTTP, 80, "61b1f87a-7a21-4ad3-9dda-7f81d249944f")
               .name("NewVip").description(null).address(null).sessionPersistence(null).connectionLimit(null).build();

         VIP vip = lbaasApi.createVIP(createVIP);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "POST", "/v2.0/lb/vips", "/lbaas/v1/vip_create_request.json");

         /*
          * Check response
          */
         assertNotNull(vip);
         assertNotNull(vip.getId());
         assertEquals(vip.getTenantId(), "83657cfcdfe44cd5920adaf26c48ceea");
         assertEquals(vip.getName(), "NewVip");
         assertEquals(vip.getDescription(), "");
         assertEquals(vip.getSubnetId(), "8032909d-47a1-4715-90af-5153ffe39861");
         assertEquals(vip.getAddress(), "10.0.0.11");
         assertEquals(vip.getPortId(), "f7e6fe6a-b8b5-43a8-8215-73456b32e0f5");
         assertEquals(vip.getProtocol(), Protocol.HTTP);
         assertEquals(vip.getProtocolPort(), Integer.valueOf(80));
         assertEquals(vip.getPoolId(), "61b1f87a-7a21-4ad3-9dda-7f81d249944f");
         assertNull(vip.getSessionPersistence());
         assertEquals(vip.getConnectionLimit(), Integer.valueOf(-1));
         assertEquals(vip.getAdminStateUp(), Boolean.TRUE);
         assertEquals(vip.getStatus(), LBaaSStatus.PENDING_CREATE);
         assertNull(vip.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateVIPFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         VIP.CreateVIP createVIP = VIP.createBuilder("8032909d-47a1-4715-90af-5153ffe39861", Protocol.HTTP, 80, "61b1f87a-7a21-4ad3-9dda-7f81d249944f")
               .name("NewVip").description(null).address(null).sessionPersistence(null).connectionLimit(null).build();

         lbaasApi.createVIP(createVIP);

         fail("Should have failed with not found exception");

      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageVIP() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/vip_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         VIPs vips = lbaasApi.listVIPs(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/vips?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(vips);
         assertEquals(vips.size(), 1);
         assertEquals(vips.first().get().getId(), "4ec89087-d057-4e2c-911f-60a3b47ee304");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageVIPFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         VIPs vips = lbaasApi.listVIPs(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/vips?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(vips);
         assertTrue(vips.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedVIP() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/lbaas/v1/vip_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/lbaas/v1/vip_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<VIP> vips = lbaasApi.listVIPs().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 4);
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/vips");
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/vips?marker=396f12f8-521e-4b91-8e21-2e003500433a");

         /*
          * Check response
          */
         assertNotNull(vips);
         assertEquals(vips.size(), 2);
         assertEquals(vips.get(0).getId(), "4ec89087-d057-4e2c-911f-60a3b47ee304");
         assertEquals(vips.get(1).getId(), "c987d2be-9a3c-4ac9-a046-e8716b1350e2");
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedVIPFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<VIP> vips = lbaasApi.listVIPs().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/vips");

         /*
          * Check response
          */
         assertNotNull(vips);
         assertTrue(vips.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetVIP() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/vip_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         VIP vip = lbaasApi.getVIP("4ec89087-d057-4e2c-911f-60a3b47ee304");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/vips/4ec89087-d057-4e2c-911f-60a3b47ee304");

         /*
          * Check response
          */
         assertNotNull(vip);
         assertNotNull(vip.getId());
         assertEquals(vip.getTenantId(), "83657cfcdfe44cd5920adaf26c48ceea");
         assertEquals(vip.getName(), "my-vip");
         assertEquals(vip.getDescription(), "");
         assertEquals(vip.getSubnetId(), "8032909d-47a1-4715-90af-5153ffe39861");
         assertEquals(vip.getAddress(), "10.0.0.10");
         assertEquals(vip.getPortId(), "b5a743d6-056b-468b-862d-fb13a9aa694e");
         assertEquals(vip.getProtocol(), Protocol.HTTP);
         assertEquals(vip.getProtocolPort(), Integer.valueOf(80));
         assertEquals(vip.getPoolId(), "72741b06-df4d-4715-b142-276b6bce75ab");
         SessionPersistence sessionPersistence = SessionPersistence.builder().type(SessionPersistence.Type.APP_COOKIE).cookieName("MyAppCookie").build();
         assertEquals(vip.getSessionPersistence(), sessionPersistence);
         assertEquals(vip.getConnectionLimit(), Integer.valueOf(1000));
         assertEquals(vip.getAdminStateUp(), Boolean.TRUE);
         assertEquals(vip.getStatus(), LBaaSStatus.ACTIVE);
         assertNull(vip.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   public void testGetVIPFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         VIP vip = lbaasApi.getVIP("4ec89087-d057-4e2c-911f-60a3b47ee304");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/vips/4ec89087-d057-4e2c-911f-60a3b47ee304");

         /*
          * Check response
          */
         assertNull(vip);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateVIP() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/vip_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         SessionPersistence sessionPersistence = SessionPersistence.builder().type(SessionPersistence.Type.APP_COOKIE).cookieName("MyNewAppCookie").build();
         VIP.UpdateVIP updateVIP = VIP.updateBuilder().name("new-name").description("new description").poolId("61b1f87a-7a21-4ad3-9dda-7f81d249944f")
               .sessionPersistence(sessionPersistence).connectionLimit(50).adminStateUp(Boolean.FALSE).build();

         VIP vip = lbaasApi.updateVIP("c987d2be-9a3c-4ac9-a046-e8716b1350e2", updateVIP);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "PUT", "/v2.0/lb/vips/c987d2be-9a3c-4ac9-a046-e8716b1350e2", "/lbaas/v1/vip_update_request.json");

         /*
          * Check response
          */
         assertNotNull(vip);
         assertNotNull(vip.getId());
         assertEquals(vip.getTenantId(), "83657cfcdfe44cd5920adaf26c48ceea");
         assertEquals(vip.getName(), "new-name");
         assertEquals(vip.getDescription(), "new description");
         assertEquals(vip.getSubnetId(), "8032909d-47a1-4715-90af-5153ffe39861");
         assertEquals(vip.getAddress(), "10.0.0.11");
         assertEquals(vip.getPortId(), "f7e6fe6a-b8b5-43a8-8215-73456b32e0f5");
         assertEquals(vip.getProtocol(), Protocol.HTTP);
         assertEquals(vip.getProtocolPort(), Integer.valueOf(80));
         assertEquals(vip.getPoolId(), "61b1f87a-7a21-4ad3-9dda-7f81d249944f");
         assertEquals(vip.getSessionPersistence(), sessionPersistence);
         assertEquals(vip.getConnectionLimit(), Integer.valueOf(50));
         assertEquals(vip.getAdminStateUp(), Boolean.FALSE);
         assertEquals(vip.getStatus(), LBaaSStatus.PENDING_UPDATE);
         assertNull(vip.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateVIPFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         SessionPersistence sessionPersistence = SessionPersistence.builder().type(SessionPersistence.Type.APP_COOKIE).cookieName("MyNewAppCookie").build();
         VIP.UpdateVIP updateVIP = VIP.updateBuilder().name("new-name").description("new description").poolId("61b1f87a-7a21-4ad3-9dda-7f81d249944f")
               .sessionPersistence(sessionPersistence).connectionLimit(50).adminStateUp(Boolean.FALSE).build();

         VIP vip = lbaasApi.updateVIP("c987d2be-9a3c-4ac9-a046-e8716b1350e2", updateVIP);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "PUT", "/v2.0/lb/vips/c987d2be-9a3c-4ac9-a046-e8716b1350e2", "/lbaas/v1/vip_update_request.json");

         /*
          * Check response
          */
         assertNull(vip);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteVIP() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.deleteVIP("c987d2be-9a3c-4ac9-a046-e8716b1350e2");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/vips/c987d2be-9a3c-4ac9-a046-e8716b1350e2");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteVIPFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.deleteVIP("c987d2be-9a3c-4ac9-a046-e8716b1350e2");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/vips/c987d2be-9a3c-4ac9-a046-e8716b1350e2");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateHealthMonitor() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/health_monitor_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         HealthMonitor.CreateHealthMonitor createHealthMonitor = HealthMonitor.createBuilder(ProbeType.HTTP, Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1))
               .httpMethod(null).urlPath(null).expectedCodes(null).adminStateUp(null).build();

         HealthMonitor healthMonitor = lbaasApi.createHealthMonitor(createHealthMonitor);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "POST", "/v2.0/lb/health_monitors", "/lbaas/v1/health_monitor_create_request.json");

         /*
          * Check response
          */
         assertNotNull(healthMonitor);
         assertNotNull(healthMonitor.getId());
         assertEquals(healthMonitor.getTenantId(), "4fd44f30292945e481c7b8a0c8908869");
         assertEquals(healthMonitor.getType(), ProbeType.HTTP);
         assertEquals(healthMonitor.getDelay(), Integer.valueOf(1));
         assertEquals(healthMonitor.getTimeout(), Integer.valueOf(1));
         assertEquals(healthMonitor.getMaxRetries(), Integer.valueOf(1));
         assertEquals(healthMonitor.getHttpMethod(), HttpMethod.GET);
         assertEquals(healthMonitor.getUrlPath(), "/");
         assertEquals(healthMonitor.getExpectedCodes(), "200");
         assertNotNull(healthMonitor.getPools());
         assertTrue(healthMonitor.getPools().isEmpty());
         assertEquals(healthMonitor.getAdminStateUp(), Boolean.TRUE);
         assertEquals(healthMonitor.getStatus(), LBaaSStatus.PENDING_CREATE);
         assertNull(healthMonitor.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateHealthMonitorFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         HealthMonitor.CreateHealthMonitor createHealthMonitor = HealthMonitor.createBuilder(ProbeType.HTTP, Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1))
               .httpMethod(null).urlPath(null).expectedCodes(null).adminStateUp(null).build();

         lbaasApi.createHealthMonitor(createHealthMonitor);

         fail("Should have failed with not found exception");

      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageHealthMonitor() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/health_monitor_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         HealthMonitors healthMonitors = lbaasApi.listHealthMonitors(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/health_monitors?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(healthMonitors);
         assertEquals(healthMonitors.size(), 1);
         assertEquals(healthMonitors.first().get().getId(), "466c8345-28d8-4f84-a246-e04380b0461d");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageHealthMonitorFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         HealthMonitors healthMonitors = lbaasApi.listHealthMonitors(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/health_monitors?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(healthMonitors);
         assertTrue(healthMonitors.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedHealthMonitor() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/lbaas/v1/health_monitor_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/lbaas/v1/health_monitor_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<HealthMonitor> healthMonitors = lbaasApi.listHealthMonitors().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 4);
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/health_monitors");
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/health_monitors?marker=396f12f8-521e-4b91-8e21-2e003500433a");

         /*
          * Check response
          */
         assertNotNull(healthMonitors);
         assertEquals(healthMonitors.size(), 2);
         assertEquals(healthMonitors.get(0).getId(), "466c8345-28d8-4f84-a246-e04380b0461d");
         assertEquals(healthMonitors.get(1).getId(), "5d4b5228-33b0-4e60-b225-9b727c1a20e7");
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedHealthMonitorFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<HealthMonitor> healthMonitors = lbaasApi.listHealthMonitors().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/health_monitors");

         /*
          * Check response
          */
         assertNotNull(healthMonitors);
         assertTrue(healthMonitors.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetHealthMonitor() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/health_monitor_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         HealthMonitor healthMonitor = lbaasApi.getHealthMonitor("5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/health_monitors/5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         /*
          * Check response
          */
         assertNotNull(healthMonitor);
         assertEquals(healthMonitor.getId(), "5d4b5228-33b0-4e60-b225-9b727c1a20e7");
         assertEquals(healthMonitor.getTenantId(), "4fd44f30292945e481c7b8a0c8908869");
         assertEquals(healthMonitor.getType(), ProbeType.HTTP);
         assertEquals(healthMonitor.getDelay(), Integer.valueOf(5));
         assertEquals(healthMonitor.getTimeout(), Integer.valueOf(2));
         assertEquals(healthMonitor.getMaxRetries(), Integer.valueOf(2));
         assertEquals(healthMonitor.getHttpMethod(), HttpMethod.GET);
         assertEquals(healthMonitor.getUrlPath(), "/");
         assertEquals(healthMonitor.getExpectedCodes(), "200");
         assertNotNull(healthMonitor.getPools());
         assertTrue(healthMonitor.getPools().isEmpty());
         assertEquals(healthMonitor.getAdminStateUp(), Boolean.TRUE);
         assertEquals(healthMonitor.getStatus(), LBaaSStatus.ACTIVE);
         assertNull(healthMonitor.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   public void testGetHealthMonitorFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         HealthMonitor healthMonitor = lbaasApi.getHealthMonitor("5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "GET", "/v2.0/lb/health_monitors/5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         /*
          * Check response
          */
         assertNull(healthMonitor);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateHealthMonitor() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/health_monitor_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         HealthMonitor.UpdateHealthMonitor updateHealthMonitor = HealthMonitor.updateBuilder().delay(Integer.valueOf(1)).timeout(Integer.valueOf(1)).maxRetries(Integer.valueOf(1))
               .httpMethod(HttpMethod.HEAD).urlPath("/index.html").expectedCodes("201").adminStateUp(Boolean.FALSE).build();

         HealthMonitor healthMonitor = lbaasApi.updateHealthMonitor("466c8345-28d8-4f84-a246-e04380b0461d", updateHealthMonitor);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "PUT", "/v2.0/lb/health_monitors/466c8345-28d8-4f84-a246-e04380b0461d", "/lbaas/v1/health_monitor_update_request.json");

         /*
          * Check response
          */
         assertNotNull(healthMonitor);
         assertEquals(healthMonitor.getId(), "466c8345-28d8-4f84-a246-e04380b0461d");
         assertEquals(healthMonitor.getTenantId(), "4fd44f30292945e481c7b8a0c8908869");
         assertEquals(healthMonitor.getType(), ProbeType.HTTP);
         assertEquals(healthMonitor.getDelay(), Integer.valueOf(1));
         assertEquals(healthMonitor.getTimeout(), Integer.valueOf(1));
         assertEquals(healthMonitor.getMaxRetries(), Integer.valueOf(1));
         assertEquals(healthMonitor.getHttpMethod(), HttpMethod.HEAD);
         assertEquals(healthMonitor.getUrlPath(), "/index.html");
         assertEquals(healthMonitor.getExpectedCodes(), "201");
         assertNotNull(healthMonitor.getPools());
         assertTrue(healthMonitor.getPools().isEmpty());
         assertEquals(healthMonitor.getAdminStateUp(), Boolean.FALSE);
         assertEquals(healthMonitor.getStatus(), LBaaSStatus.ACTIVE);
         assertNull(healthMonitor.getStatusDescription());
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateHealthMonitorFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         HealthMonitor.UpdateHealthMonitor updateHealthMonitor = HealthMonitor.updateBuilder().delay(Integer.valueOf(1)).timeout(Integer.valueOf(1)).maxRetries(Integer.valueOf(1))
               .httpMethod(HttpMethod.HEAD).urlPath("/index.html").expectedCodes("201").adminStateUp(Boolean.FALSE).build();

         HealthMonitor healthMonitor = lbaasApi.updateHealthMonitor("466c8345-28d8-4f84-a246-e04380b0461d", updateHealthMonitor);

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "PUT", "/v2.0/lb/health_monitors/466c8345-28d8-4f84-a246-e04380b0461d", "/lbaas/v1/health_monitor_update_request.json");

         /*
          * Check response
          */
         assertNull(healthMonitor);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteHealthMonitor() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.deleteHealthMonitor("466c8345-28d8-4f84-a246-e04380b0461d");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/health_monitors/466c8345-28d8-4f84-a246-e04380b0461d");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteHealthMonitorFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.deleteHealthMonitor("466c8345-28d8-4f84-a246-e04380b0461d");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/health_monitors/466c8345-28d8-4f84-a246-e04380b0461d");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testAssociateHealthMonitorWithPool() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201).setBody(stringFromResource("/lbaas/v1/pool_associate_health_monitor_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         HealthMonitor healthMonitor = lbaasApi.associateHealthMonitor("72741b06-df4d-4715-b142-276b6bce75ab", "5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "POST", "/v2.0/lb/pools/72741b06-df4d-4715-b142-276b6bce75ab/health_monitors", "/lbaas/v1/pool_associate_health_monitor_request.json");

         /*
          * Check response
          */
         assertNotNull(healthMonitor);
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAssociateHealthMonitorWithPoolFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         lbaasApi.associateHealthMonitor("72741b06-df4d-4715-b142-276b6bce75ab", "5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         fail("Should have failed with not found exception");

      } finally {
         server.shutdown();
      }
   }

   public void testDisassociateHealthMonitorFromPool() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.disassociateHealthMonitor("72741b06-df4d-4715-b142-276b6bce75ab", "5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/pools/72741b06-df4d-4715-b142-276b6bce75ab/health_monitors/5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDisassociateHealthMonitorFromPoolFail() throws IOException, InterruptedException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/extension_list_with_lbaas_v1_response.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         LBaaSApi lbaasApi = neutronApi.getLBaaSApi("RegionOne").get();

         boolean result = lbaasApi.disassociateHealthMonitor("72741b06-df4d-4715-b142-276b6bce75ab", "5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         /*
          * Check request
          */
         assertAuthentication(server);
         server.takeRequest();
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/lb/pools/72741b06-df4d-4715-b142-276b6bce75ab/health_monitors/5d4b5228-33b0-4e60-b225-9b727c1a20e7");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

}
