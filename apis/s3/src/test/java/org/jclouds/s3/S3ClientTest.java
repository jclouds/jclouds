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

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.domain.Region;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowKeyNotFoundOn404;
import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.date.TimeStamp;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.s3.S3Fallbacks.TrueOn404OrNotFoundFalseOnIllegalState;
import org.jclouds.s3.config.S3HttpApiModule;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.s3.domain.AccessControlList.Grant;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.BucketLogging;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
import org.jclouds.s3.domain.Payer;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.fallbacks.FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists;
import org.jclouds.s3.functions.ETagFromHttpResponseViaRegex;
import org.jclouds.s3.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.s3.functions.ParseObjectMetadataFromHeaders;
import org.jclouds.s3.functions.UploadIdFromHttpResponseViaRegex;
import org.jclouds.s3.internal.BaseS3ClientTest;
import org.jclouds.s3.options.CopyObjectOptions;
import org.jclouds.s3.options.ListBucketOptions;
import org.jclouds.s3.options.PutBucketOptions;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.xml.AccessControlListHandler;
import org.jclouds.s3.xml.BucketLoggingHandler;
import org.jclouds.s3.xml.CopyObjectHandler;
import org.jclouds.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.s3.xml.ListBucketHandler;
import org.jclouds.s3.xml.LocationConstraintHandler;
import org.jclouds.s3.xml.PayerHandler;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
import com.google.inject.Module;
/**
 * Tests behavior of {@code S3Client}
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "S3ClientTest")
public abstract class S3ClientTest<T extends S3Client> extends BaseS3ClientTest<T> {

   protected String url = "s3.amazonaws.com";

   public void testAllRegions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "putBucketInRegion", String.class, String.class,
               PutBucketOptions[].class);
      for (String region : Region.DEFAULT_S3) {
         processor.createRequest(method, ImmutableList.<Object> of(region, "bucket-" + region));
      }
   }

   public void testGetBucketLocation() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "getBucketLocation", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket"));

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?location HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      request = (GeneratedHttpRequest) filter.filter(request);

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?location HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "Authorization: AWS identity:2fFTeYJTDwiJmaAkKj732RjNbOg=\nDate: 2009-11-08T15:54:08.897Z\nHost: bucket."
                        + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, LocationConstraintHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetBucketPayer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "getBucketPayer", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket"));

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?requestPayment HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PayerHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetBucketPayerOwner() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "setBucketPayer", String.class, Payer.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", Payer.BUCKET_OWNER));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?requestPayment HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "<RequestPaymentConfiguration xmlns=\"http://" + url
               + "/doc/2006-03-01/\"><Payer>BucketOwner</Payer></RequestPaymentConfiguration>", "text/xml", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetBucketPayerRequester() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "setBucketPayer", String.class, Payer.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", Payer.REQUESTER));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?requestPayment HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "<RequestPaymentConfiguration xmlns=\"http://" + url
               + "/doc/2006-03-01/\"><Payer>Requester</Payer></RequestPaymentConfiguration>", "text/xml", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListBucket() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "listBucket", String.class,
               ListBucketOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket"));

      assertRequestLineEquals(request, "GET https://bucket." + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ListBucketHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testBucketExists() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "bucketExists", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket"));

      assertRequestLineEquals(request, "HEAD https://bucket." + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnContainerNotFound.class);

      checkFilters(request);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testCopyObjectInvalidName() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "copyObject", String.class, String.class, String.class,
               String.class, CopyObjectOptions[].class);
      processor.createRequest(method, ImmutableList.<Object> of("sourceBucket", "sourceObject", "destinationBucket", "destinationObject"));

   }

   public void testCopyObject() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "copyObject", String.class, String.class, String.class,
               String.class, CopyObjectOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("sourceBucket", "sourceObject", "destinationbucket",
               "destinationObject"));

      assertRequestLineEquals(request, "PUT https://destinationbucket." + url + "/destinationObject HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: destinationbucket." + url
               + "\nx-amz-copy-source: /sourceBucket/sourceObject\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CopyObjectHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteBucketIfEmpty() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "deleteBucketIfEmpty", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket"));

      assertRequestLineEquals(request, "DELETE https://bucket." + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, TrueOn404OrNotFoundFalseOnIllegalState.class);

      checkFilters(request);
   }

   public void testDeleteObject() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "deleteObject", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", "object"));

      assertRequestLineEquals(request, "DELETE https://bucket." + url + "/object HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetBucketACL() throws SecurityException, NoSuchMethodException, IOException {

      Invokable<?, ?> method = method(S3Client.class, "getBucketACL", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket"));

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccessControlListHandler.class);
      assertFallbackClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(request);
   }

   public void testGetObject() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "getObject", String.class, String.class, GetOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", "object"));

      assertRequestLineEquals(request, "GET https://bucket." + url + "/object HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseObjectFromHeadersAndHttpContent.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnKeyNotFound.class);

      checkFilters(request);
   }

   public void testGetObjectACL() throws SecurityException, NoSuchMethodException, IOException {

      Invokable<?, ?> method = method(S3Client.class, "getObjectACL", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", "object"));

      assertRequestLineEquals(request, "GET https://bucket." + url + "/object?acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccessControlListHandler.class);
      assertFallbackClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(request);
   }

   public void testObjectExists() throws SecurityException, NoSuchMethodException, IOException {

      Invokable<?, ?> method = method(S3Client.class, "objectExists", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", "object"));

      assertRequestLineEquals(request, "HEAD https://bucket." + url + "/object HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnKeyNotFound.class);

      checkFilters(request);
   }

   public void testHeadObject() throws SecurityException, NoSuchMethodException, IOException {

      Invokable<?, ?> method = method(S3Client.class, "headObject", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", "object"));

      assertRequestLineEquals(request, "HEAD https://bucket." + url + "/object HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseObjectMetadataFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnKeyNotFound.class);

      checkFilters(request);
   }

   public void testListOwnedBuckets() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "listOwnedBuckets");
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(request, "GET https://" + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: " + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ListAllMyBucketsHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testNewS3Object() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "newS3Object");
      assertEquals(method.getReturnType().getRawType(), S3Object.class);
   }

   public void testPutBucketACL() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "putBucketACL", String.class, AccessControlList.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", AccessControlList.fromCannedAccessPolicy(
               CannedAccessPolicy.PRIVATE, "1234")));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(
               request,
               "<AccessControlPolicy xmlns=\"http://"
                        + url
                        + "/doc/2006-03-01/\"><Owner><ID>1234</ID></Owner><AccessControlList><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1234</ID></Grantee><Permission>FULL_CONTROL</Permission></Grant></AccessControlList></AccessControlPolicy>",
               "text/xml", false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testPutBucketDefault() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "putBucketInRegion", String.class, String.class,
               PutBucketOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null, "bucket"));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists.class);

      checkFilters(request);
   }

   public void testPutObject() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {

      Invokable<?, ?> method = method(S3Client.class, "putObject", String.class, S3Object.class, PutObjectOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", blobToS3Object
               .apply(BindBlobToMultipartFormTest.TEST_BLOB)));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/hello HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "hello", "text/plain", false);

      assertResponseParserClassEquals(method, request, ParseETagHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testPutObjectACL() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "putObjectACL", String.class, String.class, AccessControlList.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", "key", AccessControlList.fromCannedAccessPolicy(
               CannedAccessPolicy.PRIVATE, "1234")));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/key?acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(
               request,
               "<AccessControlPolicy xmlns=\"http://"
                        + url
                        + "/doc/2006-03-01/\"><Owner><ID>1234</ID></Owner><AccessControlList><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1234</ID></Grantee><Permission>FULL_CONTROL</Permission></Grant></AccessControlList></AccessControlPolicy>",
               "text/xml", false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetBucketLogging() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "getBucketLogging", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket"));

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?logging HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BucketLoggingHandler.class);
      assertFallbackClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(request);
   }

   public void testDisableBucketLogging() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "disableBucketLogging", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket"));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?logging HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "<BucketLoggingStatus xmlns=\"http://" + url + "/doc/2006-03-01/\"/>", "text/xml",
               false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testEnableBucketLoggingOwner() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(S3Client.class, "enableBucketLogging", String.class, BucketLogging.class);
      GeneratedHttpRequest request = processor
               .createRequest(method, ImmutableList.<Object> of("bucket", new BucketLogging("mylogs", "access_log-", ImmutableSet
                        .<Grant> of(new Grant(new EmailAddressGrantee("adrian@jclouds.org"), Permission.FULL_CONTROL)))));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?logging HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/bucket_logging.xml")),
               "text/xml", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testInitiateMultipartUpload() throws SecurityException, NegativeArraySizeException,
         NoSuchMethodException {
      Invokable<?, ?> method = method(S3Client.class, "initiateMultipartUpload", String.class, ObjectMetadata.class,
            PutObjectOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", ObjectMetadataBuilder.create().key("foo")
            .contentMD5(new byte[16]).build()));

      assertRequestLineEquals(request, "POST https://bucket." + url + "/foo?uploads HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
            "Content-MD5: AAAAAAAAAAAAAAAAAAAAAA==\n" +
            "Content-Type: binary/octet-stream\n" +
            "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      // as this is a payload-related command, but with no payload, be careful
      // that we check
      // filtering and do not ignore if this fails later.
      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://bucket." + url + "/foo?uploads HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
            "Authorization: AWS identity:972m/Bqn2L5FIaB+wWDeY83mGvU=\n" +
            "Content-MD5: AAAAAAAAAAAAAAAAAAAAAA==\n" +
            "Content-Type: binary/octet-stream\n" +
            "Date: 2009-11-08T15:54:08.897Z\n" +
            "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UploadIdFromHttpResponseViaRegex.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testAbortMultipartUpload() throws SecurityException, NegativeArraySizeException, NoSuchMethodException {
      Invokable<?, ?> method = method(S3Client.class, "abortMultipartUpload", String.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", "foo", "asdsadasdas", 1,
            Payloads.newStringPayload("")));

      assertRequestLineEquals(request, "DELETE https://bucket." + url + "/foo?uploadId=asdsadasdas HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "", "application/unknown", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUploadPart() throws SecurityException, NegativeArraySizeException, NoSuchMethodException {
      Invokable<?, ?> method = method(S3Client.class, "uploadPart", String.class, String.class, int.class,
            String.class, Payload.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", "foo", 1, "asdsadasdas",
            Payloads.newStringPayload("")));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/foo?partNumber=1&uploadId=asdsadasdas HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "", "application/unknown", false);

      assertResponseParserClassEquals(method, request, ParseETagHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testCompleteMultipartUpload() throws SecurityException, NegativeArraySizeException,
         NoSuchMethodException {
      Invokable<?, ?> method = method(S3Client.class, "completeMultipartUpload", String.class, String.class,
            String.class, Map.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("bucket", "foo", "asdsadasdas",
            ImmutableMap.<Integer, String> of(1, "\"a54357aff0632cce46d942af68356b38\"")));

      assertRequestLineEquals(request, "POST https://bucket." + url + "/foo?uploadId=asdsadasdas HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(
            request,
            "<CompleteMultipartUpload><Part><PartNumber>1</PartNumber><ETag>\"a54357aff0632cce46d942af68356b38\"</ETag></Part></CompleteMultipartUpload>",
            "text/xml", false);

      assertResponseParserClassEquals(method, request, ETagFromHttpResponseViaRegex.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   @ConfiguresHttpApi
   private static final class TestS3HttpApiModule extends S3HttpApiModule<S3Client> {

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "2009-11-08T15:54:08.897Z";
      }
   }

   @Override
   protected Module createModule() {
      return new TestS3HttpApiModule();
   }

}
