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

import javax.inject.Inject;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionSecurityGroupNameAndPorts;

import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;

public class FindSecurityGroupOrCreate extends CacheLoader<RegionAndName, SecurityGroup> {

   protected final Function<RegionSecurityGroupNameAndPorts, SecurityGroup> groupCreator;

   @Inject
   public FindSecurityGroupOrCreate(
            Function<RegionSecurityGroupNameAndPorts, SecurityGroup> groupCreator) {
      this.groupCreator = checkNotNull(groupCreator, "groupCreator");
   }

   @Override
   public SecurityGroup load(RegionAndName in) {
         return createNewSecurityGroup(in);
   }

   private SecurityGroup createNewSecurityGroup(RegionAndName in) {
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
