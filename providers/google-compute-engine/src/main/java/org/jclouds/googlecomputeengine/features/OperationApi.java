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
package org.jclouds.googlecomputeengine.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.Fallbacks.VoidOnNotFoundOr404;

import java.net.URI;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseArg0ToIteratorOfListPage;
import org.jclouds.googlecomputeengine.internal.BaseToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Consumes(APPLICATION_JSON)
public interface OperationApi {

   /** Returns an operation by self-link or null if not found. */
   @Named("Operations:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation get(@EndpointParam URI operation);

   /** Deletes an operation by name. */
   @Named("Operations:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@EndpointParam URI operation);

   /**
    * Retrieves the list of operation resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("GlobalOperations:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/global/operations")
   ListPage<Operation> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("GlobalOperations:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/global/operations")
   @Transform(OperationPages.class)
   Iterator<ListPage<Operation>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("GlobalOperations:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/global/operations")
   @Transform(OperationPages.class)
   Iterator<ListPage<Operation>> list(ListOptions options);

   static final class OperationPages extends BaseToIteratorOfListPage<Operation, OperationPages> {

      private final GoogleComputeEngineApi api;

      @Inject OperationPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<Operation>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Operation>>() {
            @Override public ListPage<Operation> apply(String pageToken) {
               return api.operations().listPage(pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of operation resources available in the specified region.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("RegionOperations:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/regions/{region}/operations")
   ListPage<Operation> listPageInRegion(@PathParam("region") String region,
                                        @Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listInRegion(String, org.jclouds.googlecomputeengine.options.ListOptions) */
   @Named("RegionOperations:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/regions/{region}/operations")
   @Transform(OperationPagesInRegion.class)
   Iterator<ListPage<Operation>> listInRegion(@PathParam("region") String region);

   /** @see #listInRegion(String, org.jclouds.googlecomputeengine.options.ListOptions) */
   @Named("RegionOperations:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/regions/{region}/operations")
   @Transform(OperationPagesInRegion.class)
   Iterator<ListPage<Operation>> listInRegion(@PathParam("region") String region, ListOptions options);

   static final class OperationPagesInRegion extends BaseArg0ToIteratorOfListPage<Operation, OperationPagesInRegion> {

      private final GoogleComputeEngineApi api;

      @Inject OperationPagesInRegion(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<Operation>> fetchNextPage(final String regionName,
            final ListOptions options) {
         return new Function<String, ListPage<Operation>>() {
            @Override public ListPage<Operation> apply(String pageToken) {
               return api.operations().listPageInRegion(regionName, pageToken, options);
            }
         };
      }
   }

   /**
    * Retrieves the list of operation resources available in the specified zone.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("ZoneOperations:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/zones/{zone}/operations")
   ListPage<Operation> listPageInZone(@PathParam("zone") String zone,
         @Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listInZone(String, org.jclouds.googlecomputeengine.options.ListOptions) */
   @Named("ZoneOperations:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/zones/{zone}/operations")
   @Transform(OperationPagesInZone.class)
   Iterator<ListPage<Operation>> listInZone(@PathParam("zone") String zone);

   /** @see #listInZone(String, org.jclouds.googlecomputeengine.options.ListOptions) */
   @Named("ZoneOperations:list")
   @GET
   @Endpoint(CurrentProject.class)
   @Path("/zones/{zone}/operations")
   @Transform(OperationPagesInZone.class)
   Iterator<ListPage<Operation>> listInZone(@PathParam("zone") String zone, ListOptions options);

   static final class OperationPagesInZone extends BaseArg0ToIteratorOfListPage<Operation, OperationPagesInZone> {

      private final GoogleComputeEngineApi api;

      @Inject OperationPagesInZone(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<Operation>> fetchNextPage(final String zoneName, final ListOptions options) {
         return new Function<String, ListPage<Operation>>() {
            @Override public ListPage<Operation> apply(String pageToken) {
               return api.operations().listPageInZone(zoneName, pageToken, options);
            }
         };
      }
   }
}
