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
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;

import java.util.Iterator;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecomputeengine.GoogleComputeEngineFallbacks.EmptyIteratorOnNotFoundOr404;
import org.jclouds.googlecomputeengine.GoogleComputeEngineFallbacks.EmptyListPageOnNotFoundOr404;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseAddresses;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticationFilter.class)
@Path("/addresses")
@Consumes(APPLICATION_JSON)
public interface AddressApi {

   /** Returns an address by name or null if not found. */
   @Named("Addresses:get")
   @GET
   @Path("/{address}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Address get(@PathParam("address") String address);

   /**
    * Creates an address resource in the specified project specifying the size of the address.
    * 
    * @param address the name of address.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Addresses:insert")
   @POST
   @Produces(APPLICATION_JSON)
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String address);

   /** Deletes an address by name and returns the operation in progress, or null if not found. */
   @Named("Addresses:delete")
   @DELETE
   @Path("/{address}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("address") String address);

   /**
    * Retrieves the list of address resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param token       marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Addresses:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseAddresses.class)
   @Fallback(EmptyListPageOnNotFoundOr404.class)
   ListPage<Address> listPage(@Nullable @QueryParam("pageToken") String token, ListOptions listOptions);

   /**
    * @see #list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Addresses:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseAddresses.class)
   @Transform(ParseAddresses.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<Address>> list();

   /**
    * @see #list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Addresses:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseAddresses.class)
   @Transform(ParseAddresses.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<Address>> list(ListOptions options);
}
