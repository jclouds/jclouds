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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.domain.Server;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

@Test(groups = "live", testName = "NicApiLiveTest")
public class NicApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Server server;

   private String createdNicId;

   @BeforeClass
   public void setupTest() {
      dataCenter = findOrCreateDataCenter("nicApiLiveTest-" + System.currentTimeMillis());
      server = findOrCreateServer(dataCenter);
   }

   @Test
   public void testCreateNic() {
      assertDataCenterAvailable(dataCenter);
      String nicId = api.nicApi().createNic(Nic.Request.creatingBuilder()
              .name("name nr1")
              .dhcpActive(true)
              .serverId(server.id())
              .lanId(1)
              .build());

      assertNotNull(nicId);
      assertDataCenterAvailable(dataCenter);

      this.createdNicId = nicId;
   }

   @Test(dependsOnMethods = "testCreateNic")
   public void testGetAllNics() {
      List<Nic> nics = api.nicApi().getAllNics();

      assertNotNull(nics);
   }

   @Test(dependsOnMethods = "testCreateNic")
   public void testGetNic() {
      Nic nic = api.nicApi().getNic(createdNicId);

      assertNotNull(nic);
      assertEquals(nic.id(), createdNicId);
   }

   @Test(dependsOnMethods = "testGetNic")
   public void testUpdateNic() {
      assertDataCenterAvailable(dataCenter);
      String newName = "name nr2";
      String requestId = api.nicApi().updateNic(
              Nic.Request.updatingBuilder()
              .name("name nr2")
              .id(createdNicId)
              .build()
      );

      assertNotNull(requestId);
      assertDataCenterAvailable(dataCenter);

      Nic nic = api.nicApi().getNic(createdNicId);
      assertEquals(nic.name(), newName);
   }

   @Test(dependsOnMethods = "testUpdateNic")
   public void testSetInternetAccess() {
      assertDataCenterAvailable(dataCenter);

      String requestId = api.nicApi().setInternetAccess(Nic.Request.setInternetAccessBuilder()
              .dataCenterId(dataCenter.id())
              .lanId(1)
              .internetAccess(true)
              .build()
      );
      assertDataCenterAvailable(dataCenter);
      assertNotNull(requestId);

      Nic nic = api.nicApi().getNic(createdNicId);
      assertTrue(nic.internetAccess(), "Expected nic to have internet access");
   }

   @Test(dependsOnMethods = "testSetInternetAccess")
   public void testDeleteNic() {
      assertDataCenterAvailable(dataCenter);
      boolean result = api.nicApi().deleteNic(createdNicId);
      assertTrue(result, "Created test NIC was not deleted.");
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      destroyDataCenter(dataCenter);
   }
}
