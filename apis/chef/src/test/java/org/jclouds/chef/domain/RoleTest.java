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
package org.jclouds.chef.domain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

/**
 * Tests behaviors of {@code Role}.
 */
@Test(groups = { "unit" })
public class RoleTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void canNotAddEnvRunListMapThatIsNull() {
      Role.builder().envRunList((Map<String, List<String>>) null);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void canNotAddRunListForEnvironmentThatIsNull() {
      Role.builder().envRunList("does.not.matter", (List<String>) null);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void canNotUseNullEnvNameWhenAddingEnvRunListEntry() {
      Role.builder().envRunListElement((String) null, "does.not.matter");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void canNotUseNullEntryWhenAddingEnvRunListEntry() {
      Role.builder().envRunListElement("does.not.matter", (String) null);
   }

   public void multipleEnvRunListsCanBePopulated() {
      String env1 = "env1";
      String env2 = "env2";
      String env1Alpha = "env1.alpha";
      String env2Alpha = "env2.alpha";
      String env2Bravo = "env2.bravo";
      Role role = Role.builder().envRunListElement(env1, env1Alpha).envRunListElement(env2, env2Alpha)
            .envRunListElement(env2, env2Bravo).build();
      Map<String, List<String>> envRunList = role.getEnvRunList();
      assertNotNull(envRunList, "envRunList");
      assertEquals(envRunList.size(), 2, "envRunList.size");

      verifyRunListForEnvironment(envRunList, env1, env1Alpha);
      verifyRunListForEnvironment(envRunList, env2, env2Alpha, env2Bravo);
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void envRunListOnNewlyBuiltRoleIsImmutable() {
      String env = "env";
      Role role = Role.builder().envRunListElement(env, env + "1").build();
      role.getEnvRunList().put("does.not.matter", new ArrayList<String>());
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void envRunListEntriesOnNewlyBuiltRoleIsImmutable() {
      String env = "env";
      Role role = Role.builder().envRunListElement(env, env + "1").build();
      role.getEnvRunList().get(env).add("does.not.matter");
   }

   public void rolesWithSameEnvRunListAreEqual() {
      String env = "env";
      String entry = "entry";

      Role role1 = Role.builder().envRunListElement(env, entry).build();
      Role role2 = Role.builder().envRunListElement(env, entry).build();

      assertEquals(role1.hashCode(), role2.hashCode(), "hashCodes should be equal");
      assertEquals(role1, role2, "role1 should equal role2");
      assertEquals(role2, role1, "role2 should equal role1");
   }

   public void rolesWithDifferentEnvRunListAreNotEqual() {
      String env = "env";
      String entry = "entry";

      Role role1 = Role.builder().envRunListElement(env, entry.toUpperCase()).build();
      Role role2 = Role.builder().envRunListElement(env, entry.toLowerCase()).build();

      assertNotEquals(role1.hashCode(), role2.hashCode(), "hashCodes should not be equal");
      assertNotEquals(role1, role2, "role1 should not equal role2");
      assertNotEquals(role2, role1, "role2 should not equal role1");
   }

   public static void verifyRunListForEnvironment(Map<String, List<String>> envRunList, String envName,
         String... expectedEntries) {
      assertTrue(envRunList.containsKey(envName), "envRunList contains " + envName);
      assertEquals(envRunList.get(envName).size(), expectedEntries.length, "envRunList size for '" + envName);
      assertTrue(envRunList.get(envName).containsAll(Arrays.asList(expectedEntries)), "envRunList for e1 contains "
            + Arrays.asList(expectedEntries));
   }
}
