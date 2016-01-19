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
import java.util.concurrent.TimeUnit;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.Snapshot;
import org.jclouds.profitbricks.domain.Storage;
import org.testng.annotations.Test;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

import org.jclouds.util.Predicates2;

@Test(groups = "live", testName = "SnapshotApiLiveTest")
public class SnapshotApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Storage storage;

   private String createdSnapshotId;

   @BeforeClass
   public void setupTest() {
      dataCenter = findOrCreateDataCenter("snapshotApiLiveTest-" + System.currentTimeMillis());
      storage = FluentIterable.from(dataCenter.storages()).firstMatch(new Predicate<Storage>() {

         @Override
         public boolean apply(Storage input) {
            return input.state() == ProvisioningState.AVAILABLE
                    && input.size() <= 10f;
         }
      }).or(new Supplier<Storage>() {

         @Override
         public Storage get() {
            StorageApi storageApi = api.storageApi();
            String name = String.format("server-%d", dataCenter.servers().size());
            String createdStorageId = storageApi.createStorage(
                    Storage.Request.creatingBuilder()
                    .dataCenterId(dataCenter.id())
                    .name(name)
                    .size(2f)
                    .build()
            );
            assertDataCenterAvailable(dataCenter);

            return storageApi.getStorage(createdStorageId);
         }
      });
   }

   @Test
   public void testCreateSnapshot() {
      assertDataCenterAvailable(dataCenter);
      Snapshot snapshot = api.snapshotApi().createSnapshot(
              Snapshot.Request.creatingBuilder()
              .storageId(storage.id())
              .description("my description")
              .name("test snapshot")
              .build());

      assertNotNull(snapshot);
      assertSnapshotAvailable(snapshot.id());

      createdSnapshotId = snapshot.id();
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testGetAllSnapshots() {
      List<Snapshot> snapshots = api.snapshotApi().getAllSnapshots();

      assertNotNull(snapshots);
      assertTrue(snapshots.size() > 0);
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testGetSnapshot() {
      Snapshot snapshot = api.snapshotApi().getSnapshot(createdSnapshotId);

      assertNotNull(snapshot);
      assertEquals(snapshot.id(), createdSnapshotId);
   }

   @Test(dependsOnMethods = "testGetSnapshot")
   public void testUpdateSnapshot() {
      assertSnapshotAvailable(createdSnapshotId);
      String newName = "new name";
      String newDescription = "new description";

      String requestId = api.snapshotApi().updateSnapshot(
              Snapshot.Request.updatingBuilder()
              .id(createdSnapshotId)
              .description(newDescription)
              .name(newName)
              .bootable(true)
              .osType(OsType.LINUX)
              .isCpuHotPlug(true)
              .isCpuHotUnPlug(true)
              .isDiscVirtioHotPlug(true)
              .isDiscVirtioHotUnPlug(true)
              .isNicHotPlug(true)
              .isNicHotUnPlug(true)
              .isRamHotPlug(true)
              .isRamHotUnPlug(true)
              .build());
      assertNotNull(requestId);
   }

   @Test(dependsOnMethods = "testUpdateSnapshot")
   public void testRollbackSnapshot() {
      assertSnapshotAvailable(createdSnapshotId);
      String requestid = api.snapshotApi().rollbackSnapshot(
              Snapshot.Request.createRollbackPayload(createdSnapshotId, storage.id()));
      assertNotNull(requestid);
   }

   @Test(dependsOnMethods = "testRollbackSnapshot", alwaysRun = true)
   public void testDeleteSnapshot() {
      assertSnapshotAvailable(createdSnapshotId);
      // Newly created snapshots doesn't seem to reflect in the API right away,
      // so we need to persistently try to delete (to clean up resources as well)
      Predicate<String> persistentDelete = Predicates2.retry(new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            try {
               return api.snapshotApi().deleteSnapshot(input);
            } catch (Exception ex) {
               return false;
            }
         }
      }, 120L, 5L, 10L, TimeUnit.SECONDS);
      assertTrue(persistentDelete.apply(createdSnapshotId), "Created snapshot wasn't deleted");
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      destroyDataCenter(dataCenter);
   }
}
