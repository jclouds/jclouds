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

import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.OBJECT_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Segment;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "StaticLargeObjectApiMockTest")
public class StaticLargeObjectApiMockTest extends BaseOpenStackMockTest<SwiftApi> {

   public void testReplaceManifest() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().addHeader(HttpHeaders.ETAG, "\"abcd\"")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertEquals(
               api.getStaticLargeObjectApi("DFW", "myContainer").replaceManifest(
                     "myObject",
                     ImmutableList
                           .<Segment> builder()
                           .add(Segment.builder().path("/mycontainer/objseg1").etag("0228c7926b8b642dfb29554cd1f00963")
                                 .sizeBytes(1468006).build())
                           .add(Segment.builder().path("/mycontainer/pseudodir/seg-obj2")
                                 .etag("5bfc9ea51a00b790717eeb934fb77b9b").sizeBytes(1572864).build())
                           .add(Segment.builder().path("/other-container/seg-final")
                                 .etag("b9c3da507d2557c1ddc51f27c54bae51").sizeBytes(256).build()).build(),
                     ImmutableMap.of("MyFoo", "Bar")), "abcd");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);

         RecordedRequest replaceRequest = server.takeRequest();
         assertRequest(replaceRequest, "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject?multipart-manifest=put");
         assertEquals(replaceRequest.getHeader(OBJECT_METADATA_PREFIX + "myfoo"), "Bar");
         assertEquals(
               new String(replaceRequest.getBody()),
         "[{\"path\":\"/mycontainer/objseg1\",\"etag\":\"0228c7926b8b642dfb29554cd1f00963\",\"size_bytes\":1468006}," +
          "{\"path\":\"/mycontainer/pseudodir/seg-obj2\",\"etag\":\"5bfc9ea51a00b790717eeb934fb77b9b\",\"size_bytes\":1572864}," +
          "{\"path\":\"/other-container/seg-final\",\"etag\":\"b9c3da507d2557c1ddc51f27c54bae51\",\"size_bytes\":256}]");
      } finally {
         server.shutdown();
      }
   }

   public void testDelete() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         api.getStaticLargeObjectApi("DFW", "myContainer").delete("myObject");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject?multipart-manifest=delete");

      } finally {
         server.shutdown();
      }
   }

   public void testAlreadyDeleted() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         api.getStaticLargeObjectApi("DFW", "myContainer").delete("myObject");

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "DELETE", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer/myObject?multipart-manifest=delete");
      } finally {
         server.shutdown();
      }
   }
}
