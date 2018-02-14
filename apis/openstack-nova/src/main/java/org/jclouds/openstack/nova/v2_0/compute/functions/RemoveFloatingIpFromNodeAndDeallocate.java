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

import javax.annotation.Resource;
import javax.inject.Named;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import org.jclouds.Context;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIpForServer;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.cache.LoadingCache;
import org.jclouds.rest.ApiContext;

/**
 * A function for removing and deallocating an ip address from a node
 */
public class RemoveFloatingIpFromNodeAndDeallocate implements Function<RegionAndId, RegionAndId> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named("openstack-neutron")
   private Supplier<Context> neutronContextSupplier;

   private final NovaApi novaApi;
   private final LoadingCache<RegionAndId, Iterable<? extends FloatingIpForServer>> floatingIpCache;

   @Inject
   public RemoveFloatingIpFromNodeAndDeallocate(NovaApi novaApi,
            @Named("FLOATINGIP") LoadingCache<RegionAndId, Iterable<? extends FloatingIpForServer>> floatingIpCache) {
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.floatingIpCache = checkNotNull(floatingIpCache, "floatingIpCache");
   }

   @Override
   public RegionAndId apply(RegionAndId id) {
      if (isNeutronLinked()) {
         for (FloatingIpForServer floatingIpForServer : floatingIpCache.getUnchecked(id)) {
            logger.debug(">> deallocating floatingIp(%s)", floatingIpForServer);
            getFloatingIPApi(id.getRegion()).delete(floatingIpForServer.floatingIpId());
         }
      } else { // try nova
         FloatingIPApi floatingIpApi = novaApi.getFloatingIPApi(id.getRegion()).get();
         for (FloatingIpForServer floatingIpForServer : floatingIpCache.getUnchecked(id)) {
            logger.debug(">> removing floatingIp(%s) from node(%s)", floatingIpForServer, id);
            floatingIpApi.removeFromServer(floatingIpForServer.ip(), id.getId());
            logger.debug(">> deallocating floatingIp(%s)", floatingIpForServer);
            floatingIpApi.delete(floatingIpForServer.floatingIpId());
         }
      }
      floatingIpCache.invalidate(id);
      return id;
   }

   // FIXME remove duplications from AllocateAndAddFloatingIpToNode
   private boolean isNeutronLinked() {
      return neutronContextSupplier != null && neutronContextSupplier.get() != null;
   }

   private org.jclouds.openstack.neutron.v2.features.FloatingIPApi getFloatingIPApi(String region) {
      return ((ApiContext<NeutronApi>) neutronContextSupplier.get()).getApi().getFloatingIPApi(region);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper("RemoveFloatingIpFromNodeAndDeallocate").toString();
   }
}
