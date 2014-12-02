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

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.transform;
import static java.lang.String.format;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "FirewallApiExpectTest")
public class FirewallApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public static final HttpRequest GET_FIREWALL_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint(BASE_URL + "/party/global/firewalls/jclouds-test")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static HttpResponse GET_FIREWALL_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/firewall_get.json")).build();

   public static Payload firewallPayloadFirewallOfName(String firewallName,
                                                       String networkName,
                                                       List<String> sourceRanges,
                                                       List<String> sourceTags,
                                                       List<String> targetTags,
                                                       List<String> portRanges) throws IOException {
      Function<String, String> addQuotes = new Function<String, String>() {
         @Override
         public String apply(String input) {
            return "\"" + input + "\"";
         }
      };

      String ports = on(",").skipNulls().join(transform(portRanges, addQuotes));

      Payload payload = newStringPayload(
              format(toStringAndClose(FirewallApiExpectTest.class.getResourceAsStream("/firewall_insert.json")),
                      firewallName,
                      networkName,
                      on(",").skipNulls().join(transform(sourceRanges, addQuotes)),
                      on(",").skipNulls().join(transform(sourceTags, addQuotes)),
                      on(",").skipNulls().join(transform(targetTags, addQuotes)),
                      ports,
                      ports));
      payload.getContentMetadata().setContentType(MediaType.APPLICATION_JSON);
      return payload;
   }

   //TODO (broudy): convert to mock test and add description to test.
   public void testInsertFirewallResponseIs2xx() throws IOException {

      HttpRequest request = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/global/firewalls")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(firewallPayloadFirewallOfName(
                      "myfw",
                      "default",
                      ImmutableList.of("10.0.1.0/32"),
                      ImmutableList.of("tag1"),
                      ImmutableList.of("tag2"),
                      ImmutableList.of("22", "23-24")))
              .build();

      HttpResponse insertFirewallResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, insertFirewallResponse).firewalls();

      assertEquals(api.createInNetwork("myfw", URI.create(BASE_URL + "/party/global/networks/default"),
              new FirewallOptions()
                      .addAllowedRule(Firewall.Rule.create("tcp", ImmutableList.of("22", "23-24")))
                      .addSourceTag("tag1")
                      .addSourceRange("10.0.1.0/32")
                      .addTargetTag("tag2")), new ParseOperationTest().expected());

   }

   public void testUpdateFirewallResponseIs2xx() throws IOException {
      HttpRequest update = HttpRequest
              .builder()
              .method("PUT")
              .endpoint(BASE_URL + "/party/global/firewalls/myfw")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(firewallPayloadFirewallOfName(
                      "myfw",
                      "default",
                      ImmutableList.of("10.0.1.0/32"),
                      ImmutableList.of("tag1"),
                      ImmutableList.of("tag2"),
                      ImmutableList.of("22", "23-24")))
              .build();

      HttpResponse updateFirewallResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      FirewallApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, update,
              updateFirewallResponse).firewalls();

      assertEquals(api.update("myfw",
              new FirewallOptions()
                      .name("myfw")
                      .network(URI.create(BASE_URL + "/party/global/networks/default"))
                      .addAllowedRule(Firewall.Rule.create("tcp", ImmutableList.of("22", "23-24")))
                      .addSourceTag("tag1")
                      .addSourceRange("10.0.1.0/32")
                      .addTargetTag("tag2")), new ParseOperationTest().expected());
   }

   public void testPatchFirewallResponseIs2xx() throws IOException {
      HttpRequest update = HttpRequest
              .builder()
              .method("PATCH")
              .endpoint(BASE_URL + "/party/global/firewalls/myfw")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(firewallPayloadFirewallOfName(
                      "myfw",
                      "default",
                      ImmutableList.of("10.0.1.0/32"),
                      ImmutableList.of("tag1"),
                      ImmutableList.of("tag2"),
                      ImmutableList.of("22", "23-24")))
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
