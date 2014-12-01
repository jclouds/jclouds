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
package org.jclouds.cloudfiles;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.cloudfiles.blobstore.config.CloudFilesBlobStoreContextModule;
import org.jclouds.cloudfiles.config.CloudFilesHttpApiModule;
import org.jclouds.cloudfiles.config.CloudFilesHttpApiModule.StorageAndCDNManagementEndpointModule;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.openstack.swift.blobstore.SwiftBlobSigner;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

@AutoService(ApiMetadata.class)
public class CloudFilesApiMetadata extends SwiftApiMetadata {

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
      return properties;
   }

   public static class Builder extends SwiftApiMetadata.Builder<CloudFilesClient, Builder> {

      protected Builder() {
         super(CloudFilesClient.class);
         id("cloudfiles")
         .name("Rackspace Cloud Files API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("http://docs.rackspacecloud.com/files/api/v1/cfdevguide_d5/content/ch01.html"))
         .defaultProperties(CloudFilesApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(StorageAndCDNManagementEndpointModule.class)
                                     .add(CloudFilesHttpApiModule.class)
                                     .add(CloudFilesBlobStoreContextModule.class)
                                     .add(CloudFilesTemporaryUrlExtensionModule.class).build());
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

   public static class CloudFilesTemporaryUrlExtensionModule extends TemporaryUrlExtensionModule<CloudFilesClient> {
      @Override
      protected void bindRequestSigner() {
         bind(BlobRequestSigner.class).to(new TypeLiteral<SwiftBlobSigner<CloudFilesClient>>() {
         });
      }
   }
}
