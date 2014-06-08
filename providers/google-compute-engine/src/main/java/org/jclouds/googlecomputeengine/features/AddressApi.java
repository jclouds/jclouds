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
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseAddresses;
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
 * Provides access to Addresses via their REST API.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/addresses"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface AddressApi {

   /**
    * Returns the specified address resource.
    *
    * @param region     Name of the region the address is in.
    * @param addressName name of the address resource to return.
    * @return a Address resource.
    */
   @Named("Addresss:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/addresses/{address}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Address getInRegion(@PathParam("region") String region, @PathParam("address") String addressName);

   /**
    * Creates a address resource in the specified project specifying the size of the address.
    *
    *
    * @param region     the name of the region where the address is to be created.
    * @param addressName the name of address.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Addresss:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/addresses")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   Operation createInRegion(@PathParam("region") String region, @PayloadParam("name") String addressName);

   /**
    * Deletes the specified address resource.
    *
    * @param region     the region the address is in.
    * @param addressName name of the address resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Addresss:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/addresses/{address}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation deleteInRegion(@PathParam("region") String region, @PathParam("address") String addressName);

   /**
    * @see org.jclouds.googlecomputeengine.features.AddressApi#listAtMarkerInRegion(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Addresss:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/addresses")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseAddresses.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Address> listFirstPageInRegion(@PathParam("region") String region);

   /**
    * @see org.jclouds.googlecomputeengine.features.AddressApi#listAtMarkerInRegion(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Addresss:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/addresses")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseAddresses.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Address> listAtMarkerInRegion(@PathParam("region") String region, @QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the listPage of address resources contained within the specified project and region.
    * By default the listPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has
    * not been set.
    *
    * @param region        the region to search in
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the listPage
    * @see org.jclouds.googlecomputeengine.options.ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("Addresss:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/addresses")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseAddresses.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Address> listAtMarkerInRegion(@PathParam("region") String region, @QueryParam("pageToken") @Nullable String marker, ListOptions listOptions);

   /**
    * A paged version of AddressApi#listPageInRegion(String)
    *
    * @param region the region to list in
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see org.jclouds.collect.PagedIterable
    * @see org.jclouds.googlecomputeengine.features.AddressApi#listAtMarkerInRegion(String, String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Addresss:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/addresses")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseAddresses.class)
   @Transform(ParseAddresses.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Address> listInRegion(@PathParam("region") String region);

   @Named("Addresss:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/regions/{region}/addresses")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseAddresses.class)
   @Transform(ParseAddresses.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Address> listInRegion(@PathParam("region") String region, ListOptions options);

}
