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
package org.jclouds.googlecomputeengine.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.util.Predicates2.retry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

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
import org.jclouds.compute.reference.ComputeServiceConstants;
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
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.http.HttpResponse;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListeningExecutorService;

public class GoogleComputeEngineService extends BaseComputeService {

   private final Function<Set<? extends NodeMetadata>, Set<String>> findOrphanedGroups;
   private final GroupNamingConvention.Factory namingConvention;
   private final GoogleComputeEngineApi api;
   private final Supplier<String> project;
   private final Predicate<AtomicReference<Operation>> operationDonePredicate;
   private final long operationCompleteCheckInterval;
   private final long operationCompleteCheckTimeout;

   @Inject
   protected GoogleComputeEngineService(ComputeServiceContext context,
                                        Map<String, Credentials> credentialStore,
                                        @Memoized Supplier<Set<? extends Image>> images,
                                        @Memoized Supplier<Set<? extends Hardware>> hardwareProfiles,
                                        @Memoized Supplier<Set<? extends Location>> locations,
                                        ListNodesStrategy listNodesStrategy,
                                        GetImageStrategy getImageStrategy,
                                        GetNodeMetadataStrategy getNodeMetadataStrategy,
                                        CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy,
                                        RebootNodeStrategy rebootNodeStrategy,
                                        DestroyNodeStrategy destroyNodeStrategy,
                                        ResumeNodeStrategy resumeNodeStrategy,
                                        SuspendNodeStrategy suspendNodeStrategy,
                                        Provider<TemplateBuilder> templateBuilderProvider,
                                        @Named("DEFAULT") Provider<TemplateOptions> templateOptionsProvider,
                                        @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>>
                                                nodeRunning,
                                        @Named(TIMEOUT_NODE_TERMINATED) Predicate<AtomicReference<NodeMetadata>>
                                                nodeTerminated,
                                        @Named(TIMEOUT_NODE_SUSPENDED)
                                        Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
                                        InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
                                        InitAdminAccess initAdminAccess,
                                        RunScriptOnNode.Factory runScriptOnNodeFactory,
                                        PersistNodeCredentials persistNodeCredentials,
                                        ComputeServiceConstants.Timeouts timeouts,
                                        @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                        Optional<ImageExtension> imageExtension,
                                        Optional<SecurityGroupExtension> securityGroupExtension,
                                        Function<Set<? extends NodeMetadata>, Set<String>> findOrphanedGroups,
                                        GroupNamingConvention.Factory namingConvention,
                                        GoogleComputeEngineApi api,
                                        @UserProject Supplier<String> project,
                                        @Named("global") Predicate<AtomicReference<Operation>> operationDonePredicate,
                                        @Named(OPERATION_COMPLETE_INTERVAL) Long operationCompleteCheckInterval,
                                        @Named(OPERATION_COMPLETE_TIMEOUT) Long operationCompleteCheckTimeout) {

      super(context, credentialStore, images, hardwareProfiles, locations, listNodesStrategy, getImageStrategy,
              getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
              resumeNodeStrategy, suspendNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
              nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
              persistNodeCredentials, timeouts, userExecutor, imageExtension, securityGroupExtension);
      this.findOrphanedGroups = checkNotNull(findOrphanedGroups, "find orphaned groups function");
      this.namingConvention = checkNotNull(namingConvention, "naming convention factory");
      this.api = checkNotNull(api, "google compute api");
      this.project = checkNotNull(project, "user project name");
      this.operationDonePredicate = checkNotNull(operationDonePredicate, "operation completed predicate");
      this.operationCompleteCheckInterval = checkNotNull(operationCompleteCheckInterval,
              "operation completed check interval");
      this.operationCompleteCheckTimeout = checkNotNull(operationCompleteCheckTimeout,
              "operation completed check timeout");
   }

   @Override
   protected synchronized void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      Set<String> orphanedGroups = findOrphanedGroups.apply(deadNodes);
      for (String orphanedGroup : orphanedGroups) {
         cleanUpNetworksAndFirewallsForGroup(orphanedGroup);
      }
   }


   protected void cleanUpNetworksAndFirewallsForGroup(final String groupName) {
      String resourceName = namingConvention.create().sharedNameForGroup(groupName);
      final Network network = api.getNetworkApiForProject(project.get()).get(resourceName);
      FirewallApi firewallApi = api.getFirewallApiForProject(project.get());
      Predicate<Firewall> firewallBelongsToNetwork = new Predicate<Firewall>() {
         @Override
         public boolean apply(Firewall input) {
            return input != null && input.getNetwork().equals(network.getSelfLink());
         }
      };

      Set<AtomicReference<Operation>> operations = Sets.newHashSet();
      for (Firewall firewall : firewallApi.list().concat().filter(firewallBelongsToNetwork)) {
         operations.add(new AtomicReference<Operation>(firewallApi.delete(firewall.getName())));
      }

      for (AtomicReference<Operation> operation : operations) {
         retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
                 MILLISECONDS).apply(operation);

         if (operation.get().getHttpError().isPresent()) {
            HttpResponse response = operation.get().getHttpError().get();
            logger.warn("delete orphaned firewall %s failed. Http Error Code: %d HttpError: %s",
                    operation.get().getTargetId(), response.getStatusCode(), response.getMessage());
         }
      }

      AtomicReference<Operation> operation = Atomics.newReference(api.getNetworkApiForProject(project.get()).delete(resourceName));

      retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
              MILLISECONDS).apply(operation);

      if (operation.get().getHttpError().isPresent()) {
         HttpResponse response = operation.get().getHttpError().get();
         logger.warn("delete orphaned network failed. Http Error Code: " + response.getStatusCode() +
                 " HttpError: " + response.getMessage());
      }
   }


   /**
    * returns template options, except of type {@link org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions}.
    */
   @Override
   public GoogleComputeEngineTemplateOptions templateOptions() {
      return GoogleComputeEngineTemplateOptions.class.cast(super.templateOptions());
   }
}
