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
import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseHttpHealthCheckListTest;
import org.jclouds.googlecomputeengine.parse.ParseHttpHealthCheckTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "HttpHealthCheckApiExpectTest")
public class HttpHealthCheckApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public void testGetHttpHealthCheckResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/global/httpHealthChecks/http-health-check-api-live-test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/httphealthcheck_get.json")).build();

      HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, response).httpHeathChecks();

      assertEquals(api.get("http-health-check-api-live-test"),
              new ParseHttpHealthCheckTest().expected());
   }

   public void testGetHttpHealthCheckResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/global/httpHealthChecks/http-health-check-test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, response).httpHeathChecks();

      assertNull(api.get("http-health-check-test"));
   }

   public void testInsertHttpHealthCheckResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/global/httpHealthChecks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/httphealthcheck_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertHttpHealthCheckResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/global_operation.json")).build();

      HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertHttpHealthCheckResponse).httpHeathChecks();
      HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions().timeoutSec(0).unhealthyThreshold(0);
      assertEquals(api.insert("http-health-check", options), new ParseGlobalOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testInsertHttpHealthCheckResponseIs4xx() {
      HttpRequest create = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/global/httpHealthChecks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/httphealthcheck_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertHttpHealthCheckResponse = HttpResponse.builder().statusCode(404).build();

      HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, create, insertHttpHealthCheckResponse).httpHeathChecks();

      HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions().timeoutSec(0).unhealthyThreshold(0);
      
      api.insert("http-health-check", options);
   }

   public void testDeleteHttpHealthCheckResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/global/httpHealthChecks/http-health-check")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/global_operation.json")).build();

      HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).httpHeathChecks();

      assertEquals(api.delete("http-health-check"),
              new ParseGlobalOperationTest().expected());
   }

   public void testDeleteHttpHealthCheckResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/global/httpHealthChecks/http-health-check")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).httpHeathChecks();

      assertNull(api.delete("http-health-check"));
   }

   HttpRequest list = HttpRequest
         .builder()
         .method("GET")
         .endpoint(BASE_URL + "/party/global/httpHealthChecks")
         .addHeader("Accept", "application/json")
         .addHeader("Authorization", "Bearer " + TOKEN).build();

   public void list() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/httphealthcheck_list.json")).build();

      HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, response).httpHeathChecks();

      assertEquals(api.list().next(), new ParseHttpHealthCheckListTest().expected());
   }

   public void listEmpty() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();

      HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, response).httpHeathChecks();

      assertFalse(api.list().hasNext());
   }
   
   public void testPatchHttpHealthChecksResponseIs2xx() {
      String healthCheckName = "http-health-check";
      HttpRequest patch = HttpRequest
            .builder()
            .method("PATCH")
            .endpoint(BASE_URL + "/party/global/httpHealthChecks/" + healthCheckName)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/httphealthcheck_insert.json", MediaType.APPLICATION_JSON))
            .build();

       HttpResponse insertHttpHealthCheckResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResource("/global_operation.json")).build();
   
       HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
               TOKEN_RESPONSE, patch,
               insertHttpHealthCheckResponse).httpHeathChecks();
       HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions().timeoutSec(0).unhealthyThreshold(0);
       assertEquals(api.patch(healthCheckName, options), new ParseGlobalOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testPatchHttpHealthChecksResponseIs4xx(){
      String healthCheckName = "http-health-check";
      HttpRequest patch = HttpRequest
            .builder()
            .method("PATCH")
            .endpoint(BASE_URL + "/party/global/httpHealthChecks/" + healthCheckName)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/httphealthcheck_insert.json", MediaType.APPLICATION_JSON))
            .build();

       HttpResponse insertHttpHealthCheckResponse = HttpResponse.builder().statusCode(404).build();
   
       HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
               TOKEN_RESPONSE, patch,
               insertHttpHealthCheckResponse).httpHeathChecks();
       HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions().timeoutSec(0).unhealthyThreshold(0);
       
       api.patch(healthCheckName, options);
 }

   public void testUpdateHttpHealthChecksResponseIs2xx() {
      String healthCheckName = "http-health-check";
      HttpRequest patch = HttpRequest
            .builder()
            .method("PUT")
            .endpoint(BASE_URL + "/party/global/httpHealthChecks/" + healthCheckName)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/httphealthcheck_insert.json", MediaType.APPLICATION_JSON))
            .build();

       HttpResponse insertHttpHealthCheckResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResource("/global_operation.json")).build();

       HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
               TOKEN_RESPONSE, patch,
               insertHttpHealthCheckResponse).httpHeathChecks();
       HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions().timeoutSec(0).unhealthyThreshold(0);
       assertEquals(api.update(healthCheckName, options), new ParseGlobalOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUpdateHttpHealthChecksResponseIs4xx() {
      String healthCheckName = "http-health-check";
      HttpRequest patch = HttpRequest
            .builder()
            .method("PUT")
            .endpoint(BASE_URL + "/party/global/httpHealthChecks/" + healthCheckName)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/httphealthcheck_insert.json", MediaType.APPLICATION_JSON))
            .build();

       HttpResponse insertHttpHealthCheckResponse = HttpResponse.builder().statusCode(404).build();

       HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
               TOKEN_RESPONSE, patch,
               insertHttpHealthCheckResponse).httpHeathChecks();
       HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions().timeoutSec(0).unhealthyThreshold(0);
       api.update(healthCheckName, options);
   }

}
