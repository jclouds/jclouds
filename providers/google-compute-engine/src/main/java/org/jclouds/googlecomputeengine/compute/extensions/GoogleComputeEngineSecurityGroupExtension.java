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
package org.jclouds.googlecomputeengine.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.googlecomputeengine.compute.strategy.CreateNodesWithGroupEncodedIntoNameThenAddToSet.DEFAULT_INTERNAL_NETWORK_RANGE;
import static org.jclouds.googlecomputeengine.internal.ListPages.concat;
import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.jclouds.googlecomputeengine.predicates.NetworkFirewallPredicates.equalsIpPermission;
import static org.jclouds.googlecomputeengine.predicates.NetworkFirewallPredicates.providesIpPermission;
import static org.jclouds.util.Predicates2.retry;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.domain.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.compute.domain.SlashEncodedIds;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Atomics;

/**
 * An extension to compute service to allow for the manipulation of {@link org.jclouds.compute.domain.SecurityGroup}s.
 * Implementation
 * is optional by providers.
 */
public class GoogleComputeEngineSecurityGroupExtension implements SecurityGroupExtension {

   private final Supplier<String> userProject;
   private final GroupNamingConvention.Factory namingConvention;
   private final LoadingCache<NetworkAndAddressRange, Network> networkCreator;
   private final Function<Network, SecurityGroup> groupConverter;
   private final GoogleComputeEngineApi api;
   private final Predicate<AtomicReference<Operation>> operationDonePredicate;
   private final long operationCompleteCheckInterval;
   private final long operationCompleteCheckTimeout;

   @Inject GoogleComputeEngineSecurityGroupExtension(GoogleComputeEngineApi api,
         @UserProject Supplier<String> userProject, GroupNamingConvention.Factory namingConvention,
         LoadingCache<NetworkAndAddressRange, Network> networkCreator, Function<Network, SecurityGroup> groupConverter,
         @Named("global") Predicate<AtomicReference<Operation>> operationDonePredicate,
         @Named(OPERATION_COMPLETE_INTERVAL) Long operationCompleteCheckInterval,
         @Named(OPERATION_COMPLETE_TIMEOUT) Long operationCompleteCheckTimeout) {
      this.api = checkNotNull(api, "api");
      this.userProject = checkNotNull(userProject, "userProject");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
      this.networkCreator = checkNotNull(networkCreator, "networkCreator");
      this.groupConverter = checkNotNull(groupConverter, "groupConverter");
      this.operationCompleteCheckInterval = checkNotNull(operationCompleteCheckInterval,
            "operation completed check interval");
      this.operationCompleteCheckTimeout = checkNotNull(operationCompleteCheckTimeout,
            "operation completed check timeout");
      this.operationDonePredicate = checkNotNull(operationDonePredicate, "operationDonePredicate");
   }

   @Override
   public Set<SecurityGroup> listSecurityGroups() {
      return FluentIterable.from(concat(api.getNetworkApi(userProject.get()).list())).transform(groupConverter).toSet();
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsInLocation(final Location location) {
      return listSecurityGroups();
   }

   @Override
   public Set<SecurityGroup> listSecurityGroupsForNode(String id) {
      SlashEncodedIds zoneAndId = SlashEncodedIds.fromSlashEncoded(id);

      Instance instance = api.getInstanceApi(userProject.get(), zoneAndId.left()).get(zoneAndId.right());

      if (instance == null) {
         return ImmutableSet.of();
      }

      ImmutableSet.Builder builder = ImmutableSet.builder();

      for (NetworkInterface nwInterface : instance.networkInterfaces()) {
         String networkUrl = nwInterface.network().getPath();
         Network nw = api.getNetworkApi(userProject.get()).get(networkUrl.substring(networkUrl.lastIndexOf('/') + 1));

         SecurityGroup grp = groupForTagsInNetwork(nw, instance.tags().items());
         if (grp != null) {
            builder.add(grp);
         }
      }

      return builder.build();
   }

   @Override
   public SecurityGroup getSecurityGroupById(String id) {
      checkNotNull(id, "id");
      Network network = api.getNetworkApi(userProject.get()).get(id);

      if (network == null) {
         return null;
      }

      return groupConverter.apply(network);
   }

   @Override
   public SecurityGroup createSecurityGroup(String name, Location location) {
      return createSecurityGroup(name);
   }

   public SecurityGroup createSecurityGroup(String name) {
      checkNotNull(name, "name");

      NetworkAndAddressRange nAr = NetworkAndAddressRange.create(name, DEFAULT_INTERNAL_NETWORK_RANGE, null);

      Network nw = networkCreator.apply(nAr);

      return groupConverter.apply(nw);
   }

   @Override
   public boolean removeSecurityGroup(String id) {
      checkNotNull(id, "id");
      if (api.getNetworkApi(userProject.get()).get(id) == null) {
         return false;
      }

      ListOptions options = filter("network eq .*/" + id);

      FluentIterable<Firewall> fws = FluentIterable.from(concat(api.getFirewallApi(userProject.get()).list(options)));

      for (Firewall fw : fws) {
         AtomicReference<Operation> operation = Atomics
               .newReference(api.getFirewallApi(userProject.get()).delete(fw.name()));

         retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval, MILLISECONDS)
               .apply(operation);

         checkState(operation.get().httpErrorStatusCode() == null,
               "Could not delete firewall, operation failed" + operation);
      }

      AtomicReference<Operation> operation = Atomics.newReference(api.getNetworkApi(userProject.get()).delete(id));

      retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval, MILLISECONDS)
            .apply(operation);

      checkState(operation.get().httpErrorStatusCode() == null,
            "Could not insert network, operation failed" + operation);

      return true;
   }

   @Override
   public SecurityGroup addIpPermission(IpPermission ipPermission, SecurityGroup group) {
      checkNotNull(group, "group");
      checkNotNull(ipPermission, "ipPermission");

      checkNotNull(api.getNetworkApi(userProject.get()).get(group.getId()) == null, "network for group is null");

      ListOptions options = filter("network eq .*/" + group.getName());

      if (Iterables
            .any(concat(api.getFirewallApi(userProject.get()).list(options)), providesIpPermission(ipPermission))) {
         // Permission already exists.
         return group;
      }

      FirewallOptions fwOptions = new FirewallOptions();
      String uniqueFwName = namingConvention.createWithoutPrefix().uniqueNameForGroup(group.getName());
      fwOptions.name(uniqueFwName);
      fwOptions.network(group.getUri());
      if (!ipPermission.getGroupIds().isEmpty()) {
         fwOptions.sourceTags(ipPermission.getGroupIds());
      }
      if (!ipPermission.getCidrBlocks().isEmpty()) {
         fwOptions.sourceRanges(ipPermission.getCidrBlocks());
      }
      List<String> ports = Lists.newArrayList();
      if (ipPermission.getFromPort() > 0) {
         if (ipPermission.getFromPort() == ipPermission.getToPort()) {
            ports.add(String.valueOf(ipPermission.getToPort()));
         } else {
            ports.add(ipPermission.getFromPort() + "-" + ipPermission.getToPort());
         }
      }
      fwOptions.addAllowedRule(Firewall.Rule.create(ipPermission.getIpProtocol().value().toLowerCase(), ports));

      AtomicReference<Operation> operation = Atomics.newReference(
            api.getFirewallApi(userProject.get()).createInNetwork(uniqueFwName, group.getUri(), fwOptions));

      retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval, MILLISECONDS)
            .apply(operation);

      checkState(operation.get().httpErrorStatusCode() == null,
            "Could not insert firewall, operation failed" + operation);

      return getSecurityGroupById(group.getId());
   }

   @Override
   public SecurityGroup addIpPermission(IpProtocol protocol, int fromPort, int toPort,
         Multimap<String, String> tenantIdGroupNamePairs, Iterable<String> cidrBlocks, Iterable<String> groupIds,
         SecurityGroup group) {

      IpPermission.Builder permBuilder = IpPermission.builder();
      permBuilder.ipProtocol(protocol);
      permBuilder.fromPort(fromPort);
      permBuilder.toPort(toPort);
      permBuilder.groupIds(groupIds);
      permBuilder.cidrBlocks(cidrBlocks);

      return addIpPermission(permBuilder.build(), group);

   }

   @Override
   public SecurityGroup removeIpPermission(IpPermission ipPermission, SecurityGroup group) {
      checkNotNull(group, "group");
      checkNotNull(ipPermission, "ipPermission");

      checkNotNull(api.getNetworkApi(userProject.get()).get(group.getId()) == null, "network for group is null");

      ListOptions options = filter("network eq .*/" + group.getName());

      FluentIterable<Firewall> fws = FluentIterable.from(concat(api.getFirewallApi(userProject.get()).list(options)));

      for (Firewall fw : fws) {
         if (equalsIpPermission(ipPermission).apply(fw)) {
            AtomicReference<Operation> operation = Atomics
                  .newReference(api.getFirewallApi(userProject.get()).delete(fw.name()));

            retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval, MILLISECONDS)
                  .apply(operation);

            checkState(operation.get().httpErrorStatusCode() == null,
                  "Could not delete firewall, operation failed" + operation);
         }
      }

      return getSecurityGroupById(group.getId());
   }

   @Override
   public SecurityGroup removeIpPermission(IpProtocol protocol, int fromPort, int toPort,
         Multimap<String, String> tenantIdGroupNamePairs, Iterable<String> cidrBlocks, Iterable<String> groupIds,
         SecurityGroup group) {

      IpPermission.Builder permBuilder = IpPermission.builder();
      permBuilder.ipProtocol(protocol);
      permBuilder.fromPort(fromPort);
      permBuilder.toPort(toPort);
      permBuilder.groupIds(groupIds);
      permBuilder.cidrBlocks(cidrBlocks);

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
      return true;
   }

   @Override
   public boolean supportsExclusionCidrBlocks() {
      return false;
   }

   private SecurityGroup groupForTagsInNetwork(Network nw, final Collection<String> tags) {
      ListOptions opts = filter("network eq .*/" + nw.name());
      List<Firewall> fws = FluentIterable.from(concat(api.getFirewallApi(userProject.get()).list(opts)))
            .filter(new Predicate<Firewall>() {
               @Override public boolean apply(final Firewall input) {
                  // If any of the targetTags on the firewall apply or the firewall has no target tags...
                  return Iterables.any(input.targetTags(), Predicates.in(tags)) || Predicates.equalTo(0)
                        .apply(input.targetTags().size());
               }
            }).toList();

      if (fws.isEmpty()) {
         return null;
      }

      return groupConverter.apply(nw);
   }
}
