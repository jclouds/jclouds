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

import com.google.common.collect.Iterables;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusAware;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusPollingPredicate;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.util.Predicates2;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

@Test(groups = "live", testName = "NicApiLiveTest", singleThreaded = true)
public class NicApiLiveTest extends BaseProfitBricksLiveTest {

   private Predicate<String> waitUntilAvailable;
   private Server server;
   private Nic createdNic;

   @Override
   protected void initialize() {
      super.initialize();
      List<Server> servers = api.serverApi().getAllServers();
      assertFalse(servers.isEmpty(), "Must atleast have 1 server available for NIC testing.");

      this.server = Iterables.tryFind(servers, new Predicate<Server>() {

         @Override
         public boolean apply(Server input) {
            return input.state() == ProvisioningState.AVAILABLE;
         }
      }).orNull();

      this.waitUntilAvailable = Predicates2.retry(
              new ProvisioningStatusPollingPredicate(api, ProvisioningStatusAware.NIC, ProvisioningState.AVAILABLE),
              2l * 60l, 2l, TimeUnit.SECONDS);
   }

   @Test
   public void testCreateNic() {
      Nic.Request.CreatePayload payload = Nic.Request.creatingBuilder()
              .name("name nr1")
              .dhcpActive(true)
              .serverId(server.id())
              .lanId(1)
              .build();

      Nic nic = api.nicApi().createNic(payload);
      assertNotNull(nic);

      waitUntilAvailable.apply(nic.id());
      this.createdNic = nic;
   }

   @Test(dependsOnMethods = "testCreateNic")
   public void testGetAllNics() {
      List<Nic> nics = api.nicApi().getAllNics();

      assertNotNull(nics);
   }

   @Test(dependsOnMethods = "testCreateNic")
   public void testGetNic() {
      Nic nic = api.nicApi().getNic(createdNic.id());

      assertNotNull(nic);
      assertEquals(nic.id(), createdNic.id());
   }

   @Test(dependsOnMethods = "testCreateNic")
   public void testUpdateNic() {
      Nic.Request.UpdatePayload payload = Nic.Request.updatingBuilder()
              .name("name nr2")
              .id(createdNic.id())
              .build();

      Nic updatedNic = api.nicApi().updateNic(payload);
      assertNotNull(updatedNic);
      waitUntilAvailable.apply(payload.id());

      updatedNic = api.nicApi().getNic(payload.id());

      assertEquals(updatedNic.name(), payload.name());
   }

   @Test(dependsOnMethods = "testUpdateNic")
   public void testSetInternetAccess() {

      Nic.Request.SetInternetAccessPayload payload = Nic.Request.setInternetAccessBuilder()
              .dataCenterId(createdNic.dataCenterId())
              .lanId(1)
              .internetAccess(true)
              .build();

      Nic result = api.nicApi().setInternetAccess(payload);
      assertNotNull(result);
   }

   @AfterClass(alwaysRun = true)
   public void testDeleteNic() {
      if (createdNic != null) {
         boolean result = api.nicApi().deleteNic(createdNic.id());

         assertTrue(result, "Created test NIC was not deleted.");
      }
   }
}
