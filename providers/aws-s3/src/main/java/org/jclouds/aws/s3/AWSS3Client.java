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
package org.jclouds.aws.s3;

import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;

import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.s3.binders.BindIterableAsPayloadToDeleteRequest;
import org.jclouds.aws.s3.domain.DeleteResult;
import org.jclouds.aws.s3.xml.DeleteResultHandler;
import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.s3.Bucket;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.binders.BindAsHostPrefixIfConfigured;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.jclouds.s3.functions.AssignCorrectHostnameForBucket;
import org.jclouds.s3.predicates.validators.BucketNameValidator;

/**
 * Provides access to amazon-specific S3 features
 */
@RequestFilters(RequestAuthorizeSignature.class)
@BlobScope(CONTAINER)
public interface AWSS3Client extends S3Client {

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
}
