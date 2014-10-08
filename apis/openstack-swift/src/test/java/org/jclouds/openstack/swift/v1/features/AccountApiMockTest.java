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

import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_BYTES_USED;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_CONTAINER_COUNT;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_METADATA_PREFIX;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_OBJECT_COUNT;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_REMOVE_METADATA_PREFIX;
import static org.jclouds.openstack.swift.v1.reference.SwiftHeaders.ACCOUNT_TEMPORARY_URL_KEY;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Account;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

@Test(groups = "unit", testName = "AccountApiMockTest")
public class AccountApiMockTest extends BaseOpenStackMockTest<SwiftApi> {

   /** upper-cases first char, and lower-cases rest!! **/
   public void getKnowingServerMessesWithMetadataKeyCaseFormat() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(accountResponse()
            // note silly casing
            .addHeader(ACCOUNT_METADATA_PREFIX + "Apiname", "swift")
            .addHeader(ACCOUNT_METADATA_PREFIX + "Apiversion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         Account account = api.getAccountApi("DFW").get();
         assertEquals(account.getContainerCount(), 3l);
         assertEquals(account.getObjectCount(), 42l);
         assertEquals(account.getBytesUsed(), 323479l);
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(account.getMetadata().get(entry.getKey().toLowerCase()), entry.getValue());
         }

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertRequest(server.takeRequest(), "HEAD", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9");
      } finally {
         server.shutdown();
      }
   }

   public void updateMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(accountResponse()
            .addHeader(ACCOUNT_METADATA_PREFIX + "ApiName", "swift")
            .addHeader(ACCOUNT_METADATA_PREFIX + "ApiVersion", "v1.1")));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getAccountApi("DFW").updateMetadata(metadata));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);

         RecordedRequest replaceRequest = server.takeRequest();
         assertRequest(replaceRequest, "POST", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9");
         for (Entry<String, String> entry : metadata.entrySet()) {
            assertEquals(replaceRequest.getHeader(ACCOUNT_METADATA_PREFIX + entry.getKey().toLowerCase()), entry.getValue());
         }
      } finally {
         server.shutdown();
      }
   }

   public void updateTemporaryUrlKey() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(accountResponse()));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getAccountApi("DFW").updateTemporaryUrlKey("foobar"));

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);

         RecordedRequest replaceRequest = server.takeRequest();
         assertRequest(replaceRequest, "POST", "/v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9");
         assertEquals(replaceRequest.getHeader(ACCOUNT_TEMPORARY_URL_KEY), "foobar");
      } finally {
         server.shutdown();
      }
   }

   public void deleteMetadata() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(accountResponse()));

      try {
         SwiftApi api = api(server.getUrl("/").toString(), "openstack-swift");
         assertTrue(api.getAccountApi("DFW").deleteMetadata(metadata));

         assertEquals(server.getRequestCount(), 2);
         assertEquals(server.takeRequest().getRequestLine(), "POST /tokens HTTP/1.1");
         RecordedRequest deleteRequest = server.takeRequest();
         assertEquals(deleteRequest.getRequestLine(),
               "POST /v1/MossoCloudFS_5bcf396e-39dd-45ff-93a1-712b9aba90a9 HTTP/1.1");
         for (String key : metadata.keySet()) {
            assertEquals(deleteRequest.getHeader(ACCOUNT_REMOVE_METADATA_PREFIX + key.toLowerCase()), "ignored");
         }
      } finally {
         server.shutdown();
      }
   }

   private static final Map<String, String> metadata = ImmutableMap.of("ApiName", "swift", "ApiVersion", "v1.1");

   public static MockResponse accountResponse() {
      return new MockResponse()
            .addHeader(ACCOUNT_CONTAINER_COUNT, "3")
            .addHeader(ACCOUNT_OBJECT_COUNT, "42")
            .addHeader(ACCOUNT_BYTES_USED, "323479");
   }
}
