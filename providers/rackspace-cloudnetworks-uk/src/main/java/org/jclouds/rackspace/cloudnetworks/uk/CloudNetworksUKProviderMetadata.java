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
package org.jclouds.rackspace.cloudnetworks.uk;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.KEYSTONE_VERSION;
import static org.jclouds.openstack.keystone.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.keystone.catalog.config.ServiceCatalogModule;
import org.jclouds.openstack.keystone.catalog.config.ServiceCatalogModule.RegionModule;
import org.jclouds.openstack.neutron.v2.NeutronApiMetadata;
import org.jclouds.openstack.neutron.v2.config.NeutronHttpApiModule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@AutoService(ProviderMetadata.class)
public class CloudNetworksUKProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public CloudNetworksUKProviderMetadata() {
      this(new Builder());
   }

   protected CloudNetworksUKProviderMetadata(Builder builder) {
      super(builder);
   }

   /**
    * @return a {@link Properties} object containing the default provider properties.
    * This returns the credential type, service type, and configured regions.
    */
   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.NETWORK);
      properties.setProperty(KEYSTONE_VERSION, "2");

      properties.setProperty(PROPERTY_REGIONS, "LON");
      properties.setProperty(PROPERTY_REGION + ".LON." + ISO3166_CODES, "GB-SLG");

      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("rackspace-cloudnetworks-uk")
         .name("Rackspace Cloud Networks UK")
         .apiMetadata(new NeutronApiMetadata().toBuilder()
               .identityName("${userName}")
               .credentialName("${apiKey}")
               .defaultEndpoint("https://lon.identity.api.rackspacecloud.com/v2.0/")
               .documentation(URI.create("http://docs.rackspace.com/networks/api/v1/cf-devguide/content/index.html"))
               .endpointName("Rackspace Cloud Identity service URL ending in /v2.0/")
               .version("2.0")
               .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                     .add(CloudIdentityAuthenticationModule.class)
                     .add(ServiceCatalogModule.class)
                     .add(RegionModule.class)
                     .add(NeutronHttpApiModule.class)
                     .build())
               .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/networks"))
         .console(URI.create("https://mycloud.rackspace.co.uk"))
         .linkedServices("rackspace-autoscale-uk", "rackspace-cloudblockstorage-uk",
               "rackspace-clouddatabases-uk", "rackspace-clouddns-uk", "rackspace-cloudidentity",
               "rackspace-cloudloadbalancers-uk", "rackspace-cloudqueues-uk",
               "rackspace-cloudservers-uk")
         .iso3166Codes("GB-SLG")
         .defaultProperties(CloudNetworksUKProviderMetadata.defaultProperties());

      }

      @Override
      public CloudNetworksUKProviderMetadata build() {
         return new CloudNetworksUKProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
