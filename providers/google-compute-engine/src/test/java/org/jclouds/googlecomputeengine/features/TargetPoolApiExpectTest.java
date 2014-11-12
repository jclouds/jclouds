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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertNull;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseRegionOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolListTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "TargetPoolApiExpectTest")
public class TargetPoolApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   private static final List<URI> INSTANCES = ImmutableList
         .of(URI.create(BASE_URL + "/party/zones/europe-west1-a/instances/test"));

   private static final List<URI> HEALTH_CHECKS = ImmutableList
         .of(URI.create(BASE_URL + "/party/global/httpHealthChecks/health-check-1"));
   
   private static final URI TARGET_POOL = URI.create(BASE_URL + "/party/regions/us-central1/targetPools/tpool");
   
   public void testGetTargetPoolResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/regions/us-central1/targetPools/test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/targetpool_get.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, response).targetPoolsInRegion("us-central1");

      assertEquals(api.get("test"), new ParseTargetPoolTest().expected());
   }

   public void testGetTargetPoolResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/regions/us-central1/targetPools/test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, response).targetPoolsInRegion("us-central1");

      assertNull(api.get("test"));
   }

   public void testInsertTargetPoolResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/regions/us-central1/targetPools")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/targetpool_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertTargetPoolResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertTargetPoolResponse).targetPoolsInRegion("us-central1");
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions();
      assertEquals(api.create("test", targetPoolCreationOptions), new ParseRegionOperationTest().expected());
   }

   public void testDeleteTargetPoolResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/regions/us-central1/targetPools/test-targetPool")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).targetPoolsInRegion("us-central1");

      assertEquals(api.delete("test-targetPool"),
              new ParseRegionOperationTest().expected());
   }

   public void testDeleteTargetPoolResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/regions/us-central1/targetPools/test-targetPool")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).targetPoolsInRegion("us-central1");

      assertNull(api.delete("test-targetPool"));
   }

   HttpRequest list = HttpRequest
         .builder()
         .method("GET")
         .endpoint(BASE_URL + "/party/regions/us-central1/targetPools")
         .addHeader("Accept", "application/json")
         .addHeader("Authorization", "Bearer " + TOKEN).build();

   public void list() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/targetpool_list.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, response).targetPoolsInRegion("us-central1");

      assertEquals(api.list().next(), new ParseTargetPoolListTest().expected());
   }

   public void listEmpty() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, response).targetPoolsInRegion("us-central1");

      assertFalse(api.list().hasNext());
   }

   public void testAddInstanceResponseIs2xx() throws Exception {
      HttpRequest addInstance = makeGenericRequest("POST", "addInstance", "/targetpool_addinstance.json");
      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addInstance, response).targetPoolsInRegion("us-central1");

      assertEquals(api.addInstance("test", INSTANCES),
              new ParseRegionOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAddInstanceResponseIs4xx() throws Exception {
      HttpRequest addInstance = makeGenericRequest("POST", "addInstance", "/targetpool_addinstance.json");
      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addInstance, response).targetPoolsInRegion("us-central1");

      api.addInstance("test", INSTANCES);
   }
   
   public void testRemoveInstanceResponseIs2xx(){
      HttpRequest removeInstance = makeGenericRequest("POST", "removeInstance", "/targetpool_addinstance.json");
      
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, removeInstance, response).targetPoolsInRegion("us-central1");

      assertEquals(api.removeInstance("test", INSTANCES),
            new ParseRegionOperationTest().expected());
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testRemoveInstanceResponseIs4xx() throws Exception {
      HttpRequest removeInstance = makeGenericRequest("POST", "removeInstance", "/targetpool_addinstance.json");
      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, removeInstance, response).targetPoolsInRegion("us-central1");

      api.removeInstance("test", INSTANCES);
   }
   
   public void testAddHealthCheckResponseIs2xx(){
      HttpRequest addHealthCheck = makeGenericRequest("POST", "addHealthCheck", "/targetpool_changehealthcheck.json");
      
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, addHealthCheck, response).targetPoolsInRegion("us-central1");

      assertEquals(api.addHealthCheck("test", HEALTH_CHECKS), new ParseRegionOperationTest().expected());
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAddHealthCheckResponseIs4xx() throws Exception {
      HttpRequest addHealthCheck = makeGenericRequest("POST", "addHealthCheck", "/targetpool_changehealthcheck.json");
      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addHealthCheck, response).targetPoolsInRegion("us-central1");

      api.addHealthCheck("test", HEALTH_CHECKS);
   }
   
   public void testRemoveHealthCheckResponseIs2xx(){
      HttpRequest removeHealthCheck = makeGenericRequest("POST", "removeHealthCheck", "/targetpool_changehealthcheck.json");
      
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, removeHealthCheck, response).targetPoolsInRegion("us-central1");

      assertEquals(api.removeHealthCheck("test", HEALTH_CHECKS), new ParseRegionOperationTest().expected());
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testRemoveHealthCheckResponseIs4xx() throws Exception {
      HttpRequest removeHealthCheck = makeGenericRequest("POST", "removeHealthCheck", "/targetpool_changehealthcheck.json");
      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, removeHealthCheck, response).targetPoolsInRegion("us-central1");

      api.removeHealthCheck("test", HEALTH_CHECKS);
   }
   
   public void testSetBackupResponseIs2xx(){
      HttpRequest SetBackup = HttpRequest
            .builder()
            .method("POST")
            .endpoint(BASE_URL + "/party/regions/us-central1/targetPools/testpool/setBackup")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/targetpool_setbackup.json", MediaType.APPLICATION_JSON))
            .build();
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, SetBackup, response).targetPoolsInRegion("us-central1");

      assertEquals(api.setBackup("testpool", TARGET_POOL), new ParseRegionOperationTest().expected());
   }
   
   public void testSetBackupWithFailoverRatioResponseIs2xx(){
      HttpRequest SetBackup = HttpRequest
            .builder()
            .method("POST")
            .endpoint(BASE_URL + "/party/regions/"
                    + "us-central1/targetPools/testpool/setBackup?failoverRatio=0.5")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/targetpool_setbackup.json", MediaType.APPLICATION_JSON))
            .build();
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, SetBackup, response).targetPoolsInRegion("us-central1");

      Float failoverRatio = Float.valueOf("0.5");
      assertEquals(api.setBackup("testpool", failoverRatio, TARGET_POOL), new ParseRegionOperationTest().expected());
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testSetBackupResponseIs4xx() throws Exception {
      HttpRequest SetBackup = HttpRequest
            .builder()
            .method("POST")
            .endpoint(BASE_URL + "/party/regions/us-central1/targetPools/testpool/setBackup")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/targetpool_setbackup.json", MediaType.APPLICATION_JSON))
            .build();
      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, SetBackup, response).targetPoolsInRegion("us-central1");

      api.setBackup("testpool", TARGET_POOL);
   }
   
   public HttpRequest makeGenericRequest(String method, String endpoint, String requestPayloadFile){
      HttpRequest request = HttpRequest
            .builder()
            .method(method)
            .endpoint(BASE_URL + "/party/regions/us-central1/targetPools/test/" + endpoint)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType(requestPayloadFile, MediaType.APPLICATION_JSON))
            .build();
      return request;
   }
}
