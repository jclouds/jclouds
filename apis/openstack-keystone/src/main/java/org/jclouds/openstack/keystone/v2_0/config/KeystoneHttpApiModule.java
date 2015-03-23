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
package org.jclouds.openstack.keystone.v2_0.config;

import static org.jclouds.util.Suppliers2.getLastValueInMap;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Provider;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.handlers.KeystoneErrorHandler;
import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToAdminURIFromAccessForTypeAndVersion;
import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToAdminURISupplier;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.functions.PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.functions.ImplicitOptionalConverter;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * Configures the Keystone API.
 */
@ConfiguresHttpApi
public class KeystoneHttpApiModule extends HttpApiModule<KeystoneApi> {

   public KeystoneHttpApiModule() {
   }

   public static class KeystoneAdminURLModule extends AbstractModule {

      @Override
      protected void configure() {
         install(new FactoryModuleBuilder().implement(RegionIdToAdminURISupplier.class,
                  RegionIdToAdminURIFromAccessForTypeAndVersion.class).build(RegionIdToAdminURISupplier.Factory.class));
      }

      /**
       * in some cases, there is no {@link ServiceType#IDENTITY} entry in the service catalog. In
       * other cases, there's no adminURL entry present. Fallback to the provider in this case.
       */
      @Provides
      @Singleton
      @Identity
      protected Supplier<URI> provideStorageUrl(final RegionIdToAdminURISupplier.Factory factory,
               @ApiVersion final String version, @Provider final Supplier<URI> providerURI) {
         Supplier<URI> identityServiceForVersion = getLastValueInMap(factory.createForApiTypeAndVersion(
                  ServiceType.IDENTITY, version));
         Supplier<URI> whenIdentityServiceIsntListedFallbackToProviderURI = Suppliers2.onThrowable(
                  identityServiceForVersion, NoSuchElementException.class, providerURI);
         Supplier<URI> whenIdentityServiceHasNoAdminURLFallbackToProviderURI = Suppliers2.or(
                  whenIdentityServiceIsntListedFallbackToProviderURI, providerURI);
         return whenIdentityServiceHasNoAdminURLFallbackToProviderURI;
      }
   }

   // Allow providers to cleanly contribute their own aliases
   public static MapBinder<URI, URI> aliasBinder(Binder binder) {
      return MapBinder.newMapBinder(binder, URI.class, URI.class, Aliases.class).permitDuplicates();
   }

   @Override
   protected void configure() {
      bind(ImplicitOptionalConverter.class).to(PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet.class);
      super.configure();
      aliasBinder(binder());
   }

   @Provides
   @Singleton
   public LoadingCache<String, Set<? extends Extension>> provideExtensionsByRegion(final javax.inject.Provider<KeystoneApi> keystoneApi) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS)
            .build(CacheLoader.from(Suppliers.memoize(new Supplier<Set<? extends Extension>>() {
               @Override
               public Set<? extends Extension> get() {
                  return keystoneApi.get().getExtensionApi().list();
               }
            })));
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(KeystoneErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(KeystoneErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(KeystoneErrorHandler.class);
   }
}
