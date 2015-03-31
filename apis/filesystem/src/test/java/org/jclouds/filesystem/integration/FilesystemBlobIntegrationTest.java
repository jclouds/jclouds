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
package org.jclouds.filesystem.integration;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.filesystem.utils.TestUtils;
import org.testng.annotations.Test;

@Test(groups = { "integration" }, singleThreaded = true,  testName = "blobstore.FilesystemBlobIntegrationTest")
public class FilesystemBlobIntegrationTest extends BaseBlobIntegrationTest {
   public FilesystemBlobIntegrationTest() {
      provider = "filesystem";
      BaseBlobStoreIntegrationTest.SANITY_CHECK_RETURNED_BUCKET_NAME = true;
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(FilesystemConstants.PROPERTY_BASEDIR, TestUtils.TARGET_BASE_DIR);
      return props;
   }

   // Mac OS X HFS+ does not support UserDefinedFileAttributeView:
   // https://bugs.openjdk.java.net/browse/JDK-8030048
   @Override
   public void checkContentMetadata(Blob blob) {
      if (!org.jclouds.utils.TestUtils.isMacOSX()) {
         super.checkContentMetadata(blob);
      }
   }

   // Mac OS X HFS+ does not support UserDefinedFileAttributeView:
   // https://bugs.openjdk.java.net/browse/JDK-8030048
   @Override
   protected void checkContentDisposition(Blob blob, String contentDisposition) {
      if (!org.jclouds.utils.TestUtils.isMacOSX()) {
         super.checkContentDisposition(blob, contentDisposition);
      }
   }

   // Mac OS X HFS+ does not support UserDefinedFileAttributeView:
   // https://bugs.openjdk.java.net/browse/JDK-8030048
   @Override
   protected void validateMetadata(BlobMetadata metadata) throws IOException {
      if (!org.jclouds.utils.TestUtils.isMacOSX()) {
         super.validateMetadata(metadata);
      }
   }

   // Mac OS X HFS+ does not support UserDefinedFileAttributeView:
   // https://bugs.openjdk.java.net/browse/JDK-8030048
   @Test(dataProvider = "ignoreOnMacOSX")
   @Override
   public void testCreateBlobWithExpiry() throws InterruptedException {
      super.testCreateBlobWithExpiry();
   }

   /* Java on OS X does not support extended attributes, which the filesystem backend
    * uses to implement user metadata */
   @Override
   protected void checkUserMetadata(Map<String, String> userMetadata1, Map<String, String> userMetadata2) {
      if (!org.jclouds.utils.TestUtils.isMacOSX()) {
         super.checkUserMetadata(userMetadata1, userMetadata2);
      }
   }
}
