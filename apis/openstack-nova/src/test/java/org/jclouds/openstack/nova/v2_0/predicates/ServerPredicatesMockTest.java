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
package org.jclouds.openstack.nova.v2_0.predicates;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.internal.BaseOpenStackMockTest;
import org.testng.annotations.Test;

import static org.jclouds.openstack.nova.v2_0.domain.Server.Status.ACTIVE;
import static org.jclouds.openstack.nova.v2_0.domain.Server.Status.SHUTOFF;
import static org.jclouds.openstack.nova.v2_0.predicates.ServerPredicates.awaitActive;
import static org.jclouds.openstack.nova.v2_0.predicates.ServerPredicates.awaitShutoff;
import static org.jclouds.openstack.nova.v2_0.predicates.ServerPredicates.awaitStatus;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "ServerPredicatesMockTest")
public class ServerPredicatesMockTest extends BaseOpenStackMockTest<NovaApi> {
   public void testAwaitActive() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/access.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/server_details.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/server_details.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/server_details.json")));
      String serverDetailsActive = stringFromResource("/server_details.json").replace("BUILD(scheduling)", ACTIVE.value());
      server.enqueue(new MockResponse().setBody(serverDetailsActive));

      try {
         NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
         ServerApi serverApi = novaApi.getServerApi("RegionOne");

         boolean result = awaitActive(serverApi).apply("71752");

         assertTrue(result);
         assertEquals(server.getRequestCount(), 5);
         assertAuthentication(server);
      } finally {
         server.shutdown();
      }
   }

   public void testAwaitShutoff() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/access.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/server_details.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/server_details.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/server_details.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/server_details.json")));
      server.enqueue(new MockResponse().setBody(stringFromResource("/server_details.json")));
      String serverDetailsShutoff = stringFromResource("/server_details.json").replace("BUILD(scheduling)", SHUTOFF.value());
      server.enqueue(new MockResponse().setBody(serverDetailsShutoff));

      try {
         NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
         ServerApi serverApi = novaApi.getServerApi("RegionOne");

         boolean result = awaitShutoff(serverApi).apply("71752");

         assertTrue(result);
         assertEquals(server.getRequestCount(), 7);
         assertAuthentication(server);
      } finally {
         server.shutdown();
      }
   }

   public void testAwaitTimeout() throws Exception {
      MockWebServer server = mockOpenStackServer();
      server.enqueue(new MockResponse().setBody(stringFromResource("/access.json")));

      for (int i = 0; i < 20; i++) {
         server.enqueue(new MockResponse().setBody(stringFromResource("/server_details.json")));
      }

      try {
         NovaApi novaApi = api(server.getUrl("/").toString(), "openstack-nova");
         ServerApi serverApi = novaApi.getServerApi("RegionOne");

         boolean result = awaitStatus(serverApi, ACTIVE, 3, 1).apply("71752");

         assertFalse(result);
         assertAuthentication(server);
      } finally {
         server.shutdown();
      }
   }
}
