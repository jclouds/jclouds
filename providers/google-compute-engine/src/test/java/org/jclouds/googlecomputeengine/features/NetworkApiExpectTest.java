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

import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineScopes.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineScopes.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertNull;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseNetworkListTest;
import org.jclouds.googlecomputeengine.parse.ParseNetworkTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "NetworkApiExpectTest")
public class NetworkApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public static final HttpRequest GET_NETWORK_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint(BASE_URL + "/party/global/networks/jclouds-test")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse GET_NETWORK_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/network_get.json")).build();

   public void testGetNetworkResponseIs2xx() throws Exception {

      NetworkApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_NETWORK_REQUEST, GET_NETWORK_RESPONSE).networks();

      assertEquals(api.get("jclouds-test"),
              new ParseNetworkTest().expected());
   }

   public void testGetNetworkResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/global/networks/jclouds-test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      NetworkApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, response).networks();

      assertNull(api.get("jclouds-test"));
   }

   public void testInsertNetworkResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/global/networks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/network_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertNetworkResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      NetworkApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertNetworkResponse).networks();

      assertEquals(api.createInIPv4Range("test-network", "10.0.0.0/8"), new ParseOperationTest().expected());
   }

   public void testDeleteNetworkResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/global/networks/jclouds-test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      NetworkApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).networks();

      assertEquals(api.delete("jclouds-test"),
              new ParseOperationTest().expected());
   }

   public void testDeleteNetworkResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/global/networks/jclouds-test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      NetworkApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).networks();

      assertNull(api.delete("jclouds-test"));
   }

   HttpRequest list = HttpRequest
         .builder()
         .method("GET")
         .endpoint(BASE_URL + "/party/global/networks")
         .addHeader("Accept", "application/json")
         .addHeader("Authorization", "Bearer " + TOKEN).build();

   public void testListNetworksResponseIs2xx() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/network_list.json")).build();

      NetworkApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, response).networks();

      assertEquals(api.list().next(),
              new ParseNetworkListTest().expected());
   }

   public void listEmpty() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();

      NetworkApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE, list,
            response).networks();

      assertFalse(api.list().hasNext());
   }
}
