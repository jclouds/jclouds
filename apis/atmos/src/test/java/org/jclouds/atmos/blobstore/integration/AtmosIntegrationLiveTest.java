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
package org.jclouds.atmos.blobstore.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.Tier;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.io.ByteSource;

@Test(groups = { "integration", "live" })
public class AtmosIntegrationLiveTest extends BaseBlobIntegrationTest {
   public AtmosIntegrationLiveTest() {
      provider = "atmos";
   }
   @DataProvider(name = "delete")
   // no unicode support
   @Override
   public Object[][] createData() {
      return new Object[][] { { "normal" } };
   }

   @Override
   public void testGetTwoRanges() {
      throw new SkipException("Atmos MIME-encodes multiple ranges");
   }

   // not supported
   @Override
   protected void checkCacheControl(Blob blob, String cacheControl) {
      assertThat(blob.getPayload().getContentMetadata().getCacheControl()).isNull();
      assertThat(blob.getMetadata().getContentMetadata().getCacheControl()).isNull();
   }

   // not supported
   @Override
   protected void checkContentDisposition(Blob blob, String contentDisposition) {
      assert blob.getPayload().getContentMetadata().getContentDisposition() == null;
      assert blob.getMetadata().getContentMetadata().getContentDisposition() == null;
   }

   // not supported
   @Override
   protected void checkContentEncoding(Blob blob, String contentEncoding) {
      assert blob.getPayload().getContentMetadata().getContentEncoding() == null;
      assert blob.getMetadata().getContentMetadata().getContentEncoding() == null;
   }

   // not supported
   @Override
   protected void checkContentLanguage(Blob blob, String contentLanguage) {
      assert blob.getPayload().getContentMetadata().getContentLanguage() == null;
      assert blob.getMetadata().getContentMetadata().getContentLanguage() == null;
   }

   @Override
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPutObjectStream() throws InterruptedException, IOException, ExecutionException {
      super.testPutObjectStream();
   }

   @Override
   public void testCreateBlobWithExpiry() throws InterruptedException {
      throw new SkipException("Expiration not yet implemented");
   }

   @Override
   public void testMultipartUploadNoPartsAbort() throws Exception {
      throw new SkipException("Atmos does not support multipart uploads");
   }

   @Override
   public void testMultipartUploadSinglePart() throws Exception {
      throw new SkipException("Atmos does not support multipart uploads");
   }

   @Override
   public void testMultipartUploadMultipleParts() throws Exception {
      throw new SkipException("Atmos does not support multipart uploads");
   }

   @Override
   public void testListMultipartUploads() throws Exception {
      try {
         super.testListMultipartUploads();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("Atmos does not support multipart uploads", uoe);
      }
   }

   @Override
   public void testPutMultipartByteSource() throws Exception {
      throw new SkipException("Atmos does not support multipart uploads");
   }

   @Override
   public void testPutMultipartInputStream() throws Exception {
      throw new SkipException("Atmos does not support multipart uploads");
   }

   @Override
   @Test(groups = { "integration", "live" }, expectedExceptions = UnsupportedOperationException.class)
   public void testPutBlobAccessMultipart() throws Exception {
      super.testPutBlobAccessMultipart();
   }

   @Override
   @Test(groups = { "integration", "live" }, expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfMatch() throws Exception {
      super.testCopyIfMatch();
   }

   @Override
   @Test(groups = { "integration", "live" }, expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfMatchNegative() throws Exception {
      super.testCopyIfMatchNegative();
   }

   @Override
   @Test(groups = { "integration", "live" }, expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfNoneMatch() throws Exception {
      super.testCopyIfNoneMatch();
   }

   @Override
   @Test(groups = { "integration", "live" }, expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfNoneMatchNegative() throws Exception {
      super.testCopyIfNoneMatchNegative();
   }

   @Test(groups = { "integration", "live" })
   public void testPutBlobTierStandardMultipart() throws Exception {
      try {
         super.testPutBlobTierStandardMultipart();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("Atmos does not support multipart", uoe);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutBlobTierInfrequentMultipart() throws Exception {
      try {
         super.testPutBlobTierInfrequentMultipart();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("Atmos does not support multipart", uoe);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutBlobTierArchiveMultipart() throws Exception {
      try {
         super.testPutBlobTierArchiveMultipart();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("Atmos does not support multipart", uoe);
      }
   }

   @Override
   protected void checkTier(BlobMetadata metadata, Tier expected) {
      // Atmos maps all tiers to STANDARD
      assertThat(metadata.getTier()).isEqualTo(Tier.STANDARD);
   }

   // TODO: promote test to portable abstraction?
   @Test(groups = { "integration", "live" })
   public void testETag() throws Exception {
      String blobName = "test-etag";
      ByteSource payload = ByteSource.empty();
      BlobStore blobStore = view.getBlobStore();
      String containerName = getContainerName();
      try {
         Blob blob = blobStore.blobBuilder(blobName)
            .payload(payload)
            .contentLength(payload.size())
            .build();
         String eTag = blobStore.putBlob(containerName, blob);
         assertThat(eTag).hasSize(44);

         BlobMetadata metadata = blobStore.blobMetadata(containerName, blobName);
         assertThat(metadata.getETag()).isEqualTo(eTag);

         for (StorageMetadata sm : blobStore.list(containerName, ListContainerOptions.NONE)) {
            assertThat(sm.getETag()).isEqualTo(eTag);
         }
      } finally {
         returnContainer(containerName);
      }
   }
}
