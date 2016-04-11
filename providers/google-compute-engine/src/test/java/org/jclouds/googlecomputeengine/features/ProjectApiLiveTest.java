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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Operation.Status;
import org.jclouds.googlecomputeengine.domain.Project;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

public class ProjectApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String METADATA_ITEM_KEY = "projectLiveTestTestProp";
   private static final String METADATA_ITEM_VALUE = "projectLiveTestTestValue";

   private Project project;
   private int initialMetadataSize;
   private String initialFingerprint;

   @Test(groups = "live")
   public void getProject() {
      project = api.project().get();
      assertNotNull(project);
      assertNotNull(project.id());
      assertNotNull(project.name());
   }

   @Test(groups = "live", dependsOnMethods = "getProject")
   public void addItemToMetadata() {
      initialMetadataSize = project.commonInstanceMetadata().size();
      initialFingerprint = project.commonInstanceMetadata().fingerprint();
      Metadata metadata = project.commonInstanceMetadata().put(METADATA_ITEM_KEY, METADATA_ITEM_VALUE);
      assertOperationDoneSuccessfully(api.project().setCommonInstanceMetadata(metadata));
      project = api.project().get();
      assertNotNull(project);
      assertTrue(project.commonInstanceMetadata().containsKey(METADATA_ITEM_KEY), project.toString());
      assertEquals(project.commonInstanceMetadata().get(METADATA_ITEM_KEY), METADATA_ITEM_VALUE);
      assertNotNull(project.commonInstanceMetadata().fingerprint());
   }

   @Test(groups = "live", dependsOnMethods = "addItemToMetadata")
   public void testDeleteItemFromMetadata() {
      Metadata metadata = project.commonInstanceMetadata().remove(METADATA_ITEM_KEY);
      assertOperationDoneSuccessfully(api.project().setCommonInstanceMetadata(metadata));
      project = api.project().get();
      assertNotNull(project);
      assertFalse(project.commonInstanceMetadata().containsKey(METADATA_ITEM_KEY));
      assertEquals(project.commonInstanceMetadata().size(), initialMetadataSize);
      assertEquals(project.commonInstanceMetadata().fingerprint(), initialFingerprint);
   }

   @Test(groups = "live", dependsOnMethods = "getProject")
   public void testSetUsageExportBucket() {
      AtomicReference<Operation> o = new AtomicReference<Operation>(api.project().setUsageExportBucket(
            "unexisting-bucket", "test-"));

      retry(new Predicate<AtomicReference<Operation>>() {
         @Override
         public boolean apply(AtomicReference<Operation> input) {
            input.set(api.operations().get(input.get().selfLink()));
            return Status.DONE == input.get().status();
         }
      }, operationDoneTimeout, operationDoneInterval, MILLISECONDS).apply(o);

      assertEquals(o.get().status(), Status.DONE);
      assertEquals(o.get().error().errors().get(0).code(), "PERMISSIONS_ERROR");
      assertEquals(o.get().error().errors().get(0).message(), "Required 'read' permission for 'unexisting-bucket'");
   }
}
