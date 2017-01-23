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

import java.util.List;
import java.util.Map;

import org.jclouds.azurecompute.arm.util.GetEnumValue;
import org.jclouds.domain.JsonBall;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import static com.google.common.collect.ImmutableList.copyOf;

@AutoValue
public abstract class Deployment {

   public enum ProvisioningState {
      ACCEPTED,
      READY,
      CANCELED,
      FAILED,
      DELETED,
      SUCCEEDED,
      RUNNING,
      UNRECOGNIZED;

      public static ProvisioningState fromValue(final String text) {
         return (ProvisioningState) GetEnumValue.fromValueOrDefault(text, ProvisioningState.UNRECOGNIZED);
      }
   }

   public enum DeploymentMode {
      INCREMENTAL,
      COMPLETE,
      UNRECOGNIZED;

      public static DeploymentMode fromValue(final String text) {
         return (DeploymentMode) GetEnumValue.fromValueOrDefault(text, DeploymentMode.UNRECOGNIZED);
      }
   }

   @AutoValue
   public abstract static class TypeValue {
      public abstract String type();

      public abstract String value();

      @SerializedNames({"type", "value"})
      public static TypeValue create(final String type, final String value) {
         return new AutoValue_Deployment_TypeValue(type, value);
      }
   }

   @AutoValue
   public abstract static class ProviderResourceType {
      @Nullable
      public abstract String resourceType();

      @Nullable
      public abstract List<String> locations();

      @Nullable
      public abstract List<String> apiVersions();

      @Nullable
      public abstract Map<String, JsonBall> properties();

      @SerializedNames({"resourceType", "locations", "apiVersions", "properties"})
      public static ProviderResourceType create(final String resourceType,
                                                final List<String> locations,
                                                final List<String> apiVersions,
                                                @Nullable final Map<String, JsonBall> properties) {
         return new AutoValue_Deployment_ProviderResourceType(resourceType,
                 locations == null ? null : copyOf(locations),
                 apiVersions == null ? null : copyOf(apiVersions),
                 properties == null ? ImmutableMap.<String, JsonBall>builder().build() : ImmutableMap.copyOf(properties));
      }
   }

   @AutoValue
   public abstract static class Provider {
      @Nullable
      public abstract String id();

      @Nullable
      public abstract String namespace();

      @Nullable
      public abstract String registrationState();

      @Nullable
      public abstract List<ProviderResourceType> resourceTypes();

      @SerializedNames({"id", "namespace", "registrationState", "resourceTypes"})
      public static Provider create(final String id,
                                    final String namespace,
                                    final String registrationState,
                                    final List<ProviderResourceType> resourceTypes) {
         return new AutoValue_Deployment_Provider(id, namespace, registrationState, resourceTypes == null ? null : copyOf(resourceTypes));
      }
   }

   @AutoValue
   public abstract static class Dependency {
      @Nullable
      public abstract List<Dependency> dependencies();

      @Nullable
      public abstract List<Dependency> dependsOn();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract String resourceType();

      @Nullable
      public abstract String resourceName();

      @SerializedNames({"dependencies", "dependsOn", "id", "resourceType", "resourceName"})
      public static Dependency create(final List<Dependency> dependencies,
                                      final List<Dependency> dependsOn,
                                      final String id,
                                      final String resourceType,
                                      final String resourceName) {
         return new AutoValue_Deployment_Dependency(dependencies == null ? null : copyOf(dependencies),
               dependsOn == null ? null : copyOf(dependsOn), id, resourceType, resourceName);
      }
   }

   @AutoValue
   public abstract static class ContentLink {
      public abstract String uri();

      @Nullable
      public abstract String contentVersion();

      @SerializedNames({"uri", "contentVersion"})
      public static ContentLink create(final String uri, final String contentVersion) {
         return new AutoValue_Deployment_ContentLink(uri, contentVersion);
      }
   }

   @AutoValue
   public abstract static class DeploymentProperties implements Provisionable {
      @Nullable
      public abstract String provisioningState();

      @Nullable
      public abstract String correlationId();

      @Nullable
      public abstract String timestamp();

      @Nullable
      public abstract Map<String, JsonBall> outputs();

      @Nullable
      public abstract List<Provider> providers();

      @Nullable
      public abstract List<Dependency> dependencies();

      @Nullable
      public abstract Map<String, JsonBall> template();

      @Nullable
      public abstract ContentLink templateLink();

      @Nullable
      public abstract Map<String, Value> parameters();

      @Nullable
      public abstract ContentLink parametersLink();

      public abstract String mode();

      // The entries below seem to be dynamic/not documented in the specification
      @Nullable
      public abstract String duration();

      @Nullable
      public abstract List<Map<String, String>> outputResources();

      @SerializedNames({"provisioningState", "correlationId", "timestamp", "outputs", "providers", "dependencies", "template", "templateLink", "parameters", "parametersLink", "mode", "duration", "outputResources"})
      public static DeploymentProperties create(final String provisioningState,
                                                final String correlationId,
                                                final String timestamp,
                                                @Nullable final Map<String, JsonBall> outputs,
                                                final List<Provider> providers,
                                                final List<Dependency> dependencies,
                                                final Map<String, JsonBall> template,
                                                final ContentLink templateLink,
                                                final Map<String, Value> parameters,
                                                final ContentLink parametersLink,
                                                final String mode,
                                                final String duration,
                                                final List<Map<String, String>> outputResources) {
         return new AutoValue_Deployment_DeploymentProperties(provisioningState,
                                                              correlationId,
                                                              timestamp,
                                                              outputs == null ? ImmutableMap.<String, JsonBall>builder().build() : ImmutableMap.copyOf(outputs),
                                                              providers == null ? null : copyOf(providers),
                                                              dependencies == null ? null : copyOf(dependencies),
                                                              template == null ? ImmutableMap.<String, JsonBall>builder().build() : ImmutableMap.copyOf(template),
                                                              templateLink,
                                                              parameters == null ? ImmutableMap.<String, Value>builder().build() : ImmutableMap.copyOf(parameters),
                                                              parametersLink,
                                                              mode,
                                                              duration,
                                                              outputResources == null ? null : copyOf(outputResources));
      }
   }

   /**
    * The ID associated with the template deployment.
    */
   @Nullable
   public abstract String id();

   /**
    * The name associated with the template deployment.
    */
   public abstract String name();

   /**
    * Properties of the deployment.
    */
   @Nullable
   public abstract DeploymentProperties properties();

   @SerializedNames({"id", "name", "properties"})
   public static Deployment create(final String id, final String name, final DeploymentProperties properties) {
      return new AutoValue_Deployment(id, name, properties);
   }
}
