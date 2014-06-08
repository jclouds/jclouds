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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Project;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DiskApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String DISK_NAME = "disk-api-live-test-disk";
   public static final int TIME_WAIT = 30;
   public static final int sizeGb = 1;

   private DiskApi api() {
      return api.getDiskApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertDisk() {
      Project project = api.getProjectApi().get(userProject.get());
      assertZoneOperationDoneSucessfully(api().createInZone(DISK_NAME, sizeGb, DEFAULT_ZONE_NAME), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertDisk")
   public void testGetDisk() {

      Disk disk = api().getInZone(DEFAULT_ZONE_NAME, DISK_NAME);
      assertNotNull(disk);
      assertDiskEquals(disk);
   }

   @Test(groups = "live", dependsOnMethods = "testGetDisk")
   public void testListDisk() {

      PagedIterable<Disk> disks = api().listInZone(DEFAULT_ZONE_NAME, new ListOptions.Builder()
              .filter("name eq " + DISK_NAME));

      List<Disk> disksAsList = Lists.newArrayList(disks.concat());

      assertEquals(disksAsList.size(), 1);

      assertDiskEquals(Iterables.getOnlyElement(disksAsList));

   }

   @Test(groups = "live", dependsOnMethods = "testListDisk")
   public void testDeleteDisk() {

      assertZoneOperationDoneSucessfully(api().deleteInZone(DEFAULT_ZONE_NAME, DISK_NAME), TIME_WAIT);
   }

   private void assertDiskEquals(Disk result) {
      assertEquals(result.getName(), DISK_NAME);
      assertEquals(result.getSizeGb(), sizeGb);
      assertEquals(result.getZone(), getDefaultZoneUrl(userProject.get()));
   }

}
