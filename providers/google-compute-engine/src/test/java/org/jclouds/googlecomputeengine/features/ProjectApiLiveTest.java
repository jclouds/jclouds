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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Project;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.annotations.Test;

public class ProjectApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String METADATA_ITEM_KEY = "projectLiveTestTestProp";
   private static final String METADATA_ITEM_VALUE = "projectLiveTestTestValue";

   private ProjectApi projectApi() {
      return api.getProjectApi();
   }

   private Project project;
   private int initialMetadataSize;
   private String initialFingerprint;

   @Test(groups = "live")
   public void testGetProjectWhenExists() {
      this.project = projectApi().get(userProject.get());
      assertNotNull(project);
      assertNotNull(project.id());
      assertNotNull(project.name());
   }

   @Test(groups = "live")
   public void testGetProjectWhenNotExists() {
      Project project = projectApi().get("momma");
      assertNull(project);
   }

   @Test(groups = "live", dependsOnMethods = "testGetProjectWhenExists")
   public void addItemToMetadata() {
      this.initialMetadataSize = project.commonInstanceMetadata().size();
      this.initialFingerprint = this.project.commonInstanceMetadata().fingerprint();
      assertOperationDoneSuccessfully(addItemToMetadata(projectApi(), userProject.get(), METADATA_ITEM_KEY,
              METADATA_ITEM_VALUE));
      this.project = projectApi().get(userProject.get());
      assertNotNull(project);
      assertTrue(this.project.commonInstanceMetadata().containsKey(METADATA_ITEM_KEY),
              this.project.toString());
      assertEquals(this.project.commonInstanceMetadata().get(METADATA_ITEM_KEY), METADATA_ITEM_VALUE);
      assertNotNull(this.project.commonInstanceMetadata().fingerprint());
   }

   @Test(groups = "live", dependsOnMethods = "addItemToMetadata")
   public void testDeleteItemFromMetadata() {
      assertOperationDoneSuccessfully(deleteItemFromMetadata(projectApi(), userProject.get(), METADATA_ITEM_KEY));
      this.project = projectApi().get(userProject.get());
      assertNotNull(project);
      assertFalse(project.commonInstanceMetadata().containsKey(METADATA_ITEM_KEY));
      assertSame(this.project.commonInstanceMetadata().size(), initialMetadataSize);
      assertEquals(this.project.commonInstanceMetadata().fingerprint(), initialFingerprint);
   }

   /**
    * Adds an item to the Project's metadata
    * <p/>
    * Beyond it's use here it is also used as a cheap way of generating Operations to both test the OperationApi and
    * the pagination system.
    */
   public static Operation addItemToMetadata(ProjectApi projectApi, String projectName, String key, String value) {
      Project project = projectApi.get(projectName);
      assertNotNull(project);
      Metadata metadata = project.commonInstanceMetadata().clone().put(key, value);
      return projectApi.setCommonInstanceMetadata(projectName, metadata);
   }

   /**
    * Deletes an item from the Project's metadata
    * <p/>
    * Beyond it's use here it is also used as a cheap way of generating Operation's to both test the OperationApi and
    * the pagination system.
    */
   public static Operation deleteItemFromMetadata(ProjectApi projectApi, String projectName, String key) {
      Project project = projectApi.get(projectName);
      assertNotNull(project);
      Metadata metadata = project.commonInstanceMetadata().clone().remove(key);
      return projectApi.setCommonInstanceMetadata(projectName, metadata);
   }
}
