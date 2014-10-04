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
package org.jclouds.azureblob;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.azureblob.blobstore.config.AzureBlobStoreContextModule;
import org.jclouds.azureblob.config.AzureBlobHttpApiModule;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class AzureBlobApiMetadata extends BaseHttpApiMetadata {

   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public AzureBlobApiMetadata() {
      this(builder());
   }

   protected AzureBlobApiMetadata(Builder builder) {
      super(builder);
   }
  
   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-ms-meta-");
      return properties;
   }
   
   public static class Builder extends BaseHttpApiMetadata.Builder<AzureBlobClient, Builder> {
      protected Builder() {
         super(AzureBlobClient.class);
         id("azureblob")
         .name("Microsoft Azure Blob Service API")
         .identityName("Account Name")
         .credentialName("Access Key")
         .version("2012-02-12")
         .defaultEndpoint("https://${jclouds.identity}.blob.core.windows.net")
         .documentation(URI.create("http://msdn.microsoft.com/en-us/library/dd135733.aspx"))
         .defaultProperties(AzureBlobApiMetadata.defaultProperties())
         .view(typeToken(BlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(AzureBlobHttpApiModule.class, AzureBlobStoreContextModule.class));
      }
      
      @Override
      public AzureBlobApiMetadata build() {
         return new AzureBlobApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
