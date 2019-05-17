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
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractName;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractResourceGroup;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.SecurityGroupAvailablePredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.SecurityGroupRuleAvailablePredicateFactory;
import org.jclouds.azurecompute.arm.compute.domain.ResourceGroupAndName;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkProfile.NetworkInterface;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroupProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Access;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Direction;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties.Protocol;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.features.NetworkSecurityGroupApi;
import org.jclouds.azurecompute.arm.features.NetworkSecurityRuleApi;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

public class AzureComputeSecurityGroupExtension implements SecurityGroupExtension {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AzureComputeApi api;
   private final Function<NetworkSecurityGroup, SecurityGroup> securityGroupConverter;
   private final SecurityGroupAvailablePredicateFactory securityGroupAvailable;
   private final SecurityGroupRuleAvailablePredicateFactory securityGroupRuleAvailable;
   private final Predicate<URI> resourceDeleted;
   private final LoadingCache<String, ResourceGroup> defaultResourceGroup;
   private final Supplier<Set<String>> regionIds;

   @Inject
   AzureComputeSecurityGroupExtension(AzureComputeApi api, Function<NetworkSecurityGroup, SecurityGroup> groupConverter,
         SecurityGroupAvailablePredicateFactory securityGroupAvailable, SecurityGroupRuleAvailablePredicateFactory securityGroupRuleAvailable,
         @Named(TIMEOUT_RESOURCE_DELETED) Predicate<URI> resourceDeleted,
         LoadingCache<String, ResourceGroup> defaultResourceGroup,
         @Region Supplier<Set<String>> regionIds) {
      this.api = api;
      this.securityGroupConverter = groupConverter;
      this.securityGroupAvailable = securityGroupAvailable;
      this.securityGroupRuleAvailable = securityGroupRuleAvailable;
      this.resourceDeleted = resourceDeleted;
      this.defaultResourceGroup = defaultResourceGroup;
      this.regionIds = regionIds;
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsInLocation(Location location) {
      return securityGroupsInLocations(ImmutableSet.of(location.getId()));
   }

   @Override
   public Set<SecurityGroup> listSecurityGroups() {
      return securityGroupsInLocations(regionIds.get());
   }

   private Set<SecurityGroup> securityGroupsInLocations(final Set<String> locations) {
      List<SecurityGroup> securityGroups = new ArrayList<SecurityGroup>();
      for (ResourceGroup rg : api.getResourceGroupApi().list()) {
         securityGroups.addAll(securityGroupsInResourceGroup(rg.name()));
      }
      
      return ImmutableSet.copyOf(filter(securityGroups, new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup input) {
            return input.getLocation() != null && locations.contains(input.getLocation().getId());
         }
      }));
   }

   private Set<SecurityGroup> securityGroupsInResourceGroup(String resourceGroup) {
      List<NetworkSecurityGroup> networkGroups = api.getNetworkSecurityGroupApi(resourceGroup).list();
      return ImmutableSet.copyOf(transform(filter(networkGroups, notNull()), securityGroupConverter));
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsForNode(String nodeId) {
      logger.debug(">> getting security groups for node %s...", nodeId);

      final ResourceGroupAndName resourceGroupAndName = ResourceGroupAndName.fromSlashEncoded(nodeId);

      VirtualMachine vm = api.getVirtualMachineApi(resourceGroupAndName.resourceGroup()).get(
            resourceGroupAndName.name());
      if (vm == null) {
         throw new IllegalArgumentException("Node " + nodeId + " was not found");
      }
      List<NetworkInterface> networkInterfaces = vm.properties().networkProfile().networkInterfaces();
      List<NetworkSecurityGroup> networkGroups = new ArrayList<NetworkSecurityGroup>();

      for (NetworkInterface networkInterfaceCardIdReference : networkInterfaces) {
         String nicName = extractName(networkInterfaceCardIdReference.id());
         String nicResourceGroup = extractResourceGroup(networkInterfaceCardIdReference.id());
         NetworkInterfaceCard card = api.getNetworkInterfaceCardApi(nicResourceGroup).get(nicName);
         if (card != null && card.properties().networkSecurityGroup() != null) {
            String secGroupName = card.properties().networkSecurityGroup().name();
            String sgResourceGroup = card.properties().networkSecurityGroup().resourceGroup();
            NetworkSecurityGroup group = api.getNetworkSecurityGroupApi(sgResourceGroup).get(secGroupName);
            networkGroups.add(group);
         }
      }

      return ImmutableSet.copyOf(transform(filter(networkGroups, notNull()), securityGroupConverter));
   }

   @Override
   public SecurityGroup getSecurityGroupById(String id) {
      logger.debug(">> getting security group %s...", id);
      final ResourceGroupAndName resourceGroupAndName = ResourceGroupAndName.fromSlashEncoded(id);
      NetworkSecurityGroup securityGroup = api.getNetworkSecurityGroupApi(resourceGroupAndName.resourceGroup()).get(
            resourceGroupAndName.name());
      return securityGroup == null ? null : securityGroupConverter.apply(securityGroup);
   }

   @Override
   public SecurityGroup createSecurityGroup(String name, Location location) {
      ResourceGroup resourceGroup = defaultResourceGroup.getUnchecked(location.getId());

      logger.debug(">> creating security group %s in %s...", name, location);

      SecurityGroupBuilder builder = new SecurityGroupBuilder();
      builder.name(name);
      builder.location(location);
      
      NetworkSecurityGroup sg = api.getNetworkSecurityGroupApi(resourceGroup.name()).createOrUpdate(name,
            location.getId(), null, NetworkSecurityGroupProperties.builder().build());
      
      checkState(securityGroupAvailable.create(resourceGroup.name()).apply(name),
            "Security group was not created in the configured timeout");

      return securityGroupConverter.apply(sg);
   }

   @Override
   public boolean removeSecurityGroup(String id) {
      logger.debug(">> deleting security group %s...", id);

      final ResourceGroupAndName resourceGroupAndName = ResourceGroupAndName.fromSlashEncoded(id);
      URI uri = api.getNetworkSecurityGroupApi(resourceGroupAndName.resourceGroup())
            .delete(resourceGroupAndName.name());

      // https://docs.microsoft.com/en-us/rest/api/network/virtualnetwork/delete-a-network-security-group
      if (uri != null) {
         // 202-Accepted if resource exists and the request is accepted.
         return resourceDeleted.apply(uri);
      } else {
         // 204-No Content if resource does not exist.
         return false;
      }
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
      String ruleName = "ingress-" + protocol.name().toLowerCase() + "-" + portRange;

      logger.debug(">> adding ip permission [%s] to %s...", ruleName, group.getName());

      // TODO: Support Azure network tags somehow?

      final ResourceGroupAndName resourceGroupAndName = ResourceGroupAndName.fromSlashEncoded(group.getId());

      NetworkSecurityGroupApi groupApi = api.getNetworkSecurityGroupApi(resourceGroupAndName.resourceGroup());
      NetworkSecurityGroup networkSecurityGroup = groupApi.get(resourceGroupAndName.name());

      if (networkSecurityGroup == null) {
         throw new IllegalArgumentException("Security group " + group.getName() + " was not found");
      }

      NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourceGroupAndName.resourceGroup(), networkSecurityGroup.name());
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

         checkState(securityGroupRuleAvailable.create(resourceGroupAndName.resourceGroup(), networkSecurityGroup.name()).apply(ruleName),
               "Security group was not updated in the configured timeout");
      }

      return getSecurityGroupById(group.getId());
   }

   @Override
   public SecurityGroup removeIpPermission(final IpProtocol protocol, int startPort, int endPort,
         Multimap<String, String> tenantIdGroupNamePairs, final Iterable<String> ipRanges, Iterable<String> groupIds,
         SecurityGroup group) {
      final String portRange = startPort + "-" + endPort;
      String ruleName = "ingress-" + protocol.name().toLowerCase() + "-" + portRange;

      logger.debug(">> deleting ip permissions matching [%s] from %s...", ruleName, group.getName());

      final ResourceGroupAndName resourceGroupAndName = ResourceGroupAndName.fromSlashEncoded(group.getId());

      NetworkSecurityGroupApi groupApi = api.getNetworkSecurityGroupApi(resourceGroupAndName.resourceGroup());
      NetworkSecurityGroup networkSecurityGroup = groupApi.get(resourceGroupAndName.name());

      if (networkSecurityGroup == null) {
         throw new IllegalArgumentException("Security group " + group.getName() + " was not found");
      }

      NetworkSecurityRuleApi ruleApi = api.getNetworkSecurityRuleApi(resourceGroupAndName.resourceGroup(),
            networkSecurityGroup.name());
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
         URI uri = ruleApi.delete(matchingRule.name());
         if (uri != null) {
            checkState(resourceDeleted.apply(uri), "Rule %s could not be deleted in the configured timeout", matchingRule.id());
         }

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
