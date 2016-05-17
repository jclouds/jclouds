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


import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.IMAGE_PUBLISHERS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.RESOURCE_GROUP_NAME;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_POLL_INITIAL_PERIOD;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_POLL_MAX_PERIOD;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TCP_RULE_FORMAT;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TCP_RULE_REGEXP;

import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.DEFAULT_IMAGE_LOGIN;

import static org.jclouds.oauth.v2.config.CredentialType.CLIENT_CREDENTIALS_SECRET;
import static org.jclouds.oauth.v2.config.OAuthProperties.RESOURCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;

import java.net.URI;
import java.util.Properties;
import org.jclouds.azurecompute.arm.domain.Region;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.compute.config.ComputeServiceProperties;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

import com.google.auto.service.AutoService;

@AutoService(ProviderMetadata.class)
public class AzureComputeProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public AzureComputeProviderMetadata() {
      super(builder());
   }

   public static Properties defaultProperties() {
      final Properties properties = AzureManagementApiMetadata.defaultProperties();
      properties.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      properties.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      properties.setProperty(OPERATION_TIMEOUT, "46000000");
      properties.setProperty(OPERATION_POLL_INITIAL_PERIOD, "5");
      properties.setProperty(OPERATION_POLL_MAX_PERIOD, "15");
      properties.setProperty(TCP_RULE_FORMAT, "tcp_%s-%s");
      properties.setProperty(TCP_RULE_REGEXP, "tcp_\\d{1,5}-\\d{1,5}");
      properties.put(RESOURCE, "https://management.azure.com/");
      properties.put(CREDENTIAL_TYPE, CLIENT_CREDENTIALS_SECRET.toString());
      properties.put(RESOURCE_GROUP_NAME, "jcloudsgroup");
      properties.put(IMAGE_PUBLISHERS, "Microsoft.WindowsAzure.Compute, MicrosoftWindowsServer, Canonical");
      properties.put(DEFAULT_IMAGE_LOGIN, "jclouds:Password1!");
      properties.put(TIMEOUT_NODE_TERMINATED, 60 * 10 * 1000);
      return properties;
   }

   public AzureComputeProviderMetadata(final Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         super();
         id("azurecompute-arm")
                 .name("Azure Resource Management")
                 .apiMetadata(new AzureManagementApiMetadata())
                 .endpoint("https://management.azure.com/subscriptions/SUBSCRIPTION_ID")
                 .homepage(URI.create("https://www.windowsazure.com/"))
                 .console(URI.create("https://windows.azure.com/default.aspx"))
                 .linkedServices("azureblob")
                 .iso3166Codes(Region.iso3166Codes())
                 .defaultProperties(AzureComputeProviderMetadata.defaultProperties());
      }

      @Override
      public AzureComputeProviderMetadata build() {
         return new AzureComputeProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(final ProviderMetadata providerMetadata) {
         super.fromProviderMetadata(providerMetadata);
         return this;
      }
   }
}
