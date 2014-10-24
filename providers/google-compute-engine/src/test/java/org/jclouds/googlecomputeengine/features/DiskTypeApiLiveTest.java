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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.DiskType;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


public class DiskTypeApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private DiskType diskType;

   private DiskTypeApi api() {
      return api.getDiskTypeApi(userProject.get());
   }

   @Test(groups = "live")
   public void testDiskType() {

      PagedIterable<DiskType> diskTypes = api().listInZone(DEFAULT_ZONE_NAME, new ListOptions.Builder()
              .maxResults(1));

      Iterator<IterableWithMarker<DiskType>> pageIterator = diskTypes.iterator();
      assertTrue(pageIterator.hasNext());

      IterableWithMarker<DiskType> singlePageIterator = pageIterator.next();
      List<DiskType> diskTypeAsList = Lists.newArrayList(singlePageIterator);

      assertSame(diskTypeAsList.size(), 1);

      this.diskType = Iterables.getOnlyElement(diskTypeAsList);
   }

   @Test(groups = "live", dependsOnMethods = "testDiskType")
   public void testGetDiskType() {
      DiskType diskType = api().getInZone(DEFAULT_ZONE_NAME, this.diskType.getName());
      assertNotNull(diskType);
      assertDiskTypeEquals(diskType, this.diskType);
   }

   private void assertDiskTypeEquals(DiskType result, DiskType expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getValidDiskSize(), expected.getValidDiskSize());
      assertEquals(result.getZone(), expected.getZone());
      assertEquals(result.getDefaultDiskSizeGb(), expected.getDefaultDiskSizeGb());
      assertEquals(result.getSelfLink(), expected.getSelfLink());
   }
}
