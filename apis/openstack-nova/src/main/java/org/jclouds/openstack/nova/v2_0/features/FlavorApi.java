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
package org.jclouds.openstack.nova.v2_0.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseFlavorDetails;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseFlavors;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.WrapWith;

/**
 * Provides access to the OpenStack Compute (Nova) Flavor API.
 * <p/>
 *
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/flavors")
public interface FlavorApi {
   /**
    * List all flavors (IDs, names, links)
    *
    * @return all flavors (IDs, names, links)
    */
   @Named("flavor:list")
   @GET
   @ResponseParser(ParseFlavors.class)
   @Transform(ParseFlavors.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Resource> list();

   @Named("flavor:list")
   @GET
   @ResponseParser(ParseFlavors.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<Resource> list(PaginationOptions options);

   /**
    * List all flavors (all details)
    *
    * @return all flavors (all details)
    */
   @Named("flavor:list")
   @GET
   @Path("/detail")
   @ResponseParser(ParseFlavorDetails.class)
   @Transform(ParseFlavorDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Flavor> listInDetail();

   @Named("flavor:list")
   @GET
   @Path("/detail")
   @ResponseParser(ParseFlavorDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<Flavor> listInDetail(PaginationOptions options);

   /**
    * List details of the specified flavor
    *
    * @param id
    *           id of the flavor
    * @return flavor or null if not found
    */
   @Named("flavor:get")
   @GET
   @Path("/{id}")
   @SelectJson("flavor")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Flavor get(@PathParam("id") String id);

   /**
    * Create flavor according to the provided object
    *
    * @param flavor - flavor object
    * @return newly created flavor
    */
   @Named("flavor:create")
   @POST
   @Unwrap
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Flavor create(@WrapWith("flavor") Flavor flavor);

   /**
    * Delete flavor with a given id
    *
    * @param id - flavor id
    */
   @Named("flavor:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("id") String id);
}
