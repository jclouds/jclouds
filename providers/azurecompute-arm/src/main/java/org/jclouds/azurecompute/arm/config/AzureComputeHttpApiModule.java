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
package org.jclouds.azurecompute.arm.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.ServicePrincipal;
import org.jclouds.azurecompute.arm.filters.ApiVersionFilter;
import org.jclouds.azurecompute.arm.handlers.AzureComputeErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.FirstRegion;
import org.jclouds.oauth.v2.config.OAuthConfigFactory;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Named;

@ConfiguresHttpApi
public class AzureComputeHttpApiModule extends HttpApiModule<AzureComputeApi> {

   private static final Pattern OAUTH_TENANT_PATTERN = Pattern
         .compile("https://login.microsoftonline.com/([^/]+)/oauth2/token");

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(AzureComputeErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(AzureComputeErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(AzureComputeErrorHandler.class);
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(FirstRegion.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void configure() {
      super.configure();
      bindHttpApi(binder(), CurrentServicePrincipal.class);
      bind(OAuthScopes.class).toInstance(OAuthScopes.NoScopes.create());
      bind(OAuthConfigFactory.class).to(AzureOAuthConfigFactory.class).in(Scopes.SINGLETON);
   }

   @Provides
   @Singleton
   @Tenant
   protected String provideTenant(@Named("oauth.endpoint") final String oauthEndpoint) {
      Matcher m = OAUTH_TENANT_PATTERN.matcher(oauthEndpoint);
      if (!m.matches()) {
         throw new IllegalArgumentException("Could not parse tenantId from: " + oauthEndpoint);
      }
      return m.group(1);
   }

   @Provides
   @Singleton
   @GraphRBAC
   protected Supplier<URI> graphRBACEndpoint(@Tenant String tenantId) {
      return Suppliers.ofInstance(URI.create(GraphRBAC.ENDPOINT + tenantId));
   }

   @Provides
   @Singleton
   protected Supplier<ServicePrincipal> provideServicePrincipal(final CurrentServicePrincipal currentServicePrincipal,
         AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      // This supplier must be defensive against any auth exception.
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
            new Supplier<ServicePrincipal>() {
               @Override
               public ServicePrincipal get() {
                  return currentServicePrincipal.get();
               }
            }, seconds, TimeUnit.SECONDS);
   }

   @RequestFilters({ OAuthFilter.class, ApiVersionFilter.class })
   @Consumes(MediaType.APPLICATION_JSON)
   @Endpoint(GraphRBAC.class)
   @OAuthResource(GraphRBAC.ENDPOINT)
   public interface CurrentServicePrincipal {

      @Named("servicePrincipal:get")
      @GET
      @Path("/servicePrincipals")
      @QueryParams(keys = "$filter", values = "appId eq '{jclouds.identity}'")
      @SelectJson("value")
      @OnlyElement
      ServicePrincipal get();
   }
}
