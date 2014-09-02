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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", singleThreaded = true, testName = "FindSecurityGroupOrCreateTest")
public class FindSecurityGroupOrCreateTest {

   @Test
   public void testWhenNotFoundCreatesANewSecurityGroup() throws Exception {
      Predicate<AtomicReference<RegionAndName>> returnSecurityGroupExistsInRegion = Predicates.alwaysFalse();

      SecurityGroupInRegion securityGroupInRegion = createMock(SecurityGroupInRegion.class);

      RegionSecurityGroupNameAndPorts input = new RegionSecurityGroupNameAndPorts("region", "groupName", ImmutableSet
               .<Integer> of(22, 8080));

      Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion> groupCreator = Functions.forMap(ImmutableMap
               .<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion> of(input, securityGroupInRegion));

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(
               returnSecurityGroupExistsInRegion, groupCreator);

      assertEquals(parser.load(input), securityGroupInRegion);

   }

   @Test
   public void testWhenFoundReturnsSecurityGroupFromAtomicReferenceValueUpdatedDuringPredicateCheck() throws Exception {
      final SecurityGroupInRegion securityGroupInRegion = createMock(SecurityGroupInRegion.class);

      Predicate<AtomicReference<RegionAndName>> returnSecurityGroupExistsInRegion = new Predicate<AtomicReference<RegionAndName>>() {

         @Override
         public boolean apply(AtomicReference<RegionAndName> input) {
            input.set(securityGroupInRegion);
            return true;
         }

      };

      RegionAndName input = RegionAndName.fromRegionAndName("region", "groupName");

      Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion> groupCreator = new Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion>() {

         @Override
         public SecurityGroupInRegion apply(RegionSecurityGroupNameAndPorts input) {
            fail();
            return null;
         }

      };

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(
               returnSecurityGroupExistsInRegion, groupCreator);

      assertEquals(parser.load(input), securityGroupInRegion);

   }


   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenFoundPredicateMustUpdateAtomicReference() throws Exception {

      Predicate<AtomicReference<RegionAndName>> returnSecurityGroupExistsInRegion = Predicates.alwaysTrue();

      RegionAndName input = RegionAndName.fromRegionAndName("region", "groupName");

      Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion> groupCreator = new Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion>() {

         @Override
         public SecurityGroupInRegion apply(RegionSecurityGroupNameAndPorts input) {
            fail();
            return null;
         }

      };

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(
               returnSecurityGroupExistsInRegion, groupCreator);

      parser.load(input);

   }



   @Test(expectedExceptions = IllegalStateException.class)
   public void testWhenNotFoundInputMustBeRegionSecurityGroupNameAndPorts() throws Exception {
      Predicate<AtomicReference<RegionAndName>> returnSecurityGroupExistsInRegion = Predicates.alwaysFalse();

      RegionAndName input = RegionAndName.fromRegionAndName("region", "groupName");

      Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion> groupCreator = new Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion>() {

         @Override
         public SecurityGroupInRegion apply(RegionSecurityGroupNameAndPorts input) {
            fail();
            return null;
         }

      };

      FindSecurityGroupOrCreate parser = new FindSecurityGroupOrCreate(
               returnSecurityGroupExistsInRegion, groupCreator);

      parser.load(input);

   }
}
