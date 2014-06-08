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
import org.jclouds.googlecomputeengine.parse.ParseRegionListTest;
import org.jclouds.googlecomputeengine.parse.ParseRegionTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class RegionApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public static final String REGIONS_URL_PREFIX = "https://www.googleapis.com/compute/v1/projects/myproject/regions";

   public static final HttpRequest GET_REGION_REQ = HttpRequest
           .builder()
           .method("GET")
           .endpoint(REGIONS_URL_PREFIX + "/us-central1")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpRequest LIST_REGIONS_REQ = HttpRequest
           .builder()
           .method("GET")
           .endpoint(REGIONS_URL_PREFIX)
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_REGIONS_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/region_list.json")).build();

   public void testGetRegionResponseIs2xx() throws Exception {


      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_get.json")).build();

      RegionApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_REGION_REQ, operationResponse).getRegionApiForProject("myproject");

      assertEquals(api.get("us-central1"),
              new ParseRegionTest().expected());
   }

   public void testGetRegionResponseIs4xx() throws Exception {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      RegionApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_REGION_REQ, operationResponse).getRegionApiForProject("myproject");

      assertNull(api.get("us-central1"));
   }

   public void testListRegionNoOptionsResponseIs2xx() throws Exception {

      RegionApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_REGIONS_REQ, LIST_REGIONS_RESPONSE).getRegionApiForProject("myproject");

      assertEquals(api.listFirstPage().toString(),
              new ParseRegionListTest().expected().toString());
   }

   public void testListRegionWithPaginationOptionsResponseIs4xx() {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      RegionApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_REGIONS_REQ, operationResponse).getRegionApiForProject("myproject");

      assertTrue(api.list().concat().isEmpty());
   }
}
