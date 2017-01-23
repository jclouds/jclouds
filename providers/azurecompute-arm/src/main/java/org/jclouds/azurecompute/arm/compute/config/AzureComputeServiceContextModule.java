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
package org.jclouds.azurecompute.arm.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.util.Predicates2.retry;

import java.net.URI;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.AzureComputeService;
import org.jclouds.azurecompute.arm.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.arm.compute.domain.RegionAndIdAndIngressRules;
import org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension;
import org.jclouds.azurecompute.arm.compute.extensions.AzureComputeSecurityGroupExtension;
import org.jclouds.azurecompute.arm.compute.functions.LocationToLocation;
import org.jclouds.azurecompute.arm.compute.functions.NetworkSecurityGroupToSecurityGroup;
import org.jclouds.azurecompute.arm.compute.functions.NetworkSecurityRuleToIpPermission;
import org.jclouds.azurecompute.arm.compute.functions.ResourceDefinitionToCustomImage;
import org.jclouds.azurecompute.arm.compute.functions.VMHardwareToHardware;
import org.jclouds.azurecompute.arm.compute.functions.VMImageToImage;
import org.jclouds.azurecompute.arm.compute.functions.VirtualMachineToNodeMetadata;
import org.jclouds.azurecompute.arm.compute.loaders.CreateSecurityGroupIfNeeded;
import org.jclouds.azurecompute.arm.compute.loaders.ResourceGroupForLocation;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.compute.strategy.CreateResourceGroupThenCreateNodes;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.VMHardware;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance.PowerState;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.NodeAndTemplateOptionsToStatement;
import org.jclouds.compute.functions.NodeAndTemplateOptionsToStatementWithoutPublicKey;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.net.domain.IpPermission;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class AzureComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<VirtualMachine, VMHardware, VMImage, Location> {

   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<ComputeServiceAdapter<VirtualMachine, VMHardware, VMImage, Location>>() {
      }).to(AzureComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<VMImage, org.jclouds.compute.domain.Image>>() {
      }).to(VMImageToImage.class);
      bind(new TypeLiteral<Function<VMHardware, Hardware>>() {
      }).to(VMHardwareToHardware.class);
      bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
      }).to(VirtualMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<Location, org.jclouds.domain.Location>>() {
      }).to(LocationToLocation.class);
      bind(new TypeLiteral<Function<NetworkSecurityGroup, SecurityGroup>>() {
      }).to(NetworkSecurityGroupToSecurityGroup.class);
      bind(new TypeLiteral<Function<NetworkSecurityRule, IpPermission>>() {
      }).to(NetworkSecurityRuleToIpPermission.class);
      bind(ComputeService.class).to(AzureComputeService.class);

      install(new LocationsFromComputeServiceAdapterModule<VirtualMachine, VMHardware, VMImage, Location>() {
      });

      install(new FactoryModuleBuilder().build(ResourceDefinitionToCustomImage.Factory.class));

      bind(TemplateOptions.class).to(AzureTemplateOptions.class);
      bind(NodeAndTemplateOptionsToStatement.class).to(NodeAndTemplateOptionsToStatementWithoutPublicKey.class);
      bind(CreateNodesInGroupThenAddToSet.class).to(CreateResourceGroupThenCreateNodes.class);

      bind(new TypeLiteral<CacheLoader<RegionAndIdAndIngressRules, String>>() {
      }).to(CreateSecurityGroupIfNeeded.class);
      bind(new TypeLiteral<CacheLoader<String, ResourceGroup>>() {
      }).to(ResourceGroupForLocation.class);

      bind(new TypeLiteral<ImageExtension>() {
      }).to(AzureComputeImageExtension.class);
      bind(new TypeLiteral<SecurityGroupExtension>() {
      }).to(AzureComputeSecurityGroupExtension.class);
   }

   @Provides
   @Singleton
   protected final LoadingCache<RegionAndIdAndIngressRules, String> securityGroupMap(
         CacheLoader<RegionAndIdAndIngressRules, String> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   protected final LoadingCache<String, ResourceGroup> resourceGroupMap(CacheLoader<String, ResourceGroup> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Named(TIMEOUT_NODE_RUNNING)
   protected VirtualMachineInStatePredicateFactory provideVirtualMachineRunningPredicate(final AzureComputeApi api,
         final Timeouts timeouts, final PollPeriod pollPeriod) {
      return new VirtualMachineInStatePredicateFactory(api, PowerState.RUNNING, timeouts.nodeRunning,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<URI> provideNodeTerminatedPredicate(final AzureComputeApi api, final Timeouts timeouts,
         final PollPeriod pollPeriod) {
      return retry(new ActionDonePredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_IMAGE_AVAILABLE)
   protected Predicate<URI> provideImageAvailablePredicate(final AzureComputeApi api, final Timeouts timeouts,
         final PollPeriod pollPeriod) {
      return retry(new ImageDonePredicate(api), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_RESOURCE_DELETED)
   protected Predicate<URI> provideResourceDeletedPredicate(final AzureComputeApi api, final Timeouts timeouts,
         final PollPeriod pollPeriod) {
      return retry(new ActionDonePredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected VirtualMachineInStatePredicateFactory provideNodeSuspendedPredicate(final AzureComputeApi api,
         final Timeouts timeouts, final PollPeriod pollPeriod) {
      return new VirtualMachineInStatePredicateFactory(api, PowerState.STOPPED, timeouts.nodeTerminated,
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   protected PublicIpAvailablePredicateFactory providePublicIpAvailablePredicate(final AzureComputeApi api,
         Predicate<Supplier<Provisionable>> resourceAvailable) {
      return new PublicIpAvailablePredicateFactory(api, resourceAvailable);
   }

   @Provides
   protected SecurityGroupAvailablePredicateFactory provideSecurityGroupAvailablePredicate(final AzureComputeApi api,
         Predicate<Supplier<Provisionable>> resourceAvailable) {
      return new SecurityGroupAvailablePredicateFactory(api, resourceAvailable);
   }

   @Provides
   protected Predicate<Supplier<Provisionable>> provideResourceAvailablePredicate(final AzureComputeApi api,
         @Named(OPERATION_TIMEOUT) Integer operationTimeout, PollPeriod pollPeriod) {
      return retry(new ResourceInStatusPredicate("Succeeded"), operationTimeout, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named("STORAGE")
   protected Predicate<URI> provideStorageAccountAvailablePredicate(final AzureComputeApi api,
         @Named(OPERATION_TIMEOUT) Integer operationTimeout, PollPeriod pollPeriod) {
      return retry(new ActionDonePredicate(api), operationTimeout, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @VisibleForTesting
   static class ActionDonePredicate implements Predicate<URI> {

      private final AzureComputeApi api;

      public ActionDonePredicate(final AzureComputeApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(final URI uri) {
         checkNotNull(uri, "uri cannot be null");
         return ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri)
               || ParseJobStatus.JobStatus.NO_CONTENT == api.getJobApi().jobStatus(uri);
      }

   }

   @VisibleForTesting
   static class ImageDonePredicate implements Predicate<URI> {

      private final AzureComputeApi api;

      public ImageDonePredicate(final AzureComputeApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(final URI uri) {
         checkNotNull(uri, "uri cannot be null");
         if (api.getJobApi().jobStatus(uri) != ParseJobStatus.JobStatus.DONE) {
            return false;
         }
         List<ResourceDefinition> definitions = api.getJobApi().captureStatus(uri);
         return definitions != null;
      }
   }

   public static class VirtualMachineInStatePredicateFactory {

      private final AzureComputeApi api;
      private final PowerState powerState;
      private final long timeout;
      private final long period;
      private final long maxPeriod;

      VirtualMachineInStatePredicateFactory(final AzureComputeApi api, final PowerState powerState, final long timeout,
            final long period, final long maxPeriod) {
         this.api = checkNotNull(api, "api cannot be null");
         this.powerState = checkNotNull(powerState, "powerState cannot be null");
         this.timeout = timeout;
         this.period = period;
         this.maxPeriod = maxPeriod;
      }

      public Predicate<String> create(final String azureGroup) {
         return retry(new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               VirtualMachineInstance vmInstance = api.getVirtualMachineApi(azureGroup).getInstanceDetails(name);
               if (vmInstance == null) {
                  return false;
               }
               return powerState == vmInstance.powerState();
            }
         }, timeout, period, maxPeriod);
      }
   }
   
   public static class ResourceInStatusPredicate implements Predicate<Supplier<Provisionable>> {
      private final String expectedStatus;

      ResourceInStatusPredicate(String expectedStatus) {
         this.expectedStatus = checkNotNull(expectedStatus, "expectedStatus cannot be null");
      }

      @Override
      public boolean apply(Supplier<Provisionable> provisionableSupplier) {
         checkNotNull(provisionableSupplier, "provisionableSupplier supplier cannot be null");
         Provisionable provisionable = provisionableSupplier.get();
         return provisionable != null && provisionable.provisioningState().equalsIgnoreCase(expectedStatus);
      }
   }

   public static class PublicIpAvailablePredicateFactory {
      private final AzureComputeApi api;
      private final Predicate<Supplier<Provisionable>> resourceAvailable;

      PublicIpAvailablePredicateFactory(final AzureComputeApi api, Predicate<Supplier<Provisionable>> resourceAvailable) {
         this.api = checkNotNull(api, "api cannot be null");
         this.resourceAvailable = resourceAvailable;
      }

      public Predicate<String> create(final String azureGroup) {
         checkNotNull(azureGroup, "azureGroup cannot be null");
         return new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               return resourceAvailable.apply(new Supplier<Provisionable>() {
                  @Override
                  public Provisionable get() {
                     PublicIPAddress publicIp = api.getPublicIPAddressApi(azureGroup).get(name);
                     return publicIp == null ? null : publicIp.properties();
                  }
               });
            }
         };
      }
   }

   public static class SecurityGroupAvailablePredicateFactory {
      private final AzureComputeApi api;
      private final Predicate<Supplier<Provisionable>> resourceAvailable;

      SecurityGroupAvailablePredicateFactory(final AzureComputeApi api,
            Predicate<Supplier<Provisionable>> resourceAvailable) {
         this.api = checkNotNull(api, "api cannot be null");
         this.resourceAvailable = resourceAvailable;
      }

      public Predicate<String> create(final String resourceGroup) {
         checkNotNull(resourceGroup, "resourceGroup cannot be null");
         return new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               return resourceAvailable.apply(new Supplier<Provisionable>() {
                  @Override
                  public Provisionable get() {
                     NetworkSecurityGroup sg = api.getNetworkSecurityGroupApi(resourceGroup).get(name);
                     return sg == null ? null : sg.properties();
                  }
               });
            }
         };
      }
   }

}
