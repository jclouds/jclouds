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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Iterables;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.IpBlock;
import org.jclouds.profitbricks.domain.Nic;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "IpBlockApiLiveTest")
public class IpBlockApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Nic nic;

   private IpBlock newIpBlock;

   @BeforeClass
   public void setupTest() {
      dataCenter = findOrCreateDataCenter("ipBlockApiLiveTest" + System.currentTimeMillis());
      nic = findOrCreateNic(dataCenter);
   }

   @Test
   public void testReservePublicIpBlock() {
      assertDataCenterAvailable(dataCenter);
      newIpBlock = api.ipBlockApi().reservePublicIpBlock(1, testLocation);

      assertNotNull(newIpBlock);
      assertFalse(newIpBlock.ips().isEmpty());
   }

   @Test(dependsOnMethods = "testReservePublicIpBlock")
   public void testGetAllIpBlocks() {
      List<IpBlock> ipBlocks = api.ipBlockApi().getAllIpBlock();

      assertNotNull(ipBlocks);
      assertFalse(ipBlocks.isEmpty());
   }

   @Test(dependsOnMethods = "testReservePublicIpBlock")
   public void testGetOneIpBlock() {
      IpBlock ipBlock = api.ipBlockApi().getIpBlock(newIpBlock.id());

      assertNotNull(ipBlock);
   }

   @Test(dependsOnMethods = "testReservePublicIpBlock")
   public void testAddPublicIpToNic() {
      assertDataCenterAvailable(dataCenter);
      String ipToAdd = Iterables.getFirst(newIpBlock.ips(), null);
      String requestId = api.ipBlockApi().addPublicIpToNic(
              ipToAdd, nic.id());

      assertNotNull(requestId);
      assertDataCenterAvailable(dataCenter);
      List<String> ips = api.nicApi().getNic(nic.id()).ips();
      assertTrue(ips.contains(ipToAdd), "NIC didn't contain added public ip");
   }

   @Test(dependsOnMethods = "testAddPublicIpToNic")
   public void testRemovePublicIpFromNic() {
      assertDataCenterAvailable(dataCenter);
      String ipToRemove = Iterables.getFirst(newIpBlock.ips(), null);
      String requestId = api.ipBlockApi().removePublicIpFromNic(
              ipToRemove, nic.id());

      assertNotNull(requestId);
      assertDataCenterAvailable(dataCenter);
      List<String> ips = api.nicApi().getNic(nic.id()).ips();
      assertFalse(ips.contains(ipToRemove), "NIC still contains removed public ip");
   }

   @Test(dependsOnMethods = "testRemovePublicIpFromNic")
   public void testReleasePublicIpBlock() {
      assertDataCenterAvailable(dataCenter);
      String requestId = api.ipBlockApi().releasePublicIpBlock(newIpBlock.id());

      assertNotNull(requestId);
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      destroyDataCenter(dataCenter);
   }
}
