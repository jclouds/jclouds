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

import static com.google.common.collect.Sets.intersection;

import java.util.List;

import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.net.domain.IpPermission;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public final class NetworkFirewallPredicates {

   public static Predicate<Firewall> hasPortRange(final String protocol, final int fromPort, final int toPort) {
      return new Predicate<Firewall>() {
         @Override public boolean apply(Firewall fw) {
            for (Rule rule : fw.allowed()) {
               if (!rule.ipProtocol().equals(protocol)) {
                  continue;
               }
               if (rule.ports() == null || rule.ports().isEmpty()) {
                  return true;
               }
               for (String range : rule.ports()) {
                  if (range.indexOf('-') != -1) {
                     if (inRange(range, fromPort, toPort)) {
                        return true;
                     }
                  }
               }
            }
            return false;
         }
      };
   }

   private static boolean inRange(String range, int fromPort, int toPort) {
      List<String> ports = Splitter.on('-').splitToList(range);
      return fromPort >= Integer.valueOf(ports.get(0)) && toPort <= Integer.valueOf(ports.get(1));
   }

   public static Predicate<Firewall> hasSourceTag(final String sourceTag) {
      return new Predicate<Firewall>() {
         @Override public boolean apply(Firewall input) {
            return input.sourceTags().contains(sourceTag);
         }
      };
   }

   public static Predicate<Firewall> hasSourceRange(final String sourceRange) {
      return new Predicate<Firewall>() {
         @Override  public boolean apply(Firewall input) {
            return input.sourceRanges().contains(sourceRange);
         }
      };
   }

   public static Predicate<Firewall> equalsIpPermission(final IpPermission permission) {
      return new Predicate<Firewall>() {
         @Override public boolean apply(Firewall input) {
            return Iterables.elementsEqual(permission.getGroupIds(), input.sourceTags())
                      && Iterables.elementsEqual(permission.getCidrBlocks(), input.sourceRanges())
                      && (input.allowed().size() == 1
                             && ruleEqualsIpPermission(permission).apply(Iterables.getOnlyElement(input.allowed())));
         }
      };
   }

   public static Predicate<Firewall> providesIpPermission(final IpPermission permission) {
      return new Predicate<Firewall>() {
         @Override  public boolean apply(Firewall input) {
            boolean groupsMatchTags =
                  (permission.getGroupIds().isEmpty() && input.sourceTags().isEmpty()) || !intersection(
                        permission.getGroupIds(), ImmutableSet.copyOf(input.sourceTags())).isEmpty();
            boolean cidrsMatchRanges =
                  (permission.getCidrBlocks().isEmpty() && input.sourceRanges().isEmpty()) || !intersection(
                        permission.getCidrBlocks(), ImmutableSet.copyOf(input.sourceRanges())).isEmpty();
            boolean firewallHasPorts = hasPortRange(permission.getIpProtocol().value().toLowerCase(),
                        permission.getFromPort(), permission.getToPort()).apply(input);
            return groupsMatchTags && cidrsMatchRanges && firewallHasPorts;
         }
      };
   }

   private static Predicate<Firewall.Rule> ruleEqualsIpPermission(final IpPermission permission) {
      return new Predicate<Rule>() {
         @Override public boolean apply(Firewall.Rule input) {
            if (!permission.getIpProtocol().value().toLowerCase().equals(input.ipProtocol())) {
               return false;
            }
            if (input.ports() == null
                  || input.ports().isEmpty() && permission.getFromPort() == 0 && permission.getToPort() == 0) {
               return true;
            } else if (input.ports().size() == 1) {
               String port = Iterables.getOnlyElement(input.ports());
               if (permission.getFromPort() == permission.getToPort()) {
                  return port.equals(String.valueOf(permission.getFromPort()));
               }
               return port.equals(permission.getFromPort() + "-" + permission.getToPort());
            }
            return false;
         }
      };
   }
}
