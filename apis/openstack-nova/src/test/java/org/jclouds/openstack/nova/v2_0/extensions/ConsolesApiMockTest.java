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
package org.jclouds.openstack.nova.v2_0.extensions;

import static com.google.common.collect.Iterables.getFirst;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Console;
import org.jclouds.openstack.nova.v2_0.parse.ParseNOVNCConsoleTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseRDPConsoleTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseSPICEConsoleTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseXVPVNCConsoleTest;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Tests ConsolesApi Guice wiring and parsing
 */
@Test(groups = "unit", testName = "ConsolesApiMockTest", enabled = false)
public class ConsolesApiMockTest extends BaseOpenStackMockTest<NovaApi> {

   public void testNullConsoleType() {
      assertNull(Console.Type.fromValue(null));
   }

   public void testUnrecognizedConsoleType() {
      assertEquals(Console.Type.UNRECOGNIZED, Console.Type.fromValue("invalid type"));
   }

   public void getNOVNCConsole() throws Exception {
      getConsole(Console.Type.NOVNC, "/novnc_console.json", new ParseNOVNCConsoleTest().expected());
   }

   public void getNOVNCConsoleWhenResponseIs404NotFound() throws Exception {
      getConsoleWhenResponseIs404NotFound(Console.Type.NOVNC);
   }

   public void getXVPVNCConsole() throws Exception {
      getConsole(Console.Type.XVPVNC, "/xvpvnc_console.json", new ParseXVPVNCConsoleTest().expected());
   }

   public void getXVPVNCConsoleWhenResponseIs404NotFound() throws Exception {
      getConsoleWhenResponseIs404NotFound(Console.Type.XVPVNC);
   }

   public void getSPICEConsole() throws Exception {
      getConsole(Console.Type.SPICE_HTML5, "/spice_console.json", new ParseSPICEConsoleTest().expected());
   }

   public void getSPICEConsoleWhenResponseIs404NotFound() throws Exception {
      getConsoleWhenResponseIs404NotFound(Console.Type.SPICE_HTML5);
   }

   public void getRDPConsole() throws Exception {
      getConsole(Console.Type.RDP_HTML5, "/rdp_console.json", new ParseRDPConsoleTest().expected());
   }

   public void getRDPConsoleWhenResponseIs404NotFound() throws Exception {
      getConsoleWhenResponseIs404NotFound(Console.Type.RDP_HTML5);
   }

   private void getConsole(Console.Type consoleType, String responseResource, Console expected) throws Exception {
      String serverId = "5f64fca7-879b-4173-bf9c-8fa88330a4dc";

      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/keystoneAuthResponse.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list_full.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource(responseResource))));

      try {
         NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");

         String regionId = getFirst(novaApi.getConfiguredRegions(), "RegionTwo");

         ConsolesApi consolesApi = novaApi.getConsolesApi(regionId).get();

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertEquals(server.takeRequest().getRequestLine(),
                 "GET /v2/da0d12be20394afb851716e10a49e4a7/extensions HTTP/1.1");
         assertEquals(consolesApi.getConsole(serverId, consoleType), expected);

      } finally {
         server.shutdown();
      }
   }

   private void getConsoleWhenResponseIs404NotFound(Console.Type consoleType) throws Exception {
      String serverId = "5f64fca7-879b-4173-bf9c-8fa88330a4dc";

      MockWebServer server = mockOpenStackServer();
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/access.json"))));
      server.enqueue(addCommonHeaders(new MockResponse().setBody(stringFromResource("/extension_list_full.json"))));
      server.enqueue(addCommonHeaders(new MockResponse()
                  .setStatus("HTTP/1.1 404 Not Found")
                  .setBody("{\"itemNotFound\":" + "{\"message\":\"Instance " + serverId
                      + " could not be found.\",\"code\":404}}")
                  .setHeader("Content-Type", "application/json; charset=UTF-8")));

      try {
         NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");

         String regionId = getFirst(novaApi.getConfiguredRegions(), "RegionTwo");

         ConsolesApi consolesApi = novaApi.getConsolesApi(regionId).get();

         assertEquals(server.getRequestCount(), 2);
         assertAuthentication(server);
         assertEquals(server.takeRequest().getRequestLine(),
                 "GET /v2/da0d12be20394afb851716e10a49e4a7/extensions HTTP/1.1");

         try {
             consolesApi.getConsole(serverId, consoleType);
             fail("expected a ResourceNotFoundException");
         } catch (org.jclouds.rest.ResourceNotFoundException e) {
             // expected
         }

      } finally {
         server.shutdown();
      }
   }

}
