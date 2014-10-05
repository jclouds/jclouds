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
package org.jclouds.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import javax.inject.Provider;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.config.LocalBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.BaseRestAnnotationProcessingTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

/**
 * Tests behavior of {@code LocalBlobRequestSigner}
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "TransientBlobRequestSignerTest")
public class TransientBlobRequestSignerTest extends BaseRestAnnotationProcessingTest<LocalBlobStore> {

   private BlobRequestSigner signer;
   private Provider<BlobBuilder> blobFactory;
   private final String endpoint = new TransientApiMetadata().getDefaultEndpoint().get();
   private final String containerName = "container";
   private final String blobName = "blob";
   private final String fullUrl = String.format("%s/%s/%s", endpoint, containerName, blobName);

   public void testSignGetBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signGetBlob(containerName, blobName);

      assertRequestLineEquals(request, "GET " + fullUrl + " HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Authorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignRemoveBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signRemoveBlob(containerName, blobName);

      assertRequestLineEquals(request, "DELETE " + fullUrl + " HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Authorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignPutBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HashCode hashCode = HashCode.fromBytes(new byte[16]);
      Blob blob = blobFactory.get().name(blobName).forSigning().contentLength(2l).contentMD5(hashCode)
               .contentType("text/plain").build();

      assertEquals(blob.getPayload().getContentMetadata().getContentMD5AsHashCode(), hashCode);

      HttpRequest request = signer.signPutBlob(containerName, blob);

      assertRequestLineEquals(request, "PUT " + fullUrl + " HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n" +
               "Content-Length: 2\n" +
               "Content-MD5: AAAAAAAAAAAAAAAAAAAAAA==\n" +
               "Content-Type: text/plain\n");
      assertContentHeadersEqual(request, "text/plain", null, null, null, 2L, hashCode.asBytes(), null);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignPutBlobWithGenerate() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      ByteSource byteSource = ByteSource.wrap("foo".getBytes(Charsets.UTF_8));
      Blob blob = blobFactory.get().name(blobName)
         .payload(byteSource)
         .contentLength(byteSource.size())
         .contentMD5(byteSource.hash(Hashing.md5()).asBytes())
         .contentType("text/plain").build();
      byte[] md5 = { -84, -67, 24, -37, 76, -62, -8, 92, -19, -17, 101, 79, -52, -60, -92, -40 };
      
      assertEquals(blob.getPayload().getContentMetadata().getContentMD5(), md5);

      HttpRequest request = signer.signPutBlob(containerName, blob);

      assertRequestLineEquals(request, "PUT " + fullUrl + " HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\nContent-Length: 3\nContent-MD5: rL0Y20zC+Fzt72VPzMSk2A==\nContent-Type: text/plain\n");
      assertContentHeadersEqual(request, "text/plain", null, null, null, 3L, md5, null);

      assertEquals(request.getFilters().size(), 0);
   }

   @BeforeClass
   protected void setupFactory() throws IOException {
      super.setupFactory();
      this.blobFactory = injector.getProvider(BlobBuilder.class);
      this.signer = injector.getInstance(BlobRequestSigner.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   public ApiMetadata createApiMetadata() {
      return new TransientApiMetadata();
   }

}
