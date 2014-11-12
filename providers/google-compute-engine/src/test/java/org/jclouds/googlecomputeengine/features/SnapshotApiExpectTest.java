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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseSnapshotListTest;
import org.jclouds.googlecomputeengine.parse.ParseSnapshotTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "SnapshotApiExpectTest")
public class SnapshotApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public static final String SNAPSHOT_URL_PREFIX = BASE_URL + "/party/global/snapshots";

   public static final HttpRequest GET_SNAPSHOT_REQ = HttpRequest
           .builder()
           .method("GET")
           .endpoint(SNAPSHOT_URL_PREFIX + "/test-snap")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpRequest LIST_SNAPSHOTS_REQ = HttpRequest
           .builder()
           .method("GET")
           .endpoint(SNAPSHOT_URL_PREFIX)
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_SNAPSHOTS_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/snapshot_list.json")).build();

   public void testGetSnapshotResponseIs2xx() throws Exception {
      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/snapshot_get.json")).build();

      SnapshotApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_SNAPSHOT_REQ, response).snapshots();

      assertEquals(api.get("test-snap"),
              new ParseSnapshotTest().expected());
   }

   public void testGetSnapshotResponseIs4xx() throws Exception {
      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      SnapshotApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_SNAPSHOT_REQ, response).snapshots();

      assertNull(api.get("test-snap"));
   }

   public void list() throws Exception {
      SnapshotApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_SNAPSHOTS_REQ, LIST_SNAPSHOTS_RESPONSE).snapshots();

      assertEquals(api.list().next(), new ParseSnapshotListTest().expected());
   }

   public void listEmpty() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();

      SnapshotApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_SNAPSHOTS_REQ, response).snapshots();

      assertFalse(api.list().hasNext());
   }
}
