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
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.options.RouteOptions;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseRouteListTest;
import org.jclouds.googlecomputeengine.parse.ParseRouteTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "RouteApiMockTest", singleThreaded = true)
public class RouteApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/route_get.json"));

      assertEquals(routeApi().get("default-route-c99ebfbed0e1f375"),
            new ParseRouteTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/routes/default-route-c99ebfbed0e1f375");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(routeApi().get("default-route-c99ebfbed0e1f375"));
      assertSent(server, "GET", "/projects/party/global/routes/default-route-c99ebfbed0e1f375");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      assertEquals(routeApi().createInNetwork("default-route-c99ebfbed0e1f375",
            URI.create(url("/projects/party/global/networks/default")),
            new RouteOptions().addTag("fooTag")
                    .addTag("barTag")
                    .description("Default route to the virtual network.")
            .destRange("10.240.0.0/16")
            .priority(1000)
            .nextHopNetwork(URI.create(url("/projects/party/global/networks/default")))),
            new ParseGlobalOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/global/routes",
            stringFromResource("/route_insert.json"));
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      assertEquals(routeApi().delete("default-route-c99ebfbed0e1f375"),
            new ParseGlobalOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/global/routes/default-route-c99ebfbed0e1f375");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(routeApi().delete("default-route-c99ebfbed0e1f375"));
      assertSent(server, "DELETE", "/projects/party/global/routes/default-route-c99ebfbed0e1f375");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/route_list.json"));

      assertEquals(routeApi().list().next(), new ParseRouteListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/routes");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(routeApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/routes");
   }

   RouteApi routeApi() {
      return api().routes();
   }
}
