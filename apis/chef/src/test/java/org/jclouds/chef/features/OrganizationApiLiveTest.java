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
package org.jclouds.chef.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.UUID;

import org.jclouds.chef.domain.Group;
import org.jclouds.chef.domain.User;
import org.jclouds.chef.internal.BaseChefLiveTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests behavior of the OrganizationApi.
 */
@Test(groups = "live", singleThreaded = true, testName = "OrganizationApiLiveTest")
public class OrganizationApiLiveTest extends BaseChefLiveTest {
   private static final String GROUP_NAME = System.getProperty("user.name") + "-jcloudstest";

   private OrganizationApi orgApi;

   @BeforeMethod
   public void skipIfApiNotAvailable() {
      // Throwing SkipExceptions only works with @Test and @BeforeMethod methods
      if (!api.organizationApi().isPresent()) {
         throw new SkipException("Organization api not available in this Chef version");
      }
      orgApi = api.organizationApi().get();
   }

   public void testGetUser() {
      User user = orgApi.getUser(identity);
      assertEquals(user.getUsername(), identity);
      assertNotNull(user.getPublicKey());
   }

   public void testGetUnexistingUser() {
      User user = orgApi.getUser(UUID.randomUUID().toString());
      assertNull(user);
   }

   public void testListGroups() {
      Set<String> groups = orgApi.listGroups();
      assertNotNull(groups);
      assertFalse(groups.isEmpty());
   }

   public void testGetUnexistingGroup() {
      Group group = orgApi.getGroup(UUID.randomUUID().toString());
      assertNull(group);
   }

   public void testCreateGroup() {
      orgApi.createGroup(GROUP_NAME);
      Group group = orgApi.getGroup(GROUP_NAME);
      assertNotNull(group);
      assertEquals(group.getGroupname(), GROUP_NAME);
   }

   @Test(dependsOnMethods = "testCreateGroup")
   public void testUpdateGroup() {
      Group group = orgApi.getGroup(GROUP_NAME);
      Group updated = Group.builder(group.getGroupname()) //
            .actors(group.getActors()) //
            .orgname(group.getOrgname()) //
            .name(group.getName()) //
            .groups(group.getGroups()) //
            .client(group.getOrgname() + "-validator") //
            .user(identity) //
            .build();

      orgApi.updateGroup(updated);
      group = orgApi.getGroup(GROUP_NAME);

      assertNotNull(group);
      assertTrue(group.getUsers().contains(identity));
      assertTrue(group.getClients().contains(group.getOrgname() + "-validator"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUpdateUnexistingGroup() {
      orgApi.updateGroup(Group.builder(UUID.randomUUID().toString()).build());
   }

   @Test(dependsOnMethods = "testUpdateGroup", alwaysRun = true)
   public void testDeleteGroup() {
      orgApi.deleteGroup(GROUP_NAME);
      Group group = orgApi.getGroup(GROUP_NAME);
      assertNull(group);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDeleteUnexistingGroup() {
      orgApi.deleteGroup(UUID.randomUUID().toString());
   }

}
