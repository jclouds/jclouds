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
package org.jclouds.oauth.v2.functions;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.oauth.v2.domain.Claims.EXPIRATION_TIME;
import static org.jclouds.oauth.v2.domain.Claims.ISSUED_AT;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.location.Provider;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.reflect.Invokable;

/** Builds the default token request with the following claims: {@code iss,scope,aud,iat,exp}. */
public class BuildTokenRequest implements Function<HttpRequest, TokenRequest> {
   private static final Joiner ON_COMMA = Joiner.on(",");

   private final String assertionTargetDescription;
   private final String signatureAlgorithm;
   private final Supplier<Credentials> credentialsSupplier;
   private final long tokenDuration;

   public static class TestBuildTokenRequest extends BuildTokenRequest {
      @Inject TestBuildTokenRequest(@Named(AUDIENCE) String assertionTargetDescription,
            @Named(JWS_ALG) String signatureAlgorithm, @Provider Supplier<Credentials> credentialsSupplier,
            @Named(PROPERTY_SESSION_INTERVAL) long tokenDuration) {
         super(assertionTargetDescription, signatureAlgorithm, credentialsSupplier, tokenDuration);
      }

      public long currentTimeSeconds() {
         return 0;
      }
   }

   @Inject BuildTokenRequest(@Named(AUDIENCE) String assertionTargetDescription,
         @Named(JWS_ALG) String signatureAlgorithm, @Provider Supplier<Credentials> credentialsSupplier,
         @Named(PROPERTY_SESSION_INTERVAL) long tokenDuration) {
      this.assertionTargetDescription = assertionTargetDescription;
      this.signatureAlgorithm = signatureAlgorithm;
      this.credentialsSupplier = credentialsSupplier;
      this.tokenDuration = tokenDuration;
   }

   @Override public TokenRequest apply(HttpRequest request) {
      Header header = Header.create(signatureAlgorithm, "JWT");

      Map<String, Object> claims = new LinkedHashMap<String, Object>();
      claims.put("iss", credentialsSupplier.get().identity);
      claims.put("scope", getOAuthScopes((GeneratedHttpRequest) request));
      claims.put("aud", assertionTargetDescription);

      long now = currentTimeSeconds();
      claims.put(EXPIRATION_TIME, now + tokenDuration);
      claims.put(ISSUED_AT, now);

      return TokenRequest.create(header, claims);
   }

   //TODO: Remove and switch to a request function.
   private String getOAuthScopes(GeneratedHttpRequest request) {
      Invokable<?, ?> invokable = request.getInvocation().getInvokable();
      OAuthScopes classScopes = invokable.getOwnerType().getRawType().getAnnotation(OAuthScopes.class);
      OAuthScopes methodScopes = invokable.getAnnotation(OAuthScopes.class);
      checkState(classScopes != null || methodScopes != null, "Api interface or method should be annotated " //
                  + "with OAuthScopes specifying required permissions. Api interface: %s, Method: %s", //
            invokable.getOwnerType(), invokable.getName());
      OAuthScopes scopes = methodScopes != null ? methodScopes : classScopes;
      return ON_COMMA.join(scopes.value());
   }

   long currentTimeSeconds() {
      return System.currentTimeMillis() / 1000;
   }
}
