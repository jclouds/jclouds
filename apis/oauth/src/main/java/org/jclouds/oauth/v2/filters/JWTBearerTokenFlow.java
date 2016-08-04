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
package org.jclouds.oauth.v2.filters;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.location.Provider;
import org.jclouds.oauth.v2.AuthorizationApi;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.Token;

import com.google.auto.value.AutoValue;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Authorizes new Bearer Tokens at runtime by authorizing claims needed for the http request.
 *
 * <h3>Cache</h3>
 * This maintains a time-based Bearer Token cache. By default expires after 59 minutes
 * (the maximum time a token is valid is 60 minutes).
 * This cache and expiry period is system-wide and does not attend to per-instance expiry time
 * (e.g. "expires_in" from Google Compute -- which is set to the standard 3600 seconds).
 */
public class JWTBearerTokenFlow implements OAuthFilter {
   private static final Joiner ON_COMMA = Joiner.on(",");

   private final String audience;
   private final Supplier<Credentials> credentialsSupplier;
   private final OAuthScopes scopes;
   private final LoadingCache<TokenCacheKey, Token> tokenCache;

   @Inject JWTBearerTokenFlow(AuthorizeToken loader, @Named(PROPERTY_SESSION_INTERVAL) long tokenDuration,
         @Provider Supplier<Credentials> credentialsSupplier, OAuthScopes scopes, @Named(AUDIENCE) String audience) {
      this.credentialsSupplier = credentialsSupplier;
      this.scopes = scopes;
      this.audience = audience;
      // since the session interval is also the token expiration time requested to the server make the token expire a
      // bit before the deadline to make sure there aren't session expiration exceptions
      long cacheExpirationSeconds = tokenDuration > 30 ? tokenDuration - 30 : tokenDuration;
      this.tokenCache = CacheBuilder.newBuilder().expireAfterWrite(cacheExpirationSeconds, SECONDS).build(loader);
   }

   static final class AuthorizeToken extends CacheLoader<TokenCacheKey, Token> {
      private final AuthorizationApi api;
      private final long tokenDuration;

      @Inject AuthorizeToken(AuthorizationApi api, @Named(PROPERTY_SESSION_INTERVAL) long tokenDuration) {
         this.api = api;
         this.tokenDuration = tokenDuration;
      }

      @Override public Token load(TokenCacheKey tokenCacheKey) throws Exception {
         final Claims claims = Claims.create(
               tokenCacheKey.claims().iss(),
               tokenCacheKey.claims().scope(),
               tokenCacheKey.claims().aud(),
               tokenCacheKey.startTime + tokenDuration,
               tokenCacheKey.startTime);
         return api.authorize(claims);
      }
   }

   @Override public HttpRequest filter(HttpRequest request) throws HttpException {
      long now = currentTimeSeconds();
      Claims claims = Claims.create( //
            credentialsSupplier.get().identity, // iss
            ON_COMMA.join(scopes.forRequest(request)), // scope
            audience, // aud
            -1, // placeholder exp for the cache
            -1 // placeholder iat for the cache
      );
      final TokenCacheKey tokenCacheKey = TokenCacheKey.create(claims, now);
      Token token = tokenCache.getUnchecked(tokenCacheKey);
      String authorization = String.format("%s %s", token.tokenType(), token.accessToken());
      return request.toBuilder().addHeader("Authorization", authorization).build();
   }

   long currentTimeSeconds() {
      return System.currentTimeMillis() / 1000;
   }

   @AutoValue
   abstract static class TokenCacheKey {
      public abstract Claims claims();

      long startTime;

      public static TokenCacheKey create(Claims claims, long startTime) {
         final AutoValue_JWTBearerTokenFlow_TokenCacheKey tokenCacheKey = new AutoValue_JWTBearerTokenFlow_TokenCacheKey(claims);
         tokenCacheKey.startTime = startTime;
         return tokenCacheKey;
      }

      TokenCacheKey() {
      }
   }
}
