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
package org.jclouds.profitbricks.features;

import static org.jclouds.profitbricks.internal.BaseProfitBricksMockTest.mockWebServer;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import java.util.List;

import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.Firewall.Protocol;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "live", testName = "FirewallApiMockTest", singleThreaded = true)
public class FirewallApiMockTest extends BaseProfitBricksMockTest {

   @Test
   public void testGetAllFirewalls() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/firewall/firewalls.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));

      FirewallApi api = pbApi.firewallApi();

      try {
         List<Firewall> firewalls = api.getAllFirewalls();
         assertRequestHasCommonProperties(server.takeRequest(), "<ws:getAllFirewalls/>");
         assertNotNull(firewalls);
         assertEquals(firewalls.size(), 2);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/firewall/firewall.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));

      FirewallApi api = pbApi.firewallApi();

      String id = "firewall-id";
      String firewallruleid = "firewall-rule-id";

      String content = "<ws:getFirewall><firewallId>" + id + "</firewallId></ws:getFirewall>";

      try {
         Firewall firewall = api.getFirewall(id);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(firewall);
         assertEquals(firewall.id(), id);
         assertFalse(firewall.rules().isEmpty());
         assertEquals(firewall.rules().get(0).id(), firewallruleid);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetNonExistingFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String id = "firewall-id";

      try {
         Firewall firewall = api.getFirewall(id);
         assertRequestHasCommonProperties(server.takeRequest());
         assertNull(firewall);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testAddFirewallRuleToNic() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/firewall/firewall-addtonic.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String content = "<ws:addFirewallRulesToNic>"
              + "<nicId>nic-id</nicId>"
              + "<request>"
              + "<name>name</name>"
              + "<portRangeEnd>45678</portRangeEnd>"
              + "<portRangeStart>12345</portRangeStart>"
              + "<protocol>TCP</protocol>"
              + "<sourceIp>192.168.0.1</sourceIp>"
              + "<sourceMac>aa:bb:cc:dd:ee:ff</sourceMac>"
              + "<targetIp>192.168.0.2</targetIp>"
              + "</request>"
              + "</ws:addFirewallRulesToNic>";
      try {
         Firewall.Request.AddRulePayload payload = Firewall.Request.createAddRulePayload(
                 "nic-id", ImmutableList.of(
                         Firewall.Rule.builder()
                         .name("name")
                         .portRangeEnd(45678)
                         .portRangeStart(12345)
                         .protocol(Protocol.TCP)
                         .sourceIp("192.168.0.1")
                         .sourceMac("aa:bb:cc:dd:ee:ff")
                         .targetIp("192.168.0.2")
                         .build()
                 ));
         Firewall response = api.addFirewallRuleToNic(payload);

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(response);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testRemoveFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/firewall/firewall-remove.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String firewallId = "12345";
      String content = "<ws:removeFirewallRules>"
              + "<firewallRuleIds>" + firewallId + "</firewallRuleIds>"
              + "</ws:removeFirewallRules>";

      try {
         boolean result = api.removeFirewallRules(ImmutableList.of(firewallId));
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertTrue(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testRemoveNonExitingFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String firewallRuleId = "12345";

      try {
         boolean result = api.removeFirewallRules(ImmutableList.of(firewallRuleId));
         assertRequestHasCommonProperties(server.takeRequest());
         assertFalse(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testActivateFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/firewall/firewall-activate.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String firewallId = "12345";
      String content = "<ws:activateFirewalls>"
              + "<firewallIds>" + firewallId + "</firewallIds>"
              + "</ws:activateFirewalls>";

      try {
         boolean result = api.activateFirewall(ImmutableList.of(firewallId));
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertTrue(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testActivateNonExitingFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String firewallId = "12345";

      try {
         boolean result = api.activateFirewall(ImmutableList.of(firewallId));
         assertRequestHasCommonProperties(server.takeRequest());
         assertFalse(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeactivateFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/firewall/firewall-deactivate.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String firewallId = "12345";
      String content = "<ws:deactivateFirewalls>"
              + "<firewallIds>" + firewallId + "</firewallIds>"
              + "</ws:deactivateFirewalls>";

      try {
         boolean result = api.deactivateFirewall(ImmutableList.of(firewallId));
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertTrue(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeactivateNonExitingFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String firewallId = "12345";

      try {
         boolean result = api.deactivateFirewall(ImmutableList.of(firewallId));
         assertRequestHasCommonProperties(server.takeRequest());
         assertFalse(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/firewall/firewall-delete.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String firewallId = "12345";
      String content = "<ws:deleteFirewalls>"
              + "<firewallIds>" + firewallId + "</firewallIds>"
              + "</ws:deleteFirewalls>";

      try {
         boolean result = api.deleteFirewall(ImmutableList.of(firewallId));
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertTrue(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteNonExitingFirewall() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      FirewallApi api = pbApi.firewallApi();

      String firewallId = "12345";

      try {
         boolean result = api.deleteFirewall(ImmutableList.of(firewallId));
         assertRequestHasCommonProperties(server.takeRequest());
         assertFalse(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }
}
