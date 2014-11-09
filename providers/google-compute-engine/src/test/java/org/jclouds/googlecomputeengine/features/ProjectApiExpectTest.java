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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineScopes.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineScopes.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseMetadataTest;
import org.jclouds.googlecomputeengine.parse.ParseProjectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ProjectApiExpectTest")
public class ProjectApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public static final HttpRequest GET_PROJECT_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint(BASE_URL + "/party")
           .addHeader("Accept", APPLICATION_JSON)
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse GET_PROJECT_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/project.json")).build();

   public void testGetProjectResponseIs2xx() throws Exception {
      ProjectApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE,
            GET_PROJECT_REQUEST, GET_PROJECT_RESPONSE).project();

      assertEquals(api.get(), new ParseProjectTest().expected());
   }

   public void testGetProjectResponseIs4xx() throws Exception {
      HttpRequest getProjectRequest = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party")
              .addHeader("Accept", APPLICATION_JSON)
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse getProjectResponse = HttpResponse.builder().statusCode(404).build();

      ProjectApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE,
            getProjectRequest, getProjectResponse).project();

      assertNull(api.get());
   }

   public void testSetCommonInstanceMetadata() {
      HttpRequest setMetadata = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/setCommonInstanceMetadata")
              .addHeader("Accept", APPLICATION_JSON)
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/metadata.json", APPLICATION_JSON))
              .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/global_operation.json")).build();

      ProjectApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE), TOKEN_RESPONSE, setMetadata,
            setMetadataResponse).project();

      Metadata expected = new ParseMetadataTest().expected();
      assertEquals(api.setCommonInstanceMetadata(expected), new ParseGlobalOperationTest().expected());
   }
}
