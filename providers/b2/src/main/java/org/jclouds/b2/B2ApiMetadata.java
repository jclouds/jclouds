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
package org.jclouds.b2;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.b2.blobstore.config.B2BlobStoreContextModule;
import org.jclouds.b2.config.B2HttpApiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public final class B2ApiMetadata extends BaseHttpApiMetadata {
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public B2ApiMetadata() {
      this(new Builder());
   }

   protected B2ApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX, "X-Bz-Info-");
      properties.setProperty(Constants.PROPERTY_SESSION_INTERVAL, String.valueOf(TimeUnit.HOURS.toSeconds(1)));
      properties.setProperty(Constants.PROPERTY_IDEMPOTENT_METHODS, "DELETE,GET,HEAD,OPTIONS,POST,PUT");
      properties.setProperty(Constants.PROPERTY_RETRY_DELAY_START, String.valueOf(TimeUnit.SECONDS.toMillis(1)));
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<B2Api, Builder> {

      protected Builder() {
         super(B2Api.class);
         id("b2")
                 .name("Backblaze B2 API")
                 .identityName("Account Id")
                 .credentialName("Application Key")
                 .documentation(URI.create("https://www.backblaze.com/b2/docs/"))
                 .defaultEndpoint("https://api.backblazeb2.com/")
                 .defaultProperties(B2ApiMetadata.defaultProperties())
                 .view(typeToken(BlobStoreContext.class))
                 .defaultModules(ImmutableSet.<Class<? extends Module>>of(
                         B2HttpApiModule.class,
                         B2BlobStoreContextModule.class));
      }

      @Override
      public B2ApiMetadata build() {
         return new B2ApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         return this;
      }
   }
}
