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
package org.jclouds.googlecomputeengine;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.GCE_PROVIDER_NAME;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.googlecomputeengine.compute.config.GoogleComputeEngineServiceContextModule;
import org.jclouds.googlecomputeengine.config.GoogleComputeEngineHttpApiModule;
import org.jclouds.googlecomputeengine.config.GoogleComputeEngineParserModule;
import org.jclouds.googlecomputeengine.config.OAuthModuleWithoutTypeAdapters;
import org.jclouds.oauth.v2.config.OAuthAuthenticationModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for GoogleCompute v1beta16 API
 *
 * @author David Alves
 */
public class GoogleComputeEngineApiMetadata extends BaseHttpApiMetadata<GoogleComputeEngineApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public GoogleComputeEngineApiMetadata() {
      this(new Builder());
   }

   protected GoogleComputeEngineApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.put("oauth.endpoint", "https://accounts.google.com/o/oauth2/token");
      properties.put(AUDIENCE, "https://accounts.google.com/o/oauth2/token");
      properties.put(SIGNATURE_OR_MAC_ALGORITHM, "RS256");
      properties.put(PROPERTY_SESSION_INTERVAL, 3600);
      properties.setProperty(TEMPLATE, "osFamily=GCEL,osVersionMatches=1[012].[01][04],locationId=us-central1-a," +
              "loginUser=jclouds");
      properties.put(OPERATION_COMPLETE_INTERVAL, 500);
      properties.put(OPERATION_COMPLETE_TIMEOUT, 600000);
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<GoogleComputeEngineApi, Builder> {

      protected Builder() {
         id(GCE_PROVIDER_NAME)
                 .name("Google Compute Engine Api")
                 .identityName("Email associated with the Google API client_id")
                 .credentialName("Private key literal associated with the Google API client_id")
                 .documentation(URI.create("https://developers.google.com/compute/docs"))
                 .version("v1beta16")
                 .defaultEndpoint("https://www.googleapis.com/compute/v1beta16")
                 .defaultProperties(GoogleComputeEngineApiMetadata.defaultProperties())
                 .view(typeToken(ComputeServiceContext.class))
                 .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                         .add(GoogleComputeEngineHttpApiModule.class)
                         .add(GoogleComputeEngineParserModule.class)
                         .add(OAuthAuthenticationModule.class)
                         .add(OAuthModuleWithoutTypeAdapters.class)
                         .add(GoogleComputeEngineServiceContextModule.class)
                         .build());
      }

      @Override
      public GoogleComputeEngineApiMetadata build() {
         return new GoogleComputeEngineApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
