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
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.neutron.v2.domain.CreateFirewall;
import org.jclouds.openstack.neutron.v2.domain.CreateFirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.CreateFirewallRule;
import org.jclouds.openstack.neutron.v2.domain.Firewall;
import org.jclouds.openstack.neutron.v2.domain.FirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.FirewallRule;
import org.jclouds.openstack.neutron.v2.domain.UpdateFirewall;
import org.jclouds.openstack.neutron.v2.domain.UpdateFirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.UpdateFirewallRule;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiLiveTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Tests parsing and Guice wiring of FWaaSApi
 */
@Test(groups = "live", testName = "FWaaSApiLiveTest")
public class FWaaSApiLiveTest extends BaseNeutronApiLiveTest {

   private FWaaSApi fWaaSApi;

   @BeforeMethod
   void setUp() {
      Optional<String> optionalRegion = Iterables.tryFind(api.getConfiguredRegions(), Predicates.notNull());
      if (!optionalRegion.isPresent()) Assert.fail();
      fWaaSApi = api.getFWaaSApi(optionalRegion.get()).get();
   }
   /**
    * Smoke test for the Firewall extension for Neutron
    */
   public void testCreateUpdateAndDeleteFirewallRule() {

      String inboundPort = "22";
      FirewallRule firewallRule = null;

      try {
         // methods under test
         firewallRule = fWaaSApi.createFirewallRule(CreateFirewallRule.builder()
                 .name(String.format("jclouds-test-%s-fw-rule-%s", this.getClass().getCanonicalName().toLowerCase(), inboundPort))
                 .description("jclouds test fw rule")
                 .destinationIpAddress("192.168.0.1")
                 .destinationPort(inboundPort)
                 .enabled(true)
                 .action("allow")
                 .protocol("tcp")
                 .build());

         assertFalse(fWaaSApi.listFirewallRules().concat().toList().isEmpty());
         assertNotNull(fWaaSApi.listFirewallRules(PaginationOptions.Builder.limit(1)));

         // get
         firewallRule = fWaaSApi.getFirewallRule(firewallRule.getId());
         assertEquals(firewallRule.getName(), String.format("jclouds-test-%s-fw-rule-%s", this.getClass().getCanonicalName().toLowerCase(), inboundPort));
         assertEquals(firewallRule.getDescription(), "jclouds test fw rule");

         // update
         FirewallRule updatedFirewallRule = fWaaSApi.updateFirewallRule(firewallRule.getId(), UpdateFirewallRule.builder().name(firewallRule.getName() + "-updated").build());
         firewallRule = fWaaSApi.getFirewallRule(firewallRule.getId());
         assertEquals(updatedFirewallRule, firewallRule);
      } finally {
         // delete
         if (fWaaSApi != null) {
            assertTrue(fWaaSApi.deleteFirewallRule(firewallRule.getId()));
         }
      }
   }

   public void testCreateUpdateAndDeleteFirewallPolicy() {

      String inboundPort = "80";
      FirewallRule firewallRule = fWaaSApi.createFirewallRule(CreateFirewallRule.builder()
              .name(String.format("jclouds-test-%s-fw-rule-%s", this.getClass().getCanonicalName().toLowerCase(), inboundPort))
              .description("jclouds test fw rule")
              .destinationIpAddress("192.168.0.1")
              .destinationPort(inboundPort)
              .enabled(true)
              .action("allow")
              .protocol("tcp")
              .build());
      FirewallPolicy firewallPolicy = null;

      try {
         // methods under test
         firewallPolicy = fWaaSApi.createFirewallPolicy(CreateFirewallPolicy.builder()
                 .name(String.format("jclouds-test-%s-fw-policy", this.getClass().getCanonicalName().toLowerCase()))
                 .description("jclouds test fw policy")
                 .build());

         assertFalse(fWaaSApi.listFirewallPolicies().concat().toList().isEmpty());
         assertNotNull(fWaaSApi.listFirewallPolicies(PaginationOptions.Builder.limit(1)));

         // get
         firewallPolicy = fWaaSApi.getFirewallPolicy(firewallPolicy.getId());
         assertEquals(firewallPolicy.getName(), String.format("jclouds-test-%s-fw-policy", this.getClass().getCanonicalName().toLowerCase()));
         assertEquals(firewallPolicy.getDescription(), "jclouds test fw policy");

         // update
         FirewallPolicy updatedFirewallPolicy = fWaaSApi.updateFirewallPolicy(firewallPolicy.getId(), UpdateFirewallPolicy.builder()
                 .name(String.format("jclouds-test-%s-fw-policy-update", this.getClass().getCanonicalName().toLowerCase())).build());
         firewallPolicy = fWaaSApi.getFirewallPolicy(firewallPolicy.getId());
         assertEquals(updatedFirewallPolicy, firewallPolicy);

         firewallPolicy = fWaaSApi.insertFirewallRuleToPolicy(firewallPolicy.getId(), firewallRule.getId());
         assertNotNull(firewallPolicy);
         assertFalse(firewallPolicy.getFirewallRules().isEmpty());

         firewallPolicy = fWaaSApi.removeFirewallRuleFromPolicy(firewallPolicy.getId(), firewallRule.getId());
         assertNotNull(firewallPolicy);
         assertTrue(firewallPolicy.getFirewallRules().isEmpty());
      } finally {
         // delete
         if (fWaaSApi != null) {
            try {
               if (firewallPolicy != null) {
                  assertTrue(fWaaSApi.deleteFirewallPolicy(firewallPolicy.getId()));
               }
            } finally {
               assertTrue(fWaaSApi.deleteFirewallRule(firewallRule.getId()));
            }
         }
      }
   }

   public void testCreateUpdateAndDeleteFirewall() {

      FirewallPolicy firewallPolicy = fWaaSApi.createFirewallPolicy(CreateFirewallPolicy.builder()
              .name(String.format("jclouds-test-%s-fw-policy", this.getClass().getCanonicalName().toLowerCase()))
              .description("jclouds test fw policy")
              .build());

      Firewall firewall = null;

      try {
         // methods under test
         firewall = fWaaSApi.create(CreateFirewall.builder().name(String.format("jclouds-test-%s-fw", this.getClass().getCanonicalName().toLowerCase()))
                 .description("jclouds test firewall")
                 .firewallPolicyId(firewallPolicy.getId())
                 .build());

         assertFalse(fWaaSApi.list().concat().toList().isEmpty());
         assertNotNull(fWaaSApi.list(PaginationOptions.Builder.limit(1)));

         // get
         firewall = fWaaSApi.get(firewall.getId());
         assertEquals(firewall.getName(), String.format("jclouds-test-%s-fw", this.getClass().getCanonicalName().toLowerCase()));
         assertEquals(firewall.getDescription(), "jclouds test firewall");

         // update
         Firewall updatedFirewall = fWaaSApi.update(firewall.getId(), UpdateFirewall.builder().name(String.format("jclouds-test-%s-fw_updated", this.getClass()
                 .getCanonicalName().toLowerCase())).build());
         firewall = fWaaSApi.get(firewall.getId());
         assertEquals(updatedFirewall, firewall);

      } finally {
         // delete
         if (fWaaSApi != null) {
            try {
               if (firewallPolicy != null) {
                  assertTrue(fWaaSApi.deleteFirewallPolicy(firewallPolicy.getId()));
               }
         } finally {
            if (firewall != null) {
               assertTrue(fWaaSApi.delete(firewall.getId()));
            }         }
         }
      }
   }

}
