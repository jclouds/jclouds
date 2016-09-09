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
package org.jclouds.azurecompute.arm.internal;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroupProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class BaseAzureComputeApiLiveTest extends BaseApiLiveTest<AzureComputeApi> {

   protected static final int RAND = new Random().nextInt(999);
   public static final String LOCATION = "westeurope";
   public static final String LOCATIONDESCRIPTION = "West Europe";
   public static final String DEFAULT_VIRTUALNETWORK_ADDRESS_PREFIX = "10.2.0.0/16";
   
   protected Predicate<URI> imageAvailablePredicate;
   protected Predicate<URI> resourceDeleted;
   
   public BaseAzureComputeApiLiveTest() {
      provider = "azurecompute-arm";
   }

   @Override protected AzureComputeApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      imageAvailablePredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<URI>>() {
      }, Names.named(TIMEOUT_IMAGE_AVAILABLE)));
      resourceDeleted = injector.getInstance(Key.get(new TypeLiteral<Predicate<URI>>() {
      }, Names.named(TIMEOUT_RESOURCE_DELETED)));
      return injector.getInstance(AzureComputeApi.class);
   }

   @Override protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      // for oauth
      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");
      return properties;
   }
   
   protected void assertResourceDeleted(URI uri) {
      assertNotNull(uri);
      assertTrue(resourceDeleted.apply(uri), String.format("Resource %s was not terminated in the configured timeout", uri));
   }

   protected VirtualNetwork createDefaultVirtualNetwork(final String resourceGroupName, String virtualNetworkName, String virtualnetworkAddressPrefix, String location) {
      final VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties =
              VirtualNetwork.VirtualNetworkProperties.create(null, null,
                      VirtualNetwork.AddressSpace.create(Arrays.asList(virtualnetworkAddressPrefix)), null);
      VirtualNetwork virtualNetwork = api.getVirtualNetworkApi(resourceGroupName).createOrUpdate(virtualNetworkName, location, virtualNetworkProperties);
      retry(new Predicate<String>() {
         @Override
         public boolean apply(String name) {
            VirtualNetwork virtualNetwork = api.getVirtualNetworkApi(resourceGroupName).get(name);
            return virtualNetwork.properties().provisioningState().equals("Succeeded");
         }
      }, 60 * 4 * 1000).apply(virtualNetwork.name());
      return virtualNetwork;
   }

   protected Subnet createDefaultSubnet(final String resourceGroupName, String subnetName, final String virtualNetworkName, String subnetAddressSpace) {
      Subnet.SubnetProperties  properties = Subnet.SubnetProperties.builder().addressPrefix(subnetAddressSpace).build();
      Subnet subnet = api.getSubnetApi(resourceGroupName, virtualNetworkName).createOrUpdate(subnetName, properties);
      retry(new Predicate<String>() {
         @Override
         public boolean apply(String name) {
            Subnet subnet = api.getSubnetApi(resourceGroupName, virtualNetworkName).get(name);
            return subnet.properties().provisioningState().equals("Succeeded");
         }
      }, 60 * 4 * 1000).apply(subnet.name());
      return subnet;
   }

   protected StorageService createStorageService(String resourceGroupName, String storageServiceName, String location) {
      URI uri = api.getStorageAccountApi(resourceGroupName).create(storageServiceName, location, ImmutableMap.of("property_name",
              "property_value"), ImmutableMap.of("accountType", StorageService.AccountType.Standard_LRS.toString()));
      if (uri != null) {
         assertTrue(uri.toString().contains("api-version"));

         boolean jobDone = retry(new Predicate<URI>() {
            @Override
            public boolean apply(URI uri) {
               return ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri);
            }
         }, 60 * 1 * 1000 /* 1 minute timeout */).apply(uri);
         assertTrue(jobDone, "create operation did not complete in the configured timeout");
      }
      return api.getStorageAccountApi(resourceGroupName).get(storageServiceName);
   }

   protected ResourceGroup createResourceGroup(String resourceGroupName) {
      return api.getResourceGroupApi().create(resourceGroupName, LOCATION, ImmutableMap.<String, String>of());
   }

   protected URI deleteResourceGroup(String resourceGroupName) {
      return api.getResourceGroupApi().delete(resourceGroupName);
   }

   protected NetworkSecurityGroup newNetworkSecurityGroup(String nsgName, String locationName) {
      NetworkSecurityRule rule = NetworkSecurityRule.create("denyallout", null, null,
              NetworkSecurityRuleProperties.builder()
                      .description("deny all out")
                      .protocol(NetworkSecurityRuleProperties.Protocol.Tcp)
                      .sourcePortRange("*")
                      .destinationPortRange("*")
                      .sourceAddressPrefix("*")
                      .destinationAddressPrefix("*")
                      .access(NetworkSecurityRuleProperties.Access.Deny)
                      .priority(4095)
                      .direction(NetworkSecurityRuleProperties.Direction.Outbound)
                      .build());
      List<NetworkSecurityRule> ruleList = Lists.newArrayList();
      ruleList.add(rule);
      NetworkSecurityGroup nsg = NetworkSecurityGroup.create(nsgName, locationName, null,
              NetworkSecurityGroupProperties.builder()
                      .securityRules(ruleList)
                      .build(),
              null);
      return nsg;
   }

   protected String getSubscriptionId() {
      String subscriptionId = endpoint.substring(endpoint.lastIndexOf("/") + 1);
      assertNotNull(subscriptionId);
      return subscriptionId;
   }

}
