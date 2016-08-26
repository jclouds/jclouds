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

import static org.testng.Assert.fail;

import java.io.IOException;

import org.jclouds.blobstore.integration.internal.BaseBlobSignerLiveTest;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "S3BlobSignerLiveTest")
public class S3BlobSignerLiveTest extends BaseBlobSignerLiveTest {

   public S3BlobSignerLiveTest() {
      provider = "s3";
      BaseBlobStoreIntegrationTest.SANITY_CHECK_RETURNED_BUCKET_NAME = true;
   }

   protected boolean supportsUrlWithTime() {
      return false;
   }

   @Test
   public void testSignGetUrlWithTime() throws InterruptedException, IOException {
      try {
         super.testSignGetUrlWithTime();
         if (!supportsUrlWithTime()) {
            fail();
         }
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported by S3 signer", uoe);
      }
   }

   @Test
   public void testSignGetUrlWithTimeExpired() throws InterruptedException, IOException {
      try {
         // Intentionally try with a timeout of 0. AWS signature v4 throws an error if
         // the timeout is negative.
         super.testSignGetUrlWithTime(/*timeout=*/ 0);
         if (!supportsUrlWithTime()) {
            fail();
         }
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported by S3 signer", uoe);
      }
   }

   @Test
   public void testSignPutUrlWithTime() throws Exception {
      try {
         super.testSignPutUrlWithTime();
         if (!supportsUrlWithTime()) {
            fail();
         }
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported by S3 signer", uoe);
      }
   }

   @Test
   public void testSignPutUrlWithTimeExpired() throws Exception {
      try {
         // Intentionally try with a timeout of 0. AWS signature v4 throws an error if
         // the timeout is negative.
         super.testSignPutUrlWithTime(/*timeout=*/ 0);
         if (!supportsUrlWithTime()) {
            fail();
         }
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not supported by S3 signer", uoe);
      }
   }
}
