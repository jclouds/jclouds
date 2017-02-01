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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;


@Test(groups = "unit", testName = "NovaSecurityGroupInRegionToSecurityGroupTest")
public class NovaSecurityGroupInRegionToSecurityGroupTest {

   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("openstack-nova")
           .description("openstack-nova").build();
   Location region = new LocationBuilder().id("az-1.region-a.geo-1").description("az-1.region-a.geo-1")
           .scope(LocationScope.REGION).parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
           .<String, Location>of("az-1.region-a.geo-1", region));

   public static final String SOME_GROUP_ID = "some-group-id";
   public static final String SOME_OTHER_GROUP_ID = "some-other-group-id";
   public static final String IP_RANGE = "0.0.0.0/0";
   public static final String SOME_OTHER_GROUP = "some-other-group";
   public static final String SOME_GROUP = "some-group";

   public static final ImmutableList<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup> allGroups =
      ImmutableList.of(securityGroupWithGroup(), securityGroupWithCidr());

   public static org.jclouds.openstack.nova.v2_0.domain.SecurityGroup securityGroupWithGroup() {
      TenantIdAndName group = TenantIdAndName.builder().tenantId("tenant").name(SOME_OTHER_GROUP).build();

      SecurityGroupRule ruleToConvert = SecurityGroupRule.builder()
         .id("some-rule-id")
         .ipProtocol(IpProtocol.TCP)
         .fromPort(10)
         .toPort(20)
         .group(group)
         .parentGroupId(SOME_GROUP_ID)
         .build();

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup origGroup =
         org.jclouds.openstack.nova.v2_0.domain.SecurityGroup.builder()
            .tenantId("tenant")
            .id(SOME_GROUP_ID)
            .name(SOME_GROUP)
            .description("some-description")
            .rules(ruleToConvert)
            .build();

      return origGroup;
   }

   public static org.jclouds.openstack.nova.v2_0.domain.SecurityGroup securityGroupWithCidr() {
      SecurityGroupRule ruleToConvert = SecurityGroupRule.builder()
         .id("some-other-rule-id")
         .ipProtocol(IpProtocol.TCP)
         .fromPort(10)
         .toPort(20)
         .ipRange(IP_RANGE)
         .parentGroupId(SOME_OTHER_GROUP_ID)
         .build();

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup origGroup =
         org.jclouds.openstack.nova.v2_0.domain.SecurityGroup.builder()
            .tenantId("tenant")
            .id(SOME_OTHER_GROUP_ID)
            .name(SOME_OTHER_GROUP)
            .description("some-description")
            .rules(ruleToConvert)
            .build();

      return origGroup;
   }

   @Test
   public void testApplyWithGroup() {
      NovaSecurityGroupInRegionToSecurityGroup parser = createGroupParser();

      final org.jclouds.openstack.nova.v2_0.domain.SecurityGroup otherGroup = securityGroupWithCidr();
      SecurityGroupInRegion origGroup = new SecurityGroupInRegion(securityGroupWithGroup(), region.getId(), allGroups);

      SecurityGroup newGroup = parser.apply(origGroup);

      assertEquals(newGroup.getId(), origGroup.getRegion() + "/" + origGroup.getSecurityGroup().getId());
      assertEquals(newGroup.getProviderId(), origGroup.getSecurityGroup().getId());
      assertEquals(newGroup.getName(), origGroup.getSecurityGroup().getName());
      assertEquals(newGroup.getOwnerId(), origGroup.getSecurityGroup().getTenantId());
      final IpPermission permission = Iterables.getOnlyElement(newGroup.getIpPermissions());
      assertEquals(Iterables.getOnlyElement(permission.getGroupIds()), region.getId() + "/" + otherGroup.getId());
      assertEquals(permission.getFromPort(), 10);
      assertEquals(permission.getToPort(), 20);
      assertTrue(permission.getCidrBlocks().isEmpty());
      assertEquals(newGroup.getLocation().getId(), origGroup.getRegion());
   }

   @Test
   public void testApplyWithCidr() {

      NovaSecurityGroupInRegionToSecurityGroup parser = createGroupParser();

      SecurityGroupInRegion origGroup = new SecurityGroupInRegion(securityGroupWithCidr(), region.getId(), allGroups);

      SecurityGroup newGroup = parser.apply(origGroup);

      assertEquals(newGroup.getId(), origGroup.getRegion() + "/" + origGroup.getSecurityGroup().getId());
      assertEquals(newGroup.getProviderId(), origGroup.getSecurityGroup().getId());
      assertEquals(newGroup.getName(), origGroup.getSecurityGroup().getName());
      assertEquals(newGroup.getOwnerId(), origGroup.getSecurityGroup().getTenantId());
      final IpPermission permission = Iterables.getOnlyElement(newGroup.getIpPermissions());
      assertEquals(permission.getFromPort(), 10);
      assertEquals(permission.getToPort(), 20);
      assertEquals(Iterables.getOnlyElement(permission.getCidrBlocks()), IP_RANGE);
      assertTrue(permission.getGroupIds().isEmpty());
      assertEquals(newGroup.getLocation().getId(), origGroup.getRegion());
   }

   private NovaSecurityGroupInRegionToSecurityGroup createGroupParser() {

      NovaSecurityGroupInRegionToSecurityGroup parser = new NovaSecurityGroupInRegionToSecurityGroup(locationIndex);

      return parser;
   }
}
