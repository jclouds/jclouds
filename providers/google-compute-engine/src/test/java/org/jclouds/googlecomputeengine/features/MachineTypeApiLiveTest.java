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

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.annotations.Test;

public class MachineTypeApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private MachineType machineType;

   private MachineTypeApi api() {
      return api.machineTypesInZone(DEFAULT_ZONE_NAME);
   }

   @Test(groups = "live")
   public void testListMachineType() {
      Iterator<ListPage<MachineType>> pageIterator = api().list(maxResults(1));
      assertTrue(pageIterator.hasNext());

      List<MachineType> machineTypeAsList = pageIterator.next();

      assertEquals(machineTypeAsList.size(), 1);

      this.machineType = machineTypeAsList.get(0);
   }

   @Test(groups = "live", dependsOnMethods = "testListMachineType")
   public void testGetMachineType() {
      MachineType machineType = api().get(this.machineType.name());
      assertNotNull(machineType);
      assertMachineTypeEquals(machineType, this.machineType);
   }

   private void assertMachineTypeEquals(MachineType result, MachineType expected) {
      assertEquals(result.name(), expected.name());
   }
}
