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
import org.jclouds.googlecomputeengine.parse.ParseFirewallListTest;
import org.jclouds.googlecomputeengine.parse.ParseFirewallTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "FirewallApiMockTest", singleThreaded = true)
public class FirewallApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/firewall_get.json"));

      assertEquals(firewallApi().get("jclouds-test"),
            new ParseFirewallTest().expected(url("/projects")));

      assertSent(server, "GET", "/projects/party/global/firewalls/jclouds-test");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(firewallApi().get("jclouds-test"));

      assertSent(server, "GET", "/projects/party/global/firewalls/jclouds-test");
   }

   public void delete() throws Exception {
      server.enqueue(jsonResponse("/operation.json"));

      assertEquals(firewallApi().delete("default-allow-internal"),
            new ParseOperationTest().expected(url("/projects")));
      assertSent(server, "DELETE", "/projects/party/global/firewalls/default-allow-internal");
   }

   public void delete_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(firewallApi().delete("default-allow-internal"));
      assertSent(server, "DELETE", "/projects/party/global/firewalls/default-allow-internal");
   }

   public void list() throws Exception {
      server.enqueue(jsonResponse("/firewall_list.json"));

      assertEquals(firewallApi().list().next(), new ParseFirewallListTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/party/global/firewalls");
   }

   public void list_empty() throws Exception {
      server.enqueue(jsonResponse("/list_empty.json"));

      assertFalse(firewallApi().list().hasNext());
      assertSent(server, "GET", "/projects/party/global/firewalls");
   }

   FirewallApi firewallApi(){
      return api().firewalls();
   }

}
