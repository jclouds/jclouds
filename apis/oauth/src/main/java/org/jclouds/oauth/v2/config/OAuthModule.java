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

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.OAuthCredentials;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.filters.BearerTokenAuthenticator;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.oauth.v2.functions.BuildTokenRequest;
import org.jclouds.oauth.v2.functions.FetchToken;
import org.jclouds.oauth.v2.functions.OAuthCredentialsSupplier;
import org.jclouds.oauth.v2.functions.SignOrProduceMacForToken;
import org.jclouds.oauth.v2.json.ClaimSetTypeAdapter;
import org.jclouds.oauth.v2.json.HeaderTypeAdapter;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

/**
 * Base OAuth module
 */
public class OAuthModule extends AbstractModule {


   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<byte[], byte[]>>() {}).to(SignOrProduceMacForToken.class);
      bind(new TypeLiteral<Map<Type, Object>>() {}).toInstance(ImmutableMap.<Type, Object>of(
            Header.class, new HeaderTypeAdapter(),
            ClaimSet.class, new ClaimSetTypeAdapter()));
      bind(CredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
      bind(new TypeLiteral<Supplier<OAuthCredentials>>() {}).to(OAuthCredentialsSupplier.class);
      bind(new TypeLiteral<Function<GeneratedHttpRequest, TokenRequest>>() {}).to(BuildTokenRequest.class);
      bind(new TypeLiteral<Function<TokenRequest, Token>>() {}).to(FetchToken.class);
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
                                                               @Named(PROPERTY_SESSION_INTERVAL) long
                                                                       sessionIntervalInSeconds) {
      // since the session interval is also the token expiration time requested to the server make the token expire a
      // bit before the deadline to make sure there aren't session expiration exceptions
      sessionIntervalInSeconds = sessionIntervalInSeconds > 30 ? sessionIntervalInSeconds - 30 :
              sessionIntervalInSeconds;
      return CacheBuilder.newBuilder().expireAfterWrite(sessionIntervalInSeconds, TimeUnit.SECONDS).build(CacheLoader
              .from(getAccess));
   }

   @Singleton
   public static class CredentialTypeFromPropertyOrDefault implements Provider<CredentialType> {
      @Inject(optional = true)
      @Named(OAuthProperties.CREDENTIAL_TYPE)
      String credentialType = CredentialType.SERVICE_ACCOUNT_CREDENTIALS.toString();

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
         case SERVICE_ACCOUNT_CREDENTIALS:
            return serviceAccountAuth;
         case BEARER_TOKEN_CREDENTIALS:
            return bearerTokenAuth;
         default:
            throw new IllegalArgumentException("Unsupported credential type: " + credentialType);
      }
   }

}
