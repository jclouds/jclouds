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
import static org.jclouds.openstack.nova.v2_0.compute.functions.NovaSecurityGroupToSecurityGroupTest.securityGroupWithCidr;
import static org.jclouds.openstack.nova.v2_0.compute.functions.NovaSecurityGroupToSecurityGroupTest.securityGroupWithGroup;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "NovaSecurityGroupInRegionToSecurityGroupTest")
public class NovaSecurityGroupInRegionToSecurityGroupTest {

   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("openstack-nova")
           .description("openstack-nova").build();
   Location region = new LocationBuilder().id("az-1.region-a.geo-1").description("az-1.region-a.geo-1")
           .scope(LocationScope.REGION).parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
           .<String, Location>of("az-1.region-a.geo-1", region));


   @Test
   public void testApplyWithGroup() {
      NovaSecurityGroupInRegionToSecurityGroup parser = createGroupParser();

      SecurityGroupInRegion origGroup = new SecurityGroupInRegion(securityGroupWithGroup(), region.getId());

      SecurityGroup newGroup = parser.apply(origGroup);

      assertEquals(newGroup.getId(), origGroup.getRegion() + "/" + origGroup.getSecurityGroup().getId());
      assertEquals(newGroup.getProviderId(), origGroup.getSecurityGroup().getId());
      assertEquals(newGroup.getName(), origGroup.getSecurityGroup().getName());
      assertEquals(newGroup.getOwnerId(), origGroup.getSecurityGroup().getTenantId());
      assertEquals(newGroup.getIpPermissions(), ImmutableSet.copyOf(transform(origGroup.getSecurityGroup().getRules(),
              NovaSecurityGroupToSecurityGroupTest.ruleConverter)));
      assertEquals(newGroup.getLocation().getId(), origGroup.getRegion());
   }

   @Test
   public void testApplyWithCidr() {

      NovaSecurityGroupInRegionToSecurityGroup parser = createGroupParser();

      SecurityGroupInRegion origGroup = new SecurityGroupInRegion(securityGroupWithCidr(), region.getId());

      SecurityGroup newGroup = parser.apply(origGroup);

      assertEquals(newGroup.getId(), origGroup.getRegion() + "/" + origGroup.getSecurityGroup().getId());
      assertEquals(newGroup.getProviderId(), origGroup.getSecurityGroup().getId());
      assertEquals(newGroup.getName(), origGroup.getSecurityGroup().getName());
      assertEquals(newGroup.getOwnerId(), origGroup.getSecurityGroup().getTenantId());
      assertEquals(newGroup.getIpPermissions(), ImmutableSet.copyOf(transform(origGroup.getSecurityGroup().getRules(),
              NovaSecurityGroupToSecurityGroupTest.ruleConverter)));
      assertEquals(newGroup.getLocation().getId(), origGroup.getRegion());
   }

   private NovaSecurityGroupInRegionToSecurityGroup createGroupParser() {
      NovaSecurityGroupToSecurityGroup baseParser = new NovaSecurityGroupToSecurityGroup(NovaSecurityGroupToSecurityGroupTest.ruleConverter);

      NovaSecurityGroupInRegionToSecurityGroup parser = new NovaSecurityGroupInRegionToSecurityGroup(baseParser, locationIndex);

      return parser;
   }
}
