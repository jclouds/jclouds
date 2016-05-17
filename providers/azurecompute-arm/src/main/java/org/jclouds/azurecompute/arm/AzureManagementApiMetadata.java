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
package org.jclouds.azurecompute.arm;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.azurecompute.arm.config.AzureComputeHttpApiModule;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.internal.BaseHttpApiMetadata;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.http.okhttp.config.OkHttpCommandExecutorServiceModule;

/**
 * Implementation of {@link ApiMetadata} for Microsoft Azure Resource Manager REST API
 */
public class AzureManagementApiMetadata extends BaseHttpApiMetadata<AzureComputeApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public AzureManagementApiMetadata() {
      this(new Builder());
   }

   protected AzureManagementApiMetadata(final Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      final Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<AzureComputeApi, Builder> {

      protected Builder() {
         super();

         id("azurecompute-arm")
                 .name("Microsoft Azure Resource Manager REST API")
                 .identityName("Azure Service Principal Application Id")
                 .credentialName("Azure Service Principal Application Password")
                 .endpointName("Resource Manager Endpoint ending in your Subscription Id")
                 .documentation(URI.create("https://msdn.microsoft.com/en-us/library/azure/dn790568.aspx"))
                 .defaultProperties(AzureManagementApiMetadata.defaultProperties())
                 .view(typeToken(ComputeServiceContext.class))
                 .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                         .add(AzureComputeServiceContextModule.class)
                         .add(OAuthModule.class)
                         .add(OkHttpCommandExecutorServiceModule.class)
                         .add(AzureComputeHttpApiModule.class).build());
      }

      @Override
      public AzureManagementApiMetadata build() {
         return new AzureManagementApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
