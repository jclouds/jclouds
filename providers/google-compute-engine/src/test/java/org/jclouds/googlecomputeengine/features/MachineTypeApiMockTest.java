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
import org.jclouds.googlecomputeengine.parse.ParseMachineTypeListTest;
import org.jclouds.googlecomputeengine.parse.ParseMachineTypeTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "MachineTypeApiMockTest", singleThreaded = true)
public class MachineTypeApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception{
      server.enqueue(jsonResponse("/machinetype.json"));

      assertEquals(machineTypeApi().get("n1-standard-1"),
            new ParseMachineTypeTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/machineTypes/n1-standard-1");
   }

   public void get_4xx() throws Exception{
      server.enqueue(response404());

      assertNull(machineTypeApi().get("n1-standard-1"));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/machineTypes/n1-standard-1");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/machinetype_list.json"));

      assertEquals(machineTypeApi().list().next(), new ParseMachineTypeListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/machineTypes");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(machineTypeApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/machineTypes");
   }

   MachineTypeApi machineTypeApi() {
      return api().machineTypesInZone("us-central1-a");
   }
}
