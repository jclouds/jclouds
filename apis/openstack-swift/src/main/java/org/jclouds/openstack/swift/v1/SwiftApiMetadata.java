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
package org.jclouds.openstack.swift.v1;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.jclouds.openstack.swift.v1.blobstore.config.SignUsingTemporaryUrls;
import org.jclouds.openstack.swift.v1.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.v1.config.SwiftAuthenticationModule;
import org.jclouds.openstack.swift.v1.config.SwiftHttpApiModule;
import org.jclouds.openstack.swift.v1.config.SwiftTypeAdapters;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@AutoService(ApiMetadata.class)
public class SwiftApiMetadata extends BaseHttpApiMetadata<SwiftApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public SwiftApiMetadata() {
      this(new Builder());
   }

   protected SwiftApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.OBJECT_STORE);
      // Can alternatively be set to "tempAuthCredentials"
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<SwiftApi, Builder> {

      protected Builder() {
          id("openstack-swift")
         .name("OpenStack Swift API")
         .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
         .credentialName("${password}")
         .documentation(URI.create("http://docs.openstack.org/api/openstack-object-storage/1.0/content/ch_object-storage-dev-overview.html"))
         .version("1")
         .endpointName("Keystone base url ending in /v2.0/ or TempAuth url ending in auth/v1.0/")
         .defaultEndpoint("http://localhost:5000/v2.0/")
         .defaultProperties(SwiftApiMetadata.defaultProperties())
         .view(typeToken(RegionScopedBlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(SwiftAuthenticationModule.class)
                                     .add(RegionModule.class)
                                     .add(SwiftTypeAdapters.class)
                                     .add(SwiftHttpApiModule.class)
                                     .add(SwiftBlobStoreContextModule.class)
                                     .add(SignUsingTemporaryUrls.class).build());
      }

      @Override
      public SwiftApiMetadata build() {
         return new SwiftApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
