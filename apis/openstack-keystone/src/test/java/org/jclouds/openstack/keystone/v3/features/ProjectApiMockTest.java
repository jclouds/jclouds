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
package org.jclouds.openstack.keystone.v3.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.jclouds.openstack.keystone.v3.domain.Project;
import org.jclouds.openstack.keystone.v3.internal.BaseV3KeystoneApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "ProjectApiMockTest", singleThreaded = true)
public class ProjectApiMockTest extends BaseV3KeystoneApiMockTest {

   public void testListProjects() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/projects.json").setResponseCode(201));

      List<Project> projects = api.getProjectApi().list();
      assertFalse(projects.isEmpty());

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/projects");
   }

   public void testListProjectsReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      List<Project> projects = api.getProjectApi().list();
      assertTrue(projects.isEmpty());

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/projects");
   }

   public void testGetProject() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/project.json"));

      Project project = api.getProjectApi().get("2f9b30f706bc45d7923e055567be2e98");
      assertNotNull(project);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/projects/2f9b30f706bc45d7923e055567be2e98");
   }

   public void testGetProjectReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      Project project = api.getProjectApi().get("2f9b30f706bc45d7923e055567be2e98");
      assertNull(project);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/projects/2f9b30f706bc45d7923e055567be2e98");
   }

   public void testCreateProject() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/project.json"));

      Project project = api.getProjectApi().create("foo", null, true, false, null, null);
      assertNotNull(project);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "POST", "/projects", "{\"project\":{\"is_domain\":false,\"enabled\":true,\"name\":\"foo\"}}");
   }

   public void testUpdateProject() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/project.json"));

      Project project = api.getProjectApi().update("2f9b30f706bc45d7923e055567be2e98",
            Project.builder().name("foo").build());
      assertNotNull(project);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "PATCH", "/projects/2f9b30f706bc45d7923e055567be2e98",
            "{\"project\":{\"is_domain\":false,\"enabled\":true,\"name\":\"foo\"}}");
   }

   public void testDeleteProject() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response204());

      boolean deleted = api.getProjectApi().delete("2f9b30f706bc45d7923e055567be2e98");
      assertTrue(deleted);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "DELETE", "/projects/2f9b30f706bc45d7923e055567be2e98");
   }

   public void testDeleteProjectReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      boolean deleted = api.getProjectApi().delete("2f9b30f706bc45d7923e055567be2e98");
      assertFalse(deleted);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "DELETE", "/projects/2f9b30f706bc45d7923e055567be2e98");
   }

   public void testListTags() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(new MockResponse().setBody("{\"tags\":[\"foo\",\"bar\"]}"));

      Set<String> tags = api.getProjectApi().listTags("2f9b30f706bc45d7923e055567be2e98");
      assertEquals(tags, ImmutableSet.of("foo", "bar"));

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/projects/2f9b30f706bc45d7923e055567be2e98/tags");
   }

   public void testHasTag() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response204());

      boolean hasTag = api.getProjectApi().hasTag("2f9b30f706bc45d7923e055567be2e98", "foo");
      assertTrue(hasTag);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "HEAD", "/projects/2f9b30f706bc45d7923e055567be2e98/tags/foo");
   }

   public void testAddTag() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response201());

      api.getProjectApi().addTag("2f9b30f706bc45d7923e055567be2e98", "foo");

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "PUT", "/projects/2f9b30f706bc45d7923e055567be2e98/tags/foo");
   }

   public void testRemoveTag() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response204());

      api.getProjectApi().removeTag("2f9b30f706bc45d7923e055567be2e98", "foo");

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "DELETE", "/projects/2f9b30f706bc45d7923e055567be2e98/tags/foo");
   }

   public void testSetTags() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(new MockResponse().setBody("{\"tags\":[\"foo\",\"bar\"]}"));

      api.getProjectApi().setTags("2f9b30f706bc45d7923e055567be2e98", ImmutableSet.of("foo", "bar"));

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "PUT", "/projects/2f9b30f706bc45d7923e055567be2e98/tags", "{\"tags\":[\"foo\",\"bar\"]}");
   }
   
   public void testRemoveAllTags() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response204());

      api.getProjectApi().removeAllTags("2f9b30f706bc45d7923e055567be2e98");

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "DELETE", "/projects/2f9b30f706bc45d7923e055567be2e98/tags");
   }
}
