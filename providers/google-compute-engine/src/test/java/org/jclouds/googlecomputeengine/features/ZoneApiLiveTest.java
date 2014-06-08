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
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ZoneApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private Zone zone;

   private ZoneApi api() {
      return api.getZoneApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testListZone() {

      PagedIterable<Zone> zones = api().list(new ListOptions.Builder()
              .maxResults(1));

      Iterator<IterableWithMarker<Zone>> pageIterator = zones.iterator();
      assertTrue(pageIterator.hasNext());

      IterableWithMarker<Zone> singlePageIterator = pageIterator.next();
      List<Zone> zoneAsList = Lists.newArrayList(singlePageIterator);

      assertSame(zoneAsList.size(), 1);

      this.zone = Iterables.getOnlyElement(zoneAsList);
   }


   @Test(groups = "live", dependsOnMethods = "testListZone")
   public void testGetZone() {
      Zone zone = api().get(this.zone.getName());
      assertNotNull(zone);
      assertZoneEquals(zone, this.zone);
   }

   private void assertZoneEquals(Zone result, Zone expected) {
      assertEquals(result.getName(), expected.getName());
   }

}
