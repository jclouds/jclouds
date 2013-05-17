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
package org.jclouds.vcloud.director.v1_5.filters;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.vcloud.director.v1_5.annotations.Session;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AddVCloudAuthorizationAndCookieToRequest implements HttpRequestFilter {

   private final Supplier<String> sessionSupplier;

   @Inject
   public AddVCloudAuthorizationAndCookieToRequest(@Session Supplier<String> sessionSupplier) {
      this.sessionSupplier = sessionSupplier;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      String token = sessionSupplier.get();
      return request
               .toBuilder()
               .replaceHeaders(
                        ImmutableMultimap.of("x-vcloud-authorization", token, HttpHeaders.COOKIE, "vcloud-token="
                                 + token)).build();
   }

}
