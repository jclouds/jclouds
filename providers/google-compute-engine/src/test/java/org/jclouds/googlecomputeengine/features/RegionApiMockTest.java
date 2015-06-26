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

import org.jclouds.googlecomputeengine.parse.ParseRegionListTest;
import org.jclouds.googlecomputeengine.parse.ParseRegionTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "RegionApiMockTest", singleThreaded = true)
public class RegionApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/region_get.json"));

      assertEquals(regionApi().get("us-central1"), new ParseRegionTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions/us-central1");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(regionApi().get("us-central1"));
      assertSent(server, "GET", "/projects/party/regions/us-central1");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/region_list.json"));

      assertEquals(regionApi().list().next(), new ParseRegionListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(regionApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/regions");
   }

   RegionApi regionApi() {
      return api().regions();
   }
}
