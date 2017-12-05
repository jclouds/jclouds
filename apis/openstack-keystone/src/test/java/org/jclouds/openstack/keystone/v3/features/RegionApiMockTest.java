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
package org.jclouds.openstack.keystone.v3.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.openstack.keystone.v3.domain.Region;
import org.jclouds.openstack.keystone.v3.internal.BaseV3KeystoneApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "RegionApiMockTest", singleThreaded = true)
public class RegionApiMockTest extends BaseV3KeystoneApiMockTest {

   public void testListRegions() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/regions.json"));

      List<Region> regions = api.getRegionApi().list();
      assertFalse(regions.isEmpty());

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/regions");
   }

   public void testListRegionsReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      List<Region> regions = api.getRegionApi().list();
      assertTrue(regions.isEmpty());

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/regions");
   }

   public void testGetRegion() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/region.json"));

      Region region = api.getRegionApi().get("RegionOne");
      assertNotNull(region);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/regions/RegionOne");
   }

   public void testGetRegionReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      Region region = api.getRegionApi().get("RegionOne");
      assertNull(region);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/regions/RegionOne");
   }

   public void testCreateRegion() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/region.json"));

      Region region = api.getRegionApi().create("RegionOne", "Description", "12345");
      assertNotNull(region);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "POST", "/regions",
            "{\"region\":{\"id\":\"RegionOne\",\"description\":\"Description\",\"parent_region_id\":\"12345\"}}");
   }

   public void testUpdateRegion() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/region.json"));

      Region region = api.getRegionApi().update("RegionOne", "Updated", null);
      assertNotNull(region);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "PATCH", "/regions/RegionOne", "{\"region\":{\"description\":\"Updated\"}}");
   }

   public void testDeleteRegion() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response204());

      boolean deleted = api.getRegionApi().delete("RegionOne");
      assertTrue(deleted);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "DELETE", "/regions/RegionOne");
   }

   public void testDeleteRegionReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      boolean deleted = api.getRegionApi().delete("RegionOne");
      assertFalse(deleted);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "DELETE", "/regions/RegionOne");
   }

}
