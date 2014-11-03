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

import java.util.List;

import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public final class FirewallToIpPermission implements Function<Firewall, Iterable<IpPermission>> {
   @Override public Iterable<IpPermission> apply(Firewall fw) {
      ImmutableList.Builder<IpPermission> rules = ImmutableList.builder();

      for (Rule rule : fw.allowed()) {
         if (rule.ports() != null && !rule.ports().isEmpty()) {
            for (String r : rule.ports()) {
               IpPermission.Builder builder = populateBuilder(fw, rule.ipProtocol());
               List<String> range = Splitter.on('-').splitToList(r);
               int from = Integer.valueOf(range.get(0));
               builder.fromPort(from);
               builder.toPort(range.size() == 2 ? Integer.valueOf(range.get(1)) : from);
               rules.add(builder.build());
            }
         } else {
            rules.add(populateBuilder(fw, rule.ipProtocol()).build());
         }
      }

      return rules.build();
   }

   private static IpPermission.Builder populateBuilder(Firewall fw, String protocol) {
      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.fromValue(protocol.toUpperCase()));

      if (!fw.sourceRanges().isEmpty()) {
         builder.cidrBlocks(fw.sourceRanges());
      }
      if (!fw.sourceTags().isEmpty()) {
         builder.groupIds(fw.sourceTags());
      }

      return builder;
   }
}

