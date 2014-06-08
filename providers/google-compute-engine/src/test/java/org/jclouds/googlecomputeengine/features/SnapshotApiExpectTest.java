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

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseSnapshotListTest;
import org.jclouds.googlecomputeengine.parse.ParseSnapshotTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class SnapshotApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public static final String SNAPSHOT_URL_PREFIX = "https://www.googleapis.com/compute/v1/projects/myproject/global/snapshots";

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


      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/snapshot_get.json")).build();

      SnapshotApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_SNAPSHOT_REQ, operationResponse).getSnapshotApiForProject("myproject");

      assertEquals(api.get("test-snap"),
              new ParseSnapshotTest().expected());
   }

   public void testGetSnapshotResponseIs4xx() throws Exception {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      SnapshotApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_SNAPSHOT_REQ, operationResponse).getSnapshotApiForProject("myproject");

      assertNull(api.get("test-snap"));
   }

   public void testListSnapshotNoOptionsResponseIs2xx() throws Exception {

      SnapshotApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_SNAPSHOTS_REQ, LIST_SNAPSHOTS_RESPONSE).getSnapshotApiForProject("myproject");

      assertEquals(api.listFirstPage().toString(),
              new ParseSnapshotListTest().expected().toString());
   }

   public void testListSnapshotWithPaginationOptionsResponseIs4xx() {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      SnapshotApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_SNAPSHOTS_REQ, operationResponse).getSnapshotApiForProject("myproject");

      assertTrue(api.list().concat().isEmpty());
   }
}
