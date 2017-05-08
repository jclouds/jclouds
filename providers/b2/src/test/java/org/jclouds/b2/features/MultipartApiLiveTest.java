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

import java.util.Map;
import java.util.Random;

import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.b2.domain.Action;
import org.jclouds.b2.domain.B2Object;
import org.jclouds.b2.domain.Bucket;
import org.jclouds.b2.domain.BucketType;
import org.jclouds.b2.domain.GetUploadPartResponse;
import org.jclouds.b2.domain.ListPartsResponse;
import org.jclouds.b2.domain.ListUnfinishedLargeFilesResponse;
import org.jclouds.b2.domain.MultipartUploadResponse;
import org.jclouds.b2.internal.BaseB2ApiLiveTest;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

public final class MultipartApiLiveTest extends BaseB2ApiLiveTest {
   private static final Random random = new Random();

   @Test(groups = "live")
   public void testCancelMultipart() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      MultipartApi multipartApi = api.getMultipartApi();

      String fileName = "file-name";
      String contentType = "text/plain";
      Map<String, String> fileInfo = ImmutableMap.of("author", "unknown");

      Bucket bucket = bucketApi.createBucket(getBucketName(), BucketType.ALL_PRIVATE);
      try {
         MultipartUploadResponse response = multipartApi.startLargeFile(bucket.bucketId(), fileName, contentType, fileInfo);
         multipartApi.cancelLargeFile(response.fileId());
      } finally {
         bucketApi.deleteBucket(bucket.bucketId());
      }
   }

   @Test(groups = "live")
   public void testFinishMultipart() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      ObjectApi objectApi = api.getObjectApi();
      MultipartApi multipartApi = api.getMultipartApi();

      String fileName = "file-name";
      String contentType = "text/plain";
      Map<String, String> fileInfo = ImmutableMap.of("author", "unknown");

      Bucket bucket = bucketApi.createBucket(getBucketName(), BucketType.ALL_PRIVATE);
      MultipartUploadResponse response = null;
      B2Object b2Object = null;
      try {
         response = multipartApi.startLargeFile(bucket.bucketId(), fileName, contentType, fileInfo);

         ByteSource part1 = TestUtils.randomByteSource().slice(0, 100 * 1024 * 1024);
         String hash1 = part1.hash(Hashing.sha1()).toString();
         Payload payload1 = Payloads.newByteSourcePayload(part1);
         payload1.getContentMetadata().setContentLength(part1.size());
         GetUploadPartResponse uploadUrl = multipartApi.getUploadPartUrl(response.fileId());
         multipartApi.uploadPart(uploadUrl, 1, hash1, payload1);

         ByteSource part2 = TestUtils.randomByteSource().slice(0, 1);
         String hash2 = part2.hash(Hashing.sha1()).toString();
         Payload payload2 = Payloads.newByteSourcePayload(part2);
         payload2.getContentMetadata().setContentLength(part2.size());
         uploadUrl = multipartApi.getUploadPartUrl(response.fileId());
         multipartApi.uploadPart(uploadUrl, 2, hash2, payload2);

         b2Object = multipartApi.finishLargeFile(response.fileId(), ImmutableList.of(hash1, hash2));
         response = null;

         assertThat(b2Object.fileName()).isEqualTo(fileName);
         assertThat(b2Object.fileInfo()).isEqualTo(fileInfo);
         assertThat(b2Object.uploadTimestamp()).isAfterYear(2015);
         assertThat(b2Object.action()).isEqualTo(Action.UPLOAD);
         assertThat(b2Object.bucketId()).isEqualTo(bucket.bucketId());
         assertThat(b2Object.contentLength()).isEqualTo(100 * 1024 * 1024 + 1);
         assertThat(b2Object.contentType()).isEqualTo(contentType);
      } finally {
         if (b2Object != null) {
            objectApi.deleteFileVersion(fileName, b2Object.fileId());
         }
         if (response != null) {
            multipartApi.cancelLargeFile(response.fileId());
         }
         bucketApi.deleteBucket(bucket.bucketId());
      }
   }

   @Test(groups = "live")
   public void testListParts() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      ObjectApi objectApi = api.getObjectApi();
      MultipartApi multipartApi = api.getMultipartApi();

      String fileName = "file-name";
      String contentType = "text/plain";
      Map<String, String> fileInfo = ImmutableMap.of("author", "unknown");

      Bucket bucket = bucketApi.createBucket(getBucketName(), BucketType.ALL_PRIVATE);
      MultipartUploadResponse response = null;
      B2Object b2Object = null;
      try {
         response = multipartApi.startLargeFile(bucket.bucketId(), fileName, contentType, fileInfo);

         ListPartsResponse listParts = multipartApi.listParts(response.fileId(), 1, 1000);
         assertThat(listParts.parts()).hasSize(0);

         long contentLength = 1024 * 1024;
         ByteSource part = TestUtils.randomByteSource().slice(0, contentLength);
         String hash = part.hash(Hashing.sha1()).toString();
         Payload payload = Payloads.newByteSourcePayload(part);
         payload.getContentMetadata().setContentLength(contentLength);
         GetUploadPartResponse uploadUrl = multipartApi.getUploadPartUrl(response.fileId());
         multipartApi.uploadPart(uploadUrl, 1, hash, payload);

         listParts = multipartApi.listParts(response.fileId(), 1, 1000);
         assertThat(listParts.parts()).hasSize(1);

         ListPartsResponse.Entry entry = listParts.parts().get(0);
         assertThat(entry.contentLength()).isEqualTo(contentLength);
         assertThat(entry.contentSha1()).isEqualTo(hash);
         assertThat(entry.partNumber()).isEqualTo(1);
      } finally {
         if (response != null) {
            multipartApi.cancelLargeFile(response.fileId());
         }
         bucketApi.deleteBucket(bucket.bucketId());
      }
   }

   @Test(groups = "live")
   public void testListUnfinishedLargeFiles() throws Exception {
      BucketApi bucketApi = api.getBucketApi();
      ObjectApi objectApi = api.getObjectApi();
      MultipartApi multipartApi = api.getMultipartApi();

      String fileName = "file-name";
      String contentType = "text/plain";
      Map<String, String> fileInfo = ImmutableMap.of("author", "unknown");

      Bucket bucket = bucketApi.createBucket(getBucketName(), BucketType.ALL_PRIVATE);
      MultipartUploadResponse response = null;
      B2Object b2Object = null;
      try {
         ListUnfinishedLargeFilesResponse unfinishedLargeFiles = multipartApi.listUnfinishedLargeFiles(bucket.bucketId(), null, null);
         assertThat(unfinishedLargeFiles.files()).hasSize(0);

         response = multipartApi.startLargeFile(bucket.bucketId(), fileName, contentType, fileInfo);

         unfinishedLargeFiles = multipartApi.listUnfinishedLargeFiles(bucket.bucketId(), null, null);
         assertThat(unfinishedLargeFiles.files()).hasSize(1);

         ListUnfinishedLargeFilesResponse.Entry entry = unfinishedLargeFiles.files().get(0);
         assertThat(entry.contentType()).isEqualTo(contentType);
         assertThat(entry.fileInfo()).isEqualTo(fileInfo);
         assertThat(entry.fileName()).isEqualTo(fileName);
      } finally {
         if (response != null) {
            multipartApi.cancelLargeFile(response.fileId());
         }
         bucketApi.deleteBucket(bucket.bucketId());
      }
   }

   private static String getBucketName() {
      return "jcloudstestbucket-" + random.nextInt(Integer.MAX_VALUE);
   }
}
