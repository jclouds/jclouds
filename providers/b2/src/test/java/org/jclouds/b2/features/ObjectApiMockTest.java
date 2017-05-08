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
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.jclouds.b2.features.B2TestUtils.api;
import static org.jclouds.b2.features.B2TestUtils.assertAuthentication;
import static org.jclouds.b2.features.B2TestUtils.assertRequest;
import static org.jclouds.b2.features.B2TestUtils.createMockWebServer;
import static org.jclouds.b2.features.B2TestUtils.stringFromResource;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.b2.domain.Action;
import org.jclouds.b2.domain.B2Object;
import org.jclouds.b2.domain.B2ObjectList;
import org.jclouds.b2.domain.DeleteFileResponse;
import org.jclouds.b2.domain.HideFileResponse;
import org.jclouds.b2.domain.UploadFileResponse;
import org.jclouds.b2.domain.UploadUrlResponse;
import org.jclouds.b2.reference.B2Headers;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "ObjectApiMockTest")
public final class ObjectApiMockTest {
   private static final String BUCKET_NAME = "BUCKET_NAME";
   private static final String BUCKET_ID = "4a48fe8875c6214145260818";
   private static final String FILE_ID = "4_h4a48fe8875c6214145260818_f000000000000472a_d20140104_m032022_c001_v0000123_t0104";
   private static final String FILE_NAME = "typing_test.txt";
   private static final String CONTENT_TYPE = "text/plain";
   private static final String SHA1 = "bae5ed658ab3546aee12f23f36392f35dba1ebdd";
   private static final String PAYLOAD = "The quick brown fox jumped over the lazy dog.\n";
   private static final Map<String, String> FILE_INFO = ImmutableMap.of("author", "unknown");

   public void testGetUploadUrl() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/get_upload_url_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         UploadUrlResponse response = api.getUploadUrl(BUCKET_ID);
         assertThat(response.bucketId()).isEqualTo(BUCKET_ID);
         assertThat(response.uploadUrl()).isEqualTo(URI.create("https://pod-000-1005-03.backblaze.com/b2api/v1/b2_upload_file?cvt=c001_v0001005_t0027&bucket=4a48fe8875c6214145260818"));
         assertThat(response.authorizationToken()).isEqualTo("2_20151009170037_f504a0f39a0f4e657337e624_9754dde94359bd7b8f1445c8f4cc1a231a33f714_upld");

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_get_upload_url", "/get_upload_url_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testGetUploadUrlDeletedBucket() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setResponseCode(400).setBody(stringFromResource("/get_upload_url_deleted_bucket_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         try {
            api.getUploadUrl(BUCKET_ID);
            failBecauseExceptionWasNotThrown(ContainerNotFoundException.class);
         } catch (ContainerNotFoundException cnfe) {
            // expected
         }

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_get_upload_url", "/get_upload_url_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testUploadFile() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/upload_file_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         String accountId = "d522aa47a10f";

         UploadUrlResponse uploadUrl = UploadUrlResponse.create(BUCKET_ID, server.getUrl("/b2api/v1/b2_upload_file/4a48fe8875c6214145260818/c001_v0001007_t0042").toURI(), "FAKE-AUTHORIZATION-TOKEN");
         Payload payload = Payloads.newStringPayload(PAYLOAD);
         payload.getContentMetadata().setContentType(CONTENT_TYPE);
         UploadFileResponse response = api.uploadFile(uploadUrl, FILE_NAME, SHA1, FILE_INFO, payload);

         assertThat(response.fileId()).isEqualTo(FILE_ID);
         assertThat(response.fileName()).isEqualTo(FILE_NAME);
         assertThat(response.accountId()).isEqualTo(accountId);
         assertThat(response.bucketId()).isEqualTo(BUCKET_ID);
         assertThat(response.contentLength()).isEqualTo(PAYLOAD.length());
         assertThat(response.contentSha1()).isEqualTo(SHA1);
         assertThat(response.contentType()).isEqualTo(CONTENT_TYPE);
         assertThat(response.fileInfo()).isEqualTo(FILE_INFO);

         assertThat(server.getRequestCount()).isEqualTo(1);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_upload_file/4a48fe8875c6214145260818/c001_v0001007_t0042");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteFileVersion() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/delete_object_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         DeleteFileResponse response = api.deleteFileVersion(FILE_NAME, FILE_ID);
         assertThat(response.fileName()).isEqualTo(FILE_NAME);
         assertThat(response.fileId()).isEqualTo(FILE_ID);

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_delete_file_version", "/delete_object_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteAlreadyDeletedFileVersion() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setResponseCode(400).setBody(stringFromResource("/delete_file_version_already_deleted_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         try {
            api.deleteFileVersion(FILE_NAME, FILE_ID);
            failBecauseExceptionWasNotThrown(KeyNotFoundException.class);
         } catch (KeyNotFoundException knfe) {
            // expected
         }

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_delete_file_version", "/delete_object_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testGetFileInfo() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/get_file_info_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         B2Object b2Object = api.getFileInfo("4_ze73ede9c9c8412db49f60715_f100b4e93fbae6252_d20150824_m224353_c900_v8881000_t0001");
         assertThat(b2Object.fileId()).isEqualTo("4_ze73ede9c9c8412db49f60715_f100b4e93fbae6252_d20150824_m224353_c900_v8881000_t0001");
         assertThat(b2Object.fileName()).isEqualTo("akitty.jpg");
         assertThat(b2Object.accountId()).isEqualTo("7eecc42b9675");
         assertThat(b2Object.bucketId()).isEqualTo("e73ede9c9c8412db49f60715");
         assertThat(b2Object.contentLength()).isEqualTo(122573);
         assertThat(b2Object.contentSha1()).isEqualTo("a01a21253a07fb08a354acd30f3a6f32abb76821");
         assertThat(b2Object.contentType()).isEqualTo("image/jpeg");
         assertThat(b2Object.fileInfo()).isEqualTo(ImmutableMap.<String, String>of());
         assertThat(b2Object.action()).isEqualTo(Action.UPLOAD);
         assertThat(b2Object.uploadTimestamp()).isAfterYear(2014);
         assertThat(b2Object.payload()).isNull();

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_get_file_info", "/get_file_info_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testGetFileInfoDeletedFileVersion() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setResponseCode(404).setBody(stringFromResource("/get_file_info_deleted_file_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         B2Object b2Object = api.getFileInfo("4_ze73ede9c9c8412db49f60715_f100b4e93fbae6252_d20150824_m224353_c900_v8881000_t0001");
         assertThat(b2Object).isNull();

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_get_file_info", "/get_file_info_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testDownloadFileById() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));

      server.enqueue(new MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE)
            .addHeader(B2Headers.FILE_ID, FILE_ID)
            .addHeader(B2Headers.FILE_NAME, FILE_NAME)
            .addHeader(B2Headers.CONTENT_SHA1, SHA1)
            .addHeader(B2Headers.UPLOAD_TIMESTAMP, String.valueOf(1500000000000L))
            .addHeader(B2Headers.FILE_INFO_PREFIX + FILE_INFO.entrySet().iterator().next().getKey(), FILE_INFO.entrySet().iterator().next().getValue())
            .setBody(PAYLOAD));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();

         B2Object b2Object = api.downloadFileById(FILE_ID);

         assertThat(b2Object.fileId()).isEqualTo(FILE_ID);
         assertThat(b2Object.fileName()).isEqualTo(FILE_NAME);
         assertThat(b2Object.contentSha1()).isEqualTo(SHA1);
         assertThat(b2Object.fileInfo()).isEqualTo(FILE_INFO);
         assertThat(b2Object.uploadTimestamp()).isAfterYear(2015);
         assertThat(b2Object.payload().getContentMetadata().getContentLength()).isEqualTo(PAYLOAD.length());
         assertThat(b2Object.payload().getContentMetadata().getContentType()).isEqualTo(CONTENT_TYPE);

         assertThat(server.getRequestCount()).isEqualTo(2);

         RecordedRequest request = server.takeRequest();
         assertThat(request.getMethod()).isEqualTo("GET");
         assertThat(request.getPath()).isEqualTo("/b2api/v1/b2_authorize_account");

         request = server.takeRequest();
         assertThat(request.getMethod()).isEqualTo("GET");
         assertThat(request.getPath()).isEqualTo("/b2api/v1/b2_download_file_by_id?fileId=4_h4a48fe8875c6214145260818_f000000000000472a_d20140104_m032022_c001_v0000123_t0104");
      } finally {
         server.shutdown();
      }
   }

   public void testDownloadFileByIdOptions() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));

      server.enqueue(new MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE)
            .addHeader(B2Headers.FILE_ID, FILE_ID)
            .addHeader(B2Headers.FILE_NAME, FILE_NAME)
            .addHeader(B2Headers.CONTENT_SHA1, SHA1)
            .addHeader(B2Headers.UPLOAD_TIMESTAMP, String.valueOf(1500000000000L))
            .addHeader(B2Headers.FILE_INFO_PREFIX + FILE_INFO.entrySet().iterator().next().getKey(), FILE_INFO.entrySet().iterator().next().getValue())
            .setBody(PAYLOAD));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();

         B2Object b2Object = api.downloadFileById(FILE_ID, new GetOptions().range(42, 69));

         assertThat(server.getRequestCount()).isEqualTo(2);

         RecordedRequest request = server.takeRequest();
         assertThat(request.getMethod()).isEqualTo("GET");
         assertThat(request.getPath()).isEqualTo("/b2api/v1/b2_authorize_account");

         request = server.takeRequest();
         assertThat(request.getMethod()).isEqualTo("GET");
         assertThat(request.getPath()).isEqualTo("/b2api/v1/b2_download_file_by_id?fileId=4_h4a48fe8875c6214145260818_f000000000000472a_d20140104_m032022_c001_v0000123_t0104");
         assertThat(request.getHeaders()).contains("Range: bytes=42-69");
      } finally {
         server.shutdown();
      }
   }

   public void testDownloadFileByName() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));

      server.enqueue(new MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE)
            .addHeader(B2Headers.FILE_ID, FILE_ID)
            .addHeader(B2Headers.FILE_NAME, FILE_NAME)
            .addHeader(B2Headers.CONTENT_SHA1, SHA1)
            .addHeader(B2Headers.UPLOAD_TIMESTAMP, String.valueOf(1500000000000L))
            .addHeader(B2Headers.FILE_INFO_PREFIX + FILE_INFO.entrySet().iterator().next().getKey(), FILE_INFO.entrySet().iterator().next().getValue())
            .setBody(PAYLOAD));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();

         B2Object b2Object = api.downloadFileByName(BUCKET_NAME, FILE_NAME);

         assertThat(b2Object.fileId()).isEqualTo(FILE_ID);
         assertThat(b2Object.fileName()).isEqualTo(FILE_NAME);
         assertThat(b2Object.contentSha1()).isEqualTo(SHA1);
         assertThat(b2Object.fileInfo()).isEqualTo(FILE_INFO);
         assertThat(b2Object.uploadTimestamp()).isAfterYear(2015);
         assertThat(b2Object.payload().getContentMetadata().getContentLength()).isEqualTo(PAYLOAD.length());
         assertThat(b2Object.payload().getContentMetadata().getContentType()).isEqualTo(CONTENT_TYPE);

         assertThat(server.getRequestCount()).isEqualTo(2);

         RecordedRequest request = server.takeRequest();
         assertThat(request.getMethod()).isEqualTo("GET");
         assertThat(request.getPath()).isEqualTo("/b2api/v1/b2_authorize_account");

         request = server.takeRequest();
         assertThat(request.getMethod()).isEqualTo("GET");
         assertThat(request.getPath()).isEqualTo("/file/BUCKET_NAME/typing_test.txt");
      } finally {
         server.shutdown();
      }
   }

   public void testListFileNames() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/list_file_names_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         String accountId = "d522aa47a10f";

         B2ObjectList list = api.listFileNames(BUCKET_ID, null, null);

         assertThat(list.nextFileName()).isNull();
         assertThat(list.files()).hasSize(2);

         B2ObjectList.Entry object = list.files().get(0);
         assertThat(object.action()).isEqualTo(Action.UPLOAD);
         assertThat(object.fileId()).isEqualTo("4_z27c88f1d182b150646ff0b16_f1004ba650fe24e6b_d20150809_m012853_c100_v0009990_t0000");
         assertThat(object.fileName()).isEqualTo("files/hello.txt");
         assertThat(object.size()).isEqualTo(6);
         assertThat(object.uploadTimestamp()).isEqualTo(new Date(1439083733000L));

         object = list.files().get(1);
         assertThat(object.action()).isEqualTo(Action.UPLOAD);
         assertThat(object.fileId()).isEqualTo("4_z27c88f1d182b150646ff0b16_f1004ba650fe24e6c_d20150809_m012854_c100_v0009990_t0000");
         assertThat(object.fileName()).isEqualTo("files/world.txt");
         assertThat(object.size()).isEqualTo(6);
         assertThat(object.uploadTimestamp()).isEqualTo(new Date(1439083734000L));

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_list_file_names", "/list_file_names_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testListFileVersions() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/list_file_versions_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         String accountId = "d522aa47a10f";

         B2ObjectList list = api.listFileVersions(BUCKET_ID, null, null, null);

         assertThat(list.nextFileId()).isEqualTo("4_z27c88f1d182b150646ff0b16_f100920ddab886247_d20150809_m232316_c100_v0009990_t0003");
         assertThat(list.nextFileName()).isEqualTo("files/world.txt");
         assertThat(list.files()).hasSize(3);

         B2ObjectList.Entry object = list.files().get(0);
         assertThat(object.action()).isEqualTo(Action.UPLOAD);
         assertThat(object.fileId()).isEqualTo("4_z27c88f1d182b150646ff0b16_f100920ddab886245_d20150809_m232316_c100_v0009990_t0003");
         assertThat(object.fileName()).isEqualTo("files/hello.txt");
         assertThat(object.size()).isEqualTo(6);
         assertThat(object.uploadTimestamp()).isEqualTo(new Date(1439162596000L));

         object = list.files().get(1);
         assertThat(object.action()).isEqualTo(Action.HIDE);
         assertThat(object.fileId()).isEqualTo("4_z27c88f1d182b150646ff0b16_f100920ddab886247_d20150809_m232323_c100_v0009990_t0005");
         assertThat(object.fileName()).isEqualTo("files/world.txt");
         assertThat(object.size()).isEqualTo(0);
         assertThat(object.uploadTimestamp()).isEqualTo(new Date(1439162603000L));

         object = list.files().get(2);
         assertThat(object.action()).isEqualTo(Action.UPLOAD);
         assertThat(object.fileId()).isEqualTo("4_z27c88f1d182b150646ff0b16_f100920ddab886246_d20150809_m232316_c100_v0009990_t0003");
         assertThat(object.fileName()).isEqualTo("files/world.txt");
         assertThat(object.size()).isEqualTo(6);
         assertThat(object.uploadTimestamp()).isEqualTo(new Date(1439162596000L));

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_list_file_versions", "/list_file_versions_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testHideFile() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/hide_file_response.json")));

      try {
         ObjectApi api = api(server.getUrl("/").toString(), "b2").getObjectApi();
         String accountId = "d522aa47a10f";

         HideFileResponse response = api.hideFile(BUCKET_ID, FILE_NAME);
         assertThat(response.action()).isEqualTo(Action.HIDE);
         assertThat(response.fileId()).isEqualTo("4_h4a48fe8875c6214145260818_f000000000000472a_d20140104_m032022_c001_v0000123_t0104");
         assertThat(response.fileName()).isEqualTo(FILE_NAME);
         assertThat(response.uploadTimestamp()).isEqualTo(new Date(1437815673000L));

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_hide_file", "/hide_file_request.json");
      } finally {
         server.shutdown();
      }
   }
}
