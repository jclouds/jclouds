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
package org.jclouds.googlecloudstorage.features;

import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.STORAGE_FULLCONTROL_SCOPE;

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

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloudstorage.domain.DefaultObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.ListDefaultObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.DefaultObjectAccessControlsTemplate;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.handlers.DefaultObjectAccessControlsBinder;
import org.jclouds.http.HttpResponse;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to DefaultObjectAccessControl entities via their REST API.
 *
 * @see <a href = " https://developers.google.com/storage/docs/json_api/v1/defaultObjectAccessControls"/>
 */

@SkipEncoding({ '/', '=' })
@RequestFilters(OAuthAuthenticator.class)
public interface DefaultObjectAccessControlsApi {

   /**
    * Returns the ACL entry for the specified entity on the specified object.
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers
    *
    * @return an DefaultObjectAccessControls resource
    */

   @Named("DefaultObjectAccessControls:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/defaultObjectAcl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   DefaultObjectAccessControls getDefaultObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("entity") String entity);

   /**
    * Creates a new ACL entry for specified object
    *
    * @param bucketName
    *           Name of the bucket of that ACL to be created In the request body, supply a DefaultObjectAccessControls
    *           resource with the following properties
    *
    * @return If successful, this method returns a DefaultObjectAccessControls resource
    */

   @Named("DefaultObjectAccessControls:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/defaultObjectAcl")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @MapBinder(DefaultObjectAccessControlsBinder.class)
   DefaultObjectAccessControls createDefaultObjectAccessControls(@PathParam("bucket") String bucketName,
            @PayloadParam("template") DefaultObjectAccessControlsTemplate template);

   /**
    * Permanently deletes the DefaultObjectAcessControl entry for the specified entity on the specified bucket.
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers
    *
    * @return If successful, this method returns an empty response body
    */

   @Named("DefaultObjectAccessControls:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/defaultObjectAcl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   HttpResponse deleteDefaultObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("entity") String entity);

   /**
    * Retrieves ACL entries on a specified object
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param objectName
    *           Name of the bucket of that ACL is related
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return ListObjectAccessControls resource
    *
    */

   @Named("DefaultObjectAccessControls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/defaultObjectAcl")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ListDefaultObjectAccessControls listDefaultObjectAccessControls(@PathParam("bucket") String bucketName);

   /**
    * Retrieves ACL entries on a specified object
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return DefaultObjectAccessControls resource
    *
    */

   @Named("DefaultObjectAccessControls:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/defaultObjectAcl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   DefaultObjectAccessControls updateDefaultObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("entity") String entity,
            @BinderParam(BindToJsonPayload.class) DefaultObjectAccessControls payload);

   /**
    * Retrieves ACL entries on a specified object
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return DefaultObjectAccessControls resource
    *
    */

   @Named("DefaultObjectAccessControls:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/defaultObjectAcl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   DefaultObjectAccessControls updateDefaultObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("entity") String entity,
            @BinderParam(BindToJsonPayload.class) DefaultObjectAccessControls payload,
            @QueryParam("role") ObjectRole role);

   /**
    * Retrieves ACL entries on a specified object
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return DefaultObjectAccessControls resource
    *
    */

   @Named("DefaultObjectAccessControls:patch")
   @PATCH
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/defaultObjectAcl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   DefaultObjectAccessControls patchDefaultObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("entity") String entity,
            @BinderParam(BindToJsonPayload.class) DefaultObjectAccessControls payload);
}
