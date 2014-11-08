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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseZoneListTest;
import org.jclouds.googlecomputeengine.parse.ParseZoneTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ZoneApiExpectTest")
public class ZoneApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public static final String ZONES_URL_PREFIX = BASE_URL + "/party/zones";

   public static final HttpRequest GET_ZONE_REQ = HttpRequest
           .builder()
           .method("GET")
           .endpoint(ZONES_URL_PREFIX + "/us-central1-a")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpRequest LIST_ZONES_REQ = HttpRequest
           .builder()
           .method("GET")
           .endpoint(ZONES_URL_PREFIX)
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_ZONES_SHORT_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/zone_list_short.json")).build();

   public static final HttpResponse LIST_ZONES_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/zone_list.json")).build();

   public void testGetZoneResponseIs2xx() throws Exception {


      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_get.json")).build();

      ZoneApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_ZONE_REQ, operationResponse).getZoneApi("party");

      assertEquals(api.get("us-central1-a"),
              new ParseZoneTest().expected());
   }

   public void testGetZoneResponseIs4xx() throws Exception {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_ZONE_REQ, operationResponse).getZoneApi("party");

      assertNull(api.get("us-central1-a"));
   }

   public void testListZoneNoOptionsResponseIs2xx() throws Exception {

      ZoneApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_ZONES_REQ, LIST_ZONES_RESPONSE).getZoneApi("party");

      assertEquals(api.list().next().toString(), new ParseZoneListTest().expected().toString());
   }

   public void testListZoneWithPaginationOptionsResponseIs4xx() {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_ZONES_REQ, operationResponse).getZoneApi("party");

      assertFalse(api.list().hasNext());
   }
}
