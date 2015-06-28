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
package org.jclouds.digitalocean2.features;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.digitalocean2.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.digitalocean2.domain.Size;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2ApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "SizeApiMockTest", singleThreaded = true)
public class SizeApiMockTest extends BaseDigitalOcean2ApiMockTest {

   public void testListSizes() throws InterruptedException {
      server.enqueue(jsonResponse("/sizes-first.json"));
      server.enqueue(jsonResponse("/sizes-last.json"));

      Iterable<Size> sizes = api.sizeApi().list().concat();

      assertEquals(size(sizes), 9); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/sizes");
      assertSent(server, "GET", "/sizes?page=2&per_page=5");
   }

   public void testListSizesReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Size> sizes = api.sizeApi().list().concat();

      assertTrue(isEmpty(sizes));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/sizes");
   }

   public void testListSizesWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/sizes-first.json"));

      Iterable<Size> sizes = api.sizeApi().list(page(1).perPage(5));

      assertEquals(size(sizes), 5);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/sizes?page=1&per_page=5");
   }

   public void testListSizesWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Size> sizes = api.sizeApi().list(page(1).perPage(5));

      assertTrue(isEmpty(sizes));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/sizes?page=1&per_page=5");
   }
}
