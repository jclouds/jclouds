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
package org.jclouds.b2.config;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.b2.B2Api;
import org.jclouds.b2.domain.Authorization;
import org.jclouds.b2.filters.B2RetryHandler;
import org.jclouds.b2.filters.RequestAuthorization;
import org.jclouds.b2.handlers.ParseB2ErrorFromJsonContent;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/** Configures the mappings. Installs the Object and Parser modules. */
@ConfiguresHttpApi
public final class B2HttpApiModule extends HttpApiModule<B2Api> {
   @Override
   protected void configure() {
      super.configure();
      bind(RequestAuthorization.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseB2ErrorFromJsonContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseB2ErrorFromJsonContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseB2ErrorFromJsonContent.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ServerError.class).to(B2RetryHandler.class);
   }

   @Provides
   @Singleton
   static Supplier<Authorization> provideAuthorizationSupplier(final B2Api b2Api) {
      return new Supplier<Authorization>() {
            @Override
            public Authorization get() {
               return b2Api.getAuthorizationApi().authorizeAccount();
            }
         };
   }

   @Provides
   @Singleton
   @Memoized
   static Supplier<Authorization> provideAuthorizationCache(
         AtomicReference<AuthorizationException> authException,
         @Named(Constants.PROPERTY_SESSION_INTERVAL) long seconds,
         Supplier<Authorization>  uncached) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(
            authException, uncached, seconds, TimeUnit.SECONDS);
   }
}
