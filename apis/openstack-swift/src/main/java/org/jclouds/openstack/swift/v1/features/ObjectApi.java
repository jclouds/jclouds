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
package org.jclouds.openstack.swift.v1.features;

import static com.google.common.net.HttpHeaders.EXPECT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_COPY_FROM;

import java.util.Map;

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

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders.BindObjectMetadataToHeaders;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders.BindRawMetadataToHeaders;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders.BindRemoveObjectMetadataToHeaders;
import org.jclouds.openstack.swift.v1.binders.SetPayload;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.functions.ETagHeader;
import org.jclouds.openstack.swift.v1.functions.ParseObjectFromResponse;
import org.jclouds.openstack.swift.v1.functions.ParseObjectListFromResponse;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.openstack.swift.v1.options.PutOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.annotations.Beta;

/**
 * Provides access to the OpenStack Object Storage (Swift) Object API features.
 * <p/>
 * This API is new to jclouds and hence is in Beta. That means we need people to use it and give us feedback. Based
 * on that feedback, minor changes to the interfaces may happen. This code will replace
 * org.jclouds.openstack.swift.SwiftClient in jclouds 2.0 and it is recommended you adopt it sooner than later.
 */
@Beta
@RequestFilters(AuthenticateRequest.class)
@Consumes(APPLICATION_JSON)
public interface ObjectApi {

   /**
    * Lists up to 10,000 objects.
    *
    * @return an {@link ObjectList} of {@link SwiftObject} ordered by name or {@code null}.
    */
   @Named("object:list")
   @GET
   @ResponseParser(ParseObjectListFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ObjectList list();

   /**
    * Lists up to 10,000 objects. To control a large list of containers beyond
    * 10,000 objects, use the {@code marker} and {@code endMarker} parameters in the
    * {@link ListContainerOptions} class.
    *
    * @param options
    *           the {@link ListContainerOptions} for controlling the returned list.
    *
    * @return an {@link ObjectList} of {@link SwiftObject} ordered by name or {@code null}.
    */
   @Named("object:list")
   @GET
   @ResponseParser(ParseObjectListFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ObjectList list(ListContainerOptions options);

   /**
    * Creates or updates a {@link SwiftObject}.
    *
    * @param objectName
    *           corresponds to {@link SwiftObject#getName()}.
    * @param payload
    *           corresponds to {@link SwiftObject#getPayload()}.
    *
    * @return {@link SwiftObject#getETag()} of the object.
    */
   @Named("object:put")
   @PUT
   @Path("/{objectName}")
   @Headers(keys = EXPECT, values = "100-continue")
   @ResponseParser(ETagHeader.class)
   String put(@PathParam("objectName") String objectName, @BinderParam(SetPayload.class) Payload payload);

   /**
    * Creates or updates a {@link SwiftObject}.
    *
    * @param objectName
    *           corresponds to {@link SwiftObject#getName()}.
    * @param payload
    *           corresponds to {@link SwiftObject#getPayload()}.
    * @param options
    *           {@link PutOptions options} to control creating the {@link SwiftObject}.
    *
    * @return {@link SwiftObject#getETag()} of the object.
    */
   @Named("object:put")
   @PUT
   @Path("/{objectName}")
   @Headers(keys = EXPECT, values = "100-continue")
   @ResponseParser(ETagHeader.class)
   String put(@PathParam("objectName") String objectName, @BinderParam(SetPayload.class) Payload payload,
         PutOptions options);

   /**
    * Gets the {@link SwiftObject} metadata without its {@link Payload#openStream() body}.
    *
    * @param objectName
    *           corresponds to {@link SwiftObject#getName()}.
    *
    * @return the {@link SwiftObject} or {@code null}, if not found.
    */
   @Named("object:getWithoutBody")
   @HEAD
   @Path("/{objectName}")
   @ResponseParser(ParseObjectFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SwiftObject getWithoutBody(@PathParam("objectName") String objectName);

   /**
    * Gets the {@link SwiftObject} including its {@link Payload#openStream() body}.
    *
    * @param objectName
    *           corresponds to {@link SwiftObject#getName()}.
    *
    * @return the {@link SwiftObject} or {@code null}, if not found.
    */
   @Named("object:get")
   @GET
   @Path("/{objectName}")
   @ResponseParser(ParseObjectFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SwiftObject get(@PathParam("objectName") String objectName);

   /**
    * Gets the {@link SwiftObject} including its {@link Payload#openStream() body}.
    *
    * @param objectName
    *           corresponds to {@link SwiftObject#getName()}.
    * @param options
    *           options to control the download.
    *
    * @return the {@link SwiftObject} or {@code null}, if not found.
    */
   @Named("object:get")
   @GET
   @Path("/{objectName}")
   @ResponseParser(ParseObjectFromResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SwiftObject get(@PathParam("objectName") String objectName, GetOptions options);

   /**
    * Creates or updates the metadata for a {@link SwiftObject}.
    *
    * @param objectName
    *           corresponds to {@link SwiftObject#getName()}.
    * @param metadata
    *           the metadata to create or update.
    *
    * @return {@code true} if the metadata was successfully created or updated,
    *         {@code false} if not.
    */
   @Named("object:updateMetadata")
   @POST
   @Path("/{objectName}")
   @Produces("")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean updateMetadata(@PathParam("objectName") String objectName,
         @BinderParam(BindObjectMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Creates or updates the metadata for a {@link SwiftObject} without escaping the key.
    * This will also update metadata such as content-disposition.
    *
    * @param objectName
    *           corresponds to {@link SwiftObject#getName()}.
    * @param metadata
    *           the metadata to create or update.
    *
    * @return {@code true} if the metadata was successfully created or updated,
    *         {@code false} if not.
    */
   @Named("object:updateMetadata")
   @POST
   @Path("/{objectName}")
   @Produces("")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean updateRawMetadata(@PathParam("objectName") String objectName,
         @BinderParam(BindRawMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Deletes the metadata from a {@link SwiftObject}.
    *
    * @param objectName
    *           corresponds to {@link SwiftObject#getName()}.
    * @param metadata
    *           corresponds to {@link SwiftObject#getMetadata()}.
    *
    * @return {@code true} if the metadata was successfully deleted,
    *         {@code false} if not.
    */
   @Named("object:deleteMetadata")
   @POST
   @Path("/{objectName}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteMetadata(@PathParam("objectName") String objectName,
         @BinderParam(BindRemoveObjectMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Deletes an object, if present.
    *
    * @param objectName
    *           corresponds to {@link SwiftObject#getName()}.
    */
   @Named("object:delete")
   @DELETE
   @Path("/{objectName}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("objectName") String objectName);

   /**
    * Copies an object from one container to another.
    *
    * <h3>NOTE</h3>
    * This is a server side copy.
    *
    * @param destinationObject
    *           the destination object name.
    * @param sourceContainer
    *           the source container name.
    * @param sourceObject
    *           the source object name.
    *
    * @return {@code true} if the object was successfully copied, {@code false} if not.
    *
    * @throws org.jclouds.openstack.swift.v1.CopyObjectException if the source or destination container do not exist.
    */
   @Named("object:copy")
   @PUT
   @Path("/{destinationObject}")
   @Headers(keys = OBJECT_COPY_FROM, values = "/{sourceContainer}/{sourceObject}")
   @Fallback(FalseOnContainerNotFound.class)
   boolean copy(@PathParam("destinationObject") String destinationObject,
                @PathParam("sourceContainer") String sourceContainer,
                @PathParam("sourceObject") String sourceObject);

   /**
    * Copies an object from one container to another.
    *
    * <h3>NOTE</h3>
    * This is a server side copy.
    *
    * @param destinationObject
    *           the destination object name.
    * @param sourceContainer
    *           the source container name.
    * @param sourceObject
    *           the source object name.
    * @param userMetadata
    *           Freeform metadata for the object, automatically prefixed/escaped
    * @param objectMetadata
    *           Unprefixed/unescaped metadata, such as Content-Disposition
    *
    * @return {@code true} if the object was successfully copied, {@code false} if not.
    *
    * @throws org.jclouds.openstack.swift.v1.CopyObjectException if the source or destination container do not exist.
    */
   @Named("object:copy")
   @PUT
   @Path("/{destinationObject}")
   @Headers(keys = OBJECT_COPY_FROM, values = "/{sourceContainer}/{sourceObject}")
   @Fallback(FalseOnContainerNotFound.class)
   boolean copy(@PathParam("destinationObject") String destinationObject,
         @PathParam("sourceContainer") String sourceContainer,
         @PathParam("sourceObject") String sourceObject,
         @BinderParam(BindObjectMetadataToHeaders.class) Map<String, String> userMetadata,
         @BinderParam(BindRawMetadataToHeaders.class) Map<String, String> objectMetadata);

}
