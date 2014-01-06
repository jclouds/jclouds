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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TargetPoolApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String TARGETPOOL_NAME = "targetpool-api-live-test";
   private static final int TIME_WAIT = 30;

   private TargetPoolApi api() {
      return api.getTargetPoolApi(userProject.get(), DEFAULT_REGION_NAME);
   }

   @Test(groups = "live")
   public void testInsertTargetPool() {
      assertRegionOperationDoneSucessfully(api().create(TARGETPOOL_NAME), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool")
   public void testGetTargetPool() {
      TargetPool targetPool = api().get(TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.getName(), TARGETPOOL_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testGetTargetPool")
   public void testListTargetPool() {

      IterableWithMarker<TargetPool> targetPool = api().list(new ListOptions.Builder()
              .filter("name eq " + TARGETPOOL_NAME));
      assertEquals(targetPool.toList().size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = "testListTargetPool")
   public void testDeleteTargetPool() {
      assertRegionOperationDoneSucessfully(api().delete(TARGETPOOL_NAME), TIME_WAIT);
   }
}
