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
package org.jclouds.openstack.nova.v2_0.compute.extensions;


import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.nameIn;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleCidr;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleEndPort;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleGroup;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleProtocol;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleStartPort;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.location.Region;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsApi;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * An extension to compute service to allow for the manipulation of {@link org.jclouds.compute.domain.SecurityGroup}s. Implementation
 * is optional by providers.
 */
public class NovaSecurityGroupExtension implements SecurityGroupExtension {

   protected final NovaApi api;
   protected final ListeningExecutorService userExecutor;
   protected final Supplier<Set<String>> regionIds;
   protected final Function<SecurityGroupInRegion, SecurityGroup> groupConverter;
   protected final LoadingCache<RegionAndName, SecurityGroupInRegion> groupCreator;
   protected final GroupNamingConvention.Factory namingConvention;

   @Inject
   public NovaSecurityGroupExtension(NovaApi api,
                                    @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                    @Region Supplier<Set<String>> regionIds,
                                    Function<SecurityGroupInRegion, SecurityGroup> groupConverter,
                                    LoadingCache<RegionAndName, SecurityGroupInRegion> groupCreator,
                                    GroupNamingConvention.Factory namingConvention) {

      this.api = checkNotNull(api, "api");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.regionIds = checkNotNull(regionIds, "regionIds");
      this.groupConverter = checkNotNull(groupConverter, "groupConverter");
      this.groupCreator = checkNotNull(groupCreator, "groupCreator");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   public Set<SecurityGroup> listSecurityGroups() {
      Iterable<? extends SecurityGroupInRegion> rawGroups = pollSecurityGroups();
      Iterable<SecurityGroup> groups = transform(filter(rawGroups, notNull()),
              groupConverter);
      return ImmutableSet.copyOf(groups);
   }


   @Override
   public Set<SecurityGroup> listSecurityGroupsInLocation(final Location location) {
      String region = location.getId();
      if (region == null) {
         return ImmutableSet.of();
      }
      return listSecurityGroupsInLocation(region);
   }

   public Set<SecurityGroup> listSecurityGroupsInLocation(String region) {
      Iterable<? extends SecurityGroupInRegion> rawGroups = pollSecurityGroupsByRegion(region);
      Iterable<SecurityGroup> groups = transform(filter(rawGroups, notNull()),
              groupConverter);
      return ImmutableSet.copyOf(groups);
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsForNode(String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(checkNotNull(id, "id"));
      String region = regionAndId.getRegion();
      String instanceId = regionAndId.getId();

      Optional<? extends ServerWithSecurityGroupsApi> serverApi = api.getServerWithSecurityGroupsApi(region);
      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupApi(region);

      if (!serverApi.isPresent() || !sgApi.isPresent()) {
         return ImmutableSet.of();
      }

      ServerWithSecurityGroups instance = serverApi.get().get(instanceId);
      if (instance == null) {
         return ImmutableSet.of();
      }

      Set<String> groupNames = instance.getSecurityGroupNames();
      Set<? extends SecurityGroupInRegion> rawGroups =
              sgApi.get().list().filter(nameIn(groupNames)).transform(groupToGroupInRegion(region)).toSet();

      return ImmutableSet.copyOf(transform(filter(rawGroups, notNull()), groupConverter));
   }

   @Override
   public SecurityGroup getSecurityGroupById(String id) {
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(checkNotNull(id, "id"));
      String region = regionAndId.getRegion();
      String groupId = regionAndId.getId();

      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupApi(region);

      if (!sgApi.isPresent()) {
         return null;
      }

      SecurityGroupInRegion rawGroup = new SecurityGroupInRegion(sgApi.get().get(groupId), region);

      return groupConverter.apply(rawGroup);
   }

   @Override
   public SecurityGroup createSecurityGroup(String name, Location location) {
      String region = location.getId();
      if (region == null) {
         return null;
      }
      return createSecurityGroup(name, region);
   }

   public SecurityGroup createSecurityGroup(String name, String region) {
      String markerGroup = namingConvention.create().sharedNameForGroup(name);
      RegionSecurityGroupNameAndPorts regionAndName = new RegionSecurityGroupNameAndPorts(region, markerGroup, ImmutableSet.<Integer> of());

      SecurityGroupInRegion rawGroup = groupCreator.getUnchecked(regionAndName);
      return groupConverter.apply(rawGroup);
   }

   @Override
   public boolean removeSecurityGroup(String id) {
      checkNotNull(id, "id");
      RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      String region = regionAndId.getRegion();
      String groupId = regionAndId.getId();

      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupApi(region);

      if (!sgApi.isPresent()) {
         return false;
      }

      if (sgApi.get().get(groupId) == null) {
         return false;
      }

      sgApi.get().delete(groupId);
      // TODO: test this clear happens
      groupCreator.invalidate(new RegionSecurityGroupNameAndPorts(region, groupId, ImmutableSet.<Integer> of()));
      return true;
   }

   @Override
   public SecurityGroup addIpPermission(IpPermission ipPermission, SecurityGroup group) {
      String region = group.getLocation().getId();
      RegionAndId groupRegionAndId = RegionAndId.fromSlashEncoded(group.getId());
      String id = groupRegionAndId.getId();
      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupApi(region);

      if (!sgApi.isPresent()) {
         return null;
      }

      if (!ipPermission.getCidrBlocks().isEmpty()) {
         for (String cidr : ipPermission.getCidrBlocks()) {
            sgApi.get().createRuleAllowingCidrBlock(id,
                    Ingress.builder()
                            .ipProtocol(ipPermission.getIpProtocol())
                            .fromPort(ipPermission.getFromPort())
                            .toPort(ipPermission.getToPort())
                            .build(),
                    cidr);
         }
      }

      if (!ipPermission.getGroupIds().isEmpty()) {
         for (String regionAndGroupRaw : ipPermission.getGroupIds()) {
            RegionAndId regionAndId = RegionAndId.fromSlashEncoded(regionAndGroupRaw);
            String groupId = regionAndId.getId();
            sgApi.get().createRuleAllowingSecurityGroupId(id,
                    Ingress.builder()
                            .ipProtocol(ipPermission.getIpProtocol())
                            .fromPort(ipPermission.getFromPort())
                            .toPort(ipPermission.getToPort())
                            .build(),
                    groupId);
         }
      }

      return getSecurityGroupById(RegionAndId.fromRegionAndId(region, id).slashEncode());
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
      String region = group.getLocation().getId();
      RegionAndId groupRegionAndId = RegionAndId.fromSlashEncoded(group.getId());
      String id = groupRegionAndId.getId();

      Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupApi(region);

      if (!sgApi.isPresent()) {
         return null;
      }

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup securityGroup = sgApi.get().get(id);

      if (!ipPermission.getCidrBlocks().isEmpty()) {
         for (String cidr : ipPermission.getCidrBlocks()) {
            for (SecurityGroupRule rule : filter(securityGroup.getRules(),
                    and(ruleCidr(cidr), ruleProtocol(ipPermission.getIpProtocol()),
                            ruleStartPort(ipPermission.getFromPort()),
                            ruleEndPort(ipPermission.getToPort())))) {
               sgApi.get().deleteRule(rule.getId());
            }
         }
      }

      if (!ipPermission.getGroupIds().isEmpty()) {
         for (String groupId : ipPermission.getGroupIds()) {
            for (SecurityGroupRule rule : filter(securityGroup.getRules(),
                    and(ruleGroup(groupId), ruleProtocol(ipPermission.getIpProtocol()),
                            ruleStartPort(ipPermission.getFromPort()),
                            ruleEndPort(ipPermission.getToPort())))) {
               sgApi.get().deleteRule(rule.getId());
            }

         }
      }

      return getSecurityGroupById(RegionAndId.fromRegionAndId(region, id).slashEncode());
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
      return false;
   }

   @Override
   public boolean supportsTenantIdGroupIdPairs() {
      return false;
   }

   @Override
   public boolean supportsGroupIds() {
      return true;
   }

   @Override
   public boolean supportsPortRangesForGroups() {
      return false;
   }

   @Override
   public boolean supportsExclusionCidrBlocks() {
       return false;
   }

   protected Iterable<? extends SecurityGroupInRegion> pollSecurityGroups() {
      Iterable<? extends Set<? extends SecurityGroupInRegion>> groups
              = transform(regionIds.get(), allSecurityGroupsInRegion());

      return concat(groups);
   }


   protected Iterable<? extends SecurityGroupInRegion> pollSecurityGroupsByRegion(String region) {
      return allSecurityGroupsInRegion().apply(region);
   }

   protected Function<String, Set<? extends SecurityGroupInRegion>> allSecurityGroupsInRegion() {
      return new Function<String, Set<? extends SecurityGroupInRegion>>() {

         @Override
         public Set<? extends SecurityGroupInRegion> apply(final String from) {
            Optional<? extends SecurityGroupApi> sgApi = api.getSecurityGroupApi(from);

            if (!sgApi.isPresent()) {
               return ImmutableSet.of();
            }


            return sgApi.get().list().transform(groupToGroupInRegion(from)).toSet();
         }

      };
   }

   protected Function<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup, SecurityGroupInRegion> groupToGroupInRegion(final String region) {
      return new Function<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup, SecurityGroupInRegion>() {
         @Override
         public SecurityGroupInRegion apply(org.jclouds.openstack.nova.v2_0.domain.SecurityGroup group) {
            return new SecurityGroupInRegion(group, region);
         }
      };
   }

}
