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
package org.jclouds.s3.filters;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.util.Date;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.internal.BaseRestApiTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.s3.S3ApiMetadata;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.config.S3HttpApiModule;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.options.PutObjectOptions;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code RequestAuthorizeSignature}
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "RequestAuthorizeSignatureV4Test")
public class RequestAuthorizeSignatureV4Test {
   private static final String IDENTITY = "AKIAPAEBI3QI4EXAMPLE";
   private static final String CREDENTIAL = "oHkkcPcOjJnoAXpjT8GXdNeBjo6Ru7QeFExAmPlE";
   private static final String TIMESTAMP = "Thu, 03 Feb 2015 07:11:11 GMT";

   private static final String GET_BUCKET_LOCATION_SIGNATURE_RESULT = "AWS4-HMAC-SHA256 "
         + "Credential=AKIAPAEBI3QI4EXAMPLE/20150203/cn-north-1/s3/aws4_request, "
         + "SignedHeaders=host;x-amz-content-sha256;x-amz-date, "
         + "Signature=5634847b3ad6a857887ab0ccff2fcaf3d35ef3dc549a3c27ebc0f584a80494c3";

   private static final String GET_OBJECT_RESULT = "AWS4-HMAC-SHA256 "
         + "Credential=AKIAPAEBI3QI4EXAMPLE/20150203/cn-north-1/s3/aws4_request, "
         + "SignedHeaders=host;x-amz-content-sha256;x-amz-date, "
         + "Signature=fbd1d0f04a72907fb20ecd771644afd62cb689f91d26e9471b7a234531ec4718";

   private static final String GET_OBJECT_ACL_RESULT = "AWS4-HMAC-SHA256 "
         + "Credential=AKIAPAEBI3QI4EXAMPLE/20150203/cn-north-1/s3/aws4_request, "
         + "SignedHeaders=host;x-amz-content-sha256;x-amz-date, "
         + "Signature=52d7f31d249032b59781fe69c8124ff4bf209be3f374b28657a60d906c752381";

   private static final String PUT_OBJECT_CONTENT = "text sign";

   private static final String PUT_OBJECT_RESULT = "AWS4-HMAC-SHA256 "
         + "Credential=AKIAPAEBI3QI4EXAMPLE/20150203/cn-north-1/s3/aws4_request, "
         + "SignedHeaders=content-length;content-type;host;x-amz-content-sha256;x-amz-date;x-amz-storage-class, "
         + "Signature=090f1bb1db984221ae1a20c5d12a82820a0d74b4be85f20daa1431604f41df08";

   private static final String BUCKET_NAME = "test-bucket";
   private static final String OBJECT_NAME = "ExampleObject.txt";

   @ConfiguresHttpApi
   private static final class TestS3HttpApiModule extends S3HttpApiModule<S3Client> {
      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return TIMESTAMP;
      }

      @Override
      protected Supplier<Date> provideTimeStampCacheDate(
            @Named(Constants.PROPERTY_SESSION_INTERVAL) long seconds,
            @TimeStamp final Supplier<String> timestamp,
            final DateService dateService) {
         return Suppliers.ofInstance(dateService.rfc822DateParse(TIMESTAMP));
      }
   }

   public static Injector injector(Credentials creds) {
      return ContextBuilder.newBuilder(new S3ApiMetadata())
            .credentialsSupplier(Suppliers.<Credentials>ofInstance(creds))
            .modules(ImmutableList.<Module>of(new BaseRestApiTest.MockModule(), new NullLoggingModule(),
                  new TestS3HttpApiModule()))
            .buildInjector();
   }

   public static RequestAuthorizeSignatureV4 filter(Credentials creds) {
      return injector(creds).getInstance(RequestAuthorizeSignatureV4.class);
   }

   Credentials temporaryCredentials = new Credentials.Builder()
         .identity(IDENTITY)
         .credential(CREDENTIAL)
         .build();


   @Test
   void testGetBucketLocationSignature() {
      Invocation invocation = Invocation.create(method(S3Client.class, "getBucketLocation", String.class),
            ImmutableList.<Object>of(BUCKET_NAME));

      HttpRequest getBucketLocation = GeneratedHttpRequest.builder().method("GET")
            .invocation(invocation)
            .endpoint("https://" + BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn/")
            .addHeader(HttpHeaders.HOST, BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn")
            .addQueryParam("location", "")
            .build();
      HttpRequest filtered = filter(temporaryCredentials).filter(getBucketLocation);
      assertEquals(filtered.getFirstHeaderOrNull("Authorization"), GET_BUCKET_LOCATION_SIGNATURE_RESULT);
   }

   @Test
   void testGetObjectSignature() {
      Invocation invocation = Invocation.create(method(S3Client.class, "getObject", String.class,
                  String.class, GetOptions[].class),
            ImmutableList.<Object>of(BUCKET_NAME, OBJECT_NAME, new GetOptions[0]));

      HttpRequest getObject = GeneratedHttpRequest.builder().method("GET")
            .invocation(invocation)
            .endpoint("https://" + BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn/" + OBJECT_NAME)
            .addHeader(HttpHeaders.HOST, BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn")
            .build();

      HttpRequest filtered = filter(temporaryCredentials).filter(getObject);
      assertEquals(filtered.getFirstHeaderOrNull("Authorization"), GET_OBJECT_RESULT);

   }

   @Test
   void testGetObjectACLSignature() {

      Invocation invocation = Invocation.create(method(S3Client.class, "getObjectACL", String.class, String.class),
            ImmutableList.<Object>of(BUCKET_NAME));

      HttpRequest getObjectACL = GeneratedHttpRequest.builder().method("GET")
            .invocation(invocation)
            .endpoint("https://" + BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn/" + OBJECT_NAME)
            .addHeader(HttpHeaders.HOST, BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn")
            .addQueryParam("acl", "")
            .build();

      HttpRequest filtered = filter(temporaryCredentials).filter(getObjectACL);
      assertEquals(filtered.getFirstHeaderOrNull("Authorization"), GET_OBJECT_ACL_RESULT);
   }

   @Test
   void testPutObjectSignature() {
      Invocation invocation = Invocation.create(method(S3Client.class, "putObject", String.class, S3Object.class,
                  PutObjectOptions[].class),
            ImmutableList.<Object>of(BUCKET_NAME));

      Payload payload = Payloads.newStringPayload(PUT_OBJECT_CONTENT);
      payload.getContentMetadata().setContentType("text/plain");

      HttpRequest putObject = GeneratedHttpRequest.builder().method("PUT")
            .invocation(invocation)
            .endpoint("https://" + BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn/" + OBJECT_NAME)
            .addHeader(HttpHeaders.HOST, BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn")
            .addHeader("x-amz-storage-class", "REDUCED_REDUNDANCY")
            .payload(payload)
            .build();

      HttpRequest filtered = filter(temporaryCredentials).filter(putObject);
      assertEquals(filtered.getFirstHeaderOrNull("Authorization"), PUT_OBJECT_RESULT);

   }
}
