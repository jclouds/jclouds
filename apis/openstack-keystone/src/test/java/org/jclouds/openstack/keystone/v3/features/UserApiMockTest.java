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

import org.jclouds.openstack.keystone.v3.domain.Group;
import org.jclouds.openstack.keystone.v3.domain.Project;
import org.jclouds.openstack.keystone.v3.domain.User;
import org.jclouds.openstack.keystone.v3.internal.BaseV3KeystoneApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "UserApiMockTest", singleThreaded = true)
public class UserApiMockTest extends BaseV3KeystoneApiMockTest {

   public void testListUsers() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/users.json"));

      List<User> users = api.getUserApi().list();
      assertFalse(users.isEmpty());

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/users");
   }

   public void testListUsersReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      List<User> users = api.getUserApi().list();
      assertTrue(users.isEmpty());

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/users");
   }

   public void testGetUser() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/user.json"));

      User user = api.getUserApi().get("0bedc61110fd4e94a251260a47f18f29");
      assertNotNull(user);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/users/0bedc61110fd4e94a251260a47f18f29");
   }

   public void testGetUserReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      User user = api.getUserApi().get("0bedc61110fd4e94a251260a47f18f29");
      assertNull(user);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/users/0bedc61110fd4e94a251260a47f18f29");
   }

   public void testCreateUser() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/user.json"));

      User user = api.getUserApi().create("user", "p4ssw0rd", true, "123", "789");
      assertNotNull(user);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "POST", "/users", "{\"user\":{\"name\":\"user\",\"password\":\"p4ssw0rd\",\"enabled\":true,"
            + "\"domain_id\":\"123\",\"default_project_id\":\"789\"}}");
   }

   public void testUpdateUser() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/user.json"));

      User user = api.getUserApi().update("0bedc61110fd4e94a251260a47f18f29", "foo", null, null, null, null);
      assertNotNull(user);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "PATCH", "/users/0bedc61110fd4e94a251260a47f18f29", "{\"user\":{\"name\":\"foo\"}}");
   }

   public void testDeleteUser() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response204());

      boolean deleted = api.getUserApi().delete("0bedc61110fd4e94a251260a47f18f29");
      assertTrue(deleted);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "DELETE", "/users/0bedc61110fd4e94a251260a47f18f29");
   }

   public void testDeleteUserReturns404() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response404());

      boolean deleted = api.getUserApi().delete("0bedc61110fd4e94a251260a47f18f29");
      assertFalse(deleted);

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "DELETE", "/users/0bedc61110fd4e94a251260a47f18f29");
   }

   public void testListGroups() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/groups.json"));

      List<Group> groups = api.getUserApi().listGroups("0bedc61110fd4e94a251260a47f18f29");
      assertFalse(groups.isEmpty());

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/users/0bedc61110fd4e94a251260a47f18f29/groups");
   }

   public void testListProjects() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(jsonResponse("/v3/projects.json"));

      List<Project> projects = api.getUserApi().listProjects("0bedc61110fd4e94a251260a47f18f29");
      assertFalse(projects.isEmpty());

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "GET", "/users/0bedc61110fd4e94a251260a47f18f29/projects");
   }

   public void testChangePassword() throws InterruptedException {
      enqueueAuthentication(server);
      server.enqueue(response204());

      api.getUserApi().changePassword("0bedc61110fd4e94a251260a47f18f29", "foo", "bar");

      assertEquals(server.getRequestCount(), 2);
      assertAuthentication(server);
      assertSent(server, "POST", "/users/0bedc61110fd4e94a251260a47f18f29/password",
            "{\"user\":{\"original_password\":\"foo\",\"password\":\"bar\"}}");
   }
}
