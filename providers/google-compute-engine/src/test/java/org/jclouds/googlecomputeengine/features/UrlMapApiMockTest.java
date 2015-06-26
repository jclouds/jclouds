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

import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher.PathRule;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseUrlMapListTest;
import org.jclouds.googlecomputeengine.parse.ParseUrlMapTest;
import org.jclouds.googlecomputeengine.parse.ParseUrlMapValidateTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "UrlMapApiMockTest", singleThreaded = true)
public class UrlMapApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/url_map_get.json"));

      assertEquals(urlMapApi().get("jclouds-test"), new ParseUrlMapTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/urlMaps/jclouds-test");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(urlMapApi().get("jclouds-test"));
      assertSent(server, "GET", "/projects/party/global/urlMaps/jclouds-test");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(urlMapApi().create(createBasicMap()), new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/global/urlMaps",
            stringFromResource("/url_map_insert.json"));
   }

   public void update() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(urlMapApi().update("jclouds-test", createBasicMap()),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "PUT", "/projects/party/global/urlMaps/jclouds-test",
            stringFromResource("/url_map_insert.json"));
   }

   public void patch() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(urlMapApi().patch("jclouds-test", createBasicMap()),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "PATCH", "/projects/party/global/urlMaps/jclouds-test",
            stringFromResource("/url_map_insert.json"));
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(urlMapApi().delete("jclouds-test"),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/global/urlMaps/jclouds-test");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(urlMapApi().delete("jclouds-test"));
      assertSent(server, "DELETE", "/projects/party/global/urlMaps/jclouds-test");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/url_map_list.json"));

      assertEquals(urlMapApi().list().next(),
            new ParseUrlMapListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/urlMaps");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(urlMapApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/urlMaps");
   }

   public void validate() throws Exception {
      server.enqueue(jsonResponse("/url_map_validate.json"));

      assertEquals(urlMapApi().validate("jclouds-test", createBasicMap()),
            new ParseUrlMapValidateTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/global/urlMaps/jclouds-test/validate");
   }

   private UrlMapOptions createBasicMap() {
      URI service = URI.create(url("/projects/myproject/global/backendServices/jclouds-test"));
      return new UrlMapOptions.Builder().name("jclouds-test")
                                .description("Sample url map")
                                .hostRules(ImmutableList.of(HostRule.create(null, ImmutableList.of("jclouds-test"), "path")))
                                .pathMatchers(ImmutableList.of(PathMatcher.create("path",
                                                                null,
                                                                service,
                                                                ImmutableList.of(
                                                                      PathRule.create(ImmutableList.of("/"),
                                                                                      service)))))
                                .tests(ImmutableList.of(UrlMap.UrlMapTest.create(null, "jclouds-test", "/test/path", service)))
                                .defaultService(service)
                                .build();
   }

   UrlMapApi urlMapApi() {
      return api().urlMaps();
   }
}
