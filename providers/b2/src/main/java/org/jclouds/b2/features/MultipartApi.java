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

import java.util.Collection;
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.b2.binders.UploadPartBinder;
import org.jclouds.b2.domain.B2Object;
import org.jclouds.b2.domain.GetUploadPartResponse;
import org.jclouds.b2.domain.ListPartsResponse;
import org.jclouds.b2.domain.ListUnfinishedLargeFilesResponse;
import org.jclouds.b2.domain.MultipartUploadResponse;
import org.jclouds.b2.domain.UploadPartResponse;
import org.jclouds.b2.filters.RequestAuthorization;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

@BlobScope(CONTAINER)
@Consumes(APPLICATION_JSON)
public interface MultipartApi {
   @Named("b2_start_large_file")
   @POST
   @Path("/b2api/v1/b2_start_large_file")
   @RequestFilters(RequestAuthorization.class)
   @MapBinder(BindToJsonPayload.class)
   @Produces(APPLICATION_JSON)
   MultipartUploadResponse startLargeFile(@PayloadParam("bucketId") String bucketId, @PayloadParam("fileName") String fileName, @PayloadParam("contentType") String contentType, @PayloadParam("fileInfo") Map<String, String> fileInfo);

   @Named("b2_cancel_large_file")
   @POST
   @Path("/b2api/v1/b2_cancel_large_file")
   @RequestFilters(RequestAuthorization.class)
   @MapBinder(BindToJsonPayload.class)
   @Produces(APPLICATION_JSON)
   B2Object cancelLargeFile(@PayloadParam("fileId") String fileId);

   @Named("b2_finish_large_file")
   @POST
   @Path("/b2api/v1/b2_finish_large_file")
   @RequestFilters(RequestAuthorization.class)
   @MapBinder(BindToJsonPayload.class)
   @Produces(APPLICATION_JSON)
   B2Object finishLargeFile(@PayloadParam("fileId") String fileId, @PayloadParam("partSha1Array") Collection<String> contentSha1List);

   @Named("b2_get_upload_part_url")
   @POST
   @Path("/b2api/v1/b2_get_upload_part_url")
   @RequestFilters(RequestAuthorization.class)
   @MapBinder(BindToJsonPayload.class)
   @Produces(APPLICATION_JSON)
   GetUploadPartResponse getUploadPartUrl(@PayloadParam("fileId") String fileId);

   @Named("b2_upload_part")
   @POST
   @MapBinder(UploadPartBinder.class)
   UploadPartResponse uploadPart(@PayloadParam("response") GetUploadPartResponse response, @HeaderParam("X-Bz-Part-Number") int partNumber, @HeaderParam("X-Bz-Content-Sha1") String sha1, @PayloadParam("payload") Payload payload);

   @Named("b2_list_parts")
   @POST
   @Path("/b2api/v1/b2_list_parts")
   @RequestFilters(RequestAuthorization.class)
   @MapBinder(BindToJsonPayload.class)
   @Produces(APPLICATION_JSON)
   ListPartsResponse listParts(@PayloadParam("fileId") String fileId, @PayloadParam("startPartNumber") @Nullable Integer startPartNumber, @PayloadParam("maxPartCount") @Nullable Integer maxPartCount);

   @Named("b2_list_unfinished_large_files")
   @POST
   @Path("/b2api/v1/b2_list_unfinished_large_files")
   @RequestFilters(RequestAuthorization.class)
   @MapBinder(BindToJsonPayload.class)
   @Produces(APPLICATION_JSON)
   ListUnfinishedLargeFilesResponse listUnfinishedLargeFiles(@PayloadParam("bucketId") String bucketId, @PayloadParam("startFileId") @Nullable String startFileId, @PayloadParam("maxFileCount") @Nullable Integer maxFileCount);
}
