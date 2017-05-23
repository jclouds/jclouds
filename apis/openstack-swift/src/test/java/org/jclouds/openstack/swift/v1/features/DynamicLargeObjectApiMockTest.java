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

package org.jclouds.openstack.swift.v1.features;

import static com.google.common.net.HttpHeaders.ETAG;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "DynamicLargeObjectApiMockTest")
public final class DynamicLargeObjectApiMockTest extends BaseOpenStackMockTest<SwiftApi> {

   String containerName = "myContainer";
   String objectName = "myObjectTest";

   @SuppressWarnings("deprecation")
   @Test
   public void uploadLargeFile() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(new MockResponse().setBody("").addHeader(ETAG, "89d903bc35dede724fd52c51437ff5fd"));
      server.enqueue(new MockResponse().setBody("").addHeader(ETAG, "d41d8cd98f00b204e9800998ecf8427e"));
      server.enqueue(addCommonHeaders(new MockResponse().addHeader("X-Object-Manifest", "myContainer/myObject")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertEquals(api.getObjectApi("DFW", containerName).put(objectName.concat("1"), Payloads.newPayload("data1")),
               "89d903bc35dede724fd52c51437ff5fd");
         assertEquals(api.getDynamicLargeObjectApi("DFW", containerName).putManifest(objectName,
               ImmutableMap.of("MyFoo", "Bar"), ImmutableMap.of("MyFoo", "Bar")), "d41d8cd98f00b204e9800998ecf8427e");

         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);

         RecordedRequest uploadRequest = server.takeRequest();
         assertEquals(uploadRequest.getRequestLine(),
               "PUT /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObjectTest1 HTTP/1.1");
         assertEquals(new String(uploadRequest.getBody()), "data1");

         RecordedRequest uploadRequestManifest = server.takeRequest();
         assertRequest(uploadRequestManifest, "PUT",
               "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObjectTest");
         assertEquals(uploadRequestManifest.getHeader(OBJECT_METADATA_PREFIX + "MyFoo"), "Bar");

      } finally {
         server.shutdown();
      }
   }

   @SuppressWarnings("deprecation")
   public void testReplaceManifest() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().addHeader(HttpHeaders.ETAG, "\"abcd\"")));
      server.enqueue(addCommonHeaders(new MockResponse().addHeader("X-Object-Manifest", "myContainer/myObject")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertEquals(api.getDynamicLargeObjectApi("DFW", "myContainer").putManifest("myObject",
               ImmutableMap.of("MyFoo", "Bar"), ImmutableMap.of("MyFoo", "Bar")), "abcd");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);

         RecordedRequest replaceRequest = server.takeRequest();
         assertRequest(replaceRequest, "PUT",
               "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject");
         assertEquals(replaceRequest.getHeader(OBJECT_METADATA_PREFIX + "myfoo"), "Bar");
      } finally {
         server.shutdown();
      }
   }

   @SuppressWarnings("deprecation")
   public void testReplaceManifestUnicodeUTF8() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().addHeader(HttpHeaders.ETAG, "\"abcd\"")));
      server.enqueue(addCommonHeaders(new MockResponse().addHeader("X-Object-Manifest", "myContainer/myObject")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertEquals(api.getDynamicLargeObjectApi("DFW", "myContainer").putManifest("unic₪de",
               ImmutableMap.of("MyFoo", "Bar"), ImmutableMap.of("MyFoo", "Bar")), "abcd");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);

         RecordedRequest replaceRequest = server.takeRequest();
         assertRequest(replaceRequest, "PUT",
               "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/unic%E2%82%AAde");
         assertEquals(replaceRequest.getHeader(OBJECT_METADATA_PREFIX + "myfoo"), "Bar");
      } finally {
         server.shutdown();
      }
   }
}
