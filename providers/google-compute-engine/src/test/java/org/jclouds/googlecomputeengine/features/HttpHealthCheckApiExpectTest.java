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

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "HttpHealthCheckApiExpectTest")
public class HttpHealthCheckApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public void testPatchHttpHealthChecksResponseIs2xx() {
      String healthCheckName = "http-health-check";
      HttpRequest patch = HttpRequest
            .builder()
            .method("PATCH")
            .endpoint(BASE_URL + "/party/global/httpHealthChecks/" + healthCheckName)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/httphealthcheck_insert.json", MediaType.APPLICATION_JSON))
            .build();

       HttpResponse insertHttpHealthCheckResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResource("/global_operation.json")).build();

       HttpHealthCheckApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
               TOKEN_RESPONSE, patch,
               insertHttpHealthCheckResponse).httpHeathChecks();
       HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions().timeoutSec(0).unhealthyThreshold(0);
       assertEquals(api.patch(healthCheckName, options), new ParseGlobalOperationTest().expected());
   }

}
