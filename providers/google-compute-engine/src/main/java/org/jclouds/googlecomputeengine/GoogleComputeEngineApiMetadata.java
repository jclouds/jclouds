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
import static org.jclouds.googlecloud.config.GoogleCloudProperties.PROJECT_NAME;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineProperties.IMAGE_PROJECTS;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineProperties.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineProperties.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecomputeengine.compute.config.GoogleComputeEngineServiceContextModule;
import org.jclouds.googlecomputeengine.config.GoogleComputeEngineHttpApiModule;
import org.jclouds.googlecomputeengine.config.GoogleComputeEngineParserModule;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

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
      properties.put(JWS_ALG, "RS256");
      properties.put(PROPERTY_SESSION_INTERVAL, 3600);
      properties.put(OPERATION_COMPLETE_INTERVAL, 500);
      properties.put(OPERATION_COMPLETE_TIMEOUT, 600000);
      properties.put(TEMPLATE, "osFamily=DEBIAN,osVersionMatches=7\\..*,locationId=us-central1-a");
      properties.put(PROJECT_NAME, ""); // Defaulting to empty helps avoid temptation for optional inject!
      properties.put(IMAGE_PROJECTS, "centos-cloud,debian-cloud,rhel-cloud,suse-cloud,opensuse-cloud,gce-nvme,coreos-cloud,ubuntu-os-cloud,windows-cloud");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<GoogleComputeEngineApi, Builder> {

      protected Builder() {
         id("google-compute-engine")
           .name("Google Compute Engine Api")
           .identityName(CurrentProject.ClientEmail.DESCRIPTION)
           .credentialName("PEM encoded P12 private key associated with client_email")
           .documentation(URI.create("https://developers.google.com/compute/docs"))
           .version("v1")
           .defaultEndpoint("https://www.googleapis.com/compute/v1")
           .defaultProperties(GoogleComputeEngineApiMetadata.defaultProperties())
           .view(typeToken(ComputeServiceContext.class))
           .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                   .add(GoogleComputeEngineHttpApiModule.class)
                   .add(GoogleComputeEngineParserModule.class)
                   .add(OAuthModule.class)
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
