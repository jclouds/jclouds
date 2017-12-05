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

import static com.google.common.collect.Iterables.any;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.keystone.v3.domain.User;
import org.jclouds.openstack.keystone.v3.internal.BaseV3KeystoneApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

@Test(groups = "live", testName = "UserApiLiveTest", singleThreaded = true)
public class UserApiLiveTest extends BaseV3KeystoneApiLiveTest {

   private User user;
   
   @BeforeClass
   public void createTestUser() {
      user = api().create(getClass().getSimpleName(), "p4ssw0rd", true, null, null);
      assertNotNull(user);
   }
   
   @Test
   public void testListUsers() {
      assertTrue(any(api().list(), new Predicate<User>() {
         @Override
         public boolean apply(User input) {
            return input.id().equals(user.id());
         }
      }));
   }
   
   @Test
   public void testGetUser() {
      assertNotNull(api().get(user.id()));
   }
   
   @Test
   public void testUpdateUser() {
      api().update(user.id(), "Updated", null, null, null, null);
      user = api().get(user.id());
      assertEquals(user.name(), "Updated");
   }
   
   @Test
   public void testListGroups() {
      assertNotNull(api().listGroups(user.id()));
   }
   
   @Test
   public void testListProjects() {
      assertNotNull(api().listProjects(user.id()));
   }
   
   @Test
   public void testChangePassword() {
      api().changePassword(user.id(), "p4ssw0rd", "Newp4ssw0rd");
   }
   
   @AfterClass(alwaysRun = true)
   public void deleteUser() {
      assertTrue(api().delete(user.id()));
   }
   
   private UserApi api() {
      return api.getUserApi();
   }
}
