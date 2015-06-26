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
package org.jclouds.googlecomputeengine.features;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "live", testName = "FirewallApiLiveTest")
public class FirewallApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String FIREWALL_NAME = "firewall-api-live-test-firewall";
   private static final String FIREWALL_NETWORK_NAME = "firewall-api-live-test-network";
   private static final String IPV4_RANGE = "10.0.0.0/8";

   private FirewallApi api() {
      return api.firewalls();
   }

   @Test(groups = "live")
   public void testInsertFirewall() {
      // need to insert the network first
      assertOperationDoneSuccessfully(
            api.networks().createInIPv4Range(FIREWALL_NETWORK_NAME, IPV4_RANGE));

      FirewallOptions firewall = new FirewallOptions()
              .addAllowedRule(Firewall.Rule.create("tcp", ImmutableList.of("22")))
              .addSourceRange("10.0.0.0/8")
              .addSourceTag("tag1")
              .addTargetTag("tag2");

      assertOperationDoneSuccessfully(
            api().createInNetwork(FIREWALL_NAME, getNetworkUrl(FIREWALL_NETWORK_NAME), firewall));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertFirewall")
   public void testUpdateFirewall() {
      FirewallOptions firewall = new FirewallOptions()
              .name(FIREWALL_NAME)
              .network(getNetworkUrl(FIREWALL_NETWORK_NAME))
              .addSourceRange("10.0.0.0/8")
              .addSourceTag("tag1")
              .addTargetTag("tag2")
              .allowedRules(ImmutableList.of(Firewall.Rule.create("tcp", ImmutableList.of("23"))));

      assertOperationDoneSuccessfully(api().update(FIREWALL_NAME, firewall));
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateFirewall")
   public void testPatchFirewall() {
      FirewallOptions firewall = new FirewallOptions()
              .name(FIREWALL_NAME)
              .network(getNetworkUrl(FIREWALL_NETWORK_NAME))
              .allowedRules(ImmutableList.of(Firewall.Rule.create("tcp", ImmutableList.of("22")),
                    Firewall.Rule.create("tcp", ImmutableList.of("23"))))
              .addSourceRange("10.0.0.0/8")
              .addSourceTag("tag1")
              .addTargetTag("tag2");

      assertOperationDoneSuccessfully(api().update(FIREWALL_NAME, firewall));
   }

   @Test(groups = "live", dependsOnMethods = "testPatchFirewall")
   public void testGetFirewall() {
      FirewallOptions patchedFirewall = new FirewallOptions()
              .name(FIREWALL_NAME)
              .network(getNetworkUrl(FIREWALL_NETWORK_NAME))
              .allowedRules(ImmutableList.of(Firewall.Rule.create("tcp", ImmutableList.of("22")),
                    Firewall.Rule.create("tcp", ImmutableList.of("23"))))
              .addSourceRange("10.0.0.0/8")
              .addSourceTag("tag1")
              .addTargetTag("tag2");

      Firewall firewall = api().get(FIREWALL_NAME);
      assertNotNull(firewall);
      assertFirewallEquals(firewall, patchedFirewall);
   }

   @Test(groups = "live", dependsOnMethods = "testGetFirewall")
   public void testListFirewall() {
      Iterator<ListPage<Firewall>> firewalls = api().list(filter("name eq " + FIREWALL_NAME));

      assertEquals(firewalls.next().size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = "testListFirewall")
   public void testDeleteFirewall() {
      assertOperationDoneSuccessfully(api().delete(FIREWALL_NAME));
      assertOperationDoneSuccessfully(api.networks().delete(FIREWALL_NETWORK_NAME));
   }

   private void assertFirewallEquals(Firewall result, FirewallOptions expected) {
      assertEquals(result.name(), expected.name());
      assertEquals(getOnlyElement(result.sourceRanges()), getOnlyElement(expected.sourceRanges()));
      assertEquals(getOnlyElement(result.sourceTags()), getOnlyElement(expected.sourceTags()));
      assertEquals(getOnlyElement(result.targetTags()), getOnlyElement(expected.targetTags()));
      assertEquals(result.allowed(), expected.getAllowed());
   }
}
