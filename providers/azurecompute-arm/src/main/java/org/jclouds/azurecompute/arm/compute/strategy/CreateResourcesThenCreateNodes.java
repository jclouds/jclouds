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
package org.jclouds.azurecompute.arm.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.DEFAULT_SUBNET_ADDRESS_PREFIX;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.DEFAULT_VNET_ADDRESS_SPACE_PREFIX;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractName;
import static org.jclouds.azurecompute.arm.domain.IdReference.extractResourceGroup;
import static org.jclouds.azurecompute.arm.domain.Subnet.extractVirtualNetwork;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Optional;
import org.jclouds.Constants;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.domain.ResourceGroupAndName;
import org.jclouds.azurecompute.arm.compute.domain.ResourceGroupAndNameAndIngressRules;
import org.jclouds.azurecompute.arm.compute.functions.TemplateToAvailabilitySet;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.compute.options.IpOptions;
import org.jclouds.azurecompute.arm.domain.AvailabilitySet;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.Subnet.SubnetProperties;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork.AddressSpace;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork.VirtualNetworkProperties;
import org.jclouds.azurecompute.arm.util.Passwords;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

@Singleton
public class CreateResourcesThenCreateNodes extends CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final AzureComputeApi api;
   private final LoadingCache<ResourceGroupAndNameAndIngressRules, String> securityGroupMap;
   private final String defaultVnetAddressPrefix;
   private final String defaultSubnetAddressPrefix;
   private final TemplateToAvailabilitySet templateToAvailabilitySet;

   @Inject
   protected CreateResourcesThenCreateNodes(
         CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
         ListNodesStrategy listNodesStrategy,
         GroupNamingConvention.Factory namingConvention,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
         AzureComputeApi api, @Named(DEFAULT_VNET_ADDRESS_SPACE_PREFIX) String defaultVnetAddressPrefix,
         @Named(DEFAULT_SUBNET_ADDRESS_PREFIX) String defaultSubnetAddressPrefix,
         LoadingCache<ResourceGroupAndNameAndIngressRules, String> securityGroupMap,
         TemplateToAvailabilitySet templateToAvailabilitySet) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
            customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.api = api;
      this.securityGroupMap = securityGroupMap;
      this.defaultVnetAddressPrefix = defaultVnetAddressPrefix;
      this.defaultSubnetAddressPrefix = defaultSubnetAddressPrefix;
      this.templateToAvailabilitySet = templateToAvailabilitySet;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
         Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
         Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      AzureTemplateOptions options = template.getOptions().as(AzureTemplateOptions.class);

      // TODO Generate a private key instead. Also no need to use AUTHENTICATE_SUDO in this case.
      generatePasswordIfNoneProvided(template);
      
      // If there is a script to be run on the node and public key
      // authentication has been configured, warn users if the private key
      // is not present
      if (hasRunScriptWithKeyAuthAndNoPrivateKey(template)) {
         logger.warn(">> a runScript was configured but no SSH key has been provided. "
               + "Authentication will delegate to the ssh-agent");
      }

      String location = template.getLocation().getId();

      createResourceGroupIfNeeded(group, location, options);
      
      normalizeNetworkOptions(options);
      createDefaultNetworkIfNeeded(group, location, options);
      
      configureSecurityGroupForOptions(group, template.getLocation(), options);
      configureAvailabilitySetForTemplate(template);

      return super.execute(group, count, template, goodNodes, badNodes, customizationResponses);
   }

   // Azure requires that we pass it the VM password. Need to generate one if not overridden by the user.
   private void generatePasswordIfNoneProvided(Template template) {
      TemplateOptions options = template.getOptions();
      if (options.getLoginPassword() == null) {
         Optional<String> passwordOptional = template.getImage().getDefaultCredentials().getOptionalPassword();
         options.overrideLoginPassword(passwordOptional.or(Passwords.generate()));
      }
   }

   protected synchronized void createDefaultNetworkIfNeeded(String group, String location, AzureTemplateOptions options) {
      if (options.getIpOptions().isEmpty()) {
         String name = namingConvention.create().sharedNameForGroup(group);
         
         Subnet subnet = Subnet.builder().name(name)
               .properties(SubnetProperties.builder().addressPrefix(defaultSubnetAddressPrefix).build()).build();
         
         VirtualNetworkProperties properties = VirtualNetworkProperties.builder()
               .addressSpace(AddressSpace.create(Arrays.asList(defaultVnetAddressPrefix)))
               .subnets(Arrays.asList(subnet)).build();
         
         logger.debug(">> network options have not been configured. Creating network %s(%s) and subnet %s(%s)", name,
               defaultVnetAddressPrefix, name, defaultSubnetAddressPrefix);
         
         api.getVirtualNetworkApi(options.getResourceGroup()).createOrUpdate(name, location, properties);
         Subnet createdSubnet = api.getSubnetApi(options.getResourceGroup(), name).get(name);
         
         options.ipOptions(IpOptions.builder().subnet(createdSubnet.id()).allocateNewPublicIp(true).build());
      }
   }

   private static boolean hasRunScriptWithKeyAuthAndNoPrivateKey(Template template) {
      return template.getOptions().getRunScript() != null && template.getOptions().getPublicKey() != null
            && !template.getOptions().hasLoginPrivateKeyOption();
   }

   private void configureSecurityGroupForOptions(String group, Location location, AzureTemplateOptions options) {

      checkArgument(options.getGroups().size() <= 1,
            "Only one security group can be configured for each network interface");

      if (!options.getGroups().isEmpty()) {
         ResourceGroupAndName securityGroupId = ResourceGroupAndName.fromSlashEncoded(getOnlyElement(options.getGroups()));
         NetworkSecurityGroup securityGroup = api.getNetworkSecurityGroupApi(securityGroupId.resourceGroup()).get(
               securityGroupId.name());
         checkArgument(securityGroup != null, "Security group %s was not found", securityGroupId.slashEncode());
         options.securityGroups(securityGroup.id());
      } else if (options.getInboundPorts().length > 0) {
         String name = namingConvention.create().sharedNameForGroup(group);
         ResourceGroupAndNameAndIngressRules regionAndIdAndIngressRules = ResourceGroupAndNameAndIngressRules.create(
               options.getResourceGroup(), location.getId(), name, options.getInboundPorts());
         // this will create if not yet exists.
         String securityGroupId = securityGroupMap.getUnchecked(regionAndIdAndIngressRules);
         options.securityGroups(securityGroupId);
      }
   }
   
   private void configureAvailabilitySetForTemplate(Template template) {
      AvailabilitySet availabilitySet = templateToAvailabilitySet.apply(template);
      if (availabilitySet != null) {
         logger.debug(">> configuring nodes in availability set [%s]", availabilitySet.name());
         template.getOptions().as(AzureTemplateOptions.class).availabilitySet(availabilitySet);
      }
   }
   
   private void createResourceGroupIfNeeded(String group, String location, AzureTemplateOptions options) {
      if (options.getResourceGroup() == null) {
         options.resourceGroup(group);
      }
      logger.debug(">> using resource group [%s]", options.getResourceGroup());
      ResourceGroup rg = api.getResourceGroupApi().get(options.getResourceGroup());
      if (rg == null) {
         logger.debug(">> resource group [%s] does not exist. Creating!", options.getResourceGroup());
         api.getResourceGroupApi().create(options.getResourceGroup(), location,
               ImmutableMap.of("description", "jclouds default resource group"));
      }
   }
   
   @VisibleForTesting
   void normalizeNetworkOptions(AzureTemplateOptions options) {
      if (!options.getNetworks().isEmpty() && !options.getIpOptions().isEmpty()) {
         throw new IllegalArgumentException("The options.networks and options.ipOptions are exclusive");
      }
      
      if (!options.getNetworks().isEmpty() && options.getIpOptions().isEmpty()) {
         // The portable interface allows to configure network IDs (subnet IDs),
         // but we don't know the type of the IP configurations to be applied
         // when attaching nodes to those networks. We'll assume private IPs
         // with Dynamic allocation and no public ip address associated.
         ImmutableList.Builder<IpOptions> ipOptions = ImmutableList.builder();
         for (String subnetId : options.getNetworks()) {
            ipOptions.add(IpOptions.builder().subnet(subnetId).build());
         }
         options.ipOptions(ipOptions.build());
      }
      
      if (!options.getIpOptions().isEmpty()) {
         // Eagerly validate that all configured subnets exist.
         for (IpOptions ipConfig : options.getIpOptions()) {
            if (ipConfig.allocateNewPublicIp() && ipConfig.publicIpId() != null) {
               throw new IllegalArgumentException("The allocateNewPublicIps and publicIpId are exclusive");
            }
            
            String resourceGroup = extractResourceGroup(ipConfig.subnet());
            String networkName = extractVirtualNetwork(ipConfig.subnet());
            String subnetName = extractName(ipConfig.subnet());
            
            Subnet subnet = api.getSubnetApi(resourceGroup, networkName).get(subnetName);
            checkState(subnet != null, "Configured subnet %s does not exist", ipConfig.subnet());
            
            if (ipConfig.publicIpId() != null) {
               PublicIPAddress publicIp = api.getPublicIPAddressApi(extractResourceGroup(ipConfig.publicIpId())).get(
                     extractName(ipConfig.publicIpId()));
               checkState(publicIp != null, "Configured public ip %s does not exist", ipConfig.publicIpId());               
            }
         }
      }
   }
}
