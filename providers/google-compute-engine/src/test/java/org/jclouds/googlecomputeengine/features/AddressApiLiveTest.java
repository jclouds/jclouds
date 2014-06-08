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

import java.util.List;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class AddressApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String ADDRESS_NAME = "address-api-live-test-address";
   private static final int TIME_WAIT = 30;

   private AddressApi api() {
      return api.getAddressApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertAddress() {

      assertRegionOperationDoneSucessfully(api().createInRegion(DEFAULT_REGION_NAME, ADDRESS_NAME), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertAddress")
   public void testGetAddress() {
      Address address = api().getInRegion(DEFAULT_REGION_NAME, ADDRESS_NAME);
      assertNotNull(address);
      assertEquals(address.getName(), ADDRESS_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testGetAddress")
   public void testListAddress() {

      PagedIterable<Address> addresss = api().listInRegion(DEFAULT_REGION_NAME, new ListOptions.Builder()
              .filter("name eq " + ADDRESS_NAME));

      List<Address> addresssAsList = Lists.newArrayList(addresss.concat());

      assertEquals(addresssAsList.size(), 1);

   }

   @Test(groups = "live", dependsOnMethods = "testListAddress")
   public void testDeleteAddress() {

      assertRegionOperationDoneSucessfully(api().deleteInRegion(DEFAULT_REGION_NAME, ADDRESS_NAME), TIME_WAIT);
   }
}
