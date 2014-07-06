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
import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.STORAGE_READONLY_SCOPE;

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
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.ListPage;
import org.jclouds.googlecloudstorage.handlers.BucketBinder;
import org.jclouds.googlecloudstorage.options.DeleteBucketOptions;
import org.jclouds.googlecloudstorage.options.GetBucketOptions;
import org.jclouds.googlecloudstorage.options.InsertBucketOptions;
import org.jclouds.googlecloudstorage.options.ListOptions;
import org.jclouds.googlecloudstorage.options.UpdateBucketOptions;
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
 * Provides access to Bucket entities via their REST API.
 *
 * @see <a href = "https://developers.google.com/storage/docs/json_api/v1/buckets"/>
 */

@SkipEncoding({ '/', '=' })
@RequestFilters(OAuthAuthenticator.class)
public interface BucketApi {

   /**
    * Returns metadata for the specified bucket.
    *
    * @param bucketName
    *           Name of the bucket
    *
    * @return a {@link Bucket} resource
    */
   @Named("Bucket:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}")
   @OAuthScopes(STORAGE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Bucket getBucket(@PathParam("bucket") String bucketName);

   /**
    * Returns metadata for the specified bucket
    *
    * @param bucketName
    *           Name of the bucket
    * @param options
    *           Supply {@link GetBucketOptions} with optional query parameters
    *
    * @return a {@link Bucket} resource
    */
   @Named("Bucket:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}")
   @OAuthScopes(STORAGE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Bucket getBucket(@PathParam("bucket") String bucketName, GetBucketOptions options);

   /**
    * Creates a new bucket
    *
    * @param projectId
    *           A valid API project identifier
    * @param bucketTemplate
    *           supply a {@link BucketTemplate} resource
    *
    * @return If successful, this method returns a {@link Bucket} resource.
    */
   @Named("Bucket:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @MapBinder(BucketBinder.class)
   Bucket createBucket(@QueryParam("project") String projectId, @PayloadParam("template") BucketTemplate bucketTemplate);

   /**
    * Creates a new Bucket
    *
    * @param projectId
    *           A valid API project identifier
    *
    * @param bucketTemplate
    *           Supply a {@link BucketTemplate} resource
    * @param options
    *           Supply {@link InsertBucketOptions} with optional query parameters
    *
    * @return If successful, this method returns a {@link Bucket} resource.
    */
   @Named("Bucket:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @MapBinder(BucketBinder.class)
   Bucket createBucket(@QueryParam("project") String projectId,
            @PayloadParam("template") BucketTemplate bucketTemplate, InsertBucketOptions options);

   /**
    * Permanently deletes an empty Bucket.If bucket is not empty 409 error to indicate the conflict.  
    *
    * @param bucketName
    *           Name of the bucket
    */
   @Named("Bucket:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Nullable
   void deleteBucket(@PathParam("bucket") String bucketName);

   /**
    * Permanently deletes an empty Bucket.If bucket is not empty 409 error to indicate the conflict.
    *
    * @param bucketName
    *           Name of the bucket
    * @param options
    *           Supply {@link DeleteBucketOptions} with optional query parameters
    */
   @Named("Bucket:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Nullable
   void deleteBucket(@PathParam("bucket") String bucketName, DeleteBucketOptions options);

   /**
    * Retrieves a list of buckets for a given project
    *
    * @param projectId
    *           A valid API project identifier
    *
    * @return a {@link ListPage<Bucket>}
    */
   @Named("Bucket:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ListPage<Bucket> listBucket(@QueryParam("project") String projectId);

   /**
    * Retrieves a list of buckets for a given project
    *
    * @param projectId
    *           A valid API project identifier
    * @param options
    *           Supply {@link ListOptions} with optional query parameters
    */
   @Named("Bucket:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ListPage<Bucket> listBucket(@QueryParam("project") String projectId, ListOptions options);

   /**
    * Updates a bucket
    *
    * @param bucketName
    *           Name of the bucket
    * @param bucketTemplate
    *           Supply a {@link BucketTemplate} resource with list of {@link BucketAccessControls}
    *
    * @return If successful, this method returns the updated {@link Bucket} resource.
    */
   @Named("Bucket:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Bucket updateBucket(@PathParam("bucket") String bucketName,
            @BinderParam(BindToJsonPayload.class) BucketTemplate bucketTemplate);

   /**
    * Updates a bucket
    *
    * @param bucketName
    *           Name of the bucket
    * @param bucketTemplate
    *           Supply a {@link BucketTemplate} resource with list of {@link BucketAccessControls}
    * @param options
    *           Supply {@link UpdateBucketOptions} with optional query parameters
    *
    * @return If successful,this method returns the updated {@link Bucket} resource.
    */
   @Named("Bucket:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Bucket updateBucket(@PathParam("bucket") String bucketName,
            @BinderParam(BindToJsonPayload.class) BucketTemplate bucketTemplate, UpdateBucketOptions options);

   /**
    * Updates a bucket supporting patch semantics.
    *
    *  @see <a href = "https://developers.google.com/storage/docs/json_api/v1/how-tos/performance#patch"/>
    *
    * @param bucketName
    *           Name of the bucket
    * @param bucketTemplate
    *           Supply a {@link BucketTemplate} resource with list of {@link BucketAccessControls}
    *
    * @return If successful, this method returns the updated {@link Bucket} resource.
    */
   @Named("Bucket:patch")
   @PATCH
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Bucket patchBucket(@PathParam("bucket") String bucketName,
            @BinderParam(BindToJsonPayload.class) BucketTemplate bucketTemplate);

   /**
    * Updates a bucket supporting patch semantics.
    *
    * @see <a href = "https://developers.google.com/storage/docs/json_api/v1/how-tos/performance#patch"/>
    *
    * @param bucketName
    *           Name of the bucket
    * @param bucketTemplate
    *           Supply a {@link BucketTemplate} resource with list of {@link BucketAccessControls}
    * @param options
    *           Supply {@link UpdateBucketOptions} with optional query parameters
    *
    * @return If successful, this method returns the updated {@link Bucket} resource.
    */
   @Named("Bucket:patch")
   @PATCH
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/b/{bucket}")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Bucket patchBucket(@PathParam("bucket") String bucketName,
            @BinderParam(BindToJsonPayload.class) BucketTemplate bucketTemplate, UpdateBucketOptions options);
}
