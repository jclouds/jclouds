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
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.testng.annotations.Test;

public class DiskApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String DISK_NAME = "disk-api-live-test-disk";
   public static final String SSD_DISK_NAME = "disk-api-live-test-disk-ssd";
   public static final int SIZE_GB = 1;

   private DiskApi api() {
      return api.disksInZone(DEFAULT_ZONE_NAME);
   }

   @Test(groups = "live")
   public void testInsertDisk() {
      DiskCreationOptions options = new DiskCreationOptions.Builder().sizeGb( SIZE_GB).build();
      assertOperationDoneSuccessfully(api().create(DISK_NAME, options));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertDisk")
   public void testGetDisk() {
      Disk disk = api().get(DISK_NAME);
      assertNotNull(disk);
      assertDiskEquals(disk);
   }

   @Test(groups = "live", dependsOnMethods = "testGetDisk")
   public void testListDisk() {
      Iterator<ListPage<Disk>> disks = api().list(filter("name eq " + DISK_NAME));

      List<Disk> disksAsList = disks.next();

      assertEquals(disksAsList.size(), 1);

      assertDiskEquals(disksAsList.get(0));
   }

   @Test(groups = "live", dependsOnMethods = "testListDisk")
   public void testDeleteDisk() {
      assertOperationDoneSuccessfully(api().delete(DISK_NAME));
   }

   private void assertDiskEquals(Disk result) {
      assertEquals(result.name(), DISK_NAME);
      assertEquals(result.sizeGb(), SIZE_GB);
      assertEquals(result.zone(), getDefaultZoneUrl());
   }

   @Test(groups = "live")
   public void testInsertSSDDisk() {
      URI diskType = getDiskTypeUrl(DEFAULT_ZONE_NAME, "pd-ssd");
      DiskCreationOptions diskCreationOptions = new DiskCreationOptions.Builder().type(diskType).sizeGb(SIZE_GB).build();
      assertOperationDoneSuccessfully(api().create(SSD_DISK_NAME, diskCreationOptions));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertSSDDisk")
   public void testGetSSDDisk() {
      Disk disk = api().get(SSD_DISK_NAME);
      assertNotNull(disk);
      assertSSDDiskEquals(disk);
   }

   @Test(groups = "live", dependsOnMethods = "testGetSSDDisk")
   public void testDeleteSSDDisk() {
      assertOperationDoneSuccessfully(api().delete(SSD_DISK_NAME));
   }

   private void assertSSDDiskEquals(Disk result) {
      assertEquals(result.name(), SSD_DISK_NAME);
      assertEquals(result.sizeGb(), SIZE_GB);
      assertEquals(result.zone(), getDefaultZoneUrl());
      assertEquals(result.type(), getDiskTypeUrl(DEFAULT_ZONE_NAME, "pd-ssd"));
   }
}
