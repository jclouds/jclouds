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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.options.NodeAndNovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.rest.InsufficientResourcesException;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A function for adding and allocating an ip to a node
 */
public class AllocateAndAddFloatingIpToNode implements
         Function<AtomicReference<NodeAndNovaTemplateOptions>, AtomicReference<NodeMetadata>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Predicate<AtomicReference<NodeMetadata>> nodeRunning;
   private final NovaApi novaApi;
   private final LoadingCache<RegionAndId, Iterable<? extends FloatingIP>> floatingIpCache;

   @Inject
   public AllocateAndAddFloatingIpToNode(@Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            NovaApi novaApi, @Named("FLOATINGIP") LoadingCache<RegionAndId, Iterable<? extends FloatingIP>> floatingIpCache) {
      this.nodeRunning = checkNotNull(nodeRunning, "nodeRunning");
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.floatingIpCache = checkNotNull(floatingIpCache, "floatingIpCache");
   }

   @Override
   public AtomicReference<NodeMetadata> apply(AtomicReference<NodeAndNovaTemplateOptions> input) {
      checkState(nodeRunning.apply(input.get().getNodeMetadata()), "node never achieved state running %s", input.get().getNodeMetadata());
      NodeMetadata node = input.get().getNodeMetadata().get();
      // node's location is a host
      String regionId = node.getLocation().getParent().getId();
      FloatingIPApi floatingIpApi = novaApi.getFloatingIPApi(regionId).get();
      Optional<Set<String>> poolNames = input.get().getNovaTemplateOptions().get().getFloatingIpPoolNames();

      Optional<FloatingIP> ip = allocateFloatingIPForNode(floatingIpApi, poolNames, node.getId());
      if (!ip.isPresent()) {
         throw new InsufficientResourcesException("Failed to allocate a FloatingIP for node(" + node.getId() + ")");
      }
      logger.debug(">> adding floatingIp(%s) to node(%s)", ip.get().getIp(), node.getId());

      floatingIpApi.addToServer(ip.get().getIp(), node.getProviderId());

      input.get().getNodeMetadata().set(NodeMetadataBuilder.fromNodeMetadata(node).publicAddresses(ImmutableSet.of(ip.get().getIp())).build());
      floatingIpCache.invalidate(RegionAndId.fromSlashEncoded(node.getId()));
      return input.get().getNodeMetadata();
   }

   /**
    * Allocates a FloatingIP for a given Node
    *
    * @param floatingIpApi FloatingIPApi to create or query for a valid FloatingIP
    * @param poolNames optional set of pool names from which we will attempt to allocate an IP from. Most cases this is null
    * @param nodeID optional id of the Node we are trying to allocate a FloatingIP for. Used here only for logging purposes
    * @return Optional<FloatingIP>
    */
   private synchronized Optional<FloatingIP> allocateFloatingIPForNode(FloatingIPApi floatingIpApi, Optional<Set<String>> poolNames, String nodeID) {

      FloatingIP ip = null;

      // 1.) Attempt to allocate from optionally passed poolNames
      if (poolNames.isPresent()) {
         for (String poolName : poolNames.get()) {
            try {
               logger.debug(">> allocating floating IP from pool %s for node(%s)", poolName, nodeID);
               ip = floatingIpApi.allocateFromPool(poolName);
               if (ip != null)
                  return Optional.of(ip);
            } catch (InsufficientResourcesException ire){
               logger.trace("<< [%s] failed to allocate floating IP from pool %s for node(%s)", ire.getMessage(), poolName, nodeID);
            }
         }
      }

      // 2.) Attempt to allocate, if necessary, via 'create()' call
      try {
         logger.debug(">> creating floating IP for node(%s)", nodeID);
         ip = floatingIpApi.create();
         if (ip != null)
            return Optional.of(ip);
      } catch (InsufficientResourcesException ire) {
         logger.trace("<< [%s] failed to create floating IP for node(%s)", ire.getMessage(), nodeID);
      }

      // 3.) If no IP was found make final attempt by searching through list of available IP's
      logger.trace(">> searching for existing, unassigned floating IP for node(%s)", nodeID);
      List<FloatingIP> unassignedIps = Lists.newArrayList(Iterables.filter(floatingIpApi.list(),
            new Predicate<FloatingIP>() {

               @Override
               public boolean apply(FloatingIP arg0) {
                  return arg0.getFixedIp() == null;
               }

      }));
      // try to prevent multiple parallel launches from choosing the same ip.
      Collections.shuffle(unassignedIps);
      ip = Iterables.getLast(unassignedIps);
      return Optional.fromNullable(ip);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("AllocateAndAddFloatingIpToNode").toString();
   }
}
