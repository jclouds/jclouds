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
import static org.jclouds.openstack.nova.v2_0.compute.strategy.ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet.JCLOUDS_KP;
import static org.jclouds.openstack.nova.v2_0.compute.strategy.ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet.JCLOUDS_SG;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;

import com.google.common.base.Function;
import com.google.common.cache.LoadingCache;

@Singleton
public class CleanupServer implements Function<String, Boolean> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final NovaApi novaApi;
   protected final RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate;
   protected final LoadingCache<RegionAndName, SecurityGroupInRegion> securityGroupMap;
   protected final LoadingCache<RegionAndName, KeyPair> keyPairCache;

   @Inject
   public CleanupServer(NovaApi novaApi, RemoveFloatingIpFromNodeAndDeallocate removeFloatingIpFromNodeAndDeallocate,
                        LoadingCache<RegionAndName, SecurityGroupInRegion> securityGroupMap,
                        LoadingCache<RegionAndName, KeyPair> keyPairCache) {
      this.novaApi = novaApi;
      this.removeFloatingIpFromNodeAndDeallocate = removeFloatingIpFromNodeAndDeallocate;
      this.securityGroupMap = checkNotNull(securityGroupMap, "securityGroupMap");
      this.keyPairCache = checkNotNull(keyPairCache, "keyPairCache");

   }

   @Override
   public Boolean apply(String id) {
      final RegionAndId regionAndId = RegionAndId.fromSlashEncoded(id);
      ServerWithSecurityGroups server = novaApi.getServerWithSecurityGroupsApi(regionAndId.getRegion()).get().get(regionAndId.getId());

      if (novaApi.getFloatingIPApi(regionAndId.getRegion()).isPresent()) {
         try {
            removeFloatingIpFromNodeAndDeallocate.apply(regionAndId);
         } catch (RuntimeException e) {
            logger.warn(e, "<< error removing and deallocating ip from node(%s): %s", id, e.getMessage());
         }
      }
      if (containsMetadata(server, JCLOUDS_KP)) {
        if (novaApi.getKeyPairApi(regionAndId.getRegion()).isPresent()) {
           RegionAndName regionAndName = RegionAndName.fromRegionAndName(regionAndId.getRegion(), server.getKeyName());
           logger.debug(">> deleting keypair(%s)", regionAndName);
           novaApi.getKeyPairApi(regionAndId.getRegion()).get().delete(server.getKeyName());
           // TODO: test this clear happens
           keyPairCache.invalidate(regionAndName);
           logger.debug("<< deleted keypair(%s)", regionAndName);
        }
      }

      boolean serverDeleted = novaApi.getServerApi(regionAndId.getRegion()).delete(regionAndId.getId());

      if (containsMetadata(server, JCLOUDS_SG)) {
         for (final String securityGroupName : server.getSecurityGroupNames()) {
            for (SecurityGroup securityGroup : novaApi.getSecurityGroupApi(regionAndId.getRegion()).get().list().toList()) {
               if (securityGroup.getName().equalsIgnoreCase(securityGroupName)) {
                  if (novaApi.getSecurityGroupApi(regionAndId.getRegion()).isPresent()) {
                     RegionAndName regionAndName = RegionAndName.fromRegionAndName(regionAndId.getRegion(), securityGroup.getName());
                     logger.debug(">> deleting securityGroup(%s)", regionAndName);
                     novaApi.getSecurityGroupApi(regionAndId.getRegion()).get().delete(securityGroup.getId());
                     // TODO: test this clear happens
                     securityGroupMap.invalidate(regionAndName);
                     logger.debug("<< deleted securityGroup(%s)", regionAndName);
                  }
               }
            }
         }
      }
      return serverDeleted;
   }

   private boolean containsMetadata(ServerWithSecurityGroups server, String key) {
      if (server == null || server.getMetadata() == null || server.getMetadata().get("jclouds_tags") == null)
         return false;
      return server.getMetadata().get("jclouds_tags").contains(key);
   }
}
