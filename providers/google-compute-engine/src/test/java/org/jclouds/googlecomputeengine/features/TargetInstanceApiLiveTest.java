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

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.NewTargetInstance;
import org.jclouds.googlecomputeengine.domain.TargetInstance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;


public class TargetInstanceApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String TARGET_INSTANCE_NAME = "test-target-instance-1";

   @Test(groups = "live")
   public void testInsertTargetInstance(){

      NewTargetInstance newTargetInstance = new NewTargetInstance.Builder()
         .name(TARGET_INSTANCE_NAME)
         .description("A test Target Instance")
         .build();

      assertOperationDoneSuccessfully(api.targetInstancesInZone(DEFAULT_ZONE_NAME).create(newTargetInstance));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetInstance")
   public void testGetTargetInstance(){
      TargetInstance targetInstance = api.targetInstancesInZone(DEFAULT_ZONE_NAME).get(TARGET_INSTANCE_NAME);

      assertNotNull(targetInstance);
      assertEquals(targetInstance.name(), TARGET_INSTANCE_NAME);
      assertEquals(targetInstance.description(), "A test Target Instance");
      assertEquals(targetInstance.zone(), getZoneUrl(DEFAULT_ZONE_NAME));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetInstance", alwaysRun = true)
   public void testListTargetInstance(){
      ListPage<TargetInstance> targetInstances = api.targetInstancesInZone(DEFAULT_ZONE_NAME)
            .list(filter("name eq " + TARGET_INSTANCE_NAME)).next();

      assertEquals(targetInstances.size(), 1);
      assertTargetInstanceEquals(Iterables.getOnlyElement(targetInstances));
   }

   @Test(groups = "live", dependsOnMethods = {"testListTargetInstance", "testGetTargetInstance"}, alwaysRun = true)
   public void testDeleteTargetInstance(){
      assertOperationDoneSuccessfully(api.targetInstancesInZone(DEFAULT_ZONE_NAME).delete(TARGET_INSTANCE_NAME));
   }

   private void assertTargetInstanceEquals(TargetInstance targetInstance){
      assertNotNull(targetInstance);
      assertEquals(targetInstance.name(), TARGET_INSTANCE_NAME);
      assertEquals(targetInstance.description(), "A test Target Instance");
      assertEquals(targetInstance.zone(), getZoneUrl(DEFAULT_ZONE_NAME));
   }
}
