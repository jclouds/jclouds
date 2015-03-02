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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.Snapshot;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusAware;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusPollingPredicate;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.util.Predicates2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;

@Test(groups = "live", testName = "SnapshotApiLiveTest", singleThreaded = true)
public class SnapshotApiLiveTest extends BaseProfitBricksLiveTest {

   protected Predicate<String> snapshotWaitingPredicate;
   private String snapshotId;
   private String storageId;

   @Override
   protected void initialize() {
      super.initialize();

      initializeWaitPredicate();

      List<Storage> storages = api.storageApi().getAllStorages();
      assertFalse(storages.isEmpty(), "Must atleast have 1 storage available for snapshot testing.");

      storageId = Iterables.getFirst(storages, null).id();
   }

   @Test
   public void testCreateSnapshot() {
      Snapshot snapshot = api.snapshotApi().createSnapshot(Snapshot.Request.CreatePayload.create(storageId, "my description", "test snapshot"));

      assertNotNull(snapshot);

      snapshotWaitingPredicate.apply(snapshot.id());

      snapshotId = snapshot.id();
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testGetAllSnapshots() {
      List<Snapshot> snapshots = api.snapshotApi().getAllSnapshots();

      assertNotNull(snapshots);
      assertTrue(snapshots.size() > 0);
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testGetSnapshot() {
      Snapshot snapshot = api.snapshotApi().getSnapshot(snapshotId);

      assertNotNull(snapshot);
      assertEquals(snapshot.id(), snapshotId);
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testUpdateSnapshot() {

      String newName = "new name";

      api.snapshotApi().updateSnapshot(Snapshot.Request.updatingBuilder()
	      .snapshotId(snapshotId)
	      .description("new description")
	      .name(newName)
	      .bootable(true)
	      .osType(OsType.LINUX)
	      .cpuHotplug(true)
	      .cpuHotunplug(true)
	      .discVirtioHotplug(true)
	      .discVirtioHotunplug(true)
	      .nicHotplug(true)
	      .nicHotunplug(true)
	      .ramHotplug(true)
	      .ramHotunplug(true)
	      .build());

      Snapshot snapshot = api.snapshotApi().getSnapshot(snapshotId);

      assertNotNull(snapshot);
      assertEquals(snapshot.name(), newName);
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testRollbackSnapshot() {
      String result = api.snapshotApi().rollbackSnapshot(Snapshot.Request.RollbackPayload.create(snapshotId, storageId));

      assertNotNull(result);
   }

   @AfterClass(alwaysRun = true)
   public void testDeleteSnapshot() {
      boolean result = api.snapshotApi().deleteSnapshot(snapshotId);

      assertTrue(result);
   }

   private void initializeWaitPredicate() {
      this.snapshotWaitingPredicate = Predicates2.retry(
	      new ProvisioningStatusPollingPredicate(api, ProvisioningStatusAware.SNAPSHOT, ProvisioningState.AVAILABLE),
	      2l * 60l, 2l, TimeUnit.SECONDS);
   }
}
