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

import static org.jclouds.Constants.PROPERTY_MAX_RATE_LIMIT_WAIT;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.API_VERSION_PREFIX;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.DEFAULT_SUBNET_ADDRESS_PREFIX;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.DEFAULT_VNET_ADDRESS_SPACE_PREFIX;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.IMAGE_PUBLISHERS;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.compute.config.ComputeServiceProperties.IMAGE_AUTHENTICATE_SUDO;
import static org.jclouds.compute.config.ComputeServiceProperties.IMAGE_LOGIN_USER;
import static org.jclouds.compute.config.ComputeServiceProperties.POLL_INITIAL_PERIOD;
import static org.jclouds.compute.config.ComputeServiceProperties.POLL_MAX_PERIOD;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_DELIMITER;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_PREFIX;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.oauth.v2.config.CredentialType.CLIENT_CREDENTIALS_SECRET;
import static org.jclouds.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;
import static org.jclouds.oauth.v2.config.OAuthProperties.RESOURCE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.azurecompute.arm.domain.Region;
import org.jclouds.azurecompute.arm.features.AvailabilitySetApi;
import org.jclouds.azurecompute.arm.features.DeploymentApi;
import org.jclouds.azurecompute.arm.features.ImageApi;
import org.jclouds.azurecompute.arm.features.LoadBalancerApi;
import org.jclouds.azurecompute.arm.features.LocationApi;
import org.jclouds.azurecompute.arm.features.DiskApi;
import org.jclouds.azurecompute.arm.features.MetricDefinitionsApi;
import org.jclouds.azurecompute.arm.features.MetricsApi;
import org.jclouds.azurecompute.arm.features.NetworkInterfaceCardApi;
import org.jclouds.azurecompute.arm.features.NetworkSecurityGroupApi;
import org.jclouds.azurecompute.arm.features.NetworkSecurityRuleApi;
import org.jclouds.azurecompute.arm.features.OSImageApi;
import org.jclouds.azurecompute.arm.features.PublicIPAddressApi;
import org.jclouds.azurecompute.arm.features.ResourceGroupApi;
import org.jclouds.azurecompute.arm.features.ResourceProviderApi;
import org.jclouds.azurecompute.arm.features.StorageAccountApi;
import org.jclouds.azurecompute.arm.features.SubnetApi;
import org.jclouds.azurecompute.arm.features.VMSizeApi;
import org.jclouds.azurecompute.arm.features.VirtualMachineApi;
import org.jclouds.azurecompute.arm.features.VirtualNetworkApi;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

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
      properties.put(POLL_INITIAL_PERIOD, 1000);
      properties.put(POLL_MAX_PERIOD, 15000);
      properties.put(OPERATION_TIMEOUT, 46000000);
      properties.put(TIMEOUT_NODE_TERMINATED, 60 * 10 * 1000);
      // Default max wait in rate limit: 5m30s
      properties.put(PROPERTY_MAX_RATE_LIMIT_WAIT, 330000);
      properties.put(RESOURCE, "https://management.azure.com/");
      properties.put(CREDENTIAL_TYPE, CLIENT_CREDENTIALS_SECRET.toString());
      properties.put(DEFAULT_VNET_ADDRESS_SPACE_PREFIX, "10.0.0.0/16");
      properties.put(DEFAULT_SUBNET_ADDRESS_PREFIX, "10.0.0.0/24");
      properties.put(RESOURCENAME_PREFIX, "jclouds");
      properties.put(RESOURCENAME_DELIMITER, "-");
      properties.put(IMAGE_PUBLISHERS, "Canonical,RedHat");
      // Default credentials for all images
      properties.put(IMAGE_LOGIN_USER, "jclouds:Password12345!");
      properties.put(IMAGE_AUTHENTICATE_SUDO, "true");
      properties.put(TEMPLATE, "imageNameMatches=UbuntuServer,osVersionMatches=1[456]\\.[01][04](\\.[0-9])?-LTS");
      // Api versions used in each API
      properties.put(API_VERSION_PREFIX + DeploymentApi.class.getSimpleName(), "2016-02-01");
      properties.put(API_VERSION_PREFIX + LocationApi.class.getSimpleName(), "2015-11-01");
      properties.put(API_VERSION_PREFIX + NetworkInterfaceCardApi.class.getSimpleName(), "2017-03-01");
      properties.put(API_VERSION_PREFIX + NetworkSecurityGroupApi.class.getSimpleName(), "2016-03-30");
      properties.put(API_VERSION_PREFIX + NetworkSecurityRuleApi.class.getSimpleName(), "2016-03-30");
      properties.put(API_VERSION_PREFIX + OSImageApi.class.getSimpleName(), "2015-06-15");
      properties.put(API_VERSION_PREFIX + PublicIPAddressApi.class.getSimpleName(), "2015-06-15");
      properties.put(API_VERSION_PREFIX + ResourceGroupApi.class.getSimpleName(), "2015-01-01");
      properties.put(API_VERSION_PREFIX + ResourceProviderApi.class.getSimpleName(), "2015-01-01");
      properties.put(API_VERSION_PREFIX + StorageAccountApi.class.getSimpleName(), "2015-06-15");
      properties.put(API_VERSION_PREFIX + SubnetApi.class.getSimpleName(), "2017-03-01");
      properties.put(API_VERSION_PREFIX + VirtualNetworkApi.class.getSimpleName(), "2015-06-15");
      properties.put(API_VERSION_PREFIX + VMSizeApi.class.getSimpleName(), "2015-06-15");
      properties.put(API_VERSION_PREFIX + VirtualMachineApi.class.getSimpleName(), "2016-04-30-preview");
      properties.put(API_VERSION_PREFIX + LoadBalancerApi.class.getSimpleName(), "2016-03-30");
      properties.put(API_VERSION_PREFIX + AvailabilitySetApi.class.getSimpleName(), "2016-04-30-preview");
      properties.put(API_VERSION_PREFIX + DiskApi.class.getSimpleName(), "2017-03-30");
      properties.put(API_VERSION_PREFIX + ImageApi.class.getSimpleName(), "2016-04-30-preview");
      properties.put(API_VERSION_PREFIX + MetricDefinitionsApi.class.getSimpleName(), "2017-05-01-preview");
      properties.put(API_VERSION_PREFIX + MetricsApi.class.getSimpleName(), "2016-09-01");
      
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
