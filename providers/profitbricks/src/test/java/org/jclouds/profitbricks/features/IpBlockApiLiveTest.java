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

import com.google.common.collect.Iterables;
import java.util.List;
import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.IpBlock;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.Nic;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "IpBlockApiLiveTest", singleThreaded = true)
public class IpBlockApiLiveTest extends BaseProfitBricksLiveTest {

   private String nicid;
   private IpBlock newIpBlock;

   @Override
   public void initialize() {
      super.initialize();

      List<Nic> nics = api.nicApi().getAllNics();

      assertFalse(nics.isEmpty(), "At least one NIC is requred to test IpBlocks");

      Nic nic = Iterables.getFirst(nics, null);

      nicid = nic.id();
   }

   @Test
   public void testReservePublicIpBlock() {
      newIpBlock = api.ipBlockApi().reservePublicIpBlock("2", Location.US_LAS.value());

      assertNotNull(newIpBlock);
      assertNotNull(newIpBlock.ips());
      assertFalse(newIpBlock.ips().isEmpty());
   }

   @Test
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
      String requestId = api.ipBlockApi().addPublicIpToNic(newIpBlock.ips().get(0), nicid);

      assertNotNull(requestId);
   }

   @Test(dependsOnMethods = "testAddPublicIpToNic")
   public void testRemovePublicIpFromNic() {
      String requestId = api.ipBlockApi().removePublicIpFromNic(newIpBlock.ips().get(0), nicid);

      assertNotNull(requestId);
   }

   @Test(dependsOnMethods = "testRemovePublicIpFromNic")
   public void testReleasePublicIpBlock() {
      String requestId = api.ipBlockApi().releasePublicIpBlock(newIpBlock.id());

      assertNotNull(requestId);
   }
}
