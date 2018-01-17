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
package org.jclouds.openstack.neutron.v2.config;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneHttpApiModule.namespaceAliasBinder;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.extensions.ExtensionNamespaces;
import org.jclouds.openstack.neutron.v2.handlers.NeutronErrorHandler;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.functions.PresentWhenExtensionAnnotationMatchesExtensionSet;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;

/**
 * Configures the Neutron connection.
 *
 */
@ConfiguresHttpApi
public class NeutronHttpApiModule extends HttpApiModule<NeutronApi> {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      bind(ImplicitOptionalConverter.class).to(PresentWhenExtensionAnnotationMatchesExtensionSet.class);
      super.configure();
      bindAliases();
   }

   private void bindAliases() {
      MapBinder<URI, URI> namespaceAliases = namespaceAliasBinder(binder());
      namespaceAliases.addBinding(URI.create(ExtensionNamespaces.L3_ROUTER)).toInstance(
            URI.create("http://docs.openstack.org/ext/neutron/router/api/v1.0"));
      namespaceAliases.addBinding(URI.create(ExtensionNamespaces.SECURITY_GROUPS)).toInstance(
            URI.create("http://docs.openstack.org/ext/securitygroups/api/v2.0"));
      namespaceAliases.addBinding(URI.create(ExtensionNamespaces.LBAAS)).toInstance(
            URI.create("http://docs.openstack.org/networking/ext/lbaas/api/v1.0"));
   }

   @Provides
   @Singleton
   public LoadingCache<String, Set<? extends Extension>> provideExtensionsByRegion(final Provider<NeutronApi> neutronApi) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS)
            .build(new CacheLoader<String, Set<? extends Extension>>() {
               @Override
               public Set<? extends Extension> load(String key) throws Exception {
                  return neutronApi.get().getExtensionApi(key).list();
               }
            });
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(NeutronErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(NeutronErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(NeutronErrorHandler.class);
   }
}
