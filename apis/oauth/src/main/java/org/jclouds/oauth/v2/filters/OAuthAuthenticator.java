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

import javax.inject.Inject;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;

import com.google.common.base.Function;
import com.google.common.cache.LoadingCache;

/**
 * To be used by client applications to embed an OAuth authentication in their REST requests.
 * <p/>
 * TODO when we're able to use the OAuthAuthentication an this should be used automatically
 */
public final class OAuthAuthenticator implements OAuthAuthenticationFilter {

   private Function<HttpRequest, TokenRequest> tokenRequestBuilder;
   private Function<TokenRequest, Token> tokenFetcher;

   @Inject OAuthAuthenticator(Function<HttpRequest, TokenRequest> tokenRequestBuilder,
         LoadingCache<TokenRequest, Token> tokenFetcher) {
      this.tokenRequestBuilder = tokenRequestBuilder;
      this.tokenFetcher = tokenFetcher;
   }

   @Override public HttpRequest filter(HttpRequest request) throws HttpException {
      TokenRequest tokenRequest = tokenRequestBuilder.apply(request);
      Token token = tokenFetcher.apply(tokenRequest);
      String authorization = String.format("%s %s", token.tokenType(), token.accessToken());
      return request.toBuilder().addHeader("Authorization", authorization).build();
   }
}
