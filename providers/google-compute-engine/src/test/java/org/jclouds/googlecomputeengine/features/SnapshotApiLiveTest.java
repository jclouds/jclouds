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
package org.jclouds.googlecomputeengine.features;

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Snapshot;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.testng.annotations.Test;

public class SnapshotApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String DISK_NAME = "snapshot-api-live-test-disk";
   private static final String SNAPSHOT_NAME = "snapshot-api-live-test-snapshot";

   private Disk disk;
   private SnapshotApi api() {
      return api.snapshots();
   }

   private DiskApi diskApi() {
      return api.disksInZone(DEFAULT_ZONE_NAME);
   }

   @Test(groups = "live")
   public void testCreateSnapshot() {
      assertOperationDoneSuccessfully(diskApi().create(DISK_NAME,
            new DiskCreationOptions.Builder().sizeGb(1).build()));
      disk = diskApi().get(DISK_NAME);

      assertOperationDoneSuccessfully(diskApi().createSnapshot(DISK_NAME, SNAPSHOT_NAME));
   }

   @Test(groups = "live", dependsOnMethods = "testCreateSnapshot")
   public void testGetSnapshot() {
      Snapshot snapshot = api().get(SNAPSHOT_NAME);

      assertEquals(snapshot.name(), SNAPSHOT_NAME);
      assertSnapshotEquals(snapshot);
   }

   @Test(groups = "live", dependsOnMethods = "testGetSnapshot")
   public void testListSnapshot() {
      Iterator<ListPage<Snapshot>> snapshots = api().list(filter("name eq " + SNAPSHOT_NAME));

      List<Snapshot> snapshotsAsList = snapshots.next();

      assertEquals(snapshotsAsList.size(), 1);

      assertSnapshotEquals(snapshotsAsList.get(0));
   }

   @Test(groups = "live", dependsOnMethods = "testListSnapshot")
   public void testDeleteDisk() {
      assertOperationDoneSuccessfully(diskApi().delete(DISK_NAME));
      assertOperationDoneSuccessfully(api().delete(SNAPSHOT_NAME));
   }

   private void assertSnapshotEquals(Snapshot result) {
      assertEquals(result.name(), SNAPSHOT_NAME);
      assertEquals(result.sourceDisk(), disk.selfLink());
      assertEquals(result.diskSizeGb(), disk.sizeGb());
   }
}
