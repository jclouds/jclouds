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

import static org.jclouds.filesystem.util.Utils.isMacOSX;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MultipartPart;
import org.jclouds.blobstore.domain.MultipartUpload;
import org.jclouds.blobstore.domain.Tier;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.filesystem.utils.TestUtils;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;
import org.testng.SkipException;

@Test(groups = { "integration" }, singleThreaded = true, testName = "blobstore.FilesystemBlobIntegrationTest")
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
      if (!isMacOSX()) {
         super.checkContentMetadata(blob);
      }
   }

   // Mac OS X HFS+ does not support UserDefinedFileAttributeView:
   // https://bugs.openjdk.java.net/browse/JDK-8030048
   @Override
   protected void checkContentDisposition(Blob blob, String contentDisposition) {
      if (!isMacOSX()) {
         super.checkContentDisposition(blob, contentDisposition);
      }
   }

   // Mac OS X HFS+ does not support UserDefinedFileAttributeView:
   // https://bugs.openjdk.java.net/browse/JDK-8030048
   @Override
   protected void validateMetadata(BlobMetadata metadata) throws IOException {
      if (!isMacOSX()) {
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

   /*
    * Java on OS X does not support extended attributes, which the filesystem
    * backend uses to implement user metadata
    */
   @Override
   protected void checkUserMetadata(Map<String, String> userMetadata1, Map<String, String> userMetadata2) {
      if (!isMacOSX()) {
         super.checkUserMetadata(userMetadata1, userMetadata2);
      }
   }
   
   @Override
   protected void testPutBlobTierHelper(Tier tier, PutOptions options) throws Exception {
      checkExtendedAttributesSupport();
      super.testPutBlobTierHelper(tier, options);
   }

   @Override
   public void testSetBlobAccess() throws Exception {
      throw new SkipException("filesystem does not support anonymous access");
   }

   @Override
   protected void checkMPUParts(Blob blob, List<MultipartPart> partsList) {
      // Mac OS X HFS+ does not support UserDefinedFileAttributeView:
      // https://bugs.openjdk.java.net/browse/JDK-8030048
      if (isMacOSX()) {
         return;
      }
      assertThat(blob.getMetadata().getETag()).endsWith(String.format("-%d\"", partsList.size()));
      Hasher eTagHasher = Hashing.md5().newHasher();
      for (MultipartPart part : partsList) {
         eTagHasher.putBytes(BaseEncoding.base16().lowerCase().decode(part.partETag()));
      }
      String expectedETag = new StringBuilder("\"")
         .append(eTagHasher.hash())
         .append("-")
         .append(partsList.size())
         .append("\"")
         .toString();
      assertThat(blob.getMetadata().getETag()).isEqualTo(expectedETag);
   }

   // Mac OS X HFS+ does not support UserDefinedFileAttributeView:
   // https://bugs.openjdk.java.net/browse/JDK-8030048
   @Test(dataProvider = "ignoreOnMacOSX", groups = { "integration", "live" })
   public void testMultipartUploadMultiplePartsKnownETag() throws Exception {
      BlobStore blobStore = view.getBlobStore();
      String container = getContainerName();
      // Pre-computed ETag returned by AWS S3 for the MPU consisting of two 5MB parts filled with 'b'
      String expectedETag = "\"84462a16f6a60478d50148808aa609c1-2\"";
      int partSize = 5 * 1024 * 1024;
      try {
         String name = "blob-name";
         BlobBuilder blobBuilder = blobStore.blobBuilder(name);
         Blob blob = blobBuilder.build();
         MultipartUpload mpu = blobStore.initiateMultipartUpload(container, blob.getMetadata(), new PutOptions());

         byte[] content = new byte[partSize];
         Arrays.fill(content, (byte) 'b');
         Payload payload = Payloads.newByteArrayPayload(content);

         payload.getContentMetadata().setContentLength((long) partSize);

         MultipartPart part1 = blobStore.uploadMultipartPart(mpu, 1, payload);
         MultipartPart part2 = blobStore.uploadMultipartPart(mpu, 2, payload);
         blobStore.completeMultipartUpload(mpu, ImmutableList.of(part1, part2));

         BlobMetadata newBlobMetadata = blobStore.blobMetadata(container, name);
         assertThat(newBlobMetadata.getETag()).isEqualTo(expectedETag);
      } finally {
         returnContainer(container);
      }
   }

   @Test(groups = { "integration", "live" })
   public void test10000PartMultipartUpload() throws Exception {
      BlobStore blobStore = view.getBlobStore();
      String container = getContainerName();
      int partSize = (int) blobStore.getMinimumMultipartPartSize();
      try {
         String name = "blob-name";
         BlobBuilder blobBuilder = blobStore.blobBuilder(name);
         Blob blob = blobBuilder.build();
         MultipartUpload mpu = blobStore.initiateMultipartUpload(container, blob.getMetadata(), new PutOptions());
         ImmutableList.Builder<MultipartPart> parts = ImmutableList.builder();
         byte[] content = new byte[partSize];

         for (int i = 0; i < 10 * 1000; ++i) {
            Payload payload = Payloads.newByteArrayPayload(content);
            payload.getContentMetadata().setContentLength((long) partSize);
            parts.add(blobStore.uploadMultipartPart(mpu, i, payload));
         }

         blobStore.completeMultipartUpload(mpu, parts.build());

         BlobMetadata newBlobMetadata = blobStore.blobMetadata(container, name);
         assertThat(newBlobMetadata.getSize()).isEqualTo(10 * 1000 * partSize);
      } finally {
         returnContainer(container);
      }
   }

   protected void checkExtendedAttributesSupport() {
      if (isMacOSX()) {
         throw new SkipException("filesystem does not support extended attributes in Mac OSX");
      }
   }
}
