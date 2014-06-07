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
package org.jclouds.openstack.keystone.v2_0.extensions;

import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.features.UserApi;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.jclouds.openstack.keystone.v2_0.options.CreateUserOptions;
import org.jclouds.openstack.keystone.v2_0.options.UpdateUserOptions;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Tests behavior of UserAdminApi
 */
@Test(groups = "live", testName = "UserAdminApiLiveTest", singleThreaded = true)
public class UserAdminApiLiveTest extends BaseKeystoneApiLiveTest {

   private Optional<? extends UserAdminApi> userAdminOption;
   private Optional<? extends UserApi> userApi;

   private User testUser;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setup() {
      super.setup();
      userAdminOption = api.getUserAdminApi();
      if (!userAdminOption.isPresent()) {
         throw new SkipException("The tests are skipped since OS-KSADM extension is not exposed through the Keystone API");
      }
      userApi = api.getUserApi();
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      if (testUser != null) {
         final String userId = testUser.getId();
         boolean success = userAdminOption.get().delete(userId);
         assertTrue(retry(new Predicate<UserApi>() {
            public boolean apply(UserApi userApi) {
               return userApi.get(userId) == null;
            }
         }, 5 * 1000L).apply(userApi.get()));
      }
      super.tearDown();
   }

   @Test
   public void testCreateUser() {
      testUser = userAdminOption.get().create("jclouds-test-user", "jclouds-test-password",
            CreateUserOptions.Builder.email("jclouds-test@jclouds.org").enabled(true));
      assertTrue(retry(new Predicate<UserApi>() {
         public boolean apply(UserApi userApi) {
            return userApi.get(testUser.getId()) != null;
         }
      }, 180 * 1000L).apply(userApi.get()));

      assertEquals(userApi.get().get(testUser.getId()).getName(), "jclouds-test-user");
      assertEquals(userApi.get().get(testUser.getId()).getEmail(), "jclouds-test@jclouds.org");
      assertEquals(userApi.get().get(testUser.getId()).isEnabled(), true);
   }

   @Test(dependsOnMethods = { "testCreateUser" })
   public void testUpdateUser() {
      testUser = userAdminOption.get().update(
            testUser.getId(),
            UpdateUserOptions.Builder.email("jclouds-test.modified@jclouds.org").enabled(false)
                  .name("jclouds-test-user-modified").password("jclouds-test-password-modified"));

      assertEquals(userApi.get().get(testUser.getId()).getName(), "jclouds-test-user-modified");
      assertEquals(userApi.get().get(testUser.getId()).getEmail(), "jclouds-test.modified@jclouds.org");
      assertEquals(userApi.get().get(testUser.getId()).isEnabled(), false);

   }
}
