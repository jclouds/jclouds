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

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.options.RouteOptions;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseRouteListTest;
import org.jclouds.googlecomputeengine.parse.ParseRouteTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class RouteApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public void testGetRouteResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/global/routes/default-route-c99ebfbed0e1f375")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/route_get.json")).build();

      RouteApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getRouteApiForProject("myproject");

      assertEquals(api.get("default-route-c99ebfbed0e1f375"),
              new ParseRouteTest().expected());
   }

   public void testGetRouteResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/global/routes/default-route-c99ebfbed0e1f375")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      RouteApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getRouteApiForProject("myproject");

      assertNull(api.get("default-route-c99ebfbed0e1f375"));
   }

   public void testInsertRouteResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()

              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/global/routes")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/route_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertRouteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/global_operation.json")).build();

      RouteApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertRouteResponse).getRouteApiForProject("myproject");

      assertEquals(api.createInNetwork("default-route-c99ebfbed0e1f375",
              URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/default"),
              new RouteOptions().addTag("fooTag")
                      .addTag("barTag")
                      .description("Default route to the virtual network.")
              .destRange("10.240.0.0/16")
              .priority(1000)
              .nextHopNetwork(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/default"))
      ), new ParseOperationTest().expected());
   }

   public void testDeleteRouteResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/routes/default-route-c99ebfbed0e1f375")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/global_operation.json")).build();

      RouteApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getRouteApiForProject("myproject");

      assertEquals(api.delete("default-route-c99ebfbed0e1f375"),
              new ParseOperationTest().expected());
   }

   public void testDeleteRouteResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/routes/default-route-c99ebfbed0e1f375")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      RouteApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getRouteApiForProject("myproject");

      assertNull(api.delete("default-route-c99ebfbed0e1f375"));
   }

   public void testListRoutesResponseIs2xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/routes")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/route_list.json")).build();

      RouteApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getRouteApiForProject("myproject");

      assertEquals(api.listFirstPage().toString(),
              new ParseRouteListTest().expected().toString());
   }

   public void testListRoutesResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/routes")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      RouteApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getRouteApiForProject("myproject");

      assertTrue(api.list().concat().isEmpty());
   }
}
