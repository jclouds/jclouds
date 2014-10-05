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
package org.jclouds.azureblob.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.jclouds.azureblob.AzureBlobClient;
import org.jclouds.azureblob.AzureBlobProviderMetadata;
import org.jclouds.azureblob.config.AzureBlobHttpApiModule;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.internal.BaseRestAnnotationProcessingTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.hash.HashCode;
import com.google.inject.Module;

/**
 * Tests behavior of {@code AzureBlobRequestSigner}
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AzureBlobRequestSignerTest")
public class AzureBlobRequestSignerTest extends BaseRestAnnotationProcessingTest<AzureBlobClient> {

   public AzureBlobRequestSignerTest() {
      // this is base64 decoded in the signer;
      credential = "aaaabbbb"; 
   }
   
   private BlobRequestSigner signer;
   private Factory blobFactory;

   public void testSignGetBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signGetBlob("container", "name");

      assertRequestLineEquals(request, "GET https://identity.blob.core.windows.net/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: SharedKeyLite identity:3rx56J9eAw/0GH2pym1hBmMGQmGju4DkVDcRsBWBtAk=\n" +
               "Date: Thu, 05 Jun 2008 16:38:19 GMT\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignRemoveBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = signer.signRemoveBlob("container", "name");

      assertRequestLineEquals(request, "DELETE https://identity.blob.core.windows.net/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: SharedKeyLite identity:EN3SE/jB9anhgqIqJMmrTCzZpuqjL41BIZnaE9PEBaU=\n" +
               "Date: Thu, 05 Jun 2008 16:38:19 GMT\n" +
               "x-ms-version: 2012-02-12\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   public void testSignPutBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HashCode hashCode = HashCode.fromBytes(new byte[16]);
      Blob blob = blobFactory.create(null);
      blob.getMetadata().setName("name");
      blob.setPayload("");
      blob.getPayload().getContentMetadata().setContentLength(2l);
      blob.getPayload().getContentMetadata().setContentMD5(hashCode);
      blob.getPayload().getContentMetadata().setContentType("text/plain");
      blob.getPayload().getContentMetadata().setExpires(new Date(1000));

      HttpRequest request = signer.signPutBlob("container", blob);

      assertRequestLineEquals(request, "PUT https://identity.blob.core.windows.net/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: SharedKeyLite identity:V0gyrdMZzwQrCvxEfq6eBe8PL233yJ91+aNcISEzjfE=\n" +
               "Date: Thu, 05 Jun 2008 16:38:19 GMT\n" +
               "Expect: 100-continue\n" +
               "x-ms-blob-type: BlockBlob\n" +
               "x-ms-version: 2012-02-12\n");
      assertContentHeadersEqual(request, "text/plain", null, null, null, 2L, hashCode.asBytes(), new Date(1000));

      assertEquals(request.getFilters().size(), 0);
   }

   @BeforeClass
   protected void setupFactory() throws IOException {
      super.setupFactory();
      this.blobFactory = injector.getInstance(Blob.Factory.class);
      this.signer = injector.getInstance(BlobRequestSigner.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   protected Module createModule() {
      return new TestAzureBlobHttpApiModule();
   }

      @ConfiguresHttpApi
   private static final class TestAzureBlobHttpApiModule extends AzureBlobHttpApiModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "Thu, 05 Jun 2008 16:38:19 GMT";
      }
   }

   @Override
   public AzureBlobProviderMetadata createProviderMetadata() {
      return new AzureBlobProviderMetadata();
   }
}
