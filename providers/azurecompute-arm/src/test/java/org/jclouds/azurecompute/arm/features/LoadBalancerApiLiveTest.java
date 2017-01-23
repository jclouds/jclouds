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
package org.jclouds.azurecompute.arm.features;

import static org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions.Builder.availabilitySet;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.azurecompute.arm.domain.InboundNatRuleProperties.Protocol.Tcp;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.PublicIpAvailablePredicateFactory;
import org.jclouds.azurecompute.arm.domain.AvailabilitySet;
import org.jclouds.azurecompute.arm.domain.BackendAddressPool;
import org.jclouds.azurecompute.arm.domain.BackendAddressPoolProperties;
import org.jclouds.azurecompute.arm.domain.FrontendIPConfigurations;
import org.jclouds.azurecompute.arm.domain.FrontendIPConfigurationsProperties;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.InboundNatRule;
import org.jclouds.azurecompute.arm.domain.InboundNatRuleProperties;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.LoadBalancer;
import org.jclouds.azurecompute.arm.domain.LoadBalancerProperties;
import org.jclouds.azurecompute.arm.domain.LoadBalancingRule;
import org.jclouds.azurecompute.arm.domain.LoadBalancingRuleProperties;
import org.jclouds.azurecompute.arm.domain.LoadBalancingRuleProperties.Protocol;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.Probe;
import org.jclouds.azurecompute.arm.domain.ProbeProperties;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.domain.RegionAndId;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.AvailabilitySet.AvailabilitySetProperties;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.domain.Location;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

// We extend the BaseComputeServiceContextLiveTest to create nodes using the abstraction, which is much easier
@Test(groups = "live", singleThreaded = true)
public class LoadBalancerApiLiveTest extends BaseComputeServiceContextLiveTest {

   private static final String lbName = String.format("lb-%s-%s", LoadBalancerApiLiveTest.class.getSimpleName()
         .toLowerCase(), System.getProperty("user.name"));

   private LoadingCache<String, ResourceGroup> resourceGroupMap;
   private Predicate<URI> resourceDeleted;
   private PublicIpAvailablePredicateFactory publicIpAvailable;
   private Predicate<Supplier<Provisionable>> resourceAvailable;
   private AzureComputeApi api;

   private String resourceGroupName;
   private String location;
   private LoadBalancerApi lbApi;
   private NetworkInterfaceCardApi nicApi;

   private LoadBalancer lb;
   private String group;
   private List<String> nicNames;

   public LoadBalancerApiLiveTest() {
      provider = "azurecompute-arm";
      group = getClass().getSimpleName().toLowerCase();
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");
      return properties;
   }

   @Override
   protected void initializeContext() {
      super.initializeContext();
      resourceDeleted = context.utils().injector().getInstance(Key.get(new TypeLiteral<Predicate<URI>>() {
      }, Names.named(TIMEOUT_RESOURCE_DELETED)));
      resourceGroupMap = context.utils().injector()
            .getInstance(Key.get(new TypeLiteral<LoadingCache<String, ResourceGroup>>() {
            }));
      publicIpAvailable = context.utils().injector().getInstance(PublicIpAvailablePredicateFactory.class);
      resourceAvailable = context.utils().injector()
            .getInstance(Key.get(new TypeLiteral<Predicate<Supplier<Provisionable>>>() {
            }));
      api = view.unwrapApi(AzureComputeApi.class);
   }

   @Override
   @BeforeClass
   public void setupContext() {
      super.setupContext();
      // Use the resource name conventions used in the abstraction so the nodes
      // can see the load balancer
      ResourceGroup resourceGroup = createResourceGroup();
      resourceGroupName = resourceGroup.name();
      location = resourceGroup.location();
      lbApi = api.getLoadBalancerApi(resourceGroupName);
      nicApi = api.getNetworkInterfaceCardApi(resourceGroupName);
   }

   @Override
   @AfterClass(alwaysRun = true)
   protected void tearDownContext() {
      try {
         view.getComputeService().destroyNodesMatching(inGroup(group));
      } finally {
         try {
            URI uri = api.getResourceGroupApi().delete(resourceGroupName);
            assertResourceDeleted(uri);
         } finally {
            super.tearDownContext();
         }
      }
   }

   @Test
   public void testDeleteLoadBalancerDoesNotExist() {
      URI uri = lbApi.delete(lbName);
      assertNull(uri);
   }

   @Test(dependsOnMethods = "testDeleteLoadBalancerDoesNotExist")
   public void testCreateLoadBalancer() {
      LoadBalancer createLB = newLoadBalancer(lbName, location);

      PublicIPAddress publicIP = createPublicIPAddress("Ip4LoadBalancer");
      FrontendIPConfigurationsProperties fronendProps = FrontendIPConfigurationsProperties.builder()
            .publicIPAddress(IdReference.create(publicIP.id())).build();
      FrontendIPConfigurations frontendIps = FrontendIPConfigurations.create("ipConfigs", null, fronendProps, null);
      LoadBalancerProperties props = LoadBalancerProperties.builder()
            .frontendIPConfigurations(ImmutableList.of(frontendIps)).build();

      lb = lbApi.createOrUpdate(lbName, createLB.location(), createLB.tags(), props);
      assertNotNull(lb);
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testListLoadBalancers() {
      List<LoadBalancer> result = lbApi.list();

      // Verify we have something
      assertNotNull(result);
      assertTrue(result.size() > 0);

      // Check that the load balancer matches the one we originally passed in
      assertTrue(any(result, new Predicate<LoadBalancer>() {
         @Override
         public boolean apply(LoadBalancer input) {
            return lb.name().equals(input.name());
         }
      }));
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetLoadBalancer() {
      lb = lbApi.get(lbName);
      assertNotNull(lb);
   }

   @Test(dependsOnMethods = "testGetLoadBalancer")
   public void testAddProbe() {
      ProbeProperties probeProps = ProbeProperties.builder().protocol(ProbeProperties.Protocol.Http).port(80)
            .requestPath("/").intervalInSeconds(5).numberOfProbes(2).build();

      Probe probe = Probe.create("probetest", null, probeProps, null);
      LoadBalancerProperties props = lb.properties().toBuilder().probes(ImmutableList.of(probe)).build();

      lb = updateLoadBalancer(lbName, props);

      assertEquals(lb.properties().probes().size(), 1);
      assertEquals(lb.properties().probes().get(0).name(), probe.name());
   }

   @Test(dependsOnMethods = "testGetLoadBalancer")
   public void testAddBackendPool() throws Exception {
      List<IdReference> rules = newArrayList(transform(lb.properties().loadBalancingRules(), ToIdReference));
      BackendAddressPool pool = BackendAddressPool.create("backpools", null, BackendAddressPoolProperties.builder()
            .loadBalancingRules(rules).build(), null);

      LoadBalancerProperties props = lb.properties().toBuilder().backendAddressPools(ImmutableList.of(pool)).build();

      lb = updateLoadBalancer(lbName, props);

      assertEquals(lb.properties().backendAddressPools().size(), 1);
      assertEquals(lb.properties().backendAddressPools().get(0).name(), pool.name());
   }

   @Test(dependsOnMethods = { "testAddProbe", "testAddBackendPool" })
   public void testAddLoadBalancingRule() {
      IdReference frontendIp = IdReference.create(lb.properties().frontendIPConfigurations().get(0).id());
      IdReference probe = IdReference.create(lb.properties().probes().get(0).id());
      IdReference backendPool = IdReference.create(lb.properties().backendAddressPools().get(0).id());

      LoadBalancingRuleProperties ruleProperties = LoadBalancingRuleProperties.builder()
            .frontendIPConfiguration(frontendIp).backendAddressPool(backendPool).frontendPort(80).backendPort(80)
            .protocol(Protocol.Tcp).probe(probe).build();

      LoadBalancingRule rule = LoadBalancingRule.create("lbRule1", null, ruleProperties, null);
      LoadBalancerProperties props = lb.properties().toBuilder().loadBalancingRules(ImmutableList.of(rule)).build();

      lb = updateLoadBalancer(lbName, props);

      assertEquals(lb.properties().loadBalancingRules().size(), 1);
      assertEquals(lb.properties().loadBalancingRules().get(0).name(), rule.name());
   }

   @Test(dependsOnMethods = { "testAddBackendPool", "testAddProbe", "testAddLoadBalancingRule" })
   public void testAttachNodesToBackendPool() throws Exception {
      nicNames = createVirtualMachinesInGroupAndGetNicRefs(group, 2);

      // Add the first IP of each node to the pool
      List<NetworkInterfaceCard> attachedNics = new ArrayList<NetworkInterfaceCard>();
      BackendAddressPool targetPool = lb.properties().backendAddressPools().get(0);
      for (String nicName : nicNames) {
         attachedNics.add(attachNicToBackendPool(nicName, targetPool));
      }

      // Refresh the LB after having attached NICs to the pool
      lb = lbApi.get(lbName);
      List<BackendAddressPool> pools = lb.properties().backendAddressPools();
      assertEquals(pools.size(), 1);

      List<IdReference> backendIps = pools.get(0).properties().backendIPConfigurations();
      assertEquals(backendIps.size(), attachedNics.size());
      assertTrue(backendIps.containsAll(newArrayList(transform(attachedNics, ToFirstIpReference))));
   }

   @Test(dependsOnMethods = "testAttachNodesToBackendPool")
   public void testAddInboundNatRule() {
      IdReference frontendIp = IdReference.create(lb.properties().frontendIPConfigurations().get(0).id());

      InboundNatRuleProperties natProps = InboundNatRuleProperties.builder().frontendIPConfiguration(frontendIp)
            .frontendPort(5679).backendPort(56710).protocol(Tcp).build();

      InboundNatRule natRule = InboundNatRule.create("inboundnat", null, natProps, null);
      LoadBalancerProperties props = lb.properties().toBuilder().inboundNatRules(ImmutableList.of(natRule)).build();

      lb = updateLoadBalancer(lbName, props);

      assertEquals(lb.properties().inboundNatRules().size(), 1);
      assertEquals(lb.properties().inboundNatRules().get(0).name(), natRule.name());

      InboundNatRule createdRule = lb.properties().inboundNatRules().get(0);
      NetworkInterfaceCard updatedNic = attachNicToNatRule(nicNames.get(0), createdRule);
      List<IdReference> natRulesInNic = updatedNic.properties().ipConfigurations().get(0).properties()
            .loadBalancerInboundNatRules();

      assertEquals(natRulesInNic.size(), 1);
      assertEquals(natRulesInNic.get(0), IdReference.create(createdRule.id()));

      // Refresh the LB after having attached NICs to the pool
      lb = lbApi.get(lbName);

      IdReference backendIpRef = IdReference.create(updatedNic.properties().ipConfigurations().get(0).id());
      assertEquals(lb.properties().inboundNatRules().size(), 1);
      assertEquals(lb.properties().inboundNatRules().get(0).properties().backendIPConfiguration(), backendIpRef);

   }

   @Test(dependsOnMethods = { "testCreateLoadBalancer", "testListLoadBalancers", "testGetLoadBalancer", "testAddProbe",
         "testAddLoadBalancingRule", "testAddBackendPool", "testAttachNodesToBackendPool", "testAddInboundNatRule" }, alwaysRun = true)
   public void deleteLoadBalancer() {
      URI uri = lbApi.delete(lbName);
      assertResourceDeleted(uri);
   }

   private PublicIPAddress createPublicIPAddress(final String publicIpAddressName) {
      final PublicIPAddressApi ipApi = view.unwrapApi(AzureComputeApi.class).getPublicIPAddressApi(resourceGroupName);
      PublicIPAddress publicIPAddress = ipApi.get(publicIpAddressName);

      if (publicIPAddress == null) {
         final Map<String, String> tags = ImmutableMap.of("testkey", "testvalue");
         PublicIPAddressProperties properties = PublicIPAddressProperties.builder().publicIPAllocationMethod("Static")
               .idleTimeoutInMinutes(4).build();
         publicIPAddress = ipApi.createOrUpdate(publicIpAddressName, location, tags, properties);

         checkState(publicIpAvailable.create(resourceGroupName).apply(publicIpAddressName),
               "Public IP was not provisioned in the configured timeout");
      }

      return publicIPAddress;
   }

   private LoadBalancer newLoadBalancer(final String lbName, final String locationName) {
      FrontendIPConfigurationsProperties frontendIPConfigurationsProperties = FrontendIPConfigurationsProperties
            .builder().build();
      FrontendIPConfigurations frontendIPConfigurations = FrontendIPConfigurations.create("ipConfigs", null,
            frontendIPConfigurationsProperties, null);
      return LoadBalancer.create(lbName, locationName, null,
            LoadBalancerProperties.builder().frontendIPConfigurations(ImmutableList.of(frontendIPConfigurations))
                  .build(), null);
   }

   private void assertResourceDeleted(final URI uri) {
      if (uri != null) {
         assertTrue(resourceDeleted.apply(uri),
               String.format("Resource %s was not terminated in the configured timeout", uri));
      }
   }

   private List<String> createVirtualMachinesInGroupAndGetNicRefs(final String group, final int count)
         throws RunNodesException {

      // To add multiple nodes in a LB they must belong to the same availability
      // set
      AvailabilitySetProperties props = AvailabilitySetProperties.builder().platformUpdateDomainCount(count)
            .platformFaultDomainCount(count).build();
      AvailabilitySet as = AvailabilitySet.builder().name(group).properties(props).build();

      Set<? extends NodeMetadata> nodes = view.getComputeService()
            .createNodesInGroup(group, count, availabilitySet(as));

      List<String> nicNames = new ArrayList<String>();
      for (NodeMetadata node : nodes) {
         RegionAndId regionAndId = RegionAndId.fromSlashEncoded(node.getId());
         VirtualMachine vm = api.getVirtualMachineApi(resourceGroupName).get(regionAndId.id());

         String nicName = getLast(Splitter.on("/").split(
               vm.properties().networkProfile().networkInterfaces().get(0).id()));
         nicNames.add(nicName);
      }

      return nicNames;
   }

   private NetworkInterfaceCard attachNicToBackendPool(final String nicName, BackendAddressPool pool) {
      List<IdReference> poolRefs = ImmutableList.of(IdReference.create(pool.id()));

      // Assume we are attaching the first IP to the Load Balancer
      NetworkInterfaceCard nic = nicApi.get(nicName);

      IpConfigurationProperties ipProps = nic.properties().ipConfigurations().get(0).properties().toBuilder()
            .loadBalancerBackendAddressPools(poolRefs).build();
      List<IpConfiguration> ips = ImmutableList.of(nic.properties().ipConfigurations().get(0).toBuilder()
            .properties(ipProps).build());

      NetworkInterfaceCardProperties nicProps = nic.properties().toBuilder().ipConfigurations(ips).build();

      nicApi.createOrUpdate(nicName, location, nicProps, null);

      resourceAvailable.apply(new Supplier<Provisionable>() {
         @Override
         public Provisionable get() {
            NetworkInterfaceCard updated = nicApi.get(nicName);
            return updated == null ? null : updated.properties();
         }
      });

      return nicApi.get(nicName);
   }

   private NetworkInterfaceCard attachNicToNatRule(final String nicName, InboundNatRule rule) {
      List<IdReference> natRuleRefs = ImmutableList.of(IdReference.create(rule.id()));

      // Assume we are attaching the first IP to the NAT rule
      NetworkInterfaceCard nic = nicApi.get(nicName);

      IpConfigurationProperties ipProps = nic.properties().ipConfigurations().get(0).properties().toBuilder()
            .loadBalancerInboundNatRules(natRuleRefs).build();
      List<IpConfiguration> ips = ImmutableList.of(nic.properties().ipConfigurations().get(0).toBuilder()
            .properties(ipProps).build());

      NetworkInterfaceCardProperties nicProps = nic.properties().toBuilder().ipConfigurations(ips).build();

      nicApi.createOrUpdate(nicName, location, nicProps, null);

      resourceAvailable.apply(new Supplier<Provisionable>() {
         @Override
         public Provisionable get() {
            NetworkInterfaceCard updated = nicApi.get(nicName);
            return updated == null ? null : updated.properties();
         }
      });

      return nicApi.get(nicName);
   }

   private ResourceGroup createResourceGroup() {
      Location location = view.getComputeService().templateBuilder().build().getLocation();
      return resourceGroupMap.getUnchecked(location.getId());
   }

   private LoadBalancer updateLoadBalancer(final String name, LoadBalancerProperties props) {
      lbApi.createOrUpdate(name, location, null, props);
      resourceAvailable.apply(new Supplier<Provisionable>() {
         @Override
         public Provisionable get() {
            LoadBalancer updated = lbApi.get(name);
            return updated == null ? null : updated.properties();
         }
      });
      return lbApi.get(name);
   }

   private static final Function<LoadBalancingRule, IdReference> ToIdReference = new Function<LoadBalancingRule, IdReference>() {
      @Override
      public IdReference apply(LoadBalancingRule input) {
         return IdReference.create(input.id());
      }
   };

   private static final Function<NetworkInterfaceCard, IdReference> ToFirstIpReference = new Function<NetworkInterfaceCard, IdReference>() {
      @Override
      public IdReference apply(NetworkInterfaceCard input) {
         return IdReference.create(input.properties().ipConfigurations().get(0).id());
      }
   };

}
