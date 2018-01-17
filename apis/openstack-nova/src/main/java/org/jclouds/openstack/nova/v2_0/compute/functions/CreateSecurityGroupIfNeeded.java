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
import static com.google.common.collect.Iterables.find;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.nameEquals;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import org.jclouds.Context;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Rule;
import org.jclouds.openstack.neutron.v2.domain.RuleDirection;
import org.jclouds.openstack.neutron.v2.domain.RuleProtocol;
import org.jclouds.openstack.neutron.v2.features.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.jclouds.rest.ApiContext;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;

@Singleton
public class CreateSecurityGroupIfNeeded implements Function<RegionSecurityGroupNameAndPorts, SecurityGroup> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final NovaApi novaApi;
   private final Supplier<Map<String, Location>> locationIndex;
   private final Function<SecurityGroupInRegion, SecurityGroup> securityGroupInRegionSecurityGroupFunction;
   private final NeutronSecurityGroupToSecurityGroup.Factory neutronSecurityGroupToSecurityGroup;

   @Inject(optional = true)
   @Named("openstack-neutron")
   Supplier<Context> neutronContextSupplier;

   @Inject
   @VisibleForTesting
   public CreateSecurityGroupIfNeeded(NovaApi novaApi, Supplier<Map<String, Location>> locationIndex,
                               Function<SecurityGroupInRegion, SecurityGroup> securityGroupInRegionSecurityGroupFunction,
                               NeutronSecurityGroupToSecurityGroup.Factory neutronSecurityGroupToSecurityGroup) {
      this.novaApi = novaApi;
      this.locationIndex = locationIndex;
      this.securityGroupInRegionSecurityGroupFunction = securityGroupInRegionSecurityGroupFunction;
      this.neutronSecurityGroupToSecurityGroup = neutronSecurityGroupToSecurityGroup;
   }

   @Override
   public SecurityGroup apply(final RegionSecurityGroupNameAndPorts regionSecurityGroupNameAndPorts) {
      String regionId = regionSecurityGroupNameAndPorts.getRegion();
      Location location = locationIndex.get().get(regionId);

      logger.debug(">> creating securityGroup %s", regionSecurityGroupNameAndPorts);

      SecurityGroupApi securityGroupApi = getNeutronSecurityGroupApi(regionId);
      if (securityGroupApi != null) {
         org.jclouds.openstack.neutron.v2.domain.SecurityGroup group = securityGroupApi
               .create(org.jclouds.openstack.neutron.v2.domain.SecurityGroup.CreateSecurityGroup.createBuilder()
                     .name(regionSecurityGroupNameAndPorts.getName()).description("security group created by jclouds")
                     .build());
         return createSecurityGroupFrom(group, location, regionSecurityGroupNameAndPorts.getPorts());
      } else {
         // try to use Nova
         Optional<? extends org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi> api = novaApi
               .getSecurityGroupApi(regionId);
         checkArgument(api.isPresent(),
               "Security groups are required, but the extension is not available in region %s!", regionId);
         final FluentIterable<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup> allGroups = api.get().list();
         logger.debug(">> creating securityGroup %s", regionSecurityGroupNameAndPorts);
         try {
            org.jclouds.openstack.nova.v2_0.domain.SecurityGroup novaSecurityGroup = api.get().createWithDescription(
                  regionSecurityGroupNameAndPorts.getName(), regionSecurityGroupNameAndPorts.getName());

            logger.debug("<< created securityGroup(%s)", novaSecurityGroup);
            for (int port : regionSecurityGroupNameAndPorts.getPorts()) {
               authorizeGroupToItselfAndAllIPsToTCPPort(api.get(), novaSecurityGroup, port);
            }
            return securityGroupInRegionSecurityGroupFunction
                  .apply(new SecurityGroupInRegion(api.get().get(novaSecurityGroup.getId()), regionId, allGroups));
         } catch (IllegalStateException e) {
            logger.trace("<< trying to find securityGroup(%s): %s", regionSecurityGroupNameAndPorts, e.getMessage());
            org.jclouds.openstack.nova.v2_0.domain.SecurityGroup group = find(allGroups,
                  nameEquals(regionSecurityGroupNameAndPorts.getName()));
            logger.debug("<< reused securityGroup(%s)", group.getId());
            return securityGroupInRegionSecurityGroupFunction
                  .apply(new SecurityGroupInRegion(group, regionId, allGroups));
         }
      }
   }

   private SecurityGroup createSecurityGroupFrom(final org.jclouds.openstack.neutron.v2.domain.SecurityGroup group,
         Location location, Set<Integer> ports) {
      SecurityGroup securityGroup = neutronSecurityGroupToSecurityGroup.create(location).apply(group);
      logger.debug("<< created securityGroup(%s)", securityGroup);

      SecurityGroupApi securityGroupApi = getNeutronSecurityGroupApi(location.getId());
      try {
         for (int inboundPort : ports) {
            logger.debug(">> authorizing securityGroup(%s) permission to 0.0.0.0/0 on port %d", securityGroup, inboundPort);
            securityGroupApi.create(
                  Rule.CreateRule.createBuilder(RuleDirection.INGRESS, RegionAndId.fromSlashEncoded(securityGroup.getId()).getId()).protocol(RuleProtocol.TCP)
                        .portRangeMin(inboundPort).portRangeMax(inboundPort).remoteIpPrefix("0.0.0.0/0").build());
            logger.debug("<< authorized securityGroup(%s) permission to 0.0.0.0/0 on port %d", securityGroup, inboundPort);
         }
         return securityGroup;
      } catch (IllegalStateException e) {
         logger.trace("<< trying to find securityGroup(%s): %s", group, e.getMessage());

         return securityGroupApi.listSecurityGroups().concat()
               .filter(new Predicate<org.jclouds.openstack.neutron.v2.domain.SecurityGroup>() {
                  @Override
                  public boolean apply(@Nullable org.jclouds.openstack.neutron.v2.domain.SecurityGroup input) {
                     return input.getName().equals(group.getName());
                  }
               }).transform(neutronSecurityGroupToSecurityGroup.create(location)).first().orNull();
      }
   }

   private SecurityGroupApi getNeutronSecurityGroupApi(String region) {
      if (neutronContextSupplier == null)
         return null;
      return ((ApiContext<NeutronApi>) neutronContextSupplier.get()).getApi().getSecurityGroupApi(region);
   }

   private void authorizeGroupToItselfAndAllIPsToTCPPort(
         org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi securityGroupApi,
         org.jclouds.openstack.nova.v2_0.domain.SecurityGroup securityGroup, int port) {
      logger.debug(">> authorizing securityGroup(%s) permission to 0.0.0.0/0 on port %d", securityGroup, port);
      securityGroupApi.createRuleAllowingCidrBlock(securityGroup.getId(),
            Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(port).toPort(port).build(), "0.0.0.0/0");
      logger.debug("<< authorized securityGroup(%s) permission to 0.0.0.0/0 on port %d", securityGroup, port);
   }

}
