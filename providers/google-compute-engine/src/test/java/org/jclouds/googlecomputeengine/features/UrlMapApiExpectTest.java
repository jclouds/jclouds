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

import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathRule;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseUrlMapListTest;
import org.jclouds.googlecomputeengine.parse.ParseUrlMapTest;
import org.jclouds.googlecomputeengine.parse.ParseUrlMapValidateTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class UrlMapApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   private static final String ENDPOINT_BASE = "https://www.googleapis.com/"
            + "compute/v1/projects/myproject/global/urlMaps";
   
   private org.jclouds.http.HttpRequest.Builder<? extends HttpRequest.Builder<?>> getBasicRequest() {
      return HttpRequest.builder().addHeader("Accept", "application/json")
                                  .addHeader("Authorization", "Bearer " + TOKEN);
   }
   
   private HttpResponse createResponse(String payloadFile) {
      return HttpResponse.builder().statusCode(200)
                                   .payload(payloadFromResource(payloadFile))
                                   .build();
   }

   public void testGetUrlMapResponseIs2xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();
      
      HttpResponse response = createResponse("/url_map_get.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");

      assertEquals(api.get("jclouds-test"), new ParseUrlMapTest().expected());
   }

   public void testGetUrlMapResponseIs4xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");

      assertNull(api.get("jclouds-test"));
   }

   public void testInsertUrlMapResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("POST")
               .endpoint(ENDPOINT_BASE)
               .payload(payloadFromResourceWithContentType("/url_map_insert.json", MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");
      
      assertEquals(api.create("jclouds-test", createBasicMap()), new ParseOperationTest().expected());

   }

   public void testUpdateUrlMapResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("PUT")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .payload(payloadFromResourceWithContentType("/url_map_insert.json", MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");
      
      assertEquals(api.update("jclouds-test", createBasicMap()), new ParseOperationTest().expected());
   }

   public void testPatchUrlMapResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("PATCH")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .payload(payloadFromResourceWithContentType("/url_map_insert.json", MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");

      assertEquals(api.patch("jclouds-test", createBasicMap()), new ParseOperationTest().expected());
   }

   public void testDeleteUrlMapResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = createResponse("/operation.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");

      assertEquals(api.delete("jclouds-test"), new ParseOperationTest().expected());
   }

   public void testDeleteUrlMapResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");

      assertNull(api.delete("jclouds-test"));
   }

   public void testListUrlMapsResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE)
               .build();

      HttpResponse response = createResponse("/url_map_list.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");

      assertEquals(api.listFirstPage().toString(),
              new ParseUrlMapListTest().expected().toString());
   }

   public void testListUrlMapsResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE)
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");

      assertTrue(api.list().concat().isEmpty());
   }
   
   public void testValidateUrlMapsResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("POST")
               .endpoint(ENDPOINT_BASE + "/jclouds-test/validate")
               .payload(payloadFromResourceWithContentType("/url_map_validate_request.json",
                                                           MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/url_map_validate.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getUrlMapApiForProject("myproject");

      assertEquals(api.validate("jclouds-test", createBasicMap()), new ParseUrlMapValidateTest().expected());
   }
   
   private UrlMapOptions createBasicMap() {
      URI service = URI.create("https://www.googleapis.com/compute/v1/projects/"
               + "myproject/global/backendServices/jclouds-test");
      return new UrlMapOptions().name("jclouds-test")
                                .description("Sample url map")
                                .addHostRule(HostRule.builder().addHost("jclouds-test")
                                                               .pathMatcher("path")
                                                               .build())
                                .addPathMatcher(PathMatcher.builder()
                                                           .name("path")
                                                           .defaultService(service)
                                                           .addPathRule(PathRule.builder()
                                                                                .addPath("/")
                                                                                .service(service)
                                                                                .build())
                                                           .build())
                                .addTest(UrlMap.UrlMapTest.builder().host("jclouds-test")
                                                              .path("/test/path")
                                                              .service(service)
                                                              .build())
                                .defaultService(service);
   }
}
