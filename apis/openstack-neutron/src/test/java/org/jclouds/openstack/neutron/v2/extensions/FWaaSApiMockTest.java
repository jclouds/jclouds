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
import org.jclouds.openstack.neutron.v2.domain.CreateFirewall;
import org.jclouds.openstack.neutron.v2.domain.CreateFirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.CreateFirewallRule;
import org.jclouds.openstack.neutron.v2.domain.Firewall;
import org.jclouds.openstack.neutron.v2.domain.FirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.FirewallRule;
import org.jclouds.openstack.neutron.v2.domain.FloatingIP;
import org.jclouds.openstack.neutron.v2.domain.FloatingIPs;
import org.jclouds.openstack.neutron.v2.domain.UpdateFirewall;
import org.jclouds.openstack.neutron.v2.domain.UpdateFirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.UpdateFirewallRule;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiMockTest;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests Floating Api Guice wiring and parsing
 *
 */
@Test
public class FWaaSApiMockTest extends BaseNeutronApiMockTest {

   public void testCreateFirewall() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         CreateFirewall firewallRequest = CreateFirewall.builder()
                 .firewallPolicyId("c69933c1-b472-44f9-8226-30dc4ffd454c")
                 .adminStateUp(Boolean.TRUE)
                 .build();

         Firewall firewall = api.create(firewallRequest);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "POST", uriApiVersion + "/fw/firewalls", "/firewall_create_request.json");

         /*
          * Check response
          */
         assertNotNull(firewall);
         assertEquals(firewall.getName(), "");
         assertEquals(firewall.getTenantId(), "45977fa2dbd7482098dd68d0d8970117");
         assertEquals(firewall.getDescription(), "");
         assertEquals(firewall.getId(), "3b0ef8f4-82c7-44d4-a4fb-6177f9a21977");
         assertEquals(firewall.getStatus(), "PENDING_CREATE");
         assertTrue(firewall.isAdminStateUp());
         assertEquals(firewall.getFirewallPolicyId(), "c69933c1-b472-44f9-8226-30dc4ffd454c");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateFirewallFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         CreateFirewall firewallRequest = CreateFirewall.builder()
                 .firewallPolicyId("c69933c1-b472-44f9-8226-30dc4ffd454c")
                 .adminStateUp(Boolean.TRUE)
                 .build();

         Firewall firewall = api.create(firewallRequest);
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageFirewall() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/firewall_list_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         PaginatedCollection<Firewall> firewalls = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/fw/firewalls?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(firewalls);
         assertEquals(firewalls.size(), 1);
         assertEquals(Iterables.getFirst(firewalls, null).getId(), "5eb708e7-3856-449a-99ac-fec27cd745f9");
         assertEquals(firewalls.get(0).getId(), "5eb708e7-3856-449a-99ac-fec27cd745f9");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageFirewallFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FloatingIPApi api = neutronApi.getFloatingIPApi("RegionOne").get();

         FloatingIPs floatingIPs = api.list(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/floatingips?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(floatingIPs);
         assertTrue(floatingIPs.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedFirewall() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
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
         assertEquals(server.getRequestCount(), 4);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/floatingips");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/floatingips?marker=71c1e68c-171a-4aa2-aca5-50ea153a3718");

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

   public void testListPagedFirewallFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

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
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/floatingips");

         /*
          * Check response
          */
         assertNotNull(floatingIPs);
         assertTrue(floatingIPs.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testGetFirewall() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         Firewall firewall = api.get("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/fw/firewalls/12345");

         /*
          * Check response
          */
         assertNotNull(firewall);
         assertEquals(firewall.getId(), "3b0ef8f4-82c7-44d4-a4fb-6177f9a21977");
         assertEquals(firewall.getTenantId(), "45977fa2dbd7482098dd68d0d8970117");
         assertEquals(firewall.getDescription(), "");
         assertEquals(firewall.getName(), "");
         assertEquals(firewall.getStatus(), "ACTIVE");
         assertEquals(firewall.getFirewallPolicyId(), "c69933c1-b472-44f9-8226-30dc4ffd454c");

      } finally {
         server.shutdown();
      }
   }

   public void testGetFirewallFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         Firewall firewall = api.get("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/fw/firewalls/12345");

         /*
          * Check response
          */
         assertNull(firewall);

      } finally {
         server.shutdown();
      }
   }

   public void testUpdateFirewall() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         UpdateFirewall updateFirewall = UpdateFirewall.builder()
               .adminStateUp(false)
               .build();

         Firewall firewall = api.update("12345", updateFirewall);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "PUT", uriApiVersion + "/fw/firewalls/12345", "/firewall_update_request.json");

         /*
          * Check response
          */
         assertNotNull(firewall);
         assertFalse(firewall.isAdminStateUp());

      } finally {
         server.shutdown();
      }
   }

   public void testUpdateFirewallFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         UpdateFirewall updateFirewall = UpdateFirewall.builder()
                 .adminStateUp(false)
                 .build();

         Firewall firewall = api.update("12345", updateFirewall);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "PUT", uriApiVersion + "/fw/firewalls/12345", "/firewall_update_request.json");

         /*
          * Check response
          */
         assertNull(firewall);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteFirewall() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "DELETE", uriApiVersion + "/fw/firewalls/12345");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteFirewallFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         boolean result = api.delete("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "DELETE", uriApiVersion + "/fw/firewalls/12345");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateFirewallPolicy() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_policy_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         CreateFirewallPolicy firewallPolicyRequest = CreateFirewallPolicy.builder()
                 .name("jclouds-fw-policy_group-52-e8b")
                 .build();

         FirewallPolicy firewallPolicy = api.createFirewallPolicy(firewallPolicyRequest);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "POST", uriApiVersion + "/fw/firewall_policies", "/firewall_policy_create_request.json");

         /*
          * Check response
          */
         assertNotNull(firewallPolicy);
         assertEquals(firewallPolicy.getName(), "jclouds-fw-policy_group-52-e8b");
         assertEquals(firewallPolicy.getTenantId(), "3e00d5716204446c8d3c47a466eec25a");
         assertEquals(firewallPolicy.getDescription(), "");
         assertEquals(firewallPolicy.getId(), "12971159-95cf-4ca1-9baa-c82298ae0918");
         assertEquals(firewallPolicy.isShared(), false);
         assertEquals(firewallPolicy.getFirewallRules(), ImmutableList.of());
         assertEquals(firewallPolicy.isAudited(), false);
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateFirewallPolicyFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         CreateFirewallPolicy firewallPolicyRequest = CreateFirewallPolicy.builder()
                 .name("jclouds-fw-policy_group-52-e8b")
                 .build();

         FirewallPolicy firewallPolicy = api.createFirewallPolicy(firewallPolicyRequest);

      } finally {
         server.shutdown();
      }
   }

   public void testGetFirewallPolicy() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_policy_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         FirewallPolicy firewallPolicy = api.getFirewallPolicy("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/fw/firewall_policies/12345");

         /*
          * Check response
          */
         assertNotNull(firewallPolicy);
         assertEquals(firewallPolicy.getId(), "18d2f4e5-afdd-4c10-87ea-d35f38faf98c");
         assertEquals(firewallPolicy.getTenantId(), "e1defcdd823741c89afd5824040deed2");
         assertEquals(firewallPolicy.getDescription(), "");
         assertEquals(firewallPolicy.getName(), "myfirewallrule");
         assertEquals(firewallPolicy.isAudited(), false);
         assertEquals(firewallPolicy.isShared(), true);

      } finally {
         server.shutdown();
      }
   }

   public void testGetFirewallPolicyFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         FirewallPolicy firewallPolicy = api.getFirewallPolicy("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/fw/firewall_policies/12345");

         /*
          * Check response
          */
         assertNull(firewallPolicy);

      } finally {
         server.shutdown();
      }
   }

   public void testUpdateFirewallPolicy() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_policy_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         UpdateFirewallPolicy updateFirewallPolicy = UpdateFirewallPolicy.builder()
                 .shared(true)
                 .build();

         FirewallPolicy firewallPolicy = api.updateFirewallPolicy("12345", updateFirewallPolicy);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "PUT", uriApiVersion + "/fw/firewall_policies/12345");

         /*
          * Check response
          */
         assertNotNull(firewallPolicy);
         assertEquals(firewallPolicy.getId(), "18d2f4e5-afdd-4c10-87ea-d35f38faf98c");
         assertEquals(firewallPolicy.getTenantId(), "e1defcdd823741c89afd5824040deed2");
         assertEquals(firewallPolicy.getDescription(), "");
         assertEquals(firewallPolicy.getName(), "myfirewallrule");
         assertEquals(firewallPolicy.isAudited(), false);
         assertEquals(firewallPolicy.isShared(), true);

      } finally {
         server.shutdown();
      }
   }

   public void testUpdateFirewallPolicyFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         UpdateFirewallPolicy updateFirewallPolicy = UpdateFirewallPolicy.builder()
                 .shared(true)
                 .build();

         FirewallPolicy firewallPolicy = api.updateFirewallPolicy("12345", updateFirewallPolicy);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "PUT", uriApiVersion + "/fw/firewall_policies/12345");

         /*
          * Check response
          */
         assertNull(firewallPolicy);

      } finally {
         server.shutdown();
      }
   }

   public void testInsertFirewallRuleIntoFirewallPolicy() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_policy_insert_rule_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         FirewallPolicy updatedFirewallPolicy = api.insertFirewallRuleToPolicy("12345", "59585143-e819-48c9-944d-f03e0f049dba");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "PUT", uriApiVersion + "/fw/firewall_policies/12345/insert_rule", "/firewall_policy_insert_rule_request.json");

         /*
          * Check response
          */
         assertNotNull(updatedFirewallPolicy);

      } finally {
         server.shutdown();
      }
   }

   public void testInsertFirewallRuleIntoFirewallPolicyFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         FirewallPolicy updatedFirewallPolicy = api.insertFirewallRuleToPolicy("12345", "59585143-e819-48c9-944d-f03e0f049dba");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "PUT", uriApiVersion + "/fw/firewall_policies/12345/insert_rule", "/firewall_policy_insert_rule_request.json");

         /*
          * Check response
          */
         assertNull(updatedFirewallPolicy);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateFirewallRule() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_rule_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         CreateFirewallRule firewallRuleRequest = CreateFirewallRule.builder()
                 .name("jclouds-fw-rule_group-52-e8b_port-22")
                 .tenantId("3e00d5716204446c8d3c47a466eec25a")
                 .protocol("tcp")
                 .destinationIpAddress("192.168.0.117")
                 .destinationPort("22")
                 .action("allow")
                 .shared(false)
                 .enabled(true)
                 .build();

         FirewallRule firewallRule = api.createFirewallRule(firewallRuleRequest);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "POST", uriApiVersion + "/fw/firewall_rules", "/firewall_rule_create_request.json");

         /*
          * Check response
          */
         assertNotNull(firewallRule);
         assertEquals(firewallRule.getName(), "jclouds-fw-rule_group-52-e8b_port-22");
         assertEquals(firewallRule.getTenantId(), "3e00d5716204446c8d3c47a466eec25a");
         assertEquals(firewallRule.getDescription(), "");
         assertEquals(firewallRule.getId(), "59585143-e819-48c9-944d-f03e0f049dba");
         assertEquals(firewallRule.isShared(), false);
         assertEquals(firewallRule.isEnabled(), true);
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateFirewallRuleFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         CreateFirewallRule firewallRuleRequest = CreateFirewallRule.builder()
                 .name("jclouds-fw-rule_group-52-e8b_port-22")
                 .build();

         FirewallRule firewallRule = api.createFirewallRule(firewallRuleRequest);

      } finally {
         server.shutdown();
      }
   }

   public void testGetFirewallRule() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_rule_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         FirewallRule firewallRule = api.getFirewallRule("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/fw/firewall_rules/12345");

         /*
          * Check response
          */
         assertNotNull(firewallRule);
         assertEquals(firewallRule.getId(), "736b1686-3301-4a3d-9eaf-15e3c2682edc");
         assertEquals(firewallRule.getTenantId(), "3e00d5716204446c8d3c47a466eec25a");
         assertEquals(firewallRule.getDescription(), "jclouds test fw rule");
         assertEquals(firewallRule.getName(), "jclouds-test-org.jclouds.openstack.neutron.v2.extensions.fwaasapilivetest-fw-rule-22");
         assertEquals(firewallRule.getAction(), "allow");
         assertEquals(firewallRule.isEnabled(), true);
         assertEquals(firewallRule.getIpVersion().version(), 4);
         assertEquals(firewallRule.isShared(), false);

      } finally {
         server.shutdown();
      }
   }

   public void testGetFirewallRuleFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         FirewallRule firewallRule = api.getFirewallRule("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "GET", uriApiVersion + "/fw/firewall_rules/12345");

         /*
          * Check response
          */
         assertNull(firewallRule);

      } finally {
         server.shutdown();
      }
   }

   public void testUpdateFirewallRule() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(201).setBody(stringFromResource("/firewall_rule_update_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         UpdateFirewallRule updateFirewallRule = UpdateFirewallRule.builder()
                 .build();

         FirewallRule firewallRule = api.updateFirewallRule("12345", updateFirewallRule);

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "PUT", uriApiVersion + "/fw/firewall_rules/12345", "/firewall_rule_update_request.json");

         /*
          * Check response
          */
         assertNotNull(firewallRule);

      } finally {
         server.shutdown();
      }
   }

   public void testUpdateFirewallRuleFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         UpdateFirewallRule updateFirewallRule = UpdateFirewallRule.builder()
                 .build();

         FirewallRule firewallRule = api.updateFirewallRule("12345", updateFirewallRule);
         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "PUT", uriApiVersion + "/fw/firewall_rules/12345", "/firewall_rule_update_request.json");

         /*
          * Check response
          */
         assertNull(firewallRule);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteFirewallRule() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         boolean result = api.deleteFirewallRule("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "DELETE", uriApiVersion + "/fw/firewall_rules/12345");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteFirewallRuleFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list.json"))));
      server.enqueue(addCommonHeaders(
              new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         FWaaSApi api = neutronApi.getFWaaSApi("RegionOne").get();

         boolean result = api.deleteFirewallRule("12345");

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertExtensions(server, uriApiVersion + "");
         assertRequest(server.takeRequest(), "DELETE", uriApiVersion + "/fw/firewall_rules/12345");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

}
