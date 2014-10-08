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
package org.jclouds.openstack.swift.v1.features;

import static org.jclouds.openstack.swift.v1.options.CreateContainerOptions.Builder.anybodyRead;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "CreatePublicContainerLiveTest")
public class CreatePublicContainerLiveTest extends BaseSwiftApiLiveTest<SwiftApi> {

   private String name = getClass().getSimpleName();

   public void testAnybodyReadUpdatesMetadata() throws Exception {
      for (String regionId : api.getConfiguredRegions()) {
         api.getContainerApi(regionId).create(name, anybodyRead());
         assertTrue(api.getContainerApi(regionId).get(name).getAnybodyRead().get());
      }
   }

   @Override
   @AfterClass(groups = "live")
   public void tearDown() {
      for (String regionId : api.getConfiguredRegions()) {
         api.getContainerApi(regionId).deleteIfEmpty(name);
      }
      super.tearDown();
   }
}
