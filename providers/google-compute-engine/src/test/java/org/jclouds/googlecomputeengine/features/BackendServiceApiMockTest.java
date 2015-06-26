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
import java.util.List;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.parse.ParseHealthStatusTest;
import org.jclouds.googlecomputeengine.parse.ParseBackendServiceListTest;
import org.jclouds.googlecomputeengine.parse.ParseBackendServiceTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "BackendServiceApiMockTest", singleThreaded = true)
public class BackendServiceApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/backend_service_get.json"));

      assertEquals(backendServiceApi().get("jclouds-test"),
            new ParseBackendServiceTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/backendServices/jclouds-test");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(backendServiceApi().get("jclouds-test"));
      assertSent(server, "GET", "/projects/party/global/backendServices/jclouds-test");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      List<URI> healthChecks = ImmutableList.of(URI.create(url("/projects/"
            + "myproject/global/httpHealthChecks/jclouds-test")));

      assertEquals(backendServiceApi().create( new BackendServiceOptions.Builder("jclouds-test", healthChecks)
            .protocol("HTTP")
            .port(80)
            .timeoutSec(30)
            .build()),
            new ParseOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/global/backendServices",
            stringFromResource("/backend_service_insert.json"));
   }

   public void update() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      List<URI> healthChecks = ImmutableList.of(URI.create(url("/projects/"
            + "myproject/global/httpHealthChecks/jclouds-test")));

      assertEquals(backendServiceApi().update("jclouds-test",
            new BackendServiceOptions.Builder("jclouds-test", healthChecks)
               .protocol("HTTP")
               .port(80)
               .timeoutSec(30)
               .build()),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "PUT", "/projects/party/global/backendServices/jclouds-test",
            stringFromResource("/backend_service_insert.json"));
   }

   public void patch() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      List<URI> healthChecks = ImmutableList.of(URI.create(url("/projects/"
            + "myproject/global/httpHealthChecks/jclouds-test")));

      assertEquals(backendServiceApi().patch("jclouds-test",
            new BackendServiceOptions.Builder("jclouds-test", healthChecks)
               .protocol("HTTP")
               .port(80)
               .timeoutSec(30)
               .build()),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "PATCH", "/projects/party/global/backendServices/jclouds-test",
            stringFromResource("/backend_service_insert.json"));
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(backendServiceApi().delete("jclouds-test"),
            new ParseOperationTest().expected(url("/projects")));

      assertSent(server, "DELETE", "/projects/party/global/backendServices/jclouds-test");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(backendServiceApi().delete("jclouds-test"));

      assertSent(server, "DELETE", "/projects/party/global/backendServices/jclouds-test");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/backend_service_list.json"));

      assertEquals(backendServiceApi().list().next(), new ParseBackendServiceListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/backendServices");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(backendServiceApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/backendServices");
   }

   public void getHealth() throws Exception {
      server.enqueue(jsonResponse("/health_status_get_health.json"));

      URI group = URI.create("https://www.googleapis.com/resourceviews/v1beta1/"
            + "projects/myproject/zones/us-central1-a/"
            + "resourceViews/jclouds-test");
      assertEquals(backendServiceApi().getHealth("jclouds-test", group),
            new ParseHealthStatusTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/global/backendServices/jclouds-test/getHealth",
            stringFromResource("/backend_service_get_health_request.json"));
   }

   public BackendServiceApi backendServiceApi(){
      return api().backendServices();
   }
}
