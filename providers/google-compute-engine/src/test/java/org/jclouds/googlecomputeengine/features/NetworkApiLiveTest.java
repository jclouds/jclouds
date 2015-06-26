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
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "NetworkApiLiveTest")
public class NetworkApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String NETWORK_NAME = "network-api-live-test-network";
   private static final String IPV4_RANGE = "10.0.0.0/8";

   private NetworkApi api() {
      return api.networks();
   }

   @Test(groups = "live")
   public void testInsertNetwork() {
      assertOperationDoneSuccessfully(api().createInIPv4Range(NETWORK_NAME, IPV4_RANGE));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertNetwork")
   public void testGetNetwork() {
      Network network = api().get(NETWORK_NAME);
      assertNotNull(network);
      assertNetworkEquals(network);
   }

   @Test(groups = "live", dependsOnMethods = "testGetNetwork")
   public void testListNetwork() {
      Iterator<ListPage<Network>> networks = api().list(filter("name eq " + NETWORK_NAME));

      List<Network> networksAsList = networks.next();

      assertEquals(networksAsList.size(), 1);

      assertNetworkEquals(networksAsList.get(0));
   }

   @Test(groups = "live", dependsOnMethods = "testListNetwork")
   public void testDeleteNetwork() {
      assertOperationDoneSuccessfully(api().delete(NETWORK_NAME));
   }

   private void assertNetworkEquals(Network result) {
      assertEquals(result.name(), NETWORK_NAME);
      assertEquals(result.rangeIPv4(), IPV4_RANGE);
   }
}
