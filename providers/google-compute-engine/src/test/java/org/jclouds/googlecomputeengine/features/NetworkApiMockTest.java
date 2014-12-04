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
import org.jclouds.googlecomputeengine.parse.ParseNetworkListTest;
import org.jclouds.googlecomputeengine.parse.ParseNetworkTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "NetworkApiMockTest", singleThreaded = true)
public class NetworkApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/network_get.json"));

      assertEquals(networkApi().get("jclouds-test"),
            new ParseNetworkTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/networks/jclouds-test");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(networkApi().get("jclouds-test"));
      assertSent(server, "GET", "/projects/party/global/networks/jclouds-test");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(networkApi().createInIPv4Range("test-network", "10.0.0.0/8"), new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/global/networks",
            stringFromResource("/network_insert.json"));
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(networkApi().delete("jclouds-test"),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/global/networks/jclouds-test");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(networkApi().delete("jclouds-test"));
      assertSent(server, "DELETE", "/projects/party/global/networks/jclouds-test");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/network_list.json"));

      assertEquals(networkApi().list().next(), new ParseNetworkListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/networks");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(networkApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/networks");
   }

   NetworkApi networkApi(){
      return api().networks();
   }
}
