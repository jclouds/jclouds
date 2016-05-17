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

import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

@Test(groups = "unit", testName = "VMSizeApiMockTest", singleThreaded = true)
public class VMSizeApiMockTest extends BaseAzureComputeApiMockTest {

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/vmsizes.json"));
      final VMSizeApi vmSizeAPI = api.getVMSizeApi("westeurope");
      List<VMSize> vmSizes = vmSizeAPI.list();

      assertNotNull(vmSizes);
      assertEquals(vmSizes.size(), 3);
      assertEquals(
              vmSizes.get(0),
              VMSize.create("Standard_A0", 1, 1047552, 20480, 768, 1));


      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/providers/Microsoft.Compute/locations/westeurope/vmSizes?api-version=2015-06-15");
   }

   public void testEmptyList() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final VMSizeApi vmSizeAPI = api.getVMSizeApi("location");

      assertTrue(vmSizeAPI.list().isEmpty());

      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/providers/Microsoft.Compute/locations/location/vmSizes?api-version=2015-06-15");
   }
}
