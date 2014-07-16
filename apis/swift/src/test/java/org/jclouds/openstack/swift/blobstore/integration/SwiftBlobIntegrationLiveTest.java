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
package org.jclouds.openstack.swift.blobstore.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.io.ByteSources;
import org.jclouds.io.ByteStreams2;
import org.jclouds.io.Payload;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.swift.blobstore.strategy.MultipartUpload;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

@Test(groups = "live")
public class SwiftBlobIntegrationLiveTest extends BaseBlobIntegrationTest {
   /**
    * Use the minimum part size to minimise the file size that we have to
    * upload to get a multipart blob thereby make the test run faster
    */
   private static final long PART_SIZE = MultipartUpload.MIN_PART_SIZE;

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      props.setProperty("jclouds.mpu.parts.size", String.valueOf(PART_SIZE));
      return props;
   }
   
   private ByteSource oneHundredOneConstitutions;

   public SwiftBlobIntegrationLiveTest() {
      provider = System.getProperty("test.swift.provider", "swift");
   }

   @Override
   @Test(enabled = false)
   public void testGetTwoRanges() {
      // not supported in swift
   }

   @Override
   @Test
   public void testCreateBlobWithExpiry() throws InterruptedException {
      throw new SkipException("not yet implemented");
   }

   @BeforeClass(groups = {"integration", "live"}, dependsOnMethods = "setupContext")
   @Override
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      super.setUpResourcesOnThisThread(testContext);
      oneHundredOneConstitutions = getTestDataSupplier();
   }

   @Override
   protected void checkContentDisposition(Blob blob, String contentDisposition) {
      // This works for Swift Server 1.4.4/SWauth 1.0.3 but was null in previous versions.
      // TODO: Better testing for the different versions.
      super.checkContentDisposition(blob, contentDisposition);
   }

   // not supported in swift
   @Override
   protected void checkContentLanguage(Blob blob, String contentLanguage) {
      assert blob.getPayload().getContentMetadata().getContentLanguage() == null;
      assert blob.getMetadata().getContentMetadata().getContentLanguage() == null;
   }
   
   // swift doesn't support quotes
   @Override
   @DataProvider(name = "delete")
   public Object[][] createData() {
      return new Object[][] { { "normal" }, { "sp ace" }, { "qu?stion" }, { "unic₪de" }, { "path/foo" }, { "colon:" },
               { "asteri*k" }, { "{great<r}" }, { "lesst>en" }, { "p|pe" } };
   }
    
   @Test(groups = { "integration", "live" })
   public void testMultipartChunkedFileStream() throws IOException, InterruptedException {
      String containerName = getContainerName();
      try {
         BlobStore blobStore = view.getBlobStore();
         long countBefore = blobStore.countBlobs(containerName);

         addMultipartBlobToContainer(containerName, "const.txt");

         long countAfter = blobStore.countBlobs(containerName);
         assertNotEquals(countBefore, countAfter,
                         "No blob was created");
         assertTrue(countAfter - countBefore > 1,
                    "A multipart blob wasn't actually created - " +
                    "there was only 1 extra blob but there should be one manifest blob and multiple chunk blobs");
      } finally {
         returnContainer(containerName);
      }
   }

   /**
    * Checks that when there are more than 9 chunks the object names
    * are set correctly so that the order of the object names matches
    * the upload order.
    * See issue https://issues.apache.org/jira/browse/JCLOUDS-619
    */
   @Test(groups = {"integration", "live"})
   public void testMultipartChunkedFilenames() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         BlobStore blobStore = view.getBlobStore();
         String objectName = "object.txt";
         long countBefore = blobStore.countBlobs(containerName);

         // we want 2 parts
         ByteSource inputSource = createByteSource(PART_SIZE + 1);
         addMultipartBlobToContainer(containerName, objectName, inputSource);

         // did we create enough parts?
         long countAfter = blobStore.countBlobs(containerName);
         assertNotEquals(countAfter, countBefore, "No blob was created");
         assertEquals(countAfter, countBefore + 3,
                 "3 parts (2 objects + 1 manifest) were expected.");

         // download and check if correct
         Blob read = blobStore.getBlob(containerName, objectName);
         Payload readPayload = read.getPayload();
         assertTrue(inputSource.contentEquals(ByteSources.asByteSource(readPayload.openStream())));
      } finally {
         returnContainer(containerName);
      }
   }

   // InputStreamPayloads are handled differently than File; Test InputStreams too
   @Test(groups = { "integration", "live" })
   public void testMultipartChunkedInputStream() throws InterruptedException, IOException {
      String container = getContainerName();
      try {
         BlobStore blobStore = view.getBlobStore();

         blobStore.createContainerInLocation(null, container);

         ByteSource input = createByteSourceBiggerThan(PART_SIZE);

         Blob write = blobStore.blobBuilder("const.txt")
             .payload(input.openStream())
             .contentLength(input.size())
             .build();
         blobStore.putBlob(container, write, PutOptions.Builder.multipart());

         Blob read = blobStore.getBlob(container, "const.txt");
         InputStream is = read.getPayload().openStream();
         assertEquals(ByteStreams2.hashAndClose(is, Hashing.md5()), input.hash(Hashing.md5()));
      } finally {
         returnContainer(container);
      }
   }

   @Override
   protected int getIncorrectContentMD5StatusCode() {
      return 422;
   }

   protected void addMultipartBlobToContainer(String containerName, String key) throws IOException {
      ByteSource byteSource = createByteSourceBiggerThan(PART_SIZE);
      addMultipartBlobToContainer(containerName, key, byteSource);
   }

   protected void addMultipartBlobToContainer(String containerName, String key, ByteSource byteSource) throws IOException {
      BlobStore blobStore = view.getBlobStore();
      blobStore.createContainerInLocation(null, containerName);
      Blob blob = blobStore.blobBuilder(key)
              .payload(byteSource)
              .contentLength(byteSource.size())
              .build();
      blobStore.putBlob(containerName, blob, PutOptions.Builder.multipart());
   }

   private ByteSource createByteSource(long size) throws IOException {
      final Random random = new Random();
      final byte[] randomBytes = new byte[(int) MultipartUpload.MIN_PART_SIZE];
      random.nextBytes(randomBytes);
      ByteSource byteSource = ByteSources.repeatingArrayByteSource(randomBytes).slice(0, size);
      assertEquals(byteSource.size(), size);
      return byteSource;
   }

   private ByteSource createByteSourceBiggerThan(long partSize) throws IOException {
      int nCopies = (int) (partSize / getOneHundredOneConstitutionsLength()) + 1;
      return ByteSource.concat(Collections.nCopies(nCopies, oneHundredOneConstitutions));
   }
}
