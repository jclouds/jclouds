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
package org.jclouds.openstack.keystone.catalog.config;

import static org.jclouds.util.Suppliers2.getLastValueInMap;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.RegionIdsSupplier;
import org.jclouds.location.suppliers.all.RegionToProvider;
import org.jclouds.location.suppliers.derived.RegionIdsFromRegionIdToURIKeySet;
import org.jclouds.location.suppliers.implicit.FirstRegion;
import org.jclouds.openstack.keystone.catalog.ServiceEndpoint;
import org.jclouds.openstack.keystone.catalog.suppliers.LocationIdToURIFromServiceEndpointsForTypeAndVersion;
import org.jclouds.openstack.keystone.catalog.suppliers.RegionIdToAdminURIFromServiceEndpointsForTypeAndVersion;
import org.jclouds.openstack.keystone.catalog.suppliers.RegionIdToAdminURISupplier;
import org.jclouds.openstack.keystone.catalog.suppliers.RegionIdToURIFromServiceEndpointsForTypeAndVersion;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.keystone.v2_0.catalog.V2ServiceCatalog;
import org.jclouds.openstack.keystone.v3.catalog.V3ServiceCatalog;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ServiceCatalogModule extends AbstractModule {

   @Override
   protected void configure() {
      
   }
   
   @Provides
   @Singleton
   protected final Supplier<List<ServiceEndpoint>> provideServiceCatalog(Injector i,
         @Named(KeystoneProperties.KEYSTONE_VERSION) String keystoneVersion) {
      Map<String, Supplier<List<ServiceEndpoint>>> serviceCatalogs = Maps.newHashMap();
      serviceCatalogs.put("2", i.getInstance(V2ServiceCatalog.class));
      serviceCatalogs.put("3", i.getInstance(V3ServiceCatalog.class));
      return serviceCatalogs.get(keystoneVersion);
   }
   
   /**
    * For global services who have no regions, such as DNS. To use, do the following
    * <ol>
    * <li>add this module to your {@link org.jclouds.apis.ApiMetadata#getDefaultModules()}</li>
    * <li>create a service-specific annotation, such as {@code @CloudDNS}, and make sure that has the meta-annotation
    * {@link javax.inject.Qualifier}</li>
    * <li>add the above annotation to any {@code Api} classes by placing it on the type. ex.
    * {@code @Endpoint(CloudDNS.class)}</li>
    * <li>add the following to your {@link org.jclouds.rest.config.RestClientModule}</li>
    *
    * <pre>
    * bind(new TypeLiteral&lt;Supplier&lt;URI&gt;&gt;() {
    * }).annotatedWith(CloudDNS.class).to(new TypeLiteral&lt;Supplier&lt;URI&gt;&gt;() {
    * });
    * </pre>
    */
   public static class ProviderModule extends AbstractModule {
      @Override
      protected void configure() {
         install(new FactoryModuleBuilder().build(LocationIdToURIFromServiceEndpointsForTypeAndVersion.Factory.class));
      }

      @Provides
      @Singleton
      protected final Supplier<URI> provideZoneIdToURISupplierForApiVersion(
            @Named(KeystoneProperties.SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
            LocationIdToURIFromServiceEndpointsForTypeAndVersion.Factory factory) {
         return getLastValueInMap(factory.createForApiTypeAndVersion(serviceType, apiVersion));
      }

      @Provides
      @Singleton
      final Function<ServiceEndpoint, String> provideProvider(@Provider final String provider) {
         return new Function<ServiceEndpoint, String>() {
            @Override
            public String apply(ServiceEndpoint in) {
               return provider;
            }
         };
      }
   }

   public static class RegionModule extends AbstractModule {
      @Override
      protected void configure() {
         install(new FactoryModuleBuilder().implement(RegionIdToURISupplier.class,
               RegionIdToURIFromServiceEndpointsForTypeAndVersion.class).build(RegionIdToURISupplier.Factory.class));
         install(new FactoryModuleBuilder().implement(RegionIdToAdminURISupplier.class,
               RegionIdToAdminURIFromServiceEndpointsForTypeAndVersion.class).build(RegionIdToAdminURISupplier.Factory.class));
         // Dynamically build the region list as opposed to from properties
         bind(RegionIdsSupplier.class).to(RegionIdsFromRegionIdToURIKeySet.class);
         bind(ImplicitLocationSupplier.class).to(FirstRegion.class).in(Scopes.SINGLETON);
         bind(LocationsSupplier.class).to(RegionToProvider.class).in(Scopes.SINGLETON);
      }

      @Provides
      @Singleton
      protected final RegionIdToURISupplier guiceProvideRegionIdToURISupplierForApiVersion(
              @Named(KeystoneProperties.SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
              RegionIdToURISupplier.Factory factory) {
         return provideRegionIdToURISupplierForApiVersion(serviceType, apiVersion, factory);
      }

      // Supply the region to id map from keystone, based on the servicetype and
      // api version in config
      protected RegionIdToURISupplier provideRegionIdToURISupplierForApiVersion(
            @Named(KeystoneProperties.SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
            RegionIdToURISupplier.Factory factory) {
         return factory.createForApiTypeAndVersion(serviceType, apiVersion);
      }

      @Provides
      @Singleton
      protected final RegionIdToAdminURISupplier guiceProvideRegionIdToAdminURISupplierForApiVersion(
              @Named(KeystoneProperties.SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
              RegionIdToAdminURISupplier.Factory factory) {
         return provideRegionIdToAdminURISupplierForApiVersion(serviceType, apiVersion, factory);
      }

      // Supply the region to id to AdminURL map from keystone, based on the
      // servicetype and api version in config
      protected RegionIdToAdminURISupplier provideRegionIdToAdminURISupplierForApiVersion(
            @Named(KeystoneProperties.SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
            RegionIdToAdminURISupplier.Factory factory) {
         return factory.createForApiTypeAndVersion(serviceType, apiVersion);
      }
   }

}
