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
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.DiskType;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.TargetInstance;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "AggregatedListApiLiveTest")
public class AggregatedListApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String DISK_NAME = "aggregated-list-api-live-test-disk";
   public static final int sizeGb = 1;

   private AggregatedListApi api() {
      return api.aggregatedList();
   }

   public void machineTypes() {
      Iterator<ListPage<MachineType>> pageIterator = api().machineTypes(maxResults(1));
      assertTrue(pageIterator.hasNext());

      List<MachineType> machineTypeAsList = pageIterator.next();

      assertEquals(machineTypeAsList.size(), 1);
   }

   public void addresses() {
      Iterator<ListPage<Address>> pageIterator = api().addresses(maxResults(1));
      // make sure that in spite of having only one result per page we get at
      // least two results
      int count = 0;
      for (; count < 2 && pageIterator.hasNext();) {
         ListPage<Address> result = pageIterator.next();
         if (!result.isEmpty()) {
            count++;
         }
      }
      if (count < 2) {
         throw new SkipException("Not enough addresses");
      }
      assertEquals(count, 2);
   }

   public void disks() {
      Iterator<ListPage<Disk>> pageIterator = api().disks(maxResults(1));
      // make sure that in spite of having only one result per page we get at
      // least two results
      int count = 0;
      for (; count < 2 && pageIterator.hasNext();) {
         ListPage<Disk> result = pageIterator.next();
         if (!result.isEmpty()) {
            count++;
         }
      }
      if (count < 2) {
         throw new SkipException("Not enough disks");
      }
      assertEquals(count, 2);
   }

   public void diskTypes() {
      Iterator<ListPage<DiskType>> pageIterator = api().diskTypes(maxResults(1));
      assertTrue(pageIterator.hasNext());

      List<DiskType> diskTypeAsList = pageIterator.next();

      assertEquals(diskTypeAsList.size(), 1);
   }

   public void globalOperations() {
      Iterator<ListPage<Operation>> pageIterator = api().globalOperations(maxResults(1));
      // make sure that in spite of having only one result per page we get at
      // least two results
      int count = 0;
      for (; count < 2 && pageIterator.hasNext();) {
         ListPage<Operation> result = pageIterator.next();
         if (!result.isEmpty()) {
            count++;
         }
      }
      if (count < 2) {
         throw new SkipException("Not enough global operations");
      }
      assertEquals(count, 2);
   }

   public void forwardingRules() {
      Iterator<ListPage<ForwardingRule>> pageIterator = api().forwardingRules(maxResults(1));
      // make sure that in spite of having only one result per page we get at
      // least two results
      int count = 0;
      for (; count < 2 && pageIterator.hasNext();) {
         ListPage<ForwardingRule> result = pageIterator.next();
         if (!result.isEmpty()) {
            count++;
         }
      }
      if (count < 2) {
         throw new SkipException("Not enough forwarding rules");
      }
      assertEquals(count, 2);
   }

   public void targetInstances() {
      Iterator<ListPage<TargetInstance>> pageIterator = api().targetInstances(maxResults(1));
      // make sure that in spite of having only one result per page we get at
      // least two results
      int count = 0;
      for (; count < 2 && pageIterator.hasNext();) {
         ListPage<TargetInstance> result = pageIterator.next();
         if (!result.isEmpty()) {
            count++;
         }
      }
      if (count < 2) {
         throw new SkipException("Not enough target instances");
      }
      assertEquals(count, 2);
   }

   public void targetPools() {
      Iterator<ListPage<TargetPool>> pageIterator = api().targetPools(maxResults(1));
      // make sure that in spite of having only one result per page we get at
      // least two results
      int count = 0;
      for (; count < 2 && pageIterator.hasNext();) {
         ListPage<TargetPool> result = pageIterator.next();
         if (!result.isEmpty()) {
            count++;
         }
      }
      if (count < 2) {
         throw new SkipException("Not enough target pools");
      }
      assertEquals(count, 2);
   }
}
