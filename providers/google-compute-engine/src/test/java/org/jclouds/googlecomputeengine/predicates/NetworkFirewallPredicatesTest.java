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
package org.jclouds.googlecomputeengine.predicates;

import static org.jclouds.googlecomputeengine.compute.functions.FirewallToIpPermissionTest.fwForTest;
import static org.jclouds.googlecomputeengine.compute.predicates.NetworkFirewallPredicates.equalsIpPermission;
import static org.jclouds.googlecomputeengine.compute.predicates.NetworkFirewallPredicates.hasPortRange;
import static org.jclouds.googlecomputeengine.compute.predicates.NetworkFirewallPredicates.hasSourceRange;
import static org.jclouds.googlecomputeengine.compute.predicates.NetworkFirewallPredicates.hasSourceTag;
import static org.jclouds.googlecomputeengine.compute.predicates.NetworkFirewallPredicates.providesIpPermission;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "NetworkFirewallPredicatesTest")
public class NetworkFirewallPredicatesTest {
   protected static final String BASE_URL = "https://www.googleapis.com/compute/v1/projects";

   public static Firewall getFwForTestSourceTags() {
      return Firewall.create( //
            "abcd", // id
            URI.create(BASE_URL + "/party/global/firewalls/jclouds-test"), // selfLink
            "jclouds-test", // name
            null, // description
            URI.create(BASE_URL + "/party/global/networks/jclouds-test"), // network
            null, // sourceRanges
            ImmutableList.of("tag-1"), // sourceTags
            null, // targetTags
            ImmutableList.of( // allowed
                  Firewall.Rule.create("tcp", ImmutableList.of("1-10")), //
                  Firewall.Rule.create("tcp", ImmutableList.of("33")) //
            ));
   }

   public static Firewall getFwForTestSourceTagsExact() {
      return Firewall.create( //
            "abcd", // id
            URI.create(BASE_URL + "/party/global/firewalls/jclouds-test"), // selfLink
            "jclouds-test", // name
            null, // description
            URI.create(BASE_URL + "/party/global/networks/jclouds-test"), // network
            null, // sourceRanges
            ImmutableList.of("tag-1"), // sourceTags
            null, // targetTags
            ImmutableList.of(Firewall.Rule.create("tcp", ImmutableList.of("1-10"))) // allowed
      );
   }

   @Test
   public void testHasPortRange() {
      assertTrue(hasPortRange("tcp", 2, 9).apply(fwForTest()),
            "Firewall " + fwForTest() + " should contain the port range 2-9.");
   }

   @Test
   public void testHasPortRangeSame() {
      assertTrue(hasPortRange("tcp", 2, 2).apply(fwForTest()),
            "Firewall " + fwForTest() + " should contain the port range 2-2.");
   }

   @Test
   public void testHasPortRangeFails() {
      assertFalse(hasPortRange("tcp", 11, 15).apply(fwForTest()),
            "Firewall " + fwForTest() + " should NOT contain the port range 11-15.");
   }

   @Test
   public void testHasPortRangeFailsSame() {
      assertFalse(hasPortRange("tcp", 15, 15).apply(fwForTest()),
            "Firewall " + fwForTest() + " should NOT contain the port range 15-15.");
   }

   @Test
   public void testHasSourceTag() {
      assertTrue(hasSourceTag("tag-1").apply(getFwForTestSourceTags()),
            "Firewall " + getFwForTestSourceTags() + " should contain the source tag 'tag-1'.");
   }

   @Test
   public void testHasSourceTagFails() {
      assertFalse(hasSourceTag("tag-1").apply(fwForTest()),
            "Firewall " + fwForTest() + " should NOT contain the source tag 'tag-1'.");
   }

   @Test
   public void testHasSourceRange() {
      assertTrue(hasSourceRange("0.0.0.0/0").apply(fwForTest()),
            "Firewall " + fwForTest() + " should contain the source range '0.0.0.0/0'.");
   }

   @Test
   public void testHasSourceRangeFails() {
      assertFalse(hasSourceRange("0.0.0.0/0").apply(getFwForTestSourceTags()),
            "Firewall " + getFwForTestSourceTags() + " should NOT contain the source range '0.0.0.0/0'.");
   }

   @Test
   public void testEqualsIpPermission() {
      IpPermission perm = IpPermission.builder().groupId("tag-1").fromPort(1).toPort(10).ipProtocol(IpProtocol.TCP)
            .build();

      assertTrue(equalsIpPermission(perm).apply(getFwForTestSourceTagsExact()),
            "Firewall " + getFwForTestSourceTagsExact() + " should match IpPermission " + perm + " but does not.");
   }

   @Test
   public void testEqualsIpPermissionFails() {
      IpPermission perm = IpPermission.builder().groupId("tag-1").fromPort(1).toPort(10).ipProtocol(IpProtocol.TCP)
            .build();

      assertFalse(equalsIpPermission(perm).apply(getFwForTestSourceTags()),
            "Firewall " + getFwForTestSourceTags() + " should not match IpPermission " + perm + " but does.");
   }

   @Test
   public void testProvidesIpPermission() {
      IpPermission perm = IpPermission.builder().groupId("tag-1").fromPort(1).toPort(10).ipProtocol(IpProtocol.TCP)
            .build();

      assertTrue(providesIpPermission(perm).apply(getFwForTestSourceTagsExact()),
            "Firewall " + getFwForTestSourceTagsExact() + " should provide IpPermission " + perm + " but does not.");

      assertTrue(providesIpPermission(perm).apply(getFwForTestSourceTags()),
            "Firewall " + getFwForTestSourceTags() + " should inexactly provide IpPermission " + perm
                  + " but does not.");
   }

   @Test
   public void testProvidesIpPermissionFails() {
      IpPermission perm = IpPermission.builder().groupId("tag-1").fromPort(1).toPort(10).ipProtocol(IpProtocol.TCP)
            .build();

      assertFalse(providesIpPermission(perm).apply(fwForTest()),
            "Firewall " + fwForTest() + " should not provide IpPermission " + perm + " but does.");
   }
}

