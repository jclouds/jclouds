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
package org.jclouds.azurecompute.arm.compute.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.azurecompute.arm.compute.domain.ResourceGroupAndName.fromResourceGroupAndName;
import static org.jclouds.azurecompute.arm.compute.functions.NetworkSecurityRuleToIpPermission.InboundRule;
import static org.jclouds.azurecompute.arm.compute.functions.VirtualMachineToNodeMetadata.getLocation;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractResourceGroup;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.domain.Location;
import org.jclouds.net.domain.IpPermission;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Inject;

@Singleton
public class NetworkSecurityGroupToSecurityGroup implements Function<NetworkSecurityGroup, SecurityGroup> {
   private final Function<NetworkSecurityRule, IpPermission> ruleToPermission;
   private final Supplier<Set<? extends Location>> locations;

   @Inject
   NetworkSecurityGroupToSecurityGroup(Function<NetworkSecurityRule, IpPermission> ruleToPermission,
         @Memoized Supplier<Set<? extends Location>> locations) {
      this.ruleToPermission = ruleToPermission;
      this.locations = locations;
   }

   @Override
   public SecurityGroup apply(NetworkSecurityGroup input) {
      SecurityGroupBuilder builder = new SecurityGroupBuilder();

      builder.id(fromResourceGroupAndName(extractResourceGroup(input.id()), input.name()).slashEncode());
      builder.providerId(input.id());
      builder.name(input.name());
      builder.location(getLocation(locations, input.location()));

      if (input.properties().securityRules() != null) {
         // We just support security groups that allow traffic to a set of
         // targets. We don't support deny rules or origin based rules in the
         // security group api.
         builder.ipPermissions(transform(filter(input.properties().securityRules(), InboundRule), ruleToPermission));
      }

      return builder.build();
   }

}
