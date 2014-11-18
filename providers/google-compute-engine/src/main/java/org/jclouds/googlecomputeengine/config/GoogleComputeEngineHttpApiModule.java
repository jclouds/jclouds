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
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.googlecloud.config.GoogleCloudProperties.PROJECT_NAME;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Project;
import org.jclouds.googlecomputeengine.handlers.GoogleComputeEngineErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Provider;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.config.OAuthScopes.ReadOrWriteScopes;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Provides;

@ConfiguresHttpApi
public final class GoogleComputeEngineHttpApiModule extends HttpApiModule<GoogleComputeEngineApi> {

   @Override protected void configure() {
      super.configure();
      bindHttpApi(binder(), UseApiToResolveProjectName.GetProject.class);
      bind(OAuthScopes.class).toInstance(ReadOrWriteScopes.create( //
            "https://www.googleapis.com/auth/compute.readonly", //
            "https://www.googleapis.com/auth/compute" //
      ));
   }

   @Override protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(GoogleComputeEngineErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(GoogleComputeEngineErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(GoogleComputeEngineErrorHandler.class);
   }

   @Provides @Singleton @CurrentProject Supplier<URI> project(@Named(PROJECT_NAME) final String projectName,
         @Provider Supplier<URI> defaultEndpoint, final UseApiToResolveProjectName useApiToResolveProjectName,
         @Provider final Supplier<Credentials> creds, AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      // Try to avoid a runtime lookup by accepting a project name supplied in context overrides.
      if (Strings.emptyToNull(projectName) != null) {
         return Suppliers.memoizeWithExpiration(Suppliers.compose(new Function<URI, URI>() {
            @Override public URI apply(URI input) {
               return URI.create(String.format("%s/projects/%s", input, projectName));
            }
         }, defaultEndpoint), seconds, SECONDS);
      }

      // If the project name wasn't explicitly supplied, then we lookup via api.
      // This supplier must be defensive against any auth exception.
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier
            .create(authException, compose(useApiToResolveProjectName, creds), seconds, SECONDS);
   }

   /**
    * Parse the project ID from the identity, use it to lookup the project name, return the project-scoped uri.
    *
    * <h3>Why are we looking up the project name? We already have the project ID!</h3>
    * <a href="https://cloud.google.com/compute/docs/overview#projectids">Documentation</a> suggests that the
    * project name is interchangeable with the project ID, which we already have. However, in practice, using the
    * project ID leads to problems in POST requests.
    *
    * <p/> For example, inserting an instance using the project ID in the instances url, but the project name in
    * the machineType url results in an error of
    * <pre>{@code Cross-project references for this resource type are not allowed}.</pre>
    *
    * <p/>Similar errors occur in POST requests to other resources including at least forwardingRules, images,
    * targetPools.
    */
   static final class UseApiToResolveProjectName implements Function<Credentials, URI> {

      @SkipEncoding({ '/', '=' })
      @RequestFilters(OAuthFilter.class)
      @Consumes(APPLICATION_JSON)
      interface GetProject {
         @Named("Projects:get")
         @GET
         @Path("/projects/{projectNumber}") Project get(@PathParam("projectNumber") String projectNumber);
      }

      private final GetProject api;
      private final Supplier<URI> defaultEndpoint;
      private final String identityName;

      @Inject
      UseApiToResolveProjectName(GetProject api, @Provider Supplier<URI> defaultEndpoint, ProviderMetadata metadata) {
         this.api = api;
         this.defaultEndpoint = defaultEndpoint;
         this.identityName = metadata.getApiMetadata().getIdentityName();
      }

      @Override public URI apply(Credentials in) {
         String projectNumber = CurrentProject.ClientEmail.toProjectNumber(in.identity);
         return URI.create(defaultEndpoint.get() + "/projects/" + api.get(projectNumber).name());
      }
   }
}
