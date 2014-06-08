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

import static org.jclouds.googlecomputeengine.features.DiskApiLiveTest.TIME_WAIT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class ZoneOperationApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String DISK_NAME = "zone-operations-api-live-test-disk";
   private Operation addOperation;
   private Operation deleteOperation;

   private ZoneOperationApi api() {
      return api.getZoneOperationApiForProject(userProject.get());
   }

   private DiskApi diskApi() {
      return api.getDiskApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testCreateOperations() {
      //create some operations by creating and deleting a disk
      // this will make sure there is stuff to listFirstPage
      addOperation = assertZoneOperationDoneSucessfully(diskApi().createInZone(DISK_NAME, 1, DEFAULT_ZONE_NAME), TIME_WAIT);
      deleteOperation = assertZoneOperationDoneSucessfully(diskApi().deleteInZone(DEFAULT_ZONE_NAME, DISK_NAME), TIME_WAIT);

      assertNotNull(addOperation);
      assertNotNull(deleteOperation);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateOperations")
   public void testGetOperation() {
      Operation operation = api().getInZone(DEFAULT_ZONE_NAME, addOperation.getName());
      assertNotNull(operation);
      assertOperationEquals(operation, this.addOperation);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateOperations")
   public void testListOperationsWithFiltersAndPagination() {
      PagedIterable<Operation> operations = api().listInZone(DEFAULT_ZONE_NAME, new ListOptions.Builder()
//              .filter("operationType eq insert")
              .maxResults(1));

      // make sure that in spite of having only one result per page we get at least two results
      final AtomicInteger counter = new AtomicInteger();
      operations.firstMatch(new Predicate<IterableWithMarker<Operation>>() {

         @Override
         public boolean apply(IterableWithMarker<Operation> input) {
            counter.addAndGet(Iterables.size(input));
            return counter.get() == 2;
         }
      });
   }

   private void assertOperationEquals(Operation result, Operation expected) {
      assertEquals(result.getName(), expected.getName());
   }


}
