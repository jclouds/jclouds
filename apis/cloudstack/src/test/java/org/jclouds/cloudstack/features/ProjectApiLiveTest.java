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
package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.cloudstack.domain.Project;
import org.jclouds.cloudstack.internal.BaseCloudStackApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ProjectApi}
 */
@Test(groups = "live", singleThreaded = true, testName = "ProjectApiLiveTest")
public class ProjectApiLiveTest extends BaseCloudStackApiLiveTest {

   @Test
   public void testListAccounts() throws Exception {
      for (Project project : client.getProjectApi().listProjects())
         checkProject(project);
   }

   protected void checkProject(Project project) {
      assertNotNull(project.getId());
      assertEquals(project.toString(), client.getProjectApi().getProject(project.getId()).toString());
      assertNotNull(project.getState());
      assertNotEquals(project.getState(), Project.State.UNRECOGNIZED);
   }

}
