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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.net.HttpHeaders;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
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
import org.jclouds.util.Closeables2;
import org.testng.annotations.Test;

import javax.inject.Named;
import javax.xml.ws.http.HTTPException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

import static com.google.common.io.BaseEncoding.base16;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Tests behavior of {@code RequestAuthorizeSignature}
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "RequestAuthorizeSignatureV4ChunkedUploadTest")
public class RequestAuthorizeSignatureV4ChunkedUploadTest {
   private static final String CONTENT_SEED =
         "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc tortor metus, sagittis eget augue ut,\n"
               + "feugiat vehicula risus. Integer tortor mauris, vehicula nec mollis et, consectetur eget tortor. In ut\n"
               + "elit sagittis, ultrices est ut, iaculis turpis. In hac habitasse platea dictumst. Donec laoreet tellus\n"
               + "at auctor tempus. Praesent nec diam sed urna sollicitudin vehicula eget id est. Vivamus sed laoreet\n"
               + "lectus. Aliquam convallis condimentum risus, vitae porta justo venenatis vitae. Phasellus vitae nunc\n"
               + "varius, volutpat quam nec, mollis urna. Donec tempus, nisi vitae gravida facilisis, sapien sem malesuada\n"
               + "purus, id semper libero ipsum condimentum nulla. Suspendisse vel mi leo. Morbi pellentesque placerat congue.\n"
               + "Nunc sollicitudin nunc diam, nec hendrerit dui commodo sed. Duis dapibus commodo elit, id commodo erat\n"
               + "congue id. Aliquam erat volutpat.\n";

   private static final String CHUKED_UPLOAD_PAYLOAD_SHA256 = "2b6da230b03189254b2ceafe689c5298cfdd288869e80b2b9369da8f8f0a3d99";

   private static final String PUT_OBJECT_AUTHORIZATION = "AWS4-HMAC-SHA256 "
         + "Credential=AKIAPAEBI3QI4EXAMPLE/20150203/cn-north-1/s3/aws4_request, "
         + "SignedHeaders=content-encoding;content-length;content-type;host;x-amz-content-sha256;x-amz-date;x-amz-decoded-content-length;x-amz-storage-class, "
         + "Signature=3db48b3d786d599e8e785ba66030e8a9249c678a52f2432bf6fd44c97cb3145f";


   private static final String IDENTITY = "AKIAPAEBI3QI4EXAMPLE";
   private static final String CREDENTIAL = "oHkkcPcOjJnoAXpjT8GXdNeBjo6Ru7QeFExAmPlE";
   private static final String TIMESTAMP = "Thu, 03 Feb 2015 07:11:11 GMT";

   private static final String BUCKET_NAME = "test-bucket";
   private static final String OBJECT_NAME = "ExampleChunkedObject.txt";

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
   void testPutObjectWithChunkedUpload() {
      Invocation invocation = Invocation.create(
            method(S3Client.class, "putObject", String.class, S3Object.class, PutObjectOptions[].class),
            ImmutableList.<Object>of(BUCKET_NAME));
      byte[] content = make65KPayload().getBytes(Charset.forName("UTF-8"));
      HttpRequest putObject = GeneratedHttpRequest.builder().invocation(invocation)
            .method("PUT")
            .endpoint("https://" + BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn/" + OBJECT_NAME)
            .addHeader(HttpHeaders.HOST, BUCKET_NAME + ".s3.cn-north-1.amazonaws.com.cn")
            .addHeader("x-amz-storage-class", "REDUCED_REDUNDANCY")
            .build();
      Payload payload = Payloads.newInputStreamPayload(new ByteArrayInputStream(content));
      payload.getContentMetadata().setContentLength((long) content.length);
      payload.getContentMetadata().setContentType("text/plain");
      putObject.setPayload(payload);
      HttpRequest filtered = filter(temporaryCredentials).filter(putObject);
      assertEquals(filtered.getFirstHeaderOrNull("Authorization"), PUT_OBJECT_AUTHORIZATION);
      assertEquals(filtered.getPayload().getClass(), ChunkedUploadPayload.class);

      InputStream is = null;
      try {
         is = filtered.getPayload().openStream();
         assertEquals(base16().lowerCase().encode(hash(is)), CHUKED_UPLOAD_PAYLOAD_SHA256);
      } catch (IOException e) {
         fail("open stream error", e);
      } finally {
         Closeables2.closeQuietly(is);
      }
   }

   /**
    * Want sample to upload 3 chunks for our selected chunk size of 64K; one
    * full size chunk, one partial chunk and then the 0-byte terminator chunk.
    * This routine just takes 1K of seed text and turns it into a 65K-or-so
    * string for sample use.
    */
   private static String make65KPayload() {
      StringBuilder oneKSeed = new StringBuilder();
      while (oneKSeed.length() < 1024) {
         oneKSeed.append(CONTENT_SEED);
      }

      // now scale up to meet/exceed our requirement
      StringBuilder output = new StringBuilder();
      for (int i = 0; i < 66; i++) {
         output.append(oneKSeed);
      }
      return output.toString();
   }

   /**
    * hash input with sha256
    *
    * @param input
    * @return hash result
    * @throws HTTPException
    */
   private static byte[] hash(InputStream input) {
      try {
         Hasher hasher = Hashing.sha256().newHasher();
         byte[] buffer = new byte[4096];
         int r;
         while ((r = input.read(buffer)) != -1) {
            hasher.putBytes(buffer, 0, r);
         }
         return hasher.hash().asBytes();
      } catch (Exception e) {
         throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
      }
   }

}
