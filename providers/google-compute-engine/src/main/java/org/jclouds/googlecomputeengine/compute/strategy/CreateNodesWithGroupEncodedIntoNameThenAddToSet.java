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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.of;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
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
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshKeyPairGenerator;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

public final class CreateNodesWithGroupEncodedIntoNameThenAddToSet extends
      org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   public static final String EXTERIOR_RANGE = "0.0.0.0/0";
   public static final String DEFAULT_INTERNAL_NETWORK_RANGE = "10.0.0.0/8";

   public static final String DEFAULT_NETWORK_NAME = "default";

   private final GoogleComputeEngineApi api;
   private final Predicate<AtomicReference<Operation>> operationDone;
   private final FirewallTagNamingConvention.Factory firewallTagNamingConvention;
   private final SshKeyPairGenerator keyGenerator;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   CreateNodesWithGroupEncodedIntoNameThenAddToSet(
         CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
         ListNodesStrategy listNodesStrategy,
         GroupNamingConvention.Factory namingConvention,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
         GoogleComputeEngineApi api, Predicate<AtomicReference<Operation>> operationDone,
         FirewallTagNamingConvention.Factory firewallTagNamingConvention, SshKeyPairGenerator keyGenerator) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
            customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = api;
      this.operationDone = operationDone;
      this.firewallTagNamingConvention = firewallTagNamingConvention;
      this.keyGenerator = keyGenerator;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
         Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
         Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

            Template mutableTemplate = template.clone();
      GoogleComputeEngineTemplateOptions templateOptions = GoogleComputeEngineTemplateOptions.class
            .cast(mutableTemplate.getOptions());
      assert template.getOptions().equals(templateOptions) : "options didn't clone properly";

      // Get Network
      Network network = getNetwork(templateOptions.getNetworks());
      // Setup Firewall rules
      getOrCreateFirewalls(templateOptions, network, firewallTagNamingConvention.get(group));
      templateOptions.networks(ImmutableSet.of(network.selfLink().toString()));
      templateOptions.userMetadata(ComputeServiceConstants.NODE_GROUP_KEY, group);

      // Configure the default credentials, if needed
      if (templateOptions.autoCreateKeyPair() && Strings.isNullOrEmpty(templateOptions.getPublicKey())) {
         logger.debug(">> creating default keypair...");
         Map<String, String> defaultKeys = keyGenerator.get();
         templateOptions.authorizePublicKey(defaultKeys.get("public"));
         templateOptions.overrideLoginPrivateKey(defaultKeys.get("private"));
      }

      if (templateOptions.getRunScript() != null && templateOptions.getLoginPrivateKey() == null) {
         logger.warn(">> A runScript has been configured but no SSH key has been provided."
               + " Authentication will delegate to the ssh-agent");
      }

      return super.execute(group, count, mutableTemplate, goodNodes, badNodes, customizationResponses);
   }

   /**
    * Try and find a network previously created by the user.
    */
   private Network getNetwork(Set<String> networks) {
      String networkName;
      if (networks == null || networks.isEmpty()){
         networkName = DEFAULT_NETWORK_NAME;
      }
      else {
         Iterator<String> iterator = networks.iterator();
         networkName = nameFromNetworkString(iterator.next());
         checkArgument(!iterator.hasNext(), "Error: Please specify only one network in TemplateOptions when using GCE.");

      }
      Network network = api.networks().get(networkName);
      checkArgument(network != null, "Error: no network with name %s was found", networkName);
      return network;
   }

   /**
    * Ensures that a firewall exists for every inbound port that the instance
    * requests.
    * <p>
    * For each port, there must be a firewall with a name following the
    * {@link FirewallTagNamingConvention}, with a target tag also following the
    * {@link FirewallTagNamingConvention}, which opens the requested port for
    * all sources on both TCP and UDP protocols.
    *
    * @see org.jclouds.googlecomputeengine.features.FirewallApi#patch(String,
    *      org.jclouds.googlecomputeengine.options.FirewallOptions)
    */
   private void getOrCreateFirewalls(GoogleComputeEngineTemplateOptions templateOptions, Network network,
         FirewallTagNamingConvention naming) {

      FirewallApi firewallApi = api.firewalls();
      int[] inboundPorts = templateOptions.getInboundPorts();
      if ((inboundPorts == null) || inboundPorts.length == 0){
         return;
      }

      List<String> ports = simplifyPorts(inboundPorts);
      String name = naming.name(ports);
      Firewall firewall = firewallApi.get(name);
      AtomicReference<Operation> operation = null;
      if (firewall == null) {
         List<Rule> rules = ImmutableList.of(Rule.create("tcp", ports), Rule.create("udp", ports));
         FirewallOptions firewallOptions = new FirewallOptions().name(name).network(network.selfLink())
                  .allowedRules(rules).sourceTags(templateOptions.getTags())
                  .sourceRanges(of(DEFAULT_INTERNAL_NETWORK_RANGE, EXTERIOR_RANGE))
                  .targetTags(ImmutableList.of(name));

         operation = Atomics.newReference(firewallApi
               .createInNetwork(firewallOptions.name(), network.selfLink(), firewallOptions));

         operationDone.apply(operation);
         checkState(operation.get().httpErrorStatusCode() == null, "Could not insert firewall, operation failed %s",
               operation);
      }
   }

   // Helper function for simplifying an array of ports to a list of ranges FirewallOptions expects.
   public static List<String> simplifyPorts(int[] ports){
      if ((ports == null) || (ports.length == 0)) {
         return null;
      }
      ArrayList<String> output = new ArrayList<String>();
      Arrays.sort(ports);

      int range_start = ports[0];
      int range_end = ports[0];
      for (int i = 1; i < ports.length; i++) {
         if ((ports[i - 1] == ports[i] - 1) || (ports[i - 1] == ports[i])){
            // Range continues.
            range_end = ports[i];
         }
         else {
            // Range ends.
            output.add(formatRange(range_start, range_end));
            range_start = ports[i];
            range_end = ports[i];
         }
      }
      // Make sure we get the last range.
      output.add(formatRange(range_start, range_end));
      return output;
   }

   // Helper function for simplifyPorts. Formats port range strings.
   private static String formatRange(int start, int finish){
      if (start == finish){
         return Integer.toString(start);
      }
      else {
         return String.format("%s-%s", Integer.toString(start), Integer.toString(finish));
      }
   }

   // Helper function for getting the network name from the full URI.
   public static String nameFromNetworkString(String networkString) {
      return networkString.substring(networkString.lastIndexOf('/') + 1);
   }
}
