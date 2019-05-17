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
package org.jclouds.azurecompute.arm.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TIMEOUT_RESOURCE_DELETED;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_CERTIFICATE_DELETE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_CERTIFICATE_OPERATION_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_CERTIFICATE_RECOVERABLE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_DELETE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_KEY_DELETED_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_KEY_RECOVERABLE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_SECRET_DELETE_STATUS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.VAULT_SECRET_RECOVERABLE_STATUS;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.util.Predicates2.retry;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateBundle;
import org.jclouds.azurecompute.arm.domain.Certificate.CertificateOperation;
import org.jclouds.azurecompute.arm.domain.Certificate.DeletedCertificateBundle;
import org.jclouds.azurecompute.arm.domain.Image;
import org.jclouds.azurecompute.arm.domain.Key.DeletedKeyBundle;
import org.jclouds.azurecompute.arm.domain.Key.KeyBundle;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.Provisionable;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.Secret.DeletedSecretBundle;
import org.jclouds.azurecompute.arm.domain.Secret.SecretBundle;
import org.jclouds.azurecompute.arm.domain.Vault;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.domain.publicipaddress.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.vpn.VirtualNetworkGateway;
import org.jclouds.azurecompute.arm.functions.ParseJobStatus;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

public class AzurePredicatesModule extends AbstractModule {
   protected void configure() {
   }

   @Provides
   @Named(TIMEOUT_NODE_RUNNING)
   protected VirtualMachineInStatePredicateFactory provideVirtualMachineRunningPredicate(final AzureComputeApi api,
         final ComputeServiceConstants.Timeouts timeouts, final PollPeriod pollPeriod) {
      return new VirtualMachineInStatePredicateFactory(api, VirtualMachineInstance.PowerState.RUNNING,
            timeouts.nodeRunning, pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<URI> provideNodeTerminatedPredicate(final AzureComputeApi api,
         final ComputeServiceConstants.Timeouts timeouts, final PollPeriod pollPeriod) {
      return retry(new ActionDonePredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_IMAGE_AVAILABLE)
   protected Predicate<URI> provideImageCapturedPredicate(final AzureComputeApi api,
         final ComputeServiceConstants.Timeouts timeouts, final PollPeriod pollPeriod) {
      return retry(new ImageCapturedPredicate(api), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_RESOURCE_DELETED)
   protected Predicate<URI> provideResourceDeletedPredicate(final AzureComputeApi api,
         final ComputeServiceConstants.Timeouts timeouts, final PollPeriod pollPeriod) {
      return retry(new ActionDonePredicate(api), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected VirtualMachineInStatePredicateFactory provideNodeSuspendedPredicate(final AzureComputeApi api,
         final ComputeServiceConstants.Timeouts timeouts, final PollPeriod pollPeriod) {
      return new VirtualMachineInStatePredicateFactory(api, VirtualMachineInstance.PowerState.STOPPED,
            timeouts.nodeTerminated, pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   protected PublicIpAvailablePredicateFactory providePublicIpAvailablePredicate(final AzureComputeApi api,
         Predicate<Supplier<Provisionable>> resourceAvailable) {
      return new PublicIpAvailablePredicateFactory(api, resourceAvailable);
   }

   @Provides
   protected SecurityGroupAvailablePredicateFactory provideSecurityGroupAvailablePredicate(final AzureComputeApi api,
         Predicate<Supplier<Provisionable>> resourceAvailable) {
      return new SecurityGroupAvailablePredicateFactory(api, resourceAvailable);
   }

   @Provides
   protected SecurityGroupRuleAvailablePredicateFactory provideSecurityGroupRuleAvailablePredicate(final AzureComputeApi api,
         Predicate<Supplier<Provisionable>> resourceAvailable) {
      return new SecurityGroupRuleAvailablePredicateFactory(api, resourceAvailable);
   }

   @Provides
   protected ImageAvailablePredicateFactory provideImageAvailablePredicate(final AzureComputeApi api,
         final ComputeServiceConstants.Timeouts timeouts, final PollPeriod pollPeriod) {
      return new ImageAvailablePredicateFactory(api, retry(new ResourceInStatusPredicate("Succeeded"),
            timeouts.imageAvailable, pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod));
   }

   @Provides
   protected VirtualNetworkGatewayAvailablePredicateFactory provideVirtualNetworkGatewayAvailablePredicate(
         final AzureComputeApi api, Predicate<Supplier<Provisionable>> resourceAvailable,
         final ComputeServiceConstants.Timeouts timeouts, final PollPeriod pollPeriod) {
      // The Azure Virtual Gateways can take up to 45 minutes to be provisioned.
      // Don't poll too aggressively
      return new VirtualNetworkGatewayAvailablePredicateFactory(api, retry(new ResourceInStatusPredicate("Succeeded"),
            45, 1, 2, TimeUnit.MINUTES));
   }

   @Provides
   protected Predicate<Supplier<Provisionable>> provideResourceAvailablePredicate(final AzureComputeApi api,
         @Named(OPERATION_TIMEOUT) Integer operationTimeout, PollPeriod pollPeriod) {
      return retry(new ResourceInStatusPredicate("Succeeded"), operationTimeout, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   protected NetworkAvailablePredicateFactory provideNetworkAvailablePredicate(final AzureComputeApi api,
         Predicate<Supplier<Provisionable>> resourceAvailable) {
      return new NetworkAvailablePredicateFactory(api, resourceAvailable);
   }

   @VisibleForTesting
   static class ActionDonePredicate implements Predicate<URI> {

      private final AzureComputeApi api;

      public ActionDonePredicate(final AzureComputeApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(final URI uri) {
         checkNotNull(uri, "uri cannot be null");
         return ParseJobStatus.JobStatus.DONE == api.getJobApi().jobStatus(uri)
               || ParseJobStatus.JobStatus.NO_CONTENT == api.getJobApi().jobStatus(uri);
      }
   }

   @VisibleForTesting
   static class ImageCapturedPredicate implements Predicate<URI> {

      private final AzureComputeApi api;

      public ImageCapturedPredicate(final AzureComputeApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(final URI uri) {
         checkNotNull(uri, "uri cannot be null");
         if (api.getJobApi().jobStatus(uri) != ParseJobStatus.JobStatus.DONE) {
            return false;
         }
         List<ResourceDefinition> definitions = api.getJobApi().captureStatus(uri);
         return definitions != null;
      }
   }

   public static class VirtualMachineInStatePredicateFactory {

      private final AzureComputeApi api;
      private final VirtualMachineInstance.PowerState powerState;
      private final long timeout;
      private final long period;
      private final long maxPeriod;

      VirtualMachineInStatePredicateFactory(final AzureComputeApi api,
            final VirtualMachineInstance.PowerState powerState, final long timeout, final long period,
            final long maxPeriod) {
         this.api = checkNotNull(api, "api cannot be null");
         this.powerState = checkNotNull(powerState, "powerState cannot be null");
         this.timeout = timeout;
         this.period = period;
         this.maxPeriod = maxPeriod;
      }

      public Predicate<String> create(final String azureGroup) {
         return retry(new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               VirtualMachineInstance vmInstance = api.getVirtualMachineApi(azureGroup).getInstanceDetails(name);
               if (vmInstance == null) {
                  return false;
               }
               return powerState == vmInstance.powerState();
            }
         }, timeout, period, maxPeriod);
      }
   }

   public static class ResourceInStatusPredicate implements Predicate<Supplier<Provisionable>> {
      private final String expectedStatus;

      ResourceInStatusPredicate(String expectedStatus) {
         this.expectedStatus = checkNotNull(expectedStatus, "expectedStatus cannot be null");
      }

      @Override
      public boolean apply(Supplier<Provisionable> provisionableSupplier) {
         checkNotNull(provisionableSupplier, "provisionableSupplier supplier cannot be null");
         Provisionable provisionable = provisionableSupplier.get();
         return provisionable != null && provisionable.provisioningState().equalsIgnoreCase(expectedStatus);
      }
   }

   public static class PublicIpAvailablePredicateFactory {
      private final AzureComputeApi api;
      private final Predicate<Supplier<Provisionable>> resourceAvailable;

      PublicIpAvailablePredicateFactory(final AzureComputeApi api, Predicate<Supplier<Provisionable>> resourceAvailable) {
         this.api = checkNotNull(api, "api cannot be null");
         this.resourceAvailable = resourceAvailable;
      }

      public Predicate<String> create(final String azureGroup) {
         checkNotNull(azureGroup, "azureGroup cannot be null");
         return new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               return resourceAvailable.apply(new Supplier<Provisionable>() {
                  @Override
                  public Provisionable get() {
                     PublicIPAddress publicIp = api.getPublicIPAddressApi(azureGroup).get(name);
                     return publicIp == null ? null : publicIp.properties();
                  }
               });
            }
         };
      }
   }

   public static class SecurityGroupAvailablePredicateFactory {
      private final AzureComputeApi api;
      private final Predicate<Supplier<Provisionable>> resourceAvailable;

      SecurityGroupAvailablePredicateFactory(final AzureComputeApi api,
            Predicate<Supplier<Provisionable>> resourceAvailable) {
         this.api = checkNotNull(api, "api cannot be null");
         this.resourceAvailable = resourceAvailable;
      }

      public Predicate<String> create(final String resourceGroup) {
         checkNotNull(resourceGroup, "resourceGroup cannot be null");
         return new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               return resourceAvailable.apply(new Supplier<Provisionable>() {
                  @Override
                  public Provisionable get() {
                     NetworkSecurityGroup sg = api.getNetworkSecurityGroupApi(resourceGroup).get(name);
                     return sg == null ? null : sg.properties();
                  }
               });
            }
         };
      }
   }

   public static class SecurityGroupRuleAvailablePredicateFactory {
      private final AzureComputeApi api;
      private final Predicate<Supplier<Provisionable>> resourceAvailable;

      SecurityGroupRuleAvailablePredicateFactory(final AzureComputeApi api, Predicate<Supplier<Provisionable>> resourceAvailable) {
         this.api = checkNotNull(api, "api cannot be null");
         this.resourceAvailable = resourceAvailable;
      }

      public Predicate<String> create(final String resourceGroup, final String securityGroupName) {
         checkNotNull(resourceGroup, "resourceGroup cannot be null");
         checkNotNull(securityGroupName, "securityGroupName cannot be null");
         return new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               return resourceAvailable.apply(new Supplier<Provisionable>() {
                  @Override
                  public Provisionable get() {
                     NetworkSecurityRule securityRule = api.getNetworkSecurityRuleApi(resourceGroup, securityGroupName).get(name);
                     return securityRule == null ? null : securityRule.properties();
                  }
               });
            }
         };
      }
   }

   public static class NetworkAvailablePredicateFactory {
      private final AzureComputeApi api;
      private final Predicate<Supplier<Provisionable>> resourceAvailable;

      NetworkAvailablePredicateFactory(final AzureComputeApi api, Predicate<Supplier<Provisionable>> resourceAvailable) {
         this.api = checkNotNull(api, "api cannot be null");
         this.resourceAvailable = resourceAvailable;
      }

      public Predicate<String> create(final String resourceGroup) {
         checkNotNull(resourceGroup, "resourceGroup cannot be null");
         return new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               return resourceAvailable.apply(new Supplier<Provisionable>() {
                  @Override
                  public Provisionable get() {
                     VirtualNetwork vnet = api.getVirtualNetworkApi(resourceGroup).get(name);
                     return vnet == null ? null : vnet.properties();
                  }
               });
            }
         };
      }
   }

   public static class ImageAvailablePredicateFactory {
      private final AzureComputeApi api;
      private final Predicate<Supplier<Provisionable>> resourceAvailable;

      ImageAvailablePredicateFactory(final AzureComputeApi api, Predicate<Supplier<Provisionable>> resourceAvailable) {
         this.api = checkNotNull(api, "api cannot be null");
         this.resourceAvailable = resourceAvailable;
      }

      public Predicate<String> create(final String resourceGroup) {
         checkNotNull(resourceGroup, "resourceGroup cannot be null");
         return new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               return resourceAvailable.apply(new Supplier<Provisionable>() {
                  @Override
                  public Provisionable get() {
                     Image img = api.getVirtualMachineImageApi(resourceGroup).get(name);
                     return img == null ? null : img.properties();
                  }
               });
            }
         };
      }
   }

   public static class VirtualNetworkGatewayAvailablePredicateFactory {
      private final AzureComputeApi api;
      private final Predicate<Supplier<Provisionable>> resourceAvailable;

      VirtualNetworkGatewayAvailablePredicateFactory(final AzureComputeApi api,
            Predicate<Supplier<Provisionable>> resourceAvailable) {
         this.api = checkNotNull(api, "api cannot be null");
         this.resourceAvailable = resourceAvailable;
      }

      public Predicate<String> create(final String resourceGroup) {
         checkNotNull(resourceGroup, "resourceGroup cannot be null");
         return new Predicate<String>() {
            @Override
            public boolean apply(final String name) {
               checkNotNull(name, "name cannot be null");
               return resourceAvailable.apply(new Supplier<Provisionable>() {
                  @Override
                  public Provisionable get() {
                     VirtualNetworkGateway vng = api.getVirtualNetworkGatewayApi(resourceGroup).get(name);
                     return vng == null ? null : vng.properties();
                  }
               });
            }
         };
      }
   }

   @Provides
   @Named(VAULT_DELETE_STATUS)
   protected VaultPredicates.DeletedVaultStatusPredicateFactory provideDeletedVaultStatusPredicateFactory(
         final AzureComputeApi api, @Named(OPERATION_TIMEOUT) Integer operationTimeout, final PollPeriod pollPeriod) {
      return new VaultPredicates.DeletedVaultStatusPredicateFactory(api, operationTimeout.longValue(),
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   public static class VaultPredicates {
      public static class DeletedVaultStatusPredicateFactory {
         private final AzureComputeApi api;
         private final long operationTimeout;
         private final long initialPeriod;
         private final long maxPeriod;

         DeletedVaultStatusPredicateFactory(final AzureComputeApi api, final long operationTimeout,
               final long initialPeriod, final long maxPeriod) {
            this.api = checkNotNull(api, "api cannot be null");
            this.operationTimeout = operationTimeout;
            this.initialPeriod = initialPeriod;
            this.maxPeriod = maxPeriod;
         }

         public Predicate<String> create(final String resourceGroup, final boolean shouldBePresent) {
            checkNotNull(resourceGroup, "resourceGroup cannot be null");
            return retry(new Predicate<String>() {
               @Override
               public boolean apply(final String name) {
                  checkNotNull(name, "name cannot be null");
                  List<Vault.DeletedVault> vaults = api.getVaultApi(resourceGroup).listDeletedVaults();
                  return shouldBePresent == Iterables.any(vaults, new Predicate<Vault.DeletedVault>() {
                     @Override
                     public boolean apply(Vault.DeletedVault input) {
                        return input.name().equals(name);
                     }
                  });
               }
            }, operationTimeout, initialPeriod, maxPeriod);
         }
      }
   }

   @Provides
   @Named(VAULT_KEY_DELETED_STATUS)
   protected VaultKeyPredicates.DeletedKeyStatusPredicateFactory provideDeletedKeyStatusPredicateFactory(
         final AzureComputeApi api, @Named(OPERATION_TIMEOUT) Integer operationTimeout, final PollPeriod pollPeriod) {
      return new VaultKeyPredicates.DeletedKeyStatusPredicateFactory(api, operationTimeout.longValue(),
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(VAULT_KEY_RECOVERABLE_STATUS)
   protected VaultKeyPredicates.RecoverableKeyStatusPredicateFactory provideRecoverableKeyStatusPredicateFactory(
         final AzureComputeApi api, @Named(OPERATION_TIMEOUT) Integer operationTimeout, final PollPeriod pollPeriod) {
      return new VaultKeyPredicates.RecoverableKeyStatusPredicateFactory(api, operationTimeout.longValue(),
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   public static class VaultKeyPredicates {
      public static class DeletedKeyStatusPredicateFactory {
         private final AzureComputeApi api;
         private final long operationTimeout;
         private final long initialPeriod;
         private final long maxPeriod;

         DeletedKeyStatusPredicateFactory(final AzureComputeApi api, final long operationTimeout,
               final long initialPeriod, final long maxPeriod) {
            this.api = checkNotNull(api, "api cannot be null");
            this.operationTimeout = operationTimeout;
            this.initialPeriod = initialPeriod;
            this.maxPeriod = maxPeriod;
         }

         public Predicate<String> create(final String resourceGroup, final URI vaultUri, final boolean shouldBePresent) {
            checkNotNull(resourceGroup, "resourceGroup cannot be null");
            checkNotNull(vaultUri, "vaultUri cannot be null");
            return retry(new Predicate<String>() {
               @Override
               public boolean apply(final String name) {
                  checkNotNull(name, "name cannot be null");
                  DeletedKeyBundle key = api.getVaultApi(resourceGroup).getDeletedKey(vaultUri, name);
                  return shouldBePresent == (key != null);
               }
            }, operationTimeout, initialPeriod, maxPeriod);
         }
      }

      public static class RecoverableKeyStatusPredicateFactory {
         private final AzureComputeApi api;
         private final long operationTimeout;
         private final long initialPeriod;
         private final long maxPeriod;

         RecoverableKeyStatusPredicateFactory(final AzureComputeApi api, final long operationTimeout,
               final long initialPeriod, final long maxPeriod) {
            this.api = checkNotNull(api, "api cannot be null");
            this.operationTimeout = operationTimeout;
            this.initialPeriod = initialPeriod;
            this.maxPeriod = maxPeriod;
         }

         public Predicate<String> create(final String resourceGroup, final URI vaultUri, final boolean isRecovered) {
            checkNotNull(resourceGroup, "resourceGroup cannot be null");
            checkNotNull(vaultUri, "vaultUri cannot be null");
            return retry(new Predicate<String>() {
               @Override
               public boolean apply(final String name) {
                  checkNotNull(name, "name cannot be null");
                  KeyBundle key = api.getVaultApi(resourceGroup).getKey(vaultUri, name);
                  return key != null ? (isRecovered ? true : key.attributes().recoveryLevel().contains("Recoverable"))
                        : false;
               }
            }, operationTimeout, initialPeriod, maxPeriod);
         }
      }
   }

   @Provides
   @Named(VAULT_SECRET_DELETE_STATUS)
   protected VaultSecretPredicates.DeletedSecretStatusPredicateFactory provideDeletedSecretStatusPredicateFactory(
         final AzureComputeApi api, @Named(OPERATION_TIMEOUT) Integer operationTimeout, final PollPeriod pollPeriod) {
      return new VaultSecretPredicates.DeletedSecretStatusPredicateFactory(api, operationTimeout.longValue(),
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(VAULT_SECRET_RECOVERABLE_STATUS)
   protected VaultSecretPredicates.RecoverableSecretStatusPredicateFactory provideRecoverableSecretStatusPredicateFactory(
         final AzureComputeApi api, @Named(OPERATION_TIMEOUT) Integer operationTimeout, final PollPeriod pollPeriod) {
      return new VaultSecretPredicates.RecoverableSecretStatusPredicateFactory(api, operationTimeout.longValue(),
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   public static class VaultSecretPredicates {
      public static class DeletedSecretStatusPredicateFactory {
         private final AzureComputeApi api;
         private final long operationTimeout;
         private final long initialPeriod;
         private final long maxPeriod;

         DeletedSecretStatusPredicateFactory(final AzureComputeApi api, final long operationTimeout,
               final long initialPeriod, final long maxPeriod) {
            this.api = checkNotNull(api, "api cannot be null");
            this.operationTimeout = operationTimeout;
            this.initialPeriod = initialPeriod;
            this.maxPeriod = maxPeriod;
         }

         public Predicate<String> create(final String resourceGroup, final URI vaultUri, final boolean shouldBePresent) {
            checkNotNull(resourceGroup, "resourceGroup cannot be null");
            checkNotNull(vaultUri, "vaultUri cannot be null");
            return retry(new Predicate<String>() {
               @Override
               public boolean apply(final String name) {
                  checkNotNull(name, "name cannot be null");
                  DeletedSecretBundle secret = api.getVaultApi(resourceGroup).getDeletedSecret(vaultUri, name);
                  return shouldBePresent == (secret != null);
               }
            }, operationTimeout, initialPeriod, maxPeriod);
         }
      }

      public static class RecoverableSecretStatusPredicateFactory {
         private final AzureComputeApi api;
         private final long operationTimeout;
         private final long initialPeriod;
         private final long maxPeriod;

         RecoverableSecretStatusPredicateFactory(final AzureComputeApi api, final long operationTimeout,
               final long initialPeriod, final long maxPeriod) {
            this.api = checkNotNull(api, "api cannot be null");
            this.operationTimeout = operationTimeout;
            this.initialPeriod = initialPeriod;
            this.maxPeriod = maxPeriod;
         }

         public Predicate<String> create(final String resourceGroup, final URI vaultUri, final boolean isRecovered) {
            checkNotNull(resourceGroup, "resourceGroup cannot be null");
            checkNotNull(vaultUri, "vaultUri cannot be null");
            return retry(new Predicate<String>() {
               @Override
               public boolean apply(final String name) {
                  checkNotNull(name, "name cannot be null");
                  SecretBundle secret = api.getVaultApi(resourceGroup).getSecret(vaultUri, name, null);
                  return secret != null ? (isRecovered ? true : secret.attributes().recoveryLevel()
                        .contains("Recoverable")) : false;
               }
            }, operationTimeout, initialPeriod, maxPeriod);
         }
      }
   }

   @Provides
   @Named(VAULT_CERTIFICATE_DELETE_STATUS)
   protected VaultCertificatePredicates.DeletedCertificateStatusPredicateFactory provideDeletedCertificateStatusPredicateFactory(
         final AzureComputeApi api, @Named(OPERATION_TIMEOUT) Integer operationTimeout, final PollPeriod pollPeriod) {
      return new VaultCertificatePredicates.DeletedCertificateStatusPredicateFactory(api, operationTimeout.longValue(),
            pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(VAULT_CERTIFICATE_RECOVERABLE_STATUS)
   protected VaultCertificatePredicates.RecoverableCertificateStatusPredicateFactory provideRecoverableCertificateStatusPredicateFactory(
         final AzureComputeApi api, @Named(OPERATION_TIMEOUT) Integer operationTimeout, final PollPeriod pollPeriod) {
      return new VaultCertificatePredicates.RecoverableCertificateStatusPredicateFactory(api,
            operationTimeout.longValue(), pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Named(VAULT_CERTIFICATE_OPERATION_STATUS)
   protected VaultCertificatePredicates.CertificateOperationStatusPredicateFactory provideCertificateOperationStatusPredicateFactory(
         final AzureComputeApi api, @Named(OPERATION_TIMEOUT) Integer operationTimeout, final PollPeriod pollPeriod) {
      return new VaultCertificatePredicates.CertificateOperationStatusPredicateFactory(api,
            operationTimeout.longValue(), pollPeriod.pollInitialPeriod, pollPeriod.pollMaxPeriod);
   }

   public static class VaultCertificatePredicates {
      public static class DeletedCertificateStatusPredicateFactory {
         private final AzureComputeApi api;
         private final long operationTimeout;
         private final long initialPeriod;
         private final long maxPeriod;

         DeletedCertificateStatusPredicateFactory(final AzureComputeApi api, final long operationTimeout,
               final long initialPeriod, final long maxPeriod) {
            this.api = checkNotNull(api, "api cannot be null");
            this.operationTimeout = operationTimeout;
            this.initialPeriod = initialPeriod;
            this.maxPeriod = maxPeriod;
         }

         public Predicate<String> create(final String resourceGroup, final URI vaultUri, final boolean shouldBePresent) {
            checkNotNull(resourceGroup, "resourceGroup cannot be null");
            checkNotNull(vaultUri, "vaultUri cannot be null");
            return retry(new Predicate<String>() {
               @Override
               public boolean apply(final String name) {
                  checkNotNull(name, "name cannot be null");
                  DeletedCertificateBundle cert = api.getVaultApi(resourceGroup).getDeletedCertificate(vaultUri, name);
                  return shouldBePresent == (cert != null);
               }
            }, operationTimeout, initialPeriod, maxPeriod);
         }
      }

      public static class RecoverableCertificateStatusPredicateFactory {
         private final AzureComputeApi api;
         private final long operationTimeout;
         private final long initialPeriod;
         private final long maxPeriod;

         RecoverableCertificateStatusPredicateFactory(final AzureComputeApi api, final long operationTimeout,
               final long initialPeriod, final long maxPeriod) {
            this.api = checkNotNull(api, "api cannot be null");
            this.operationTimeout = operationTimeout;
            this.initialPeriod = initialPeriod;
            this.maxPeriod = maxPeriod;
         }

         public Predicate<String> create(final String resourceGroup, final URI vaultUri, final boolean isImport) {
            checkNotNull(resourceGroup, "resourceGroup cannot be null");
            checkNotNull(vaultUri, "vaultUri cannot be null");
            return retry(new Predicate<String>() {
               @Override
               public boolean apply(final String name) {
                  checkNotNull(name, "name cannot be null");
                  CertificateBundle cert = api.getVaultApi(resourceGroup).getCertificate(vaultUri, name, null);
                  return cert != null ? (isImport ? true : cert.attributes().recoveryLevel().contains("Recoverable"))
                        : false;

               }
            }, operationTimeout, initialPeriod, maxPeriod);
         }
      }

      public static class CertificateOperationStatusPredicateFactory {
         private final AzureComputeApi api;
         private final long operationTimeout;
         private final long initialPeriod;
         private final long maxPeriod;

         CertificateOperationStatusPredicateFactory(final AzureComputeApi api, final long operationTimeout,
               final long initialPeriod, final long maxPeriod) {
            this.api = checkNotNull(api, "api cannot be null");
            this.operationTimeout = operationTimeout;
            this.initialPeriod = initialPeriod;
            this.maxPeriod = maxPeriod;
         }

         public Predicate<String> create(final String resourceGroup, final URI vaultUri, final boolean isCreate) {
            checkNotNull(resourceGroup, "resourceGroup cannot be null");
            checkNotNull(vaultUri, "vaultUri cannot be null");
            return retry(new Predicate<String>() {
               @Override
               public boolean apply(final String name) {
                  checkNotNull(name, "name cannot be null");
                  CertificateOperation certOp = api.getVaultApi(resourceGroup).getCertificateOperation(vaultUri, name);
                  return isCreate ? ((certOp != null) ? !certOp.status().equals("inProgress") : false)
                        : (certOp == null);
               }
            }, operationTimeout, initialPeriod, maxPeriod);
         }
      }
   }
}
