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
package org.jclouds.profitbricks.binder.firewall;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.jclouds.profitbricks.binder.firewall.FirewallBinder.ActivateFirewallRequestBinder;
import org.jclouds.profitbricks.binder.firewall.FirewallBinder.DeactivateFirewallRequestBinder;
import org.jclouds.profitbricks.binder.firewall.FirewallBinder.DeleteFirewallRequestBinder;
import org.jclouds.profitbricks.binder.firewall.FirewallBinder.RemoveFirewallRuleRequestBinder;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "FirewallBinderTest")
public class FirewallBinderTest {

   @Test
   public void testActivateFirewallBindPayload() {
      ActivateFirewallRequestBinder binder = new ActivateFirewallRequestBinder();

      List<String> payload = ImmutableList.of(
              "firewall-id-1",
              "firewall-id-2",
              "firewall-id-3",
              "firewall-id-4"
      );

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");

      String expected = ("<ws:activateFirewalls>\n"
              + "         <firewallIds>firewall-id-1</firewallIds>\n"
              + "         <firewallIds>firewall-id-2</firewallIds>\n"
              + "         <firewallIds>firewall-id-3</firewallIds>\n"
              + "         <firewallIds>firewall-id-4</firewallIds>\n"
              + "      </ws:activateFirewalls>").replaceAll("\\s+", "");

      assertEquals(actual, expected);
   }

   @Test
   public void testDeactivateFirewallBindPayload() {
      DeactivateFirewallRequestBinder binder = new DeactivateFirewallRequestBinder();

      List<String> payload = ImmutableList.of(
              "firewall-id-1",
              "firewall-id-2",
              "firewall-id-3",
              "firewall-id-4"
      );

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");

      String expected = ("<ws:deactivateFirewalls>\n"
              + "         <firewallIds>firewall-id-1</firewallIds>\n"
              + "         <firewallIds>firewall-id-2</firewallIds>\n"
              + "         <firewallIds>firewall-id-3</firewallIds>\n"
              + "         <firewallIds>firewall-id-4</firewallIds>\n"
              + "      </ws:deactivateFirewalls>").replaceAll("\\s+", "");

      assertEquals(actual, expected);
   }

   @Test
   public void testDeleteFirewallBindPayload() {
      DeleteFirewallRequestBinder binder = new DeleteFirewallRequestBinder();

      List<String> payload = ImmutableList.of(
              "firewall-id-1",
              "firewall-id-2",
              "firewall-id-3",
              "firewall-id-4"
      );

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");

      String expected = ("<ws:deleteFirewalls>\n"
              + "         <firewallIds>firewall-id-1</firewallIds>\n"
              + "         <firewallIds>firewall-id-2</firewallIds>\n"
              + "         <firewallIds>firewall-id-3</firewallIds>\n"
              + "         <firewallIds>firewall-id-4</firewallIds>\n"
              + "      </ws:deleteFirewalls>").replaceAll("\\s+", "");

      assertEquals(actual, expected);
   }

   @Test
   public void testRemoveFirewallRuleBindPayload() {
      RemoveFirewallRuleRequestBinder binder = new RemoveFirewallRuleRequestBinder();

      List<String> payload = ImmutableList.of(
              "firewall-rule-id-1",
              "firewall-rule-id-2",
              "firewall-rule-id-3",
              "firewall-rule-id-4"
      );

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");

      String expected = ("<ws:removeFirewallRules>\n"
              + "         <firewallRuleIds>firewall-rule-id-1</firewallRuleIds>\n"
              + "         <firewallRuleIds>firewall-rule-id-2</firewallRuleIds>\n"
              + "         <firewallRuleIds>firewall-rule-id-3</firewallRuleIds>\n"
              + "         <firewallRuleIds>firewall-rule-id-4</firewallRuleIds>\n"
              + "      </ws:removeFirewallRules>").replaceAll("\\s+", "");

      assertEquals(actual, expected);
   }

}
