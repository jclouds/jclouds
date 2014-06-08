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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class MachineTypeApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private MachineType machineType;

   private MachineTypeApi api() {
      return api.getMachineTypeApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testListMachineType() {

      PagedIterable<MachineType> machineTypes = api().listInZone(DEFAULT_ZONE_NAME, new ListOptions.Builder()
              .maxResults(1));

      Iterator<IterableWithMarker<MachineType>> pageIterator = machineTypes.iterator();
      assertTrue(pageIterator.hasNext());

      IterableWithMarker<MachineType> singlePageIterator = pageIterator.next();
      List<MachineType> machineTypeAsList = Lists.newArrayList(singlePageIterator);

      assertSame(machineTypeAsList.size(), 1);

      this.machineType = Iterables.getOnlyElement(machineTypeAsList);
   }


   @Test(groups = "live", dependsOnMethods = "testListMachineType")
   public void testGetMachineType() {
      MachineType machineType = api().getInZone(DEFAULT_ZONE_NAME, this.machineType.getName());
      assertNotNull(machineType);
      assertMachineTypeEquals(machineType, this.machineType);
   }

   private void assertMachineTypeEquals(MachineType result, MachineType expected) {
      assertEquals(result.getName(), expected.getName());
   }
}
