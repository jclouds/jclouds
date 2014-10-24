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

import java.net.URI;
import java.util.Set;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseRegionOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolListTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import javax.ws.rs.core.MediaType;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

@Test(groups = "unit")
public class TargetPoolApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   private static final Set<URI> INSTANCES = ImmutableSet.of(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/zones/europe-west1-a/instances/test"));
   
   private static final Set<URI> HEALTH_CHECKS = ImmutableSet.of(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/httpHealthChecks/health-check-1"));
   
   private static final URI TARGET_POOL = URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/tpool");
   
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
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions();
      assertEquals(api.create("test", targetPoolCreationOptions), new ParseRegionOperationTest().expected());
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

      ListOptions options = new ListOptions();
      assertEquals(api.list(options).toString(),
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
      HttpRequest addInstance = makeGenericRequest("POST", "addInstance", "/targetpool_addinstance.json");
      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addInstance, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertEquals(api.addInstance("test", INSTANCES),
              new ParseRegionOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAddInstanceResponseIs4xx() throws Exception {
      HttpRequest addInstance = makeGenericRequest("POST", "addInstance", "/targetpool_addinstance.json");
      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addInstance, operationResponse).getTargetPoolApi("myproject", "us-central1");

      api.addInstance("test", INSTANCES);
   }
   
   public void testRemoveInstanceResponseIs2xx(){
      HttpRequest removeInstance = makeGenericRequest("POST", "removeInstance", "/targetpool_addinstance.json");
      
      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, removeInstance, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertEquals(api.removeInstance("test", INSTANCES),
            new ParseRegionOperationTest().expected());
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testRemoveInstanceResponseIs4xx() throws Exception {
      HttpRequest removeInstance = makeGenericRequest("POST", "removeInstance", "/targetpool_addinstance.json");
      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, removeInstance, operationResponse).getTargetPoolApi("myproject", "us-central1");

      api.removeInstance("test", INSTANCES);
   }
   
   public void testAddHealthCheckResponseIs2xx(){
      HttpRequest addHealthCheck = makeGenericRequest("POST", "addHealthCheck", "/targetpool_changehealthcheck.json");
      
      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, addHealthCheck, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertEquals(api.addHealthCheck("test", HEALTH_CHECKS),
            new ParseRegionOperationTest().expected());
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAddHealthCheckResponseIs4xx() throws Exception {
      HttpRequest addHealthCheck = makeGenericRequest("POST", "addHealthCheck", "/targetpool_changehealthcheck.json");
      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addHealthCheck, operationResponse).getTargetPoolApi("myproject", "us-central1");

      api.addHealthCheck("test", HEALTH_CHECKS);
   }
   
   public void testRemoveHealthCheckResponseIs2xx(){
      HttpRequest removeHealthCheck = makeGenericRequest("POST", "removeHealthCheck", "/targetpool_changehealthcheck.json");
      
      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, removeHealthCheck, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertEquals(api.removeHealthCheck("test", HEALTH_CHECKS),
            new ParseRegionOperationTest().expected());
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testRemoveHealthCheckResponseIs4xx() throws Exception {
      HttpRequest removeHealthCheck = makeGenericRequest("POST", "removeHealthCheck", "/targetpool_changehealthcheck.json");
      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, removeHealthCheck, operationResponse).getTargetPoolApi("myproject", "us-central1");

      api.removeHealthCheck("test", HEALTH_CHECKS);
   }
   
   public void testSetBackupResponseIs2xx(){
      HttpRequest setBackup = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/testpool/setBackup")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/targetpool_setbackup.json", MediaType.APPLICATION_JSON))
            .build();
      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, setBackup, operationResponse).getTargetPoolApi("myproject", "us-central1");

      assertEquals(api.setBackup("testpool", TARGET_POOL ),
            new ParseRegionOperationTest().expected());
   }
   
   public void testSetBackupWithFailoverRatioResponseIs2xx(){
      HttpRequest setBackup = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/"
                    + "us-central1/targetPools/testpool/setBackup?failoverRatio=0.5")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/targetpool_setbackup.json", MediaType.APPLICATION_JSON))
            .build();
      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, setBackup, operationResponse).getTargetPoolApi("myproject", "us-central1");

      Float failoverRatio = Float.valueOf("0.5");
      assertEquals(api.setBackup("testpool", failoverRatio, TARGET_POOL ),
            new ParseRegionOperationTest().expected());
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testSetBackupResponseIs4xx() throws Exception {
      HttpRequest setBackup = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/testpool/setBackup")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/targetpool_setbackup.json", MediaType.APPLICATION_JSON))
            .build();
      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, setBackup, operationResponse).getTargetPoolApi("myproject", "us-central1");

      api.setBackup("testpool", TARGET_POOL );
   }
   
   public HttpRequest makeGenericRequest(String method, String endpoint, String requestPayloadFile){
      HttpRequest request = HttpRequest
            .builder()
            .method(method)
            .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test/" + endpoint)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType(requestPayloadFile, MediaType.APPLICATION_JSON))
            .build();
      return request;
   }
}
