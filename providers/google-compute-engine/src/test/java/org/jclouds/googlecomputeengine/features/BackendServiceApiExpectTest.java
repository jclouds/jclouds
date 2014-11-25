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
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class BackendServiceApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   private static final String ENDPOINT_BASE = "https://www.googleapis.com/"
            + "compute/v1/projects/party/global/backendServices";

   private org.jclouds.http.HttpRequest.Builder<? extends HttpRequest.Builder<?>> getBasicRequest() {
      return HttpRequest.builder().addHeader("Accept", "application/json")
                                  .addHeader("Authorization", "Bearer " + TOKEN);
   }

   private HttpResponse createResponse(String payloadFile) {
      return HttpResponse.builder().statusCode(200)
                                   .payload(payloadFromResource(payloadFile))
                                   .build();
   }

   public void testPatchBackendServiceResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("PATCH")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .payload(payloadFromResourceWithContentType("/backend_service_insert.json",
                                                           APPLICATION_JSON))
               .build();
      HttpResponse response = createResponse("/operation.json");

      BackendServiceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).backendServices();

      List<URI> healthChecks = ImmutableList.of(URI.create("https://www.googleapis.com/compute/v1/projects/"
                          + "myproject/global/httpHealthChecks/jclouds-test"));
      assertEquals(api.patch("jclouds-test", new BackendServiceOptions().name("jclouds-test")
                                                                         .protocol("HTTP")
                                                                         .port(80)
                                                                         .timeoutSec(30)
                                                                         .healthChecks(healthChecks)),
                   new ParseOperationTest().expected());
   }
}
