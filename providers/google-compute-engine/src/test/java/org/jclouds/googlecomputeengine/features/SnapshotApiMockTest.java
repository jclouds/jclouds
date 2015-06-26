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

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.parse.ParseGlobalOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseSnapshotListTest;
import org.jclouds.googlecomputeengine.parse.ParseSnapshotTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "SnapshotApiMockTest", singleThreaded = true)
public class SnapshotApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/snapshot_get.json"));

      assertEquals(snapshotApi().get("test-snap"),
            new ParseSnapshotTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/snapshots/test-snap");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(snapshotApi().get("test-snap"));
      assertSent(server, "GET", "/projects/party/global/snapshots/test-snap");
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/global_operation.json"));

      assertEquals(snapshotApi().delete("test-snap"),
            new ParseGlobalOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/global/snapshots/test-snap");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(snapshotApi().delete("test-snap"));
      assertSent(server, "DELETE", "/projects/party/global/snapshots/test-snap");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/snapshot_list.json"));

      assertEquals(snapshotApi().list().next(), new ParseSnapshotListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/snapshots");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(snapshotApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/snapshots");
   }

   SnapshotApi snapshotApi() {
      return api().snapshots();
   }
}
