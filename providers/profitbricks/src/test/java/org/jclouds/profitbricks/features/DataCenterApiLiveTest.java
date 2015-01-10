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
import static org.testng.Assert.assertNull;

import java.util.List;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.ProvisioningState;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "DataCenterApiLiveTest", singleThreaded = true)
public class DataCenterApiLiveTest extends BaseProfitBricksLiveTest {

   private String dcId;

   @Test
   public void testCreateDataCenter() {
      DataCenter dc = api.dataCenterApi().createDataCenter(
              DataCenter.Request.CreatePayload.create("JClouds", Location.DE_FKB)
      );

      assertNotNull(dc);
      dcWaitingPredicate.apply(dc.id());

      dcId = dc.id();
   }

   @Test(dependsOnMethods = "testCreateDataCenter")
   public void testGetDataCenter() {
      assertNotNull(dcId, "No available datacenter found.");

      DataCenter dataCenter = api.dataCenterApi().getDataCenter(dcId);

      assertNotNull(dataCenter);
      assertEquals(dataCenter.id(), dcId);
   }

   @Test(dependsOnMethods = "testCreateDataCenter")
   public void testGetAllDataCenters() {
      List<DataCenter> dataCenters = api.dataCenterApi().getAllDataCenters();

      assertNotNull(dataCenters);
      assertFalse(dataCenters.isEmpty(), "No datacenter found.");
   }

   @Test(dependsOnMethods = "testCreateDataCenter")
   public void testGetDataCenterState() {
      assertNotNull(dcId, "No available datacenter found.");

      ProvisioningState state = api.dataCenterApi().getDataCenterState(dcId);

      assertNotNull(state);
   }

   @Test(dependsOnMethods = "testGetDataCenter")
   public void testUpdateDataCenter() {
      assertNotNull(dcId, "No available datacenter found.");

      final String newName = "Apache";
      DataCenter dataCenter = api.dataCenterApi().updateDataCenter(
              DataCenter.Request.UpdatePayload.create(dcId, newName)
      );

      assertNotNull(dataCenter);
      dcWaitingPredicate.apply(dcId);

      DataCenter fetchedDc = api.dataCenterApi().getDataCenter(dcId);

      assertNotNull(fetchedDc);
      assertEquals(newName, fetchedDc.name());
   }

   @Test(dependsOnMethods = "testUpdateDataCenter")
   public void testClearDataCenter() {
      DataCenter dataCenter = api.dataCenterApi().clearDataCenter(dcId);

      assertNotNull(dataCenter);
   }

   @Test
   public void testGetNonExistingDataCenter() {
      DataCenter dataCenter = api.dataCenterApi().getDataCenter("random-non-existing-id");

      assertNull(dataCenter);
   }

   @Test
   public void testDeleteNonExistingDataCenterMustReturnFalse() {
      boolean result = api.dataCenterApi().deleteDataCenter("random-non-existing-id");

      assertFalse(result);
   }

   @AfterClass(alwaysRun = true)
   public void testDeleteDataCenter() {
      if (dcId != null) {
         boolean result = api.dataCenterApi().deleteDataCenter(dcId);

         assertTrue(result, "Created test data center was not deleted.");
      }
   }
}
