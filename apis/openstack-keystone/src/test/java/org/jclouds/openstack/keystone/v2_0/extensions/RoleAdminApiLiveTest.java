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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Tests behavior of RoleAdminApi
 */
@Test(groups = "live", testName = "RoleAdminApiLiveTest", singleThreaded = true)
public class RoleAdminApiLiveTest extends BaseKeystoneApiLiveTest {

   private Optional<? extends RoleAdminApi> roleAdminOption;

   private Role testRole;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setup() {
      super.setup();
      roleAdminOption = api.getRoleAdminApi();
      if (!roleAdminOption.isPresent()) {
         throw new SkipException("The tests are skipped since OS-KSADM extension is not exposed through the Keystone API");
      }
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      if (testRole != null) {
         final String roleId = testRole.getId();
         boolean success = roleAdminOption.get().delete(roleId);
         assertTrue(retry(new Predicate<RoleAdminApi>() {
            public boolean apply(RoleAdminApi roleApi) {
               return roleApi.get(roleId) == null;
            }
         }, 5 * 1000L).apply(roleAdminOption.get()));
      }
      super.tearDown();
   }

   public void testCreateRole() {
      testRole = roleAdminOption.get().create("jclouds-test-role");
      assertTrue(retry(new Predicate<RoleAdminApi>() {
         public boolean apply(RoleAdminApi roleApi) {
            return roleApi.get(testRole.getId()) != null;
         }
      }, 180 * 1000L).apply(roleAdminOption.get()));

      assertEquals(roleAdminOption.get().get(testRole.getId()).getName(), "jclouds-test-role");
   }

   public void testListRoles() {
      RoleAdminApi roleApi = roleAdminOption.get();
      Set<? extends Role> roles = roleApi.list().toSet();
      assertNotNull(roles);
      assertFalse(roles.isEmpty());
      for (Role role : roles) {
         Role aRole = roleApi.get(role.getId());
         assertEquals(aRole, role);
      }

   }

   @Test(dependsOnMethods = { "testCreateRole" })
   public void testGetRole() {

      Role testGetRole = roleAdminOption.get().get(testRole.getId());
      assertNotNull(testGetRole);
      assertEquals(testGetRole.getName(), "jclouds-test-role");

   }
}
