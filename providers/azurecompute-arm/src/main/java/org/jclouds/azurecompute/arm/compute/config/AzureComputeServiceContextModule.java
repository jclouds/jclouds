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

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.AzureComputeService;
import org.jclouds.azurecompute.arm.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension;
import org.jclouds.azurecompute.arm.compute.functions.DeploymentToNodeMetadata;
import org.jclouds.azurecompute.arm.compute.functions.LocationToLocation;
import org.jclouds.azurecompute.arm.compute.functions.VMHardwareToHardware;
import org.jclouds.azurecompute.arm.compute.functions.VMImageToImage;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.compute.strategy.AzurePopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.azurecompute.arm.compute.strategy.CreateResourceGroupThenCreateNodes;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.VMDeployment;
import org.jclouds.azurecompute.arm.domain.VMHardware;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.DEFAULT_DATADISKSIZE;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.DEFAULT_IMAGE_LOGIN;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.DEFAULT_SUBNET_ADDRESS_PREFIX;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.DEFAULT_VNET_ADDRESS_SPACE_PREFIX;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.IMAGE_PUBLISHERS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_POLL_INITIAL_PERIOD;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_POLL_MAX_PERIOD;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.RESOURCE_GROUP_NAME;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TCP_RULE_FORMAT;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TCP_RULE_REGEXP;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.util.Predicates2.retry;

public class AzureComputeServiceContextModule
        extends ComputeServiceAdapterContextModule<VMDeployment, VMHardware, VMImage, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<VMDeployment, VMHardware, VMImage, Location>>() {
      }).to(AzureComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<VMImage, org.jclouds.compute.domain.Image>>() {
      }).to(VMImageToImage.class);
      bind(new TypeLiteral<Function<VMHardware, Hardware>>() {
      }).to(VMHardwareToHardware.class);
      bind(new TypeLiteral<Function<VMDeployment, NodeMetadata>>() {
      }).to(DeploymentToNodeMetadata.class);
      bind(new TypeLiteral<Function<Location, org.jclouds.domain.Location>>() {
      }).to(LocationToLocation.class);
      bind(ComputeService.class).to(AzureComputeService.class);
      install(new LocationsFromComputeServiceAdapterModule<VMDeployment, VMHardware, VMImage, Location>() {
      });

      bind(TemplateOptions.class).to(AzureTemplateOptions.class);
      bind(PopulateDefaultLoginCredentialsForImageStrategy.class).to(AzurePopulateDefaultLoginCredentialsForImageStrategy.class);
      //bind(TemplateOptionsToStatement.class).to(TemplateOptionsToStatementWithoutPublicKey.class);
      bind(CreateNodesInGroupThenAddToSet.class).to(CreateResourceGroupThenCreateNodes.class);
      bind(new TypeLiteral<ImageExtension>() {
      }).to(AzureComputeImageExtension.class);
   }

   @Singleton
   public static class AzureComputeConstants {

      @Named(OPERATION_TIMEOUT)
      @Inject
      private String operationTimeoutProperty;

      @Named(OPERATION_POLL_INITIAL_PERIOD)
      @Inject
      private String operationPollInitialPeriodProperty;

      @Named(OPERATION_POLL_MAX_PERIOD)
      @Inject
      private String operationPollMaxPeriodProperty;

      @Named(TCP_RULE_FORMAT)
      @Inject
      private String tcpRuleFormatProperty;

      @Named(TCP_RULE_REGEXP)
      @Inject
      private String tcpRuleRegexpProperty;

      @Named(RESOURCE_GROUP_NAME)
      @Inject
      private String azureResourceGroupProperty;

      @Named(IMAGE_PUBLISHERS)
      @Inject
      private String azureImagePublishersProperty;

      @Named(DEFAULT_IMAGE_LOGIN)
      @Inject
      private String azureDefaultImageLoginProperty;

      @Named(DEFAULT_VNET_ADDRESS_SPACE_PREFIX)
      @Inject
      private String azureDefaultVnetAddressPrefixProperty;

      @Named(DEFAULT_SUBNET_ADDRESS_PREFIX)
      @Inject
      private String azureDefaultSubnetAddressPrefixProperty;

      @Named(DEFAULT_DATADISKSIZE)
      @Inject
      private String azureDefaultDataDiskSizeProperty;

      public Long operationTimeout() {
         return Long.parseLong(operationTimeoutProperty);
      }

      public String azureResourceGroup() {
         return azureResourceGroupProperty;
      }

      public String azureImagePublishers() {
         return azureImagePublishersProperty;
      }

      public String azureDefaultImageLogin() {
         return azureDefaultImageLoginProperty;
      }

      public String azureDefaultVnetAddressPrefixProperty() {
         return azureDefaultVnetAddressPrefixProperty;
      }

      public String azureDefaultSubnetAddressPrefixProperty() {
         return azureDefaultSubnetAddressPrefixProperty;
      }

      public String azureDefaultDataDiskSizeProperty() {
         return azureDefaultDataDiskSizeProperty;
      }

      public Integer operationPollInitialPeriod() {
         return Integer.parseInt(operationPollInitialPeriodProperty);
      }

      public Integer operationPollMaxPeriod() {
         return Integer.parseInt(operationPollMaxPeriodProperty);
      }

      public String tcpRuleFormat() {
         return tcpRuleFormatProperty;
      }

      public String tcpRuleRegexp() {
         return tcpRuleRegexpProperty;
      }
   }

   @Provides
   @Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<URI> provideNodeTerminatedPredicate(final AzureComputeApi api, Timeouts timeouts, PollPeriod pollPeriod) {
      return retry(new ActionDonePredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
              pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_IMAGE_AVAILABLE)
   protected Predicate<URI> provideImageAvailablePredicate(final AzureComputeApi api, Timeouts timeouts, PollPeriod pollPeriod) {
      return retry(new ImageDonePredicate(api), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
              pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_RESOURCE_DELETED)
   protected Predicate<URI> provideResourceDeletedPredicate(final AzureComputeApi api, Timeouts timeouts, PollPeriod pollPeriod) {
      return retry(new ActionDonePredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
              pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected Predicate<String> provideNodeSuspendedPredicate(final AzureComputeApi api, final AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants,
                                                             Timeouts timeouts, PollPeriod pollPeriod) {
      String azureGroup = azureComputeConstants.azureResourceGroup();
      return retry(new NodeSuspendedPredicate(api, azureGroup), timeouts.nodeSuspended, pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @VisibleForTesting
   static class ActionDonePredicate implements Predicate<URI> {

      private final AzureComputeApi api;

      public ActionDonePredicate(AzureComputeApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(URI uri) {
         checkNotNull(uri, "uri cannot be null");
         return (ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri)) || (ParseJobStatus.JobStatus.NO_CONTENT == api.getJobApi().jobStatus(uri));
      }

   }

   @VisibleForTesting
   static class ImageDonePredicate implements Predicate<URI> {

      private final AzureComputeApi api;

      public ImageDonePredicate(AzureComputeApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(URI uri) {
         checkNotNull(uri, "uri cannot be null");
         if (api.getJobApi().jobStatus(uri) != ParseJobStatus.JobStatus.DONE) return false;
         List<ResourceDefinition> definitions = api.getJobApi().captureStatus(uri);
         return definitions != null;
      }
   }

   @VisibleForTesting
   static class NodeSuspendedPredicate implements Predicate<String> {

      private final AzureComputeApi api;
      private final String azureGroup;

      public NodeSuspendedPredicate(AzureComputeApi api, String azureGroup) {
         this.api = checkNotNull(api, "api must not be null");
         this.azureGroup = checkNotNull(azureGroup, "azuregroup must not be null");
      }

      @Override
      public boolean apply(String name) {
         checkNotNull(name, "name cannot be null");
         String status = "";
         VirtualMachineInstance virtualMachineInstance = api.getVirtualMachineApi(this.azureGroup).getInstanceDetails(name);
         if (virtualMachineInstance == null) return false;
         List<VirtualMachineInstance.VirtualMachineStatus> statuses = virtualMachineInstance.statuses();
         for (int c = 0; c < statuses.size(); c++) {
            if (statuses.get(c).code().substring(0, 10).equals("PowerState")) {
               status = statuses.get(c).displayStatus();
               break;
            }
         }
         return status.equals("VM stopped");
      }
   }
}
