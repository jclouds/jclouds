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
package org.jclouds.b2.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.b2.domain.Bucket;
import org.jclouds.b2.domain.BucketList;
import org.jclouds.b2.domain.BucketType;
import org.jclouds.b2.filters.RequestAuthorization;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

@RequestFilters(RequestAuthorization.class)
@BlobScope(CONTAINER)
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public interface BucketApi {
   @Named("b2_create_bucket")
   @POST
   @Path("/b2api/v1/b2_create_bucket")
   @MapBinder(BindToJsonPayload.class)
   @PayloadParams(keys = {"accountId"}, values = {"{jclouds.identity}"})
   Bucket createBucket(@PayloadParam("bucketName") String bucketName, @PayloadParam("bucketType") BucketType bucketType);

   @Named("b2_delete_bucket")
   @POST
   @Path("/b2api/v1/b2_delete_bucket")
   @MapBinder(BindToJsonPayload.class)
   @PayloadParams(keys = {"accountId"}, values = {"{jclouds.identity}"})
   @Fallback(NullOnNotFoundOr404.class)
   Bucket deleteBucket(@PayloadParam("bucketId") String bucketId);

   @Named("b2_update_bucket")
   @POST
   @Path("/b2api/v1/b2_update_bucket")
   @MapBinder(BindToJsonPayload.class)
   @PayloadParams(keys = {"accountId"}, values = {"{jclouds.identity}"})
   Bucket updateBucket(@PayloadParam("bucketId") String bucketId, @PayloadParam("bucketType") BucketType bucketType);

   @Named("b2_list_buckets")
   @POST
   @Path("/b2api/v1/b2_list_buckets")
   @MapBinder(BindToJsonPayload.class)
   @PayloadParams(keys = {"accountId"}, values = {"{jclouds.identity}"})
   BucketList listBuckets();
}
