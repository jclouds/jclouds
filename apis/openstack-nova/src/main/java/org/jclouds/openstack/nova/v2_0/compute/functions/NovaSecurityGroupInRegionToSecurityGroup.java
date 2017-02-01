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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;


/**
 * A function for transforming a Nova-specific SecurityGroup into a generic
 * SecurityGroup object.
 */
@Singleton
public class NovaSecurityGroupInRegionToSecurityGroup implements Function<SecurityGroupInRegion, SecurityGroup> {
    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    protected final Supplier<Map<String, Location>> locationIndex;

    @Inject
    public NovaSecurityGroupInRegionToSecurityGroup(Supplier<Map<String, Location>> locationIndex) {
        this.locationIndex = checkNotNull(locationIndex, "locationIndex");
    }

    @Override
    public SecurityGroup apply(final SecurityGroupInRegion groupInRegion) {
        SecurityGroupBuilder builder = new SecurityGroupBuilder();

        final org.jclouds.openstack.nova.v2_0.domain.SecurityGroup group = groupInRegion.getSecurityGroup();
        builder.id(group.getId());
        builder.providerId(group.getId());
        builder.ownerId(group.getTenantId());
        builder.name(group.getName());
        if (group.getRules() != null) {
           builder.ipPermissions(filter(transform(group.getRules(), new Function<SecurityGroupRule, IpPermission>() {
              @Override
              public IpPermission apply(SecurityGroupRule input) {
                 return securityGroupRuleToIpPermission(groupInRegion, input);
              }
           }), Predicates.notNull()));
        }

        final String regionId = groupInRegion.getRegion();
        Location region = locationIndex.get().get(regionId);
        checkState(region != null, "location %s not in locationIndex: %s", regionId, locationIndex.get());

        builder.location(region);

        builder.id(regionId + "/" + group.getId());

        return builder.build();
    }

    private IpPermission securityGroupRuleToIpPermission(SecurityGroupInRegion groupInRegion, SecurityGroupRule rule) {
        IpPermission.Builder builder = IpPermission.builder();
        builder.ipProtocol(rule.getIpProtocol());
        builder.fromPort(rule.getFromPort());
        builder.toPort(rule.getToPort());
        final TenantIdAndName ruleGroup = rule.getGroup();
        if (ruleGroup != null) {
           final org.jclouds.openstack.nova.v2_0.domain.SecurityGroup owningGroup =
              groupInRegion.getSecurityGroup();
           final Collection<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup> referredGroup =
              groupInRegion.getGroupsByName().get(ruleGroup);
           if (null == referredGroup) {
              logger.warn("Unknown group {} used in security rule, refusing to add it to {} ({})",
                 ruleGroup, owningGroup.getName(), owningGroup.getId());
              return null;
           }
           /*  Checking referredGroup.size(), see comments on SecurityGroupInRegion.getGroupsByName(). If there are
               duplicate groups with the same tenant-id-and-name as that of ruleGroup then it is not possible
               with the Nova /v2/12345/os-security-groups API to know which group is intended, as the only
               information it returns about referred groups is the tenant id and name:
                        "group": {
                           "tenant_id": "a0ade3ca76784719845363979dc1014e",
                           "name": "jclouds-qa-scheduler-docker-entity"
                        },
              Rather than pick one group at random and risk using the wrong group, here we fall back to the
              least-worst option(?) and refuse to add any rule.

              See https://issues.apache.org/jira/browse/JCLOUDS-1234.
           */
           if (referredGroup.size() != 1) {
              logger.warn("Ambiguous group %s used in security rule, refusing to add it to %s (%s)",
                 ruleGroup, owningGroup.getName(), owningGroup.getId());
              return null;
           }
           builder.groupId(groupInRegion.getRegion() + "/" + Iterables.getOnlyElement(referredGroup).getId());
        }
        if (rule.getIpRange() != null) {
            builder.cidrBlock(rule.getIpRange());
        }
        return builder.build();
    }
}
