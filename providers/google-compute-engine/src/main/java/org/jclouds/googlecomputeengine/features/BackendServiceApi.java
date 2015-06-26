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

import java.net.URI;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.BackendService;
import org.jclouds.googlecomputeengine.domain.HealthStatus;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Consumes(APPLICATION_JSON)
public interface BackendServiceApi {
   /**
    * Returns the specified backend service resource.
    *
    * @param backendServiceName name of the backend service resource to return.
    * @return a BackendService resource.
    */
   @Named("BackendServices:get")
   @GET
   @Path("/{backendService}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   BackendService get(@PathParam("backendService") String backendServiceName);

   /**
    * Creates a backend service resource in the specified project using the data
    * included in the request.
    *
    * @param backendService  options for this backend service.
    * @return an Operation resource. To check on the status of an operation,
    *         poll the Operations resource returned to you, and look for the
    *         status field.
    */
   @Named("BackendServices:insert")
   @POST
   @Produces(APPLICATION_JSON)
   Operation create(@BinderParam(BindToJsonPayload.class) BackendServiceOptions backendService);

   /**
    * Updates the specified backend service resource with the data included in
    * the request.
    *
    * @param backendServiceName    the name backend service to be updated.
    * @param backendServiceOptions the new backend service.
    * @return an Operation resource. To check on the status of an operation,
    *         poll the Operations resource returned to you, and look for the
    *         status field.
    */
   @Named("BackendServices:update")
   @PUT
   @Produces(APPLICATION_JSON)
   @Path("/{backendService}")
   Operation update(@PathParam("backendService") String backendServiceName,
                    @BinderParam(BindToJsonPayload.class) BackendServiceOptions backendServiceOptions);

   /**
    * Updates the specified backend service resource, with patch semantics, with
    * the data included in the request.
    *
    * @param backendServiceName    the name backend service to be updated.
    * @param backendServiceOptions the new backend service.
    * @return an Operation resource. To check on the status of an operation,
    *         poll the Operations resource returned to you, and look for the
    *         status field.
    */
   @Named("BackendServices:patch")
   @PATCH
   @Produces(APPLICATION_JSON)
   @Path("/{backendService}")
   Operation patch(@PathParam("backendService") String backendServiceName,
                   @BinderParam(BindToJsonPayload.class) BackendServiceOptions backendServiceOptions);

   /**
    * Gets the most recent health check results for this backend service. Note
    * that health check results will only be returned if the backend service has
    *  a valid global forwarding rule referencing it.
    *
    * @param backendServiceName    the name backend service to get health stats on.
    * @param group                 the group in the backend service to get health stats on.
    * @return a BackendServiceGroupHealth resource denoting the health states of
    *         instances in the specified group.
    */
   @Named("BackendServices:getHealth")
   @POST
   @Produces(APPLICATION_JSON)
   @Path("/{backendService}/getHealth")
   @MapBinder(BindToJsonPayload.class)
   HealthStatus getHealth(@PathParam("backendService") String backendServiceName,
                                       @PayloadParam("group") URI group);

   /**
    * Deletes the specified backend service resource.
    *
    * @param backendServiceName  name of the backend service resource to delete.
    * @return an Operation resource. To check on the status of an operation,
    *         poll the Operations resource returned to you, and look for the
    *         status field.
    */
   @Named("BackendServices:delete")
   @DELETE
   @Path("/{backendService}")
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("backendService") String backendServiceName);

   /**
    * Retrieves the list of backend service resources available to the specified
    * project. By default the list as a maximum size of 100, if no options are
    * provided or ListOptions#getMaxResults() has not been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("BackendServices:list")
   @GET
   ListPage<BackendService> listPage(@QueryParam("pageToken") @Nullable String pageToken, ListOptions options);

   /** @see #listPage(String, ListOptions) */
   @Named("BackendServices:list")
   @GET
   @Transform(BackendServicePages.class)
   Iterator<ListPage<BackendService>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("BackendServices:list")
   @GET
   @Transform(BackendServicePages.class)
   Iterator<ListPage<BackendService>> list(ListOptions options);

   static final class BackendServicePages extends BaseToIteratorOfListPage<BackendService, BackendServicePages> {

      private final GoogleComputeEngineApi api;

      @Inject BackendServicePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<BackendService>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<BackendService>>() {
            @Override public ListPage<BackendService> apply(String pageToken) {
               return api.backendServices().listPage(pageToken, options);
            }
         };
      }
   }
}
