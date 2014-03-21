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

import com.google.common.base.Optional;

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

import org.jclouds.Fallbacks.AbsentOn403Or404Or500;
import org.jclouds.Fallbacks.EmptyMapOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.binders.BindMetadataToJsonPayload;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.functions.ParseImageIdFromLocationHeader;
import org.jclouds.openstack.nova.v2_0.functions.internal.OnlyMetadataValueOrNull;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseDiagnostics;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseServerDetails;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseServers;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.options.RebuildServerOptions;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to the OpenStack Compute (Nova) Server API.
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/servers")
public interface ServerApi {
   /**
    * List all servers (IDs, names, links)
    *
    * @return all servers (IDs, names, links)
    */
   @Named("server:list")
   @GET
   @ResponseParser(ParseServers.class)
   @Transform(ParseServers.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Resource> list();

   @Named("server:list")
   @GET
   @ResponseParser(ParseServers.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<Resource> list(PaginationOptions options);

   /**
    * List all servers (all details)
    *
    * @return all servers (all details)
    */
   @Named("server:list")
   @GET
   @Path("/detail")
   @ResponseParser(ParseServerDetails.class)
   @Transform(ParseServerDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Server> listInDetail();

   @Named("server:list")
   @GET
   @Path("/detail")
   @ResponseParser(ParseServerDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<Server> listInDetail(PaginationOptions options);

   /**
    * List details of the specified server
    *
    * @param id
    *           id of the server
    * @return server or null if not found
    */
   @Named("server:get")
   @GET
   @Path("/{id}")
   @SelectJson("server")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Server get(@PathParam("id") String id);

   /**
    * Create a new server
    *
    * @param name
    *           name of the server to create
    * @param imageRef
    *           reference to the image for the server to use
    * @param flavorRef
    *           reference to the flavor to use when creating the server
    * @param options
    *           optional parameters to be passed into the server creation
    *           request
    * @return the newly created server
    */
   @Named("server:create")
   @POST
   @Unwrap
   @MapBinder(CreateServerOptions.class)
   ServerCreated create(@PayloadParam("name") String name, @PayloadParam("imageRef") String imageRef,
         @PayloadParam("flavorRef") String flavorRef, CreateServerOptions... options);

   /**
    * Terminate and delete a server.
    *
    * @param id
    *           id of the server
    * @return True if successful, False otherwise
    */
   @Named("server:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);

   /**
    * Start a server
    *
    * @param id
    *           id of the server
    */
   @Named("server:start")
   @POST
   @Path("/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"os-start\":null}")
   void start(@PathParam("id") String id);

   /**
    * Stop a server
    *
    * @param id
    *           id of the server
    */
   @Named("server:stop")
   @POST
   @Path("/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"os-stop\":null}")
   void stop(@PathParam("id") String id);

   /**
    * Reboot a server.
    *
    * @param id
    *           id of the server
    * @param rebootType
    *           The type of reboot to perform (Hard/Soft)
    */
   @Named("server:reboot")
   @POST
   @Path("/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"reboot\":%7B\"type\":\"{type}\"%7D%7D")
   void reboot(@PathParam("id") String id, @PayloadParam("type") RebootType rebootType);

   /**
    * Resize a server to a new flavor size.
    *
    * @param id
    *           id of the server
    * @param flavorId
    *           id of the new flavor to use
    */
   @Named("server:resize")
   @POST
   @Path("/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"resize\":%7B\"flavorRef\":{flavorId}%7D%7D")
   void resize(@PathParam("id") String id, @PayloadParam("flavorId") String flavorId);

   /**
    * Confirm a resize operation.
    *
    * @param id
    *           id of the server
    */
   @Named("server:confirmResize")
   @POST
   @Path("/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"confirmResize\":null}")
   void confirmResize(@PathParam("id") String id);

   /**
    * Revert a resize operation.
    *
    * @param id
    *           id of the server
    */
   @Named("server:revertResize")
   @POST
   @Path("/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"revertResize\":null}")
   void revertResize(@PathParam("id") String id);

   /**
    * Rebuild a server.
    *
    * @param id
    *           id of the server
    * @param options
    *           Optional parameters to the rebuilding operation.
    */
   @Named("server:rebuild")
   @POST
   @Path("/{id}/action")
   @MapBinder(RebuildServerOptions.class)
   void rebuild(@PathParam("id") String id, RebuildServerOptions... options);

   /**
    * Change the administrative password to a server.
    *
    * @param id
    *           id of the server
    * @param adminPass
    *           The new administrative password to use
    */
   @Named("server:changeAdminPass")
   @POST
   @Path("/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"changePassword\":%7B\"adminPass\":\"{adminPass}\"%7D%7D")
   void changeAdminPass(@PathParam("id") String id, @PayloadParam("adminPass") String adminPass);

   /**
    * Rename a server.
    *
    * @param id
    *           id of the server
    * @param newName
    *           The new name for the server
    */
   @Named("server:rename")
   @PUT
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"server\":%7B\"name\":\"{name}\"%7D%7D")
   void rename(@PathParam("id") String id, @PayloadParam("name") String newName);

   /**
    * Create an image from a server.
    *
    * @param name
    *           The name of the new image
    * @param id
    *           id of the server
    *
    * @return ID of the new / updated image
    */
   @Named("server:createImageFromServer")
   @POST
   @Path("/{id}/action")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"createImage\":%7B\"name\":\"{name}\", \"metadata\": %7B%7D%7D%7D")
   @ResponseParser(ParseImageIdFromLocationHeader.class)
   @Fallback(MapHttp4xxCodesToExceptions.class)
   String createImageFromServer(@PayloadParam("name") String name, @PathParam("id") String id);

   /**
    * List all metadata for a server.
    *
    * @param id
    *           id of the server
    *
    * @return the metadata as a Map<String, String>
    */
   @Named("server:getMetadata")
   @GET
   @Path("/{id}/metadata")
   @SelectJson("metadata")
   @Fallback(EmptyMapOnNotFoundOr404.class)
   Map<String, String> getMetadata(@PathParam("id") String id);

   /**
    * Set the metadata for a server.
    *
    * @param id
    *           id of the server
    * @param metadata
    *           a Map containing the metadata
    * @return the metadata as a Map<String, String>
    */
   @Named("server:setMetadata")
   @PUT
   @Path("/{id}/metadata")
   @SelectJson("metadata")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   Map<String, String> setMetadata(@PathParam("id") String id,
         @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * Update the metadata for a server.
    *
    * @param id
    *           id of the server
    * @param metadata
    *           a Map containing the metadata
    * @return the metadata as a Map<String, String>
    */
   @Named("server:updateMetadata")
   @POST
   @Path("/{id}/metadata")
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("metadata")
   @MapBinder(BindToJsonPayload.class)
   @Fallback(EmptyMapOnNotFoundOr404.class)
   Map<String, String> updateMetadata(@PathParam("id") String id,
         @PayloadParam("metadata") Map<String, String> metadata);

   /**
    * Update the metadata for a server.
    *
    * @param id
    *           id of the image
    * @param metadata
    *           a Map containing the metadata
    * @return the value or null if not present
    */
   @Named("server:getMetadata")
   @GET
   @Path("/{id}/metadata/{key}")
   @ResponseParser(OnlyMetadataValueOrNull.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   String getMetadata(@PathParam("id") String id, @PathParam("key") String key);

   /**
    * Set a metadata item for a server.
    *
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    * @param value
    *           the value of the metadata item
    * @return the value you updated
    */
   @Named("server:updateMetadata")
   @PUT
   @Path("/{id}/metadata/{key}")
   @Produces(MediaType.APPLICATION_JSON)
   @ResponseParser(OnlyMetadataValueOrNull.class)
   @MapBinder(BindMetadataToJsonPayload.class)
   String updateMetadata(@PathParam("id") String id, @PathParam("key") @PayloadParam("key") String key,
         @PathParam("value") @PayloadParam("value") String value);

   /**
    * Delete a metadata item from a server.
    *
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    */
   @Named("server:deleteMetadata")
   @DELETE
   @Path("/{id}/metadata/{key}")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteMetadata(@PathParam("id") String id, @PathParam("key") String key);

   /**
    * Get usage information about the server such as CPU usage, Memory and IO.
    * The information returned by this method is dependent on the hypervisor
    * in use by the OpenStack installation and whether that hypervisor supports
    * this method. More information can be found in the
    * <a href="http://api.openstack.org/api-ref.html"> OpenStack API
    * reference</a>. <br/>
    * At the moment the returned response is a generic map. In future versions
    * of OpenStack this might be subject to change.
    *
    * @param id
    *           id of the server
    * @return A Map containing the collected values organized by key - value.
    */
   @Named("server:getDiagnostics")
   @GET
   @Path("/{id}/diagnostics")
   @ResponseParser(ParseDiagnostics.class)
   @Fallback(AbsentOn403Or404Or500.class)
   Optional<Map<String, String>> getDiagnostics(@PathParam("id") String id);
}
