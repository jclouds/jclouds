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

import java.util.List;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class NetworkApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String NETWORK_NAME = "network-api-live-test-network";
   private static final String IPV4_RANGE = "10.0.0.0/8";
   private static final int TIME_WAIT = 10;

   private NetworkApi api() {
      return api.getNetworkApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertNetwork() {

      assertGlobalOperationDoneSucessfully(api().createInIPv4Range(NETWORK_NAME, IPV4_RANGE), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertNetwork")
   public void testGetNetwork() {

      Network network = api().get(NETWORK_NAME);
      assertNotNull(network);
      assertNetworkEquals(network);
   }

   @Test(groups = "live", dependsOnMethods = "testGetNetwork")
   public void testListNetwork() {

      PagedIterable<Network> networks = api().list(new ListOptions.Builder()
              .filter("name eq " + NETWORK_NAME));

      List<Network> networksAsList = Lists.newArrayList(networks.concat());

      assertEquals(networksAsList.size(), 1);

      assertNetworkEquals(Iterables.getOnlyElement(networksAsList));

   }

   @Test(groups = "live", dependsOnMethods = "testListNetwork")
   public void testDeleteNetwork() {

      assertGlobalOperationDoneSucessfully(api().delete(NETWORK_NAME), TIME_WAIT);
   }

   private void assertNetworkEquals(Network result) {
      assertEquals(result.getName(), NETWORK_NAME);
      assertEquals(result.getIPv4Range(), IPV4_RANGE);
   }

}
