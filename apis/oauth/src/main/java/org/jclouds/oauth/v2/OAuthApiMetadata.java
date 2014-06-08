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
package org.jclouds.oauth.v2;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;

import java.net.URI;
import java.util.Properties;

import org.jclouds.oauth.v2.config.OAuthHttpApiModule;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for OAuth 2 API
 */
public class OAuthApiMetadata extends BaseHttpApiMetadata<OAuthApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public OAuthApiMetadata() {
      this(new Builder());
   }

   protected OAuthApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.put(SIGNATURE_OR_MAC_ALGORITHM, "RS256");
      properties.put(PROPERTY_SESSION_INTERVAL, 3600);
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<OAuthApi, Builder> {

      protected Builder() {
         id("oauth")
         .name("OAuth API")
         .identityName("service_account")
         .credentialName("service_key")
         .documentation(URI.create("TODO"))
         .version("2")
         .defaultProperties(OAuthApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(OAuthModule.class, OAuthHttpApiModule.class));
      }

      @Override
      public OAuthApiMetadata build() {
         return new OAuthApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
