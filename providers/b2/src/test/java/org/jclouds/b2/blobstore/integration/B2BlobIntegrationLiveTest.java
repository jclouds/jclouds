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
package org.jclouds.b2.blobstore.integration;

import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.BlobBuilder.PayloadBlobBuilder;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test(groups = { "live", "blobstorelive" })
public final class B2BlobIntegrationLiveTest extends BaseBlobIntegrationTest {
   public B2BlobIntegrationLiveTest() throws IOException {
      provider = "b2";
   }

   @Override
   protected long getMinimumMultipartBlobSize() {
      return view.getBlobStore().getMinimumMultipartPartSize() + 1;
   }

   @Override
   protected void addContentMetadata(PayloadBlobBuilder blobBuilder) {
      blobBuilder.contentType("text/csv");
      // B2 does not support the following:
      //blobBuilder.contentDisposition("attachment; filename=photo.jpg");
      //blobBuilder.contentEncoding("gzip");
      //blobBuilder.contentLanguage("en");
   }

   @Override
   protected void checkContentMetadata(Blob blob) {
      checkContentType(blob, "text/csv");
      // B2 does not support the following:
      //checkContentDisposition(blob, "attachment; filename=photo.jpg");
      //checkContentEncoding(blob, "gzip");
      //checkContentLanguage(blob, "en");
   }

   @Override
   protected void checkMD5(BlobMetadata metadata) throws IOException {
      // B2 does not support Content-MD5
   }

   @Override
   public void testCopyBlobCopyMetadata() throws Exception {
      try {
         super.testCopyBlobCopyMetadata();
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         throw new SkipException("B2 does not support the Cache-Control header", iae);
      }
   }

   @Override
   public void testCopyBlobReplaceMetadata() throws Exception {
      try {
         super.testCopyBlobReplaceMetadata();
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         throw new SkipException("B2 does not support the Cache-Control header", iae);
      }
   }

   @Override
   public void testCopyIfMatch() throws Exception {
      try {
         super.testCopyIfMatch();
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         throw new SkipException("B2 does not support the Cache-Control header", iae);
      }
   }

   @Override
   public void testCopyIfNoneMatch() throws Exception {
      try {
         super.testCopyIfNoneMatch();
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         throw new SkipException("B2 does not support the Cache-Control header", iae);
      }
   }

   @Override
   public void testCopyIfModifiedSince() throws Exception {
      try {
         super.testCopyIfModifiedSince();
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         throw new SkipException("B2 does not support the Cache-Control header", iae);
      }
   }

   @Override
   public void testCopyIfUnmodifiedSince() throws Exception {
      try {
         super.testCopyIfUnmodifiedSince();
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         throw new SkipException("B2 does not support the Cache-Control header", iae);
      }
   }

   @Override
   public void testPutObjectStream() throws InterruptedException, IOException, ExecutionException {
      try {
         super.testPutObjectStream();
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         throw new SkipException("B2 does not support the Cache-Control header", iae);
      }
   }

   @Override
   public void testPutIncorrectContentMD5() throws InterruptedException, IOException {
      try {
         super.testPutIncorrectContentMD5();
         failBecauseExceptionWasNotThrown(AssertionError.class);
      } catch (AssertionError ae) {
         throw new SkipException("B2 does not enforce Content-MD5", ae);
      }
   }

   @Override
   public void testCreateBlobWithExpiry() throws InterruptedException {
      try {
         super.testCreateBlobWithExpiry();
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         throw new SkipException("B2 does not allow Expires header", iae);
      }
   }

   @Override
   public void testSetBlobAccess() throws Exception {
      try {
         super.testSetBlobAccess();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported on B2", uoe);
      }
   }

   @Override
   public void testPutBlobAccess() throws Exception {
      try {
         super.testPutBlobAccess();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported on B2", uoe);
      }
   }

   @Override
   public void testPutBlobAccessMultipart() throws Exception {
      try {
         super.testPutBlobAccessMultipart();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported on B2", uoe);
      }
   }

   @Override
   public void testGetIfModifiedSince() throws InterruptedException {
      try {
         super.testGetIfModifiedSince();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported on B2", uoe);
      }
   }

   @Override
   public void testGetIfUnmodifiedSince() throws InterruptedException {
      try {
         super.testGetIfUnmodifiedSince();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported on B2", uoe);
      }
   }

   @Override
   public void testGetIfMatch() throws InterruptedException {
      try {
         super.testGetIfMatch();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported on B2", uoe);
      }
   }

   @Override
   public void testGetIfNoneMatch() throws InterruptedException {
      try {
         super.testGetIfNoneMatch();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported on B2", uoe);
      }
   }

   @Override
   public void testGetRangeOutOfRange() throws InterruptedException, IOException {
      try {
         super.testGetRangeOutOfRange();
         failBecauseExceptionWasNotThrown(AssertionError.class);
      } catch (AssertionError ae) {
         throw new SkipException("B2 does not error on invalid ranges", ae);
      }
   }

   @Override
   public void testMultipartUploadSinglePart() throws Exception {
      try {
         super.testMultipartUploadSinglePart();
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (IllegalArgumentException iae) {
         throw new SkipException("B2 requires at least two parts", iae);
      }
   }
}
