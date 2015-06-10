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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.Nic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.jclouds.profitbricks.compute.internal.ProvisioningStatusAware;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusPollingPredicate;
import org.jclouds.profitbricks.domain.Firewall.Protocol;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.util.Predicates2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

@Test(groups = "live", testName = "FirewallApiLiveTest", singleThreaded = true)
public class FirewallApiLiveTest extends BaseProfitBricksLiveTest {

   private Predicate<String> waitUntilAvailable;
   private Nic nic;

   private Firewall createdFirewall;
   private Firewall.Rule createdFirewallRule;

   @Override
   protected void initialize() {
      super.initialize();
      List<Nic> nics = api.nicApi().getAllNics();
      assertFalse(nics.isEmpty(), "Must atleast have 1 NIC available for firewall testing.");

      this.nic = Iterables.tryFind(nics, new Predicate<Nic>() {

         @Override
         public boolean apply(Nic input) {
            return input.state() == ProvisioningState.AVAILABLE;
         }
      }).orNull();

      assertNotNull(nic, "No available NIC for firewall testing was found.");

      this.waitUntilAvailable = Predicates2.retry(
              new ProvisioningStatusPollingPredicate(api, ProvisioningStatusAware.NIC, ProvisioningState.AVAILABLE),
              2l * 60l, 2l, TimeUnit.SECONDS);
   }

   @Test
   public void testAddFirewallRuleToNic() {
      Firewall firewall = api.firewallApi().addFirewallRuleToNic(
              Firewall.Request.ruleAddingBuilder()
              .nicId(nic.id())
              .newRule()
              .name("test-rule-tcp")
              .protocol(Protocol.TCP)
              .endRule()
              .build());

      assertNotNull(firewall);
      assertNotNull(firewall.rules());

      waitUntilAvailable.apply(nic.id());
      createdFirewall = firewall;
      createdFirewallRule = Iterables.getOnlyElement(firewall.rules());
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
      boolean result = api.firewallApi().activateFirewall(ImmutableList.of(createdFirewall.id()));

      waitUntilAvailable.apply(nic.id());

      assertTrue(result);
   }

   @Test(dependsOnMethods = "testActivateFirewall")
   void testDeactivateFirewall() {
      boolean result = api.firewallApi().deactivateFirewall(ImmutableList.of(createdFirewall.id()));

      waitUntilAvailable.apply(nic.id());

      assertTrue(result);
   }

   @Test(dependsOnMethods = "testActivateFirewall")
   void testRemoveFirewallRule() {
      boolean result = api.firewallApi().removeFirewallRules(ImmutableList.of(createdFirewallRule.id()));

      waitUntilAvailable.apply(nic.id());

      assertTrue(result);
   }

   @AfterClass(alwaysRun = true)
   public void testDeleteFirewall() {
      if (createdFirewall != null) {
         boolean result = api.firewallApi().deleteFirewall(ImmutableList.of(createdFirewall.id()));

         assertTrue(result, "Created firewall was not deleted.");
      }
   }
}
