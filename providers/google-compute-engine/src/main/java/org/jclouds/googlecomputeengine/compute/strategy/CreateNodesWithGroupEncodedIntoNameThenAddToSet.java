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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.of;

import java.util.List;
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
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.domain.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.googlecomputeengine.options.FirewallOptions;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

public final class CreateNodesWithGroupEncodedIntoNameThenAddToSet extends
        org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   public static final String EXTERIOR_RANGE = "0.0.0.0/0";
   public static final String DEFAULT_INTERNAL_NETWORK_RANGE = "10.0.0.0/8";

   private final GoogleComputeEngineApi api;
   private final Supplier<String> userProject;
   private final LoadingCache<NetworkAndAddressRange, Network> networkMap;
   private final Predicate<AtomicReference<Operation>> operationDone;
   private final FirewallTagNamingConvention.Factory firewallTagNamingConvention;

   @Inject CreateNodesWithGroupEncodedIntoNameThenAddToSet(
           CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
           ListNodesStrategy listNodesStrategy,
           GroupNamingConvention.Factory namingConvention,
           @Named(Constants.PROPERTY_USER_THREADS)
           ListeningExecutorService userExecutor,
           CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory
                   customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
           GoogleComputeEngineApi api,
           @UserProject Supplier<String> userProject,
           Predicate<AtomicReference<Operation>> operationDone,
           LoadingCache<NetworkAndAddressRange, Network> networkMap,
           FirewallTagNamingConvention.Factory firewallTagNamingConvention) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
              customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = api;
      this.userProject = userProject;
      this.operationDone = operationDone;
      this.networkMap = networkMap;
      this.firewallTagNamingConvention = firewallTagNamingConvention;
   }

   @Override // TODO: why synchronized?
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

      // get or insert the network and insert a firewall with the users configuration
      Network network = getOrCreateNetwork(templateOptions, sharedResourceName);
      getOrCreateFirewalls(templateOptions, network, firewallTagNamingConvention.get(group));
      templateOptions.network(network.selfLink());
      templateOptions.userMetadata(ComputeServiceConstants.NODE_GROUP_KEY, group);

      return super.execute(group, count, mutableTemplate, goodNodes, badNodes, customizationResponses);
   }

   /**
    * Try and find a network either previously created by jclouds or user defined.
    */
   private Network getOrCreateNetwork(GoogleComputeEngineTemplateOptions templateOptions, String sharedResourceName) {

      String networkName = templateOptions.getNetworkName().or(sharedResourceName);

      return networkMap.apply(NetworkAndAddressRange.create(networkName, DEFAULT_INTERNAL_NETWORK_RANGE, null));
   }

   /**
    * Ensures that a firewall exists for every inbound port that the instance requests.
    * <p>
    * For each port, there must be a firewall with a name following the {@link FirewallTagNamingConvention},
    * with a target tag also following the {@link FirewallTagNamingConvention}, which opens the requested port
    * for all sources on both TCP and UDP protocols.
    * @see org.jclouds.googlecomputeengine.features.FirewallApi#patch(String, org.jclouds.googlecomputeengine.options.FirewallOptions)
    */
   private void getOrCreateFirewalls(GoogleComputeEngineTemplateOptions templateOptions, Network network,
                                     FirewallTagNamingConvention naming) {

      String projectName = userProject.get();
      FirewallApi firewallApi = api.getFirewallApi(projectName);
      List<AtomicReference<Operation>> operations = Lists.newArrayList();

      for (Integer port : templateOptions.getInboundPorts()) {
         String name = naming.name(port);
         Firewall firewall = firewallApi.get(name);
         if (firewall == null) {
            List<String> ports = ImmutableList.of(String.valueOf(port));
            List<Rule> rules = ImmutableList.of(Rule.create("tcp", ports), Rule.create("udp", ports));
            FirewallOptions firewallOptions = new FirewallOptions()
                    .name(name)
                    .network(network.selfLink())
                    .allowedRules(rules)
                    .sourceTags(templateOptions.getTags())
                    .sourceRanges(of(DEFAULT_INTERNAL_NETWORK_RANGE, EXTERIOR_RANGE))
                    .targetTags(ImmutableList.of(name));
            AtomicReference<Operation> operation = Atomics.newReference(firewallApi.createInNetwork(
                    firewallOptions.name(),
                    network.selfLink(),
                    firewallOptions));
            operations.add(operation);
         }
      }

      for (AtomicReference<Operation> operation : operations) {
         operationDone.apply(operation);
         checkState(operation.get().httpErrorStatusCode() == null, "Could not insert firewall, operation failed %s",
               operation);
      }
   }
}
