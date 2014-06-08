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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Route;
import org.jclouds.googlecomputeengine.functions.internal.ParseRoutes;
import org.jclouds.googlecomputeengine.handlers.RouteBinder;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.RouteOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

/**
 * Provides access to Routes via their REST API.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/routess"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface RouteApi {

   /**
    * Returns the specified route resource
    *
    * @param routeName name of the region resource to return.
    * @return If successful, this method returns a Route resource
    */
   @Named("Routes:get")
   @GET
   @Path("/global/routes/{route}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Route get(@PathParam("route") String routeName);

   /**
    * @see org.jclouds.googlecomputeengine.features.RouteApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Routes:list")
   @GET
   @Path("/global/routes")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseRoutes.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Route> listFirstPage();

   /**
    * @see org.jclouds.googlecomputeengine.features.RouteApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Routes:list")
   @GET
   @Path("/global/routes")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseRoutes.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Route> listAtMarker(String marker);

   /**
    * Retrieves the listFirstPage of route resources available to the specified project.
    * By default the listFirstPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults()
    * has not been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the listFirstPage
    * @see org.jclouds.googlecomputeengine.options.ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("Routes:list")
   @GET
   @Path("/global/routes")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseRoutes.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Route> listAtMarker(String marker, ListOptions listOptions);

   /**
    * @see org.jclouds.googlecomputeengine.features.RouteApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Routes:list")
   @GET
   @Path("/global/routes")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseRoutes.class)
   @Transform(ParseRoutes.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Route> list();

   /**
    * A paged version of RegionApi#listFirstPage()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see org.jclouds.googlecomputeengine.features.RouteApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    * @see org.jclouds.collect.PagedIterable
    */
   @Named("Routes:list")
   @GET
   @Path("/global/routes")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseRoutes.class)
   @Transform(ParseRoutes.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Route> list(ListOptions listOptions);

   /**
    * Deletes the specified route resource.
    *
    * @param routeName name of the route resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the route did not exist the result is null.
    */
   @Named("Routes:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/routes/{route}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("route") String routeName);

   /**
    * Creates a route resource in the specified project using the data included in the request.
    *
    * @param name            the name of the route to be inserted.
    * @param network         the network to which to add the route
    * @param routeOptions the options of the route to add
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Routes:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/routes")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(RouteBinder.class)
   Operation createInNetwork(@PayloadParam("name") String name,
                             @PayloadParam("network") URI network,
                             @PayloadParam("options") RouteOptions routeOptions);

}
