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
package org.jclouds.cloudstack.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.cloudstack.options.DeployVirtualMachineOptions.Builder.displayName;
import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.id;
import static org.jclouds.cloudstack.predicates.TemplatePredicates.isReady;
import static org.jclouds.cloudstack.predicates.ZonePredicates.supportsSecurityGroups;
import static org.jclouds.ssh.SshKeys.fingerprintPrivateKey;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Capabilities;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Project;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.domain.Tag;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.domain.ZoneAndName;
import org.jclouds.cloudstack.domain.ZoneSecurityGroupNamePortsCidrs;
import org.jclouds.cloudstack.features.TemplateApi;
import org.jclouds.cloudstack.functions.CreateFirewallRulesForIP;
import org.jclouds.cloudstack.functions.CreatePortForwardingRulesForIP;
import org.jclouds.cloudstack.functions.StaticNATVirtualMachineInNetwork;
import org.jclouds.cloudstack.functions.StaticNATVirtualMachineInNetwork.Factory;
import org.jclouds.cloudstack.options.CreateTagsOptions;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.cloudstack.options.ListFirewallRulesOptions;
import org.jclouds.cloudstack.options.ListTemplatesOptions;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

/**
 * defines the connection between the {@link CloudStackApi} implementation
 * and the jclouds {@link ComputeService}
 */
@Singleton
public class CloudStackComputeServiceAdapter implements
   ComputeServiceAdapter<VirtualMachine, ServiceOffering, Template, Zone> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudStackApi client;
   private final Predicate<String> jobComplete;
   private final Supplier<Map<String, Network>> networkSupplier;
   private final Supplier<Map<String, Project>> projectSupplier;
   private final BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult;
   private final Factory staticNATVMInNetwork;
   private final CreatePortForwardingRulesForIP setupPortForwardingRulesForIP;
   private final CreateFirewallRulesForIP setupFirewallRulesForIP;
   private final LoadingCache<String, Set<IPForwardingRule>> vmToRules;
   private final Map<String, Credentials> credentialStore;
   private final Map<NetworkType, ? extends OptionsConverter> optionsConverters;
   private final Supplier<LoadingCache<String, Zone>> zoneIdToZone;
   private final LoadingCache<ZoneAndName, SecurityGroup> securityGroupCache;
   private final LoadingCache<String, SshKeyPair> keyPairCache;
   private final GroupNamingConvention.Factory namingConvention;
   private final GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull credentialsProvider;

   @Inject
   public CloudStackComputeServiceAdapter(CloudStackApi client, Predicate<String> jobComplete,
                                          @Memoized Supplier<Map<String, Network>> networkSupplier,
                                          @Memoized Supplier<Map<String, Project>> projectSupplier,
                                          BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult,
                                          StaticNATVirtualMachineInNetwork.Factory staticNATVMInNetwork,
                                          CreatePortForwardingRulesForIP setupPortForwardingRulesForIP,
                                          CreateFirewallRulesForIP setupFirewallRulesForIP,
                                          LoadingCache<String, Set<IPForwardingRule>> vmToRules,
                                          Map<String, Credentials> credentialStore,
                                          Map<NetworkType, ? extends OptionsConverter> optionsConverters,
                                          Supplier<LoadingCache<String, Zone>> zoneIdToZone,
                                          LoadingCache<ZoneAndName, SecurityGroup> securityGroupCache,
                                          LoadingCache<String, SshKeyPair> keyPairCache,
                                          GroupNamingConvention.Factory namingConvention,
                                          GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull credentialsProvider) {
      this.client = checkNotNull(client, "client");
      this.jobComplete = checkNotNull(jobComplete, "jobComplete");
      this.networkSupplier = checkNotNull(networkSupplier, "networkSupplier");
      this.projectSupplier = checkNotNull(projectSupplier, "projectSupplier");
      this.blockUntilJobCompletesAndReturnResult = checkNotNull(blockUntilJobCompletesAndReturnResult,
         "blockUntilJobCompletesAndReturnResult");
      this.staticNATVMInNetwork = checkNotNull(staticNATVMInNetwork, "staticNATVMInNetwork");
      this.setupPortForwardingRulesForIP = checkNotNull(setupPortForwardingRulesForIP, "setupPortForwardingRulesForIP");
      this.setupFirewallRulesForIP = checkNotNull(setupFirewallRulesForIP, "setupFirewallRulesForIP");
      this.vmToRules = checkNotNull(vmToRules, "vmToRules");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.securityGroupCache = checkNotNull(securityGroupCache, "securityGroupCache");
      this.keyPairCache = checkNotNull(keyPairCache, "keyPairCache");
      this.optionsConverters = optionsConverters;
      this.zoneIdToZone = zoneIdToZone;
      this.namingConvention = namingConvention;
      this.credentialsProvider = credentialsProvider;
   }

   @Override
   public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(String group, String name,
                                                                                       org.jclouds.compute.domain.Template template) {

      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");
      checkArgument(template.getOptions().getClass().isAssignableFrom(CloudStackTemplateOptions.class),
         "options class %s should have been assignable from CloudStackTemplateOptions", template.getOptions()
         .getClass());
      Map<String, Network> networks = networkSupplier.get();

      final String zoneId = template.getLocation().getId();
      Zone zone = zoneIdToZone.get().getUnchecked(zoneId);

      CloudStackTemplateOptions templateOptions = template.getOptions().as(CloudStackTemplateOptions.class);

      checkState(optionsConverters.containsKey(zone.getNetworkType()), "no options converter configured for network type %s", zone.getNetworkType());
      DeployVirtualMachineOptions options = displayName(name).name(name);
      if (templateOptions.getAccount() != null) {
          options.accountInDomain(templateOptions.getAccount(), templateOptions.getDomainId());
      } else if (templateOptions.getDomainId() != null) {
          options.domainId(templateOptions.getDomainId());
      }

      OptionsConverter optionsConverter = optionsConverters.get(zone.getNetworkType());
      options = optionsConverter.apply(templateOptions, networks, zoneId, options);

      options.group(group);

      if (templateOptions.getIpOnDefaultNetwork() != null) {
         options.ipOnDefaultNetwork(templateOptions.getIpOnDefaultNetwork());
      }

      if (!templateOptions.getIpsToNetworks().isEmpty()) {
         options.ipsToNetworks(templateOptions.getIpsToNetworks());
      }

      if (templateOptions.getKeyPair() != null) {
         SshKeyPair keyPair = null;
         if (templateOptions.getLoginPrivateKey() != null) {
            String pem = templateOptions.getLoginPrivateKey();
            keyPair = SshKeyPair.builder().name(templateOptions.getKeyPair())
               .fingerprint(fingerprintPrivateKey(pem)).privateKey(pem).build();
            keyPairCache.asMap().put(keyPair.getName(), keyPair);
            options.keyPair(keyPair.getName());
         } else if (client.getSSHKeyPairApi().getSSHKeyPair(templateOptions.getKeyPair()) != null) {
            keyPair = client.getSSHKeyPairApi().getSSHKeyPair(templateOptions.getKeyPair());
         }
         if (keyPair != null) {
            keyPairCache.asMap().put(keyPair.getName(), keyPair);
            options.keyPair(keyPair.getName());
         }
      } else if (templateOptions.shouldGenerateKeyPair()) {
         SshKeyPair keyPair = keyPairCache.getUnchecked(namingConvention.create()
                                                        .sharedNameForGroup(group));
         keyPairCache.asMap().put(keyPair.getName(), keyPair);
         templateOptions.keyPair(keyPair.getName());
         options.keyPair(keyPair.getName());
      }

      if (templateOptions.getDiskOfferingId() != null) {
         options.diskOfferingId(templateOptions.getDiskOfferingId());
         if (templateOptions.getDataDiskSize() > 0) {
            options.dataDiskSize(templateOptions.getDataDiskSize());
         }
      }

      if (supportsSecurityGroups().apply(zone)) {
         List<Integer> inboundPorts = Ints.asList(templateOptions.getInboundPorts());

         if (templateOptions.getSecurityGroupIds().isEmpty()
             && !inboundPorts.isEmpty()
             && templateOptions.shouldGenerateSecurityGroup()) {
            String securityGroupName = namingConvention.create().sharedNameForGroup(group);
            SecurityGroup sg = securityGroupCache.getUnchecked(ZoneSecurityGroupNamePortsCidrs.builder()
                                                               .zone(zone.getId())
                                                               .name(securityGroupName)
                                                               .ports(ImmutableSet.copyOf(inboundPorts))
                                                               .cidrs(ImmutableSet.<String> of()).build());
            options.securityGroupId(sg.getId());
         }
      }

      String templateId = template.getImage().getId();
      String serviceOfferingId = template.getHardware().getId();

      logger.debug("serviceOfferingId %s, templateId %s, zoneId %s, options %s%n", serviceOfferingId, templateId,
         zoneId, options);
      AsyncCreateResponse job = client.getVirtualMachineApi().deployVirtualMachineInZone(zoneId, serviceOfferingId,
         templateId, options);
      VirtualMachine vm = blockUntilJobCompletesAndReturnResult.<VirtualMachine>apply(job);
      logger.debug("--- virtualmachine: %s", vm);
      LoginCredentials credentials = credentialsProvider.get();
      if (credentials == null || credentials.getUser() == null) {
         LoginCredentials.Builder credentialsBuilder = LoginCredentials.builder();
         if (templateOptions.getKeyPair() != null) {
            SshKeyPair keyPair = keyPairCache.getUnchecked(templateOptions.getKeyPair());
            credentialsBuilder.privateKey(keyPair.getPrivateKey());
         } else if (vm.isPasswordEnabled()) {
            assert vm.getPassword() != null : vm;
            credentialsBuilder.password(vm.getPassword());
         }
         credentials = credentialsBuilder.build();
      }

      try {
         ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
         builder.putAll(template.getOptions().getUserMetadata());
         for (String tag : template.getOptions().getTags())
            builder.put(tag, "jclouds-empty-tag-placeholder");
         Map<String, String> common = builder.build();

         if (!common.isEmpty()) {
            logger.debug(">> adding tags %s to virtualmachine(%s)", common, vm.getId());
            CreateTagsOptions tagOptions = CreateTagsOptions.Builder.resourceIds(vm.getId())
                  .resourceType(Tag.ResourceType.USER_VM)
                  .tags(common);
            AsyncCreateResponse tagJob = client.getTagApi().createTags(tagOptions);
            awaitCompletion(tagJob.getJobId());
            logger.debug("<< tags added");
            vm = client.getVirtualMachineApi().getVirtualMachine(vm.getId());
         }
         if (templateOptions.shouldSetupStaticNat()) {
             Capabilities capabilities = client.getConfigurationApi().listCapabilities();
             // TODO: possibly not all network ids, do we want to do this
             for (String networkId : options.getNetworkIds()) {
                logger.debug(">> creating static NAT for virtualMachine(%s) in network(%s)", vm.getId(), networkId);
                PublicIPAddress ip = staticNATVMInNetwork.create(networks.get(networkId)).apply(vm);
                logger.trace("<< static NATed IPAddress(%s) to virtualMachine(%s)", ip.getId(), vm.getId());
                vm = client.getVirtualMachineApi().getVirtualMachine(vm.getId());
                List<Integer> ports = Ints.asList(templateOptions.getInboundPorts());
                if (capabilities.getCloudStackVersion().startsWith("2")) {
                   logger.debug(">> setting up IP forwarding for IPAddress(%s) rules(%s)", ip.getId(), ports);
                   Set<IPForwardingRule> rules = setupPortForwardingRulesForIP.apply(ip, ports);
                   logger.trace("<< setup %d IP forwarding rules on IPAddress(%s)", rules.size(), ip.getId());
                } else {
                   logger.debug(">> setting up firewall rules for IPAddress(%s) rules(%s)", ip.getId(), ports);
                   Set<FirewallRule> rules = setupFirewallRulesForIP.apply(ip, ports);
                   logger.trace("<< setup %d firewall rules on IPAddress(%s)", rules.size(), ip.getId());
                }
             }
          }
      } catch (RuntimeException re) {
          logger.error("-- exception after node has been created, trying to destroy the created virtualMachine(%s)", vm.getId());
          try {
              destroyNode(vm.getId());
          } catch (RuntimeException re2) {
              logger.debug("-- exception in exceptionHandler while executing destroyNode for virtualMachine(%s), ignoring and rethrowing original exception", vm.getId());
          }
          throw re;
      }
      return new NodeAndInitialCredentials<VirtualMachine>(vm, vm.getId() + "", credentials);
   }

   @Override
   public Iterable<ServiceOffering> listHardwareProfiles() {
      // TODO: we may need to filter these
      return client.getOfferingApi().listServiceOfferings();
   }

   @Override
   public Iterable<Template> listImages() {
      TemplateApi templateApi = client.getTemplateApi();
      ImmutableSet.Builder<Template> templates = ImmutableSet.builder();
      templates.addAll(templateApi.listTemplates());
      for (String project : projectSupplier.get().keySet()) {
         templates.addAll(templateApi.listTemplates(ListTemplatesOptions.Builder.projectId(project)));
      }

      return filter(templates.build(), isReady());
   }

   @Override
   public Template getImage(String id) {
      return get(client.getTemplateApi().listTemplates(id(id)), 0, null);
   }

   @Override
   public Iterable<VirtualMachine> listNodes() {
      return client.getVirtualMachineApi().listVirtualMachines();
   }

   @Override
   public Iterable<VirtualMachine> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<VirtualMachine>() {

            @Override
            public boolean apply(VirtualMachine vm) {
               return contains(ids, vm.getId());
            }
         });
   }

   @Override
   public Iterable<Zone> listLocations() {
      // TODO: we may need to filter these
      return client.getZoneApi().listZones();
   }

   @Override
   public VirtualMachine getNode(String id) {
      String virtualMachineId = id;
      return client.getVirtualMachineApi().getVirtualMachine(virtualMachineId);
   }

   @Override
   public void destroyNode(String id) {
      String virtualMachineId = id;
      // There was a bug in 2.2.12 release happening when static nat IP address
      // was being released, and corresponding firewall rules were left behind.
      // So next time the same IP is allocated, it might be not be static nat
      // enabled, but there are still rules associated with it. And when you try
      // to release this IP, the release will fail.
      //
      // The bug was fixed in 2.2.13 release only, and the current system wasn't
      // updated yet.
      //
      // To avoid the issue, every time you release a static nat ip address, do
      // the following:

      // 1) Delete IP forwarding rules associated with IP.
      Set<String> ipAddresses = deleteIPForwardingRulesForVMAndReturnDistinctIPs(virtualMachineId);

      // 2) Delete firewall rules associated with IP.
      ipAddresses.addAll(deleteFirewallRulesForVMAndReturnDistinctIPs(virtualMachineId));

      // 3) Disable static nat rule for the IP.
      disableStaticNATOnIPAddresses(ipAddresses);

      // 4) Only after 1 and 2 release the IP address.
      disassociateIPAddresses(ipAddresses);

      destroyVirtualMachine(virtualMachineId);

      vmToRules.invalidate(virtualMachineId);
   }

   public void disassociateIPAddresses(Set<String> ipAddresses) {
      for (String ipAddress : ipAddresses) {
         logger.debug(">> disassociating IPAddress(%s)", ipAddress);
         client.getAddressApi().disassociateIPAddress(ipAddress);
      }
   }

   public void destroyVirtualMachine(String virtualMachineId) {

      String destroyVirtualMachine = client.getVirtualMachineApi().destroyVirtualMachine(virtualMachineId);
      if (destroyVirtualMachine != null) {
         logger.debug(">> destroying virtualMachine(%s) job(%s)", virtualMachineId, destroyVirtualMachine);
         awaitCompletion(destroyVirtualMachine);
      } else {
         logger.trace("<< virtualMachine(%s) not found", virtualMachineId);
      }

   }

   public void disableStaticNATOnIPAddresses(Set<String> ipAddresses) {
      Builder<String> jobsToTrack = ImmutableSet.builder();
      for (String ipAddress : ipAddresses) {
         String disableStaticNAT = client.getNATApi().disableStaticNATOnPublicIP(ipAddress);
         if (disableStaticNAT != null) {
            logger.debug(">> disabling static NAT IPAddress(%s) job(%s)", ipAddress, disableStaticNAT);
            jobsToTrack.add(disableStaticNAT);
         }
      }
      awaitCompletion(jobsToTrack.build());
   }

   public Set<String> deleteIPForwardingRulesForVMAndReturnDistinctIPs(String virtualMachineId) {
      Builder<String> jobsToTrack = ImmutableSet.builder();

      // immutable doesn't permit duplicates
      Set<String> ipAddresses = Sets.newLinkedHashSet();

      Set<IPForwardingRule> forwardingRules = client.getNATApi().getIPForwardingRulesForVirtualMachine(
         virtualMachineId);
      for (IPForwardingRule rule : forwardingRules) {
         if (!"Deleting".equals(rule.getState())) {
            ipAddresses.add(rule.getIPAddressId());
            String deleteForwardingRule = client.getNATApi().deleteIPForwardingRule(rule.getId());
            if (deleteForwardingRule != null) {
               logger.debug(">> deleting IPForwardingRule(%s) job(%s)", rule.getId(), deleteForwardingRule);
               jobsToTrack.add(deleteForwardingRule);
            }
         }
      }
      awaitCompletion(jobsToTrack.build());
      return ipAddresses;
   }

   public Set<String> deleteFirewallRulesForVMAndReturnDistinctIPs(String virtualMachineId) {
      // immutable doesn't permit duplicates
      Set<String> ipAddresses = Sets.newLinkedHashSet();

      String publicIpId = client.getVirtualMachineApi().getVirtualMachine(virtualMachineId).getPublicIPId();
      if (publicIpId != null) {
         Set<FirewallRule> firewallRules = client.getFirewallApi()
            .listFirewallRules(ListFirewallRulesOptions.Builder.ipAddressId(client.getVirtualMachineApi().getVirtualMachine(virtualMachineId).getPublicIPId()));

         for (FirewallRule rule : firewallRules) {
            if (rule.getState() != FirewallRule.State.DELETING) {
               ipAddresses.add(rule.getIpAddressId());
               client.getFirewallApi().deleteFirewallRule(rule.getId());
               logger.debug(">> deleting FirewallRule(%s)", rule.getId());
            }
         }
      }
      return ipAddresses;
   }

   public void awaitCompletion(Iterable<String> jobs) {
      logger.debug(">> awaiting completion of jobs(%s)", jobs);
      for (String job : jobs)
         awaitCompletion(job);
      logger.trace("<< completed jobs(%s)", jobs);
   }

   public void awaitCompletion(String job) {
      boolean completed = jobComplete.apply(job);
      logger.trace("<< job(%s) complete(%s)", job, completed);
   }

   @Override
   public void rebootNode(String id) {
      String virtualMachineId = id;
      String job = client.getVirtualMachineApi().rebootVirtualMachine(virtualMachineId);
      if (job != null) {
         logger.debug(">> rebooting virtualMachine(%s) job(%s)", virtualMachineId, job);
         awaitCompletion(job);
      }
   }

   @Override
   public void resumeNode(String id) {
      String virtualMachineId = id;
      String job = client.getVirtualMachineApi().startVirtualMachine(id);
      if (job != null) {
         logger.debug(">> starting virtualMachine(%s) job(%s)", virtualMachineId, job);
         awaitCompletion(job);
      }
   }

   @Override
   public void suspendNode(String id) {
      String virtualMachineId = id;
      String job = client.getVirtualMachineApi().stopVirtualMachine(id);
      if (job != null) {
         logger.debug(">> stopping virtualMachine(%s) job(%s)", virtualMachineId, job);
         awaitCompletion(job);
      }
   }

}
