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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.Atomics;

public class FindSecurityGroupOrCreate extends CacheLoader<RegionAndName, SecurityGroupInRegion> {

   protected final Predicate<AtomicReference<RegionAndName>> returnSecurityGroupExistsInRegion;
   protected final Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion> groupCreator;

   @Inject
   public FindSecurityGroupOrCreate(
            @Named("SECURITYGROUP_PRESENT") Predicate<AtomicReference<RegionAndName>> returnSecurityGroupExistsInRegion,
            Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion> groupCreator) {
      this.returnSecurityGroupExistsInRegion = checkNotNull(returnSecurityGroupExistsInRegion,
               "returnSecurityGroupExistsInRegion");
      this.groupCreator = checkNotNull(groupCreator, "groupCreator");
   }

   @Override
   public SecurityGroupInRegion load(RegionAndName in) {
      AtomicReference<RegionAndName> securityGroupInRegionRef = Atomics.newReference(checkNotNull(in,
               "regionSecurityGroupNameAndPorts"));
      if (returnSecurityGroupExistsInRegion.apply(securityGroupInRegionRef)) {
         return returnExistingSecurityGroup(securityGroupInRegionRef);
      } else {
         return createNewSecurityGroup(in);
      }
   }

   private SecurityGroupInRegion returnExistingSecurityGroup(AtomicReference<RegionAndName> securityGroupInRegionRef) {
      RegionAndName securityGroupInRegion = securityGroupInRegionRef.get();
      checkState(securityGroupInRegion instanceof SecurityGroupInRegion,
               "programming error: predicate %s should update the atomic reference to the actual security group found",
               returnSecurityGroupExistsInRegion);
      return SecurityGroupInRegion.class.cast(securityGroupInRegion);
   }

   private SecurityGroupInRegion createNewSecurityGroup(RegionAndName in) {
      checkState(
               checkNotNull(in, "regionSecurityGroupNameAndPorts") instanceof RegionSecurityGroupNameAndPorts,
               "programming error: when issuing get to this cacheloader, you need to pass an instance of RegionSecurityGroupNameAndPorts, not %s",
               in);
      RegionSecurityGroupNameAndPorts regionSecurityGroupNameAndPorts = RegionSecurityGroupNameAndPorts.class.cast(in);
      return groupCreator.apply(regionSecurityGroupNameAndPorts);
   }

   @Override
   public String toString() {
      return "returnExistingSecurityGroupInRegionOrCreateAsNeeded()";
   }

}
