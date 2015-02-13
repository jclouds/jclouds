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
package org.jclouds.s3;

import static com.google.common.net.HttpHeaders.EXPECT;
import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;
import static org.jclouds.s3.S3Fallbacks.TrueOn404OrNotFoundFalseOnIllegalState;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowKeyNotFoundOn404;
import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.s3.binders.BindACLToXMLPayload;
import org.jclouds.s3.binders.BindAsHostPrefixIfConfigured;
import org.jclouds.s3.binders.BindBucketLoggingToXmlPayload;
import org.jclouds.s3.binders.BindIterableAsPayloadToDeleteRequest;
import org.jclouds.s3.binders.BindNoBucketLoggingToXmlPayload;
import org.jclouds.s3.binders.BindObjectMetadataToRequest;
import org.jclouds.s3.binders.BindPartIdsAndETagsToRequest;
import org.jclouds.s3.binders.BindPayerToXmlPayload;
import org.jclouds.s3.binders.BindS3ObjectMetadataToRequest;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.BucketLogging;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.s3.domain.DeleteResult;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.Payer;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.fallbacks.FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.jclouds.s3.functions.AssignCorrectHostnameForBucket;
import org.jclouds.s3.functions.BindRegionToXmlPayload;
import org.jclouds.s3.functions.DefaultEndpointThenInvalidateRegion;
import org.jclouds.s3.functions.ETagFromHttpResponseViaRegex;
import org.jclouds.s3.functions.ObjectKey;
import org.jclouds.s3.functions.ObjectMetadataKey;
import org.jclouds.s3.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.s3.functions.ParseObjectMetadataFromHeaders;
import org.jclouds.s3.functions.UploadIdFromHttpResponseViaRegex;
import org.jclouds.s3.options.CopyObjectOptions;
import org.jclouds.s3.options.ListBucketOptions;
import org.jclouds.s3.options.PutBucketOptions;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.predicates.validators.BucketNameValidator;
import org.jclouds.s3.xml.AccessControlListHandler;
import org.jclouds.s3.xml.BucketLoggingHandler;
import org.jclouds.s3.xml.CopyObjectHandler;
import org.jclouds.s3.xml.DeleteResultHandler;
import org.jclouds.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.s3.xml.ListBucketHandler;
import org.jclouds.s3.xml.LocationConstraintHandler;
import org.jclouds.s3.xml.PayerHandler;

import com.google.inject.Provides;

/**
 * Provides access to S3 via their REST API.
 */
@RequestFilters(RequestAuthorizeSignature.class)
@BlobScope(CONTAINER)
public interface S3Client extends Closeable {

   /**
    * Creates a default implementation of S3Object
    */
   @Provides
   S3Object newS3Object();

   /**
    * Retrieves the S3Object associated with the Key or KeyNotFoundException if not available;
    * 
    * <p/>
    * To use GET, you must have READ access to the object. If READ access is granted to the
    * anonymous user, you can request the object without an authorization header.
    * 
    * <p />
    * This command allows you to specify {@link GetOptions} to control delivery of content.
    * 
    * <h2>Note</h2>
    * If you specify any of the below options, you will receive partial content:
    * <ul>
    * <li>{@link GetOptions#range}</li>
    * <li>{@link GetOptions#startAt}</li>
    * <li>{@link GetOptions#tail}</li>
    * </ul>
    * 
    * @param bucketName
    *           namespace of the object you are retrieving
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @return Future reference to a fully populated S3Object including data stored in S3
    *         or null if not present.
    * 
    * @throws org.jclouds.http.HttpResponseException
    *            if the conditions requested set were not satisfied by the object on the server.
    */
   @Named("GetObject")
   @GET
   @Path("/{key}")
   @Fallback(NullOnKeyNotFound.class)
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   S3Object getObject(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") String key, GetOptions... options);

   /**
    * Retrieves the {@link org.jclouds.s3.domain.internal.BucketListObjectMetadata metadata} of
    * the object associated with the key or null if not available.
    * 
    * <p/>
    * The HEAD operation is used to retrieve information about a specific object or object size,
    * without actually fetching the object itself. This is useful if you're only interested in the
    * object metadata, and don't want to waste bandwidth on the object data.
    * 
    * 
    * @param bucketName namespace of the metadata you are retrieving
    * @param key unique key in the s3Bucket identifying the object
    * @return metadata associated with the key or null if not present.
    */
   @Named("GetObject")
   @HEAD
   @Path("/{key}")
   @Fallback(NullOnKeyNotFound.class)
   @ResponseParser(ParseObjectMetadataFromHeaders.class)
   ObjectMetadata headObject(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") String key);

   @Named("GetObject")
   @HEAD
   @Path("/{key}")
   @Fallback(FalseOnKeyNotFound.class)
   boolean objectExists(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") String key);

   /**
    * Removes the object and metadata associated with the key.
    * <p/>
    * The DELETE request operation removes the specified object from Amazon S3. Once deleted, there
    * is no method to restore or undelete an object.
    * 
    * 
    * @param bucketName
    *           namespace of the object you are deleting
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @throws org.jclouds.http.HttpResponseException
    *            if the bucket is not available
    */
   @Named("DeleteObject")
   @DELETE
   @Path("/{key}")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteObject(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") String key);

   /**
    * The Multi-Object Delete operation enables you to delete multiple objects from a bucket using a
    * single HTTP request. If you know the object keys that you want to delete, then this operation
    * provides a suitable alternative to sending individual delete requests (see DELETE Object),
    * reducing per-request overhead.
    *
    * The Multi-Object Delete request contains a set of up to 1000 keys that you want to delete.
    *
    * If a key does not exist is considered to be deleted.
    *
    * The Multi-Object Delete operation supports two modes for the response; verbose and quiet.
    * By default, the operation uses verbose mode in which the response includes the result of
    * deletion of each key in your request.
    *
    * @param bucketName
    *           namespace of the objects you are deleting
    * @param keys
    *           set of unique keys identifying objects
    */
   @Named("DeleteObject")
   @POST
   @Path("/")
   @QueryParams(keys = "delete")
   @XMLResponseParser(DeleteResultHandler.class)
   DeleteResult deleteObjects(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @BinderParam(BindIterableAsPayloadToDeleteRequest.class) Iterable<String> keys);

   /**
    * Store data by creating or overwriting an object.
    * <p/>
    * This method will store the object with the default <code>private</code acl.
    * 
    * <p/>
    * This returns a byte[] of the eTag hash of what Amazon S3 received
    * <p />
    * 
    * @param bucketName
    *           namespace of the object you are storing
    * @param object
    *           contains the data and metadata to create or overwrite
    * @param options
    *           options for creating the object
    * @return ETag of the content uploaded
    * @throws org.jclouds.http.HttpResponseException
    *            if the conditions requested set are not satisfied by the object on the server.
    * @see org.jclouds.s3.domain.CannedAccessPolicy#PRIVATE
    */
   @Named("PutObject")
   @PUT
   @Path("/{key}")
   @Headers(keys = EXPECT, values = "100-continue")
   @ResponseParser(ParseETagHeader.class)
   String putObject(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") @ParamParser(ObjectKey.class) @BinderParam(BindS3ObjectMetadataToRequest.class)
         S3Object object, PutObjectOptions... options);

   /**
    * Create and name your own bucket in which to store your objects.
    * 
    * <p/>
    * you can use {@link PutBucketOptions} to create the bucket in EU.
    * <p/>
    * The PUT request operation with a bucket URI creates a new bucket. Depending on your latency
    * and legal requirements, you can specify a location constraint that will affect where your data
    * physically resides. You can currently specify a Europe (EU) location constraint via
    * {@link PutBucketOptions}.
    * 
    * @param options
    *           for creating your bucket
    * @return true, if the bucket was created or false, if the container was already present
    * 
    * @see PutBucketOptions
    */
   @Named("CreateBucket")
   @PUT
   @Path("/")
   @Endpoint(Bucket.class)
   @Fallback(FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists.class)
   boolean putBucketInRegion(@BinderParam(BindRegionToXmlPayload.class) @Nullable String region,
         @Bucket @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class)
         String bucketName, PutBucketOptions... options);

   /**
    * Deletes the bucket, if it is empty.
    * <p/>
    * The DELETE request operation deletes the bucket named in the URI. All objects in the bucket
    * must be deleted before the bucket itself can be deleted.
    * <p />
    * Only the owner of a bucket can delete it, regardless of the bucket's access control policy.
    * 
    * 
    * @param bucketName what to delete
    * @return false, if the bucket was not empty and therefore not deleted
    */
   @Named("DeleteBucket")
   @DELETE
   @Path("/")
   @Fallback(TrueOn404OrNotFoundFalseOnIllegalState.class)
   boolean deleteBucketIfEmpty(@Bucket @EndpointParam(parser = DefaultEndpointThenInvalidateRegion.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);


   /**
    * Issues a HEAD command to determine if the bucket exists or not.
    */
   @Named("BucketExists")
   @HEAD
   @Path("/")
   @Fallback(FalseOnContainerNotFound.class)
   boolean bucketExists(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);

   /**
    * Retrieve a {@code S3Bucket} listing. A GET request operation using a bucket URI lists
    * information about the objects in the bucket. You can use {@link ListBucketOptions} to control
    * the amount of S3Objects to return.
    * <p />
    * To list the keys of a bucket, you must have READ access to the bucket.
    * <p/>
    * 
    * @param bucketName namespace of the objects you wish to list
    * @return potentially empty or partial list of the bucket.
    * @see ListBucketOptions
    */
   @Named("ListBucket")
   @GET
   @Path("/")
   @XMLResponseParser(ListBucketHandler.class)
   ListBucketResponse listBucket(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         ListBucketOptions... options);

   /**
    * Returns a list of all of the buckets owned by the authenticated sender of the request.
    * 
    * @return list of all of the buckets owned by the authenticated sender of the request.
    */
   @Named("ListAllMyBuckets")
   @GET
   @XMLResponseParser(ListAllMyBucketsHandler.class)
   @Path("/")
   @VirtualHost
   Set<BucketMetadata> listOwnedBuckets();


   /**
    * Copies one object to another bucket, retaining UserMetadata from the source. The destination
    * will have a private acl. The copy operation creates a copy of an object that is already stored
    * in Amazon S3.
    * <p/>
    * When copying an object, you can preserve all metadata (default) or
    * {@link CopyObjectOptions#overrideMetadataWith(java.util.Map)} specify new
    * metadata}. However, the ACL is not preserved and is set to private for the user making the
    * request. To override the default ACL setting,
    * {@link CopyObjectOptions#overrideAcl(org.jclouds.s3.domain.CannedAccessPolicy) specify a
    * new ACL} when generating a copy request.
    * 
    * @return metadata populated with lastModified and eTag of the new object
    * @throws org.jclouds.http.HttpResponseException
    *            if the conditions requested set are not satisfied by the object on the server.
    * @see CopyObjectOptions
    * @see org.jclouds.s3.domain.CannedAccessPolicy
    */
   @Named("PutObject")
   @PUT
   @Path("/{destinationObject}")
   @Headers(keys = "x-amz-copy-source", values = "/{sourceBucket}/{sourceObject}")
   @XMLResponseParser(CopyObjectHandler.class)
   ObjectMetadata copyObject(@PathParam("sourceBucket") String sourceBucket,
         @PathParam("sourceObject") String sourceObject,
         @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
               BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String destinationBucket,
         @PathParam("destinationObject") String destinationObject, CopyObjectOptions... options);


   /**
    * 
    * A GET request operation directed at an object or bucket URI with the "acl" parameter retrieves
    * the Access Control List (ACL) settings for that S3 item.
    * <p />
    * To list a bucket's ACL, you must have READ_ACP access to the item.
    * 
    * @return access permissions of the bucket
    */
   @Named("GetBucketAcl")
   @GET
   @QueryParams(keys = "acl")
   @XMLResponseParser(AccessControlListHandler.class)
   @Fallback(ThrowContainerNotFoundOn404.class)
   @Path("/")
   AccessControlList getBucketACL(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);


   /**
    * Update a bucket's Access Control List settings.
    * <p/>
    * A PUT request operation directed at a bucket URI with the "acl" parameter sets the Access
    * Control List (ACL) settings for that S3 item.
    * <p />
    * To set a bucket or object's ACL, you must have WRITE_ACP or FULL_CONTROL access to the item.
    * 
    * @param bucketName
    *           the bucket whose Access Control List settings will be updated.
    * @param acl
    *           the ACL to apply to the bucket. This acl object <strong>must</strong include a valid
    *           owner identifier string in {@link AccessControlList#getOwner()}.
    * @return true if the bucket's Access Control List was updated successfully.
    */
   @Named("PutBucketAcl")
   @PUT
   @Path("/")
   @QueryParams(keys = "acl")
   boolean putBucketACL(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @BinderParam(BindACLToXMLPayload.class) AccessControlList acl);

   /**
    * A GET request operation directed at an object or bucket URI with the "acl" parameter retrieves
    * the Access Control List (ACL) settings for that S3 item.
    * <p />
    * To list a object's ACL, you must have READ_ACP access to the item.
    * 
    * @return access permissions of the object
    */
   @Named("GetObjectAcl")
   @GET
   @QueryParams(keys = "acl")
   @Path("/{key}")
   @XMLResponseParser(AccessControlListHandler.class)
   @Fallback(ThrowKeyNotFoundOn404.class)
   AccessControlList getObjectACL(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") String key);

   /**
    * Update an object's Access Control List settings.
    * <p/>
    * A PUT request operation directed at an object URI with the "acl" parameter sets the Access
    * Control List (ACL) settings for that S3 item.
    * <p />
    * To set a bucket or object's ACL, you must have WRITE_ACP or FULL_CONTROL access to the item.
    * 
    * @param bucketName
    *           the bucket containing the object to be updated
    * @param key
    *           the key of the object whose Access Control List settings will be updated.
    * @param acl
    *           the ACL to apply to the object. This acl object <strong>must</strong include a valid
    *           owner identifier string in {@link AccessControlList#getOwner()}.
    * @return true if the object's Access Control List was updated successfully.
    */
   @Named("PutObjectAcl")
   @PUT
   @QueryParams(keys = "acl")
   @Path("/{key}")
   boolean putObjectACL(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") String key, @BinderParam(BindACLToXMLPayload.class) AccessControlList acl);


   /**
    * A GET location request operation using a bucket URI lists the location constraint of the
    * bucket.
    * <p/>
    * To view the location constraint of a bucket, you must be the bucket owner.
    * 
    * @param bucketName
    *           the bucket you wish to know where exists
    * 
    * @return location of the bucket
    */
   @Named("GetBucketLocation")
   @GET
   @QueryParams(keys = "location")
   @Path("/")
   @Endpoint(Bucket.class)
   @XMLResponseParser(LocationConstraintHandler.class)
   String getBucketLocation(@Bucket @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(
         BucketNameValidator.class) String bucketName);


   /**
    * A GET request operation on a requestPayment resource returns the request payment configuration
    * of a bucket.
    * <p/>
    * Only the bucket owner has permissions to get this value.
    * 
    * @param bucketName
    *           the bucket you wish to know the payer status
    * 
    * @return {@link Payer#REQUESTER} for a Requester Pays bucket, and {@link Payer#BUCKET_OWNER},
    *         for a normal bucket.
    */
   @Named("GetBucketRequestPayment")
   @GET
   @QueryParams(keys = "requestPayment")
   @Path("/")
   @XMLResponseParser(PayerHandler.class)
   Payer getBucketPayer(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);


   /**
    * The PUT request operation with a requestPayment URI configures an existing bucket to be
    * Requester Pays or not. To make a bucket a Requester Pays bucket, make the Payer value
    * Requester. Otherwise, make the value BucketOwner.
    * <p/>
    * Only a bucket owner is allowed to configure a bucket. As a result any requests for this
    * resource should be signed with the bucket owner's credentials. Anonymous requests are never
    * allowed to create Requester Pays buckets.
    * 
    * @param bucketName
    *           the bucket you wish to know the payer status
    * 
    * @param payer
    *           {@link Payer#REQUESTER} for a Requester Pays bucket, and {@link Payer#BUCKET_OWNER},
    *           for a normal bucket.
    */
   @Named("PutBucketRequestPayment")
   @PUT
   @QueryParams(keys = "requestPayment")
   @Path("/")
   void setBucketPayer(
         @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
               BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @BinderParam(BindPayerToXmlPayload.class) Payer payer);



   /**
    * Inspects the logging status for a bucket.
    * 
    * 
    * @param bucketName
    *           the bucket you wish to know the logging status
    * @return bucketLogging configuration or null, if not configured
    */
   @Named("GetBucketLogging")
   @GET
   @QueryParams(keys = "logging")
   @XMLResponseParser(BucketLoggingHandler.class)
   @Fallback(ThrowContainerNotFoundOn404.class)
   @Path("/")
   BucketLogging getBucketLogging(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);


   /**
    * Enables logging for a bucket.
    * 
    * @param bucketName
    *           the bucket you wish to enable logging for
    * @param logging
    *           configuration including destination, prefix, and access rules
    */
   @Named("PutBucketLogging")
   @PUT
   @Path("/")
   @QueryParams(keys = "logging")
   void enableBucketLogging(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @BinderParam(BindBucketLoggingToXmlPayload.class) BucketLogging logging);

   /**
    * Disables logging for a bucket.
    * 
    * @param bucketName
    *           the bucket you wish to disable logging for
    */
   @Named("PutBucketLogging")
   @PUT
   @Path("/")
   @QueryParams(keys = "logging")
   @Produces(MediaType.TEXT_XML)
   void disableBucketLogging(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
               BindNoBucketLoggingToXmlPayload.class) @ParamValidators(BucketNameValidator.class) String bucketName);

   /**
    * This operation initiates a multipart upload and returns an upload ID. This upload ID is used
    * to associate all the parts in the specific multipart upload. You specify this upload ID in
    * each of your subsequent upload part requests (see Upload Part). You also include this upload
    * ID in the final request to either complete or abort the multipart upload request.
    *
    * <h4>Note</h4> If you create an object using the multipart upload APIs, currently you cannot
    * copy the object between regions.
    *
    *
    * @param bucketName
    *           namespace of the object you are to upload
    * @param objectMetadata
    *           metadata around the object you wish to upload
    * @param options
    *           controls optional parameters such as canned ACL
    * @return ID for the initiated multipart upload.
    */
   @Named("PutObject")
   @POST
   @QueryParams(keys = "uploads")
   @Path("/{key}")
   @ResponseParser(UploadIdFromHttpResponseViaRegex.class)
   String initiateMultipartUpload(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") @ParamParser(ObjectMetadataKey.class) @BinderParam(BindObjectMetadataToRequest.class)
         ObjectMetadata objectMetadata, PutObjectOptions... options);

   /**
    * This operation aborts a multipart upload. After a multipart upload is aborted, no additional
    * parts can be uploaded using that upload ID. The storage consumed by any previously uploaded
    * parts will be freed. However, if any part uploads are currently in progress, those part
    * uploads might or might not succeed. As a result, it might be necessary to abort a given
    * multipart upload multiple times in order to completely free all storage consumed by all parts.
    *
    *
    * @param bucketName
    *           namespace of the object you are deleting
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @param uploadId
    *           id of the multipart upload in progress.
    */
   @Named("AbortMultipartUpload")
   @DELETE
   @Path("/{key}")
   @Fallback(VoidOnNotFoundOr404.class)
   void abortMultipartUpload(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") String key, @QueryParam("uploadId") String uploadId);

   /**
    * This operation uploads a part in a multipart upload. You must initiate a multipart upload (see
    * Initiate Multipart Upload) before you can upload any part. In response to your initiate
    * request. Amazon S3 returns an upload ID, a unique identifier, that you must include in your
    * upload part request.
    *
    * <p/>
    * Part numbers can be any number from 1 to 10,000, inclusive. A part number uniquely identifies
    * a part and also defines its position within the object being created. If you upload a new part
    * using the same part number that was used with a previous part, the previously uploaded part is
    * overwritten. Each part must be at least 5 MB in size, except the last part. There is no size
    * limit on the last part of your multipart upload.
    *
    * <p/>
    * To ensure that data is not corrupted when traversing the network, specify the Content-MD5
    * header in the upload part request. Amazon S3 checks the part data against the provided MD5
    * value. If they do not match, Amazon S3 returns an error.
    *
    *
    * @param bucketName
    *           namespace of the object you are storing
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @param partNumber
    *           which part is this.
    * @param uploadId
    *           id of the multipart upload in progress.
    * @param part
    *           contains the data to create or overwrite
    * @return ETag of the content uploaded
    */
   @Named("PutObject")
   @PUT
   @Path("/{key}")
   @ResponseParser(ParseETagHeader.class)
   String uploadPart(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") String key, @QueryParam("partNumber") int partNumber,
         @QueryParam("uploadId") String uploadId, Payload part);

   /**
    *
    This operation completes a multipart upload by assembling previously uploaded parts.
    * <p/>
    * You first initiate the multipart upload and then upload all parts using the Upload Parts
    * operation (see Upload Part). After successfully uploading all relevant parts of an upload, you
    * call this operation to complete the upload. Upon receiving this request, Amazon S3
    * concatenates all the parts in ascending order by part number to create a new object. In the
    * Complete Multipart Upload request, you must provide the parts list. For each part in the list,
    * you must provide the part number and the ETag header value, returned after that part was
    * uploaded.
    * <p/>
    * Processing of a Complete Multipart Upload request could take several minutes to complete.
    * After Amazon S3 begins processing the request, it sends an HTTP response header that specifies
    * a 200 OK response. While processing is in progress, Amazon S3 periodically sends whitespace
    * characters to keep the connection from timing out. Because a request could fail after the
    * initial 200 OK response has been sent, it is important that you check the response body to
    * determine whether the request succeeded.
    * <p/>
    * Note that if Complete Multipart Upload fails, applications should be prepared to retry the
    * failed requests.
    *
    * @param bucketName
    *           namespace of the object you are deleting
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @param uploadId
    *           id of the multipart upload in progress.
    * @param parts
    *           a map of part id to eTag from the {@link #uploadPart} command.
    * @return ETag of the content uploaded
    */
   @Named("PutObject")
   @POST
   @Path("/{key}")
   @ResponseParser(ETagFromHttpResponseViaRegex.class)
   String completeMultipartUpload(@Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(
         BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
         @PathParam("key") String key, @QueryParam("uploadId") String uploadId,
         @BinderParam(BindPartIdsAndETagsToRequest.class) Map<Integer, String> parts);
}
