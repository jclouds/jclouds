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
package org.jclouds.profitbricks.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ServerApiLiveTest")
public class ServerApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private String createdServerId;

   @BeforeClass
   public void setupTest() {
      dataCenter = findOrCreateDataCenter("serverApiLiveTest-" + System.currentTimeMillis());
   }

   @Test
   public void testCreateServer() {
      assertDataCenterAvailable(dataCenter);
      String serverId = api.serverApi().createServer(
              Server.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("jclouds-node")
              .cores(1)
              .ram(1024)
              .build());

      assertNotNull(serverId);
      assertDataCenterAvailable(dataCenter);
      assertNodeRunning(serverId);

      this.createdServerId = serverId;
   }

   @Test(dependsOnMethods = "testCreateServer")
   public void testGetServer() {
      Server server = api.serverApi().getServer(createdServerId);

      assertNotNull(server);
      assertEquals(server.id(), createdServerId);
   }

   @Test(dependsOnMethods = "testCreateServer")
   public void testGetAllServers() {
      List<Server> servers = api.serverApi().getAllServers();

      assertNotNull(servers);
      assertFalse(servers.isEmpty());
   }

   @Test(dependsOnMethods = "testGetServer")
   public void testUpdateServer() {
      assertDataCenterAvailable(dataCenter);
      String requestId = api.serverApi().updateServer(
              Server.Request.updatingBuilder()
              .id(createdServerId)
              .name("apache-node")
              .cores(2)
              .ram(2 * 1024)
              .build());

      assertNotNull(requestId);
      assertDataCenterAvailable(dataCenter);

      Server server = api.serverApi().getServer(createdServerId);
      assertEquals(server.state(), ProvisioningState.AVAILABLE);
   }

   @Test(dependsOnMethods = "testUpdateServer")
   public void testStopServer() {
      String requestId = api.serverApi().stopServer(createdServerId);
      assertNotNull(requestId);
      assertNodeSuspended(createdServerId);

      Server server = api.serverApi().getServer(createdServerId);
      assertEquals(server.status(), Server.Status.SHUTOFF);
   }

   @Test(dependsOnMethods = "testStopServer")
   public void testStartServer() {
      String requestId = api.serverApi().startServer(createdServerId);
      assertNotNull(requestId);
      assertNodeRunning(createdServerId);

      Server server = api.serverApi().getServer(createdServerId);
      assertEquals(server.status(), Server.Status.RUNNING);
   }

   @Test(dependsOnMethods = "testStartServer")
   public void testDeleteServer() {
      assertDataCenterAvailable(dataCenter);
      boolean result = api.serverApi().deleteServer(createdServerId);
      assertTrue(result, "Created test server was not deleted.");
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      destroyDataCenter(dataCenter);
   }

}
