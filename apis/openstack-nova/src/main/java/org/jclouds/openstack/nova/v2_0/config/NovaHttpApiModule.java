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
package org.jclouds.openstack.nova.v2_0.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces;
import org.jclouds.openstack.nova.v2_0.handlers.NovaErrorHandler;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.functions.PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneHttpApiModule.aliasBinder;

/**
 * Configures the Nova connection.
 *
 */
@ConfiguresHttpApi
public class NovaHttpApiModule extends HttpApiModule<NovaApi> {

   public NovaHttpApiModule() {
   }

   @Override
   protected void configure() {
      bind(ImplicitOptionalConverter.class).to(PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet.class);
      super.configure();
      bindDefaultAliases();
   }

   // Intentionally private so subclasses use the Guice multibindings to contribute their aliases
   private void bindDefaultAliases() {
      MapBinder<URI, URI> aliases = aliasBinder(binder());
      aliases.addBinding(URI.create(ExtensionNamespaces.SECURITY_GROUPS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/securitygroups/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.FLOATING_IPS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/floating_ips/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.KEYPAIRS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/keypairs/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.SIMPLE_TENANT_USAGE)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/os-simple-tenant-usage/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.HOSTS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/hosts/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.VOLUMES)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/volumes/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.VIRTUAL_INTERFACES)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/virtual_interfaces/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.CREATESERVEREXT)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/createserverext/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.ADMIN_ACTIONS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/admin-actions/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.AGGREGATES)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/aggregates/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.FLAVOR_EXTRA_SPECS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/flavor_extra_specs/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.QUOTAS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/quotas-sets/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.VOLUME_TYPES)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/volume_types/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.AVAILABILITY_ZONE)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/availabilityzone/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.VOLUME_ATTACHMENTS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/os-volume-attachment-update/api/v2"));
      aliases.addBinding(URI.create(ExtensionNamespaces.ATTACH_INTERFACES)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/interfaces/api/v1.1"));
      aliases.addBinding(URI.create(ExtensionNamespaces.HYPERVISORS)).toInstance(
            URI.create("http://docs.openstack.org/compute/ext/hypervisors/api/v1.1"));
   }

   @Provides
   @Singleton
   public LoadingCache<String, Set<? extends Extension>> provideExtensionsByRegion(final Provider<NovaApi> novaApi) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS)
            .build(new CacheLoader<String, Set<? extends Extension>>() {
               @Override
               public Set<? extends Extension> load(final String key) throws Exception {
                  return novaApi.get().getExtensionApi(key).list();
               }
            });
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(NovaErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(NovaErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(NovaErrorHandler.class);
   }
}
