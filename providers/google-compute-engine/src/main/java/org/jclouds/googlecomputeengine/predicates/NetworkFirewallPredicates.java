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

import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

public class NetworkFirewallPredicates {

   public static Predicate<Firewall> hasProtocol(final IpProtocol protocol) {
      return new Predicate<Firewall>() {

         @Override
         public boolean apply(Firewall fw) {
            for (Rule rule : fw.getAllowed()) {
               if (rule.getIpProtocol().equals(protocol)) {
                  return true;
               }
            }

            return false;
         }
      };
   }

   public static Predicate<Firewall> hasPortRange(final Range<Integer> portRange) {
      return new Predicate<Firewall>() {

         @Override
         public boolean apply(Firewall fw) {
            return Iterables.any(fw.getAllowed(), new Predicate<Rule>() {
               @Override
               public boolean apply(Rule input) {
                  return input.getPorts().encloses(portRange);
               }
            });
         }
      };
   }

   public static Predicate<Firewall> hasSourceTag(final String sourceTag) {
      return new Predicate<Firewall>() {
         @Override
         public boolean apply(Firewall input) {
            return input.getSourceTags() != null && input.getSourceTags().contains(sourceTag);
         }
      };
   }

   public static Predicate<Firewall> hasSourceRange(final String sourceRange) {
      return new Predicate<Firewall>() {
         @Override
         public boolean apply(Firewall input) {
            return input.getSourceRanges() != null && input.getSourceRanges().contains(sourceRange);
         }
      };
   }

   public static Predicate<Firewall> equalsIpPermission(final IpPermission permission) {
      return new Predicate<Firewall>() {
         @Override
         public boolean apply(Firewall input) {
            return Iterables.elementsEqual(permission.getGroupIds(), input.getSourceTags())
                      && Iterables.elementsEqual(permission.getCidrBlocks(), input.getSourceRanges())
                      && (input.getAllowed().size() == 1
                             && ruleEqualsIpPermission(permission).apply(Iterables.getOnlyElement(input.getAllowed())));
         }
      };
   }

   public static Predicate<Firewall> providesIpPermission(final IpPermission permission) {
      return new Predicate<Firewall>() {
         @Override
         public boolean apply(Firewall input) {
            boolean groupsMatchTags = (permission.getGroupIds().isEmpty() && input.getSourceTags().isEmpty())
                    || !Sets.intersection(permission.getGroupIds(), input.getSourceTags()).isEmpty();
            boolean cidrsMatchRanges = (permission.getCidrBlocks().isEmpty() && input.getSourceRanges().isEmpty())
                    || !Sets.intersection(permission.getCidrBlocks(), input.getSourceRanges()).isEmpty();
            boolean firewallHasPorts = hasProtocol(permission.getIpProtocol()).apply(input)
                    && ((permission.getFromPort() == 0 && permission.getToPort() == 0)
                    || hasPortRange(Range.closed(permission.getFromPort(), permission.getToPort())).apply(input));

            return groupsMatchTags && cidrsMatchRanges && firewallHasPorts;
         }
      };
   }

   private static Predicate<Firewall.Rule> ruleEqualsIpPermission(final IpPermission permission) {
      return new Predicate<Rule>() {
         @Override
         public boolean apply(Firewall.Rule input) {
            return permission.getIpProtocol().equals(input.getIpProtocol())
                      && ((input.getPorts().isEmpty() && permission.getFromPort() == 0 && permission.getToPort() == 0)
                             || (input.getPorts().asRanges().size() == 1
                                    && permission.getFromPort() == Iterables.getOnlyElement(input.getPorts().asRanges()).lowerEndpoint()
                                    && permission.getToPort() == Iterables.getOnlyElement(input.getPorts().asRanges()).upperEndpoint()));
         }
      };
   }
}
