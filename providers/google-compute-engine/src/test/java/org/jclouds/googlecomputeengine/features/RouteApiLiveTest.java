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
import org.jclouds.googlecomputeengine.domain.Route;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.RouteOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RouteApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String DEST_RANGE = "20.10.0.0/16";
   private static final String IPV4_RANGE = "10.0.0.0/8";
   private static final String ROUTE_NAME = "route-api-live-test-route";
   private static final String ROUTE_NETWORK_NAME = "route-api-live-test-network";
   public static final int TIME_WAIT = 30;

   private RouteApi api() {
      return api.getRouteApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertRoute() {
      assertGlobalOperationDoneSucessfully(api.getNetworkApiForProject(userProject.get()).createInIPv4Range
              (ROUTE_NETWORK_NAME, IPV4_RANGE), TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api().createInNetwork(ROUTE_NAME,
              getNetworkUrl(userProject.get(), ROUTE_NETWORK_NAME),
              new RouteOptions().addTag("footag")
                      .addTag("bartag")
                      .description("RouteApi Live Test")
                      .destRange(DEST_RANGE)
                      .priority(1000)
                      .nextHopGateway(getGatewayUrl(userProject.get(), DEFAULT_GATEWAY_NAME))),
              TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertRoute")
   public void testGetRoute() {
      Route route = api().get(ROUTE_NAME);

      assertNotNull(route);
      assertRouteEquals(route);
   }

   @Test(groups = "live", dependsOnMethods = "testGetRoute")
   public void testListRoute() {

      PagedIterable<Route> routes = api().list(new ListOptions()
              .filter("name eq " + ROUTE_NAME));

      List<Route> routesAsList = Lists.newArrayList(routes.concat());

      assertEquals(routesAsList.size(), 1);

      assertRouteEquals(Iterables.getOnlyElement(routesAsList));

   }

   @Test(groups = "live", dependsOnMethods = "testListRoute")
   public void testDeleteRoute() {
      assertGlobalOperationDoneSucessfully(api().delete(ROUTE_NAME), TIME_WAIT);
      assertGlobalOperationDoneSucessfully(api.getNetworkApiForProject(userProject.get())
              .delete(ROUTE_NETWORK_NAME), TIME_WAIT);
   }

   private void assertRouteEquals(Route result) {
      assertEquals(result.getName(), ROUTE_NAME);
      assertEquals(result.getDestRange(), DEST_RANGE);
      assertEquals(result.getNextHopGateway().orNull(), getGatewayUrl(userProject.get(), DEFAULT_GATEWAY_NAME));
   }

}
