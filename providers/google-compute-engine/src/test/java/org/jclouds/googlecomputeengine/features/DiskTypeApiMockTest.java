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
import org.jclouds.googlecomputeengine.parse.ParseDiskTypeListTest;
import org.jclouds.googlecomputeengine.parse.ParseDiskTypeTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "DiskTypeApiMockTest", singleThreaded = true)
public class DiskTypeApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/disktype.json"));

      assertEquals(diskTypeApi().get("pd-standard"),
            new ParseDiskTypeTest().expected());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/diskTypes/pd-standard");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(diskTypeApi().get("pd-standard"));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/diskTypes/pd-standard");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/disktype_list.json"));

      assertEquals(diskTypeApi().list().next(), new ParseDiskTypeListTest().expected());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/diskTypes");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(diskTypeApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/diskTypes");
   }

   public DiskTypeApi diskTypeApi() {
      return api().diskTypesInZone("us-central1-a");
   }
}
