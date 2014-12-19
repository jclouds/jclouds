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

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.options.TargetHttpProxyOptions;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetHttpProxyListTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetHttpProxyTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "TargetHttpProxyApiMockTest", singleThreaded = true)
public class TargetHttpProxyApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/target_http_proxy_get.json"));

      assertEquals(targetHttpProxyApi().get("jclouds-test"),
            new ParseTargetHttpProxyTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/targetHttpProxies/jclouds-test");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(targetHttpProxyApi().get("jclouds-test"));
      assertSent(server, "GET", "/projects/party/global/targetHttpProxies/jclouds-test");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      URI urlMap = URI.create(url("/projects/myproject/global/urlMaps/jclouds-test"));
      assertEquals(targetHttpProxyApi().create("jclouds-test", urlMap), new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/global/targetHttpProxies",
            stringFromResource("/target_http_proxy_insert.json"));
   }

   public void insert_options() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      URI urlMap = URI.create(url("/projects/myproject/global/urlMaps/jclouds-test"));
      TargetHttpProxyOptions options = new TargetHttpProxyOptions.Builder("jclouds-test", urlMap).description("test").build();
      assertEquals(targetHttpProxyApi().create(options), new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/global/targetHttpProxies",
            "{" +
            "  \"name\": \"jclouds-test\"," +
            "  \"urlMap\": \"" + url("/projects/myproject/global/urlMaps/jclouds-test") + "\"," +
            "  \"description\": \"test\"" +
            "}");
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(targetHttpProxyApi().delete("jclouds-test"),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/global/targetHttpProxies/jclouds-test");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(targetHttpProxyApi().delete("jclouds-test"));
      assertSent(server, "DELETE", "/projects/party/global/targetHttpProxies/jclouds-test");
   }

   public void setUrlMap() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      URI urlMap = URI.create(url("/projects/myproject/global/urlMaps/jclouds-test"));
      assertEquals(targetHttpProxyApi().setUrlMap("jclouds-test", urlMap), new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/targetHttpProxies/jclouds-test/setUrlMap",
            stringFromResource("/target_http_proxy_set_url_map.json"));
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/target_http_proxy_list.json"));

      assertEquals(targetHttpProxyApi().list().next(), new ParseTargetHttpProxyListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/targetHttpProxies");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(targetHttpProxyApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/targetHttpProxies");
   }

   TargetHttpProxyApi targetHttpProxyApi() {
      return api().targetHttpProxies();
   }
}
