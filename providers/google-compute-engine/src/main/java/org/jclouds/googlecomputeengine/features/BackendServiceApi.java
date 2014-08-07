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

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;

import java.net.URI;
import java.util.Set;

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
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.BackendService;
import org.jclouds.googlecomputeengine.domain.BackendServiceGroupHealth;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.PATCH;
import org.jclouds.googlecomputeengine.functions.internal.ParseBackendServices;
import org.jclouds.googlecomputeengine.handlers.PayloadBinder;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to BackendServices via their REST API.
 * <p/>
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/backendServices"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface BackendServiceApi {
   /**
    * Returns the specified backend service resource.
    *
    * @param backendServiceName name of the backend service resource to return.
    * @return a BackendService resource.
    */
   @Named("BackendServices:get")
   @GET
   @Path("/global/backendServices/{backendService}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   BackendService get(@PathParam("backendService") String backendServiceName);

   /**
    * Creates a backend service resource in the specified project using the data
    * included in the request.
    *
    * @param name            the name of the backend service to be inserted.
    * @param backendService  options for this backend service.
    * @return an Operation resource. To check on the status of an operation,
    *         poll the Operations resource returned to you, and look for the
    *         status field.
    */
   @Named("BackendServices:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/backendServices")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(PayloadBinder.class)
   Operation create(@PayloadParam("name") String name,
                    @PayloadParam("options") BackendServiceOptions backendService);
   
   /**
    * Creates a backend service resource in the specified project using the data
    * included in the request.
    *
    * @param name            the name of the backend service to be inserted.
    * @param healthChecks    health checks to add to the backend service.
    * @return an Operation resource. To check on the status of an operation,
    *         poll the Operations resource returned to you, and look for the
    *         status field.
    */
   @Named("BackendServices:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/backendServices")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String name,
                    @PayloadParam("healthChecks") Set<URI> healthChecks);

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
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/backendServices/{backendService}")
   @OAuthScopes({COMPUTE_SCOPE})
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
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/backendServices/{backendService}")
   @OAuthScopes({COMPUTE_SCOPE})
   Operation patch(@PathParam("backendService") String backendServiceName,
                   @BinderParam(PayloadBinder.class) BackendServiceOptions backendServiceOptions);
   
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
   // The documentation does not reflect the fact that compute_scope is needed for this operation.
   // Running getHealth with compute_readonly_scope will return with an error saying the 
   // resource /projects/<project name> could not be found.
   @Named("BackendServices:getHealth")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/backendServices/{backendService}/getHealth")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   BackendServiceGroupHealth getHealth(@PathParam("backendService") String backendServiceName,
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
   @Path("/global/backendServices/{backendService}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("backendService") String backendServiceName);

   /**
    * @see BackendServiceApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("BackendServices:list")
   @GET
   @Path("/global/backendServices")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseBackendServices.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<BackendService> listFirstPage();

   /**
    * @see BackendServiceApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("BackendServices:list")
   @GET
   @Path("/global/backendServices")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseBackendServices.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<BackendService> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the list of backend service resources available to the specified
    * project. By default the list as a maximum size of 100, if no options are
    * provided or ListOptions#getMaxResults() has not been set.
    *
    * @param marker      marks the beginning of the next list page.
    * @param listOptions listing options.
    * @return a page of the list.
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("BackendServices:list")
   @GET
   @Path("/global/backendServices")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseBackendServices.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<BackendService> listAtMarker(@QueryParam("pageToken") @Nullable String marker, ListOptions options);

   /**
    * @see BackendServiceApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("BackendServices:list")
   @GET
   @Path("/global/backendServices")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseBackendServices.class)
   @Transform(ParseBackendServices.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<BackendService> list();

   /**
    * A paged version of BackendserviceApi#list().
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages
    *         when required.
    * @see PagedIterable
    * @see BackendServiceApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("BackendServices:list")
   @GET
   @Path("/global/backendServices")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseBackendServices.class)
   @Transform(ParseBackendServices.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<BackendService> list(ListOptions options);
}
