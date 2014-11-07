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

import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;
import static org.jclouds.util.Predicates2.retry;

import java.net.URI;
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
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.compute.GoogleComputeEngineService;
import org.jclouds.googlecomputeengine.compute.GoogleComputeEngineServiceAdapter;
import org.jclouds.googlecomputeengine.compute.domain.InstanceInZone;
import org.jclouds.googlecomputeengine.compute.domain.MachineTypeInZone;
import org.jclouds.googlecomputeengine.compute.domain.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.compute.extensions.GoogleComputeEngineSecurityGroupExtension;
import org.jclouds.googlecomputeengine.compute.functions.BuildInstanceMetadata;
import org.jclouds.googlecomputeengine.compute.functions.CreateNetworkIfNeeded;
import org.jclouds.googlecomputeengine.compute.functions.FindNetworkOrCreate;
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.functions.FirewallToIpPermission;
import org.jclouds.googlecomputeengine.compute.functions.GoogleComputeEngineImageToImage;
import org.jclouds.googlecomputeengine.compute.functions.InstanceInZoneToNodeMetadata;
import org.jclouds.googlecomputeengine.compute.functions.MachineTypeInZoneToHardware;
import org.jclouds.googlecomputeengine.compute.functions.NetworkToSecurityGroup;
import org.jclouds.googlecomputeengine.compute.functions.OrphanedGroupsFromDeadNodes;
import org.jclouds.googlecomputeengine.compute.functions.ResourceFunctions;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.compute.predicates.AllNodesInGroupTerminated;
import org.jclouds.googlecomputeengine.compute.predicates.AtomicInstanceVisible;
import org.jclouds.googlecomputeengine.compute.predicates.AtomicOperationDone;
import org.jclouds.googlecomputeengine.compute.strategy.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.googlecomputeengine.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.googlecomputeengine.compute.strategy.UseNodeCredentialsButOverrideFromTemplate;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.net.domain.IpPermission;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

public final class GoogleComputeEngineServiceContextModule
      extends ComputeServiceAdapterContextModule<InstanceInZone, MachineTypeInZone, Image, Location> {

   @Override
   protected void configure() {
      super.configure();

      bind(ComputeService.class).to(GoogleComputeEngineService.class);

      bind(new TypeLiteral<ComputeServiceAdapter<InstanceInZone, MachineTypeInZone, Image, Location>>() {
      }).to(GoogleComputeEngineServiceAdapter.class);

      bind(new TypeLiteral<Function<Location, Location>>() {
      }).toInstance(Functions.<Location>identity());

      bind(new TypeLiteral<Function<InstanceInZone, NodeMetadata>>() {
      }).to(InstanceInZoneToNodeMetadata.class);

      bind(new TypeLiteral<Function<MachineTypeInZone, Hardware>>() {
      }).to(MachineTypeInZoneToHardware.class);

      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
      }).to(GoogleComputeEngineImageToImage.class);

      bind(new TypeLiteral<Function<Firewall, Iterable<IpPermission>>>() {
      }).to(FirewallToIpPermission.class);

      bind(new TypeLiteral<Function<Network, SecurityGroup>>() {
      }).to(NetworkToSecurityGroup.class);

      bind(new TypeLiteral<Function<TemplateOptions, ImmutableMap.Builder<String, String>>>() {
      }).to(BuildInstanceMetadata.class);

      bind(org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy.class)
            .to(PopulateDefaultLoginCredentialsForImageStrategy.class);

      bind(org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet.class)
            .to(CreateNodesWithGroupEncodedIntoNameThenAddToSet.class);

      bind(TemplateOptions.class).to(GoogleComputeEngineTemplateOptions.class);

      bind(new TypeLiteral<Function<Set<? extends NodeMetadata>, Set<String>>>() {
      }).to(OrphanedGroupsFromDeadNodes.class);

      bind(new TypeLiteral<Predicate<String>>() {
      }).to(AllNodesInGroupTerminated.class);

      bind(new TypeLiteral<Function<NetworkAndAddressRange, Network>>() {
      }).to(CreateNetworkIfNeeded.class);

      bind(new TypeLiteral<CacheLoader<NetworkAndAddressRange, Network>>() {
      }).to(FindNetworkOrCreate.class);

      bind(SecurityGroupExtension.class).to(GoogleComputeEngineSecurityGroupExtension.class);

      bind(PrioritizeCredentialsFromTemplate.class).to(UseNodeCredentialsButOverrideFromTemplate.class);
      bind(FirewallTagNamingConvention.Factory.class).in(Scopes.SINGLETON);

      bindHttpApi(binder(), ResourceFunctions.class);
   }

   // TODO: these timeouts need thinking through.
   @Provides Predicate<AtomicReference<Operation>> operationDone(AtomicOperationDone input,
         @Named(OPERATION_COMPLETE_TIMEOUT) long timeout, @Named(OPERATION_COMPLETE_INTERVAL) long interval) {
      return retry(input, timeout, interval, MILLISECONDS);
   }

   @Provides Predicate<AtomicReference<Instance>> instanceVisible(AtomicInstanceVisible input,
         @Named(OPERATION_COMPLETE_TIMEOUT) long timeout, @Named(OPERATION_COMPLETE_INTERVAL) long interval) {
      return retry(input, timeout, interval, MILLISECONDS);
   }

   @Provides @Singleton @Memoized Supplier<Map<URI, org.jclouds.compute.domain.Image>> imageByUri(
         @Memoized final Supplier<Set<? extends org.jclouds.compute.domain.Image>> imageSupplier,
         @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return memoizeWithExpiration(new Supplier<Map<URI, org.jclouds.compute.domain.Image>>() {
         @Override public Map<URI, org.jclouds.compute.domain.Image> get() {
            ImmutableMap.Builder<URI, org.jclouds.compute.domain.Image> result = ImmutableMap.builder();
            for (org.jclouds.compute.domain.Image image : imageSupplier.get()) {
               result.put(image.getUri(), image);
            }
            return result.build();
         }
      }, seconds, SECONDS);
   }

   @Provides @Singleton @Memoized Supplier<Map<URI, Hardware>> hardwareByUri(
         @Memoized final Supplier<Set<? extends Hardware>> hardwareSupplier,
         @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return memoizeWithExpiration(new Supplier<Map<URI, Hardware>>() {
         @Override public Map<URI, Hardware> get() {
            ImmutableMap.Builder<URI, Hardware> result = ImmutableMap.builder();
            for (Hardware hardware : hardwareSupplier.get()) {
               result.put(hardware.getUri(), hardware);
            }
            return result.build();
         }
      }, seconds, SECONDS);
   }

   @Provides @Singleton @Memoized Supplier<Map<URI, Location>> locationsByUri(
         @Memoized final Supplier<Set<? extends Location>> locations,
         @Memoized final Supplier<Map<URI, String>> selfLinkToNames, @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return memoizeWithExpiration(new Supplier<Map<URI, Location>>() {
         @Override public Map<URI, Location> get() {
            ImmutableMap.Builder<URI, Location> result = ImmutableMap.builder();
            for (Location location : locations.get()) {
               for (Map.Entry<URI, String> entry : selfLinkToNames.get().entrySet()) {
                  if (entry.getValue().equals(location.getId())) {
                     result.put(entry.getKey(), location);
                     continue;
                  }
               }
            }
            return result.build();
         }
      }, seconds, SECONDS);
   }

   @Provides @Singleton
   LoadingCache<NetworkAndAddressRange, Network> networkMap(CacheLoader<NetworkAndAddressRange, Network> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Override protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.absent();
   }

   @Override protected Optional<SecurityGroupExtension> provideSecurityGroupExtension(Injector i) {
      return Optional.of(i.getInstance(SecurityGroupExtension.class));
   }

   private static final Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus =
         ImmutableMap.<Instance.Status, NodeMetadata.Status>builder()
                     .put(Instance.Status.PROVISIONING, NodeMetadata.Status.PENDING)
                     .put(Instance.Status.STAGING, NodeMetadata.Status.PENDING)
                     .put(Instance.Status.RUNNING, NodeMetadata.Status.RUNNING)
                     .put(Instance.Status.STOPPING, NodeMetadata.Status.PENDING)
                     .put(Instance.Status.STOPPED, NodeMetadata.Status.SUSPENDED)
                     .put(Instance.Status.TERMINATED, NodeMetadata.Status.TERMINATED).build();

   @Provides Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }
}
