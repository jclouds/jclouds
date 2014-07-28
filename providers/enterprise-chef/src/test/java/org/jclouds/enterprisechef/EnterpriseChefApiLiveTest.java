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
package org.jclouds.enterprisechef;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.UUID;

import org.jclouds.chef.internal.BaseChefApiLiveTest;
import org.jclouds.enterprisechef.domain.Group;
import org.jclouds.enterprisechef.domain.User;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * Tests behavior of the EnterpriseChefApi.
 */
@Test(groups = "live", singleThreaded = true, testName = "EnterpriseChefApiLiveTest")
public class EnterpriseChefApiLiveTest extends BaseChefApiLiveTest<EnterpriseChefApi> {

   private static final String GROUP_NAME = System.getProperty("user.name") + "-jcloudstest";
   private static final String ORG_NAME = System.getProperty("test.enterprisechef.org");

   public EnterpriseChefApiLiveTest() {
      provider = "enterprisechef";
   }

   @Override
   @Test
   public void testSearchClientsWithOptions() throws Exception {
      // This test will fail because Enterprise Chef does not index client name.
      // Once it is fixes, the test should succeed.
      // See: http://tickets.opscode.com/browse/CHEF-2477
      super.testSearchClientsWithOptions();
   }

   public void testGetUser() {
      User user = api.getUser(identity);
      assertEquals(user.getUsername(), identity);
      assertNotNull(user.getPublicKey());
   }

   public void testGetUnexistingUser() {
      User user = api.getUser(UUID.randomUUID().toString());
      assertNull(user);
   }

   public void testListGroups() {
      Set<String> groups = api.listGroups();
      assertNotNull(groups);
      assertFalse(groups.isEmpty());
   }

   public void testGetUnexistingGroup() {
      Group group = api.getGroup(UUID.randomUUID().toString());
      assertNull(group);
   }

   public void testCreateGroup() {
      api.createGroup(GROUP_NAME);
      Group group = api.getGroup(GROUP_NAME);
      assertNotNull(group);
      assertEquals(group.getGroupname(), GROUP_NAME);
   }

   @Test(dependsOnMethods = "testCreateGroup")
   public void testUpdateGroup() {
      Group group = api.getGroup(GROUP_NAME);
      Group updated = Group.builder(group.getGroupname()) //
            .actors(group.getActors()) //
            .orgname(group.getOrgname()) //
            .name(group.getName()) //
            .groups(group.getGroups()) //
            .client(ORG_NAME + "-validator") //
            .user(identity) //
            .build();

      api.updateGroup(updated);
      group = api.getGroup(GROUP_NAME);

      assertNotNull(group);
      assertTrue(group.getUsers().contains(identity));
      assertTrue(group.getClients().contains(ORG_NAME + "-validator"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUpdateUnexistingGroup() {
      api.updateGroup(Group.builder(UUID.randomUUID().toString()).build());
   }

   @Test(dependsOnMethods = "testUpdateGroup")
   public void testDeleteGroup() {
      api.deleteGroup(GROUP_NAME);
      Group group = api.getGroup(GROUP_NAME);
      assertNull(group);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDeleteUnexistingGroup() {
      api.deleteGroup(UUID.randomUUID().toString());
   }

}
