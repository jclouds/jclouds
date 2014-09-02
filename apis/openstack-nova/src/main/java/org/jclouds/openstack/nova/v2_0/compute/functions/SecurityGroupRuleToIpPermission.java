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
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getFirst;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Atomics;

/**
 * A function for transforming a nova-specific SecurityGroupRule into a generic
 * IpPermission object.
 */
public class SecurityGroupRuleToIpPermission implements Function<SecurityGroupRule, IpPermission> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final Predicate<AtomicReference<RegionAndName>> returnSecurityGroupExistsInRegion;
   protected final Supplier<Map<String, Location>> locationIndex;
   LoadingCache<RegionAndName, SecurityGroupInRegion> groupMap;

   @Inject
   public SecurityGroupRuleToIpPermission(@Named("SECURITYGROUP_PRESENT") Predicate<AtomicReference<RegionAndName>> returnSecurityGroupExistsInRegion,
                                          Supplier<Map<String, Location>> locationIndex,
                                          LoadingCache<RegionAndName, SecurityGroupInRegion> groupMap) {
      this.returnSecurityGroupExistsInRegion = checkNotNull(returnSecurityGroupExistsInRegion,
              "returnSecurityGroupExistsInRegion");
      this.locationIndex = checkNotNull(locationIndex, "locationIndex");
      this.groupMap = checkNotNull(groupMap, "groupMap");
   }

   @Override
   public IpPermission apply(SecurityGroupRule rule) {
      IpPermission.Builder builder = IpPermission.builder();
      builder.ipProtocol(rule.getIpProtocol());
      builder.fromPort(rule.getFromPort());
      builder.toPort(rule.getToPort());
      if (rule.getGroup() != null) {
         String region = getFirst(filter(locationIndex.get().keySet(), isSecurityGroupInRegion(rule.getGroup().getName())),
                 null);
         if (region != null) {
            SecurityGroupInRegion group = groupMap.getUnchecked(RegionAndName.fromRegionAndName(region, rule.getGroup().getName()));
            builder.groupId(region + "/" + group.getSecurityGroup().getId());
         }
      }
      if (rule.getIpRange() != null)
         builder.cidrBlock(rule.getIpRange());

      return builder.build();
   }

   protected Predicate<String> isSecurityGroupInRegion(final String groupName) {
      return new Predicate<String>() {

         @Override
         public boolean apply(String region) {
            AtomicReference<RegionAndName> securityGroupInRegionRef = Atomics.newReference(RegionAndName.fromRegionAndName(region, groupName));
            return returnSecurityGroupExistsInRegion.apply(securityGroupInRegionRef);
         }
      };
   }
}
