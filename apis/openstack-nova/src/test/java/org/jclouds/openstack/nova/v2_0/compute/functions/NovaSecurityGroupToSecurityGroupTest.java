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

import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "NovaSecurityGroupToSecurityGroupTest")
public class NovaSecurityGroupToSecurityGroupTest {

   private static final Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("openstack-nova")
           .description("openstack-nova").build();
   private static final Location region = new LocationBuilder().id("az-1.region-a.geo-1").description("az-1.region-a.geo-1")
           .scope(LocationScope.REGION).parent(provider).build();
   private static final Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
           .<String, Location>of("az-1.region-a.geo-1", region));


   private static final Predicate<AtomicReference<RegionAndName>> returnSecurityGroupExistsInRegion = Predicates.alwaysTrue();

   private static final Map<RegionAndName, SecurityGroupInRegion> groupMap = ImmutableMap.of(
           RegionAndName.fromRegionAndName("az-1.region-a.geo-1", "some-group"), new SecurityGroupInRegion(securityGroupWithGroup(), "az-1.region-a.geo-1"),
           RegionAndName.fromRegionAndName("az-1.region-a.geo-1", "some-other-group"), new SecurityGroupInRegion(securityGroupWithCidr(), "az-1.region-a.geo-1"));

   // weird compilation error means have to declare extra generics for call to build() - see https://bugs.eclipse.org/bugs/show_bug.cgi?id=365818
   private static final Supplier <LoadingCache<RegionAndName, SecurityGroupInRegion>> groupCache = Suppliers.<LoadingCache<RegionAndName, SecurityGroupInRegion>> ofInstance(
           CacheBuilder.newBuilder().<RegionAndName, SecurityGroupInRegion>build(CacheLoader.from(Functions.forMap(groupMap))));

   public static final SecurityGroupRuleToIpPermission ruleConverter = new SecurityGroupRuleToIpPermission(returnSecurityGroupExistsInRegion, locationIndex,
           groupCache.get());

   public static org.jclouds.openstack.nova.v2_0.domain.SecurityGroup securityGroupWithGroup() {
      TenantIdAndName group = TenantIdAndName.builder().tenantId("tenant").name("some-other-group").build();

      SecurityGroupRule ruleToConvert = SecurityGroupRule.builder()
              .id("some-id")
              .ipProtocol(IpProtocol.TCP)
              .fromPort(10)
              .toPort(20)
              .group(group)
              .parentGroupId("some-other-id")
              .build();

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup origGroup = org.jclouds.openstack.nova.v2_0.domain.SecurityGroup.builder()
              .tenantId("tenant")
              .id("some-id")
              .name("some-group")
              .description("some-description")
              .rules(ruleToConvert)
              .build();

      return origGroup;
   }

   public static org.jclouds.openstack.nova.v2_0.domain.SecurityGroup securityGroupWithCidr() {
      SecurityGroupRule ruleToConvert = SecurityGroupRule.builder()
              .id("some-id")
              .ipProtocol(IpProtocol.TCP)
              .fromPort(10)
              .toPort(20)
              .ipRange("0.0.0.0/0")
              .parentGroupId("some-other-id")
              .build();

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup origGroup = org.jclouds.openstack.nova.v2_0.domain.SecurityGroup.builder()
              .tenantId("tenant")
              .id("some-id")
              .name("some-other-group")
              .description("some-description")
              .rules(ruleToConvert)
              .build();

      return origGroup;
   }

   @Test
   public void testApplyWithGroup() {
      NovaSecurityGroupToSecurityGroup parser = createGroupParser();

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup origGroup = securityGroupWithGroup();

      SecurityGroup newGroup = parser.apply(origGroup);

      assertEquals(newGroup.getId(), origGroup.getId());
      assertEquals(newGroup.getProviderId(), origGroup.getId());
      assertEquals(newGroup.getName(), origGroup.getName());
      assertEquals(newGroup.getOwnerId(), origGroup.getTenantId());
      assertEquals(newGroup.getIpPermissions(), ImmutableSet.copyOf(transform(origGroup.getRules(), ruleConverter)));
   }

   @Test
   public void testApplyWithCidr() {

      NovaSecurityGroupToSecurityGroup parser = createGroupParser();

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup origGroup = securityGroupWithCidr();

      SecurityGroup group = parser.apply(origGroup);

      assertEquals(group.getId(), origGroup.getId());
      assertEquals(group.getProviderId(), origGroup.getId());
      assertEquals(group.getName(), origGroup.getName());
      assertEquals(group.getOwnerId(), origGroup.getTenantId());
      assertEquals(group.getIpPermissions(), ImmutableSet.copyOf(transform(origGroup.getRules(), ruleConverter)));
   }

   private NovaSecurityGroupToSecurityGroup createGroupParser() {
      NovaSecurityGroupToSecurityGroup parser = new NovaSecurityGroupToSecurityGroup(ruleConverter);

      return parser;
   }

}
