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
package org.jclouds.openstack.v2_0.functions;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.openstack.keystone.v2_0.config.Aliases;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.annotations.Delegate;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;

@Test(groups = "unit", testName = "PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSetTest")
public class PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSetTest {

   Extension keypairs = Extension.builder().alias("os-keypairs").name("Keypairs").namespace(
            URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1")).updated(
            new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-08-08T00:00:00+00:00")).description(
            "Keypair Support").build();

   @org.jclouds.openstack.v2_0.services.Extension(of = ServiceType.COMPUTE, namespace = "http://docs.openstack.org/ext/keypairs/api/v1.1")
   interface KeyPairApi {

   }

   Extension floatingIps = Extension.builder().alias("os-floating-ips").name("Floating_ips").namespace(
            URI.create("http://docs.openstack.org/ext/floating_ips/api/v1.1")).updated(
            new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-06-16T00:00:00+00:00")).description(
            "Floating IPs support").build();

   @org.jclouds.openstack.v2_0.services.Extension(of = ServiceType.COMPUTE, namespace = "http://docs.openstack.org/ext/floating_ips/api/v1.1")
   interface FloatingIPApi {

   }

   interface NovaApi {

      @Delegate
      Optional<FloatingIPApi> getFloatingIPExtensionApi(String region);

      @Delegate
      Optional<KeyPairApi> getKeyPairExtensionApi(String region);

   }

   InvocationSuccess getFloatingIPExtension(List<Object> args) throws SecurityException, NoSuchMethodException {
      return InvocationSuccess.create(
            Invocation.create(method(NovaApi.class, "getFloatingIPExtensionApi", String.class), args), "foo");
   }

   InvocationSuccess getKeyPairExtension(List<Object> args) throws SecurityException, NoSuchMethodException {
      return InvocationSuccess.create(
            Invocation.create(method(NovaApi.class, "getKeyPairExtensionApi", String.class), args), "foo");
   }

   public void testPresentWhenExtensionsIncludeNamespaceFromAnnotationAbsentWhenNot() throws SecurityException, NoSuchMethodException {

      assertEquals(whenExtensionsInRegionInclude("region", keypairs, floatingIps).apply(getFloatingIPExtension(ImmutableList.<Object> of("region"))), Optional.of("foo"));
      assertEquals(whenExtensionsInRegionInclude("region", keypairs, floatingIps).apply(getKeyPairExtension(ImmutableList.<Object> of("region"))), Optional.of("foo"));
      assertEquals(whenExtensionsInRegionInclude("region", keypairs).apply(getFloatingIPExtension(ImmutableList.<Object> of("region"))), Optional.absent());
      assertEquals(whenExtensionsInRegionInclude("region", floatingIps).apply(getKeyPairExtension(ImmutableList.<Object> of("region"))), Optional.absent());
   }

   public void testRegionWithoutExtensionsReturnsAbsent() throws SecurityException, NoSuchMethodException {
      assertEquals(whenExtensionsInRegionInclude("region", floatingIps).apply(
               getFloatingIPExtension(ImmutableList.<Object> of("differentregion"))), Optional.absent());
      assertEquals(whenExtensionsInRegionInclude("region", keypairs).apply(
               getKeyPairExtension(ImmutableList.<Object> of("differentregion"))), Optional.absent());
   }

   /**
    * It is possible that the /extensions call returned the correct extension, but that the
    * namespaces were different, for whatever reason. One way to address this is to have a multimap
    * of the authoritative namespace to alternate onces, which could be wired up with guice
    *
    */
   public void testPresentWhenAliasForExtensionMapsToNamespace() throws SecurityException, NoSuchMethodException {
      Extension keypairsWithDifferentNamespace = keypairs.toBuilder().namespace(
               URI.create("http://docs.openstack.org/ext/arbitrarilydifferent/keypairs/api/v1.1")).build();

      Multimap<URI, URI> aliases = ImmutableMultimap.of(keypairs.getNamespace(), keypairsWithDifferentNamespace
               .getNamespace());

      assertEquals(whenExtensionsAndAliasesInRegionInclude("region", ImmutableSet.of(keypairsWithDifferentNamespace), aliases).apply(
              getKeyPairExtension(ImmutableList.<Object> of("region"))), Optional.of("foo"));
      assertEquals(whenExtensionsAndAliasesInRegionInclude("region", ImmutableSet.of(keypairsWithDifferentNamespace), aliases).apply(
              getFloatingIPExtension(ImmutableList.<Object> of("region"))), Optional.absent());

   }

   private PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet whenExtensionsInRegionInclude(
            String region, Extension... extensions) {
      return whenExtensionsAndAliasesInRegionInclude(region, ImmutableSet.copyOf(extensions), ImmutableMultimap.<URI, URI> of());
   }

   private PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet whenExtensionsAndAliasesInRegionInclude(
            String region, final Set<Extension> extensions, final Multimap<URI, URI> aliases) {
      final LoadingCache<String, Set<? extends Extension>> extensionsForRegion = CacheBuilder.newBuilder().build(
               CacheLoader.from(Functions.forMap(ImmutableMap.<String, Set<? extends Extension>>of(region, extensions, "differentregion",
                        ImmutableSet.<Extension> of()))));

      PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet fn = Guice.createInjector(
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     MapBinder<URI, URI> aliasBindings = MapBinder.newMapBinder(binder(),
                           URI.class, URI.class, Aliases.class).permitDuplicates();
                     for (URI key : aliases.keySet()) {
                        for (URI value : aliases.get(key)) {
                           aliasBindings.addBinding(key).toInstance(value);
                        }
                     }
                  }

                  @Provides
                  LoadingCache<String, Set<? extends Extension>> getExtensions() {
                     return extensionsForRegion;
                  }

               }).getInstance(PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet.class);

      return fn;
   }
}
