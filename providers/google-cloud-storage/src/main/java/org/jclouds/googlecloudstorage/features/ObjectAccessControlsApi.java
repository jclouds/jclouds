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
import org.jclouds.googlecloudstorage.domain.ListObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.ObjectAccessControls;
import org.jclouds.googlecloudstorage.domain.templates.ObjectAccessControlsTemplate;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to ObjectAccessControl entities via their REST API.
 *
 * @see <a href = " https://developers.google.com/storage/docs/json_api/v1/objectAccessControls "/>
 */

@SkipEncoding({ '/', '=' })
@RequestFilters(OAuthAuthenticator.class)
public interface ObjectAccessControlsApi {

   /**
    * Returns the acl entry for the specified entity on the specified object.
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param objectName
    *           Name of the bucket of that acl is related
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers
    *
    * @return an {@link ObjectAccessControls }
    */

   @Named("ObjectAccessControls:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ObjectAccessControls getObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName, @PathParam("entity") String entity);

   /**
    * Returns the acl entry for the specified entity on the specified object.
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param objectName
    *           Name of the object of that acl is related
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return an {@link ObjectAccessControls }
    */

   @Named("ObjectAccessControls:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ObjectAccessControls getObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName, @PathParam("entity") String entity,
            @QueryParam("generation") Long generation);

   /**
    * Creates a new acl entry for specified object
    *
    * @param bucketName
    *           Name of the bucket of that acl to be created In the request body, supply a ObjectAccessControls resource
    *           with the following properties
    * @param objectName
    *           Name of the bucket of that acl is related
    *
    * @return an {@link ObjectAccessControls }
    */

   @Named("ObjectAccessControls:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   ObjectAccessControls createObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName,
            @BinderParam(BindToJsonPayload.class) ObjectAccessControlsTemplate template);

   /**
    * Creates a new acl entry for specified object
    *
    * @param bucketName
    *           Name of the bucket of that acl to be created In the request body, supply a ObjectAccessControls resource
    *           with the following properties
    * @param objectName
    *           Name of the bucket of that acl is related
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return an {@link ObjectAccessControls }
    */

   @Named("ObjectAccessControls:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   ObjectAccessControls createObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName,
            @BinderParam(BindToJsonPayload.class) ObjectAccessControlsTemplate template,
            @QueryParam("generation") Long generation);

   /**
    * Permanently deletes the acl entry for the specified entity on the specified bucket.
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param objectName
    *           Name of the bucket of which acl is related
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers
    */

   @Named("ObjectAccessControls:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   void deleteObjectAccessControls(@PathParam("bucket") String bucketName, @PathParam("object") String objectName,
            @PathParam("entity") String entity);

   /**
    * Permanently deletes the acl entry for the specified entity on the specified bucket.
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param objectName
    *           Name of the bucket of that acl is related
    * @param generation
    *           If present, selects a specific revision of this object
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers
    */

   @Named("ObjectAccessControls:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   void deleteObjectAccessControls(@PathParam("bucket") String bucketName, @PathParam("object") String objectName,
            @PathParam("entity") String entity, @QueryParam("generation") Long generation);

   /**
    * Retrieves acl entries on a specified object
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param objectName
    *           Name of the bucket of that acl is related
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return {@link ListObjectAccessControls } resource
    *
    */

   @Named("ObjectAccessControls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ListObjectAccessControls listObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName);

   /**
    * Retrieves acl entries on a specified object
    *
    * @param bucketName
    *           Name of the bucket which contains the object
    * @param objectName
    *           Name of the bucket of that acl is related
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return a {@link ListObjectAccessControls }
    *
    */

   @Named("ObjectAccessControls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ListObjectAccessControls listObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName, @QueryParam("generation") Long generation);

   /**
    * Updates an acl entry on the specified object
    *
    * @param bucketName
    *           Name of the bucket of which contains the object
    * @param objectName
    *           Name of the object which acl is related
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers.
    * @param template
    *           Supply an {@link ObjectAccessControlsTemplate}
    *
    * @return an {@link ObjectAccessControls }
    */

   @Named("ObjectAccessControls:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ObjectAccessControls updateObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName, @PathParam("entity") String entity,
            @BinderParam(BindToJsonPayload.class) ObjectAccessControlsTemplate template);

   /**
    * Updates an acl entry on the specified object
    *
    * @param bucketName
    *           Name of the bucket of which contains the object
    * @param objectName
    *           Name of the object which acl is related *
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers
    * @param template
    *           Supply an {@link ObjectAccessControlsTemplate}
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return {@link ObjectAccessControls }
    */

   @Named("ObjectAccessControls:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ObjectAccessControls updateObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName, @PathParam("entity") String entity,
            @BinderParam(BindToJsonPayload.class) ObjectAccessControlsTemplate template,
            @QueryParam("generation") Long generation);

   /**
    * Updates an acl entry on the specified object
    *
    * @param bucketName
    *           Name of the bucket of which contains the object
    * @param objectName
    *           Name of the object which acl is related
    * @param template
    *           Supply an {@link ObjectAccessControlsTemplate}
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers.
    *
    * @return an {@link ObjectAccessControls }
    */

   @Named("ObjectAccessControls:patch")
   @PATCH
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ObjectAccessControls patchObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName, @PathParam("entity") String entity,
            @BinderParam(BindToJsonPayload.class) ObjectAccessControlsTemplate template);

   /**
    * Updates an acl entry on the specified object
    *
    * @param bucketName
    *           Name of the bucket of which contains the object
    * @param objectName
    *           Name of the object which acl is related
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers
    * @param template
    *           Supply an {@link ObjectAccessControlsTemplate}
    * @param generation
    *           If present, selects a specific revision of this object
    *
    * @return {@link ObjectAccessControls }
    */

   @Named("ObjectAccessControls:patch")
   @PATCH
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/o/{object}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ObjectAccessControls patchObjectAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("object") String objectName, @PathParam("entity") String entity,
            @BinderParam(BindToJsonPayload.class) ObjectAccessControlsTemplate template,
            @QueryParam("generation") Long generation);

}
