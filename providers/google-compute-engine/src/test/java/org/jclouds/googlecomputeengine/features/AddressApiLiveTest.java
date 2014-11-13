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

import java.util.Iterator;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.annotations.Test;

public class AddressApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String ADDRESS_NAME = "address-api-live-test-address";

   private AddressApi api() {
      return api.addressesInRegion(DEFAULT_REGION_NAME);
   }

   @Test(groups = "live")
   public void testInsertAddress() {
      assertOperationDoneSuccessfully(api().create(ADDRESS_NAME));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertAddress")
   public void testGetAddress() {
      Address address = api().get(ADDRESS_NAME);
      assertNotNull(address);
      assertEquals(address.name(), ADDRESS_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testGetAddress")
   public void testListAddress() {
      Iterator<ListPage<Address>> addresses = api().list(filter("name eq " + ADDRESS_NAME));
      assertEquals(addresses.next().size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = "testListAddress")
   public void testDeleteAddress() {
      assertOperationDoneSuccessfully(api().delete(ADDRESS_NAME));
   }
}
