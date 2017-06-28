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

import java.util.Map;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.b2.binders.UploadFileBinder;
import org.jclouds.b2.domain.B2Object;
import org.jclouds.b2.domain.B2ObjectList;
import org.jclouds.b2.domain.DeleteFileResponse;
import org.jclouds.b2.domain.HideFileResponse;
import org.jclouds.b2.domain.UploadFileResponse;
import org.jclouds.b2.domain.UploadUrlResponse;
import org.jclouds.b2.filters.RequestAuthorization;
import org.jclouds.b2.filters.RequestAuthorizationDownload;
import org.jclouds.b2.functions.ParseB2ObjectFromResponse;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;

@BlobScope(CONTAINER)
public interface ObjectApi {
   @Named("b2_get_upload_url")
   @POST
   @Path("/b2api/v1/b2_get_upload_url")
   @RequestFilters(RequestAuthorization.class)
   @MapBinder(BindToJsonPayload.class)
   @Consumes(APPLICATION_JSON)
   @Produces(APPLICATION_JSON)
   UploadUrlResponse getUploadUrl(@PayloadParam("bucketId") String bucketId);

   @Named("b2_upload_file")
   @POST
   @MapBinder(UploadFileBinder.class)
   @Consumes(APPLICATION_JSON)
   UploadFileResponse uploadFile(@PayloadParam("uploadUrl") UploadUrlResponse uploadUrl, @PayloadParam("fileName") String fileName, @Nullable @PayloadParam("contentSha1") String contentSha1, @PayloadParam("fileInfo") Map<String, String> fileInfo, Payload payload);

   @Named("b2_delete_file_version")
   @POST
   @Path("/b2api/v1/b2_delete_file_version")
   @MapBinder(BindToJsonPayload.class)
   @RequestFilters(RequestAuthorization.class)
   @Consumes(APPLICATION_JSON)
   @Produces(APPLICATION_JSON)
   DeleteFileResponse deleteFileVersion(@PayloadParam("fileName") String fileName, @PayloadParam("fileId") String fileId);

   @Named("b2_get_file_info")
   @POST
   @Path("/b2api/v1/b2_get_file_info")
   @MapBinder(BindToJsonPayload.class)
   @RequestFilters(RequestAuthorization.class)
   @Consumes(APPLICATION_JSON)
   @Produces(APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   B2Object getFileInfo(@PayloadParam("fileId") String fileId);

   @Named("b2_download_file_by_id")
   @GET
   @Path("/b2api/v1/b2_download_file_by_id")
   @RequestFilters(RequestAuthorizationDownload.class)
   @ResponseParser(ParseB2ObjectFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   B2Object downloadFileById(@QueryParam("fileId") String fileId);

   @Named("b2_download_file_by_id")
   @GET
   @Path("/b2api/v1/b2_download_file_by_id")
   @RequestFilters(RequestAuthorizationDownload.class)
   @ResponseParser(ParseB2ObjectFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   B2Object downloadFileById(@QueryParam("fileId") String fileId, GetOptions options);

   @Named("b2_download_file_by_name")
   @GET
   @Path("/file/{bucketName}/{fileName}")
   @RequestFilters(RequestAuthorizationDownload.class)
   @ResponseParser(ParseB2ObjectFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   B2Object downloadFileByName(@PathParam("bucketName") String bucketName, @PathParam("fileName") String fileName);

   @Named("b2_download_file_by_name")
   @GET
   @Path("/file/{bucketName}/{fileName}")
   @RequestFilters(RequestAuthorizationDownload.class)
   @ResponseParser(ParseB2ObjectFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   B2Object downloadFileByName(@PathParam("bucketName") String bucketName, @PathParam("fileName") String fileName, GetOptions options);

   @Deprecated
   @Named("b2_list_file_names")
   @GET
   @Path("/b2api/v1/b2_list_file_names")
   @MapBinder(BindToJsonPayload.class)
   @RequestFilters(RequestAuthorization.class)
   @Consumes(APPLICATION_JSON)
   @Produces(APPLICATION_JSON)
   B2ObjectList listFileNames(@PayloadParam("bucketId") String bucketId, @PayloadParam("startFileName") @Nullable String startFileName, @PayloadParam("maxFileCount") @Nullable Integer maxFileCount);

   @Named("b2_list_file_names")
   @GET
   @Path("/b2api/v1/b2_list_file_names")
   @MapBinder(BindToJsonPayload.class)
   @RequestFilters(RequestAuthorization.class)
   @Consumes(APPLICATION_JSON)
   @Produces(APPLICATION_JSON)
   B2ObjectList listFileNames(@PayloadParam("bucketId") String bucketId, @PayloadParam("startFileName") @Nullable String startFileName, @PayloadParam("maxFileCount") @Nullable Integer maxFileCount, @PayloadParam("prefix") @Nullable String prefix, @Nullable @PayloadParam("delimiter") String delimiter);

   @Deprecated
   @Named("b2_list_file_versions")
   @GET
   @Path("/b2api/v1/b2_list_file_versions")
   @MapBinder(BindToJsonPayload.class)
   @RequestFilters(RequestAuthorization.class)
   @Consumes(APPLICATION_JSON)
   @Produces(APPLICATION_JSON)
   B2ObjectList listFileVersions(@PayloadParam("bucketId") String bucketId, @PayloadParam("startFileId") @Nullable String startFileId, @PayloadParam("startFileName") @Nullable String startFileName, @PayloadParam("maxFileCount") @Nullable Integer maxFileCount);

   @Named("b2_list_file_versions")
   @GET
   @Path("/b2api/v1/b2_list_file_versions")
   @MapBinder(BindToJsonPayload.class)
   @RequestFilters(RequestAuthorization.class)
   @Consumes(APPLICATION_JSON)
   @Produces(APPLICATION_JSON)
   B2ObjectList listFileVersions(@PayloadParam("bucketId") String bucketId, @PayloadParam("startFileId") @Nullable String startFileId, @PayloadParam("startFileName") @Nullable String startFileName, @PayloadParam("maxFileCount") @Nullable Integer maxFileCount, @PayloadParam("prefix") @Nullable String prefix, @PayloadParam("delimiter") @Nullable String delimiter);

   @Named("b2_hide_file")
   @POST
   @Path("/b2api/v1/b2_hide_file")
   @MapBinder(BindToJsonPayload.class)
   @RequestFilters(RequestAuthorization.class)
   @Consumes(APPLICATION_JSON)
   @Produces(APPLICATION_JSON)
   HideFileResponse hideFile(@PayloadParam("bucketId") String bucketId, @PayloadParam("fileName") String fileName);
}
