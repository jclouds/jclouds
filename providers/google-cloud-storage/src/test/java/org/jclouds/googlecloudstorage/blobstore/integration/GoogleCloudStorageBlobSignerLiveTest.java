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
package org.jclouds.googlecloudstorage.blobstore.integration;

import java.io.IOException;

import org.jclouds.blobstore.integration.internal.BaseBlobSignerLiveTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test(groups = { "live" })
public class GoogleCloudStorageBlobSignerLiveTest extends BaseBlobSignerLiveTest {
   public GoogleCloudStorageBlobSignerLiveTest() {
      provider = "google-cloud-storage";
   }

   @Test
   public void testSignGetUrl() throws Exception {
      try {
         super.testSignGetUrl();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not yet implemented in GCS", uoe);
      }
   }

   @Test
   public void testSignGetUrlOptions() throws Exception {
      try {
         super.testSignGetUrlOptions();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not yet implemented in GCS", uoe);
      }
   }

   @Test
   public void testSignGetUrlWithTime() throws InterruptedException, IOException {
      try {
         super.testSignGetUrlWithTime();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not yet implemented in GCS", uoe);
      }
   }

   @Test
   public void testSignGetUrlWithTimeExpired() throws InterruptedException, IOException {
      try {
         super.testSignGetUrlWithTimeExpired();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not yet implemented in GCS", uoe);
      }
   }

   @Test
   public void testSignPutUrl() throws Exception {
      try {
         super.testSignPutUrl();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not yet implemented in GCS", uoe);
      }
   }

   @Test
   public void testSignPutUrlWithTime() throws Exception {
      try {
         super.testSignPutUrlWithTime();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not yet implemented in GCS", uoe);
      }
   }

   @Test
   public void testSignPutUrlWithTimeExpired() throws Exception {
      try {
         super.testSignPutUrlWithTimeExpired();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not yet implemented in GCS", uoe);
      }
   }

   @Test
   public void testSignRemoveUrl() throws Exception {
      try {
         super.testSignRemoveUrl();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("not yet implemented in GCS", uoe);
      }
   }
}
