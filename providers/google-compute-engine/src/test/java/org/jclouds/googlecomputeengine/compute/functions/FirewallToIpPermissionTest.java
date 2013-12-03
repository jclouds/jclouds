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
package org.jclouds.googlecomputeengine.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Date;

import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class FirewallToIpPermissionTest {

   @Test
   public void testApply() {

      Firewall fw = fwForTest();

      FirewallToIpPermission converter = new FirewallToIpPermission();

      Iterable<IpPermission> perms = converter.apply(fw);

      assertEquals(Iterables.size(perms), 3, "There should be three IpPermissions but there is only " + Iterables.size(perms));

      assertTrue(Iterables.any(perms, Predicates.and(hasProtocol(IpProtocol.TCP),
              hasStartAndEndPort(1, 10))), "No permission found for TCP, ports 1-10");
      assertTrue(Iterables.any(perms, Predicates.and(hasProtocol(IpProtocol.TCP),
              hasStartAndEndPort(33, 33))), "No permission found for TCP, port 33");
      assertTrue(Iterables.any(perms, hasProtocol(IpProtocol.ICMP)),
              "No permission found for ICMP");
   }

   public static Firewall fwForTest() {
      Firewall.Builder builder = Firewall.builder();

      builder.addSourceRange("0.0.0.0/0");
      builder.addAllowed(Firewall.Rule.builder().IpProtocol(IpProtocol.TCP)
              .addPortRange(1, 10).build());
      builder.addAllowed(Firewall.Rule.builder().IpProtocol(IpProtocol.TCP)
              .addPort(33).build());
      builder.addAllowed(Firewall.Rule.builder().IpProtocol(IpProtocol.ICMP).build());
      builder.id("abcd");
      builder.selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/firewalls/jclouds-test"));
      builder.network(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/jclouds-test"));
      builder.creationTimestamp(new Date());
      builder.name("jclouds-test");

      return builder.build();
   }

   public static Predicate<IpPermission> hasProtocol(final IpProtocol protocol) {
      return new Predicate<IpPermission>() {

         @Override
         public boolean apply(IpPermission perm) {
            return protocol.equals(perm.getIpProtocol());
         }
      };
   }

   public static Predicate<IpPermission> hasStartAndEndPort(final int startPort, final int endPort) {
      return new Predicate<IpPermission>() {

         @Override
         public boolean apply(IpPermission perm) {
            return startPort == perm.getFromPort() && endPort == perm.getToPort();
         }
      };
   }

}
