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
package org.jclouds.b2.filters;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.b2.domain.Authorization;

import com.google.common.base.Supplier;
import com.google.common.net.HttpHeaders;

@Singleton
public final class RequestAuthorization implements HttpRequestFilter {
   private final Supplier<Authorization> auth;

   @Inject
   RequestAuthorization(@Memoized Supplier<Authorization> auth) {
      this.auth = auth;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      Authorization auth = this.auth.get();

      // Replace with API URL
      URI endpoint = request.getEndpoint();
      endpoint = URI.create(auth.apiUrl() +
            (endpoint.getPort() == -1 ? "" : ":" + endpoint.getPort()) +
            endpoint.getPath() +
            (endpoint.getQuery() == null ? "" : "?" + endpoint.getQuery()));

      request = request.toBuilder()
            .endpoint(endpoint)
            .replaceHeader(HttpHeaders.AUTHORIZATION, auth.authorizationToken())
            .build();
      return request;
   }
}
