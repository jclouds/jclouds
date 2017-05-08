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

import org.jclouds.b2.domain.Bucket;
import org.jclouds.b2.domain.BucketList;
import org.jclouds.b2.domain.BucketType;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "BucketApiMockTest")
public final class BucketApiMockTest {
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
}
