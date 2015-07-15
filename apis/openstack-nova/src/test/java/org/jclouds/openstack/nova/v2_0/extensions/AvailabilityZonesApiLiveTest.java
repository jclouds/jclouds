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
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.AvailabilityZone;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.AvailabilityZoneDetails;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.AvailabilityZoneDetails.HostService;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "AvailabilityZonesApiLiveTest")
public class AvailabilityZonesApiLiveTest extends BaseNovaApiLiveTest {

   @Test
   public void testListAvailabilityZones() throws Exception {

      Optional<? extends AvailabilityZoneApi> availabilityZoneApi = api.getAvailabilityZoneApi("RegionOne");
      if (availabilityZoneApi.isPresent()) {
         FluentIterable<? extends AvailabilityZone> zones = availabilityZoneApi.get().listAvailabilityZones();

         for (AvailabilityZone zone : zones) {
            assertNotNull(zone.getName());
            assertTrue(zone.getState().isAvailable(), "zone: " + zone.getName() + " is not available.");
         }
      }
   }

   @Test
   public void testListInDetail() throws Exception {

      Optional<? extends AvailabilityZoneApi> availabilityZoneApi = api.getAvailabilityZoneApi("RegionOne");
      if (availabilityZoneApi.isPresent()) {
         FluentIterable<? extends AvailabilityZoneDetails> zones = availabilityZoneApi.get().listInDetail();

         for (AvailabilityZoneDetails zone : zones) {
            assertNotNull(zone.getName());
            assertTrue(zone.getState()
                  .isAvailable(), "zone: " + zone.getName() + " is not available.");
            String hostName = zone.getHosts().keySet().iterator().next();
            assertNotNull(hostName, "Expected host name to be not null");
            String hostServiceName = zone.getHosts().get(hostName).keySet().iterator().next();
            assertNotNull(hostServiceName, "Expected host service name to be not null");
            HostService hostService = zone.getHosts().get(hostName).get(hostServiceName);
            assertTrue(hostService.isAvailable(), "Couldn't find host service availability");
            assertTrue(hostService.isActive(), "Couldn't find host service state");
            assertNotNull(hostService.getUpdated(), "Expected Updated time, but none received ");

         }
      }
   }
}
