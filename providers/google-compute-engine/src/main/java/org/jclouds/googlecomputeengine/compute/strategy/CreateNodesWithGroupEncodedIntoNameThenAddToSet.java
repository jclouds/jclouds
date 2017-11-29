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
import static org.jclouds.domain.LocationScope.ZONE;
import static org.jclouds.googlecomputeengine.compute.domain.internal.RegionAndName.fromRegionAndName;

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
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.domain.internal.RegionAndName;
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.functions.Resources;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Subnetwork;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshKeyPairGenerator;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

public final class CreateNodesWithGroupEncodedIntoNameThenAddToSet extends
      org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   public static final String EXTERIOR_RANGE = "0.0.0.0/0";
   public static final String DEFAULT_INTERNAL_NETWORK_RANGE = "10.0.0.0/8";

   public static final String DEFAULT_NETWORK_NAME = "default";

   private final GoogleComputeEngineApi api;
   private final Resources resources;
   private final Predicate<AtomicReference<Operation>> operationDone;
   private final FirewallTagNamingConvention.Factory firewallTagNamingConvention;
   private final SshKeyPairGenerator keyGenerator;
   private final LoadingCache<RegionAndName, Optional<Subnetwork>> subnetworksMap;

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
         GoogleComputeEngineApi api, Resources resources, Predicate<AtomicReference<Operation>> operationDone,
         FirewallTagNamingConvention.Factory firewallTagNamingConvention, SshKeyPairGenerator keyGenerator,
         LoadingCache<RegionAndName, Optional<Subnetwork>> subnetworksMap) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
            customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = api;
      this.resources = resources;
      this.operationDone = operationDone;
      this.firewallTagNamingConvention = firewallTagNamingConvention;
      this.keyGenerator = keyGenerator;
      this.subnetworksMap = subnetworksMap;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
         Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
         Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      GoogleComputeEngineTemplateOptions templateOptions = GoogleComputeEngineTemplateOptions.class
            .cast(template.getOptions());
      assert template.getOptions().equals(templateOptions) : "options didn't clone properly";

      // Configure networking
      configureNetworking(group, templateOptions, template.getLocation());
      
      templateOptions.userMetadata(ComputeServiceConstants.NODE_GROUP_KEY, group);

      // Configure the default credentials, if needed
      if (templateOptions.autoCreateKeyPair() && Strings.isNullOrEmpty(templateOptions.getPublicKey())) {
         logger.debug(">> creating default keypair...");
         Map<String, String> defaultKeys = keyGenerator.get();
         templateOptions.authorizePublicKey(defaultKeys.get("public"));
         templateOptions.overrideLoginPrivateKey(defaultKeys.get("private"));
      }

      if (templateOptions.getRunScript() != null && templateOptions.getLoginPrivateKey() == null) {
         logger.warn(">> a runScript has been configured but no SSH key has been provided."
               + " Authentication will delegate to the ssh-agent");
      }

      return super.execute(group, count, template, goodNodes, badNodes, customizationResponses);
   }

   /**
    * Configure the networks taking into account that users may have configured
    * a custom subnet or a legacy network.
    */
   private void configureNetworking(String group, GoogleComputeEngineTemplateOptions options, Location location) {
      String networkName = null;
      Network network = null;

      if (options.getNetworks().isEmpty()) {
         networkName = DEFAULT_NETWORK_NAME;
      } else {
         Iterator<String> iterator = options.getNetworks().iterator();
         networkName = nameFromNetworkString(iterator.next());
         checkArgument(!iterator.hasNext(),
               "Error: Please specify only one network/subnetwork in TemplateOptions when using GCE.");
      }

      String region = ZONE == location.getScope() ? location.getParent().getId() : location.getId();
      Optional<Subnetwork> subnet = subnetworksMap.getUnchecked(fromRegionAndName(region, networkName));
      if (subnet.isPresent()) {
         network = resources.network(subnet.get().network());
         options.networks(ImmutableSet.of(subnet.get().selfLink().toString()));
         logger.debug(">> attaching nodes to subnet(%s) in region(%s)", subnet.get().name(), region);
      } else {
         logger.warn(">> subnet(%s) was not found in region(%s). Trying to find a matching legacy network...",
               networkName, region);
         network = api.networks().get(networkName);
         options.networks(ImmutableSet.of(network.selfLink().toString()));
         logger.debug(">> attaching nodes to legacy network(%s)", network.name());
      }

      checkArgument(network != null, "Error: no network with name %s was found", networkName);

      // Setup Firewall rules
      getOrCreateFirewalls(options, network, subnet, firewallTagNamingConvention.get(group));
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
         Optional<Subnetwork> subnet, FirewallTagNamingConvention naming) {

      Set<String> tags = Sets.newLinkedHashSet(templateOptions.getTags());

      FirewallApi firewallApi = api.firewalls();

      if (!templateOptions.getGroups().isEmpty()) {
         for (String firewallName : templateOptions.getGroups()) {
            Firewall firewall = firewallApi.get(firewallName);
            validateFirewall(firewall, network);
            if (!firewall.targetTags().isEmpty()) {
               // Add tags coming from firewalls
               tags.addAll(firewall.targetTags());
            }
         }
      }

      int[] inboundPorts = templateOptions.getInboundPorts();
      
      if (inboundPorts != null && inboundPorts.length > 0) {
         List<String> ports = simplifyPorts(inboundPorts);
         String name = naming.name(ports);
         Firewall firewall = firewallApi.get(name);
         AtomicReference<Operation> operation = null;

         String interiorRange = subnet.isPresent() ? subnet.get().ipCidrRange() : DEFAULT_INTERNAL_NETWORK_RANGE;

         if (firewall == null) {
            List<Rule> rules = ImmutableList.of(Rule.create("tcp", ports), Rule.create("udp", ports));
            FirewallOptions firewallOptions = new FirewallOptions().name(name).network(network.selfLink())
                  .allowedRules(rules).sourceTags(templateOptions.getTags())
                  .sourceRanges(of(interiorRange, EXTERIOR_RANGE)).targetTags(ImmutableList.of(name));

            operation = Atomics.newReference(firewallApi.createInNetwork(firewallOptions.name(), network.selfLink(),
                  firewallOptions));

            operationDone.apply(operation);
            checkState(operation.get().httpErrorStatusCode() == null, "Could not insert firewall, operation failed %s",
                  operation);
         }

         tags.add(name);  // Add tags for the inbound ports firewall
      }

      templateOptions.tags(tags);
   }

   private void validateFirewall(Firewall firewall, Network network) {
      if (firewall == null || !firewall.network().equals(network.selfLink())) {
         throw new IllegalArgumentException(String.format("Can't find firewall %s in network %s.", firewall.name(), network));
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
