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
package org.jclouds.rackspace.cloudfiles.v1;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import org.jclouds.openstack.swift.v1.SwiftApiMetadata;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.jclouds.openstack.swift.v1.blobstore.config.SignUsingTemporaryUrls;
import org.jclouds.openstack.swift.v1.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.v1.config.SwiftTypeAdapters;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rackspace.cloudfiles.v1.config.CloudFilesHttpApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Cloud Files.
 */
@AutoService(ApiMetadata.class)
public class CloudFilesApiMetadata extends BaseHttpApiMetadata<CloudFilesApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudFilesApiMetadata() {
      this(new Builder());
   }

   protected CloudFilesApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = SwiftApiMetadata.defaultProperties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.OBJECT_STORE);
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<CloudFilesApi, Builder> {

      protected Builder() {
          id("rackspace-cloudfiles")
         .name("Rackspace Cloud Files API")
         .identityName("${userName}")
         .credentialName("${apiKey}")
         .documentation(URI.create("http://docs.rackspace.com/files/api/v1/cf-devguide/content/index.html"))
         .version("1.0")
         .endpointName("Rackspace Cloud Identity service URL ending in /v2.0/")
         .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(CloudFilesApiMetadata.defaultProperties())
         .view(typeToken(RegionScopedBlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(CloudIdentityAuthenticationApiModule.class)
                                     .add(CloudIdentityAuthenticationModule.class)
                                     .add(RegionModule.class)
                                     .add(SwiftTypeAdapters.class)
                                     .add(CloudFilesHttpApiModule.class)
                                     .add(SwiftBlobStoreContextModule.class)
                                     .add(SignUsingTemporaryUrls.class)
                                     .build());
      }

      @Override
      public CloudFilesApiMetadata build() {
         return new CloudFilesApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
