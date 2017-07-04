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
package org.jclouds.net.domain;

import static org.jclouds.net.domain.IpPermission.builder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class IpPermissionTest {

   @Test
   public void testCompareProtocol() {
      final IpPermission tcp = builder().ipProtocol(IpProtocol.TCP).build();
      final IpPermission tcp2 = builder().ipProtocol(IpProtocol.TCP).build();
      assertEqualAndComparable(tcp, tcp2);

      final IpPermission udp = builder().ipProtocol(IpProtocol.UDP).build();
      assertOrder(tcp, udp);

      final IpPermission t10 = builder().fromPermission(tcp).fromPort(10).build();
      final IpPermission t20 = builder().fromPermission(tcp).fromPort(20).build();
      final IpPermission u10 = builder().fromPermission(udp).fromPort(10).build();

      final IpPermission t0to10 = builder().fromPermission(tcp).toPort(10).build();
      final IpPermission t0to20 = builder().fromPermission(tcp).toPort(20).build();

      assertTotalOrder(ImmutableList.of(tcp, t0to10, t0to20, t10, t20, udp, u10));
   }

   @Test
   public void testCompareTenantIdGroupNamePairs() {
      final IpPermission tcp = builder().ipProtocol(IpProtocol.TCP).build();

      final IpPermission g1 = builder().fromPermission(tcp)
         .tenantIdGroupNamePair("tenant1", "group1").build();

      final IpPermission g2 = builder().fromPermission(tcp)
         .tenantIdGroupNamePair("tenant1", "group2").build();

      final IpPermission g12 = builder().fromPermission(tcp)
         .tenantIdGroupNamePair("tenant1", "group1")
         .tenantIdGroupNamePair("tenant1", "group2").build();

      final IpPermission g21 = builder().fromPermission(tcp)
         .tenantIdGroupNamePair("tenant1", "group2")
         .tenantIdGroupNamePair("tenant1", "group1").build();

      final IpPermission t2g1 = builder().fromPermission(tcp)
         .tenantIdGroupNamePair("tenant2", "group1").build();

      assertTotalOrder(ImmutableList.of(tcp, g1, g12, g2, g21, t2g1));

      final IpPermission g12b = builder().fromPermission(tcp)
         .tenantIdGroupNamePair("tenant1", "group1")
         .tenantIdGroupNamePair("tenant1", "group2").build();

      assertEqualAndComparable(g12, g12b);
   }

   @Test
   public void testCompareGroupIds() {
      final IpPermission tcp = builder().ipProtocol(IpProtocol.TCP).build();

      final IpPermission aa = builder().fromPermission(tcp)
         .groupId("a").build();

      final IpPermission a = builder().fromPermission(tcp)
         .groupId("a").build();

      final IpPermission ab = builder().fromPermission(tcp)
         .groupId("a")
         .groupId("b").build();

      final IpPermission ba = builder().fromPermission(tcp)
         .groupId("b")
         .groupId("a").build();

      assertTotalOrder(ImmutableList.of(tcp, a, ab, ba));
      assertEqualAndComparable(a, aa);
   }

   @Test
   public void testCompareCidrBlocks() {
      final IpPermission tcp = builder().ipProtocol(IpProtocol.TCP).build();

      final IpPermission everything = builder().fromPermission(tcp)
         .cidrBlock("0.0.0.0/0").build();
      final IpPermission universal = builder().fromPermission(tcp)
         .cidrBlock("0.0.0.0/0").build();
      assertEqualAndComparable(everything, universal);

      final IpPermission localhost = builder().fromPermission(tcp)
         .cidrBlock("127.0.0.1/32").build();

      final IpPermission tenTwentyOne = builder().fromPermission(tcp)
         .cidrBlock("10.0.0.21/32").build();

      final IpPermission tenTwoHundred = builder().fromPermission(tcp)
         .cidrBlock("10.0.0.200/32").build();

      // comparison is alphabetic, not by numeric equivalent
      assertOrder(tenTwoHundred, tenTwentyOne);

      assertTotalOrder(ImmutableList.of(tcp, everything, tenTwoHundred, tenTwentyOne, localhost));
   }

   @Test
   public void testCompareExclusionCidrBlocks() {
      final IpPermission tcp = builder().ipProtocol(IpProtocol.TCP).build();

      final IpPermission everything = builder().fromPermission(tcp)
         .exclusionCidrBlock("0.0.0.0/0").build();
      final IpPermission universal = builder().fromPermission(tcp)
         .exclusionCidrBlock("0.0.0.0/0").build();
      assertEqualAndComparable(everything, universal);

      final IpPermission localhost = builder().fromPermission(tcp)
         .exclusionCidrBlock("127.0.0.1/32").build();
      final IpPermission stillLocal = builder().fromPermission(tcp)
         .exclusionCidrBlock("127.0.0.1/32").build();
      assertEqualAndComparable(localhost, stillLocal);

      final IpPermission tenTwentyOne = builder().fromPermission(tcp)
         .exclusionCidrBlock("10.0.0.21/32").build();

      final IpPermission tenTwoHundred = builder().fromPermission(tcp)
         .exclusionCidrBlock("10.0.0.200/32").build();

      // comparison is alphabetic, not by numeric equivalent
      assertOrder(tenTwoHundred, tenTwentyOne);

      assertTotalOrder(ImmutableList.of(tcp, everything, tenTwoHundred, tenTwentyOne, localhost));
   }


   @Test
   public void testPairwise() {

      final IpPermission tcp = builder().ipProtocol(IpProtocol.TCP).build();
      final IpPermission udp = builder().ipProtocol(IpProtocol.UDP).build();

      final IpPermission f10 = builder().fromPermission(tcp).fromPort(10).build();
      final IpPermission f20 = builder().fromPermission(tcp).fromPort(20).build();
      final IpPermission u10 = builder().fromPermission(udp).fromPort(10).build();

      final IpPermission t20 = builder().fromPermission(f10).toPort(20).build();
      final IpPermission t30 = builder().fromPermission(f10).toPort(30).build();

      final IpPermission t2g1 = builder().fromPermission(t20)
         .tenantIdGroupNamePair("tenant1", "group1")
      .build();

      final IpPermission t2g2 = builder().fromPermission(t20)
         .tenantIdGroupNamePair("tenant1", "group2")
      .build();

      final IpPermission gidA = builder().fromPermission(t2g1)
         .groupId("groupA")
      .build();

      final IpPermission gidB = builder().fromPermission(t2g1)
         .groupId("groupB")
      .build();

      final IpPermission cidr10 = builder().fromPermission(gidA)
         .cidrBlock("10.10.10.10/32")
      .build();

      final IpPermission cidr20 = builder().fromPermission(gidA)
         .cidrBlock("10.10.10.20/32")
      .build();

      final IpPermission ex10 = builder().fromPermission(cidr10)
         .exclusionCidrBlock("172.16.10.10/32")
      .build();

      final IpPermission ex20 = builder().fromPermission(cidr10)
         .exclusionCidrBlock("172.16.10.20/32")
      .build();

      assertTotalOrder(ImmutableList.of(
         tcp,
            f10,
               t20,
                  t2g1,
                     gidA,
                        cidr10,
                           ex10,
                           ex20,
                        cidr20,
                     gidB,
                  t2g2,
               t30,
            f20,
         udp,
            u10
      ));
   }


   public static void assertEqualAndComparable(IpPermission first, IpPermission second) {
      assertEquals(first, second, first + " does not equal " + second);
      assertTrue(first.compareTo(second) == 0, first + " does not compare zero to " + second);
   }

   @SuppressWarnings("SelfComparison")
   private static void assertOrder(IpPermission smaller, IpPermission bigger) {
      assertTrue(smaller.compareTo(bigger) < 0, smaller + " does not compare less than " + bigger);
      assertTrue(bigger.compareTo(smaller) > 0, bigger + " does not compare greater than " + smaller);
      assertTrue(smaller.compareTo(smaller) == 0, smaller + " does not compare zero to itself");
      assertTrue(bigger.compareTo(bigger) == 0, bigger + " does not compare zero to itself");
   }

   private static void assertTotalOrder(List<IpPermission> permissions) {
      if (permissions.size() < 2) return;
      IpPermission head = permissions.get(0);
      List<IpPermission> tail = permissions.subList(1, permissions.size());
      for (IpPermission perm : tail) {
         assertOrder(head, perm);
      }
      assertTotalOrder(tail);
   }

}
