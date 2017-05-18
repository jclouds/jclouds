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
import org.jclouds.packet.domain.Plan;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.packet.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "PlanApiMockTest", singleThreaded = true)
public class PlanApiMockTest extends BasePacketApiMockTest {

   public void testListPlans() throws InterruptedException {
      server.enqueue(jsonResponse("/plans-first.json"));
      server.enqueue(jsonResponse("/plans-last.json"));

      Iterable<Plan> plans = api.planApi().list().concat();

      assertEquals(size(plans), 7); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/plans");
      assertSent(server, "GET", "/plans?page=2");
   }

   public void testListPlansReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Plan> plans = api.planApi().list().concat();

      assertTrue(isEmpty(plans));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/plans");
   }

   public void testListPlansWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/plans-first.json"));

      Iterable<Plan> plans = api.planApi().list(page(1).perPage(5));

      assertEquals(size(plans), 4);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/plans?page=1&per_page=5");
   }

   public void testListPlansWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Plan> plans = api.planApi().list(page(1).perPage(5));

      assertTrue(isEmpty(plans));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/plans?page=1&per_page=5");
   }

}
