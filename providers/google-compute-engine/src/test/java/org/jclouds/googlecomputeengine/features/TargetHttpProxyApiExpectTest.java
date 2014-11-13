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

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetHttpProxyListTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetHttpProxyTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class TargetHttpProxyApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   private static final String ENDPOINT_BASE = "https://www.googleapis.com/"
            + "compute/v1/projects/party/global/targetHttpProxies";

   private org.jclouds.http.HttpRequest.Builder<? extends HttpRequest.Builder<?>> getBasicRequest() {
      return HttpRequest.builder().addHeader("Accept", "application/json")
                                  .addHeader("Authorization", "Bearer " + TOKEN);
   }

   private HttpResponse createResponse(String payloadFile) {
      return HttpResponse.builder().statusCode(200)
                                   .payload(payloadFromResource(payloadFile))
                                   .build();
   }

   public void testGetTargetHttpProxyResponseIs2xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE + "/jclouds-test").build();

      HttpResponse response = createResponse("/target_http_proxy_get.json");

      TargetHttpProxyApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).targetHttpProxies();

      assertEquals(api.get("jclouds-test"),
                   new ParseTargetHttpProxyTest().expected());
   }

   public void testGetTargetHttpProxyResponseIs4xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE + "/jclouds-test").build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      TargetHttpProxyApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).targetHttpProxies();

      assertNull(api.get("jclouds-test"));
   }



   public void testInsertTargetHttpProxyResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("POST")
               .endpoint(ENDPOINT_BASE)
               .payload(payloadFromResourceWithContentType("/target_http_proxy_insert.json",
                                                           MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      TargetHttpProxyApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).targetHttpProxies();

      URI urlMap = URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/urlMaps/jclouds-test");
      assertEquals(api.create("jclouds-test", urlMap), new ParseOperationTest().expected());
   }

   public void testDeleteTargetHttpProxyResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = createResponse("/operation.json");

      TargetHttpProxyApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).targetHttpProxies();

      assertEquals(api.delete("jclouds-test"),
              new ParseOperationTest().expected());
   }

   public void testDeleteTargetHttpProxyResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      TargetHttpProxyApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).targetHttpProxies();

      assertNull(api.delete("jclouds-test"));
   }

   public void testSetUrlMapTargetHttpProxyResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("POST")
               // setUrlMap uses a non-standard url pattern
               .endpoint("https://www.googleapis.com/compute/v1/projects/party/targetHttpProxies"
                         + "/jclouds-test/setUrlMap")
               .payload(payloadFromResourceWithContentType("/target_http_proxy_set_url_map.json",
                                                           MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      TargetHttpProxyApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).targetHttpProxies();

      URI urlMap = URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/urlMaps/jclouds-test");
      assertEquals(api.setUrlMap("jclouds-test", urlMap), new ParseOperationTest().expected());
   }

   public void testListTargetHttpProxiesResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE)
               .build();

      HttpResponse response = createResponse("/target_http_proxy_list.json");

      TargetHttpProxyApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).targetHttpProxies();

      assertEquals(api.list().next(),
              new ParseTargetHttpProxyListTest().expected());
   }

   public void listEmpty() {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint(BASE_URL + "/party/global/targetHttpProxies")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();

      TargetHttpProxyApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, list, response).targetHttpProxies();

      assertFalse(api.list().hasNext());
   }
}
