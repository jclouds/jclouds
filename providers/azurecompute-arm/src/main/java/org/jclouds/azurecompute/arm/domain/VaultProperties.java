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
package org.jclouds.azurecompute.arm.domain;

import java.net.URI;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

@AutoValue
public abstract class VaultProperties {

   @AutoValue
   public abstract static class Permissions {

      public abstract List<String> certificates();
      public abstract List<String> keys();
      public abstract List<String> secrets();
      public abstract List<String> storage();

      @SerializedNames({ "certificates", "keys", "secrets", "storage"})
      public static Permissions create(final List<String> certificates, final List<String> keys, final List<String> secrets, final List<String> storage) {
         return new AutoValue_VaultProperties_Permissions(
                 certificates != null ? ImmutableList.copyOf(certificates) : ImmutableList.<String> of(),
                 keys != null ? ImmutableList.copyOf(keys) : ImmutableList.<String> of(),
                 secrets != null ? ImmutableList.copyOf(secrets) : ImmutableList.<String> of(),
                 storage != null ? ImmutableList.copyOf(storage) : ImmutableList.<String> of()
         );
      }

      Permissions() {

      }
   }
   
   @AutoValue
   public abstract static class AccessPolicyEntry {

      @Nullable public abstract String applicationId();
      public abstract String objectId();
      public abstract String tenantId();
      @Nullable public abstract Permissions permissions();

      @SerializedNames({"applicationId", "objectId", "tenantId", "permissions"})
      public static AccessPolicyEntry create(final String applicationId, final String objectId, final String tenantId, final Permissions permissions) {
         return new AutoValue_VaultProperties_AccessPolicyEntry(applicationId, objectId, tenantId, permissions);
      }

      AccessPolicyEntry() {

      }
   }
   
   @Nullable
   public abstract String tenantId();
   @Nullable
   public abstract URI vaultUri();
   @Nullable
   public abstract Boolean enabledForDeployment();
   @Nullable
   public abstract Boolean enabledForTemplateDeployment();
   @Nullable
   public abstract Boolean enableSoftDelete();
   @Nullable
   public abstract String createMode();
   @Nullable
   public abstract SKU sku();
   @Nullable
   public abstract List<AccessPolicyEntry> accessPolicies();

   @SerializedNames({"tenantId", "vaultUri", "enabledForDeployment", "enabledForTemplateDeployment", "enableSoftDelete", "createMode", "sku", "accessPolicies" })
   public static VaultProperties create(final String tenantId, final URI vaultUri, final Boolean enabledForDeployment, final Boolean enabledForTemplateDeployment, final Boolean enableSoftDelete, final String createMode,
                                        final SKU sku, final List<AccessPolicyEntry> accessPolicies) {
      return builder()
              .tenantId(tenantId)
              .vaultUri(vaultUri)
              .enabledForDeployment(enabledForDeployment)
              .enabledForTemplateDeployment(enabledForTemplateDeployment)
              .enableSoftDelete(enableSoftDelete)
              .createMode(createMode)
              .sku(sku)
              .accessPolicies(accessPolicies)
              .build();
   }

   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_VaultProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder tenantId(String tenantId);
      public abstract Builder vaultUri(URI vaultUri);
      public abstract Builder enabledForDeployment(Boolean enabledForDeployment);
      public abstract Builder enabledForTemplateDeployment(Boolean enabledForTemplateDeployment);
      public abstract Builder enableSoftDelete(Boolean enableSoftDelete);
      public abstract Builder createMode(String createMode);
      public abstract Builder sku(SKU sku);
      public abstract Builder accessPolicies(List<AccessPolicyEntry> accessPolicies);
      
      abstract List<AccessPolicyEntry> accessPolicies();

      abstract VaultProperties autoBuild();

      public VaultProperties build() {
         return accessPolicies(accessPolicies() != null ? ImmutableList.copyOf(accessPolicies()) : ImmutableList.<AccessPolicyEntry>of())
                 .autoBuild();
      }

   }
}
