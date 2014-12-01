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
package org.jclouds.hpcloud.blockstorage;

import org.jclouds.openstack.cinder.v1.CinderApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.REQUIRES_TENANT;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import com.google.auto.service.AutoService;

/**
 * Implementation of {@link ProviderMetadata} for HP Cloud Block Storage service.
 * 
 */
@AutoService(ProviderMetadata.class)
public class HPCloudBlockStorageProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public HPCloudBlockStorageProviderMetadata() {
      super(builder());
   }

   public HPCloudBlockStorageProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(REQUIRES_TENANT, "true");
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.API_ACCESS_KEY_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.BLOCK_STORAGE);

      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("hpcloud-blockstorage")
         .name("HP Cloud Block Storage")
         .apiMetadata(new CinderApiMetadata().toBuilder()
               .identityName("${tenantName:accessKey}")
               .credentialName("${secret}")
               .defaultEndpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/")
               .endpointName("identity service url ending in /v2.0/")
               .documentation(URI.create("https://docs.hpcloud.com/api/v13/block-storage/"))
               .version("1.0")
               .build())
         .homepage(URI.create("https://horizon.hpcloud.com/project/volumes/"))
         .console(URI.create("https://horizon.hpcloud.com"))
         .linkedServices("hpcloud-compute", "hpcloud-objectstorage")
         .iso3166Codes("US-NV", "US-VA")
         .endpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/")
         .defaultProperties(HPCloudBlockStorageProviderMetadata.defaultProperties());
      }

      @Override
      public HPCloudBlockStorageProviderMetadata build() {
         return new HPCloudBlockStorageProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }

}
