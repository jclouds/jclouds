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
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.cache.LoadingCache;

/**
 * A function for removing and deallocating an ip address from a node
 */
public class RemoveFloatingIpFromNodeAndDeallocate implements Function<RegionAndId, RegionAndId> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final NovaApi novaApi;
   private final LoadingCache<RegionAndId, Iterable<? extends FloatingIP>> floatingIpCache;

   @Inject
   public RemoveFloatingIpFromNodeAndDeallocate(NovaApi novaApi,
            @Named("FLOATINGIP") LoadingCache<RegionAndId, Iterable<? extends FloatingIP>> floatingIpCache) {
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.floatingIpCache = checkNotNull(floatingIpCache, "floatingIpCache");
   }

   @Override
   public RegionAndId apply(RegionAndId id) {
      FloatingIPApi floatingIpApi = novaApi.getFloatingIPApi(id.getRegion()).get();
      for (FloatingIP ip : floatingIpCache.getUnchecked(id)) {
         logger.debug(">> removing floatingIp(%s) from node(%s)", ip, id);
         floatingIpApi.removeFromServer(ip.getIp(), id.getId());
         logger.debug(">> deallocating floatingIp(%s)", ip);
         floatingIpApi.delete(ip.getId());
      }
      floatingIpCache.invalidate(id);
      return id;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("RemoveFloatingIpFromNodeAndDecreate").toString();
   }
}
