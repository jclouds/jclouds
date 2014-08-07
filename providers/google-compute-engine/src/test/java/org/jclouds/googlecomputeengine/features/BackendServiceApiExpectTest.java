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

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.parse.ParseBackendServiceGetHealthTest;
import org.jclouds.googlecomputeengine.parse.ParseBackendServiceListTest;
import org.jclouds.googlecomputeengine.parse.ParseBackendServiceTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class BackendServiceApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {
   
   private static final String ENDPOINT_BASE = "https://www.googleapis.com/"
            + "compute/v1/projects/myproject/global/backendServices";
   
   private org.jclouds.http.HttpRequest.Builder<? extends HttpRequest.Builder<?>> getBasicRequest() {
      return HttpRequest.builder().addHeader("Accept", "application/json")
                                  .addHeader("Authorization", "Bearer " + TOKEN);
   }
   
   private HttpResponse createResponse(String payloadFile) {
      return HttpResponse.builder().statusCode(200)
                                   .payload(payloadFromResource(payloadFile))
                                   .build();
   }

   public void testGetBackendServiceResponseIs2xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(ENDPOINT_BASE + "/jclouds-test")
                                             .build();
      HttpResponse response = createResponse("/backend_service_get.json");
      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getBackendServiceApiForProject("myproject");

      assertEquals(api.get("jclouds-test"), new ParseBackendServiceTest().expected());
   }

   public void testGetBackendServiceResponseIs4xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(ENDPOINT_BASE + "/jclouds-test")
                                             .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getBackendServiceApiForProject("myproject");

      assertNull(api.get("jclouds-test"));
   }

   public void testInsertBackendServiceResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("POST")
               .endpoint(ENDPOINT_BASE)
               .payload(payloadFromResourceWithContentType("/backend_service_insert.json",
                                                           MediaType.APPLICATION_JSON))
               .build();
      HttpResponse response = createResponse("/operation.json");

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getBackendServiceApiForProject("myproject");

      URI hc = URI.create("https://www.googleapis.com/compute/v1/projects/"
                           + "myproject/global/httpHealthChecks/jclouds-test");
      assertEquals(api.create("jclouds-test", new BackendServiceOptions().name("jclouds-test")
                                                                         .protocol("HTTP")
                                                                         .port(80)
                                                                         .timeoutSec(30)
                                                                         .addHealthCheck(hc)),
                   new ParseOperationTest().expected());

   }

   public void testUpdateBackendServiceResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("PUT")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .payload(payloadFromResourceWithContentType("/backend_service_insert.json",
                                                           MediaType.APPLICATION_JSON))
               .build();
      HttpResponse response = createResponse("/operation.json");

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getBackendServiceApiForProject("myproject");

      URI hc = URI.create("https://www.googleapis.com/compute/v1/projects/"
                          + "myproject/global/httpHealthChecks/jclouds-test");
      assertEquals(api.update("jclouds-test", new BackendServiceOptions().name("jclouds-test")
                                                                         .protocol("HTTP")
                                                                         .port(80)
                                                                         .timeoutSec(30)
                                                                         .addHealthCheck(hc)),
                   new ParseOperationTest().expected());
   }

   public void testPatchBackendServiceResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("PATCH")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .payload(payloadFromResourceWithContentType("/backend_service_insert.json",
                                                           MediaType.APPLICATION_JSON))
               .build();
      HttpResponse response = createResponse("/operation.json");

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getBackendServiceApiForProject("myproject");

      URI hc = URI.create("https://www.googleapis.com/compute/v1/projects/"
                          + "myproject/global/httpHealthChecks/jclouds-test");
      assertEquals(api.patch("jclouds-test", new BackendServiceOptions().name("jclouds-test")
                                                                         .protocol("HTTP")
                                                                         .port(80)
                                                                         .timeoutSec(30)
                                                                         .addHealthCheck(hc)),
                   new ParseOperationTest().expected());
   }

   public void testDeleteBackendServiceResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();
      HttpResponse response = createResponse("/operation.json");

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getBackendServiceApiForProject("myproject");

      assertEquals(api.delete("jclouds-test"), new ParseOperationTest().expected());
   }

   public void testDeleteBackendServiceResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getBackendServiceApiForProject("myproject");

      assertNull(api.delete("jclouds-test"));
   }

   public void testListBackendServiceResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE)
               .build();
      HttpResponse response = createResponse("/backend_service_list.json");

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).getBackendServiceApiForProject("myproject");

      assertEquals(api.listFirstPage().toString(),
              new ParseBackendServiceListTest().expected().toString());
   }

   public void testListBackendServiceResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE)
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).getBackendServiceApiForProject("myproject");

      assertTrue(api.list().concat().isEmpty());
   }
   
   public void testGetHealthResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("POST")
                                             .endpoint(ENDPOINT_BASE
                                                       + "/jclouds-test/getHealth")
                                             .payload(payloadFromResource("/backend_service_get_health_request.json"))
                                             .build();
      HttpResponse response = createResponse("/backend_service_get_health.json");

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getBackendServiceApiForProject("myproject");

      URI group = URI.create("https://www.googleapis.com/resourceviews/v1beta1/"
                             + "projects/myproject/zones/us-central1-a/"
                             + "resourceViews/jclouds-test");
      assertEquals(api.getHealth("jclouds-test", group), new ParseBackendServiceGetHealthTest().expected());

   }
}
