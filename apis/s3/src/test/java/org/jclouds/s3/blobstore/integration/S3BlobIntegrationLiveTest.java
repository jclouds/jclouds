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
package org.jclouds.s3.blobstore.integration;

import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.s3.blobstore.strategy.MultipartUpload;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "S3BlobIntegrationLiveTest")
public class S3BlobIntegrationLiveTest extends BaseBlobIntegrationTest {

   public S3BlobIntegrationLiveTest() {
      provider = "s3";
      BaseBlobStoreIntegrationTest.SANITY_CHECK_RETURNED_BUCKET_NAME = true;
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty("jclouds.mpu.parts.size", String.valueOf(MultipartUpload.MIN_PART_SIZE));
      return props;
   }

   @Override
   protected long getMinimumMultipartBlobSize() {
      return MultipartUpload.MIN_PART_SIZE + 1;
   }
   
   @Override
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPutObjectStream() throws InterruptedException, IOException, ExecutionException {
      super.testPutObjectStream();
   }

   @Override
   public void testPutBlobTierArchive() throws Exception {
      try {
         super.testPutBlobTierArchive();
         failBecauseExceptionWasNotThrown(HttpResponseException.class);
      } catch (HttpResponseException hre) {
         throw new SkipException("S3 does not allow setting Glacier storage class on putBlob", hre);
      }
   }

   @Override
   public void testPutBlobTierArchiveMultipart() throws Exception {
      try {
         super.testPutBlobTierArchiveMultipart();
         failBecauseExceptionWasNotThrown(HttpResponseException.class);
      } catch (HttpResponseException hre) {
         throw new SkipException("S3 does not allow setting Glacier storage class on putBlob", hre);
      }
   }
}
