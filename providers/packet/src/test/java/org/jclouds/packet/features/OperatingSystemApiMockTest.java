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
import org.jclouds.packet.domain.OperatingSystem;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.packet.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "OperatingSystemApiMockTest", singleThreaded = true)
public class OperatingSystemApiMockTest extends BasePacketApiMockTest {

   public void testListOperatingSystems() throws InterruptedException {

      server.enqueue(jsonResponse("/operatingSystems-first.json"));
      server.enqueue(jsonResponse("/operatingSystems-last.json"));

      Iterable<OperatingSystem> operatingSystems = api.operatingSystemApi().list().concat();
      assertEquals(size(operatingSystems), 14); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/operating-systems");
      assertSent(server, "GET", "/operating-systems?page=2");
   }

   public void testListOperatingSystemsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<OperatingSystem> operatingSystems = api.operatingSystemApi().list().concat();

      assertTrue(isEmpty(operatingSystems));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/operating-systems");
   }

   public void testListOperatingSystemsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/operatingSystems-first.json"));

      Iterable<OperatingSystem> operatingSystems = api.operatingSystemApi().list(page(1).perPage(5));

      assertEquals(size(operatingSystems), 7);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/operating-systems?page=1&per_page=5");
   }

   public void testListOperatingSystemsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<OperatingSystem> operatingSystems = api.operatingSystemApi().list(page(1).perPage(5));

      assertTrue(isEmpty(operatingSystems));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/operating-systems?page=1&per_page=5");
   }

}
