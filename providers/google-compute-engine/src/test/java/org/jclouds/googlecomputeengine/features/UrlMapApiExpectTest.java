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

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher.PathRule;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class UrlMapApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {


   public void patch() throws IOException {
      HttpRequest request = HttpRequest.builder()
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .method("PATCH")
            .endpoint("https://www.googleapis.com/compute/v1/projects"
                     + "/party/global/urlMaps/jclouds-test")
            .payload(payloadFromResourceWithContentType("/url_map_insert.json", MediaType.APPLICATION_JSON))
            .build();

      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/operation.json"))
            .build();

      UrlMapApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).urlMaps();

      assertEquals(api.patch("jclouds-test", createBasicMap()), new ParseOperationTest().expected());
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
