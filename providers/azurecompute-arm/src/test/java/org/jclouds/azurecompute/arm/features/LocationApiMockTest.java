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
package org.jclouds.azurecompute.arm.features;

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "LocationApiMockTest", singleThreaded = true)
public class LocationApiMockTest extends BaseAzureComputeApiMockTest {

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/locations.json"));
      final LocationApi locationAPI = api.getLocationApi();
      assertEquals(locationAPI.list(), ImmutableList.of(
              Location.create("/subscriptions/SUBSCRIPTIONID/locations/eastasia",
                      "eastasia", "East Asia", "114.188", "22.267")
      ));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/locations?api-version=2015-11-01");
   }

   public void testEmptyList() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final LocationApi locationAPI = api.getLocationApi();

      assertTrue(locationAPI.list().isEmpty());

      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/locations?api-version=2015-11-01");
   }
}
