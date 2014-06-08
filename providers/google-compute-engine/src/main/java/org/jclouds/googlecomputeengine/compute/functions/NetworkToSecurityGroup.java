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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * A function for transforming a GCE-specific Network into a generic
 * SecurityGroup object.
 */
public class NetworkToSecurityGroup implements Function<Network, SecurityGroup> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Function<Firewall, Iterable<IpPermission>> firewallToPerms;
   private final GoogleComputeEngineApi api;
   private final Supplier<String> project;

   @Inject
   public NetworkToSecurityGroup(Function<Firewall, Iterable<IpPermission>> firewallToPerms,
                                 GoogleComputeEngineApi api,
                                 @UserProject Supplier<String> project) {
      this.firewallToPerms = firewallToPerms;
      this.api = api;
      this.project = project;
   }

   @Override
   public SecurityGroup apply(Network network)  {
      SecurityGroupBuilder builder = new SecurityGroupBuilder();

      builder.id(network.getName());
      builder.providerId(network.getId());
      builder.name(network.getName());
      builder.uri(network.getSelfLink());

      ImmutableSet.Builder permBuilder = ImmutableSet.builder();

      ListOptions options = new ListOptions.Builder().filter("network eq .*/" + network.getName());

      for (Firewall fw : api.getFirewallApiForProject(project.get()).list(options).concat()) {
         permBuilder.addAll(firewallToPerms.apply(fw));
      }

      builder.ipPermissions(permBuilder.build());

      return builder.build();
   }
}

