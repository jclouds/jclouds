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
package org.jclouds.azurecompute.arm.compute.extensions;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.SecurityGroupAvailablePredicateFactory;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroupProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Access;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Direction;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Protocol;
import org.jclouds.azurecompute.arm.domain.RegionAndId;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.features.NetworkSecurityGroupApi;
import org.jclouds.azurecompute.arm.features.NetworkSecurityRuleApi;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

public class AzureComputeSecurityGroupExtension implements SecurityGroupExtension {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AzureComputeApi api;
   private final Function<NetworkSecurityGroup, SecurityGroup> securityGroupConverter;
   private final Supplier<Set<? extends Location>> locations;
   private final SecurityGroupAvailablePredicateFactory securityGroupAvailable;
   private final Predicate<URI> resourceDeleted;
   private final LoadingCache<String, ResourceGroup> resourceGroupMap;

   @Inject
   AzureComputeSecurityGroupExtension(AzureComputeApi api, @Memoized Supplier<Set<? extends Location>> locations,
         Function<NetworkSecurityGroup, SecurityGroup> groupConverter,
         SecurityGroupAvailablePredicateFactory securityRuleAvailable,
         @Named(TIMEOUT_RESOURCE_DELETED) Predicate<URI> resourceDeleted,
         LoadingCache<String, ResourceGroup> resourceGroupMap) {
      this.api = api;
      this.locations = locations;
      this.securityGroupConverter = groupConverter;
      this.securityGroupAvailable = securityRuleAvailable;
      this.resourceDeleted = resourceDeleted;
      this.resourceGroupMap = resourceGroupMap;
   }

   @Override
   public Set<SecurityGroup> listSecurityGroups() {
      return ImmutableSet.copyOf(concat(transform(locations.get(), new Function<Location, Set<SecurityGroup>>() {
         @Override
         public Set<SecurityGroup> apply(Location input) {
            return listSecurityGroupsInLocation(input);
         }
      })));
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsInLocation(Location location) {
      logger.debug(">> getting security groups for %s...", location);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(location.getId());
      List<NetworkSecurityGroup> networkGroups = api.getNetworkSecurityGroupApi(resourceGroup.name()).list();
      return ImmutableSet.copyOf(transform(filter(networkGroups, notNull()), securityGroupConverter));
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsForNode(String nodeId) {
      logger.debug(">> getting security groups for node %s...", nodeId);

      final RegionAndId regionAndId = RegionAndId.fromSlashEncoded(nodeId);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());

      VirtualMachine vm = api.getVirtualMachineApi(resourceGroup.name()).get(regionAndId.id());
      if (vm == null) {
         throw new IllegalArgumentException("Node " + regionAndId.id() + " was not found");
      }
      List<IdReference> networkInterfacesIdReferences = vm.properties().networkProfile().networkInterfaces();
      List<NetworkSecurityGroup> networkGroups = new ArrayList<NetworkSecurityGroup>();

      for (IdReference networkInterfaceCardIdReference : networkInterfacesIdReferences) {
         String nicName = Iterables.getLast(Splitter.on("/").split(networkInterfaceCardIdReference.id()));
         NetworkInterfaceCard card = api.getNetworkInterfaceCardApi(resourceGroup.name()).get(nicName);
         if (card != null && card.properties().networkSecurityGroup() != null) {
            String secGroupName = Iterables.getLast(Splitter.on("/").split(
                  card.properties().networkSecurityGroup().id()));
            NetworkSecurityGroup group = api.getNetworkSecurityGroupApi(resourceGroup.name()).get(secGroupName);
            networkGroups.add(group);
         }
      }

      return ImmutableSet.copyOf(transform(filter(networkGroups, notNull()), securityGroupConverter));
   }

   @Override
   public SecurityGroup getSecurityGroupById(String id) {
      logger.debug(">> getting security group %s...", id);
      final RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());
      NetworkSecurityGroup securityGroup = api.getNetworkSecurityGroupApi(resourceGroup.name()).get(regionAndId.id());
      return securityGroup == null ? null : securityGroupConverter.apply(securityGroup);
   }

   @Override
   public SecurityGroup createSecurityGroup(String name, Location location) {
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(location.getId());

      logger.debug(">> creating security group %s in %s...", name, location);

      SecurityGroupBuilder builder = new SecurityGroupBuilder();
      builder.name(name);
      builder.location(location);

      return securityGroupConverter.apply(api.getNetworkSecurityGroupApi(resourceGroup.name()).createOrUpdate(name,
            location.getId(), null, NetworkSecurityGroupProperties.builder().build()));
   }

   @Override
   public boolean removeSecurityGroup(String id) {
      logger.debug(">> deleting security group %s...", id);

      final RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());
      URI uri = api.getNetworkSecurityGroupApi(resourceGroup.name()).delete(regionAndId.id());
      return resourceDeleted.apply(uri);
   }

   @Override
   public SecurityGroup addIpPermission(IpPermission ipPermission, SecurityGroup group) {
      return addIpPermission(ipPermission.getIpProtocol(), ipPermission.getFromPort(), ipPermission.getToPort(),
            ipPermission.getTenantIdGroupNamePairs(), ipPermission.getCidrBlocks(), ipPermission.getGroupIds(), group);
   }

   @Override
   public SecurityGroup removeIpPermission(IpPermission ipPermission, SecurityGroup group) {
      return removeIpPermission(ipPermission.getIpProtocol(), ipPermission.getFromPort(), ipPermission.getToPort(),
            ipPermission.getTenantIdGroupNamePairs(), ipPermission.getCidrBlocks(), ipPermission.getGroupIds(), group);
   }

   @Override
   public SecurityGroup addIpPermission(IpProtocol protocol, int startPort, int endPort,
         Multimap<String, String> tenantIdGroupNamePairs, Iterable<String> ipRanges, Iterable<String> groupIds,
         SecurityGroup group) {
      String portRange = startPort + "-" + endPort;
      String ruleName = protocol + "-" + portRange;

      logger.debug(">> adding ip permission [%s] to %s...", ruleName, group.getName());

      // TODO: Support Azure network tags somehow?

      final RegionAndId regionAndId = RegionAndId.fromSlashEncoded(group.getId());
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());

      NetworkSecurityGroupApi groupApi = api.getNetworkSecurityGroupApi(resourceGroup.name());
      NetworkSecurityGroup networkSecurityGroup = groupApi.get(regionAndId.id());

      if (networkSecurityGroup == null) {
         throw new IllegalArgumentException("Security group " + group.getName() + " was not found");
      }

      NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourceGroup.name(), networkSecurityGroup.name());
      int nextPriority = getRuleStartingPriority(networkSecurityGroup);

      for (String ipRange : ipRanges) {
         NetworkSecurityRuleProperties properties = NetworkSecurityRuleProperties.builder()
               .protocol(Protocol.fromValue(protocol.name()))
               .sourceAddressPrefix(ipRange)
               .sourcePortRange("*")
               .destinationAddressPrefix("*")
               .destinationPortRange(portRange)
               .direction(Direction.Inbound)
               .access(Access.Allow)
               .priority(nextPriority++)
               .build();

         logger.debug(">> creating network security rule %s for %s...", ruleName, ipRange);

         ruleApi.createOrUpdate(ruleName, properties);

         checkState(securityGroupAvailable.create(resourceGroup.name()).apply(networkSecurityGroup.name()),
               "Security group was not updated in the configured timeout");
      }

      return getSecurityGroupById(group.getId());
   }

   @Override
   public SecurityGroup removeIpPermission(final IpProtocol protocol, int startPort, int endPort,
         Multimap<String, String> tenantIdGroupNamePairs, final Iterable<String> ipRanges, Iterable<String> groupIds,
         SecurityGroup group) {
      final String portRange = startPort + "-" + endPort;
      String ruleName = protocol + "-" + portRange;

      logger.debug(">> deleting ip permissions matching [%s] from %s...", ruleName, group.getName());

      final RegionAndId regionAndId = RegionAndId.fromSlashEncoded(group.getId());
      ResourceGroup resourceGroup = resourceGroupMap.getUnchecked(regionAndId.region());

      NetworkSecurityGroupApi groupApi = api.getNetworkSecurityGroupApi(resourceGroup.name());
      NetworkSecurityGroup networkSecurityGroup = groupApi.get(regionAndId.id());

      if (networkSecurityGroup == null) {
         throw new IllegalArgumentException("Security group " + group.getName() + " was not found");
      }

      NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourceGroup.name(), networkSecurityGroup.name());
      Iterable<NetworkSecurityRule> rules = filter(ruleApi.list(), new Predicate<NetworkSecurityRule>() {
         @Override
         public boolean apply(NetworkSecurityRule input) {
            NetworkSecurityRuleProperties props = input.properties();
            return Objects.equal(portRange, props.destinationPortRange())
                  && Objects.equal(Protocol.fromValue(protocol.name()), props.protocol())
                  && Objects.equal(Direction.Inbound, props.direction()) //
                  && Objects.equal(Access.Allow, props.access())
                  && any(ipRanges, equalTo(props.sourceAddressPrefix().replace("*", "0.0.0.0/0")));
         }
      });

      for (NetworkSecurityRule matchingRule : rules) {
         logger.debug(">> deleting network security rule %s from %s...", matchingRule.name(), group.getName());
         ruleApi.delete(matchingRule.name());
         checkState(securityGroupAvailable.create(resourceGroup.name()).apply(networkSecurityGroup.name()),
               "Security group was not updated in the configured timeout");
      }

      return getSecurityGroupById(group.getId());
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

   private int getRuleStartingPriority(NetworkSecurityGroup securityGroup) {
      List<NetworkSecurityRule> existingRules = securityGroup.properties().securityRules();
      return existingRules.isEmpty() ? 100 : rulesByPriority().max(existingRules).properties().priority() + 1;
   }

   private static Ordering<NetworkSecurityRule> rulesByPriority() {
      return new Ordering<NetworkSecurityRule>() {
         @Override
         public int compare(NetworkSecurityRule left, NetworkSecurityRule right) {
            return left.properties().priority() - right.properties().priority();
         }
      };
   }

}
