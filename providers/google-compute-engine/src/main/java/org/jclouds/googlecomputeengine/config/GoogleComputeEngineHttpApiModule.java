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
package org.jclouds.googlecomputeengine.config;

import static com.google.common.base.Suppliers.compose;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.handlers.GoogleComputeEngineErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Provider;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Provides;

@ConfiguresHttpApi
public final class GoogleComputeEngineHttpApiModule extends HttpApiModule<GoogleComputeEngineApi> {
   public GoogleComputeEngineHttpApiModule() {
   }

   @Override protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(GoogleComputeEngineErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(GoogleComputeEngineErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(GoogleComputeEngineErrorHandler.class);
   }

   /**
    * Since this is caching a direct api call, we memoize, but short-circuit on any auth exception. This prevents
    * excessive errors when things occur in parallel, or as peers on a function graph.
    */
   @Provides
   @Singleton
   @UserProject Supplier<String> projectName(@Provider final Supplier<Credentials> creds,
                                             final GoogleComputeEngineApi api,
                                             AtomicReference<AuthorizationException> authException,
                                             @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
              compose(new Function<Credentials, String>() {
                 public String apply(Credentials in) {
                    // ID should be of the form project_id@developer.gserviceaccount.com 
                    // OR (increasingly often) project_id-extended_uid@developer.gserviceaccount.com
                    // where project_id is the NUMBER;
                    // HERE we also accept simply "project" as the identity, if no "@" is present;
                    // this is used in tests, but not sure if it is valid in the wild.
                    String projectName = in.identity;
                    if (projectName.indexOf("@") != -1) {
                       projectName = Iterables.get(Splitter.on("@").split(projectName), 0);
                       if (projectName.indexOf("-") != -1) {
                          // if ID is of the form project_id-extended_uid@developer.gserviceaccount.com
                          projectName = Iterables.get(Splitter.on("-").split(projectName), 0);
                       }
                    }
                    return api.getProjectApi().get(projectName).name();
                 }
              }, creds), seconds, SECONDS);
   }
}
