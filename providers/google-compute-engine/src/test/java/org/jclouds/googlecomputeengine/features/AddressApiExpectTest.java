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
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseAddressListTest;
import org.jclouds.googlecomputeengine.parse.ParseAddressTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class AddressApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public void testGetAddressResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/addresses/test-ip1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/address_get.json")).build();

      AddressApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getAddressApiForProject("myproject");

      assertEquals(api.getInRegion("us-central1", "test-ip1"),
              new ParseAddressTest().expected());
   }

   public void testGetAddressResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/addresses/test-ip1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      AddressApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getAddressApiForProject("myproject");

      assertNull(api.getInRegion("us-central1", "test-ip1"));
   }

   public void testInsertAddressResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/addresses")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/address_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertAddressResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      AddressApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertAddressResponse).getAddressApiForProject("myproject");

      assertEquals(api.createInRegion("us-central1", "test-ip1"), new ParseOperationTest().expected());
   }

   public void testDeleteAddressResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/regions/us-central1/addresses/test-ip1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      AddressApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getAddressApiForProject("myproject");

      assertEquals(api.deleteInRegion("us-central1", "test-ip1"),
              new ParseOperationTest().expected());
   }

   public void testDeleteAddressResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/regions/us-central1/addresses/test-ip1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      AddressApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getAddressApiForProject("myproject");

      assertNull(api.deleteInRegion("us-central1", "test-ip1"));
   }

   public void testListAddresssResponseIs2xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/regions/us-central1/addresses")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/address_list.json")).build();

      AddressApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getAddressApiForProject("myproject");

      assertEquals(api.listFirstPageInRegion("us-central1").toString(),
              new ParseAddressListTest().expected().toString());
   }

   public void testListAddresssResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/regions/us-central1/addresses")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      AddressApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getAddressApiForProject("myproject");

      assertTrue(api.listInRegion("us-central1").concat().isEmpty());
   }
}
