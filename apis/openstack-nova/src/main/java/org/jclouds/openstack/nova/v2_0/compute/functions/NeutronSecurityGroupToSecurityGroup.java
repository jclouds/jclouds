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

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.inject.assistedinject.Assisted;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.domain.Location;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.neutron.v2.domain.Rule;
import org.jclouds.openstack.neutron.v2.domain.RuleDirection;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

public class NeutronSecurityGroupToSecurityGroup implements Function<org.jclouds.openstack.neutron.v2.domain.SecurityGroup, SecurityGroup> {

    public interface Factory {
        NeutronSecurityGroupToSecurityGroup create(Location location);
    }

    private final Location location;

    @Inject
    public NeutronSecurityGroupToSecurityGroup(@Assisted Location location) {
        this.location = location;
    }

    @Override
    public SecurityGroup apply(@Nullable org.jclouds.openstack.neutron.v2.domain.SecurityGroup group) {
        SecurityGroupBuilder builder = new SecurityGroupBuilder();
        builder.providerId(group.getId());
        builder.ownerId(group.getTenantId());
        builder.name(group.getName());
        final String regionId = location.getId();
        builder.location(location);

        builder.id(regionId + "/" + group.getId());
        if (group.getRules() != null) {
            builder.ipPermissions(filter(transform(group.getRules(), new Function<Rule, IpPermission>() {
                @Override
                public IpPermission apply(Rule from) {
                    if (from.getDirection() == RuleDirection.EGRESS) return null;
                    IpPermission.Builder builder = IpPermission.builder();
                    if (from.getProtocol() != null) {
                        builder.ipProtocol(IpProtocol.fromValue(from.getProtocol().name()));
                    } else {
                        builder.ipProtocol(IpProtocol.TCP);
                    }
                    if (from.getPortRangeMin() != null) builder.fromPort(from.getPortRangeMin());
                    if (from.getPortRangeMax() != null) builder.toPort(from.getPortRangeMax());
                    if (from.getRemoteGroupId() != null) {
                        builder.groupId(regionId + "/" + from.getRemoteGroupId());
                    } else if (from.getRemoteIpPrefix() != null){
                        builder.cidrBlock(from.getRemoteIpPrefix());
                    }

                    return builder.build();
                }
            }), Predicates.notNull()));
        }

        return builder.build();
    }
}
