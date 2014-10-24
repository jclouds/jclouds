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
package org.jclouds.openstack.cinder.v1.features;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.jclouds.openstack.cinder.v1.domain.AvailabilityZone;
import org.jclouds.openstack.cinder.v1.extensions.AvailabilityZoneApi;
import org.jclouds.openstack.cinder.v1.internal.BaseCinderApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "AvailabilityZoneApiLiveTest", singleThreaded = true)
public class AvailabilityZoneApiLiveTest extends BaseCinderApiLiveTest {

   private AvailabilityZoneApi availabilityZoneApi;

   public AvailabilityZoneApiLiveTest() {
      super();
      provider = "openstack-cinder";
   }

   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      super.setup();
      String region = Iterables.getFirst(api.getConfiguredRegions(), "regionOne");
      availabilityZoneApi = api.getAvailabilityZoneApi(region);
   }

   public void testListAvailabilityZones() {
      ImmutableList<? extends AvailabilityZone> cinderZones = availabilityZoneApi.list().toList();

      assertTrue(!cinderZones.isEmpty());
      for (AvailabilityZone zone : cinderZones) {
         assertTrue(!Strings.isNullOrEmpty(zone.getName()));
         assertTrue(zone.getZoneState().available());
      }

   }
}
