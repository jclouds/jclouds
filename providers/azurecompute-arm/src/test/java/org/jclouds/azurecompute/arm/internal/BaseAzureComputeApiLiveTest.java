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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_DELETE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_SECRET_DELETE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_SECRET_RECOVERABLE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_KEY_DELETED_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_KEY_RECOVERABLE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_CERTIFICATE_DELETE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_CERTIFICATE_RECOVERABLE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_CERTIFICATE_OPERATION_STATUS;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.config.Tenant;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.VaultPredicates.DeletedVaultStatusPredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.VaultKeyPredicates.DeletedKeyStatusPredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.VaultKeyPredicates.RecoverableKeyStatusPredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.VaultSecretPredicates.DeletedSecretStatusPredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.VaultSecretPredicates.RecoverableSecretStatusPredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.VaultCertificatePredicates.CertificateOperationStatusPredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.VaultCertificatePredicates.DeletedCertificateStatusPredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.VaultCertificatePredicates.RecoverableCertificateStatusPredicateFactory;
import org.jclouds.azurecompute.arm.compute.config.AzurePredicatesModule.PublicIpAvailablePredicateFactory;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroupProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.name.Names;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class BaseAzureComputeApiLiveTest extends BaseApiLiveTest<AzureComputeApi> {

   protected static final int RAND = new Random().nextInt(999);
   public static final String LOCATION = "westeurope";
   public static final String LOCATIONDESCRIPTION = "West Europe";
   public static final String DEFAULT_VIRTUALNETWORK_ADDRESS_PREFIX = "10.2.0.0/16";
   
   protected Predicate<URI> imageAvailablePredicate;
   protected Predicate<URI> resourceDeleted;
   protected PublicIpAvailablePredicateFactory publicIpAvailable;
   protected Predicate<Supplier<Provisionable>> resourceAvailable;
   protected DeletedVaultStatusPredicateFactory deletedVaultStatus;
   protected DeletedKeyStatusPredicateFactory deletedKeyStatus;
   protected RecoverableKeyStatusPredicateFactory recoverableKeyStatus;
   protected DeletedSecretStatusPredicateFactory deletedSecretStatus;
   protected RecoverableSecretStatusPredicateFactory recoverableSecretStatus;
   protected DeletedCertificateStatusPredicateFactory deletedCertificateStatus;
   protected RecoverableCertificateStatusPredicateFactory recoverableCertificateStatus;
   protected CertificateOperationStatusPredicateFactory certificateOperationStatus;


   protected String resourceGroupName;
   
   protected String vaultResourceGroup;
   protected String vaultName;
   protected String vaultCertificateUrl;
   protected String tenantId;

   public BaseAzureComputeApiLiveTest() {
      provider = "azurecompute-arm";
   }
   
   @Override
   @AfterClass(groups = "live")
   protected void tearDown() {
      try {
         if (resourceGroupName != null) {
            deleteResourceGroup(resourceGroupName);
         }
      } finally {
         super.tearDown();
      }
   }

   @BeforeClass
   @Override
   public void setup() {
      super.setup();

      // Providing system properties for specifying the required Azure KeyVault configurations for Live tests
      // They have to be externally provided, because azurecompute-arm doesn't support creating KeyVaults yet
      //
      // TODO Replace the used configurations once full KeyVault implementation is added to azurecompute-arm
      vaultResourceGroup = System.getProperty("test.azurecompute-arm.vault.resource.group");
      vaultName = System.getProperty("test.azurecompute-arm.vault.name");
      vaultCertificateUrl = System.getProperty("test.azurecompute-arm.vault.certificate.url");
   }

   @Override protected AzureComputeApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      imageAvailablePredicate = injector.getInstance(Key.get(new TypeLiteral<Predicate<URI>>() {
      }, Names.named(TIMEOUT_IMAGE_AVAILABLE)));
      resourceDeleted = injector.getInstance(Key.get(new TypeLiteral<Predicate<URI>>() {
      }, Names.named(TIMEOUT_RESOURCE_DELETED)));
      publicIpAvailable = injector.getInstance(PublicIpAvailablePredicateFactory.class);
      resourceAvailable = injector.getInstance(Key.get(new TypeLiteral<Predicate<Supplier<Provisionable>>>() {
      }));
      deletedVaultStatus = injector.getInstance(Key.get(DeletedVaultStatusPredicateFactory.class, Names.named(VAULT_DELETE_STATUS)));
      deletedKeyStatus = injector.getInstance(Key.get(DeletedKeyStatusPredicateFactory.class, Names.named(VAULT_KEY_DELETED_STATUS)));
      recoverableKeyStatus = injector.getInstance(Key.get(RecoverableKeyStatusPredicateFactory.class, Names.named(VAULT_KEY_RECOVERABLE_STATUS)));
      deletedSecretStatus = injector.getInstance(Key.get(DeletedSecretStatusPredicateFactory.class, Names.named(VAULT_SECRET_DELETE_STATUS)));
      recoverableSecretStatus = injector.getInstance(Key.get(RecoverableSecretStatusPredicateFactory.class, Names.named(VAULT_SECRET_RECOVERABLE_STATUS)));
      deletedCertificateStatus = injector.getInstance(Key.get(DeletedCertificateStatusPredicateFactory.class, Names.named(VAULT_CERTIFICATE_DELETE_STATUS)));
      recoverableCertificateStatus = injector.getInstance(Key.get(RecoverableCertificateStatusPredicateFactory.class, Names.named(VAULT_CERTIFICATE_RECOVERABLE_STATUS)));
      certificateOperationStatus = injector.getInstance(Key.get(CertificateOperationStatusPredicateFactory.class, Names.named(VAULT_CERTIFICATE_OPERATION_STATUS)));

      tenantId = injector.getInstance(Key.get(String.class, Tenant.class));
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
      if (uri != null) {
         assertTrue(resourceDeleted.apply(uri),
               String.format("Resource %s was not terminated in the configured timeout", uri));
      }
   }

   protected VirtualNetwork createDefaultVirtualNetwork(final String resourceGroupName, final String virtualNetworkName, final String virtualnetworkAddressPrefix, final String location) {
      final VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties =
              VirtualNetwork.VirtualNetworkProperties.create(null, null,
                      VirtualNetwork.AddressSpace.create(Arrays.asList(virtualnetworkAddressPrefix)), null);
      VirtualNetwork virtualNetwork = api.getVirtualNetworkApi(resourceGroupName).createOrUpdate(virtualNetworkName, location, null, virtualNetworkProperties);
      retry(new Predicate<String>() {
         @Override
         public boolean apply(final String name) {
            VirtualNetwork virtualNetwork = api.getVirtualNetworkApi(resourceGroupName).get(name);
            return virtualNetwork.properties().provisioningState().equals("Succeeded");
         }
      }, 60 * 4 * 1000).apply(virtualNetwork.name());
      return virtualNetwork;
   }

   protected Subnet createDefaultSubnet(final String resourceGroupName, final String subnetName, final String virtualNetworkName, final String subnetAddressSpace) {
      Subnet.SubnetProperties  properties = Subnet.SubnetProperties.builder().addressPrefix(subnetAddressSpace).build();
      Subnet subnet = api.getSubnetApi(resourceGroupName, virtualNetworkName).createOrUpdate(subnetName, properties);
      retry(new Predicate<String>() {
         @Override
         public boolean apply(final String name) {
            Subnet subnet = api.getSubnetApi(resourceGroupName, virtualNetworkName).get(name);
            return subnet.properties().provisioningState().equals("Succeeded");
         }
      }, 60 * 4 * 1000).apply(subnet.name());
      return subnet;
   }

   protected void createTestResourceGroup() {
      String name = String.format("rg-%s-%s", this.getClass().getSimpleName().toLowerCase(),
            System.getProperty("user.name"));
      ResourceGroup rg = api.getResourceGroupApi().create(name, LOCATION, ImmutableMap.<String, String> of());
      assertNotNull(rg);
      resourceGroupName = rg.name();
   }

   protected void deleteResourceGroup(final String resourceGroupName) {
      URI uri = api.getResourceGroupApi().delete(resourceGroupName);
      assertResourceDeleted(uri);
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
      NetworkSecurityGroup nsg = NetworkSecurityGroup.create("id", nsgName, locationName, null,
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

   protected String stringFromResource(String resourceName) {
      try {
         return Resources.toString(getClass().getResource(resourceName), Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
}
