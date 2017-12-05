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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.keystone.v3.domain.Project;
import org.jclouds.openstack.keystone.v3.internal.BaseV3KeystoneApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

@Test(groups = "live", testName = "ProjectApiLiveTest", singleThreaded = true)
public class ProjectApiLiveTest extends BaseV3KeystoneApiLiveTest {

   private Project project;
   
   @BeforeClass
   public void createTestProject() {
      project = api().create(getClass().getSimpleName(), null, true, false, null, null);
      assertNotNull(project.id());
   }
   
   @Test
   public void testListProjects() {
      assertTrue(any(api().list(), new Predicate<Project>() {
         @Override
         public boolean apply(Project input) {
            return input.id().equals(project.id());
         }
      }));
   }
   
   @Test
   public void testGetProject() {
      assertNotNull(api().get(project.id()));
   }
   
   @Test
   public void testUpdateProject() {
      Project updated = api().get(project.id());
      api().update(project.id(), updated.toBuilder().description("Updated").build());
      project = api().get(project.id());
      assertEquals(project.description(), "Updated");
   }
   
   @Test
   public void testSetAndListTags() {
      api().setTags(project.id(), ImmutableSet.of("foo", "bar"));
      Set<String> projectTags = api().listTags(project.id());
      assertEquals(projectTags, ImmutableSet.of("foo", "bar"));
   }
   
   @Test(dependsOnMethods = "testSetAndListTags")
   public void testHasTag() {
      assertTrue(api().hasTag(project.id(), "foo"));
   }
   
   @Test(dependsOnMethods = "testSetAndListTags")
   public void testAddTag() {
      api().addTag(project.id(), "three");
      assertTrue(api().hasTag(project.id(), "three"));
   }
   
   @Test(dependsOnMethods = "testSetAndListTags")
   public void testRemoveTag() {
      api().removeTag(project.id(), "bar");
      assertFalse(api().hasTag(project.id(), "bar"));
   }
   
   @Test(dependsOnMethods = "testRemoveTag")
   public void testRemoveAllTags() {
      api().removeAllTags(project.id());
      assertTrue(api().listTags(project.id()).isEmpty());
   }
   
   @AfterClass(alwaysRun = true)
   public void deleteProject() {
      assertTrue(api().delete(project.id()));
   }
   
   private ProjectApi api() {
      return api.getProjectApi();
   }
}
