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
package org.jclouds.profitbricks.binder.snapshot;

import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.Snapshot;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(groups = "unit", testName = "UpdateSnapshotRequestBinderTest")
public class UpdateSnapshotRequestBinderTest {

   @Test
   public void testUpdatePayload() {
      UpdateSnapshotRequestBinder binder = new UpdateSnapshotRequestBinder();

      Snapshot.Request.UpdatePayload payload = Snapshot.Request.UpdatePayload.create("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh", "description", "snapshot-name", true, OsType.LINUX, true, true, true, true, true, true, true, true);

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");
      assertEquals(expectedPayload, actual);

   }

   private final String expectedPayload
           = "<ws:updateSnapshot>"
           + "<request>"
           + "<snapshotId>qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh</snapshotId>"
           + "<description>description</description>"
           + "<snapshotName>snapshot-name</snapshotName>"
           + "<bootable>true</bootable>"
           + "<osType>LINUX</osType>"
           + "<cpuHotPlug>true</cpuHotPlug>"
           + "<cpuHotUnPlug>true</cpuHotUnPlug>"
           + "<ramHotPlug>true</ramHotPlug>"
           + "<ramHotUnPlug>true</ramHotUnPlug>"
           + "<nicHotPlug>true</nicHotPlug>"
           + "<nicHotUnPlug>true</nicHotUnPlug>"
           + "<discVirtioHotPlug>true</discVirtioHotPlug>"
           + "<discVirtioHotUnPlug>true</discVirtioHotUnPlug>"
           + "</request>"
           + "</ws:updateSnapshot>".replaceAll("\\s", "");

}
