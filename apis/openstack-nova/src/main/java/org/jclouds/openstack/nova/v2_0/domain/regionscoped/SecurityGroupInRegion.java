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
package org.jclouds.openstack.nova.v2_0.domain.regionscoped;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;

import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SecurityGroupInRegion extends RegionAndName {
   protected final SecurityGroup securityGroup;

   protected final Multimap<TenantIdAndName, SecurityGroup> groupsByName;

   public SecurityGroupInRegion(SecurityGroup securityGroup, String regionId, Iterable<SecurityGroup> allGroupsInRegion) {
      super(regionId, checkNotNull(securityGroup, "securityGroup").getName());
      this.securityGroup = securityGroup;
      this.groupsByName = HashMultimap.create();
      for (SecurityGroup groupInRegion : allGroupsInRegion) {
         final TenantIdAndName tenantIdAndName = TenantIdAndName.builder()
            .tenantId(groupInRegion.getTenantId())
            .name(groupInRegion.getName())
            .build();
         this.groupsByName.put(tenantIdAndName, groupInRegion);
      }
   }

   public SecurityGroup getSecurityGroup() {
      return securityGroup;
   }

   /**
    * Returns a map of group {@link TenantIdAndName}s to groups.
    *
    * The returned value is a collection, to take into account the possibility that certain clouds
    * may permit duplicate group names.
    *
    * @return The map of names to (collections of) groups.
    */
   public Map<TenantIdAndName, Collection<SecurityGroup>> getGroupsByName() {
       return groupsByName.asMap();
   }

   // superclass hashCode/equals are good enough, and help us use RegionAndName and SecurityGroupInRegion
   // interchangeably as Map keys

   @Override
   protected ToStringHelper string() {
      return super.string()
          .add("securityGroup", securityGroup)
          .add("groupsByName", groupsByName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
