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
import org.jclouds.googlecomputeengine.options.SubnetworkCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseSubnetworkListTest;
import org.jclouds.googlecomputeengine.parse.ParseSubnetworkTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "SubnetworkApiMockTest", singleThreaded = true)
public class SubnetworkApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/subnetwork_get.json"));

      assertEquals(subnetworkApi().get("jclouds-test"),
            new ParseSubnetworkTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions/someregion/subnetworks/jclouds-test");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(subnetworkApi().get("jclouds-test"));
      assertSent(server, "GET", "/projects/party/regions/someregion/subnetworks/jclouds-test");
   }

   public void insert() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(subnetworkApi().createInNetwork(SubnetworkCreationOptions.create(
              "jclouds-test",
              "my subnetwork",
              URI.create(url("/projects/party/global/networks/mynetwork")),
              "10.0.0.0/24",
              URI.create(url("/projects/party/regions/someregion")))), new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "POST", "/projects/party/regions/someregion/subnetworks",
            stringFromResource("/subnetwork_insert.json"));
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(subnetworkApi().delete("jclouds-test"),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/regions/someregion/subnetworks/jclouds-test");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(subnetworkApi().delete("jclouds-test"));
      assertSent(server, "DELETE", "/projects/party/regions/someregion/subnetworks/jclouds-test");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/subnetwork_list.json"));

      assertEquals(subnetworkApi().list().next(), new ParseSubnetworkListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/regions/someregion/subnetworks");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(subnetworkApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/regions/someregion/subnetworks");
   }

   SubnetworkApi subnetworkApi() {
      return api().subnetworksInRegion("someregion");
   }
}
