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

import static org.jclouds.openstack.keystone.config.KeystoneProperties.KEYSTONE_VERSION;
import static org.jclouds.util.Suppliers2.getLastValueInMap;

import java.net.URI;
import java.util.NoSuchElementException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.location.Provider;
import org.jclouds.openstack.keystone.catalog.suppliers.RegionIdToAdminURIFromServiceEndpointsForTypeAndVersion;
import org.jclouds.openstack.keystone.catalog.suppliers.RegionIdToAdminURISupplier;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class KeystoneAdminURLModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new FactoryModuleBuilder().implement(RegionIdToAdminURISupplier.class,
               RegionIdToAdminURIFromServiceEndpointsForTypeAndVersion.class).build(RegionIdToAdminURISupplier.Factory.class));
   }

   /**
    * in some cases, there is no {@link ServiceType#IDENTITY} entry in the service catalog. In
    * other cases, there's no adminURL entry present. Fallback to the provider in this case.
    */
   @Provides
   @Singleton
   @Identity
   protected final Supplier<URI> provideIdentityAdminUrl(final RegionIdToAdminURISupplier.Factory factory,
         @ApiVersion final String version, @Named(KEYSTONE_VERSION) String keystoneVersion,
         @Provider final Supplier<URI> providerURI) {
      // There is a convention to use service types such as "identityv3" for specific endpoints. let's look first for
      // those endpoints, and fallback to the default "identity" one or the project URL.
      Supplier<URI> identityServiceForSpecificVersionInType = getLastValueInMap(factory.createForApiTypeAndVersion(
            ServiceType.IDENTITY + "v" + keystoneVersion, version));
      Supplier<URI> identityServiceForVersion = Suppliers2.onThrowable(identityServiceForSpecificVersionInType,
            NoSuchElementException.class,
            getLastValueInMap(factory.createForApiTypeAndVersion(ServiceType.IDENTITY, version)));
      Supplier<URI> whenIdentityServiceIsntListedFallbackToProviderURI = Suppliers2.onThrowable(
               identityServiceForVersion, NoSuchElementException.class, providerURI);
      Supplier<URI> whenIdentityServiceHasNoAdminURLFallbackToProviderURI = Suppliers2.or(
               whenIdentityServiceIsntListedFallbackToProviderURI, providerURI);
      return whenIdentityServiceHasNoAdminURLFallbackToProviderURI;
   }
}
