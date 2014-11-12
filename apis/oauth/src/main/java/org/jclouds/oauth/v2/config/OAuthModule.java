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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.oauth.v2.JWSAlgorithms.NONE;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;

import java.security.PrivateKey;

import org.jclouds.http.HttpRequest;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.filters.BearerTokenAuthenticator;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.oauth.v2.functions.BuildTokenRequest;
import org.jclouds.oauth.v2.functions.FetchToken;
import org.jclouds.oauth.v2.functions.PrivateKeySupplier;
import org.jclouds.oauth.v2.functions.SignOrProduceMacForToken;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

public class OAuthModule extends AbstractModule {

   @Override protected void configure() {
      bind(CredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
      bind(new TypeLiteral<Function<HttpRequest, TokenRequest>>() {}).to(BuildTokenRequest.class);
      bind(new TypeLiteral<Function<TokenRequest, Token>>() {}).to(FetchToken.class);
      bind(new TypeLiteral<Supplier<PrivateKey>>() {}).annotatedWith(OAuth.class).to(PrivateKeySupplier.class);
   }

   /**
    * Provides a cache for tokens. Cache is time based and by default expires after 59 minutes 
    * (the maximum time a token is valid is 60 minutes).
    * This cache and expiry period is system-wide and does not attend to per-instance expiry time
    * (e.g. "expires_in" from Google Compute -- which is set to the standard 3600 seconds).
    */
   // NB: If per-instance expiry time is required, significant refactoring will be needed.
   @Provides
   @Singleton
   public LoadingCache<TokenRequest, Token> provideAccessCache(Function<TokenRequest, Token> getAccess,
                                                               @Named(PROPERTY_SESSION_INTERVAL) long expirationSeconds) {
      // since the session interval is also the token expiration time requested to the server make the token expire a
      // bit before the deadline to make sure there aren't session expiration exceptions
      expirationSeconds = expirationSeconds > 30 ? expirationSeconds - 30 : expirationSeconds;
      return CacheBuilder.newBuilder().expireAfterWrite(expirationSeconds, SECONDS).build(CacheLoader.from(getAccess));
   }

   /**
    * Defers instantiation of {@linkplain SignOrProduceMacForToken} so as to avoid requiring private keys when the alg
    * is set to {@linkplain org.jclouds.oauth.v2.JWSAlgorithms#NONE}.
    */
   @Provides @Singleton Supplier<Function<byte[], byte[]>> signOrProduceMacForToken(@Named(JWS_ALG) String jwsAlg,
         Provider<SignOrProduceMacForToken> in) {
      if (jwsAlg.equals(NONE)) { // Current implementation requires we return null on none.
         return (Supplier) Suppliers.ofInstance(Functions.constant(null));
      }
      return Suppliers.memoize(in.get());
   }

   @Singleton
   public static class CredentialTypeFromPropertyOrDefault implements Provider<CredentialType> {
      @Inject(optional = true)
      @Named(OAuthProperties.CREDENTIAL_TYPE)
      String credentialType = CredentialType.P12_PRIVATE_KEY_CREDENTIALS.toString();

      @Override
      public CredentialType get() {
         return CredentialType.fromValue(credentialType);
      }
   }

   @Provides
   @Singleton
   protected OAuthAuthenticationFilter authenticationFilterForCredentialType(CredentialType credentialType,
                                                                             OAuthAuthenticator serviceAccountAuth,
                                                                             BearerTokenAuthenticator bearerTokenAuth) {
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
