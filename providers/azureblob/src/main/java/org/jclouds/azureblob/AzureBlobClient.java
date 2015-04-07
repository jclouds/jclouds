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
package org.jclouds.azureblob;

import static com.google.common.net.HttpHeaders.EXPECT;
import static org.jclouds.Fallbacks.TrueOnNotFoundOr404;
import static org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import static org.jclouds.azureblob.AzureBlobFallbacks.FalseIfContainerAlreadyExists;
import static org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import static org.jclouds.blobstore.BlobStoreFallbacks.FalseOnKeyNotFound;
import static org.jclouds.blobstore.BlobStoreFallbacks.NullOnContainerNotFound;
import static org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;

import java.io.Closeable;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.azureblob.binders.BindAzureBlobMetadataToRequest;
import org.jclouds.azureblob.binders.BindAzureBlobMetadataToMultipartRequest;
import org.jclouds.azureblob.binders.BindAzureBlocksToRequest;
import org.jclouds.azureblob.binders.BindAzureContentMetadataToRequest;
import org.jclouds.azureblob.binders.BindAzureCopyOptionsToRequest;
import org.jclouds.azureblob.binders.BindPublicAccessToRequest;
import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.ListBlobBlocksResponse;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.azureblob.functions.BlobName;
import org.jclouds.azureblob.functions.ParseBlobFromHeadersAndHttpContent;
import org.jclouds.azureblob.functions.ParseBlobPropertiesFromHeaders;
import org.jclouds.azureblob.functions.ParseContainerPropertiesFromHeaders;
import org.jclouds.azureblob.functions.ParsePublicAccessHeader;
import org.jclouds.azureblob.options.CopyBlobOptions;
import org.jclouds.azureblob.options.CreateContainerOptions;
import org.jclouds.azureblob.options.ListBlobsOptions;
import org.jclouds.azureblob.predicates.validators.BlockIdValidator;
import org.jclouds.azureblob.predicates.validators.ContainerNameValidator;
import org.jclouds.azureblob.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azureblob.xml.BlobBlocksResultsHandler;
import org.jclouds.azureblob.xml.ContainerNameEnumerationResultsHandler;
import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.inject.Provides;

/** Provides access to Azure Blob via their REST API.  */
@RequestFilters(SharedKeyLiteAuthentication.class)
@Headers(keys = AzureStorageHeaders.VERSION, values = "{jclouds.api-version}")
@SkipEncoding({ '/', '$' })
@Path("/")
public interface AzureBlobClient extends Closeable {
   @Provides
   AzureBlob newBlob();

   /**
    * The List Containers operation returns a list of the containers under the specified identity.
    * <p />
    * The 2009-07-17 version of the List Containers operation times out after 30 seconds.
    * 
    * @param listOptions
    *           controls the number or type of results requested
    * @see ListOptions
    */
   @Named("ListContainers")
   @GET
   @XMLResponseParser(AccountNameEnumerationResultsHandler.class)
   @QueryParams(keys = "comp", values = "list")
   BoundedSet<ContainerProperties> listContainers(ListOptions... listOptions);


   /**
    * The Create Container operation creates a new container under the specified identity. If the
    * container with the same name already exists, the operation fails.
    * <p/>
    * The container resource includes metadata and properties for that container. It does not
    * include a list of the blobs contained by the container.
    * 
    * @return true, if the bucket was created or false, if the container was already present
    * 
    * @see CreateContainerOptions
    * 
    */
   @Named("CreateContainer")
   @PUT
   @Path("{container}")
   @Fallback(FalseIfContainerAlreadyExists.class)
   @QueryParams(keys = "restype", values = "container")
   boolean createContainer(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         CreateContainerOptions... options);


   /**
    * The Get Container Properties operation returns all user-defined metadata and system properties
    * for the specified container. The data returned does not include the container's list of blobs.
    */
   @Named("GetContainerProperties")
   @HEAD
   @Path("{container}")
   @QueryParams(keys = "restype", values = "container")
   @ResponseParser(ParseContainerPropertiesFromHeaders.class)
   @Fallback(NullOnContainerNotFound.class)
   ContainerProperties getContainerProperties(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container);


   /**
    * Issues a HEAD command to determine if the container exists or not.
    */
   @Named("GetContainerProperties")
   @HEAD
   @Path("{container}")
   @QueryParams(keys = "restype", values = "container")
   @Fallback(FalseOnContainerNotFound.class)
   boolean containerExists(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container);

   /**
    * The Set Container Metadata operation sets one or more user-defined name/value pairs for the
    * specified container. <h4>Remarks</h4>
    * 
    * 
    * Calling the Set Container Metadata operation overwrites all existing metadata that is
    * associated with the container. It's not possible to modify an individual name/value pair.
    * <p/>
    * You may also set metadata for a container at the time it is created.
    * <p/>
    * Calling Set Container Metadata updates the ETag for the container.
    */
   @Named("SetContainerMetadata")
   @PUT
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "metadata" })
   void setResourceMetadata(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> metadata);


   /**
    * The Delete Container operation marks the specified container for deletion. The container and
    * any blobs contained within it are later deleted during garbage collection.
    * <p/>
    * When a container is deleted, a container with the same name cannot be created for at least 30
    * seconds; the container may not be available for more than 30 seconds if the service is still
    * processing the request. While the container is being deleted, attempts to create a container
    * of the same name will fail with status code 409 (Conflict), with the service returning
    * additional error information indicating that the container is being deleted. All other
    * operations, including operations on any blobs under the container, will fail with status code
    * 404 (Not Found) while the container is being deleted.
    * 
    */
   @Named("DeleteContainer")
   @DELETE
   @Path("{container}")
   @Fallback(VoidOnNotFoundOr404.class)
   @QueryParams(keys = "restype", values = "container")
   void deleteContainer(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container);

   /**
    * The root container is a default container that may be inferred from a URL requesting a blob
    * resource. The root container makes it possible to reference a blob from the top level of the
    * storage identity hierarchy, without referencing the container name.
    * <p/>
    * The container resource includes metadata and properties for that container. It does not
    * include a list of the blobs contained by the container.
    * 
    * @see CreateContainerOptions
    * 
    */
   @Named("CreateContainer")
   @PUT
   @Path("$root")
   @Fallback(FalseIfContainerAlreadyExists.class)
   @QueryParams(keys = "restype", values = "container")
   boolean createRootContainer(CreateContainerOptions... options);

   /**
    * Returns whether data in the container may be accessed publicly and the level of access
    */
   @Named("GetContainerACL")
   @HEAD
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "acl" })
   @ResponseParser(ParsePublicAccessHeader.class)
   @Fallback(NullOnContainerNotFound.class)
   PublicAccess getPublicAccessForContainer(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container);

   /**
    * Returns whether data in the container may be accessed publicly and the level of access
    */
   @Named("SetContainerACL")
   @PUT
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "acl" })
   @ResponseParser(ParseETagHeader.class)
   String setPublicAccessForContainer(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @BinderParam(BindPublicAccessToRequest.class) PublicAccess access);

   /**
    * The Delete Container operation marks the specified container for deletion. The container and
    * any blobs contained within it are later deleted during garbage collection. <h4>Remarks</h4>
    * When a container is deleted, a container with the same name cannot be created for at least 30
    * seconds; the container may not be available for more than 30 seconds if the service is still
    * processing the request. While the container is being deleted, attempts to create a container
    * of the same name will fail with status code 409 (Conflict), with the service returning
    * additional error information indicating that the container is being deleted. All other
    * operations, including operations on any blobs under the container, will fail with status code
    * 404 (Not Found) while the container is being deleted.
    * 
    * @see #deleteContainer(String)
    * @see #createRootContainer(CreateContainerOptions...)
    */
   @Named("DeleteContainer")
   @DELETE
   @Path("$root")
   @Fallback(TrueOnNotFoundOr404.class)
   @QueryParams(keys = "restype", values = "container")
   void deleteRootContainer();

   /**
    * The List Blobs operation enumerates the list of blobs under the specified container.
    * <p/>
    * <h4>Authorization</h4>
    * 
    * If the container's access control list (ACL) is set to allow anonymous access, any client may
    * call this operation.
    * <h4>Remarks</h4>
    * 
    * If you specify a value for the maxresults parameter and the number of blobs to return exceeds
    * this value, or exceeds the default value for maxresults, the response body will contain a
    * NextMarker element that indicates the next blob to return on a subsequent request. To return
    * the next set of items, specify the value of NextMarker as the marker parameter on the URI for
    * the subsequent request.
    * <p/>
    * Note that the value of NextMarker should be treated as opaque.
    * <p/>
    * The delimiter parameter enables the caller to traverse the blob keyspace by using a
    * user-configured delimiter. The delimiter may be a single character or a string. When the
    * request includes this parameter, the operation returns a BlobPrefix element. The BlobPrefix
    * element is returned in place of all blobs whose keys begin with the same substring up to the
    * appearance of the delimiter character. The value of the BlobPrefix element is
    * substring+delimiter, where substring is the common substring that begins one or more blob
    * keys, and delimiter is the value of the delimiter parameter.
    * <p/>
    * You can use the value of BlobPrefix to make a subsequent call to list the blobs that begin
    * with this prefix, by specifying the value of BlobPrefix for the prefix parameter on the
    * request URI. In this way, you can traverse a virtual hierarchy of blobs as though it were a
    * file system.
    * <p/>
    * Note that each BlobPrefix element returned counts toward the maximum result, just as each Blob
    * element does.
    * <p/>
    * Blobs are listed in alphabetical order in the response body.
    */
   @Named("ListBlobs")
   @GET
   @XMLResponseParser(ContainerNameEnumerationResultsHandler.class)
   @Path("{container}")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "list" })
   ListBlobsResponse listBlobs(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         ListBlobsOptions... options);


   @Named("ListBlobs")
   @GET
   @XMLResponseParser(ContainerNameEnumerationResultsHandler.class)
   @Path("$root")
   @QueryParams(keys = { "restype", "comp" }, values = { "container", "list" })
   ListBlobsResponse listBlobs(ListBlobsOptions... options);

   /**
    * The Put Blob operation creates a new blob or updates the content of an existing blob.
    * <p/>
    * Updating an existing blob overwrites any existing metadata on the blob. Partial updates are
    * not supported; the content of the existing blob is overwritten with the content of the new
    * blob.
    * <p/>
    * <h4>Remarks</h4>
    * The maximum upload size for a blob is 64 MB. If your blob is larger than 64 MB, you may upload
    * it as a set of blocks. For more information, see the Put Block and Put Block List operations.
    * <p/>
    * If you attempt to upload a blob that is larger than 64 MB, the service returns status code 413
    * (Request Payload Too Large). The Blob service also returns additional information about the
    * error in the response, including the maximum blob size permitted in bytes.
    */
   @Named("PutBlob")
   @PUT
   @Path("{container}/{name}")
   @Headers(keys = EXPECT, values = "100-continue")
   @ResponseParser(ParseETagHeader.class)
   String putBlob(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") @ParamParser(BlobName.class) @BinderParam(BindAzureBlobMetadataToRequest.class)
         AzureBlob object);


   /**
    * The Get Blob operation reads or downloads a blob from the system, including its metadata and
    * properties.
    */
   @Named("GetBlob")
   @GET
   @ResponseParser(ParseBlobFromHeadersAndHttpContent.class)
   @Fallback(NullOnKeyNotFound.class)
   @Path("{container}/{name}")
   AzureBlob getBlob(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") String name, GetOptions... options);

   /**
    *  The Put Block operation creates a block blob on Azure which can be later assembled into
    *  a single, large blob object with the Put Block List operation.
    */
   @Named("PutBlock")
   @PUT
   @Path("{container}/{name}")
   @QueryParams(keys = { "comp" }, values = { "block" })
   void putBlock(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") String name,
         @QueryParam("blockid") @ParamValidators(BlockIdValidator.class) String blockId, Payload part);


   /**
    *  The Put Block List assembles a list of blocks previously uploaded with Put Block into a single
    *  blob. Blocks are either already committed to a blob or uncommitted. The blocks ids passed here
    *  are searched for first in the uncommitted block list; then committed using the "latest" strategy.
    *
    *  @deprecated call putBlockList(String, AzureBlob, List&lt;String&gt;) instead
    */
   @Deprecated
   @Named("PutBlockList")
   @PUT
   @Path("{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   @QueryParams(keys = { "comp" }, values = { "blocklist" })
   String putBlockList(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") String name,
         @BinderParam(BindAzureBlocksToRequest.class) List<String> blockIdList);

   /**
    *  The Put Block List assembles a list of blocks previously uploaded with Put Block into a single
    *  blob. Blocks are either already committed to a blob or uncommitted. The blocks ids passed here
    *  are searched for first in the uncommitted block list; then committed using the "latest" strategy.
    */
   @Named("PutBlockList")
   @PUT
   @Path("{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   @QueryParams(keys = { "comp" }, values = { "blocklist" })
   String putBlockList(@PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") @ParamParser(BlobName.class) @BinderParam(BindAzureBlobMetadataToMultipartRequest.class) AzureBlob object,
         @BinderParam(BindAzureBlocksToRequest.class) List<String> blockIdList);

   @Named("GetBlockList")
   @GET
   @Path("{container}/{name}")
   @XMLResponseParser(BlobBlocksResultsHandler.class)
   @QueryParams(keys = { "comp" }, values = { "blocklist" })
   ListBlobBlocksResponse getBlockList(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") String name);


   /**
    * The Get Blob Properties operation returns all user-defined metadata, standard HTTP properties,
    * and system properties for the blob. It does not return the content of the blob.
    */
   @Named("GetBlobProperties")
   @HEAD
   @ResponseParser(ParseBlobPropertiesFromHeaders.class)
   @Fallback(NullOnKeyNotFound.class)
   @Path("{container}/{name}")
   BlobProperties getBlobProperties(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") String name);

   @Named("SetBlobProperties")
   @PUT
   @Path("{container}/{name}")
   @QueryParams(keys = { "comp" }, values = { "metadata" })
   @ResponseParser(ParseETagHeader.class)
   String setBlobProperties(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") String name,
         @BinderParam(BindAzureContentMetadataToRequest.class) ContentMetadata contentMetadata);

   @Named("SetBlobMetadata")
   @PUT
   @Path("{container}/{name}")
   @QueryParams(keys = { "comp" }, values = { "metadata" })
   @ResponseParser(ParseETagHeader.class)
   String setBlobMetadata(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") String name, @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> metadata);

   /**
    * The Delete Blob operation marks the specified blob for deletion. The blob is later deleted
    * during garbage collection.
    */
   @Named("DeleteBlob")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("{container}/{name}")
   void deleteBlob(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") String name);
   /**
    * @throws org.jclouds.blobstore.ContainerNotFoundException if the container is not present.
    */
   @Named("GetBlobProperties")
   @HEAD
   @Fallback(FalseOnKeyNotFound.class)
   @Path("{container}/{name}")
   boolean blobExists(
         @PathParam("container") @ParamValidators(ContainerNameValidator.class) String container,
         @PathParam("name") String name);

   /**
    * @throws ContainerNotFoundException if the container is not present.
    */
   @Named("CopyBlob")
   @PUT
   @Path("{toContainer}/{toName}")
   @Headers(keys = AzureStorageHeaders.COPY_SOURCE, values = "{copySource}")
   void copyBlob(
         @PathParam("copySource") URI copySource,
         @PathParam("toContainer") @ParamValidators(ContainerNameValidator.class) String toContainer, @PathParam("toName") String toName,
         @BinderParam(BindAzureCopyOptionsToRequest.class) CopyBlobOptions options);
}
