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
package org.jclouds.openstack.swift;

import static com.google.common.net.HttpHeaders.EXPECT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import static org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import static org.jclouds.blobstore.BlobStoreFallbacks.FalseOnKeyNotFound;
import static org.jclouds.blobstore.BlobStoreFallbacks.NullOnContainerNotFound;
import static org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;
import static org.jclouds.openstack.swift.reference.SwiftHeaders.OBJECT_COPY_FROM;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ReturnTrueIf201;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.swift.binders.BindIterableToHeadersWithContainerDeleteMetadataPrefix;
import org.jclouds.openstack.swift.binders.BindMapToHeadersWithContainerMetadataPrefix;
import org.jclouds.openstack.swift.binders.BindSwiftObjectMetadataToRequest;
import org.jclouds.openstack.swift.domain.AccountMetadata;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.functions.ObjectName;
import org.jclouds.openstack.swift.functions.ParseAccountMetadataResponseFromHeaders;
import org.jclouds.openstack.swift.functions.ParseContainerMetadataFromHeaders;
import org.jclouds.openstack.swift.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.openstack.swift.functions.ParseObjectInfoFromHeaders;
import org.jclouds.openstack.swift.functions.ParseObjectInfoListFromJsonResponse;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.inject.Provides;

/**
 * Common features in OpenStack Swift.
 *
 *
 * @deprecated Please use {@code org.jclouds.openstack.swift.v1.SwiftApi} and related
 *             feature APIs in {@code org.jclouds.openstack.swift.v1.features.*} as noted in
 *             each method. This interface will be removed in jclouds 2.0.
 */
@Deprecated
public interface CommonSwiftClient extends Closeable {

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.domain.SwiftObject#builder()}
    */
   @Deprecated
   @Provides
   SwiftObject newSwiftObject();

   /**
    * HEAD operations against an identity are performed to retrieve the number of Containers and the
    * total bytes stored in Cloud Files for the identity.
    * <p/>
    * Determine the number of Containers within the identity and the total bytes stored. Since the
    * storage system is designed to store large amounts of data, care should be taken when
    * representing the total bytes response as an integer; when possible, convert it to a 64-bit
    * unsigned integer if your platform supports that primitive flavor.
    *
    * @return the {@link AccountMetadata}
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.AccountApi#get()}
    */
   @Deprecated
   @Named("GetAccountMetadata")
   @HEAD
   @Path("/")
   @Consumes()
   @ResponseParser(ParseAccountMetadataResponseFromHeaders.class)
   AccountMetadata getAccountStatistics();

   /**
    * GET operations against the X-Storage-Url for an identity are performed to retrieve a list of
    * existing storage
    * <p/>
    * Containers ordered by name. The following list describes the optional query parameters that
    * are supported with this request.
    * <ul>
    * <li>limit - For an integer value N, limits the number of results to at most N values.</li>
    * <li>marker - Given a string value X, return Object names greater in value than the specified
    * marker.</li>
    * <li>format - Specify either json or xml to return the respective serialized response.</li>
    * </ul>
    * <p/>
    * At this time, a prex query parameter is not supported at the Account level.
    *
    *<h4>Large Container Lists</h4>
    * The system will return a maximum of 10,000 Container names per request. To retrieve subsequent
    * container names, another request must be made with a marker parameter. The marker indicates
    * where the last list left off and the system will return container names greater than this
    * marker, up to 10,000 again. Note that the marker value should be URL encoded prior to sending
    * the HTTP request.
    * <p/>
    * If 10,000 is larger than desired, a limit parameter may be given.
    * <p/>
    * If the number of container names returned equals the limit given (or 10,000 if no limit is
    * given), it can be assumed there are more container names to be listed. If the container name
    * list is exactly divisible by the limit, the last request will simply have no content.
    *
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ContainerApi#list()} and
    *             {@link org.jclouds.openstack.swift.v1.features.ContainerApi#list(ListContainerOptions)}
    */
   @Deprecated
   @Named("ListContainers")
   @GET
   @Consumes(APPLICATION_JSON)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   Set<ContainerMetadata> listContainers(ListContainerOptions... options);

   /**
    * Get the {@link ContainerMetadata} for the specified container.
    *
    * @param container
    *           the container to get the metadata from
    * @return the {@link ContainerMetadata}
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ContainerApi#get()}
    */
   @Deprecated
   @Named("GetContainerMetadata")
   @HEAD
   @Path("/{container}")
   @Consumes()
   @ResponseParser(ParseContainerMetadataFromHeaders.class)
   @Fallback(NullOnContainerNotFound.class)
   ContainerMetadata getContainerMetadata(@PathParam("container") String container);

   /**
    * Set the {@link ContainerMetadata} on the given container.
    *
    * @param container
    *           the container to set the metadata on
    * @param containerMetadata
    *           a {@code Map<String, String>} containing the metadata
    * @return {@code true}
    *            if the Container Metadata was successfully created or updated, false if not.
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ContainerApi#updateMetadata()}
    */
   @Deprecated
   @Named("UpdateContainerMetadata")
   @POST
   @Path("/{container}")
   @Fallback(FalseOnContainerNotFound.class)
   boolean setContainerMetadata(@PathParam("container") String container,
         @BinderParam(BindMapToHeadersWithContainerMetadataPrefix.class) Map<String, String> containerMetadata);


   /**
    * Delete the metadata on the given container.
    *
    * @param container
    *           the container to delete the metadata from
    * @param metadataKeys
    *           the metadata keys
    * @return {@code true}
    *            if the Container was successfully deleted, false if not.
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ContainerApi#deleteMetadata()}
    */
   @Deprecated
   @Named("UpdateContainerMetadata")
   @POST
   @Path("/{container}")
   @Fallback(FalseOnContainerNotFound.class)
   boolean deleteContainerMetadata(@PathParam("container") String container,
         @BinderParam(BindIterableToHeadersWithContainerDeleteMetadataPrefix.class) Iterable<String> metadataKeys);

   /**
    * Create a container.
    *
    * @param container
    *           the name of the container
    * @return {@code true}
    *            if the Container was successfully created, false if not.
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ContainerApi#createIfAbsent()}
    */
   @Deprecated
   @Named("CreateContainer")
   @PUT
   @ResponseParser(ReturnTrueIf201.class)
   @Path("/{container}")
   boolean createContainer(@PathParam("container") String container);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ContainerApi#createIfAbsent()}
    */
   @Deprecated
   @Named("CreateContainer")
   @PUT
   @ResponseParser(ReturnTrueIf201.class)
   @Path("/{container}")
   boolean createContainer(@PathParam("container") String container, CreateContainerOptions... options);

   /**
    * @deprecated This method will be replaced by
    *             (@link org.jclouds.openstack.swift.v1.features.ContainerApi#deleteIfEmpty()}
    */
   @Deprecated
   @Named("DeleteContainer")
   @DELETE
   @Fallback(SwiftFallbacks.TrueOn404FalseOn409.class)
   @Path("/{container}")
   boolean deleteContainerIfEmpty(@PathParam("container") String container);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ContainerApi#head()}
    */
   @Deprecated
   @Named("GetContainerMetadata")
   @HEAD
   @Path("/{container}")
   @Consumes
   @Fallback(FalseOnContainerNotFound.class)
   boolean containerExists(@PathParam("container") String container);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#list()} and
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#list(ListContainerOptions)}
    */
   @Deprecated
   @Named("ListObjects")
   @GET
   @QueryParams(keys = "format", values = "json")
   @ResponseParser(ParseObjectInfoListFromJsonResponse.class)
   @Path("/{container}")
   PageSet<ObjectInfo> listObjects(@PathParam("container") String container,
         ListContainerOptions... options);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#get()}
    */
   @Deprecated
   @Named("GetObject")
   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @Fallback(NullOnKeyNotFound.class)
   @Path("/{container}/{name}")
   SwiftObject getObject(@PathParam("container") String container, @PathParam("name") String name,
         GetOptions... options);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi@updateMetadata()}
    */
   @Deprecated
   @Named("UpdateObjectMetadata")
   @POST
   @Path("/{container}/{name}")
   boolean setObjectInfo(@PathParam("container") String container,
         @PathParam("name") String name,
         @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> userMetadata);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#head()}
    */
   @Deprecated
   @Named("GetObjectMetadata")
   @HEAD
   @ResponseParser(ParseObjectInfoFromHeaders.class)
   @Fallback(NullOnKeyNotFound.class)
   @Path("/{container}/{name}")
   @Consumes
   MutableObjectInfoWithMetadata getObjectInfo(@PathParam("container") String container,
         @PathParam("name") String name);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#replace()}
    */
   @Deprecated
   @Named("PutObject")
   @PUT
   @Path("/{container}/{name}")
   @Headers(keys = EXPECT, values = "100-continue")
   @ResponseParser(ParseETagHeader.class)
   String putObject(@PathParam("container") String container, @PathParam("name") @ParamParser(ObjectName.class)
      @BinderParam(BindSwiftObjectMetadataToRequest.class) SwiftObject object);


   /**
    * @return True If the object was copied
    * @throws CopyObjectException If the object was not copied
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#copy()}
    */
   @Deprecated
   @Named("CopyObject")
   @PUT
   @Path("/{destinationContainer}/{destinationObject}")
   @Headers(keys = OBJECT_COPY_FROM, values = "/{sourceContainer}/{sourceObject}")
   @Fallback(FalseOnContainerNotFound.class)
   boolean copyObject(@PathParam("sourceContainer") String sourceContainer,
                      @PathParam("sourceObject") String sourceObject,
                      @PathParam("destinationContainer") String destinationContainer,
                      @PathParam("destinationObject") String destinationObject);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#delete()}
    */
   @Deprecated
   @Named("RemoveObject")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/{container}/{name}")
   void removeObject(@PathParam("container") String container, @PathParam("name") String name);


   /**
    * @throws org.jclouds.blobstore.ContainerNotFoundException
    *            if the container is not present
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#head()}
    */
   @Deprecated
   @Named("GetObjectMetadata")
   @HEAD
   @Fallback(FalseOnKeyNotFound.class)
   @Path("/{container}/{name}")
   @Consumes
   boolean objectExists(@PathParam("container") String container, @PathParam("name") String name);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#replaceManifest()}
    */
   @Deprecated
   @Named("PutObjectManifest")
   @PUT
   @Path("/{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   @Headers(keys = "X-Object-Manifest", values = "{container}/{name}/")
   String putObjectManifest(@PathParam("container") String container, @PathParam("name") String name);

   /**
    * @deprecated This method will be replaced by
    *             {@link org.jclouds.openstack.swift.v1.features.ObjectApi#replaceManifest()}
    */
   @Deprecated
   @Named("PutObjectManifest")
   @PUT
   @Path("/{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   @Headers(keys = "X-Object-Manifest", values = "{container}/{name}/")
   String putObjectManifest(@PathParam("container") String container, @PathParam("name") @ParamParser(ObjectName.class)
      @BinderParam(BindSwiftObjectMetadataToRequest.class) SwiftObject object);
}
