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
package org.jclouds.googlecomputeengine.compute.config;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Scopes;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.GoogleComputeEngineService;
import org.jclouds.googlecomputeengine.compute.GoogleComputeEngineServiceAdapter;
import org.jclouds.googlecomputeengine.compute.extensions.GoogleComputeEngineSecurityGroupExtension;
import org.jclouds.googlecomputeengine.compute.functions.BuildInstanceMetadata;
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.functions.FirewallToIpPermission;
import org.jclouds.googlecomputeengine.compute.functions.GoogleComputeEngineImageToImage;
import org.jclouds.googlecomputeengine.compute.functions.InstanceInZoneToNodeMetadata;
import org.jclouds.googlecomputeengine.compute.functions.MachineTypeInZoneToHardware;
import org.jclouds.googlecomputeengine.compute.functions.NetworkToSecurityGroup;
import org.jclouds.googlecomputeengine.compute.functions.OrphanedGroupsFromDeadNodes;
import org.jclouds.googlecomputeengine.compute.functions.RegionToLocation;
import org.jclouds.googlecomputeengine.compute.functions.ZoneToLocation;
import org.jclouds.googlecomputeengine.compute.loaders.FindNetworkOrCreate;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.compute.predicates.AllNodesInGroupTerminated;
import org.jclouds.googlecomputeengine.compute.strategy.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.googlecomputeengine.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.googlecomputeengine.compute.strategy.UseNodeCredentialsButOverrideFromTemplate;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceInZone;
import org.jclouds.googlecomputeengine.domain.MachineTypeInZone;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Region;
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.domain.internal.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.functions.CreateNetworkIfNeeded;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class GoogleComputeEngineServiceContextModule
        extends ComputeServiceAdapterContextModule<InstanceInZone, MachineTypeInZone, Image, Zone> {

   @Override
   protected void configure() {
      super.configure();

      bind(ComputeService.class).to(GoogleComputeEngineService.class);

      bind(new TypeLiteral<ComputeServiceAdapter<InstanceInZone, MachineTypeInZone, Image, Zone>>() {})
              .to(GoogleComputeEngineServiceAdapter.class);

      bind(new TypeLiteral<Function<InstanceInZone, NodeMetadata>>() {})
              .to(InstanceInZoneToNodeMetadata.class);

      bind(new TypeLiteral<Function<MachineTypeInZone, Hardware>>() {})
              .to(MachineTypeInZoneToHardware.class);

      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {})
              .to(GoogleComputeEngineImageToImage.class);

      bind(new TypeLiteral<Function<Region, Location>>() {
      })
              .to(RegionToLocation.class);

      bind(new TypeLiteral<Function<Zone, Location>>() {})
              .to(ZoneToLocation.class);

      bind(new TypeLiteral<Function<Firewall, Iterable<IpPermission>>>() {})
              .to(FirewallToIpPermission.class);

      bind(new TypeLiteral<Function<Network, SecurityGroup>>() {})
              .to(NetworkToSecurityGroup.class);

      bind(new TypeLiteral<Function<TemplateOptions, ImmutableMap.Builder<String, String>>>() {})
              .to(BuildInstanceMetadata.class);

      bind(org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy.class)
              .to(PopulateDefaultLoginCredentialsForImageStrategy.class);

      bind(org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet.class).to(
              CreateNodesWithGroupEncodedIntoNameThenAddToSet.class);

      bind(TemplateOptions.class).to(GoogleComputeEngineTemplateOptions.class);

      bind(new TypeLiteral<Function<Set<? extends NodeMetadata>, Set<String>>>() {})
              .to(OrphanedGroupsFromDeadNodes.class);

      bind(new TypeLiteral<Predicate<String>>() {}).to(AllNodesInGroupTerminated.class);

      bind(new TypeLiteral<Function<NetworkAndAddressRange, Network>>() {})
              .to(CreateNetworkIfNeeded.class);

      bind(new TypeLiteral<CacheLoader<NetworkAndAddressRange, Network>>() {})
              .to(FindNetworkOrCreate.class);

      bind(new TypeLiteral<SecurityGroupExtension>() {})
              .to(GoogleComputeEngineSecurityGroupExtension.class);

      bind(PrioritizeCredentialsFromTemplate.class).to(UseNodeCredentialsButOverrideFromTemplate.class);

      install(new LocationsFromComputeServiceAdapterModule<InstanceInZone, MachineTypeInZone, Image, Zone>() {});

      bind(FirewallTagNamingConvention.Factory.class).in(Scopes.SINGLETON);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<URI, ? extends org.jclouds.compute.domain.Image>> provideImagesMap(
           AtomicReference<AuthorizationException> authException,
           final Supplier<Set<? extends org.jclouds.compute.domain.Image>> images,
           @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
              new Supplier<Map<URI, ? extends org.jclouds.compute.domain.Image>>() {
                 @Override
                 public Map<URI, ? extends org.jclouds.compute.domain.Image> get() {
                    return uniqueIndex(images.get(), new Function<org.jclouds.compute.domain.Image, URI>() {
                       @Override
                       public URI apply(org.jclouds.compute.domain.Image input) {
                          return input.getUri();
                       }
                    });
                 }
              },
              seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<URI, ? extends Hardware>> provideHardwaresMap(
           AtomicReference<AuthorizationException> authException,
           final Supplier<Set<? extends Hardware>> hardwares,
           @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
              new Supplier<Map<URI, ? extends Hardware>>() {
                 @Override
                 public Map<URI, ? extends Hardware> get() {
                    return uniqueIndex(hardwares.get(), new Function<Hardware, URI>() {
                       @Override
                       public URI apply(Hardware input) {
                          return input.getUri();
                       }
                    });
                 }
              },
              seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<URI, ? extends Location>> provideZones(
           AtomicReference<AuthorizationException> authException,
           final GoogleComputeEngineApi api, final Function<Zone, Location> zoneToLocation,
           @UserProject final Supplier<String> userProject,
           @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
              new Supplier<Map<URI, ? extends Location>>() {
                 @Override
                 public Map<URI, ? extends Location> get() {
                    return uniqueIndex(transform(api.getZoneApiForProject(userProject.get()).list().concat(), zoneToLocation),
                            new Function<Location, URI>() {
                               @Override
                               public URI apply(Location input) {
                                  return (URI) input.getMetadata().get("selfLink");
                               }
                            });
                 }
              },
              seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<URI, Region>> provideRegions(
           AtomicReference<AuthorizationException> authException,
           final GoogleComputeEngineApi api,
           @UserProject final Supplier<String> userProject,
           @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
              new Supplier<Map<URI, Region>>() {
                 @Override
                 public Map<URI, Region> get() {
                    return uniqueIndex(api.getRegionApiForProject(userProject.get()).list().concat(),
                            new Function<Region, URI>() {
                               @Override
                               public URI apply(Region input) {
                                  return input.getSelfLink();
                               }
                            });
                 }
              },
              seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected LoadingCache<NetworkAndAddressRange, Network> networkMap(
           CacheLoader<NetworkAndAddressRange, Network> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Override
   protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.absent();
   }

   @Override
   protected Optional<SecurityGroupExtension> provideSecurityGroupExtension(Injector i) {
      return Optional.of(i.getInstance(SecurityGroupExtension.class));
   }

   @VisibleForTesting
   public static final Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus =
           ImmutableMap.<Instance.Status, NodeMetadata.Status>builder()
                   .put(Instance.Status.PROVISIONING, NodeMetadata.Status.PENDING)
                   .put(Instance.Status.STAGING, NodeMetadata.Status.PENDING)
                   .put(Instance.Status.RUNNING, NodeMetadata.Status.RUNNING)
                   .put(Instance.Status.STOPPING, NodeMetadata.Status.PENDING)
                   .put(Instance.Status.STOPPED, NodeMetadata.Status.SUSPENDED)
                   .put(Instance.Status.TERMINATED, NodeMetadata.Status.TERMINATED).build();

   @Singleton
   @Provides
   protected Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }
}
