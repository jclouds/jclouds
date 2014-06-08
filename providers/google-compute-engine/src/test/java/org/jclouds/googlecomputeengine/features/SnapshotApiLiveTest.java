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

import static org.jclouds.googlecomputeengine.features.DiskApiLiveTest.TIME_WAIT;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Snapshot;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class SnapshotApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String DISK_NAME = "snapshot-api-live-test-disk";
   private static final String SNAPSHOT_NAME = "snapshot-api-live-test-snapshot";

   private Disk disk;
   private SnapshotApi api() {
      return api.getSnapshotApiForProject(userProject.get());
   }

   private DiskApi diskApi() {
      return api.getDiskApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testCreateSnapshot() {
      assertZoneOperationDoneSucessfully(diskApi().createInZone(DISK_NAME, 1, DEFAULT_ZONE_NAME), TIME_WAIT);
      disk = diskApi().getInZone(DEFAULT_ZONE_NAME, DISK_NAME);

      assertZoneOperationDoneSucessfully(diskApi().createSnapshotInZone(DEFAULT_ZONE_NAME, DISK_NAME, SNAPSHOT_NAME),
              TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateSnapshot")
   public void testGetSnapshot() {
      Snapshot snapshot = api().get(SNAPSHOT_NAME);

      assertEquals(snapshot.getName(), SNAPSHOT_NAME);
      assertSnapshotEquals(snapshot);
   }

   @Test(groups = "live", dependsOnMethods = "testGetSnapshot")
   public void testListSnapshot() {

      PagedIterable<Snapshot> snapshots = api().list(new ListOptions.Builder()
              .filter("name eq " + SNAPSHOT_NAME));

      List<Snapshot> snapshotsAsList = Lists.newArrayList(snapshots.concat());

      assertEquals(snapshotsAsList.size(), 1);

      assertSnapshotEquals(Iterables.getOnlyElement(snapshotsAsList));
   }

   @Test(groups = "live", dependsOnMethods = "testListSnapshot")
   public void testDeleteDisk() {

      assertZoneOperationDoneSucessfully(diskApi().deleteInZone(DEFAULT_ZONE_NAME, DISK_NAME), TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api().delete(SNAPSHOT_NAME), TIME_WAIT);
   }

   private void assertSnapshotEquals(Snapshot result) {
      assertEquals(result.getName(), SNAPSHOT_NAME);
      assertEquals(result.getSourceDisk().orNull(), disk.getSelfLink());
      assertEquals(result.getSizeGb(), disk.getSizeGb());
   }


}
