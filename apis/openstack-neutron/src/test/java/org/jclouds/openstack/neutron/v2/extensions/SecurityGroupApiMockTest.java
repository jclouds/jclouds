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
import org.jclouds.openstack.neutron.v2.domain.Rule;
import org.jclouds.openstack.neutron.v2.domain.RuleDirection;
import org.jclouds.openstack.neutron.v2.domain.RuleEthertype;
import org.jclouds.openstack.neutron.v2.domain.RuleProtocol;
import org.jclouds.openstack.neutron.v2.domain.Rules;
import org.jclouds.openstack.neutron.v2.domain.SecurityGroup;
import org.jclouds.openstack.neutron.v2.domain.SecurityGroups;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiMockTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests NetworkApi Guice wiring and parsing
 *
 */
@Test
public class SecurityGroupApiMockTest extends BaseNeutronApiMockTest {

   public void testCreateSecurityGroup() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/security_group_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         SecurityGroup.CreateSecurityGroup createSecurityGroup = SecurityGroup.createBuilder().name("new-webservers")
               .description("security group for webservers")
               .build();

         SecurityGroup securityGroup = api.create(createSecurityGroup);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/security-groups", "/security_group_create_request.json");

         /*
          * Check response
          */
         assertNotNull(securityGroup);
         assertEquals(securityGroup.getId(), "2076db17-a522-4506-91de-c6dd8e837028");
         assertEquals(securityGroup.getTenantId(), "e4f50856753b4dc6afee5fa6b9b6c550");
         assertEquals(securityGroup.getName(), "new-webservers");
         assertEquals(securityGroup.getDescription(), "security group for webservers");

         Rule sgr0 = securityGroup.getRules().get(0);
         Rule sgr1 = securityGroup.getRules().get(1);

         assertEquals(sgr0.getId(), "38ce2d8e-e8f1-48bd-83c2-d33cb9f50c3d");
         assertEquals(sgr1.getId(), "565b9502-12de-4ffd-91e9-68885cff6ae1");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateSecurityGroupFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         SecurityGroup.CreateSecurityGroup createSecurityGroup = SecurityGroup.createBuilder().name("new-webservers")
               .description("security group for webservers")
               .build();

         SecurityGroup securityGroup = api.create(createSecurityGroup);
      } finally {
         server.shutdown();
      }
   }

   public void testCreateSecurityGroupRule() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/security_group_rule_create_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         Rule.CreateRule createSecurityGroupRule = Rule.createBuilder(
               RuleDirection.INGRESS, "a7734e61-b545-452d-a3cd-0189cbd9747a")
               .portRangeMin(80)
               .portRangeMax(80)
               .ethertype(RuleEthertype.IPV4)
               .protocol(RuleProtocol.TCP)
               .remoteGroupId("85cc3048-abc3-43cc-89b3-377341426ac5")
               .build();

         Rule rule = api.create(createSecurityGroupRule);

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/v2.0/security-group-rules", "/security_group_rule_create_request.json");

         /*
          * Check response
          */
         assertNotNull(rule);
         assertEquals(rule.getId(), "2bc0accf-312e-429a-956e-e4407625eb62");
         assertEquals(rule.getTenantId(), "e4f50856753b4dc6afee5fa6b9b6c550");
         assertEquals(rule.getDirection(), RuleDirection.INGRESS);
         assertEquals(rule.getPortRangeMax().intValue(), 80);
         assertEquals(rule.getPortRangeMin().intValue(), 80);
         assertEquals(rule.getEthertype(), RuleEthertype.IPV4);
         assertEquals(rule.getProtocol(), RuleProtocol.TCP);
         assertEquals(rule.getRemoteGroupId(), "85cc3048-abc3-43cc-89b3-377341426ac5");
         assertEquals(rule.getSecurityGroupId(), "a7734e61-b545-452d-a3cd-0189cbd9747a");

      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateSecurityGroupRuleFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         Rule.CreateRule createSecurityGroupRule = Rule.createBuilder(
               RuleDirection.INGRESS, "a7734e61-b545-452d-a3cd-0189cbd9747a")
               .portRangeMin(80)
               .portRangeMax(80)
               .ethertype(RuleEthertype.IPV4)
               .protocol(RuleProtocol.TCP)
               .remoteGroupId("85cc3048-abc3-43cc-89b3-377341426ac5")
               .build();

         Rule rule = api.create(createSecurityGroupRule);
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageSecurityGroup() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/security_group_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         SecurityGroups securityGroups = api.listSecurityGroups(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-groups?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(securityGroups);
         assertEquals(securityGroups.size(), 2);
         // Ensures the full collection is parsed and ordering is preserved.
         assertEquals(securityGroups.first().get().getId(), "85cc3048-abc3-43cc-89b3-377341426ac5");
         assertEquals(securityGroups.get(1).getId(), "85cc3048-abc3-43cc-89b3-377341426ac52");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageSecurityGroupFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         SecurityGroups securityGroups = api.listSecurityGroups(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-groups?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(securityGroups);
         assertTrue(securityGroups.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageSecurityGroupRule() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/security_group_rule_list_response_paged1.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         Rules rules = api.listRules(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-group-rules?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(rules);
         assertEquals(rules.size(), 4);
         // Ensures the full collection is parsed and ordering is preserved.
         assertEquals(rules.first().get().getId(), "3c0e45ff-adaf-4124-b083-bf390e5482ff");
         assertEquals(rules.get(3).getId(), "f7d45c89-008e-4bab-88ad-d6811724c51c");
      } finally {
         server.shutdown();
      }
   }

   public void testListSpecificPageSecurityGroupRuleFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         Rules rules = api.listRules(PaginationOptions.Builder.limit(2).marker("abcdefg"));

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-group-rules?limit=2&marker=abcdefg");

         /*
          * Check response
          */
         assertNotNull(rules);
         assertTrue(rules.isEmpty());
      } finally {
         server.shutdown();
      }
   }

   public void testListPagedSecurityGroups() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/security_group_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/security_group_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<SecurityGroup> securityGroups = api.listSecurityGroups().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-groups");
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-groups?marker=71c1e68c-171a-4aa2-aca5-50ea153a3718");

         /*
          * Check response
          */
         assertNotNull(securityGroups);
         assertEquals(securityGroups.size(), 4);
         // Ensures ordering is preserved and both pages are parsed.
         assertEquals(securityGroups.get(0).getId(), "85cc3048-abc3-43cc-89b3-377341426ac5");
         assertEquals(securityGroups.get(3).getId(), "85cc3048-abc3-43cc-89b3-377341426ac524");

      } finally {
         server.shutdown();
      }
   }

   public void testListPagedSecurityGroupsFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<SecurityGroup> securityGroups = api.listSecurityGroups().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-groups");

         /*
          * Check response
          */
         assertNotNull(securityGroups);
         assertTrue(securityGroups.isEmpty());

      } finally {
         server.shutdown();
      }
   }

   public void testListPagedSecurityGroupRules() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/security_group_rule_list_response_paged1.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(200).setBody(stringFromResource("/security_group_rule_list_response_paged2.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<Rule> rules = api.listRules().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-group-rules");
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-group-rules?marker=71c1e68c-171a-4aa2-aca5-50ea153a3718");

         /*
          * Check response
          */
         assertNotNull(rules);
         assertEquals(rules.size(), 8);
         // Ensures both pages are tested and ordering is preserved.
         assertEquals(rules.get(0).getId(), "3c0e45ff-adaf-4124-b083-bf390e5482ff");
         assertEquals(rules.get(7).getId(), "f7d45c89-008e-4bab-88ad-d6811724c51c2");

      } finally {
         server.shutdown();
      }
   }

   public void testListPagedSecurityGroupRulesFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         // Note: Lazy! Have to actually look at the collection.
         List<Rule> rules = api.listRules().concat().toList();

         /*
          * Check request
          */
         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-group-rules");

         /*
          * Check response
          */
         assertNotNull(rules);
         assertTrue(rules.isEmpty());

      } finally {
         server.shutdown();
      }
   }

   public void testGetSecurityGroup() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/security_group_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         SecurityGroup securityGroup = api.getSecurityGroup("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-groups/12345");

         /*
          * Check response
          */
         assertNotNull(securityGroup);
         assertEquals(securityGroup.getName(), "default");
         assertEquals(securityGroup.getDescription(), "default");
         assertEquals(securityGroup.getId(), "85cc3048-abc3-43cc-89b3-377341426ac5");
         assertEquals(securityGroup.getTenantId(), "e4f50856753b4dc6afee5fa6b9b6c550");
         Rule sgr = securityGroup.getRules().get(0);
         assertEquals(sgr.getId(), "3c0e45ff-adaf-4124-b083-bf390e5482ff");
      } finally {
         server.shutdown();
      }
   }

   public void testGetSecurityGroupFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         SecurityGroup securityGroup = api.getSecurityGroup("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-groups/12345");

         /*
          * Check response
          */
         assertNull(securityGroup);

      } finally {
         server.shutdown();
      }
   }

   public void testGetSecurityGroupRule() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201).setBody(stringFromResource("/security_group_rule_get_response.json"))));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         Rule rule = api.get("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-group-rules/12345");

         /*
          * Check response
          */
         assertNotNull(rule);
         assertEquals(rule.getDirection(), RuleDirection.EGRESS);
         assertEquals(rule.getEthertype(), RuleEthertype.IPV6);
         assertEquals(rule.getId(), "3c0e45ff-adaf-4124-b083-bf390e5482ff");
         assertEquals(rule.getTenantId(), "e4f50856753b4dc6afee5fa6b9b6c550");
         assertEquals(rule.getSecurityGroupId(), "85cc3048-abc3-43cc-89b3-377341426ac5");

      } finally {
         server.shutdown();
      }
   }

   public void testGetSecurityGroupRuleFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         Rule rule = api.get("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v2.0/security-group-rules/12345");

         /*
          * Check response
          */
         assertNull(rule);

      } finally {
         server.shutdown();
      }
   }

   public void testDeleteSecurityGroup() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         boolean result = api.deleteSecurityGroup("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/security-groups/12345");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteSecurityGroupFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         boolean result = api.deleteSecurityGroup("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/security-groups/12345");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteSecurityGroupRule() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(201)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         boolean result = api.deleteRule("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/security-group-rules/12345");

         /*
          * Check response
          */
         assertTrue(result);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteSecurityGroupRuleFail() throws IOException, InterruptedException, URISyntaxException {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(
            new MockResponse().setResponseCode(404)));

      try {
         NeutronApi neutronApi = api(server.getUrl("/").toString(), "openstack-neutron", overrides);
         SecurityGroupApi api = neutronApi.getSecurityGroupApi("RegionOne").get();

         boolean result = api.deleteRule("12345");

         /*
          * Check request
          */
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v2.0/security-group-rules/12345");

         /*
          * Check response
          */
         assertFalse(result);
      } finally {
         server.shutdown();
      }
   }
}
