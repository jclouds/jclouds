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
import static org.testng.Assert.assertNull;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineConstants;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseMetadataTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseProjectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;


@Test(groups = "unit")
public class ProjectApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public static final String PROJECTS_URL_PREFIX = "https://www.googleapis.com/compute/v1/projects";

   public static final HttpRequest GET_PROJECT_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint(PROJECTS_URL_PREFIX + "/myproject")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse GET_PROJECT_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/project.json")).build();

   public void testGetProjectResponseIs2xx() throws Exception {
      ProjectApi api = requestsSendResponses(requestForScopes(GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_PROJECT_REQUEST,
              GET_PROJECT_RESPONSE).getProjectApi();

      assertEquals(api.get("myproject"), new ParseProjectTest().expected());
   }

   public void testGetProjectResponseIs4xx() throws Exception {
      HttpRequest getProjectRequest = HttpRequest
              .builder()
              .method("GET")
              .endpoint(PROJECTS_URL_PREFIX + "/myproject")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse getProjectResponse = HttpResponse.builder().statusCode(404).build();

      ProjectApi api = requestsSendResponses(requestForScopes(GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, getProjectRequest,
              getProjectResponse).getProjectApi();

      assertNull(api.get("myproject"));
   }

   public void testSetCommonInstanceMetadata() {
      HttpRequest setMetadata = HttpRequest
              .builder()
              .method("POST")
              .endpoint(PROJECTS_URL_PREFIX + "/myproject/setCommonInstanceMetadata")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/metadata.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/global_operation.json")).build();

      ProjectApi api = requestsSendResponses(requestForScopes(GoogleComputeEngineConstants.COMPUTE_SCOPE),
              TOKEN_RESPONSE, setMetadata,
              setMetadataResponse).getProjectApi();
      Metadata expected = new ParseMetadataTest().expected();
      assertEquals(api.setCommonInstanceMetadata("myproject", expected.getItems(), expected.getFingerprint()),
              new ParseOperationTest().expected());
   }

}
