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
package org.jclouds.openstack.nova.v2_0.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", singleThreaded = true, testName = "FindSecurityGroupInRegionOrCreateTest")
public class FindSecurityGroupInRegionOrCreateTest {

   @Test
   public void testWhenNotFoundCreatesANewSecurityGroup() throws Exception {
      SecurityGroup securityGroup = createMock(SecurityGroup.class);

      RegionSecurityGroupNameAndPorts input = new RegionSecurityGroupNameAndPorts("region", "groupName", ImmutableSet
               .<Integer> of(22, 8080));

      Function<RegionSecurityGroupNameAndPorts, SecurityGroup> groupCreator = Functions.forMap(ImmutableMap
               .of(input, securityGroup));

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(groupCreator);

      assertEquals(parser.load(input), securityGroup);

   }

   @Test(enabled = false) // TODO does it apply now?
   public void testWhenFoundReturnsSecurityGroupFromAtomicReferenceValueUpdatedDuringPredicateCheck() throws Exception {
      SecurityGroup expected = new SecurityGroupBuilder().id("region/id").name("name").build();
      final SecurityGroupInRegion securityGroupInRegion = createMock(SecurityGroupInRegion.class);
      final org.jclouds.openstack.nova.v2_0.domain.SecurityGroup novaSecurityGroup = createMock(org.jclouds.openstack.nova.v2_0.domain.SecurityGroup.class);


      expect(novaSecurityGroup.getId()).andReturn("id").anyTimes();
      expect(novaSecurityGroup.getName()).andReturn("name");
      replay(novaSecurityGroup);

      expect(securityGroupInRegion.getRegion()).andReturn("region");
      expect(securityGroupInRegion.getSecurityGroup()).andReturn(novaSecurityGroup).anyTimes();
      replay(securityGroupInRegion);

      RegionAndName input = RegionAndName.fromRegionAndName("region", "groupName");

      Function<RegionSecurityGroupNameAndPorts, SecurityGroup> groupCreator = new Function<RegionSecurityGroupNameAndPorts, SecurityGroup>() {

         @Override
         public SecurityGroup apply(RegionSecurityGroupNameAndPorts input) {
            fail();
            return null;
         }

      };

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(groupCreator);

      assertEquals(parser.load(input), expected);

   }


   @Test(expectedExceptions = IllegalStateException.class, enabled = false) // TODO does it apply now?
   public void testWhenFoundPredicateMustUpdateAtomicReference() throws Exception {

      RegionAndName input = RegionAndName.fromRegionAndName("region", "groupName");

      Function<RegionSecurityGroupNameAndPorts, SecurityGroup> groupCreator = new Function<RegionSecurityGroupNameAndPorts, SecurityGroup>() {

         @Override
         public SecurityGroup apply(RegionSecurityGroupNameAndPorts input) {
            fail();
            return null;
         }

      };

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(groupCreator);

      parser.load(input);

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenNotFoundInputMustBeRegionSecurityGroupNameAndPorts() throws Exception {

      RegionAndName input = RegionAndName.fromRegionAndName("region", "groupName");

      Function<RegionSecurityGroupNameAndPorts, SecurityGroup> groupCreator = new Function<RegionSecurityGroupNameAndPorts, SecurityGroup>() {

         @Override
         public SecurityGroup apply(RegionSecurityGroupNameAndPorts input) {
            fail();
            return null;
         }

      };

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(groupCreator);

      parser.load(input);

   }
}
