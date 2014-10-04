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
package org.jclouds.hpcloud.objectstorage.config;

import static org.jclouds.util.Suppliers2.getLastValueInMap;
import static org.jclouds.util.Suppliers2.getValueInMapOrNull;

import java.net.URI;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageApi;
import org.jclouds.hpcloud.services.HPExtensionCDN;
import org.jclouds.hpcloud.services.HPExtensionServiceType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.reference.LocationConstants;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.services.ServiceType;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.config.SwiftHttpApiModule;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.Scopes;

@ConfiguresHttpApi
public class HPCloudObjectStorageHttpApiModule extends SwiftHttpApiModule<HPCloudObjectStorageApi> {

   public HPCloudObjectStorageHttpApiModule() {
      super(HPCloudObjectStorageApi.class);
   }

   protected void bindResolvedClientsToCommonSwift() {
      bind(CommonSwiftClient.class).to(HPCloudObjectStorageApi.class).in(Scopes.SINGLETON);
   }

   private static Supplier<URI> getUriSupplier(String serviceType, String apiVersion,  RegionIdToURISupplier.Factory factory, String region) {
      Supplier<Map<String, Supplier<URI>>> endpointsSupplier = factory.createForApiTypeAndVersion(serviceType, apiVersion);

      if (region.isEmpty()) {
         return getLastValueInMap(endpointsSupplier);
      } else {
         return getValueInMapOrNull(endpointsSupplier, region);
      }
   }

   @Provides
   @Singleton
   @HPExtensionCDN
   @Nullable
   protected Supplier<URI> provideCDNUrl(RegionIdToURISupplier.Factory factory,
                                         @ApiVersion String apiVersion,
                                         @Named(LocationConstants.PROPERTY_REGION) String region) {

      return getUriSupplier(HPExtensionServiceType.CDN, apiVersion, factory, region);
   }

   // Ignores requested apiVersion to work around versionId issue in HP endpoints
   public static class HPCloudObjectStorageEndpointModule extends KeystoneAuthenticationModule {
      @Provides
      @Singleton
      @Storage
      @Nullable
      protected Supplier<URI> provideStorageUrl(RegionIdToURISupplier.Factory factory,
                                                @ApiVersion String apiVersion,
                                                @Named(LocationConstants.PROPERTY_REGION) String region) {

         return getUriSupplier(ServiceType.OBJECT_STORE, null, factory, region);

      }
   }

}
