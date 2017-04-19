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
package org.jclouds.aws.s3.blobstore;

import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.s3.config.AWSS3HttpApiModule;
import org.jclouds.aws.s3.filters.AWSRequestAuthorizeSignature;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.s3.blobstore.S3BlobSignerExpectTest;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Module;
import com.google.inject.Scopes;

@Test(groups = "unit", testName = "AWSS3BlobSignerExpectTest")
public class AWSS3BlobSignerExpectTest extends S3BlobSignerExpectTest {
   private static final String DATE = "Thu, 05 Jun 2008 16:38:19 GMT";
   private static final String HOST = "container.s3.amazonaws.com";

   public AWSS3BlobSignerExpectTest() {
      provider = "aws-s3";
   }

   @Override
   protected HttpRequest getBlob() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addQueryParam("X-Amz-Algorithm", "AWS4-HMAC-SHA256")
            .addQueryParam("X-Amz-Credential", "identity/20080605/us-east-1/s3/aws4_request")
            .addQueryParam("X-Amz-Date", "20080605T163819Z")
            .addQueryParam("X-Amz-Expires", "900")
            .addQueryParam("X-Amz-SignedHeaders", "host")
            .addQueryParam("X-Amz-Signature", "1aa13b18ef9c4a9a98db7539e9eeb2c63afadbab649e14e28d5b765dfd96c32b")
            .addHeader("Host", HOST)
            .build();
   }

   @Override
   protected HttpRequest getBlobWithTime() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addQueryParam("Expires", "1212683902")
            .addQueryParam("AWSAccessKeyId", "identity")
            .addQueryParam("Signature", "Y4Ac4sZfBemGZmgfG78F7IX+IFg=")
            .addHeader("Host", "container.s3.amazonaws.com")
            .addHeader("Date", DATE).build();
   }

   @Override
   protected HttpRequest getBlobWithOptions() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addHeader("Host", HOST)
            .addHeader("Range", "bytes=0-1")
            .addHeader("x-amz-content-sha256", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
            .addHeader("X-Amz-Date", "20080605T163819Z")
            .addHeader("Authorization", "AWS4-HMAC-SHA256 Credential=identity/20080605/us-east-1/s3/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=8f6a70bf43f31c92a67095510b080f574154df8a5ccb988ec8a6cbcce03dd5b8")
            .build();
   }

   private void compareRequestComponents(final HttpRequest request, final HttpRequest compare) {
      assertEquals(request.getMethod(), compare.getMethod());
      String query = request.getEndpoint().toString().split("\\?")[1];
      final Map<String, String> params = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
      assertEquals(params.get("X-Amz-Algorithm"), "AWS4-HMAC-SHA256");
      assertEquals(params.get("X-Amz-Expires"), "900");
      assertEquals(params.get("X-Amz-SignedHeaders"), "host");
   }

   @Override
   @Test
   public void testSignGetBlobWithTime() {
      BlobStore getBlobWithTime = requestsSendResponses(init());
      HttpRequest compare = getBlobWithTime();
      HttpRequest request = getBlobWithTime.getContext().getSigner().signGetBlob(container, name, 900L /* seconds */);
      compareRequestComponents(request, compare);
   }

   @Override
   protected HttpRequest putBlob() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addHeader("Host", HOST)
            .addHeader("Expect", "100-continue")
            .addHeader("Date", "Thu, 05 Jun 2008 16:38:19 GMT")
            .addHeader("Authorization", "AWS identity:zM2oT+71KcoOSxv1SU5L12UXnT8=").build();
   }

   @Override
   protected HttpRequest putBlobWithTime() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addQueryParam("Expires", "1212683902")
            .addQueryParam("AWSAccessKeyId", "identity")
            .addQueryParam("Signature", "genkB2vLxe3AWV/bPvRTMqQts7E=")
            .addHeader("Expect", "100-continue")
            .addHeader("Host", "container.s3.amazonaws.com")
            .addHeader("Date", DATE)
            .build();
   }

   @Override
   protected HttpRequest removeBlob() {
      return HttpRequest.builder().method("DELETE")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addHeader("x-amz-content-sha256", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
            .addHeader("X-Amz-Date", "20080605T163819Z")
            .addHeader("Authorization", "AWS4-HMAC-SHA256 Credential=identity/20080605/us-east-1/s3/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=b068a3b2a76f06bf1d73b907243602f43962f5572ea1e588ed193c8c656118fe")
            .addHeader("Host", HOST)
            .build();
   }

   @Override
   @Test
   public void testSignPutBlob() throws Exception {
      BlobStore signPutBloblWithTime = requestsSendResponses(init());
      Blob blob = signPutBloblWithTime.blobBuilder(name).payload(text).contentType("text/plain").build();
      HttpRequest compare = putBlobWithTime();
      compare.setPayload(blob.getPayload());
      HttpRequest request = signPutBloblWithTime.getContext().getSigner().signPutBlob(container, blob);
      compareRequestComponents(request, compare);
      assertEquals(request.getPayload(), compare.getPayload());
   }

   @Override
   @Test
   public void testSignPutBlobWithTime() throws Exception {
      BlobStore signPutBloblWithTime = requestsSendResponses(init());
      Blob blob = signPutBloblWithTime.blobBuilder(name).payload(text).contentType("text/plain").build();
      HttpRequest compare = putBlobWithTime();
      compare.setPayload(blob.getPayload());
      HttpRequest request = signPutBloblWithTime.getContext().getSigner().signPutBlob(container, blob, 900L /* seconds */);
      compareRequestComponents(request, compare);
      assertEquals(request.getPayload(), compare.getPayload());
   }

   @Override
   protected Module createModule() {
      return new TestAWSS3HttpApiModule();
   }

   @ConfiguresHttpApi
   private static final class TestAWSS3HttpApiModule extends AWSS3HttpApiModule {
      @Override
      @TimeStamp
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return DATE;
      }

      @Override
      @TimeStamp
      protected Supplier<Date> provideTimeStampCacheDate(
            @Named(Constants.PROPERTY_SESSION_INTERVAL) long seconds,
            @TimeStamp final Supplier<String> timestamp,
            final DateService dateService) {
         return Suppliers.ofInstance(new Date(1212683899000L));
      }
   }
}
