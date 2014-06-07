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
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;

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
   protected final Predicate<AtomicReference<ZoneAndName>> returnSecurityGroupExistsInZone;
   protected final Supplier<Map<String, Location>> locationIndex;
   LoadingCache<ZoneAndName, SecurityGroupInZone> groupMap;

   @Inject
   public SecurityGroupRuleToIpPermission(@Named("SECURITYGROUP_PRESENT") Predicate<AtomicReference<ZoneAndName>> returnSecurityGroupExistsInZone,
                                          Supplier<Map<String, Location>> locationIndex,
                                          LoadingCache<ZoneAndName, SecurityGroupInZone> groupMap) {
      this.returnSecurityGroupExistsInZone = checkNotNull(returnSecurityGroupExistsInZone,
              "returnSecurityGroupExistsInZone");
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
         String zone = getFirst(filter(locationIndex.get().keySet(), isSecurityGroupInZone(rule.getGroup().getName())),
                 null);
         if (zone != null) {
            SecurityGroupInZone group = groupMap.getUnchecked(ZoneAndName.fromZoneAndName(zone, rule.getGroup().getName()));
            builder.groupId(zone + "/" + group.getSecurityGroup().getId());
         }
      }
      if (rule.getIpRange() != null)
         builder.cidrBlock(rule.getIpRange());
      
      return builder.build();
   }

   protected Predicate<String> isSecurityGroupInZone(final String groupName) {
      return new Predicate<String>() {

         @Override
         public boolean apply(String zone) {
            AtomicReference<ZoneAndName> securityGroupInZoneRef = Atomics.newReference(ZoneAndName.fromZoneAndName(zone, groupName));
            return returnSecurityGroupExistsInZone.apply(securityGroupInZoneRef);
         }
      };
   }
}
