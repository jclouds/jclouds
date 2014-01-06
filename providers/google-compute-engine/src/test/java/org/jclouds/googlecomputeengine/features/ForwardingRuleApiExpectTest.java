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
import org.jclouds.googlecomputeengine.parse.ParseForwardingRuleListTest;
import org.jclouds.googlecomputeengine.parse.ParseForwardingRuleTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import java.net.URI;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

@Test(groups = "unit")
public class ForwardingRuleApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public void testGetForwardingRuleResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/regions/us-central1/forwardingRules/test-forwarding-rule")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/forwardingrule_get.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getForwardingRuleApi("myproject", "us-central1");

      assertEquals(api.get("test-forwarding-rule"),
              new ParseForwardingRuleTest().expected());
   }

   public void testGetForwardingRuleResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/regions/us-central1/forwardingRules/test-forwarding-rule")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getForwardingRuleApi("myproject", "us-central1");

      assertNull(api.get("test-forwarding-rule"));
   }

   public void testInsertForwardingRuleResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/forwardingRules")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/forwardingrule_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertForwardingRuleResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertForwardingRuleResponse).getForwardingRuleApi("myproject", "us-central1");
      assertEquals(api.create("test-forwarding-rule",
              URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/europe-west1/" +
                      "targetPools/test-target-pool")), new ParseRegionOperationTest().expected());
   }

   public void testDeleteForwardingRuleResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/regions/us-central1/forwardingRules/test-forwarding-rule")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getForwardingRuleApi("myproject", "us-central1");

      assertEquals(api.delete("test-forwarding-rule"),
              new ParseRegionOperationTest().expected());
   }

   public void testDeleteForwardingRuleResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/regions/us-central1/forwardingRules/test-targetPool")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getForwardingRuleApi("myproject", "us-central1");

      assertNull(api.delete("test-targetPool"));
   }

   public void testListForwardingRulesResponseIs2xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/forwardingRules")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/forwardingrule_list.json")).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getForwardingRuleApi("myproject", "us-central1");

      assertEquals(api.list().toString(),
              new ParseForwardingRuleListTest().expected().toString());
   }

   public void testListForwardingRulesResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/forwardingRules")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getForwardingRuleApi("myproject", "us-central1");

      assertTrue(api.list().concat().isEmpty());
   }

}
