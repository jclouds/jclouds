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
package org.jclouds.azurecompute.arm.features;

import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "LocationApiLiveTest")
public class LocationApiLiveTest extends BaseAzureComputeApiLiveTest {

   @Test
   public void testList() {
      for (Location location : api().list()) {
          assertTrue(!location.id().isEmpty());
      }
      assertTrue(!api().list().isEmpty());
   }

   private LocationApi api() {
      return api.getLocationApi();
   }
}
