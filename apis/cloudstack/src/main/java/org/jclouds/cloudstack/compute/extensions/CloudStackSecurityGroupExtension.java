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
package org.jclouds.cloudstack.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.cloudstack.predicates.SecurityGroupPredicates.ruleCidrMatches;
import static org.jclouds.cloudstack.predicates.SecurityGroupPredicates.ruleGroupMatches;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.ZoneAndName;
import org.jclouds.cloudstack.domain.ZoneSecurityGroupNamePortsCidrs;
import org.jclouds.cloudstack.options.ListSecurityGroupsOptions;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * An extension to compute service to allow for the manipulation of {@link org.jclouds.compute.domain.SecurityGroup}s. Implementation
 * is optional by providers.
 */
public class CloudStackSecurityGroupExtension implements SecurityGroupExtension {
   protected final CloudStackApi api;
   protected final Function<org.jclouds.cloudstack.domain.SecurityGroup, SecurityGroup> groupConverter;
   protected final LoadingCache<ZoneAndName, org.jclouds.cloudstack.domain.SecurityGroup> groupCreator;
   protected final GroupNamingConvention.Factory namingConvention;
   protected final Supplier<Set<? extends Location>> locations;
   protected final BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult;
   protected final Predicate<String> jobComplete;

   @Inject
   public CloudStackSecurityGroupExtension(CloudStackApi api,
                                           Function<org.jclouds.cloudstack.domain.SecurityGroup, SecurityGroup> groupConverter,
                                           LoadingCache<ZoneAndName, org.jclouds.cloudstack.domain.SecurityGroup> groupCreator,
                                           GroupNamingConvention.Factory namingConvention,
                                           @Memoized Supplier<Set<? extends Location>> locations,
                                           BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult,
                                           Predicate<String> jobComplete) {
      this.api = checkNotNull(api, "api");
      this.groupConverter = checkNotNull(groupConverter, "groupConverter");
      this.groupCreator = checkNotNull(groupCreator, "groupCreator");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
      this.locations = checkNotNull(locations, "locations");
      this.blockUntilJobCompletesAndReturnResult = checkNotNull(blockUntilJobCompletesAndReturnResult,
              "blockUntilJobCompletesAndReturnResult");
      this.jobComplete = checkNotNull(jobComplete, "jobComplete");
   }

   @Override
   public Set<SecurityGroup> listSecurityGroups() {
      Iterable<? extends org.jclouds.cloudstack.domain.SecurityGroup> rawGroups =
              api.getSecurityGroupApi().listSecurityGroups();
      Iterable<SecurityGroup> groups = transform(filter(rawGroups, notNull()),
              groupConverter);
      return ImmutableSet.copyOf(groups);
   }

   /**
    * Note that for the time being, security groups are not scoped by location in
    * CloudStack, so this will simply return listSecurityGroups().
    *
    * @param location
    * @return security groups
    */
   @Override
   public Set<SecurityGroup> listSecurityGroupsInLocation(final Location location) {
      return listSecurityGroups();
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsForNode(String id) {
      checkNotNull(id, "id");

      Iterable<? extends org.jclouds.cloudstack.domain.SecurityGroup> rawGroups =
              api.getSecurityGroupApi().listSecurityGroups(ListSecurityGroupsOptions.Builder
                      .virtualMachineId(id));

      Iterable<SecurityGroup> groups = transform(filter(rawGroups, notNull()),
              groupConverter);
      return ImmutableSet.copyOf(groups);
   }

   @Override
   public SecurityGroup getSecurityGroupById(String id) {
      checkNotNull(id, "id");

      org.jclouds.cloudstack.domain.SecurityGroup rawGroup
              = api.getSecurityGroupApi().getSecurityGroup(id);

      if (rawGroup == null) {
         return null;
      }

      return groupConverter.apply(rawGroup);
   }

   @Override
   public SecurityGroup createSecurityGroup(String name, Location location) {
      checkNotNull(name, "name");
      checkNotNull(location, "location");

      String markerGroup = namingConvention.create().sharedNameForGroup(name);

      ZoneSecurityGroupNamePortsCidrs zoneAndName = ZoneSecurityGroupNamePortsCidrs.builder()
              .zone(location.getId())
              .name(markerGroup)
              .build();

      return groupConverter.apply(groupCreator.getUnchecked(zoneAndName));
   }

   @Override
   public boolean removeSecurityGroup(String id) {
      checkNotNull(id, "id");

      org.jclouds.cloudstack.domain.SecurityGroup group =
              api.getSecurityGroupApi().getSecurityGroup(id);

      if (group == null) {
         return false;
      }

      for (IngressRule rule : group.getIngressRules()) {
         jobComplete.apply(api.getSecurityGroupApi().revokeIngressRule(rule.getId()));
      }

      api.getSecurityGroupApi().deleteSecurityGroup(id);

      // TODO find something better here maybe - hard to map zones to groups
      for (Location location : locations.get()) {
         groupCreator.invalidate(ZoneSecurityGroupNamePortsCidrs.builder()
                 .zone(location.getId())
                 .name(group.getName())
                 .build());
      }

      return true;
   }

   @Override
   public SecurityGroup addIpPermission(IpPermission ipPermission, SecurityGroup group) {
      checkNotNull(group, "group");
      checkNotNull(ipPermission, "ipPermission");
      String id = checkNotNull(group.getId(), "group.getId()");

      if (!ipPermission.getCidrBlocks().isEmpty()) {
         jobComplete.apply(api.getSecurityGroupApi().authorizeIngressPortsToCIDRs(id,
                 ipPermission.getIpProtocol().toString().toUpperCase(),
                 ipPermission.getFromPort(),
                 ipPermission.getToPort(),
                 ipPermission.getCidrBlocks()));
      }

      if (!ipPermission.getTenantIdGroupNamePairs().isEmpty()) {
         jobComplete.apply(api.getSecurityGroupApi().authorizeIngressPortsToSecurityGroups(id,
                 ipPermission.getIpProtocol().toString().toUpperCase(),
                 ipPermission.getFromPort(),
                 ipPermission.getToPort(),
                 ipPermission.getTenantIdGroupNamePairs()));
      }

      return getSecurityGroupById(id);
   }

   @Override
   public SecurityGroup addIpPermission(IpProtocol protocol, int startPort, int endPort,
                                        Multimap<String, String> tenantIdGroupNamePairs,
                                        Iterable<String> ipRanges,
                                        Iterable<String> groupIds, SecurityGroup group) {
      IpPermission.Builder permBuilder = IpPermission.builder();
      permBuilder.ipProtocol(protocol);
      permBuilder.fromPort(startPort);
      permBuilder.toPort(endPort);
      permBuilder.tenantIdGroupNamePairs(tenantIdGroupNamePairs);
      permBuilder.cidrBlocks(ipRanges);
      permBuilder.groupIds(groupIds);

      return addIpPermission(permBuilder.build(), group);
   }

   @Override
   public SecurityGroup removeIpPermission(IpPermission ipPermission, SecurityGroup group) {
      checkNotNull(group, "group");
      checkNotNull(ipPermission, "ipPermission");
      String id = checkNotNull(group.getId(), "group.getId()");

      org.jclouds.cloudstack.domain.SecurityGroup rawGroup = api.getSecurityGroupApi()
              .getSecurityGroup(id);

      if (!ipPermission.getCidrBlocks().isEmpty()) {
         for (IngressRule rule : filter(rawGroup.getIngressRules(),
                 ruleCidrMatches(ipPermission.getIpProtocol().toString(),
                         ipPermission.getFromPort(),
                         ipPermission.getToPort(),
                         ipPermission.getCidrBlocks()))) {
             jobComplete.apply(api.getSecurityGroupApi().revokeIngressRule(rule.getId()));
         }
      }

      if (!ipPermission.getTenantIdGroupNamePairs().isEmpty()) {
         for (IngressRule rule : filter(rawGroup.getIngressRules(),
                 ruleGroupMatches(ipPermission.getIpProtocol().toString(),
                         ipPermission.getFromPort(),
                         ipPermission.getToPort(),
                         ipPermission.getTenantIdGroupNamePairs()))) {
             jobComplete.apply(api.getSecurityGroupApi().revokeIngressRule(rule.getId()));
         }
      }

      return getSecurityGroupById(id);
   }

   @Override
   public SecurityGroup removeIpPermission(IpProtocol protocol, int startPort, int endPort,
                                           Multimap<String, String> tenantIdGroupNamePairs,
                                           Iterable<String> ipRanges,
                                           Iterable<String> groupIds, SecurityGroup group) {
      IpPermission.Builder permBuilder = IpPermission.builder();
      permBuilder.ipProtocol(protocol);
      permBuilder.fromPort(startPort);
      permBuilder.toPort(endPort);
      permBuilder.tenantIdGroupNamePairs(tenantIdGroupNamePairs);
      permBuilder.cidrBlocks(ipRanges);
      permBuilder.groupIds(groupIds);

      return removeIpPermission(permBuilder.build(), group);
   }

   @Override
   public boolean supportsTenantIdGroupNamePairs() {
      return true;
   }

   @Override
   public boolean supportsTenantIdGroupIdPairs() {
      return false;
   }

   @Override
   public boolean supportsGroupIds() {
      return false;
   }

   @Override
   public boolean supportsPortRangesForGroups() {
      return false;
   }

   @Override
   public boolean supportsExclusionCidrBlocks() {
      return false;
   }

}
