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

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.annotations.Test;

public class ZoneApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private Zone zone;

   private ZoneApi api() {
      return api.zones();
   }

   @Test(groups = "live")
   public void testListZone() {
      Iterator<ListPage<Zone>> pageIterator = api().list(maxResults(1));
      assertTrue(pageIterator.hasNext());

      List<Zone> zoneAsList = pageIterator.next();

      assertEquals(zoneAsList.size(), 1);

      this.zone = zoneAsList.get(0);
   }


   @Test(groups = "live", dependsOnMethods = "testListZone")
   public void testGetZone() {
      Zone zone = api().get(this.zone.name());
      assertNotNull(zone);
      assertZoneEquals(zone, this.zone);
   }

   private void assertZoneEquals(Zone result, Zone expected) {
      assertEquals(result.name(), expected.name());
   }

}
