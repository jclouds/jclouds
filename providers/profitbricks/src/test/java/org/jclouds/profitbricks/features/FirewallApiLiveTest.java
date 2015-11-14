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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.domain.Firewall.Protocol;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;


@Test(groups = "live", testName = "FirewallApiLiveTest")
public class FirewallApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Nic nic;

   private Firewall createdFirewall;

   @BeforeClass
   public void setupTest() {
      dataCenter = findOrCreateDataCenter("firewallApiLiveTest" + System.currentTimeMillis());
      nic = findOrCreateNic(dataCenter);
   }

   @Test
   public void testAddFirewallRuleToNic() {
      assertDataCenterAvailable(dataCenter);
      Firewall firewall = api.firewallApi().addFirewallRuleToNic(
              Firewall.Request.createAddRulePayload(
                      nic.id(), ImmutableList.of(
                              Firewall.Rule.builder()
                              .name("test-rule-tcp")
                              .protocol(Protocol.TCP)
                              .build()
                      )
              )
      );

      assertNotNull(firewall);
      assertFalse(firewall.rules().isEmpty());
      assertDataCenterAvailable(dataCenter);

      createdFirewall = firewall;
   }

   @Test(dependsOnMethods = "testAddFirewallRuleToNic")
   public void testGetAllFirewalls() {
      List<Firewall> firewalls = api.firewallApi().getAllFirewalls();

      assertNotNull(firewalls);
      assertFalse(firewalls.isEmpty());
   }

   @Test(dependsOnMethods = "testAddFirewallRuleToNic")
   public void testGetFirewall() {
      Firewall firewall = api.firewallApi().getFirewall(createdFirewall.id());

      assertNotNull(firewall);
      assertEquals(createdFirewall.id(), firewall.id());
   }

   @Test(dependsOnMethods = "testAddFirewallRuleToNic")
   public void testActivateFirewall() {
      assertDataCenterAvailable(dataCenter);
      boolean result = api.firewallApi().activateFirewall(
              ImmutableList.of(createdFirewall.id()));
      assertDataCenterAvailable(dataCenter);
      assertTrue(result);

      Firewall firewall = api.firewallApi().getFirewall(createdFirewall.id());
      assertTrue(firewall.active(), "Firewall wasn't activated");
   }

   @Test(dependsOnMethods = "testActivateFirewall")
   void testDeactivateFirewall() {
      assertDataCenterAvailable(dataCenter);
      boolean result = api.firewallApi().deactivateFirewall(
              ImmutableList.of(createdFirewall.id()));
      assertDataCenterAvailable(dataCenter);
      assertTrue(result);

      Firewall firewall = api.firewallApi().getFirewall(createdFirewall.id());
      assertFalse(firewall.active(), "Firewall wasn't deactivated");
   }

   @Test(dependsOnMethods = "testDeactivateFirewall")
   void testRemoveFirewallRule() {
      assertDataCenterAvailable(dataCenter);
      for (Firewall.Rule rule : createdFirewall.rules()) {
         boolean result = api.firewallApi().removeFirewallRules(
                 ImmutableList.of(rule.id()));

         assertTrue(result);
         assertDataCenterAvailable(dataCenter);

      }
      Firewall firewall = api.firewallApi().getFirewall(createdFirewall.id());
      assertTrue(firewall.rules().isEmpty(), "Not all rules removed");
   }

   @Test(dependsOnMethods = "testRemoveFirewallRule")
   public void testDeleteFirewall() {
      assertDataCenterAvailable(dataCenter);
      boolean result = api.firewallApi().deleteFirewall(ImmutableList.of(createdFirewall.id()));
      assertTrue(result, "Created firewall was not deleted.");
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      destroyDataCenter(dataCenter);
   }
}
