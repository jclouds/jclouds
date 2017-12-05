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
package org.jclouds.openstack.keystone.catalog.suppliers;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Multimaps.asMap;
import static com.google.common.collect.Multimaps.index;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Resource;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.keystone.catalog.ServiceEndpoint;
import org.jclouds.openstack.keystone.catalog.functions.ServiceEndpointResolutionStrategy;
import org.jclouds.openstack.keystone.catalog.functions.ServiceEndpointToRegion;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class LocationIdToURIFromServiceEndpointsForTypeAndVersion implements Supplier<Map<String, Supplier<URI>>> {

   public interface Factory {
      
      LocationIdToURIFromServiceEndpointsForTypeAndVersion createForApiTypeAndVersion(@Assisted("apiType") String apiType,
                                                                                      @Nullable @Assisted("apiVersion") String apiVersion) throws NoSuchElementException;
   }

   @Resource
   protected Logger logger = Logger.NULL;

   protected final Supplier<List<ServiceEndpoint>> serviceEndpoints;
   protected final ServiceEndpointResolutionStrategy resolveServiceEndpointURI;
   protected final ServiceEndpointToRegion serviceEndpointToRegion;
   protected final String apiType;
   protected final String apiVersion;

   @Inject
   LocationIdToURIFromServiceEndpointsForTypeAndVersion(Supplier<List<ServiceEndpoint>> serviceEndpoints,
                                                        ServiceEndpointResolutionStrategy resolveServiceEndpointURI, ServiceEndpointToRegion serviceEndpointToRegion,
                                                        @Assisted("apiType") String apiType, @Nullable @Assisted("apiVersion") String apiVersion) {
      this.serviceEndpoints = serviceEndpoints;
      this.resolveServiceEndpointURI = resolveServiceEndpointURI;
      this.serviceEndpointToRegion = serviceEndpointToRegion;
      this.apiType = apiType;
      this.apiVersion = apiVersion;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      List<ServiceEndpoint> endpoints = serviceEndpoints.get();
      if (endpoints.isEmpty()) {
         throw new NoSuchElementException(
               "No endpoints were found in the service catalog. Make sure your authentication token has the right scopes and permissions");
      }

      // Filter endpoints by service type
      Iterable<ServiceEndpoint> endpointsForType = filter(endpoints, apiTypeEquals);
      if (isEmpty(endpointsForType)) {
         throw new NoSuchElementException(String.format("no endpoints for apiType %s in service endpoints %s", apiType,
               endpoints));
      }
      
      // Check if there are endpoints for a particular version, if specified and
      // there are versioned endpoints
      boolean checkVersionId = apiVersion != null && any(endpointsForType, versionAware);
      Predicate<ServiceEndpoint> versionFilter = checkVersionId ? apiVersionEqualsVersionId : Predicates.<ServiceEndpoint> alwaysTrue();
      Iterable<ServiceEndpoint> endpointsForTypeAndVersion = filter(endpointsForType, versionFilter);
      if (isEmpty(endpointsForTypeAndVersion)) {
         throw new NoSuchElementException(String.format(
               "no service endpoints for apiType %s are of version %s, or version agnostic: %s", apiType, apiVersion,
               endpointsForType));
      }

      logger.debug("service endpoints for apiType %s and version %s: %s", apiType, apiVersion,
            endpointsForTypeAndVersion);
      
      Multimap<String, ServiceEndpoint> locationToServiceEndpoints = index(endpointsForTypeAndVersion,
            serviceEndpointToRegion);
      return transformValues(asMap(locationToServiceEndpoints), resolveServiceEndpointURI);
   }

   private final Predicate<ServiceEndpoint> apiVersionEqualsVersionId = new Predicate<ServiceEndpoint>() {
      @Override
      public boolean apply(ServiceEndpoint input) {
         return input.version().equals(apiVersion);
      }
   };

   private final Predicate<ServiceEndpoint> versionAware = new Predicate<ServiceEndpoint>() {
      @Override
      public boolean apply(ServiceEndpoint input) {
         return input.version() != null;
      }
   };

   private final Predicate<ServiceEndpoint> apiTypeEquals = new Predicate<ServiceEndpoint>() {
      @Override
      public boolean apply(ServiceEndpoint input) {
         return input.type().equals(apiType);
      }
   };

   @Override
   public String toString() {
      return "locationIdToURIFromServiceEndpointsForTypeAndVersion(" + apiType + ", " + apiVersion + ")";
   }
}
