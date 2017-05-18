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
package org.jclouds.packet.features;

import org.jclouds.packet.compute.internal.BasePacketApiMockTest;
import org.jclouds.packet.domain.Facility;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.packet.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "FacilityApiMockTest", singleThreaded = true)
public class FacilityApiMockTest extends BasePacketApiMockTest {

   public void testListFacilities() throws InterruptedException {
      server.enqueue(jsonResponse("/facilities-first.json"));
      server.enqueue(jsonResponse("/facilities-last.json"));

      Iterable<Facility> facilities = api.facilityApi().list().concat();

      assertEquals(size(facilities), 3); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/facilities");
      assertSent(server, "GET", "/facilities?page=2");
   }

   public void testListFacilitiesReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Facility> facilities = api.facilityApi().list().concat();

      assertTrue(isEmpty(facilities));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/facilities");
   }

   public void testListFacilitiesWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/facilities-first.json"));

      Iterable<Facility> actions = api.facilityApi().list(page(1).perPage(2));

      assertEquals(size(actions), 2);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/facilities?page=1&per_page=2");
   }

   public void testListFacilitiesWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Facility> actions = api.facilityApi().list(page(1).perPage(2));

      assertTrue(isEmpty(actions));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/facilities?page=1&per_page=2");
   }

}
