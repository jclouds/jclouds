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
package org.jclouds.atmos;

import static com.google.common.net.HttpHeaders.EXPECT;
import static org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyAlreadyExists;
import static org.jclouds.blobstore.BlobStoreFallbacks.ThrowContainerNotFoundOn404;
import static org.jclouds.blobstore.BlobStoreFallbacks.ThrowKeyNotFoundOn404;

import java.io.Closeable;
import java.net.URI;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.atmos.binders.BindMetadataToHeaders;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.SystemMetadata;
import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.atmos.fallbacks.TrueOn404FalseOnPathNotEmpty;
import org.jclouds.atmos.filters.SignRequest;
import org.jclouds.atmos.functions.AtmosObjectName;
import org.jclouds.atmos.functions.ParseDirectoryListFromContentAndHeaders;
import org.jclouds.atmos.functions.ParseNullableURIFromListOrLocationHeaderIf20x;
import org.jclouds.atmos.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.atmos.functions.ParseSystemMetadataFromHeaders;
import org.jclouds.atmos.functions.ParseUserMetadataFromHeaders;
import org.jclouds.atmos.functions.ReturnTrueIfGroupACLIsOtherRead;
import org.jclouds.atmos.options.ListOptions;
import org.jclouds.atmos.options.PutOptions;
import org.jclouds.http.options.GetOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.inject.Provides;

/** Provides access to EMC Atmos Online Storage resources via their REST API. */
@RequestFilters(SignRequest.class)
@Path("/rest/namespace")
public interface AtmosClient extends Closeable {

   @Provides
   AtmosObject newObject();

   @Named("ListDirectory")
   @GET
   @Path("/")
   @ResponseParser(ParseDirectoryListFromContentAndHeaders.class)
   @Consumes(MediaType.TEXT_XML)
   BoundedSet<? extends DirectoryEntry> listDirectories(ListOptions... options);

   @Named("ListDirectory")
   @GET
   @Path("/{directoryName}/")
   @ResponseParser(ParseDirectoryListFromContentAndHeaders.class)
   @Fallback(ThrowContainerNotFoundOn404.class)
   @Consumes(MediaType.TEXT_XML)
   @Headers(keys = "x-emc-include-meta", values = "1")
   BoundedSet<? extends DirectoryEntry> listDirectory(
         @PathParam("directoryName") String directoryName, ListOptions... options);

   @Named("CreateDirectory")
   @POST
   @Path("/{directoryName}/")
   @Fallback(NullOnKeyAlreadyExists.class)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Consumes(MediaType.WILDCARD)
   URI createDirectory(@PathParam("directoryName") String directoryName, PutOptions... options);

   @Nullable
   @Named("CreateObject")
   @POST
   @Path("/{parent}/{name}")
   @Headers(keys = EXPECT, values = "100-continue")
   @ResponseParser(ParseNullableURIFromListOrLocationHeaderIf20x.class)
   @Consumes(MediaType.WILDCARD)
   URI createFile(@PathParam("parent") String parent, @PathParam("name") @ParamParser(AtmosObjectName.class)
      @BinderParam(BindMetadataToHeaders.class) AtmosObject object, PutOptions... options);

   @Named("UpdateObject")
   @PUT
   @Path("/{parent}/{name}")
   @Headers(keys = EXPECT, values = "100-continue")
   @Fallback(ThrowKeyNotFoundOn404.class)
   @Consumes(MediaType.WILDCARD)
   void updateFile(@PathParam("parent") String parent, @PathParam("name") @ParamParser(AtmosObjectName.class)
      @BinderParam(BindMetadataToHeaders.class) AtmosObject object, PutOptions... options);

   @Named("ReadObject")
   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   AtmosObject readFile(@PathParam("path") String path, GetOptions... options);

   @Named("GetObjectMetadata")
   @HEAD
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   AtmosObject headFile(@PathParam("path") String path);

   @Named("GetSystemMetadata")
   @HEAD
   @ResponseParser(ParseSystemMetadataFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   // currently throws 403 errors @QueryParams(keys = "metadata/system")
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   SystemMetadata getSystemMetadata(@PathParam("path") String path);

   @Named("GetUserMetadata")
   @HEAD
   @ResponseParser(ParseUserMetadataFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{path}")
   @QueryParams(keys = "metadata/user")
   @Consumes(MediaType.WILDCARD)
   UserMetadata getUserMetadata(@PathParam("path") String path);

   @Named("DeleteObject")
   @DELETE
   @Fallback(TrueOn404FalseOnPathNotEmpty.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   boolean deletePath(@PathParam("path") String path);

   @Named("GetObjectMetadata")
   @HEAD
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   boolean pathExists(@PathParam("path") String path);

   @Named("GetObjectMetadata")
   @HEAD
   @ResponseParser(ReturnTrueIfGroupACLIsOtherRead.class)
   @Path("/{path}")
   @Consumes(MediaType.WILDCARD)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean isPublic(@PathParam("path") String path);

   @Named("SetObjectMetadata")
   @POST
   @Path("/{path}")
   @QueryParams(keys = "acl")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Fallback(ThrowKeyNotFoundOn404.class)
   @Consumes(MediaType.WILDCARD)
   void setGroupAcl(@PathParam("path") String path, PutOptions options);
}
