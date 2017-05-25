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

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Subnetwork;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.SubnetworkCreationOptions;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "SubnetworkApiLiveTest")
public class SubnetworkApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String SUBNETWORK_NAME = "subnetwork-api-live-test-network";
   private static final String SUBNETWORK_RANGE = "10.0.0.0/8";

   private SubnetworkApi api() {
      return api.subnetworksInRegion(DEFAULT_REGION_NAME);
   }

   @Test
   public void testInsertSubnetwork() {
      assertOperationDoneSuccessfully(api.networks().createCustom(SUBNETWORK_NAME));
      Network network = api.networks().get(SUBNETWORK_NAME);
      assertNotNull(network);

      SubnetworkCreationOptions opts = SubnetworkCreationOptions.create(SUBNETWORK_NAME, SUBNETWORK_NAME,
            network.selfLink(), SUBNETWORK_RANGE, getDefaultRegionUrl(), false);
      assertOperationDoneSuccessfully(api().createInNetwork(opts));
   }

   @Test(dependsOnMethods = "testInsertSubnetwork")
   public void testGetSubnetwork() {
      Subnetwork subnet = api().get(SUBNETWORK_NAME);
      assertNotNull(subnet);
      assertSubnetworkEquals(subnet);
   }

   @Test(dependsOnMethods = "testInsertSubnetwork")
   public void testListSubnetworks() {
      Iterator<ListPage<Subnetwork>> subnets = api().list(filter("name eq " + SUBNETWORK_NAME));
      List<Subnetwork> subnetsAsList = subnets.next();

      assertEquals(subnetsAsList.size(), 1);
      assertSubnetworkEquals(subnetsAsList.get(0));
   }

   @Test(dependsOnMethods = { "testListSubnetworks", "testGetSubnetwork" }, alwaysRun = true)
   public void testDeleteSubnetwork() {
      assertOperationDoneSuccessfully(api().delete(SUBNETWORK_NAME));
      assertOperationDoneSuccessfully(api.networks().delete(SUBNETWORK_NAME));
   }

   private void assertSubnetworkEquals(Subnetwork result) {
      assertEquals(result.name(), SUBNETWORK_NAME);
      assertEquals(result.ipCidrRange(), SUBNETWORK_RANGE);
   }
}
