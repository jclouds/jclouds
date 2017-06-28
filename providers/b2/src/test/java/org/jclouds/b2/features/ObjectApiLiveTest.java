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
package org.jclouds.b2.features;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.b2.domain.Action;
import org.jclouds.b2.domain.B2Object;
import org.jclouds.b2.domain.B2ObjectList;
import org.jclouds.b2.domain.Bucket;
import org.jclouds.b2.domain.BucketType;
import org.jclouds.b2.domain.HideFileResponse;
import org.jclouds.b2.domain.UploadFileResponse;
import org.jclouds.b2.domain.UploadUrlResponse;
import org.jclouds.b2.internal.BaseB2ApiLiveTest;
import org.jclouds.util.Closeables2;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

public final class ObjectApiLiveTest extends BaseB2ApiLiveTest {
   private static final Random random = new Random();

   @Test(groups = "live")
   public void testGetFileInfo() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      ObjectApi objectApi = api.getObjectApi();

      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);
      Payload payload = Payloads.newByteSourcePayload(byteSource);
      payload.getContentMetadata().setContentLength(byteSource.size());
      String fileName = "file-name";
      String contentSha1 = byteSource.hash(Hashing.sha1()).toString();
      String contentType = "text/plain";
      payload.getContentMetadata().setContentType(contentType);
      Map<String, String> fileInfo = ImmutableMap.of("author", "unknown");

      Bucket response = bucketApi.createBucket(getBucketName(), BucketType.ALL_PRIVATE);
      UploadFileResponse uploadFile = null;
      try {
         UploadUrlResponse uploadUrl = objectApi.getUploadUrl(response.bucketId());

         uploadFile = objectApi.uploadFile(uploadUrl, fileName, contentSha1, fileInfo, payload);

         B2Object b2Object = objectApi.getFileInfo(uploadFile.fileId());
         assertThat(b2Object.fileId()).isEqualTo(uploadFile.fileId());
         assertThat(b2Object.fileName()).isEqualTo(fileName);
         assertThat(b2Object.accountId()).isEqualTo(response.accountId());
         assertThat(b2Object.bucketId()).isEqualTo(response.bucketId());
         assertThat(b2Object.contentLength()).isEqualTo(byteSource.size());
         assertThat(b2Object.contentSha1()).isEqualTo(contentSha1);
         assertThat(b2Object.contentType()).isEqualTo(contentType);
         assertThat(b2Object.fileInfo()).isEqualTo(fileInfo);
         assertThat(b2Object.action()).isEqualTo(Action.UPLOAD);
         assertThat(b2Object.uploadTimestamp()).isAfterYear(2015);
         assertThat(b2Object.payload()).isNull();
      } finally {
         if (uploadFile != null) {
            objectApi.deleteFileVersion(uploadFile.fileName(), uploadFile.fileId());
         }
         bucketApi.deleteBucket(response.bucketId());
      }
   }

   @Test(groups = "live")
   public void testDownloadFileById() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      ObjectApi objectApi = api.getObjectApi();

      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);
      Payload payload = Payloads.newByteSourcePayload(byteSource);
      payload.getContentMetadata().setContentLength(byteSource.size());
      String fileName = "file-name";
      String contentSha1 = byteSource.hash(Hashing.sha1()).toString();
      String contentType = "text/plain";
      payload.getContentMetadata().setContentType(contentType);
      Map<String, String> fileInfo = ImmutableMap.of("author", "unknown");

      Bucket response = bucketApi.createBucket(getBucketName(), BucketType.ALL_PRIVATE);
      UploadFileResponse uploadFile = null;
      try {
         UploadUrlResponse uploadUrl = objectApi.getUploadUrl(response.bucketId());

         uploadFile = objectApi.uploadFile(uploadUrl, fileName, contentSha1, fileInfo, payload);

         B2Object b2Object = objectApi.downloadFileById(uploadFile.fileId());
         payload = b2Object.payload();
         assertThat(b2Object.fileName()).isEqualTo(fileName);
         assertThat(b2Object.contentSha1()).isEqualTo(contentSha1);
         assertThat(b2Object.fileInfo()).isEqualTo(fileInfo);
         assertThat(b2Object.uploadTimestamp()).isAfterYear(2015);
         assertThat(payload.getContentMetadata().getContentType()).isEqualTo(contentType);

         InputStream actual = null;
         InputStream expected = null;
         try {
            actual = payload.openStream();
            expected = byteSource.openStream();
            assertThat(actual).hasContentEqualTo(expected);
         } finally {
            Closeables2.closeQuietly(expected);
            Closeables2.closeQuietly(actual);
         }
      } finally {
         if (uploadFile != null) {
            objectApi.deleteFileVersion(uploadFile.fileName(), uploadFile.fileId());
         }
         bucketApi.deleteBucket(response.bucketId());
      }
   }

   @Test(groups = "live")
   public void testDownloadFileByName() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      ObjectApi objectApi = api.getObjectApi();

      String bucketName = getBucketName();
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);
      Payload payload = Payloads.newByteSourcePayload(byteSource);
      payload.getContentMetadata().setContentLength(byteSource.size());
      String fileName = "file name";  // intentionally using spaces in file name
      String contentSha1 = byteSource.hash(Hashing.sha1()).toString();
      String contentType = "text/plain";
      payload.getContentMetadata().setContentType(contentType);
      Map<String, String> fileInfo = ImmutableMap.of("author", "unknown");

      Bucket response = bucketApi.createBucket(bucketName, BucketType.ALL_PRIVATE);
      UploadFileResponse uploadFile = null;
      try {
         UploadUrlResponse uploadUrl = objectApi.getUploadUrl(response.bucketId());

         uploadFile = objectApi.uploadFile(uploadUrl, fileName, contentSha1, fileInfo, payload);

         B2Object b2Object = objectApi.downloadFileByName(bucketName, fileName);
         payload = b2Object.payload();
         assertThat(b2Object.fileName()).isEqualTo(fileName);
         assertThat(b2Object.contentSha1()).isEqualTo(contentSha1);
         assertThat(b2Object.fileInfo()).isEqualTo(fileInfo);
         assertThat(b2Object.uploadTimestamp()).isAfterYear(2015);
         assertThat(payload.getContentMetadata().getContentType()).isEqualTo(contentType);

         InputStream actual = null;
         InputStream expected = null;
         try {
            actual = payload.openStream();
            expected = byteSource.openStream();
            assertThat(actual).hasContentEqualTo(expected);
         } finally {
            Closeables2.closeQuietly(expected);
            Closeables2.closeQuietly(actual);
         }
      } finally {
         if (uploadFile != null) {
            objectApi.deleteFileVersion(uploadFile.fileName(), uploadFile.fileId());
         }
         bucketApi.deleteBucket(response.bucketId());
      }
   }

   @Test(groups = "live")
   public void testListFileNames() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      ObjectApi objectApi = api.getObjectApi();

      Bucket response = bucketApi.createBucket(getBucketName(), BucketType.ALL_PRIVATE);
      int numFiles = 3;
      ImmutableList.Builder<UploadFileResponse> uploadFiles = ImmutableList.builder();
      try {
         for (int i = 0; i < numFiles; ++i) {
            uploadFiles.add(createFile(objectApi, response.bucketId(), "file" + i));
         }

         B2ObjectList list = objectApi.listFileNames(response.bucketId(), null, null, null, null);
         assertThat(list.files()).hasSize(numFiles);
      } finally {
         for (UploadFileResponse uploadFile : uploadFiles.build()) {
            objectApi.deleteFileVersion(uploadFile.fileName(), uploadFile.fileId());
         }
         bucketApi.deleteBucket(response.bucketId());
      }
   }

   @Test(groups = "live")
   public void testListFileVersions() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      ObjectApi objectApi = api.getObjectApi();

      Bucket response = bucketApi.createBucket(getBucketName(), BucketType.ALL_PRIVATE);
      int numFiles = 3;
      ImmutableList.Builder<UploadFileResponse> uploadFiles = ImmutableList.builder();
      try {
         for (int i = 0; i < numFiles; ++i) {
            uploadFiles.add(createFile(objectApi, response.bucketId(), "file"));
         }

         B2ObjectList list = objectApi.listFileNames(response.bucketId(), null, null, null, null);
         assertThat(list.files()).hasSize(1);

         list = objectApi.listFileVersions(response.bucketId(), null, null, null, null, null);
         assertThat(list.files()).hasSize(numFiles);
      } finally {
         for (UploadFileResponse uploadFile : uploadFiles.build()) {
            objectApi.deleteFileVersion(uploadFile.fileName(), uploadFile.fileId());
         }
         bucketApi.deleteBucket(response.bucketId());
      }
   }

   @Test(groups = "live")
   public void testHideFile() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      ObjectApi objectApi = api.getObjectApi();
      String fileName = "file-name";

      Bucket response = bucketApi.createBucket(getBucketName(), BucketType.ALL_PRIVATE);
      UploadFileResponse uploadFile = null;
      HideFileResponse hideFile = null;
      try {
         uploadFile = createFile(objectApi, response.bucketId(), fileName);

         B2ObjectList list = objectApi.listFileNames(response.bucketId(), null, null, null, null);
         assertThat(list.files()).hasSize(1);

         hideFile = objectApi.hideFile(response.bucketId(), fileName);

         list = objectApi.listFileNames(response.bucketId(), null, null, null, null);
         assertThat(list.files()).isEmpty();

         list = objectApi.listFileVersions(response.bucketId(), null, null, null, null, null);
         assertThat(list.files()).hasSize(2);
      } finally {
         if (hideFile != null) {
            objectApi.deleteFileVersion(hideFile.fileName(), hideFile.fileId());
         }
         if (uploadFile != null) {
            objectApi.deleteFileVersion(uploadFile.fileName(), uploadFile.fileId());
         }
         bucketApi.deleteBucket(response.bucketId());
      }
   }

   private static String getBucketName() {
      return "jcloudstestbucket-" + random.nextInt(Integer.MAX_VALUE);
   }

   private static UploadFileResponse createFile(ObjectApi objectApi, String bucketId, String fileName) throws IOException {
      ByteSource byteSource = TestUtils.randomByteSource().slice(0, 1024);
      Payload payload = Payloads.newByteSourcePayload(byteSource);
      payload.getContentMetadata().setContentLength(byteSource.size());
      String contentSha1 = byteSource.hash(Hashing.sha1()).toString();
      String contentType = "text/plain";
      payload.getContentMetadata().setContentType(contentType);
      Map<String, String> fileInfo = ImmutableMap.of("author", "unknown");

      UploadUrlResponse uploadUrl = objectApi.getUploadUrl(bucketId);

      return objectApi.uploadFile(uploadUrl, fileName, contentSha1, fileInfo, payload);
   }
}
