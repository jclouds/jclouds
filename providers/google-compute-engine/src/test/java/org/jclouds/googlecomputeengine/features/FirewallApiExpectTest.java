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

import java.io.IOException;
import java.net.URI;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "FirewallApiExpectTest")
public class FirewallApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public void testPatchFirewallResponseIs2xx() throws IOException {
      HttpRequest update = HttpRequest
              .builder()
              .method("PATCH")
              .endpoint(BASE_URL + "/party/global/firewalls/myfw")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResource("/firewall_insert.json"))
              .build();

      HttpResponse updateFirewallResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, update,
              updateFirewallResponse).firewalls();

      assertEquals(api.patch("myfw",
              new FirewallOptions()
                      .name("myfw")
                      .network(URI.create(BASE_URL + "/party/global/networks/default"))
                      .addAllowedRule(Firewall.Rule.create("tcp", ImmutableList.of("22", "23-24")))
                      .addSourceTag("tag1")
                      .addSourceRange("10.0.1.0/32")
                      .addTargetTag("tag2")), new ParseOperationTest().expected());
   }
}
