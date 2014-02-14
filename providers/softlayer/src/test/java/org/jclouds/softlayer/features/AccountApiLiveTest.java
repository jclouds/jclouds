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
package org.jclouds.softlayer.features;

import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code AccountApi}
 */
@Test(groups = "live")
public class AccountApiLiveTest extends BaseSoftLayerApiLiveTest {

   @Test
   public void testGetBlockDeviceTemplateGroups() {
      Set<VirtualGuestBlockDeviceTemplateGroup> privateImages = api().getBlockDeviceTemplateGroups();
      assertNotNull(privateImages);
      for (VirtualGuestBlockDeviceTemplateGroup privateImage : privateImages) {
         assertTrue(privateImage.getId() > 0, "id must be greater than 0");
         assertTrue(privateImage.getStatusId() > 0, "status id must be greater than 0");
         assertTrue(privateImage.getAccountId() > 0, "id must be greater than 0");
      }
   }

   @Test
   public void testListVirtualGuests() throws Exception {
      Set<VirtualGuest> response = api().listVirtualGuests();
      assertTrue(response.size() >= 0);
      for (VirtualGuest vg : response) {
         checkVirtualGuest(vg);
      }
   }

   private AccountApi api() {
      return api.getAccountApi();
   }

   private void checkVirtualGuest(VirtualGuest vg) {
      if (vg.getActiveTransactionCount() == 0) {
         assertNotNull(vg.getDomain(), "domain must be not null");
         assertNotNull(vg.getFullyQualifiedDomainName(), "fullyQualifiedDomainName must be not null");
         assertNotNull(vg.getHostname(), "hostname must be not null");
         assertTrue(vg.getId() > 0, "id must be greater than 0");
         assertTrue(vg.getMaxCpu() > 0, "maxCpu must be greater than 0");
         assertNotNull(vg.getMaxCpuUnits(), "maxCpuUnits must be not null");
         assertTrue(vg.getMaxMemory() > 0, "maxMemory must be greater than 0");
         assertTrue(vg.getStartCpus() > 0, "startCpus must be greater than 0");
         assertTrue(vg.getStatusId() > 0, "statusId must be greater than 0");
         assertNotNull(vg.getUuid(), "uuid must be not null");
      }
   }

}
