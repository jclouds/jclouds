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
import org.jclouds.packet.domain.Project;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.packet.domain.options.ListOptions.Builder.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "ProjectApiMockTest", singleThreaded = true)
public class ProjectApiMockTest extends BasePacketApiMockTest {

   public void testListProjects() throws InterruptedException {
      server.enqueue(jsonResponse("/projects-first.json"));
      server.enqueue(jsonResponse("/projects-last.json"));

      Iterable<Project> projects = api.projectApi().list().concat();

      assertEquals(size(projects), 8); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/projects");
      assertSent(server, "GET", "/projects?page=2");
   }

   public void testListProjectsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Project> projects = api.projectApi().list().concat();

      assertTrue(isEmpty(projects));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/projects");
   }

   public void testListProjectsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/projects-first.json"));

      Iterable<Project> actions = api.projectApi().list(page(1).perPage(5));

      assertEquals(size(actions), 5);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/projects?page=1&per_page=5");
   }

   public void testListProjectsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Project> actions = api.projectApi().list(page(1).perPage(5));

      assertTrue(isEmpty(actions));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/projects?page=1&per_page=5");
   }
   

}
