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

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyMapOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.binders.BindMetadataToJsonPayload;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.functions.internal.OnlyMetadataValueOrNull;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseImageDetails;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseImages;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to the OpenStack Compute (Nova) Image API.
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/images")
public interface ImageApi {
   /**
    * List all images (IDs, names, links)
    *
    * @return all images (IDs, names, links)
    */
   @Named("image:list")
   @GET
   @ResponseParser(ParseImages.class)
   @Transform(ParseImages.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Resource> list();

   @Named("image:list")
   @GET
   @ResponseParser(ParseImages.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<Resource> list(PaginationOptions options);

   /**
    * List all images (all details)
    *
    * @return all images (all details)
    */
   @Named("image:list")
   @GET
   @Path("/detail")
   @ResponseParser(ParseImageDetails.class)
   @Transform(ParseImageDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Image> listInDetail();

   @Named("image:list")
   @GET
   @Path("/detail")
   @ResponseParser(ParseImageDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<Image> listInDetail(PaginationOptions options);

   /**
    * List details of the specified image
    *
    * @param id
    *           id of the server
    * @return server or null if not found
    */
   @Named("image:get")
   @GET
   @Path("/{id}")
   @SelectJson("image")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Image get(@PathParam("id") String id);

   /**
    * Delete the specified image
    *
    * @param id
    *           id of the image
    * @return server or null if not found
    */
   @Named("image:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("id") String id);

   /**
    * List all metadata for an image.
    *
    * @param id
    *           id of the image
    * @return the metadata as a Map<String, String>
    */
   @Named("image:getMetadata")
   @GET
   @Path("/{id}/metadata")
   @SelectJson("metadata")
   @Fallback(EmptyMapOnNotFoundOr404.class)
   Map<String, String> getMetadata(@PathParam("id") String id);

   /**
    * Sets the metadata for an image.
    *
    * @param id
    *           id of the image
    * @param metadata
    *           a Map containing the metadata
    * @return the metadata as a Map<String, String>
    */
   @Named("image:setMetadata")
   @PUT
   @Path("/{id}/metadata")
   @SelectJson("metadata")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   Map<String, String> setMetadata(@PathParam("id") String id, @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * Update the metadata for a server.
    *
    * @param id
    *           id of the image
    * @param metadata
    *           a Map containing the metadata
    * @return the metadata as a Map<String, String>
    */
   @Named("image:updateMetadata")
   @POST
   @Path("/{id}/metadata")
   @SelectJson("metadata")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   Map<String, String> updateMetadata(@PathParam("id") String id, @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * Update the metadata for an image.
    *
    * @param id
    *           id of the image
    * @param metadata
    *           a Map containing the metadata
    * @return the value or null if not present
    */
   @Named("image:getMetadata")
   @GET
   @Path("/{id}/metadata/{key}")
   @ResponseParser(OnlyMetadataValueOrNull.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   String getMetadata(@PathParam("id") String id, @PathParam("key") String key);

   /**
    * Set a metadata item for an image.
    *
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    * @param value
    *           the value of the metadata item
    * @return the value you updated
    */
   @Named("image:updateMetadata")
   @PUT
   @Path("/{id}/metadata/{key}")
   @ResponseParser(OnlyMetadataValueOrNull.class)
   @MapBinder(BindMetadataToJsonPayload.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   String updateMetadata(@PathParam("id") String id, @PathParam("key") @PayloadParam("key") String key,
         @PathParam("value") @PayloadParam("value") String value);

   /**
    * Delete a metadata item from an image.
    *
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    */
   @Named("image:deleteMetadata")
   @DELETE
   @Path("/{id}/metadata/{key}")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteMetadata(@PathParam("id") String id, @PathParam("key") String key);
}
