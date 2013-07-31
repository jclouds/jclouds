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
package org.jclouds.googlecomputeengine.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableSet.of;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.util.Predicates2.retry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.options.FirewallOptions;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * @author David Alves
 */
public class CreateNodesWithGroupEncodedIntoNameThenAddToSet extends
        org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   public static final String EXTERIOR_RANGE = "0.0.0.0/0";
   public static final String DEFAULT_INTERNAL_NETWORK_RANGE = "10.0.0.0/8";

   private final GoogleComputeEngineApi api;
   private final Supplier<String> userProject;
   private final Predicate<AtomicReference<Operation>> operationDonePredicate;
   private final long operationCompleteCheckInterval;
   private final long operationCompleteCheckTimeout;

   @Inject
   protected CreateNodesWithGroupEncodedIntoNameThenAddToSet(
           CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
           ListNodesStrategy listNodesStrategy,
           GroupNamingConvention.Factory namingConvention,
           @Named(Constants.PROPERTY_USER_THREADS)
           ListeningExecutorService userExecutor,
           CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory
                   customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
           GoogleComputeEngineApi api,
           @UserProject Supplier<String> userProject,
           @Named("global") Predicate<AtomicReference<Operation>> operationDonePredicate,
           @Named(OPERATION_COMPLETE_INTERVAL) Long operationCompleteCheckInterval,
           @Named(OPERATION_COMPLETE_TIMEOUT) Long operationCompleteCheckTimeout) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
              customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);

      this.api = checkNotNull(api, "google compute api");
      this.userProject = checkNotNull(userProject, "user project name");
      this.operationCompleteCheckInterval = checkNotNull(operationCompleteCheckInterval,
              "operation completed check interval");
      this.operationCompleteCheckTimeout = checkNotNull(operationCompleteCheckTimeout,
              "operation completed check timeout");
      this.operationDonePredicate = operationDonePredicate;
   }

   @Override
   public synchronized Map<?, ListenableFuture<Void>> execute(String group, int count,
                                                              Template template,
                                                              Set<NodeMetadata> goodNodes,
                                                              Map<NodeMetadata, Exception> badNodes,
                                                              Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      String sharedResourceName = namingConvention.create().sharedNameForGroup(group);
      Template mutableTemplate = template.clone();
      GoogleComputeEngineTemplateOptions templateOptions = GoogleComputeEngineTemplateOptions.class.cast(mutableTemplate
              .getOptions());
      assert template.getOptions().equals(templateOptions) : "options didn't clone properly";

      // get or create the network and create a firewall with the users configuration
      Network network = getOrCreateNetwork(templateOptions, sharedResourceName);
      getOrCreateFirewall(templateOptions, network, sharedResourceName);
      templateOptions.network(network.getSelfLink());

      return super.execute(group, count, mutableTemplate, goodNodes, badNodes, customizationResponses);
   }

   /**
    * Try and find a network either previously created by jclouds or user defined.
    */
   private Network getOrCreateNetwork(GoogleComputeEngineTemplateOptions templateOptions, String sharedResourceName) {

      String networkName = templateOptions.getNetworkName().or(sharedResourceName);

      // check if the network was previously created (cache???)
      Network network = api.getNetworkApiForProject(userProject.get()).get(networkName);

      if (network != null) {
         return network;
      } else if (templateOptions.getNetwork().isPresent()) {
         throw new IllegalArgumentException("requested network " + networkName + " does not exist");
      }

      AtomicReference<Operation> operation = new AtomicReference<Operation>(api.getNetworkApiForProject(userProject
              .get()).createInIPv4Range(sharedResourceName, DEFAULT_INTERNAL_NETWORK_RANGE));
      retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
              MILLISECONDS).apply(operation);

      checkState(!operation.get().getHttpError().isPresent(), "Could not create network, operation failed" + operation);

      return checkNotNull(api.getNetworkApiForProject(userProject.get()).get(sharedResourceName),
              "no network with name %s was found", sharedResourceName);

   }

   /**
    * Tries to find if a firewall already exists for this group, if not it creates one.
    *
    * @see org.jclouds.googlecomputeengine.features.FirewallAsyncApi#patch(String, org.jclouds.googlecomputeengine.options.FirewallOptions)
    */
   private void getOrCreateFirewall(GoogleComputeEngineTemplateOptions templateOptions, Network network,
                                    String sharedResourceName) {

      Firewall firewall = api.getFirewallApiForProject(userProject.get()).get(sharedResourceName);

      if (firewall != null) {
         return;
      }

      ImmutableSet.Builder<Firewall.Rule> rules = ImmutableSet.builder();

      Firewall.Rule.Builder tcpRule = Firewall.Rule.builder();
      tcpRule.IPProtocol(Firewall.Rule.IPProtocol.TCP);
      Firewall.Rule.Builder udpRule = Firewall.Rule.builder();
      udpRule.IPProtocol(Firewall.Rule.IPProtocol.UDP);
      for (Integer port : templateOptions.getInboundPorts()) {
         tcpRule.addPort(port);
         udpRule.addPort(port);
      }
      rules.add(tcpRule.build());
      rules.add(udpRule.build());


      FirewallOptions options = new FirewallOptions()
              .name(sharedResourceName)
              .network(network.getSelfLink())
              .sourceTags(templateOptions.getTags())
              .allowedRules(rules.build())
              .sourceRanges(of(DEFAULT_INTERNAL_NETWORK_RANGE, EXTERIOR_RANGE));

      AtomicReference<Operation> operation = new AtomicReference<Operation>(api.getFirewallApiForProject(userProject
              .get()).createInNetwork(
              sharedResourceName,
              network.getSelfLink(),
              options));

      retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
              MILLISECONDS).apply(operation);

      checkState(!operation.get().getHttpError().isPresent(), "Could not create firewall, operation failed" + operation);
   }


}
