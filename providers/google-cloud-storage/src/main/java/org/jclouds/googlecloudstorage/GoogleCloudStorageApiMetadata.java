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
package org.jclouds.googlecloudstorage;

import static org.jclouds.Constants.PROPERTY_IDEMPOTENT_METHODS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecloudstorage.blobstore.config.GoogleCloudStorageBlobStoreContextModule;
import org.jclouds.googlecloudstorage.config.GoogleCloudStorageHttpApiModule;
import org.jclouds.googlecloudstorage.config.GoogleCloudStorageParserModule;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class GoogleCloudStorageApiMetadata extends BaseHttpApiMetadata<GoogleCloudStorageApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public GoogleCloudStorageApiMetadata() {
      this(new Builder());
   }

   protected GoogleCloudStorageApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.put("oauth.endpoint", "https://accounts.google.com/o/oauth2/token");
      properties.put(AUDIENCE, "https://accounts.google.com/o/oauth2/token");
      properties.put(JWS_ALG, "RS256");
      properties.put(PROPERTY_SESSION_INTERVAL, 3600);
      properties.put(OPERATION_COMPLETE_INTERVAL, 2000);
      properties.put(OPERATION_COMPLETE_TIMEOUT, 600000);
      properties.setProperty(PROPERTY_IDEMPOTENT_METHODS, "DELETE,GET,HEAD,OPTIONS,POST,PUT");
      // bucket operations have a longer timeout
      properties.setProperty(Constants.PROPERTY_RETRY_DELAY_START, String.valueOf(TimeUnit.SECONDS.toMillis(1)));
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<GoogleCloudStorageApi, Builder> {
      protected Builder() {
         id("google-cloud-storage")
         .name("Google Cloud Storage Api")
         .identityName(CurrentProject.ClientEmail.DESCRIPTION)
         .credentialName("PEM encoded P12 private key associated with client_email")
         .documentation(URI.create("https://developers.google.com/storage/docs/json_api"))
         .version("v1")
         .defaultEndpoint("https://www.googleapis.com")
         .defaultProperties(GoogleCloudStorageApiMetadata.defaultProperties())
         .view(typeToken(BlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>> builder()
                 .add(GoogleCloudStorageParserModule.class)
                 .add(OAuthModule.class)
                 .add(GoogleCloudStorageHttpApiModule.class)
                 .add(GoogleCloudStorageBlobStoreContextModule.class).build());
      }

      @Override
      public GoogleCloudStorageApiMetadata build() {
         return new GoogleCloudStorageApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
