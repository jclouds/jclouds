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

import static org.jclouds.googlecloud.internal.ListPages.concat;
import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;

import javax.inject.Inject;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.net.domain.IpPermission;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

public final class NetworkToSecurityGroup implements Function<Network, SecurityGroup> {

   private final Function<Firewall, Iterable<IpPermission>> firewallToPerms;
   private final GoogleComputeEngineApi api;

   @Inject NetworkToSecurityGroup(Function<Firewall, Iterable<IpPermission>> firewallToPerms,
         GoogleComputeEngineApi api) {
      this.firewallToPerms = firewallToPerms;
      this.api = api;
   }

   @Override public SecurityGroup apply(Network network)  {
      SecurityGroupBuilder builder = new SecurityGroupBuilder();

      builder.id(network.name());
      builder.providerId(network.id());
      builder.name(network.name());
      builder.uri(network.selfLink());

      ImmutableList.Builder permBuilder = ImmutableList.builder();

      ListOptions options = filter("network eq .*/" + network.name());

      for (Firewall fw : concat(api.firewalls().list(options))) {
         permBuilder.addAll(firewallToPerms.apply(fw));
      }

      builder.ipPermissions(permBuilder.build());

      return builder.build();
   }
}

