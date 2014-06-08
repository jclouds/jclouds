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

import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class RegionOperationApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String ADDRESS_NAME = "region-operations-api-live-test-address";
   private Operation addOperation;
   private Operation deleteOperation;

   private RegionOperationApi api() {
      return api.getRegionOperationApiForProject(userProject.get());
   }

   private AddressApi addressApi() {
      return api.getAddressApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testCreateOperations() {
      //create some operations by adding and deleting metadata items
      // this will make sure there is stuff to listFirstPage
      addOperation = assertRegionOperationDoneSucessfully(addressApi().createInRegion(DEFAULT_REGION_NAME,
              ADDRESS_NAME), 20);
      deleteOperation = assertRegionOperationDoneSucessfully(addressApi().deleteInRegion(DEFAULT_REGION_NAME,
              ADDRESS_NAME), 20);

      assertNotNull(addOperation);
      assertNotNull(deleteOperation);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateOperations")
   public void testGetOperation() {
      Operation operation = api().getInRegion(DEFAULT_REGION_NAME, addOperation.getName());
      assertNotNull(operation);
      assertOperationEquals(operation, this.addOperation);
   }

   @Test(groups = "live", dependsOnMethods = "testCreateOperations")
   public void testListOperationsWithFiltersAndPagination() {
      PagedIterable<Operation> operations = api().listInRegion(DEFAULT_REGION_NAME, new ListOptions.Builder()
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
