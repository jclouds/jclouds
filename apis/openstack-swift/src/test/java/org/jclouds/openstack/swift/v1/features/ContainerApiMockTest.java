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

import static org.jclouds.openstack.swift.v1.options.CreateContainerOptions.Builder.anybodyRead;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_ACL_ANYBODY_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_BYTES_USED;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_METADATA_PREFIX;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_OBJECT_COUNT;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_READ;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.CONTAINER_REMOVE_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "ContainerApiMockTest")
public class ContainerApiMockTest extends BaseOpenStackMockTest<SwiftApi> {

   public void testList() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/container_list.json"))));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         ImmutableList<Container> containers = api.getContainerApi("DFW").list().toList();
         assertEquals(containers, ImmutableList.of(
               Container.builder()
                     .name("test_container_1")
                     .objectCount(2)
                     .bytesUsed(78).build(),
               Container.builder()
                     .name("test_container_2")
                     .objectCount(1)
                     .bytesUsed(17).build()));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9");
      } finally {
         server.shutdown();
      }
   }

   public void testListWithOptions() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/container_list.json"))));

      ListContainerOptions options = ListContainerOptions.Builder.marker("test");
      assertNotNull(options);

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         ImmutableList<Container> containers = api.getContainerApi("DFW").list(options).toList();
         assertEquals(containers, ImmutableList.of(
               Container.builder()
                     .name("test_container_1")
                     .objectCount(2)
                     .bytesUsed(78).build(),
               Container.builder()
                     .name("test_container_2")
                     .objectCount(1)
                     .bytesUsed(17).build()));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "GET", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9?marker=test");
      } finally {
         server.shutdown();
      }
   }

   public void testContainerExists() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));
      server.enqueue(addCommonHeaders(containerResponse()
            .addHeader(CONTAINER_METADATA_PREFIX + "ApiName", "swift")
            .addHeader(CONTAINER_METADATA_PREFIX + "ApiVersion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getContainerApi("DFW").create("myContainer", anybodyRead().metadata(metadata)));

         Container container = api.getContainerApi("DFW").get("myContainer");
         assertEquals(container.getName(), "myContainer");
         assertEquals(container.getObjectCount(), 42l);
         assertEquals(container.getBytesUsed(), 323479l);
         for (Entry<String, String> entry : container.getMetadata().entrySet()) {
            assertEquals(container.getMetadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }
         assertEquals(server.getRequestCount(), 3);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer");
         assertRequest(server.takeRequest(), "HEAD", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer");
      } finally {
         server.shutdown();
      }
   }

   @Test(expectedExceptions = ContainerNotFoundException.class)
   public void testContainerDoesNotExist() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(404)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getContainerApi("DFW").create("myContainer", anybodyRead().metadata(metadata)));

         // the head call will throw the ContainerNotFoundException
         api.getContainerApi("DFW").get("myContainer");
      } finally {
         server.shutdown();
      }
   }

   public void testCreate() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getContainerApi("DFW").create("myContainer"));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWithOptions() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getContainerApi("DFW").create("myContainer", anybodyRead().metadata(metadata)));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);

         RecordedRequest createRequest = server.takeRequest();
         assertRequest(createRequest, "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer");

         assertEquals(createRequest.getHeader(CONTAINER_READ), CONTAINER_ACL_ANYBODY_READ);

         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(createRequest.getHeader(CONTAINER_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void testCreateWithSpacesAndSpecialCharacters() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(201)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getContainerApi("DFW").create("container # ! special"));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/container%20%23%20%21%20special");
      } finally {
         server.shutdown();
      }
   }

   public void testAlreadyCreated() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(202)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertFalse(api.getContainerApi("DFW").create("myContainer"));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "PUT", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer");
      } finally {
         server.shutdown();
      }
   }

   /** upper-cases first char, and lower-cases rest!! **/
   public void testGetKnowingServerMessesWithMetadataKeyCaseFormat() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(containerResponse()
            // note silly casing
            .addHeader(CONTAINER_METADATA_PREFIX + "Apiname", "swift")
            .addHeader(CONTAINER_METADATA_PREFIX + "Apiversion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         Container container = api.getContainerApi("DFW").get("myContainer");
         assertEquals(container.getName(), "myContainer");
         assertEquals(container.getObjectCount(), 42l);
         assertEquals(container.getBytesUsed(), 323479l);
         for (Entry<String, String> entry : container.getMetadata().entrySet()) {
            assertEquals(container.getMetadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "HEAD", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(containerResponse()
            .addHeader(CONTAINER_METADATA_PREFIX + "ApiName", "swift")
            .addHeader(CONTAINER_METADATA_PREFIX + "ApiVersion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getContainerApi("DFW").updateMetadata("myContainer", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest replaceRequest = server.takeRequest();
         assertEquals(replaceRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replaceRequest.getHeader(CONTAINER_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(containerResponse()));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getContainerApi("DFW").deleteMetadata("myContainer", metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
         for (String key : metadata.keySet()) {
            assertEquals(deleteRequest.getHeader(CONTAINER_REMOVE_METADATA_PREFIX + key.toLowerCase()), "ignored");
         }
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteIfEmpty() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(204)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getContainerApi("DFW").deleteIfEmpty("myContainer"));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
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
         assertTrue(api.getContainerApi("DFW").deleteIfEmpty("myContainer"));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteWhenNotEmpty() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setResponseCode(409)));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertFalse(api.getContainerApi("DFW").deleteIfEmpty("myContainer"));

      } finally {
         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "DELETE /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9/myContainer HTTP/1.1");
         server.shutdown();
      }
   }

   private static final Map<String, String> metadata = ImmutableMap.of("ApiName", "swift", "ApiVersion", "v1.1");

   static MockResponse containerResponse() {
      return new MockResponse()
            .addHeader(CONTAINER_OBJECT_COUNT, "42")
            .addHeader(CONTAINER_BYTES_USED, "323479");
   }
}
