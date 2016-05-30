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
import static org.jclouds.b2.features.B2TestUtils.api;
import static org.jclouds.b2.features.B2TestUtils.assertAuthentication;
import static org.jclouds.b2.features.B2TestUtils.assertRequest;
import static org.jclouds.b2.features.B2TestUtils.createMockWebServer;
import static org.jclouds.b2.features.B2TestUtils.stringFromResource;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.b2.domain.Action;
import org.jclouds.b2.domain.B2Object;
import org.jclouds.b2.domain.GetUploadPartResponse;
import org.jclouds.b2.domain.ListPartsResponse;
import org.jclouds.b2.domain.ListUnfinishedLargeFilesResponse;
import org.jclouds.b2.domain.MultipartUploadResponse;
import org.jclouds.b2.domain.UploadPartResponse;
import org.jclouds.utils.TestUtils;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "MultipartApiMockTest")
public final class MultipartApiMockTest {
   private static final String ACCOUNT_ID = "YOUR_ACCOUNT_ID";
   private static final String AUTHORIZATION_TOKEN = "3_20160409004829_42b8f80ba60fb4323dcaad98_ec81302316fccc2260201cbf17813247f312cf3b_000_uplg";
   private static final String BUCKET_NAME = "BUCKET_NAME";
   private static final String BUCKET_ID = "e73ede9c9c8412db49f60715";
   private static final String CONTENT_TYPE = "b2/x-auto";
   private static final String FILE_ID = "4_za71f544e781e6891531b001a_f200ec353a2184825_d20160409_m004829_c000_v0001016_t0028";
   private static final Map<String, String> FILE_INFO = ImmutableMap.of("author", "unknown");
   private static final String FILE_NAME = "bigfile.dat";
   private static final String SHA1 = "062685a84ab248d2488f02f6b01b948de2514ad8";
   private static final Date UPLOAD_TIMESTAMP = new Date(1460162909000L);

   public void testStartLargeFile() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/start_large_file_response.json")));

      try {
         MultipartApi api = api(server.getUrl("/").toString(), "b2").getMultipartApi();
         MultipartUploadResponse response = api.startLargeFile(BUCKET_ID, FILE_NAME, CONTENT_TYPE, FILE_INFO);
         assertThat(response.accountId()).isEqualTo(ACCOUNT_ID);
         assertThat(response.bucketId()).isEqualTo(BUCKET_ID);
         assertThat(response.contentType()).isEqualTo(CONTENT_TYPE);
         assertThat(response.fileId()).isEqualTo(FILE_ID);
         assertThat(response.fileInfo()).isEqualTo(FILE_INFO);
         assertThat(response.fileName()).isEqualTo(FILE_NAME);
         assertThat(response.uploadTimestamp()).isEqualTo(UPLOAD_TIMESTAMP);

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_start_large_file", "/start_large_file_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testCancelLargeFile() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/cancel_large_file_response.json")));

      try {
         MultipartApi api = api(server.getUrl("/").toString(), "b2").getMultipartApi();
         B2Object response = api.cancelLargeFile(FILE_ID);
         assertThat(response.accountId()).isEqualTo(ACCOUNT_ID);
         assertThat(response.bucketId()).isEqualTo(BUCKET_ID);
         assertThat(response.fileId()).isEqualTo(FILE_ID);
         assertThat(response.fileName()).isEqualTo(FILE_NAME);

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_cancel_large_file", "/cancel_large_file_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testFinishLargeFile() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/finish_large_file_response.json")));
      Collection<String> sha1 = ImmutableList.of(
            "0000000000000000000000000000000000000000",
            "ffffffffffffffffffffffffffffffffffffffff");

      try {
         MultipartApi api = api(server.getUrl("/").toString(), "b2").getMultipartApi();
         B2Object response = api.finishLargeFile(FILE_ID, sha1);
         assertThat(response.accountId()).isEqualTo(ACCOUNT_ID);
         assertThat(response.action()).isEqualTo(Action.UPLOAD);
         assertThat(response.bucketId()).isEqualTo(BUCKET_ID);
         assertThat(response.contentLength()).isEqualTo(208158542);
         assertThat(response.contentSha1()).isNull();
         assertThat(response.contentType()).isEqualTo(CONTENT_TYPE);
         assertThat(response.fileId()).isEqualTo(FILE_ID);
         assertThat(response.fileInfo()).isEqualTo(FILE_INFO);
         assertThat(response.fileName()).isEqualTo(FILE_NAME);
         assertThat(response.uploadTimestamp()).isEqualTo(UPLOAD_TIMESTAMP);

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_finish_large_file", "/finish_large_file_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testGetUploadPartUrl() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/get_upload_part_url_response.json")));

      try {
         MultipartApi api = api(server.getUrl("/").toString(), "b2").getMultipartApi();
         GetUploadPartResponse response = api.getUploadPartUrl(FILE_ID);
         assertThat(response.authorizationToken()).isEqualTo(AUTHORIZATION_TOKEN);
         assertThat(response.fileId()).isEqualTo(FILE_ID);
         assertThat(response.uploadUrl()).isEqualTo(URI.create("https://pod-000-1016-09.backblaze.com/b2api/v1/b2_upload_part/4_ze73ede9c9c8412db49f60715_f100b4e93fbae6252_d20150824_m224353_c900_v8881000_t0001/0037"));

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_get_upload_part_url", "/get_upload_part_url_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testUploadPart() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/upload_part_response.json")));

      try {
         MultipartApi api = api(server.getUrl("/").toString(), "b2").getMultipartApi();
         GetUploadPartResponse uploadPart = GetUploadPartResponse.create(FILE_ID, server.getUrl("/b2api/v1/b2_upload_part/4a48fe8875c6214145260818/c001_v0001007_t0042").toURI(), AUTHORIZATION_TOKEN);
         long contentLength = 100 * 1000 * 1000;
         Payload payload = Payloads.newByteSourcePayload(TestUtils.randomByteSource().slice(0, contentLength));
         payload.getContentMetadata().setContentLength(contentLength);
         UploadPartResponse response = api.uploadPart(uploadPart, 1, SHA1, payload);
         assertThat(response.contentLength()).isEqualTo(contentLength);
         assertThat(response.contentSha1()).isEqualTo(SHA1);
         assertThat(response.fileId()).isEqualTo(FILE_ID);
         assertThat(response.partNumber()).isEqualTo(1);

         assertThat(server.getRequestCount()).isEqualTo(1);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_upload_part/4a48fe8875c6214145260818/c001_v0001007_t0042");
      } finally {
         server.shutdown();
      }
   }

   public void testListParts() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/list_parts_response.json")));

      try {
         MultipartApi api = api(server.getUrl("/").toString(), "b2").getMultipartApi();
         ListPartsResponse response = api.listParts(FILE_ID, 1, 1000);
         assertThat(response.nextPartNumber()).isNull();
         assertThat(response.parts()).hasSize(3);

         ListPartsResponse.Entry entry = response.parts().get(0);
         assertThat(entry.contentLength()).isEqualTo(100000000);
         assertThat(entry.contentSha1()).isEqualTo("062685a84ab248d2488f02f6b01b948de2514ad8");
         assertThat(entry.fileId()).isEqualTo("4_ze73ede9c9c8412db49f60715_f100b4e93fbae6252_d20150824_m224353_c900_v8881000_t0001");
         assertThat(entry.partNumber()).isEqualTo(1);
         assertThat(entry.uploadTimestamp()).isEqualTo(new Date(1462212185000L));

         entry = response.parts().get(1);
         assertThat(entry.contentLength()).isEqualTo(100000000);
         assertThat(entry.contentSha1()).isEqualTo("cf634751c3d9f6a15344f23cbf13f3fc9542addf");
         assertThat(entry.fileId()).isEqualTo("4_ze73ede9c9c8412db49f60715_f100b4e93fbae6252_d20150824_m224353_c900_v8881000_t0001");
         assertThat(entry.partNumber()).isEqualTo(2);
         assertThat(entry.uploadTimestamp()).isEqualTo(new Date(1462212296000L));

         entry = response.parts().get(2);
         assertThat(entry.contentLength()).isEqualTo(8158554);
         assertThat(entry.contentSha1()).isEqualTo("00ad164147cbbd60aedb2b04ff66b0f74f962753");
         assertThat(entry.fileId()).isEqualTo("4_ze73ede9c9c8412db49f60715_f100b4e93fbae6252_d20150824_m224353_c900_v8881000_t0001");
         assertThat(entry.partNumber()).isEqualTo(3);
         assertThat(entry.uploadTimestamp()).isEqualTo(new Date(1462212327000L));

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_list_parts", "/list_parts_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testListUnfinishedLargeFiles() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/list_unfinished_large_files_response.json")));

      try {
         MultipartApi api = api(server.getUrl("/").toString(), "b2").getMultipartApi();
         ListUnfinishedLargeFilesResponse response = api.listUnfinishedLargeFiles(BUCKET_ID, FILE_ID, 1000);
         assertThat(response.nextFileId()).isNull();
         assertThat(response.files()).hasSize(1);

         ListUnfinishedLargeFilesResponse.Entry entry = response.files().get(0);
         assertThat(entry.accountId()).isEqualTo(ACCOUNT_ID);
         assertThat(entry.bucketId()).isEqualTo(BUCKET_ID);
         assertThat(entry.contentType()).isEqualTo("application/octet-stream");
         assertThat(entry.fileId()).isEqualTo("4_ze73ede9c9c8412db49f60715_f100b4e93fbae6252_d20150824_m224353_c900_v8881000_t0001");
         assertThat(entry.fileInfo()).isEqualTo(FILE_INFO);
         assertThat(entry.fileName()).isEqualTo(FILE_NAME);
         assertThat(entry.uploadTimestamp()).isEqualTo(new Date(1462212184000L));

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_list_unfinished_large_files", "/list_unfinished_large_files_request.json");
      } finally {
         server.shutdown();
      }
   }
}
