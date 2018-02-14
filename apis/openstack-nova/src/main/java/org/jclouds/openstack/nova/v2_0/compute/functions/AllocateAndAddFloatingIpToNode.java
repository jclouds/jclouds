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
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import com.google.common.collect.Sets;
import org.jclouds.Context;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.Networks;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.PortApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.options.NodeAndNovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIpForServer;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.rest.ApiContext;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * A function for adding and allocating an ip to a node
 */
public class AllocateAndAddFloatingIpToNode
      implements Function<AtomicReference<NodeAndNovaTemplateOptions>, AtomicReference<NodeMetadata>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named("openstack-neutron")
   private Supplier<Context> neutronContextSupplier;

   private final Predicate<AtomicReference<NodeMetadata>> nodeRunning;
   private final NovaApi novaApi;
   private final LoadingCache<RegionAndId, Iterable<? extends FloatingIpForServer>> floatingIpCache;
   private final CleanupResources cleanupResources;

   @Inject
   public AllocateAndAddFloatingIpToNode(
         @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning, NovaApi novaApi,
         @Named("FLOATINGIP") LoadingCache<RegionAndId, Iterable<? extends FloatingIpForServer>> floatingIpCache,
         CleanupResources cleanupResources) {
      this.nodeRunning = checkNotNull(nodeRunning, "nodeRunning");
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.floatingIpCache = checkNotNull(floatingIpCache, "floatingIpCache");
      this.cleanupResources = checkNotNull(cleanupResources, "cleanupResources");
   }

   @Override
   public AtomicReference<NodeMetadata> apply(AtomicReference<NodeAndNovaTemplateOptions> input) {
      checkState(nodeRunning.apply(input.get().getNodeMetadata()), "node never achieved state running %s", input.get().getNodeMetadata());
      final NodeMetadata node = input.get().getNodeMetadata().get();
      // node's location is a host
      String regionId = node.getLocation().getParent().getId();
      Optional<Set<String>> poolNames = input.get().getNovaTemplateOptions().get().getFloatingIpPoolNames();

      String availabilityZone = getAvailabilityZoneFromTemplateOptionsOrDefault(input, regionId);

      if (isNeutronLinked()) {
         org.jclouds.openstack.neutron.v2.features.FloatingIPApi neutronFloatingApi = getFloatingIPApi(regionId);
            final Optional<Port> optionalPort = getPortApi(regionId).list().concat().firstMatch(new Predicate<Port>() {
               @Override
               public boolean apply(@Nullable Port input) {
                  return input.getDeviceId().equals(node.getProviderId());
               }
            });
            if (optionalPort.isPresent()) {
               Optional<org.jclouds.openstack.neutron.v2.domain.FloatingIP> floatingIPOptional = tryFindExistingFloatingIp(neutronFloatingApi, availabilityZone);
               org.jclouds.openstack.neutron.v2.domain.FloatingIP floatingIP;
               if (floatingIPOptional.isPresent()) {
                  floatingIP = floatingIPOptional.get();
               } else {
                  floatingIP = createFloatingIpUsingNeutron(neutronFloatingApi, node, poolNames, availabilityZone);
               }

               org.jclouds.openstack.neutron.v2.domain.FloatingIP ip = neutronFloatingApi.update(floatingIP.getId(),
                       org.jclouds.openstack.neutron.v2.domain.FloatingIP.UpdateFloatingIP
                               .updateBuilder()
                               .portId(optionalPort.get().getId())
                               .build());

               input.get().getNodeMetadata().set(NodeMetadataBuilder.fromNodeMetadata(node).publicAddresses(ImmutableSet.of(ip.getFloatingIpAddress())).build());
            } else {
               logger.error("Node %s doesn't have a port to attach a floating IP", node);
               throw new IllegalStateException("Missing required port in node: " + node);
            }
      } else { // try nova
         FloatingIPApi floatingIpApi = novaApi.getFloatingIPApi(regionId).get();

         Optional<FloatingIP> ip = allocateFloatingIPForNodeOnNova(floatingIpApi, poolNames, node.getId());
         if (!ip.isPresent()) {
            cleanupResources.apply(node);
            throw new InsufficientResourcesException("Failed to allocate a FloatingIP for node(" + node.getId() + ")");
         }
         logger.debug(">> adding floatingIp(%s) to node(%s)", ip.get().getIp(), node.getId());

         floatingIpApi.addToServer(ip.get().getIp(), node.getProviderId());
         input.get().getNodeMetadata().set(NodeMetadataBuilder.fromNodeMetadata(node).publicAddresses(ImmutableSet.of(ip.get().getIp())).build());
         floatingIpCache.asMap().put(RegionAndId.fromSlashEncoded(node.getId()), ImmutableList.of(FloatingIpForServer.create(RegionAndId.fromSlashEncoded(node.getId()), ip.get().getId(), ip.get().getIp())));
      }
      return input.get().getNodeMetadata();
   }

   private String getAvailabilityZoneFromTemplateOptionsOrDefault(AtomicReference<NodeAndNovaTemplateOptions> input, String regionId) {
      return MoreObjects.firstNonNull(input.get().getNovaTemplateOptions().get().getAvailabilityZone(),
              Iterables.get(novaApi.getAvailabilityZoneApi(regionId).get().listAvailabilityZones(), 0).getName());
   }

   /**
    * Allocates a FloatingIP for a given Node
    *
    * @param floatingIpApi
    *           FloatingIPApi to create or query for a valid FloatingIP
    * @param poolNames
    *           optional set of pool names from which we will attempt to allocate
    *           an IP from. Most cases this is null
    * @param nodeID
    *           optional id of the Node we are trying to allocate a FloatingIP for.
    *           Used here only for logging purposes
    * @return Optional<FloatingIP>
    */
   private synchronized Optional<FloatingIP> allocateFloatingIPForNodeOnNova(FloatingIPApi floatingIpApi,
         Optional<Set<String>> poolNames, String nodeID) {

      FloatingIP ip;

      // 1.) Attempt to allocate from optionally passed poolNames
      if (poolNames.isPresent()) {
         for (String poolName : poolNames.get()) {
            try {
               logger.debug(">> allocating floating IP from pool %s for node(%s)", poolName, nodeID);
               ip = floatingIpApi.allocateFromPool(poolName);
               return Optional.of(ip);
            } catch (ResourceNotFoundException ex) {
               logger.trace("<< [%s] failed to allocate floating IP from pool %s for node(%s)", ex.getMessage(),
                     poolName, nodeID);
            } catch (InsufficientResourcesException ire) {
               logger.trace("<< [%s] failed to allocate floating IP from pool %s for node(%s)", ire.getMessage(),
                     poolName, nodeID);
            }
         }
      }

      // 2.) Attempt to allocate, if necessary, via 'create()' call
      try {
         logger.debug(">> creating floating IP for node(%s)", nodeID);
         ip = floatingIpApi.create();
         return Optional.of(ip);
      } catch (ResourceNotFoundException ex) {
         logger.trace("<< [%s] failed to create floating IP for node(%s)", ex.getMessage(), nodeID);
      } catch (InsufficientResourcesException ire) {
         logger.trace("<< [%s] failed to create floating IP for node(%s)", ire.getMessage(), nodeID);
      }

      // 3.) If no IP was found make final attempt by searching through list of
      // available IP's
      logger.trace(">> searching for existing, unassigned floating IP for node(%s)", nodeID);
      List<FloatingIP> unassignedIps = Lists
            .newArrayList(Iterables.filter(floatingIpApi.list(), new Predicate<FloatingIP>() {

               @Override
               public boolean apply(FloatingIP arg0) {
                  return arg0.getFixedIp() == null;
               }

            }));
      // try to prevent multiple parallel launches from choosing the same ip.
      if (unassignedIps.isEmpty()) {
         return Optional.absent();
      }
      Collections.shuffle(unassignedIps);
      ip = Iterables.getLast(unassignedIps);
      return Optional.fromNullable(ip);
   }

   private Optional<org.jclouds.openstack.neutron.v2.domain.FloatingIP> tryFindExistingFloatingIp(org.jclouds.openstack.neutron.v2.features.FloatingIPApi neutronFloatingApi, final String availabilityZone) {
      Optional<org.jclouds.openstack.neutron.v2.domain.FloatingIP> floatingIPOptional = neutronFloatingApi.list().concat().firstMatch(new Predicate<org.jclouds.openstack.neutron.v2.domain.FloatingIP>() {
         @Override
         public boolean apply(@Nullable org.jclouds.openstack.neutron.v2.domain.FloatingIP input) {
            return input.getPortId() == null && input.getAvailabilityZone().equals(availabilityZone);
         }
      });
      return floatingIPOptional;
   }

   private org.jclouds.openstack.neutron.v2.domain.FloatingIP createFloatingIpUsingNeutron(org.jclouds.openstack.neutron.v2.features.FloatingIPApi neutronFloatingApi,
                                                                                           NodeMetadata node, Optional<Set<String>> poolNames, final String availabilityZone) {
      String regionId = node.getLocation().getParent().getId();
      List<Network> networks = getSuitableNetworks(regionId, availabilityZone, poolNames.or(Sets.<String>newHashSet()));
      org.jclouds.openstack.neutron.v2.domain.FloatingIP floatingIP = null;
      for (Network network : networks) {
         try {
            logger.debug(">> allocating floating IP from network %s for node(%s)", network, node);
            org.jclouds.openstack.neutron.v2.domain.FloatingIP createFloatingIP = org.jclouds.openstack.neutron.v2.domain.FloatingIP.CreateFloatingIP.createBuilder(network.getId()).availabilityZone(network.getAvailabilityZone()).build();
            floatingIP = neutronFloatingApi.create((org.jclouds.openstack.neutron.v2.domain.FloatingIP.CreateFloatingIP) createFloatingIP);
            logger.debug(">> allocated floating IP(%s) from network(%s) for node(%s)", floatingIP, network, node);
            floatingIpCache.asMap().put(RegionAndId.fromSlashEncoded(node.getId()), ImmutableList.of(FloatingIpForServer.create(RegionAndId.fromSlashEncoded(node.getId()), floatingIP.getId(), floatingIP.getFloatingIpAddress())));
            return floatingIP;
         } catch (Exception ex) {
            logger.trace("<< [%s] failed to allocate a floating IP from network %s for node(%s)", ex.getMessage(), network, node);
         }
      }

      throw new IllegalStateException("Failed to allocate a floating IP for node " + node + ".\n" +
            "Failed to find suitable external networks or to allocate from poolNames specified: "
            + Iterables.toString(poolNames.get()));

   }


   /**
    * Get all suitable networks to allocate a floating ip
    *
    * It will prefer networks specified using the poolNames first and then the external networks in the given availability zone
    */
   private List<Network> getSuitableNetworks(String regionId, final String availabilityZone, final Set<String> poolNames) {
      List<Network> allNetworks = getNetworkApi(regionId).list().concat().toList();
      Iterable<Network> externalNetworks = Iterables.filter(allNetworks, Networks.Predicates.externalNetworks(availabilityZone));
      Iterable<Network> networksFromPoolName = Iterables.filter(allNetworks, Networks.Predicates.namedNetworks(poolNames));
      return Lists.newArrayList(Iterables.concat(networksFromPoolName, externalNetworks));
   }

   private boolean isNeutronLinked() {
      return neutronContextSupplier != null && neutronContextSupplier.get() != null;
   }

   private org.jclouds.openstack.neutron.v2.features.FloatingIPApi getFloatingIPApi(String region) {
      return ((ApiContext<NeutronApi>) neutronContextSupplier.get()).getApi().getFloatingIPApi(region);
   }

   private PortApi getPortApi(String regionId) {
      return ((ApiContext<NeutronApi>) neutronContextSupplier.get()).getApi().getPortApi(regionId);
   }

   private NetworkApi getNetworkApi(String regionId) {
      return ((ApiContext<NeutronApi>) neutronContextSupplier.get()).getApi().getNetworkApi(regionId);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper("AllocateAndAddFloatingIpToNode").toString();
   }

}
