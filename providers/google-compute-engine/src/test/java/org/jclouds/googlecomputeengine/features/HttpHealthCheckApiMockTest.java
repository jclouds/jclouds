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

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseHttpHealthCheckListTest;
import org.jclouds.googlecomputeengine.parse.ParseHttpHealthCheckTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "HttpHealthCheckApiMockTest", singleThreaded = true)
public class HttpHealthCheckApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception{
      server.enqueue(jsonResponse("/httphealthcheck_get.json"));

      assertEquals(httpHealthCheckApi().get("http-health-check-api-live-test"),
            new ParseHttpHealthCheckTest().expected(url("/projects")));

      assertSent(server, "GET", "/projects/party/global/httpHealthChecks/http-health-check-api-live-test");
   }

   public void get_4xx() throws Exception{
      server.enqueue(response404());

      assertNull(httpHealthCheckApi().get("http-health-check-api-live-test"));
      assertSent(server, "GET", "/projects/party/global/httpHealthChecks/http-health-check-api-live-test");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions.Builder()
         .timeoutSec(0).unhealthyThreshold(0).buildWithDefaults();
      assertEquals(httpHealthCheckApi().insert("http-health-check", options),
            new ParseGlobalOperationTest().expected(url("/projects")));

      assertSent(server, "POST", "/projects/party/global/httpHealthChecks",
            stringFromResource("/httphealthcheck_insert.json"));
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      assertEquals(httpHealthCheckApi().delete("http-health-check"),
            new ParseGlobalOperationTest().expected(url("/projects")));

      assertSent(server, "DELETE", "/projects/party/global/httpHealthChecks/http-health-check");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(httpHealthCheckApi().delete("http-health-check"));

      assertSent(server, "DELETE", "/projects/party/global/httpHealthChecks/http-health-check");
   }

   public void update() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions.Builder()
         .timeoutSec(0).unhealthyThreshold(0).buildWithDefaults();
      assertEquals(httpHealthCheckApi().update("http-health-check", options),
            new ParseGlobalOperationTest().expected(url("/projects")));

      assertSent(server, "PUT", "/projects/party/global/httpHealthChecks/http-health-check",
            stringFromResource("/httphealthcheck_insert.json"));
   }

   public void patch() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      HttpHealthCheckCreationOptions options = new HttpHealthCheckCreationOptions.Builder()
         .timeoutSec(0).unhealthyThreshold(0).buildForPatch();
      assertEquals(httpHealthCheckApi().patch("http-health-check", options),
            new ParseGlobalOperationTest().expected(url("/projects")));

      assertSent(server, "PATCH", "/projects/party/global/httpHealthChecks/http-health-check",
            stringFromResource("/httphealthcheck_patch.json"));
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/httphealthcheck_list.json"));

      assertEquals(httpHealthCheckApi().list().next(),
            new ParseHttpHealthCheckListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/httpHealthChecks");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(httpHealthCheckApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/httpHealthChecks");
   }


   HttpHealthCheckApi httpHealthCheckApi() {
      return api().httpHeathChecks();
   }
}
