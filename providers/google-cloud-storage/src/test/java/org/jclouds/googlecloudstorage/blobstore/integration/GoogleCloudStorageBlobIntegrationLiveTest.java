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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder.PayloadBlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecloud.internal.TestProperties;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApi;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.ByteSourcePayload;
import org.jclouds.utils.TestUtils;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

@Test(groups = { "live", "blobstorelive" })
public class GoogleCloudStorageBlobIntegrationLiveTest extends BaseBlobIntegrationTest {

   private final String PROJECT_NUMBER;
   private long PART_SIZE = 5L * 1024L * 1024L;

   @Override
   protected long getMinimumMultipartBlobSize() {
      return PART_SIZE + 1;
   }

   public GoogleCloudStorageBlobIntegrationLiveTest() throws IOException {
      provider = "google-cloud-storage";
      PROJECT_NUMBER = CurrentProject.ClientEmail.toProjectNumber(System.getProperty("test.google-cloud-storage.identity"));
   }

   @Override protected Properties setupProperties() {
      TestProperties.setGoogleCredentialsFromJson(provider);
      Properties properties = super.setupProperties();
      properties.put("jclouds.mpu.parts.size", 2 * 1024 * 1024);
      return TestProperties.apply(provider, properties);
   }

   @Override
   @Test(enabled = false)
   public void testGetTwoRanges() throws SkipException {
      // not supported in GoogleCloudStorage
   }

   @Override
   @Test(enabled = false)
   public void testGetRange() throws SkipException {
      // not supported in GoogleCloudStorage
   }

   @Override
   @Test(enabled = false)
   public void testCreateBlobWithExpiry() throws SkipException {
      // not supported in object level.
   }

   @Override
   @Test(groups = { "integration", "live" }, dataProvider = "gcsPutTest")
   public void testPutObject(String name, String type, Object content, Object realObject) throws InterruptedException,
            IOException {
      PayloadBlobBuilder blobBuilder = view.getBlobStore().blobBuilder(name).payload(Payloads.newPayload(content))
               .contentType(type);
      addContentMetadata(blobBuilder);
      Blob blob = blobBuilder.build();
      blob.getPayload().setContentMetadata(blob.getMetadata().getContentMetadata());
      String container = getContainerName();

      try {
         assertNotNull(view.getBlobStore().putBlob(container, blob));
         blob = view.getBlobStore().getBlob(container, blob.getMetadata().getName());
         validateMetadata(blob.getMetadata(), container, name);
         checkContentMetadata(blob);

         String returnedString = getContentAsStringOrNullAndClose(blob);
         assertEquals(returnedString, realObject);
         PageSet<? extends StorageMetadata> set = view.getBlobStore().list(container);
         assertThat(set).isNotEmpty();
      } finally {
         returnContainer(container);
      }
   }

   @Override
   protected void addContentMetadata(PayloadBlobBuilder blobBuilder) {
      blobBuilder.contentType("text/csv");
      blobBuilder.contentDisposition("attachment; filename=photo.jpg");
      // TODO: causes failures with subsequent GET operations:
      // HTTP/1.1 failed with response: HTTP/1.1 503 Service Unavailable; content: [Service Unavailable]
      //blobBuilder.contentEncoding("gzip");
      blobBuilder.contentLanguage("en");
   }

   @Override
   protected void checkContentMetadata(Blob blob) {
      checkContentType(blob, "text/csv");
      checkContentDisposition(blob, "attachment; filename=photo.jpg");
      //checkContentEncoding(blob, "gzip");
      checkContentLanguage(blob, "en");
   }

   @DataProvider(name = "gcsPutTest")
   public Object[][] createData1() throws IOException {
      File file = new File("pom.xml");
      String realObject = Files.toString(file, Charsets.UTF_8);

      return new Object[][] { { "file.xml", "text/xml", file, realObject },
               { "string.xml", "text/xml", realObject, realObject },
               { "stringwith/slash.xml", "text/xml", realObject, realObject },
               { "bytes.xml", "application/octet-stream", realObject.getBytes(), realObject } };
   }

   // Content-Length should not be null
   @Override
   public void testPutObjectStream() throws InterruptedException, IOException, java.util.concurrent.ExecutionException {

      ByteSource byteSource = ByteSource.wrap("foo".getBytes());
      ByteSourcePayload payload = new ByteSourcePayload(byteSource);
      PayloadBlobBuilder blobBuilder = view.getBlobStore().blobBuilder("streaming").payload(payload)
               .contentLength(byteSource.read().length);
      addContentMetadata(blobBuilder);

      Blob blob = blobBuilder.build();
      String container = getContainerName();

      try {
         assertNotNull(view.getBlobStore().putBlob(container, blob));

         blob = view.getBlobStore().getBlob(container, blob.getMetadata().getName());
         String returnedString = getContentAsStringOrNullAndClose(blob);
         assertEquals(returnedString, "foo");
         validateMetadata(blob.getMetadata(), container, blob.getMetadata().getName());
         checkContentMetadata(blob);
         PageSet<? extends StorageMetadata> set = view.getBlobStore().list(container);
         assertThat(set).isNotEmpty();
      } finally {
         returnContainer(container);
      }
   };

   @Override
   public void testMetadata() throws InterruptedException, IOException {
      String name = "hello";

      HashFunction hf = Hashing.md5();
      HashCode md5 = hf.newHasher().putString(TEST_STRING, Charsets.UTF_8).hash();
      Blob blob = view.getBlobStore().blobBuilder(name).userMetadata(ImmutableMap.of("adrian", "powderpuff"))
               .payload(TEST_STRING).contentType(MediaType.TEXT_PLAIN).contentMD5(md5).build();
      String container = getContainerName();
      try {
         assertNull(view.getBlobStore().blobMetadata(container, "powderpuff"));

         addBlobToContainer(container, blob);
         Blob newObject = validateContent(container, name);

         BlobMetadata metadata = newObject.getMetadata();

         validateMetadata(metadata);
         validateMetadata(metadata, container, name);
         validateMetadata(view.getBlobStore().blobMetadata(container, name));

         blob.getMetadata().getUserMetadata().put("adrian", "wonderpuff");
         blob.getMetadata().getUserMetadata().put("adrian", "powderpuff");

         addBlobToContainer(container, blob);
         validateMetadata(view.getBlobStore().blobMetadata(container, name));

      } finally {
         returnContainer(container);
      }
   }

   @Override
   protected void checkMD5(BlobMetadata metadata) throws IOException {
      HashCode md5 = Hashing.md5().hashString(TEST_STRING, Charsets.UTF_8);
      assertEquals(metadata.getContentMetadata().getContentMD5AsHashCode(), md5);
   }

   @Test(groups = { "integration", "live" })
   public void testMultipartChunkedFileStream() throws IOException, InterruptedException {
      String containerName = getContainerName();
      try {
         BlobStore blobStore = view.getBlobStore();
         long countBefore = blobStore.countBlobs(containerName);

         addMultipartBlobToContainer(containerName, "const.txt");

         long countAfter = blobStore.countBlobs(containerName);
         assertThat(countAfter).isEqualTo(countBefore + 1);
      } finally {
         returnContainer(containerName);
      }
   }

   protected void addMultipartBlobToContainer(String containerName, String key) throws IOException {
      ByteSource sourceToUpload = TestUtils.randomByteSource().slice(0, PART_SIZE + 1);

      BlobStore blobStore = view.getBlobStore();
      blobStore.createContainerInLocation(null, containerName);
      Blob blob = blobStore.blobBuilder(key).payload(sourceToUpload).contentLength(sourceToUpload.size())
               .contentType(MediaType.TEXT_PLAIN).build();
      blobStore.putBlob(containerName, blob, PutOptions.Builder.multipart());
   }

   @DataProvider(name = "delete")
   public Object[][] createData() {
      if (System.getProperty("os.name").toLowerCase().contains("windows")) {
         return new Object[][] { { "normal" }, { "sp ace" } };
      } else {
         return new Object[][] { { "normal" }, { "sp ace" }, { "qu?stion" }, { "path/foo" }, { "colon:" },
                  { "asteri*k" }, { "quote\"" }, { "{great<r}" }, { "lesst>en" }, { "p|pe" } };
      }
   }

   // Remove "unic₪de" from DataProvider
   @Override
   @Test(groups = { "integration", "live" }, dataProvider = "delete")
   public void deleteObject(String name) throws InterruptedException {
      super.deleteObject(name);
   }

   @Override
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfMatch() throws Exception {
      super.testCopyIfMatch();
   }

   @Override
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfMatchNegative() throws Exception {
      super.testCopyIfMatch();
   }

   @Override
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfNoneMatch() throws Exception {
      super.testCopyIfNoneMatch();
   }

   @Override
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfNoneMatchNegative() throws Exception {
      super.testCopyIfNoneMatch();
   }

   @Override
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfModifiedSince() throws Exception {
      super.testCopyIfModifiedSince();
   }

   @Override
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfModifiedSinceNegative() throws Exception {
      super.testCopyIfModifiedSince();
   }

   @Override
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfUnmodifiedSince() throws Exception {
      super.testCopyIfUnmodifiedSince();
   }

   @Override
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCopyIfUnmodifiedSinceNegative() throws Exception {
      super.testCopyIfUnmodifiedSince();
   }

   @Override
   public void testListMultipartUploads() throws Exception {
      try {
         super.testListMultipartUploads();
      } catch (UnsupportedOperationException uoe) {
         throw new SkipException("GCS does not support listing multipart uploads", uoe);
      }
   }

   @Override
   public void testPutBlobTierArchiveMultipart() throws Exception {
      try {
         super.testPutBlobTierArchiveMultipart();
         failBecauseExceptionWasNotThrown(AssertionError.class);
      } catch (AssertionError ae) {
         throw new SkipException("GCS does not report storage class of composed objects", ae);
      }
   }

   @Override
   public void testPutBlobTierInfrequentMultipart() throws Exception {
      try {
         super.testPutBlobTierInfrequentMultipart();
         failBecauseExceptionWasNotThrown(AssertionError.class);
      } catch (AssertionError ae) {
         throw new SkipException("GCS does not report storage class of composed objects", ae);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testMultipartUploadSinglePartMultiRegional() throws Exception {
      String containerName = "jclouds-multiregional";
      BucketTemplate template = new BucketTemplate()
            .name(containerName)
            .location(Location.US)
            .storageClass(StorageClass.COLDLINE);
      GoogleCloudStorageApi api = view.unwrapApi(GoogleCloudStorageApi.class);
      api.getBucketApi().createBucket(PROJECT_NUMBER, template);
      String blobName = "multiregional-blobname";

      try {
         long length = 32 * 1024L;
         ByteSource byteSource = TestUtils.randomByteSource().slice(0, length);
         Blob blob = view.getBlobStore().blobBuilder(blobName)
                 .payload(byteSource)
                 .contentLength(length)
                 .build();
         view.getBlobStore().putBlob(containerName, blob, new PutOptions().multipart(true));
      } finally {
         view.getBlobStore().removeBlob(containerName, blobName);
         api.getBucketApi().deleteBucket(containerName);
      }
   }
}
