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
package org.jclouds.hpcloud.objectstorage;

import static org.jclouds.hpcloud.objectstorage.config.HPCloudObjectStorageHttpApiModule.HPCloudObjectStorageEndpointModule;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.net.URI;
import java.util.Properties;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.hpcloud.objectstorage.blobstore.HPCloudObjectStorageBlobRequestSigner;
import org.jclouds.hpcloud.objectstorage.blobstore.config.HPCloudObjectStorageBlobStoreContextModule;
import org.jclouds.hpcloud.objectstorage.config.HPCloudObjectStorageHttpApiModule;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.keystone.v2_0.config.AuthenticationApiModule;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToAdminURISupplier;
import org.jclouds.openstack.swift.SwiftKeystoneApiMetadata;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule;
import org.jclouds.openstack.swift.extensions.KeystoneTemporaryUrlKeyApi;
import org.jclouds.openstack.swift.extensions.TemporaryUrlKeyApi;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.inject.name.Named;
/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for HP Cloud Services Object Storage
 */
public class HPCloudObjectStorageApiMetadata extends SwiftKeystoneApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public HPCloudObjectStorageApiMetadata() {
      this(new Builder());
   }

   protected HPCloudObjectStorageApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = SwiftKeystoneApiMetadata.defaultProperties();
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.API_ACCESS_KEY_CREDENTIALS);
      return properties;
   }

   public static class Builder extends SwiftKeystoneApiMetadata.Builder<HPCloudObjectStorageApi, Builder> {
      protected Builder() {
         super(HPCloudObjectStorageApi.class);
         id("hpcloud-objectstorage")
         .endpointName("identity service url ending in /v2.0/")
         .defaultEndpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/")
         .name("HP Cloud Services Object Storage API")
         .documentation(URI.create("https://build.hpcloud.com/object-storage/api"))
         .defaultProperties(HPCloudObjectStorageApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(AuthenticationApiModule.class)
                                     .add(HPCloudObjectStorageEndpointModule.class)
                                     .add(IgnoreRegionVersionsModule.class)
                                     .add(HPCloudObjectStorageHttpApiModule.class)
                                     .add(HPCloudObjectStorageBlobStoreContextModule.class)
                                     .add(HPCloudObjectStorageTemporaryUrlExtensionModule.class).build());
      }

      @Override
      public HPCloudObjectStorageApiMetadata build() {
         return new HPCloudObjectStorageApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

   /**
    * Ensures keystone auth is used instead of swift auth
    */
   public static class HPCloudObjectStorageTemporaryUrlExtensionModule extends
         TemporaryUrlExtensionModule<HPCloudObjectStorageApi> {
      @Override
      protected void bindRequestSigner() {
         bind(BlobRequestSigner.class).to(HPCloudObjectStorageBlobRequestSigner.class);
      }
      @Override
      protected void bindTemporaryUrlKeyApi() {
         bindHttpApi(binder(), KeystoneTemporaryUrlKeyApi.class);
         bind(TemporaryUrlKeyApi.class).to(KeystoneTemporaryUrlKeyApi.class);
      }
   }

   /**
    * Use this when the keystone configuration incorrectly mismatches api
    * versions across regions.
    */
   public static class IgnoreRegionVersionsModule extends RegionModule {

      @Override
      protected RegionIdToURISupplier provideRegionIdToURISupplierForApiVersion(
            @Named(SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
            RegionIdToURISupplier.Factory factory) {
         return factory.createForApiTypeAndVersion(serviceType, null);
      }

      @Override
      protected RegionIdToAdminURISupplier provideRegionIdToAdminURISupplierForApiVersion(
            @Named(SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
            RegionIdToAdminURISupplier.Factory factory) {
         return factory.createForApiTypeAndVersion(serviceType, null);
      }
   }
}
