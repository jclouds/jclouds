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

import static org.jclouds.oauth.v2.config.CredentialType.P12_PRIVATE_KEY_CREDENTIALS;
import static org.jclouds.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.net.URI;
import java.security.PrivateKey;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.oauth.v2.AuthorizationApi;
import org.jclouds.oauth.v2.filters.BearerTokenFromCredentials;
import org.jclouds.oauth.v2.filters.JWTBearerTokenFlow;
import org.jclouds.oauth.v2.filters.OAuthFilter;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public final class OAuthModule extends AbstractModule {

   @Override protected void configure() {
      bindHttpApi(binder(), AuthorizationApi.class);
      bind(CredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
      bind(new TypeLiteral<Supplier<PrivateKey>>() {}).annotatedWith(Authorization.class).to(PrivateKeySupplier.class);
   }

   @Provides
   @Authorization
   protected Supplier<URI> oauthEndpoint(@javax.inject.Named("oauth.endpoint") String endpoint) {
      return Suppliers.ofInstance(URI.create(endpoint));
   }

   @Singleton
   public static class CredentialTypeFromPropertyOrDefault implements Provider<CredentialType> {
      @Inject(optional = true)
      @Named(CREDENTIAL_TYPE)
      String credentialType = P12_PRIVATE_KEY_CREDENTIALS.toString();

      @Override
      public CredentialType get() {
         return CredentialType.fromValue(credentialType);
      }
   }

   @Provides
   @Singleton
   protected OAuthFilter authenticationFilterForCredentialType(CredentialType credentialType,
                                                               JWTBearerTokenFlow serviceAccountAuth,
                                                               BearerTokenFromCredentials bearerTokenAuth) {
      switch (credentialType) {
         case P12_PRIVATE_KEY_CREDENTIALS:
            return serviceAccountAuth;
         case BEARER_TOKEN_CREDENTIALS:
            return bearerTokenAuth;
         default:
            throw new IllegalArgumentException("Unsupported credential type: " + credentialType);
      }
   }

}
