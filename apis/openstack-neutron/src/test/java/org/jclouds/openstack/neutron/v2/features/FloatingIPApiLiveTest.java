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

package org.jclouds.openstack.neutron.v2.features;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import com.google.common.base.Predicate;
import org.jclouds.openstack.neutron.v2.domain.FloatingIP;
import org.jclouds.openstack.neutron.v2.domain.IP;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.internal.BaseNeutronApiLiveTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of FloatingIPApi
 */
@Test(groups = "live", testName = "FloatingIPApiLiveTest")
public class FloatingIPApiLiveTest extends BaseNeutronApiLiveTest {

   public void testCreateUpdateAndDeleteFloatingIP() {
      for (String region : api.getConfiguredRegions()) {

         FloatingIPApi floatingIPApi = api.getFloatingIPApi(region);
         NetworkApi networkApi = api.getNetworkApi(region);

         FloatingIP floatingIPGet = null;
         Network network;

         try {
            network = networkApi.list().concat().firstMatch(new Predicate<Network>() {
               @Override
               public boolean apply(Network input) {
                  return input.getExternal();
               }
            }).orNull();

            if (network == null) Assert.fail("Cannot find a suitable external network. Please add it manually or contact your administrator");

            FloatingIP floatingIP = floatingIPApi.create(FloatingIP.createBuilder(network.getId()).availabilityZone(network.getAvailabilityZone()).build());
            /* List and Get test */
            Set<FloatingIP> floatingIPs = floatingIPApi.list().concat().toSet();
            floatingIPGet = floatingIPApi.get(floatingIP.getId());

            assertNotNull(floatingIPGet);
            assertTrue(floatingIPs.contains(floatingIP));
         }
         finally {
            assertTrue(floatingIPApi.delete(floatingIPGet.getId()));
         }
      }
   }

   public Set<IP> getFixedAddresses(String subnetId) {
      return ImmutableSet.of(IP.builder().subnetId(subnetId).build());
   }
}
