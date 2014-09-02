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
package org.jclouds.openstack.nova.v2_0.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.openstack.nova.v2_0.predicates.KeyPairPredicates.nameMatches;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetImageStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListeningExecutorService;

@Singleton
public class NovaComputeService extends BaseComputeService {
   protected final NovaApi novaApi;
   protected final LoadingCache<RegionAndName, SecurityGroupInRegion> securityGroupMap;
   protected final LoadingCache<RegionAndName, KeyPair> keyPairCache;
   protected final Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByRegionId;
   protected final GroupNamingConvention.Factory namingConvention;

   @Inject
   protected NovaComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
            @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetImageStrategy getImageStrategy, GetNodeMetadataStrategy getNodeMetadataStrategy,
            CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy, RebootNodeStrategy rebootNodeStrategy,
            DestroyNodeStrategy destroyNodeStrategy, ResumeNodeStrategy startNodeStrategy,
            SuspendNodeStrategy stopNodeStrategy, Provider<TemplateBuilder> templateBuilderProvider,
            @Named("DEFAULT") Provider<TemplateOptions> templateOptionsProvider,
            @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            @Named(TIMEOUT_NODE_TERMINATED) Predicate<AtomicReference<NodeMetadata>> nodeTerminated,
            @Named(TIMEOUT_NODE_SUSPENDED) Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
            InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
            RunScriptOnNode.Factory runScriptOnNodeFactory, InitAdminAccess initAdminAccess,
            PersistNodeCredentials persistNodeCredentials, Timeouts timeouts,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, NovaApi novaApi,
            LoadingCache<RegionAndName, SecurityGroupInRegion> securityGroupMap,
            LoadingCache<RegionAndName, KeyPair> keyPairCache,
            Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByRegionId,
            GroupNamingConvention.Factory namingConvention, Optional<ImageExtension> imageExtension,
            Optional<SecurityGroupExtension> securityGroupExtension) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getImageStrategy,
               getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
               startNodeStrategy, stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
               nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
               persistNodeCredentials, timeouts, userExecutor, imageExtension, securityGroupExtension);
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.securityGroupMap = checkNotNull(securityGroupMap, "securityGroupMap");
      this.keyPairCache = checkNotNull(keyPairCache, "keyPairCache");
      this.orphanedGroupsByRegionId = checkNotNull(orphanedGroupsByRegionId, "orphanedGroupsByRegionId");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   protected void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      Multimap<String, String> regionToRegionAndGroupNames = orphanedGroupsByRegionId.apply(deadNodes);
      for (Map.Entry<String, Collection<String>> entry : regionToRegionAndGroupNames.asMap().entrySet()) {
         cleanOrphanedGroupsInRegion(ImmutableSet.copyOf(entry.getValue()), entry.getKey());
      }
   }

   protected void cleanOrphanedGroupsInRegion(Set<String> groups, String regionId) {
      cleanupOrphanedSecurityGroupsInRegion(groups, regionId);
      cleanupOrphanedKeyPairsInRegion(groups, regionId);
   }

   private void cleanupOrphanedSecurityGroupsInRegion(Set<String> groups, String regionId) {
      Optional<? extends SecurityGroupApi> securityGroupApi = novaApi.getSecurityGroupApi(regionId);
      if (securityGroupApi.isPresent()) {
         for (String group : groups) {
            for (SecurityGroup securityGroup : Iterables.filter(securityGroupApi.get().list(),
                     SecurityGroupPredicates.nameMatches(namingConvention.create().containsGroup(group)))) {
               RegionAndName regionAndName = RegionAndName.fromRegionAndName(regionId, securityGroup.getName());
               logger.debug(">> deleting securityGroup(%s)", regionAndName);
               securityGroupApi.get().delete(securityGroup.getId());
               // TODO: test this clear happens
               securityGroupMap.invalidate(regionAndName);
               logger.debug("<< deleted securityGroup(%s)", regionAndName);
            }
         }
      }
   }

   private void cleanupOrphanedKeyPairsInRegion(Set<String> groups, String regionId) {
      Optional<? extends KeyPairApi> keyPairApi = novaApi.getKeyPairApi(regionId);
      if (keyPairApi.isPresent()) {
         for (String group : groups) {
            for (KeyPair pair : keyPairApi.get().list().filter(nameMatches(namingConvention.create().containsGroup(group)))) {
               RegionAndName regionAndName = RegionAndName.fromRegionAndName(regionId, pair.getName());
               logger.debug(">> deleting keypair(%s)", regionAndName);
               keyPairApi.get().delete(pair.getName());
               // TODO: test this clear happens
               keyPairCache.invalidate(regionAndName);
               logger.debug("<< deleted keypair(%s)", regionAndName);
            }
            keyPairCache.invalidate(RegionAndName.fromRegionAndName(regionId,
                     namingConvention.create().sharedNameForGroup(group)));
         }
      }
   }

   /**
    * returns template options, except of type {@link NovaTemplateOptions}.
    */
   @Override
   public NovaTemplateOptions templateOptions() {
      return NovaTemplateOptions.class.cast(super.templateOptions());
   }

}
