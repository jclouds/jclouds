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
package org.jclouds.oauth.v2.config;

import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.RESOURCE;

import java.util.List;

import javax.inject.Named;

import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.Beta;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Provides all OAuth configuration. Implementations are api-specific.
 */
@Beta
@ImplementedBy(OAuthConfigFactory.OAuthConfigFromProperties.class)
public interface OAuthConfigFactory {

   @AutoValue
   public abstract static class OAuthConfig {
      public abstract List<String> scopes();
      @Nullable public abstract String audience();
      @Nullable public abstract String resource();

      public static OAuthConfig create(List<String> scopes, String audience, String resource) {
         return new AutoValue_OAuthConfigFactory_OAuthConfig(scopes, audience, resource);
      }
   }

   /**
    * Returns the OAuth configuration to be used to authenticate the gicen
    * request.
    */
   OAuthConfig forRequest(HttpRequest input);

   public static class OAuthConfigFromProperties implements OAuthConfigFactory {
      private final OAuthScopes scopes;

      @Inject(optional = true)
      @Named(AUDIENCE)
      private String audience;

      @Inject(optional = true)
      @Named(RESOURCE)
      private String resource;

      @Inject
      OAuthConfigFromProperties(OAuthScopes scopes) {
         this.scopes = scopes;
      }

      @Override
      public OAuthConfig forRequest(HttpRequest input) {
         return OAuthConfig.create(scopes.forRequest(input), audience, resource);
      }
   }
}
