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
import org.jclouds.googlecomputeengine.parse.ParseDiskTypeListTest;
import org.jclouds.googlecomputeengine.parse.ParseDiskTypeTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class DiskTypeApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public static final HttpRequest LIST_DISK_TYPES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/diskTypes")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_DISK_TYPES_RESPONSE = HttpResponse.builder()
           .statusCode(200)
           .payload(staticPayloadFromResource("/disktype_list.json"))
           .build();

   public static final HttpRequest LIST_CENTRAL1B_DISK_TYPES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-b/diskTypes")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_CENTRAL1B_DISK_TYPES_RESPONSE = HttpResponse.builder()
           .statusCode(200)
           .payload(staticPayloadFromResource("/disktype_list_central1b.json"))
           .build();

   public void testGetDiskTypeResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/diskTypes/pd-standard")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/disktype.json")).build();

      DiskTypeApi diskTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getDiskTypeApi("myproject");

      assertEquals(diskTypeApi.getInZone("us-central1-a", "pd-standard"),
              new ParseDiskTypeTest().expected());
   }

   public void testGetDiskTypeResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/diskTypes/pd-standard")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      DiskTypeApi diskTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getDiskTypeApi("myproject");

      assertNull(diskTypeApi.getInZone("us-central1-a", "pd-standard"));
   }

   public void testListDiskTypeNoOptionsResponseIs2xx() throws Exception {

      DiskTypeApi diskTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_DISK_TYPES_REQUEST, LIST_DISK_TYPES_RESPONSE).getDiskTypeApi
              ("myproject");

      assertEquals(diskTypeApi.listFirstPageInZone("us-central1-a").toString(),
              new ParseDiskTypeListTest().expected().toString());
   }

   public void testLisOperationWithPaginationOptionsResponseIs4xx() {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      DiskTypeApi diskTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_DISK_TYPES_REQUEST, operationResponse).getDiskTypeApi("myproject");

      assertTrue(diskTypeApi.listInZone("us-central1-a").concat().isEmpty());
   }
}
