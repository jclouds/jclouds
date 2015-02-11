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

import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.Properties;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.s3.AWSS3ApiMetadata;
import org.jclouds.aws.s3.AWSS3ProviderMetadata;
import org.jclouds.aws.s3.blobstore.config.AWSS3BlobStoreContextModule;
import org.jclouds.aws.s3.config.AWSS3HttpApiModule;
import org.jclouds.aws.s3.filters.AWSRequestAuthorizeSignatureV4;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.s3.blobstore.S3BlobSignerExpectTest;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import com.google.inject.Module;
import com.google.inject.Scopes;

@Test(groups = "unit", testName = "AWSS3BlobSignerV4ExpectTest")
public class AWSS3BlobSignerV4ExpectTest extends S3BlobSignerExpectTest {
   private static final String IDENTITY = "AKIAPAEBI3QI4EXAMPLE";
   private static final String CREDENTIAL = "oHkkcPcOjJnoAXpjT8GXdNeBjo6Ru7QeFExAmPlE";
   private static final String TIMESTAMP = "Thu, 03 Feb 2015 07:11:11 GMT";

   private static final String BUCKET_NAME = "test-bucket";
   private static final String OBJECT_NAME = "ExampleObject.txt";
   private static final String HOST = BUCKET_NAME + ".s3.amazonaws.com";

   public AWSS3BlobSignerV4ExpectTest() {
      provider = null;
   }

   @Override
   protected HttpRequest getBlobWithTime() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://" + HOST + "/" + OBJECT_NAME
                  + "?X-Amz-Algorithm=AWS4-HMAC-SHA256"
                  + "&X-Amz-Credential=AKIAPAEBI3QI4EXAMPLE/20150203/us-east-1/s3/aws4_request"
                  + "&X-Amz-Date=20150203T071111Z"
                  + "&X-Amz-Expires=86400"
                  + "&X-Amz-SignedHeaders=host"
                  + "&X-Amz-Signature=0bafb6a0d99c8b7c39abe5496e9897e8c442b09278f1a647267acb25e8d1c550")
            .addHeader(HttpHeaders.HOST, HOST)
            .build();
   }

   @Test
   @Override
   public void testSignGetBlobWithTime() {
      BlobStore getBlobWithTime = requestsSendResponses(init());
      HttpRequest compare = getBlobWithTime();
      HttpRequest signedRequest = getBlobWithTime.getContext().getSigner().signGetBlob(BUCKET_NAME, OBJECT_NAME,
            86400l /* seconds */);
      assertEquals(signedRequest, compare);
   }

   protected HttpRequest _putBlobWithTime() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://" + HOST + "/" + OBJECT_NAME
                  + "?X-Amz-Algorithm=AWS4-HMAC-SHA256"
                  + "&X-Amz-Credential=AKIAPAEBI3QI4EXAMPLE/20150203/us-east-1/s3/aws4_request"
                  + "&X-Amz-Date=20150203T071111Z"
                  + "&X-Amz-Expires=86400"
                  + "&X-Amz-SignedHeaders=host"
                  + "&X-Amz-Signature=41484fb83e0c51b289907979ff96b2c743f6faf8dc70fca1c6fa78d8aeda132f")
            .addHeader(HttpHeaders.EXPECT, "100-continue")
            .addHeader(HttpHeaders.HOST, HOST)
            .build();
   }

   @Test
   @Override
   public void testSignPutBlobWithTime() throws Exception {
      BlobStore signPutBloblWithTime = requestsSendResponses(init());
      Blob blob = signPutBloblWithTime.blobBuilder(OBJECT_NAME).payload(text).contentType("text/plain").build();
      HttpRequest compare = _putBlobWithTime();
      compare.setPayload(blob.getPayload());
      HttpRequest signedRequest = signPutBloblWithTime.getContext().getSigner().signPutBlob(BUCKET_NAME, blob,
            86400l /* seconds */);
      assertEquals(signedRequest, compare);
   }

   @Override
   protected HttpRequest putBlob() {
      throw new SkipException("skip putBlob");
   }

   @Override
   public void testSignPutBlob() {
      throw new SkipException("skip testSignPutBlob");
   }

   @Override
   public void testSignGetBlob() {
      throw new SkipException("skip testSignGetBlob");
   }

   @Override
   public void testSignGetBlobWithOptions() {
      throw new SkipException("skip testSignGetBlobWithOptions");
   }

   @Override
   public void testSignRemoveBlob() {
      throw new SkipException("skip testSignRemoveBlob");
   }

   @Override
   protected Module createModule() {
      return new TestAWSS3SignerV4HttpApiModule();
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(PROPERTY_IDENTITY, IDENTITY);
      props.put(PROPERTY_CREDENTIAL, CREDENTIAL);
      return props;
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      AWSS3ApiMetadata.Builder apiBuilder = new AWSS3ApiMetadata().toBuilder();
      apiBuilder.defaultModules(ImmutableSet.<Class<? extends Module>>of(TestAWSS3SignerV4HttpApiModule.class,
            TestAWSS3BlobStoreContextModule.class));
      return new AWSS3ProviderMetadata().toBuilder().apiMetadata(apiBuilder.build()).build();
   }

   public static final class TestAWSS3BlobStoreContextModule extends AWSS3BlobStoreContextModule {

      @Override
      protected void bindRequestSigner() {
         // replace AWSS3BlobRequestSigner aws s3 with AWSS3BlobRequestSignerV4
         bind(BlobRequestSigner.class).to(AWSS3BlobRequestSignerV4.class);
      }

   }

   @ConfiguresHttpApi
   public static final class TestAWSS3SignerV4HttpApiModule extends AWSS3HttpApiModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected void bindRequestSigner() {
         bind(RequestAuthorizeSignature.class).to(AWSRequestAuthorizeSignatureV4.class).in(Scopes.SINGLETON);
      }

      @Override
      @TimeStamp
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return TIMESTAMP;
      }

      @Override
      @TimeStamp
      protected Supplier<Date> provideTimeStampCacheDate(
            @Named(Constants.PROPERTY_SESSION_INTERVAL) long seconds,
            @TimeStamp final Supplier<String> timestamp,
            final DateService dateService) {
         return Suppliers.ofInstance(dateService.rfc822DateParse(TIMESTAMP));
      }
   }
}
