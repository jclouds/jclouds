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
import static org.jclouds.googlecomputeengine.predicates.NetworkFirewallPredicates.equalsIpPermission;
import static org.jclouds.googlecomputeengine.predicates.NetworkFirewallPredicates.hasPortRange;
import static org.jclouds.googlecomputeengine.predicates.NetworkFirewallPredicates.hasProtocol;
import static org.jclouds.googlecomputeengine.predicates.NetworkFirewallPredicates.hasSourceRange;
import static org.jclouds.googlecomputeengine.predicates.NetworkFirewallPredicates.hasSourceTag;
import static org.jclouds.googlecomputeengine.predicates.NetworkFirewallPredicates.providesIpPermission;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Date;

import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.collect.Range;

@Test(groups = "unit")
public class NetworkFirewallPredicatesTest {

   public static Firewall getFwForTestSourceTags() {
      Firewall.Builder builder = Firewall.builder();

      builder.network(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/jclouds-test"));
      builder.selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/firewalls/jclouds-test"));
      builder.addSourceTag("tag-1");
      builder.addAllowed(Firewall.Rule.builder().IpProtocol(IpProtocol.TCP)
              .addPortRange(1, 10).build());
      builder.addAllowed(Firewall.Rule.builder().IpProtocol(IpProtocol.TCP)
              .addPort(33).build());
      builder.addAllowed(Firewall.Rule.builder().IpProtocol(IpProtocol.ICMP).build());
      builder.id("abcd");
      builder.creationTimestamp(new Date());
      builder.name("jclouds-test");

      return builder.build();
   }

   public static Firewall getFwForTestSourceTagsExact() {
      Firewall.Builder builder = Firewall.builder();

      builder.network(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/jclouds-test"));
      builder.selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/firewalls/jclouds-test"));
      builder.addSourceTag("tag-1");
      builder.addAllowed(Firewall.Rule.builder().IpProtocol(IpProtocol.TCP)
              .addPortRange(1, 10).build());
      builder.id("abcd");
      builder.creationTimestamp(new Date());
      builder.name("jclouds-test");

      return builder.build();
   }

   @Test
   public void testHasProtocol() {
      assertTrue(hasProtocol(IpProtocol.TCP).apply(fwForTest()),
              "Firewall " + fwForTest() + " should contain a TCP rule.");
   }

   @Test
   public void testHasProtocolFails() {
      assertFalse(hasProtocol(IpProtocol.UDP).apply(fwForTest()),
              "Firewall " + fwForTest() + " should NOT contain a UDP rule.");
   }

   @Test
   public void testHasPortRange() {
      assertTrue(hasPortRange(Range.closed(2, 9)).apply(fwForTest()),
              "Firewall " + fwForTest() + " should contain the port range 2-9.");
   }

   @Test
   public void testHasPortRangeFails() {
      assertFalse(hasPortRange(Range.closed(11, 15)).apply(fwForTest()),
              "Firewall " + fwForTest() + " should NOT contain the port range 11-15.");
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
      IpPermission perm = IpPermission.builder().groupId("tag-1")
              .fromPort(1).toPort(10).ipProtocol(IpProtocol.TCP).build();

      assertTrue(equalsIpPermission(perm).apply(getFwForTestSourceTagsExact()),
              "Firewall " + getFwForTestSourceTagsExact() + " should match IpPermission " + perm + " but does not.");
   }

   @Test
   public void testEqualsIpPermissionFails() {
      IpPermission perm = IpPermission.builder().groupId("tag-1")
              .fromPort(1).toPort(10).ipProtocol(IpProtocol.TCP).build();

      assertFalse(equalsIpPermission(perm).apply(getFwForTestSourceTags()),
              "Firewall " + getFwForTestSourceTags() + " should not match IpPermission " + perm + " but does.");
   }

   @Test
   public void testProvidesIpPermission() {
      IpPermission perm = IpPermission.builder().groupId("tag-1")
              .fromPort(1).toPort(10).ipProtocol(IpProtocol.TCP).build();

      assertTrue(providesIpPermission(perm).apply(getFwForTestSourceTagsExact()),
              "Firewall " + getFwForTestSourceTagsExact() + " should provide IpPermission " + perm + " but does not.");

      assertTrue(providesIpPermission(perm).apply(getFwForTestSourceTags()),
              "Firewall " + getFwForTestSourceTags() + " should inexactly provide IpPermission " + perm + " but does not.");
   }

   @Test
   public void testProvidesIpPermissionFails() {
      IpPermission perm = IpPermission.builder().groupId("tag-1")
              .fromPort(1).toPort(10).ipProtocol(IpProtocol.TCP).build();

      assertFalse(providesIpPermission(perm).apply(fwForTest()),
              "Firewall " + fwForTest() + " should not provide IpPermission " + perm + " but does.");
   }
}

