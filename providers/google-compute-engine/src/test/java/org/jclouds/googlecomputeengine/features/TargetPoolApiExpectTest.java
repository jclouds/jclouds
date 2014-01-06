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

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseRegionOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolListTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

@Test(groups = "unit")
public class TargetPoolApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public void testGetTargetPoolResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/targetpool_get.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertEquals(api.get("test"),
              new ParseTargetPoolTest().expected());
   }

   public void testGetTargetPoolResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertNull(api.get("test"));
   }

   public void testInsertTargetPoolResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/targetpool_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertTargetPoolResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertTargetPoolResponse).getTargetPoolApi("myproject", "us-central1");
      assertEquals(api.create("test"), new ParseRegionOperationTest().expected());
   }

   public void testDeleteTargetPoolResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test-targetPool")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getTargetPoolApi("myproject", "us-central1");

      assertEquals(api.delete("test-targetPool"),
              new ParseRegionOperationTest().expected());
   }

   public void testDeleteTargetPoolResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test-targetPool")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getTargetPoolApi("myproject", "us-central1");

      assertNull(api.delete("test-targetPool"));
   }

   public void testListTargetPoolsResponseIs2xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/targetpool_list.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertEquals(api.list().toString(),
              new ParseTargetPoolListTest().expected().toString());
   }

   public void testListTargetPoolsResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertTrue(api.list().concat().isEmpty());
   }

   public void testAddInstanceResponseIs2xx() throws Exception {
      HttpRequest addInstance = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test/addInstance")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/targetpool_addinstance.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addInstance, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertEquals(api.addInstance("test",
              "https://www.googleapis.com/compute/v1/projects/myproject/zones/europe-west1-a/instances/test"),
              new ParseRegionOperationTest().expected());
   }

   public void testAddInstanceResponseIs4xx() throws Exception {
      HttpRequest addInstance = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test/addInstance")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/targetpool_addinstance.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addInstance, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertNull(api.addInstance("test", "https://www.googleapis" +
              ".com/compute/v1/projects/myproject/zones/europe-west1-a/instances/test"));
   }
}
