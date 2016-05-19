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
package org.jclouds.labs.b2.features;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.labs.b2.B2Api;
import org.jclouds.labs.b2.domain.Bucket;
import org.jclouds.labs.b2.domain.BucketList;
import org.jclouds.labs.b2.domain.BucketType;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "BucketApiMockTest")
public final class BucketApiMockTest {
   private final Set<Module> modules = ImmutableSet.<Module> of(
         new ExecutorServiceModule(MoreExecutors.sameThreadExecutor()));

   public void testCreateBucket() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/bucket.json")));

      try {
         BucketApi api = api(server.getUrl("/").toString(), "b2").getBucketApi();
         Bucket response = api.createBucket("any_name_you_pick", BucketType.ALL_PRIVATE);
         assertThat(response.bucketId()).isEqualTo("4a48fe8875c6214145260818");
         assertThat(response.bucketName()).isEqualTo("any_name_you_pick");
         assertThat(response.bucketType()).isEqualTo(BucketType.ALL_PRIVATE);

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_create_bucket", "/create_bucket_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteBucket() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/bucket.json")));

      try {
         BucketApi api = api(server.getUrl("/").toString(), "b2").getBucketApi();
         Bucket response = api.deleteBucket("4a48fe8875c6214145260818");
         assertThat(response.bucketId()).isEqualTo("4a48fe8875c6214145260818");
         assertThat(response.bucketName()).isEqualTo("any_name_you_pick");
         assertThat(response.bucketType()).isEqualTo(BucketType.ALL_PRIVATE);

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_delete_bucket", "/delete_bucket_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteAlreadyDeletedBucket() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setResponseCode(400).setBody(stringFromResource("/delete_bucket_already_deleted_response.json")));

      try {
         BucketApi api = api(server.getUrl("/").toString(), "b2").getBucketApi();
         Bucket response = api.deleteBucket("4a48fe8875c6214145260818");
         assertThat(response).isNull();

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_delete_bucket", "/delete_bucket_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateBucket() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/bucket.json")));

      try {
         BucketApi api = api(server.getUrl("/").toString(), "b2").getBucketApi();
         Bucket response = api.updateBucket("4a48fe8875c6214145260818", BucketType.ALL_PRIVATE);
         assertThat(response.bucketId()).isEqualTo("4a48fe8875c6214145260818");
         assertThat(response.bucketName()).isEqualTo("any_name_you_pick");
         assertThat(response.bucketType()).isEqualTo(BucketType.ALL_PRIVATE);

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_update_bucket", "/update_bucket_request.json");
      } finally {
         server.shutdown();
      }
   }

   public void testListBuckets() throws Exception {
      MockWebServer server = createMockWebServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/authorize_account_response.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/list_buckets_response.json")));

      try {
         BucketApi api = api(server.getUrl("/").toString(), "b2").getBucketApi();
         BucketList response = api.listBuckets();

         assertThat(response.buckets()).hasSize(3);

         assertThat(response.buckets().get(0).bucketName()).isEqualTo("Kitten Videos");
         assertThat(response.buckets().get(0).bucketType()).isEqualTo(BucketType.ALL_PRIVATE);

         assertThat(response.buckets().get(1).bucketName()).isEqualTo("Puppy Videos");
         assertThat(response.buckets().get(1).bucketType()).isEqualTo(BucketType.ALL_PUBLIC);

         assertThat(response.buckets().get(2).bucketName()).isEqualTo("Vacation Pictures");
         assertThat(response.buckets().get(2).bucketType()).isEqualTo(BucketType.ALL_PRIVATE);

         assertThat(server.getRequestCount()).isEqualTo(2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "POST", "/b2api/v1/b2_list_buckets", "/list_buckets_request.json");
      } finally {
         server.shutdown();
      }
   }

   public B2Api api(String uri, String provider, Properties overrides) {
      return ContextBuilder.newBuilder(provider)
            .credentials("ACCOUNT_ID", "APPLICATION_KEY")
            .endpoint(uri)
            .overrides(overrides)
            .modules(modules)
            .buildApi(B2Api.class);
   }

   public B2Api api(String uri, String provider) {
      return api(uri, provider, new Properties());
   }

   public static MockWebServer createMockWebServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      URL url = server.getUrl("");
      return server;
   }

   public void assertAuthentication(MockWebServer server) {
      assertThat(server.getRequestCount()).isGreaterThanOrEqualTo(1);
      try {
         assertThat(server.takeRequest().getRequestLine()).isEqualTo("GET /b2api/v1/b2_authorize_account HTTP/1.1");
      } catch (InterruptedException e) {
         throw Throwables.propagate(e);
      }
   }

   /**
    * Ensures the request has a json header for the proper REST methods.
    *
    * @param request
    * @param method
    *           The request method (such as GET).
    * @param path
    *           The path requested for this REST call.
    * @see RecordedRequest
    */
   public void assertRequest(RecordedRequest request, String method, String path) {
      assertThat(request.getMethod()).isEqualTo(method);
      assertThat(request.getPath()).isEqualTo(path);
   }

   /**
    * Ensures the request is json and has the same contents as the resource
    * file provided.
    *
    * @param request
    * @param method
    *           The request method (such as GET).
    * @param resourceLocation
    *           The location of the resource file. Contents will be compared to
    *           the request body as JSON.
    * @see RecordedRequest
    */
   public void assertRequest(RecordedRequest request, String method, String path, String resourceLocation) {
      assertRequest(request, method, path);
      assertContentTypeIsJson(request);
      JsonParser parser = new JsonParser();
      JsonElement requestJson;
      try {
         requestJson = parser.parse(new String(request.getBody(), Charsets.UTF_8));
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
      JsonElement resourceJson = parser.parse(stringFromResource(resourceLocation));
      assertThat(requestJson).isEqualTo(resourceJson);
   }

   /**
    * Ensures the request has a json header.
    *
    * @param request
    * @see RecordedRequest
    */
   private void assertContentTypeIsJson(RecordedRequest request) {
      assertThat(request.getHeaders()).contains("Content-Type: application/json");
   }

   /**
    * Get a string from a resource
    *
    * @param resourceName
    *           The name of the resource.
    * @return The content of the resource
    */
   public String stringFromResource(String resourceName) {
      try {
         return Strings2.toStringAndClose(getClass().getResourceAsStream(resourceName));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
}
