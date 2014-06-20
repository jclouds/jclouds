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
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls;
import org.jclouds.googlecloudstorage.domain.ListBucketAccessControls;
import org.jclouds.googlecloudstorage.handlers.BucketAccessControlsBinder;
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
 * Provides access to BucketAccessControl entities via their REST API.
 *
 * @see <a href = " https://developers.google.com/storage/docs/json_api/v1/bucketAccessControls "/>
 */

@SkipEncoding({ '/', '=' })
@RequestFilters(OAuthAuthenticator.class)
public interface BucketAccessControlsApi {

   /**
    * Returns the ACL entry for the specified entity on the specified bucket.
    *
    * @param bucketName
    *           Name of the bucket which ACL is related
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers.
    *
    * @return a BucketAccessControls resource
    */

   @Named("BucketAccessControls:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   BucketAccessControls getBucketAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("entity") String entity);

   /**
    * Creates a new ACL entry on the specified bucket.
    *
    * @param bucketName
    *           Name of the bucket of which ACL to be created
    *
    * @param bucketAccessControls
    *           In the request body,supply a BucketAccessControls resource with role and entity
    *
    * @return If successful, this method returns a BucketAccessControls resource in the response body
    */

   @Named("BucketAccessControls:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/acl")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @MapBinder(BucketAccessControlsBinder.class)
   BucketAccessControls createBucketAccessControls(@PathParam("bucket") String bucketName,
            @PayloadParam("BACLInsert") BucketAccessControls bucketAccessControls);

   /**
    * Permanently deletes the ACL entry for the specified entity on the specified bucket.
    *
    * @param bucketName
    *           Name of the bucket of that ACL is related
    * @return If successful, this method returns an empty response body.
    */

   @Named("BucketAccessControls:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   HttpResponse deleteBucketAccessControls(@PathParam("bucket") String bucketName, @PathParam("entity") String entity);

   /**
    * Retrieves all ACL entries on a specified bucket
    *
    * @param bucketName
    *           Name of the bucket which ACL is related
    *
    * @return ListBucketAccessControls resource
    */

   @Named("BucketAccessControls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/acl")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ListBucketAccessControls listBucketAccessControls(@PathParam("bucket") String bucketName);

   /**
    * Updates an ACL entry on the specified bucket
    *
    * @param bucketName
    *           Name of the bucket which ACL to be created
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers. In the request body, supply a
    *           BucketAccessControls resource with role
    *
    * @return If successful, this method returns a BucketAccessControls resource in the response body
    */

   @Named("BucketAccessControls:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   BucketAccessControls updateBucketAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("entity") String entity,
            @BinderParam(BindToJsonPayload.class) BucketAccessControls bucketAccessControls);

   /**
    * Updates an ACL entry on the specified bucket.
    *
    * @param bucketName
    *           Name of the bucket which ACL to be created
    * @param entity
    *           The entity holding the permission. Can be user-userId, user-emailAddress, group-groupId,
    *           group-emailAddress, allUsers, or allAuthenticatedUsers
    *
    * @param bucketAccessControls
    *           In the request body, supply a BucketAccessControls resource with role
    *
    * @return If successful, this method returns a BucketAccessControls resource in the response body
    */

   @Named("BucketAccessControls:patch")
   @PATCH
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}/acl/{entity}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   BucketAccessControls patchBucketAccessControls(@PathParam("bucket") String bucketName,
            @PathParam("entity") String entity,
            @BinderParam(BindToJsonPayload.class) BucketAccessControls bucketAccessControls);
}
