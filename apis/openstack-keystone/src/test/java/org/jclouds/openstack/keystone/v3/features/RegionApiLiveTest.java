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

import static com.google.common.collect.Iterables.any;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.keystone.v3.domain.Region;
import org.jclouds.openstack.keystone.v3.internal.BaseV3KeystoneApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

@Test(groups = "live", testName = "RegionApiLiveTest", singleThreaded = true)
public class RegionApiLiveTest extends BaseV3KeystoneApiLiveTest {

   private Region region;
   
   @BeforeClass
   public void createTestRegion() {
      region = api().create(getClass().getSimpleName(), null, null);
      assertEquals(region.id(), getClass().getSimpleName());
   }
   
   @Test
   public void testListRegions() {
      assertTrue(any(api().list(), new Predicate<Region>() {
         @Override
         public boolean apply(Region input) {
            return input.id().equals(region.id());
         }
      }));
   }
   
   @Test
   public void testGetRegion() {
      assertNotNull(api().get(region.id()));
   }
   
   @Test
   public void testUpdateRegion() {
      api().update(region.id(), "Updated", null);
      region = api().get(region.id());
      assertEquals(region.description(), "Updated");
   }
   
   @AfterClass(alwaysRun = true)
   public void deleteRegion() {
      assertTrue(api().delete(region.id()));
   }
   
   private RegionApi api() {
      return api.getRegionApi();
   }
}
