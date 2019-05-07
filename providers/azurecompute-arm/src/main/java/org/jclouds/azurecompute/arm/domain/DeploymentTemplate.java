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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.jclouds.javax.annotation.Nullable;

import java.util.List;
import java.util.Map;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class DeploymentTemplate {

   //Empty placeholders as we want to generate the empty JSON object
   @AutoValue
   public abstract static class Parameters {

      @Nullable
      public abstract KeyVaultReference publicKeyFromAzureKeyVault();

      public static Parameters create(KeyVaultReference reference)
      {
         return new AutoValue_DeploymentTemplate_Parameters(reference);
      }
   }

   @AutoValue
   public abstract static class TemplateParameters {

      @Nullable
      public abstract TemplateParameterType publicKeyFromAzureKeyVault();

      public static TemplateParameters create(TemplateParameterType publicKeyFromAzureKeyVault)
      {
         return new AutoValue_DeploymentTemplate_TemplateParameters(publicKeyFromAzureKeyVault);
      }
   }

   public abstract String schema();

   public abstract String contentVersion();

   public abstract TemplateParameters parameters();

   public abstract Map<String, String> variables();

   public abstract List<ResourceDefinition> resources();

   @Nullable
   public abstract List<?> outputs();

   @SerializedNames({"$schema", "contentVersion", "parameters", "variables", "resources", "outputs"})
   public static DeploymentTemplate create(final String schema,
                                           final String contentVersion,
                                           final TemplateParameters parameters,
                                           final Map<String, String> variables,
                                           final List<ResourceDefinition> resources,
                                           final List<?> outputs) {

      DeploymentTemplate.Builder builder = DeploymentTemplate.builder()
              .schema(schema)
              .contentVersion(contentVersion)
              .parameters(parameters);

      if (variables != null)
         builder.variables(variables);

      if (resources != null)
         builder.resources(resources);

      builder.outputs(outputs == null ? null : ImmutableList.copyOf(outputs));

      return builder.build();
   }
   
   public abstract Builder toBuilder();

   public static Builder builder() {
      return new AutoValue_DeploymentTemplate.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder schema(String schema);

      public abstract Builder contentVersion(String type);

      public abstract Builder parameters(TemplateParameters parameters);

      public abstract Builder variables(Map<String, String> variables);

      public abstract Builder resources(List<ResourceDefinition> resources);

      public abstract Builder outputs(List<?> outputs);

      abstract Map<String, String> variables();
      abstract List<ResourceDefinition> resources();

      abstract DeploymentTemplate autoBuild();

      public DeploymentTemplate build() {
         variables(variables() != null ? ImmutableMap.copyOf(variables()) : null);
         resources(resources() != null ? ImmutableList.copyOf(resources()) : null);
         return autoBuild();
      }
   }
}
