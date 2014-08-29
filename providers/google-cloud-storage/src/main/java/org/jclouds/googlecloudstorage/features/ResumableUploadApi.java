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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.googlecloudstorage.binders.ResumableUploadBinder;
import org.jclouds.googlecloudstorage.binders.UploadBinder;
import org.jclouds.googlecloudstorage.domain.ResumableUpload;
import org.jclouds.googlecloudstorage.domain.templates.ObjectTemplate;
import org.jclouds.googlecloudstorage.options.InsertObjectOptions;
import org.jclouds.googlecloudstorage.parser.ParseToResumableUpload;
import org.jclouds.io.Payload;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

/**
 * Provides Resumable Upload support via Rest API
 *
 * @see <a href="https://developers.google.com/storage/docs/json_api/v1/objects"/>
 * @see <a href="https://developers.google.com/storage/docs/json_api/v1/how-tos/upload#resumable"/>
 */

@SkipEncoding({ '/', '=' })
@RequestFilters(OAuthAuthenticator.class)
public interface ResumableUploadApi {

   /**
    * initiate a Resumable Upload Session
    *
    * @see https://developers.google.com/storage/docs/json_api/v1/how-tos/upload#resumable
    *
    * @param bucketName
    *           Name of the bucket in which the object to be stored
    * @param objectName
    *           Name of the object to upload
    * @param contentType
    *           Content type of the uploaded data
    * @param contentLength
    *           ContentLength of the uploaded object (Media part)
    *
    * @return a {@link ResumableUpload}
    */
   @Named("Object:initResumableUpload")
   @POST
   @QueryParams(keys = "uploadType", values = "resumable")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/upload/storage/v1/b/{bucket}/o")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @ResponseParser(ParseToResumableUpload.class)
   ResumableUpload initResumableUpload(@PathParam("bucket") String bucketName, @QueryParam("name") String objectName,
            @HeaderParam("X-Upload-Content-Type") String contentType,
            @HeaderParam("X-Upload-Content-Length") String contentLength);

   /**
    * initiate a Resumable Upload Session
    *
    * @see https://developers.google.com/storage/docs/json_api/v1/how-tos/upload#simple
    *
    * @param bucketName
    *           Name of the bucket in which the object to be stored
    * @param contentType
    *           Content type of the uploaded data (Media part)
    * @param contentLength
    *           Content length of the uploaded data (Media part)
    * @param metada
    *           Supply an {@link ObjectTemplate}
    *
    * @return a {@link ResumableUpload}
    */
   @Named("Object:resumableUpload")
   @POST
   @QueryParams(keys = "uploadType", values = "resumable")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/upload/storage/v1/b/{bucket}/o")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @MapBinder(ResumableUploadBinder.class)
   @ResponseParser(ParseToResumableUpload.class)
   ResumableUpload initResumableUpload(@PathParam("bucket") String bucketName,
            @HeaderParam("X-Upload-Content-Type") String contentType,
            @HeaderParam("X-Upload-Content-Length") Long contentLength,
            @PayloadParam("template") ObjectTemplate metadata);

   /**
    * Stores a new object
    *
    * @see https://developers.google.com/storage/docs/json_api/v1/how-tos/upload#resumable
    *
    * @param bucketName
    *           Name of the bucket in which the object to be stored
    * @param options
    *           Supply {@link InsertObjectOptions} with optional query parameters. 'name' is mandatory.
    *
    * @return If successful, this method returns a {@link GCSObject} resource.
    */
   @Named("Object:resumableUpload")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "uploadType", values = "resumable")
   @Path("/upload/storage/v1/b/{bucket}/o")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @MapBinder(UploadBinder.class)
   @ResponseParser(ParseToResumableUpload.class)
   ResumableUpload upload(@PathParam("bucket") String bucketName, @QueryParam("upload_id") String uploadId,
            @HeaderParam("Content-Type") String contentType, @HeaderParam("Content-Length") String contentLength,
            @PayloadParam("payload") Payload payload);

   /**
    * Facilitate to use resumable upload operation to upload files in chunks
    *
    * @see https://developers.google.com/storage/docs/json_api/v1/how-tos/upload#resumable
    *
    * @param bucketName
    *           Name of the bucket in which the object to be stored
    * @param uploadId
    *           uploadId returned from initResumableUpload operation
    * @param contentType
    *           Content type of the uploaded data
    * @param contentLength
    *           Content length of the uploaded data
    * @param contentRange
    *           Range in {bytes StartingByte - Endingbyte/Totalsize } format ex: bytes 0 - 1213/2000
    * @param payload
    *           a {@link Payload} with actual data to upload
    *
    * @return a {@link ResumableUpload}
    */
   @Named("Object:Upload")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "uploadType", values = "resumable")
   @Path("/upload/storage/v1/b/{bucket}/o")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @MapBinder(UploadBinder.class)
   @ResponseParser(ParseToResumableUpload.class)
   ResumableUpload chunkUpload(@PathParam("bucket") String bucketName, @QueryParam("upload_id") String uploadId,
            @HeaderParam("Content-Type") String contentType, @HeaderParam("Content-Length") Long contentLength,
            @HeaderParam("Content-Range") String contentRange, @PayloadParam("payload") Payload payload);

   /**
    * Check the status of a resumable upload
    *
    * @see https://developers.google.com/storage/docs/json_api/v1/how-tos/upload#resumable
    *
    * @param bucketName
    *           Name of the bucket in which the object to be stored
    * @param uploadId
    *           uploadId returned from initResumableUpload operation
    * @param contentRange
    *           Range in {bytes StartingByte - Endingbyte/Totalsize } format ex: bytes 0 - 1213/2000
    *
    * @return a {@link ResumableUpload}
    */

   @Named("Object:Upload")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @DefaultValue("0")
   @QueryParams(keys = "uploadType", values = "resumable")
   @Path("/upload/storage/v1/b/{bucket}/o")
   @OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
   @ResponseParser(ParseToResumableUpload.class)
   ResumableUpload checkStatus(@PathParam("bucket") String bucketName, @QueryParam("upload_id") String uploadId,
            @HeaderParam("Content-Range") String contentRange);

}
