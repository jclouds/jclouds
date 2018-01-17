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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.inject.assistedinject.Assisted;

/**
 * A function for transforming a Nova-specific SecurityGroup into a generic
 * SecurityGroup object.
 */
public class NovaSecurityGroupToSecurityGroup
      implements Function<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup, SecurityGroup> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public interface Factory {
      NovaSecurityGroupToSecurityGroup create(Location location);
   }

   private final Location location;

   @Inject
   public NovaSecurityGroupToSecurityGroup(@Assisted Location location) {
      this.location = location;
   }

   @Override
   public SecurityGroup apply(@Nullable org.jclouds.openstack.nova.v2_0.domain.SecurityGroup group) {
      SecurityGroupBuilder builder = new SecurityGroupBuilder();
      builder.providerId(group.getId());
      builder.ownerId(group.getTenantId());
      builder.name(group.getName());
      final String regionId = location.getId();
      builder.location(location);

      builder.id(regionId + "/" + group.getId());
      if (group.getRules() != null) {
         builder.ipPermissions(filter(transform(group.getRules(), new Function<SecurityGroupRule, IpPermission>() {
            @Override
            public IpPermission apply(SecurityGroupRule input) {
               return securityGroupRuleToIpPermission(input);
            }
         }), Predicates.notNull()));
      }
      return builder.build();
   }

   private IpPermission securityGroupRuleToIpPermission(SecurityGroupRule rule) {
      IpPermission.Builder builder = IpPermission.builder();
      builder.ipProtocol(rule.getIpProtocol());
      builder.fromPort(rule.getFromPort());
      builder.toPort(rule.getToPort());
      final TenantIdAndName ruleGroup = rule.getGroup();
      if (ruleGroup != null) {
         builder.groupId(location.getId() + "/" + ruleGroup.getTenantId());
      }
      if (rule.getIpRange() != null) {
         builder.cidrBlock(rule.getIpRange());
      }
      return builder.build();
   }
}
