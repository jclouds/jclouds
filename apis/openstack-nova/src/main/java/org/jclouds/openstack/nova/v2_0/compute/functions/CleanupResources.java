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
import static org.jclouds.openstack.nova.v2_0.compute.strategy.ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet.JCLOUDS_SG_PREFIX;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;

@Singleton
public class CleanupResources implements Function<NodeMetadata, Boolean> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final NovaApi novaApi;
   protected final RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate;
   protected final LoadingCache<RegionAndName, SecurityGroupInRegion> securityGroupMap;

   @Inject
   public CleanupResources(NovaApi novaApi, RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate,
                           LoadingCache<RegionAndName, SecurityGroupInRegion> securityGroupMap) {

      this.novaApi = novaApi;
      this.removeFloatingIpFromNodeAndDeallocate = removeFloatingIpFromNodeAndDeallocate;
      this.securityGroupMap = checkNotNull(securityGroupMap, "securityGroupMap");
   }

   @Override
   public Boolean apply(NodeMetadata node) {
      final RegionAndId regionAndId = RegionAndId.fromSlashEncoded(node.getId());
      removeFloatingIpFromNodeifAny(regionAndId);
      return removeSecurityGroupCreatedByJcloudsAndInvalidateCache(regionAndId.getRegion(), node.getTags());
   }

   public boolean removeSecurityGroupCreatedByJcloudsAndInvalidateCache(String regionId, Set<String> tags) {
      String securityGroupIdCreatedByJclouds = getSecurityGroupIdCreatedByJclouds(tags);
      if (securityGroupIdCreatedByJclouds != null) {
         SecurityGroup securityGroup = novaApi.getSecurityGroupApi(regionId).get().get(securityGroupIdCreatedByJclouds);
         RegionAndName regionAndName = RegionAndName.fromRegionAndName(regionId, securityGroup.getName());
         logger.debug(">> deleting securityGroup(%s)", regionAndName);
         novaApi.getSecurityGroupApi(regionId).get().delete(securityGroupIdCreatedByJclouds);
         securityGroupMap.invalidate(regionAndName);
         logger.debug("<< deleted securityGroup(%s)", regionAndName);
         return true;
      }
      return false;
   }

   private void removeFloatingIpFromNodeifAny(RegionAndId regionAndId) {
      try {
         removeFloatingIpFromNodeAndDeallocate.apply(regionAndId);
      } catch (RuntimeException e) {
         logger.warn(e, "<< error removing and deallocating ip from node(%s): %s", regionAndId, e.getMessage());
      }
   }
   
   private String getSecurityGroupIdCreatedByJclouds(Set<String> tags) {
      return FluentIterable.from(tags).filter(new Predicate<String>() {
         @Override
         public boolean apply(String input) {
            return input.startsWith(JCLOUDS_SG_PREFIX);
         }
      }).transform(new Function<String, String>() {
         @Override
         public String apply(String input) {
            return input.substring(JCLOUDS_SG_PREFIX.length() + 1);
         }
      }).first().orNull();
   }

}
