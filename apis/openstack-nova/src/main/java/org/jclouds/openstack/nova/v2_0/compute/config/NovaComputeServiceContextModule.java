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
package org.jclouds.openstack.nova.v2_0.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_ALLOCATE_FLOATING_IPS;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_GENERATE_KEYPAIRS;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.TIMEOUT_SECURITYGROUP_PRESENT;
import static org.jclouds.util.Predicates2.retry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.NovaComputeService;
import org.jclouds.openstack.nova.v2_0.compute.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.v2_0.compute.extensions.NovaImageExtension;
import org.jclouds.openstack.nova.v2_0.compute.extensions.NovaSecurityGroupExtension;
import org.jclouds.openstack.nova.v2_0.compute.functions.CleanupResources;
import org.jclouds.openstack.nova.v2_0.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.openstack.nova.v2_0.compute.functions.FlavorInRegionToHardware;
import org.jclouds.openstack.nova.v2_0.compute.functions.ImageInRegionToImage;
import org.jclouds.openstack.nova.v2_0.compute.functions.ImageToOperatingSystem;
import org.jclouds.openstack.nova.v2_0.compute.functions.NovaSecurityGroupInRegionToSecurityGroup;
import org.jclouds.openstack.nova.v2_0.compute.functions.OrphanedGroupsByRegionId;
import org.jclouds.openstack.nova.v2_0.compute.functions.ServerInRegionToNodeMetadata;
import org.jclouds.openstack.nova.v2_0.compute.loaders.FindSecurityGroupOrCreate;
import org.jclouds.openstack.nova.v2_0.compute.loaders.LoadFloatingIpsForInstance;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.compute.strategy.ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.FlavorInRegion;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.ImageInRegion;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndId;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.ServerInRegion;
import org.jclouds.openstack.nova.v2_0.predicates.FindSecurityGroupWithNameAndReturnTrue;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Module for building a compute service context for Nova
 */
public class NovaComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<ServerInRegion, FlavorInRegion, ImageInRegion, Location> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<ServerInRegion, FlavorInRegion, ImageInRegion, Location>>() {
      }).to(NovaComputeServiceAdapter.class);

      bind(ComputeService.class).to(NovaComputeService.class);

      bind(new TypeLiteral<Function<ServerInRegion, NodeMetadata>>() {
      }).to(ServerInRegionToNodeMetadata.class);

      bind(new TypeLiteral<Function<SecurityGroupInRegion, SecurityGroup>>() {
      }).to(NovaSecurityGroupInRegionToSecurityGroup.class);

      bind(new TypeLiteral<Function<Set<? extends NodeMetadata>,  Multimap<String, String>>>() {
      }).to(OrphanedGroupsByRegionId.class);

      bind(new TypeLiteral<Function<ImageInRegion, Image>>() {
      }).to(ImageInRegionToImage.class);
      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.v2_0.domain.Image, OperatingSystem>>() {
      }).to(ImageToOperatingSystem.class);

      bind(new TypeLiteral<Function<FlavorInRegion, Hardware>>() {
      }).to(FlavorInRegionToHardware.class);

      // we aren't converting location from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));

      bind(TemplateOptions.class).to(NovaTemplateOptions.class);

      bind(new TypeLiteral<CacheLoader<RegionAndId, Iterable<? extends FloatingIP>>>() {
      }).annotatedWith(Names.named("FLOATINGIP")).to(LoadFloatingIpsForInstance.class);

      bind(new TypeLiteral<Function<RegionSecurityGroupNameAndPorts, SecurityGroupInRegion>>() {
      }).to(CreateSecurityGroupIfNeeded.class);

      bind(new TypeLiteral<CacheLoader<RegionAndName, SecurityGroupInRegion>>() {
      }).to(FindSecurityGroupOrCreate.class);

      bind(CreateNodesWithGroupEncodedIntoNameThenAddToSet.class).to(
               ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet.class);

      bind(new TypeLiteral<ImageExtension>() {
      }).to(NovaImageExtension.class);

      bind(new TypeLiteral<SecurityGroupExtension>() {
      }).to(NovaSecurityGroupExtension.class);

      bind(new TypeLiteral<Function<NodeMetadata, Boolean>>() {
      }).to(CleanupResources.class);
   }

   @Override
   protected TemplateOptions provideTemplateOptions(Injector injector, TemplateOptions options) {
      return options.as(NovaTemplateOptions.class)
            .autoAssignFloatingIp(injector.getInstance(
                  Key.get(boolean.class, Names.named(AUTO_ALLOCATE_FLOATING_IPS))))
            .generateKeyPair(injector.getInstance(
                  Key.get(boolean.class, Names.named(AUTO_GENERATE_KEYPAIRS))));
   }

   @Provides
   @com.google.inject.name.Named(TIMEOUT_NODE_RUNNING)
   protected Predicate<RegionAndId> provideServerRunningPredicate(final NovaApi api,
                                                             ComputeServiceConstants.Timeouts timeouts,
                                                             ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new ServerInStatusPredicate(api, Status.ACTIVE), timeouts.nodeRunning,
              pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @com.google.inject.name.Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<RegionAndId> provideServerTerminatedPredicate(final NovaApi api, ComputeServiceConstants.Timeouts timeouts,
                                                                ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new ServerTerminatedPredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
              pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Singleton
   @Named("FLOATINGIP")
   protected final LoadingCache<RegionAndId, Iterable<? extends FloatingIP>> instanceToFloatingIps(
            @Named("FLOATINGIP") CacheLoader<RegionAndId, Iterable<? extends FloatingIP>> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   protected final LoadingCache<RegionAndName, SecurityGroupInRegion> securityGroupMap(
            CacheLoader<RegionAndName, SecurityGroupInRegion> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Override
   protected Map<OsFamily, LoginCredentials> osFamilyToCredentials(Injector injector) {
      return ImmutableMap.of(OsFamily.WINDOWS, LoginCredentials.builder().user("Administrator").build(),
               OsFamily.UBUNTU, LoginCredentials.builder().user("ubuntu").build());
   }

   @Provides
   @Singleton
   @Named("SECURITYGROUP_PRESENT")
   protected final Predicate<AtomicReference<RegionAndName>> securityGroupEventualConsistencyDelay(
            FindSecurityGroupWithNameAndReturnTrue in,
            @Named(TIMEOUT_SECURITYGROUP_PRESENT) long msDelay) {
      return retry(in, msDelay, 100L, MILLISECONDS);
   }

   @Provides
   @Singleton
   protected final Supplier<Map<String, Location>> createLocationIndexedById(
            @Memoized Supplier<Set<? extends Location>> locations) {
      return Suppliers.compose(new Function<Set<? extends Location>, Map<String, Location>>() {

         @SuppressWarnings("unchecked")
         @Override
         public Map<String, Location> apply(Set<? extends Location> arg0) {
            // TODO: find a nice way to get rid of this cast.
            Iterable<Location> locations = (Iterable<Location>) arg0;
            return Maps.uniqueIndex(locations, new Function<Location, String>() {

               @Override
               public String apply(Location arg0) {
                  return arg0.getId();
               }

            });
         }
      }, locations);

   }

   @VisibleForTesting
   public static final Map<Status, NodeMetadata.Status> toPortableNodeStatus = ImmutableMap
            .<Status, NodeMetadata.Status> builder()
            .put(Status.ACTIVE, NodeMetadata.Status.RUNNING)
            .put(Status.BUILD, NodeMetadata.Status.PENDING)
            .put(Status.DELETED, NodeMetadata.Status.TERMINATED)
            .put(Status.ERROR, NodeMetadata.Status.ERROR)
            .put(Status.HARD_REBOOT, NodeMetadata.Status.PENDING)
            .put(Status.MIGRATING, NodeMetadata.Status.PENDING)
            .put(Status.PASSWORD, NodeMetadata.Status.PENDING)
            .put(Status.PAUSED, NodeMetadata.Status.SUSPENDED)
            .put(Status.REBOOT, NodeMetadata.Status.PENDING)
            .put(Status.REBUILD, NodeMetadata.Status.PENDING)
            .put(Status.RESCUE, NodeMetadata.Status.PENDING)
            .put(Status.RESIZE, NodeMetadata.Status.PENDING)
            .put(Status.REVERT_RESIZE, NodeMetadata.Status.PENDING)
            .put(Status.SHELVED, NodeMetadata.Status.SUSPENDED)
            .put(Status.SHELVED_OFFLOADED, NodeMetadata.Status.SUSPENDED)
            .put(Status.SHUTOFF, NodeMetadata.Status.SUSPENDED)
            .put(Status.SOFT_DELETED, NodeMetadata.Status.TERMINATED)
            .put(Status.STOPPED, NodeMetadata.Status.SUSPENDED)
            .put(Status.SUSPENDED, NodeMetadata.Status.SUSPENDED)
            .put(Status.UNKNOWN, NodeMetadata.Status.UNRECOGNIZED)
            .put(Status.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED)
            .put(Status.VERIFY_RESIZE, NodeMetadata.Status.PENDING)
            .build();

   @Singleton
   @Provides
   protected final Map<Status, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }

   @VisibleForTesting
   public static final Map<org.jclouds.openstack.nova.v2_0.domain.Image.Status, Image.Status> toPortableImageStatus = ImmutableMap
            .<org.jclouds.openstack.nova.v2_0.domain.Image.Status, Image.Status> builder()
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.ACTIVE, Image.Status.AVAILABLE)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.SAVING, Image.Status.PENDING)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.DELETED, Image.Status.DELETED)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.ERROR, Image.Status.ERROR)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.UNKNOWN, Image.Status.UNRECOGNIZED)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.UNRECOGNIZED, Image.Status.UNRECOGNIZED).build();


   @VisibleForTesting
   static class ServerInStatusPredicate implements Predicate<RegionAndId> {

      private final NovaApi api;
      private final Status status;

      public ServerInStatusPredicate(NovaApi api, Status status) {
         this.api = checkNotNull(api, "api must not be null");
         this.status = checkNotNull(status, "status must not be null");
      }

      @Override
      public boolean apply(RegionAndId regionAndId) {
         checkNotNull(regionAndId, "regionAndId");
         Server server = api.getServerApi(regionAndId.getRegion()).get(regionAndId.getId());
         if (server == null) {
            throw new IllegalStateException(String.format("Server %s not found.", regionAndId.getId()));
         }
         return status.equals(server.getStatus());      
      }
   }

   @VisibleForTesting
   static class ServerTerminatedPredicate implements Predicate<RegionAndId> {

      private final NovaApi api;

      public ServerTerminatedPredicate(NovaApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(RegionAndId regionAndId) {
         checkNotNull(regionAndId, "regionAndId");
         Server server = api.getServerApi(regionAndId.getRegion()).get(regionAndId.getId());
         return server == null;
      }
   }

   @Singleton
   @Provides
   protected final Map<org.jclouds.openstack.nova.v2_0.domain.Image.Status, Image.Status> toPortableImageStatus() {
      return toPortableImageStatus;
   }
}
