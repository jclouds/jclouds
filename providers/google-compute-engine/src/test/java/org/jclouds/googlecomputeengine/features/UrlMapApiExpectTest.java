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

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher.PathRule;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseUrlMapListTest;
import org.jclouds.googlecomputeengine.parse.ParseUrlMapTest;
import org.jclouds.googlecomputeengine.parse.ParseUrlMapValidateTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class UrlMapApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   private static final String ENDPOINT_BASE = "https://www.googleapis.com/"
            + "compute/v1/projects/party/global/urlMaps";

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
              TOKEN_RESPONSE, request, response).urlMaps();

      assertEquals(api.get("jclouds-test"), new ParseUrlMapTest().expected());
   }

   public void testGetUrlMapResponseIs4xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).urlMaps();

      assertNull(api.get("jclouds-test"));
   }

   public void testInsertUrlMapResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("POST")
               .endpoint(ENDPOINT_BASE)
               .payload(payloadFromResourceWithContentType("/url_map_insert.json", MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).urlMaps();

      assertEquals(api.create(createBasicMap()), new ParseOperationTest().expected());

   }

   public void testUpdateUrlMapResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("PUT")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .payload(payloadFromResourceWithContentType("/url_map_insert.json", MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).urlMaps();

      assertEquals(api.update("jclouds-test", createBasicMap()), new ParseOperationTest().expected());
   }

   public void testPatchUrlMapResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("PATCH")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .payload(payloadFromResourceWithContentType("/url_map_insert.json", MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).urlMaps();

      assertEquals(api.patch("jclouds-test", createBasicMap()), new ParseOperationTest().expected());
   }

   public void testDeleteUrlMapResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = createResponse("/operation.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).urlMaps();

      assertEquals(api.delete("jclouds-test"), new ParseOperationTest().expected());
   }

   public void testDeleteUrlMapResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).urlMaps();

      assertNull(api.delete("jclouds-test"));
   }

   HttpRequest list = HttpRequest
         .builder()
         .method("GET")
         .endpoint(ENDPOINT_BASE)
         .addHeader("Accept", "application/json")
         .addHeader("Authorization", "Bearer " + TOKEN).build();

   public void list() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/url_map_list.json")).build();

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, response).urlMaps();

      assertEquals(api.list().next(), new ParseUrlMapListTest().expected());
   }

   public void listEmpty() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, response).urlMaps();

      assertFalse(api.list().hasNext());
   }

   public void testValidateUrlMapsResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("POST")
               .endpoint(ENDPOINT_BASE + "/jclouds-test/validate")
               .payload(payloadFromResourceWithContentType("/url_map_validate_request.json",
                                                           MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/url_map_validate.json");

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).urlMaps();

      assertEquals(api.validate("jclouds-test", createBasicMap()), new ParseUrlMapValidateTest().expected());
   }

   private UrlMapOptions createBasicMap() {
      URI service = URI.create("https://www.googleapis.com/compute/v1/projects/"
               + "myproject/global/backendServices/jclouds-test");
      return new UrlMapOptions().name("jclouds-test")
                                .description("Sample url map")
                                .hostRule(HostRule.create(null, ImmutableList.of("jclouds-test"), "path"))
                                .pathMatcher(PathMatcher.create("path",
                                                                null,
                                                                service,
                                                                ImmutableList.of(
                                                                      PathRule.create(ImmutableList.of("/"),
                                                                                      service))))
                                .test(UrlMap.UrlMapTest.create(null, "jclouds-test", "/test/path", service))
                                .defaultService(service);
   }
}
