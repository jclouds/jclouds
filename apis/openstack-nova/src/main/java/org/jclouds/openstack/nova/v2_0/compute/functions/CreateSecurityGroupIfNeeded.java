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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.nameEquals;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;

import com.google.common.base.Function;
import com.google.common.base.Optional;

@Singleton
public class CreateSecurityGroupIfNeeded implements Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final NovaApi novaApi;

   @Inject
   public CreateSecurityGroupIfNeeded(NovaApi novaApi) {
      this.novaApi = checkNotNull(novaApi, "novaApi");
   }

   @Override
   public SecurityGroupInRegion apply(RegionSecurityGroupNameAndPorts regionSecurityGroupNameAndPorts) {
      checkNotNull(regionSecurityGroupNameAndPorts, "regionSecurityGroupNameAndPorts");

      String regionId = regionSecurityGroupNameAndPorts.getRegion();
      Optional<? extends SecurityGroupApi> api = novaApi.getSecurityGroupApi(regionId);
      checkArgument(api.isPresent(), "Security groups are required, but the extension is not available in region %s!", regionId);
      logger.debug(">> creating securityGroup %s", regionSecurityGroupNameAndPorts);
      try {

         SecurityGroup securityGroup = api.get().createWithDescription(
                  regionSecurityGroupNameAndPorts.getName(), regionSecurityGroupNameAndPorts.getName());

         logger.debug("<< created securityGroup(%s)", securityGroup);
         for (int port : regionSecurityGroupNameAndPorts.getPorts()) {
            authorizeGroupToItselfAndAllIPsToTCPPort(api.get(), securityGroup, port);
         }
         return new SecurityGroupInRegion(api.get().get(securityGroup.getId()), regionId);
      } catch (IllegalStateException e) {
         logger.trace("<< trying to find securityGroup(%s): %s", regionSecurityGroupNameAndPorts, e.getMessage());
         SecurityGroup group = find(api.get().list(), nameEquals(regionSecurityGroupNameAndPorts
                  .getName()));
         logger.debug("<< reused securityGroup(%s)", group.getId());
         return new SecurityGroupInRegion(group, regionId);
      }
   }

   private void authorizeGroupToItselfAndAllIPsToTCPPort(SecurityGroupApi securityGroupApi,
            SecurityGroup securityGroup, int port) {
      logger.debug(">> authorizing securityGroup(%s) permission to 0.0.0.0/0 on port %d", securityGroup, port);
      securityGroupApi.createRuleAllowingCidrBlock(securityGroup.getId(), Ingress.builder().ipProtocol(
               IpProtocol.TCP).fromPort(port).toPort(port).build(), "0.0.0.0/0");
      logger.debug("<< authorized securityGroup(%s) permission to 0.0.0.0/0 on port %d", securityGroup, port);

   }
}
