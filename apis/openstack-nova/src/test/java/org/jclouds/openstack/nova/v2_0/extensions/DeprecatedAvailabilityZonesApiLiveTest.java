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
package org.jclouds.openstack.nova.v2_0.extensions;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.AvailabilityZone;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "DeprecatedAvailabilityZonesApiLiveTest")
public class DeprecatedAvailabilityZonesApiLiveTest extends BaseNovaApiLiveTest {

   @Test
   public void testListAvailabilityZones() throws Exception {

      Optional<? extends AvailabilityZoneApi> availabilityZoneApi = api.getAvailabilityZoneApi("RegionOne");
      if (availabilityZoneApi.isPresent()) {
         FluentIterable<? extends AvailabilityZone> zones = availabilityZoneApi.get().list();

         for (AvailabilityZone zone : zones) {
            assertNotNull(zone.getName());
            assertTrue(zone.getState().available(), "zone: " + zone.getName() + " is not available.");
         }
      }
   }
}
