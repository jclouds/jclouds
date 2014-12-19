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
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseHealthStatusTest;
import org.jclouds.googlecomputeengine.parse.ParseRegionOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolListTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "TargetPoolApiMockTest", singleThreaded = true)
public class TargetPoolApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/targetpool_get.json"));

      assertEquals(targetPoolApi().get("test"),
            new ParseTargetPoolTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions/us-central1/targetPools/test");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(targetPoolApi().get("test"));
      assertSent(server, "GET", "/projects/party/regions/us-central1/targetPools/test");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions.Builder("test").build();
      assertEquals(targetPoolApi().create(targetPoolCreationOptions),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/us-central1/targetPools",
            stringFromResource("/targetpool_insert.json"));
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      assertEquals(targetPoolApi().delete("test-targetPool"),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/regions/us-central1/targetPools/test-targetPool");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(targetPoolApi().delete("test-targetPool"));
      assertSent(server, "DELETE", "/projects/party/regions/us-central1/targetPools/test-targetPool");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/targetpool_list.json"));

      assertEquals(targetPoolApi().list().next(), new ParseTargetPoolListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions/us-central1/targetPools");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(targetPoolApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/regions/us-central1/targetPools");
   }

   public void addInstance() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      List<URI> instances = ImmutableList
            .of(URI.create(url("/projects/party/zones/europe-west1-a/instances/test")));

      assertEquals(targetPoolApi().addInstance("test", instances),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/us-central1/targetPools/test/addInstance",
            stringFromResource("/targetpool_addinstance.json"));
   }

   public void removeInstance() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      List<URI> instances = ImmutableList
            .of(URI.create(url("/projects/party/zones/europe-west1-a/instances/test")));

      assertEquals(targetPoolApi().removeInstance("test", instances),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/us-central1/targetPools/test/removeInstance",
            stringFromResource("/targetpool_addinstance.json"));
   }

   public void addHealthCheck() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      List<URI> healthChecks = ImmutableList
            .of(URI.create(url("/projects/party/global/httpHealthChecks/health-check-1")));

      assertEquals(targetPoolApi().addHealthCheck("test", healthChecks),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/us-central1/targetPools/test/addHealthCheck",
            stringFromResource("/targetpool_changehealthcheck.json"));
   }

   public void removeHealthCheck() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      List<URI> healthChecks = ImmutableList
            .of(URI.create(url("/projects/party/global/httpHealthChecks/health-check-1")));

      assertEquals(targetPoolApi().removeHealthCheck("test", healthChecks),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/us-central1/targetPools/test/removeHealthCheck",
            stringFromResource("/targetpool_changehealthcheck.json"));
   }

   public void setBackup() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      URI backup = URI.create(url("/projects/party/regions/us-central1/targetPools/tpool"));

      assertEquals(targetPoolApi().setBackup("test", backup),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/us-central1/targetPools/test/setBackup",
            stringFromResource("/targetpool_setbackup.json"));
   }

   public void setBackup_FailoverRatio() throws Exception {
      server.enqueue(jsonResponse("/region_operation.json"));

      URI backup = URI.create(url("/projects/party/regions/us-central1/targetPools/tpool"));

      Float failoverRatio = Float.valueOf("0.5");
      assertEquals(targetPoolApi().setBackup("test", failoverRatio, backup),
            new ParseRegionOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/us-central1/targetPools/"
            + "test/setBackup?failoverRatio=0.5",
            stringFromResource("/targetpool_setbackup.json"));
   }

   public void getHealth() throws Exception {
      server.enqueue(jsonResponse("/health_status_get_health.json"));

      URI instance = URI.create(url("/party/zones/us-central1-a/instances/jclouds-test"));
      assertEquals(targetPoolApi().getHealth("test-pool", instance),
            new ParseHealthStatusTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/us-central1/targetPools/test-pool/getHealth",
            "{\"instance\": \"" + instance.toString() + "\"}");
   }

   public TargetPoolApi targetPoolApi() {
      return api().targetPoolsInRegion("us-central1");
   }
}
