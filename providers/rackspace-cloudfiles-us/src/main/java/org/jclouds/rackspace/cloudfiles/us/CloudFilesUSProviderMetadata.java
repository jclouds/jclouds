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
package org.jclouds.rackspace.cloudfiles.us;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.jclouds.openstack.swift.v1.blobstore.config.SignUsingTemporaryUrls;
import org.jclouds.openstack.swift.v1.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.v1.config.SwiftTypeAdapters;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApiMetadata;
import org.jclouds.rackspace.cloudfiles.v1.config.CloudFilesHttpApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ProviderMetadata} for Rackspace Cloud Files US regions.
 */
@AutoService(ProviderMetadata.class)
public class CloudFilesUSProviderMetadata extends BaseProviderMetadata {
   
   /**
    * @return The Builder object.
    */
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   /**
    * Provider constructor.
    */
   public CloudFilesUSProviderMetadata() {
      this(new Builder());
   }

   /**
    * @param builder the Builder for the provider.
    */
   protected CloudFilesUSProviderMetadata(Builder builder) {
      super(builder);
   }

   /**
    * @return a {@link Properties} object containing the default provider properties.
    */
   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.OBJECT_STORE); 

      properties.setProperty(PROPERTY_REGIONS, "ORD,DFW,IAD,SYD,HKG");
      properties.setProperty(PROPERTY_REGION + ".ORD." + ISO3166_CODES, "US-IL");
      properties.setProperty(PROPERTY_REGION + ".DFW." + ISO3166_CODES, "US-TX");
      properties.setProperty(PROPERTY_REGION + ".IAD." + ISO3166_CODES, "US-VA");
      properties.setProperty(PROPERTY_REGION + ".SYD." + ISO3166_CODES, "AU-NSW");
      properties.setProperty(PROPERTY_REGION + ".HKG." + ISO3166_CODES, "HK");

      return properties;
   }

   /**
    * Builder pattern class.
    */
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("rackspace-cloudfiles-us")
         .name("Rackspace Cloud Files US")
         .apiMetadata(new CloudFilesApiMetadata().toBuilder()
               .identityName("${userName}")
               .credentialName("${apiKey}")
               .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
               .documentation(URI.create("http://docs.rackspace.com/files/api/v1/cf-devguide/content/index.html"))
               .endpointName("Rackspace Cloud Identity service URL ending in /v2.0/")
               .version("1.0")
               .view(typeToken(RegionScopedBlobStoreContext.class))
               .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                     .add(CloudIdentityAuthenticationApiModule.class)
                     .add(CloudIdentityAuthenticationModule.class)
                     .add(RegionModule.class)
                     .add(SwiftTypeAdapters.class)
                     .add(CloudFilesHttpApiModule.class)
                     .add(SwiftBlobStoreContextModule.class)
                     .add(SignUsingTemporaryUrls.class)
                     .build())
               .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/files"))
         .console(URI.create("https://mycloud.rackspace.com"))
         .linkedServices("rackspace-autoscale-us", "rackspace-cloudblockstorage-us",
                         "rackspace-clouddatabases-us", "rackspace-clouddns-us",
                         "rackspace-cloudidentity", "rackspace-cloudloadbalancers-us",
                         "rackspace-cloudqueues-us")
         .iso3166Codes("US-IL", "US-TX", "US-VA", "AU-NSW", "HK")
         .defaultProperties(CloudFilesUSProviderMetadata.defaultProperties());
         
      }

      @Override
      public CloudFilesUSProviderMetadata build() {
         return new CloudFilesUSProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
