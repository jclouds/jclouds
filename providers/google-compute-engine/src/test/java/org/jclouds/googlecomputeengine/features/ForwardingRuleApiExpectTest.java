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

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseForwardingRuleListTest;
import org.jclouds.googlecomputeengine.parse.ParseForwardingRuleTest;
import org.jclouds.googlecomputeengine.parse.ParseRegionOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ForwardingRuleApiExpectTest")
public class ForwardingRuleApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public void testGetForwardingRuleResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
            .builder()
            .method("GET")
            .endpoint(BASE_URL + "/party/regions/us-central1/forwardingRules/test-forwarding-rule")
                  .addHeader("Accept", "application/json")
                  .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/forwardingrule_get.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, get, response).forwardingRulesInRegion("us-central1");

      assertEquals(api.get("test-forwarding-rule"), new ParseForwardingRuleTest().expected());
   }

   public void testGetForwardingRuleResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
            .builder()
            .method("GET")
            .endpoint(BASE_URL + "/party/regions/us-central1/forwardingRules/test-forwarding-rule")
                  .addHeader("Accept", "application/json")
                  .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, get, response).forwardingRulesInRegion("us-central1");

      assertNull(api.get("test-forwarding-rule"));
   }

   public void testInsertForwardingRuleResponseIs2xx() {
      HttpRequest insert = HttpRequest
            .builder()
            .method("POST")
            .endpoint(BASE_URL + "/party/regions/us-central1/forwardingRules")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/forwardingrule_insert.json", MediaType.APPLICATION_JSON))
            .build();

      HttpResponse insertForwardingRuleResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, insert,
            insertForwardingRuleResponse).forwardingRulesInRegion("us-central1");

      ForwardingRuleCreationOptions forwardingRuleCreationOptions = new ForwardingRuleCreationOptions()
      .target(URI.create(BASE_URL + "/party/regions/europe-west1/targetPools/test-target-pool"));
      assertEquals(api.create("test-forwarding-rule", forwardingRuleCreationOptions),
            new ParseRegionOperationTest().expected());
   }

   public void testDeleteForwardingRuleResponseIs2xx() {
      HttpRequest delete = HttpRequest
            .builder()
            .method("DELETE")
            .endpoint(BASE_URL + "/party/regions/us-central1/forwardingRules/test-forwarding-rule")
                  .addHeader("Accept", "application/json")
                  .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, delete, deleteResponse).forwardingRulesInRegion("us-central1");

      assertEquals(api.delete("test-forwarding-rule"), new ParseRegionOperationTest().expected());
   }

   public void testDeleteForwardingRuleResponseIs4xx() {
      HttpRequest delete = HttpRequest
            .builder()
            .method("DELETE")
            .endpoint(BASE_URL + "/party/regions/us-central1/forwardingRules/test-targetPool")
                  .addHeader("Accept", "application/json")
                  .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, delete, deleteResponse).forwardingRulesInRegion("us-central1");

      assertNull(api.delete("test-targetPool"));
   }

   HttpRequest list = HttpRequest
         .builder()
         .method("GET")
         .endpoint(BASE_URL + "/party/regions/us-central1/forwardingRules")
         .addHeader("Accept", "application/json")
         .addHeader("Authorization", "Bearer " + TOKEN).build();

   public void list() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/forwardingrule_list.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, list, response).forwardingRulesInRegion("us-central1");

      assertEquals(api.list().next(), new ParseForwardingRuleListTest().expected());
   }

   public void listEmpty() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, list, response).forwardingRulesInRegion("us-central1");

      assertFalse(api.list().hasNext());
   }

   public void testSetTargetForwardingRuleResponseIs2xx(){
      String ruleName = "testForwardingRule";
      HttpRequest setTarget = HttpRequest
            .builder()
            .method("POST")
            .endpoint(BASE_URL + "/party/regions/us-central1/forwardingRules/" + ruleName + "/setTarget")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/forwardingrule_set_target.json", MediaType.APPLICATION_JSON))
            .build();

      HttpResponse setTargetResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/region_operation.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, setTarget, setTargetResponse).forwardingRulesInRegion("us-central1");

      URI newTarget = URI.create(BASE_URL + "/party/regions/europe-west1/targetPools/test-target-pool");
      assertEquals(api.setTarget(ruleName, newTarget), new ParseRegionOperationTest().expected());
   }

}
