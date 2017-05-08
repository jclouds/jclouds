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

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;

import com.google.common.collect.ImmutableSet;

public final class B2ContainerIntegrationLiveTest extends BaseContainerIntegrationTest {
   public B2ContainerIntegrationLiveTest() {
      provider = "b2";
   }

   @Override
   public void testListMarkerAfterLastKey() throws Exception {
      try {
         super.testListMarkerAfterLastKey();
         failBecauseExceptionWasNotThrown(AssertionError.class);
      } catch (AssertionError ae) {
         throw new SkipException("B2 uses the marker as the current key, not the next key", ae);
      }
   }

   @Override
   public void testListContainerWithZeroMaxResults() throws Exception {
      try {
         super.testListContainerWithZeroMaxResults();
         failBecauseExceptionWasNotThrown(AssertionError.class);
      } catch (AssertionError ae) {
         throw new SkipException("B2 does not enforce zero max results", ae);
      }
   }

   @Override
   public void testDirectory() throws InterruptedException {
      throw new SkipException("B2 does not support directories");
   }

   @Override
   public void testSetContainerAccess() throws Exception {
      try {
         super.testSetContainerAccess();
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("Test uses blob signer which B2 does not support", uoe);
      }
   }

   @Override
   protected void checkMD5(BlobMetadata metadata) throws IOException {
      // B2 does not support Content-MD5
   }

   // B2 does not support " " file name
   @DataProvider
   @Override
   public Object[][] getBlobsToEscape() {
      ImmutableSet<String> testNames = ImmutableSet.of("%20", "%20 ", " %20");
      Object[][] result = new Object[1][1];
      result[0][0] = testNames;
      return result;
   }
}
