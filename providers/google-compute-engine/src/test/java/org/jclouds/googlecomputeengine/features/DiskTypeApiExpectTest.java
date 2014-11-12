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

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseDiskTypeListTest;
import org.jclouds.googlecomputeengine.parse.ParseDiskTypeTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "DiskTypeApiExpectTest")
public class DiskTypeApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public static final HttpRequest LIST_DISK_TYPES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint(BASE_URL + "/party/zones/us-central1-a/diskTypes")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_DISK_TYPES_RESPONSE = HttpResponse.builder()
           .statusCode(200)
           .payload(staticPayloadFromResource("/disktype_list.json"))
           .build();

   public void testGetDiskTypeResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/diskTypes/pd-standard")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/disktype.json")).build();

      DiskTypeApi diskTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, response).diskTypesInZone("us-central1-a");

      assertEquals(diskTypeApi.get("pd-standard"),
              new ParseDiskTypeTest().expected());
   }

   public void testGetDiskTypeResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/diskTypes/pd-standard")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      DiskTypeApi diskTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, response).diskTypesInZone("us-central1-a");

      assertNull(diskTypeApi.get("pd-standard"));
   }

   public void list() throws Exception {

      DiskTypeApi diskTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, LIST_DISK_TYPES_REQUEST, LIST_DISK_TYPES_RESPONSE).diskTypesInZone("us-central1-a");

      assertEquals(diskTypeApi.list().next(), new ParseDiskTypeListTest().expected());
   }

   public void listWithOptionsEmpty() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();
      
      HttpRequest listRequestWithOptions = LIST_DISK_TYPES_REQUEST.toBuilder()
            .endpoint(LIST_DISK_TYPES_REQUEST.getEndpoint() + "?maxResults=1").build();

      DiskTypeApi diskTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, listRequestWithOptions, response).diskTypesInZone("us-central1-a");

      assertFalse(diskTypeApi.list(maxResults(1)).hasNext());
   }
}
