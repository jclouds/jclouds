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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseNetworks;
import org.jclouds.googlecomputeengine.options.ListOptions;
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
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to Networks via their REST API.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta16/networks"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface NetworkApi {

   /**
    * Returns the specified persistent network resource.
    *
    * @param networkName name of the persistent network resource to return.
    * @return a Network resource.
    */
   @Named("Networks:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/networks/{network}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Network get(@PathParam("network") String networkName);

   /**
    * Creates a persistent network resource in the specified project with the specified range.
    *
    * @param networkName the network name
    * @param IPv4Range   the range of the network to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Networks:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/networks")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   Operation createInIPv4Range(@PayloadParam("name") String networkName,
                               @PayloadParam("IPv4Range") String IPv4Range);

   /**
    * Creates a persistent network resource in the specified project with the specified range and specified gateway.
    *
    * @param networkName the network name
    * @param IPv4Range   the range of the network to be inserted.
    * @param gatewayIPv4 the range of the network to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Networks:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/networks")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   Operation createInIPv4RangeWithGateway(@PayloadParam("name") String networkName,
                                          @PayloadParam("IPv4Range") String IPv4Range,
                                          @PayloadParam("gatewayIPv4") String gatewayIPv4);

   /**
    * Deletes the specified persistent network resource.
    *
    * @param networkName name of the persistent network resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Networks:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/networks/{network}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("network") String networkName);

   /**
    * @see NetworkApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Network> listFirstPage();

   /**
    * @see NetworkApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Network> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the list of persistent network resources contained within the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Network> listAtMarker(@QueryParam("pageToken") @Nullable String marker,
                                  ListOptions options);

   /**
    * @see NetworkApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Transform(ParseNetworks.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Network> list();

   /**
    * A paged version of NetworkApi#list()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see NetworkApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Transform(ParseNetworks.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Network> list(ListOptions options);
}
