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
package org.jclouds.azurecompute.arm.domain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "SubnetTest")
public class SubnetTest {

   @Test
   public void testExtractVirtualNetwork() {

      assertEquals(Subnet.builder().build().virtualNetwork(), null);
      assertEquals(
            Subnet.builder()
                  .id("/subscriptions/subscription/resourceGroups/rg/providers/Microsoft.Network/virtualNetworks/vn/subnets/subnet")
                  .build().virtualNetwork(), "vn");
      assertInvalidId("/subscriptions/subscription/resourceGroups/rg/providers/Microsoft.Network/virtualNetworks");
      assertInvalidId("virtualNetworks/vn");
   }

   private static void assertInvalidId(String id) {
      try {
         Subnet.builder().id(id).build().virtualNetwork();
         fail("The given ID " + id + "should not match a valid virtual network");
      } catch (IllegalStateException ex) {
         // Expected
      }
   }
}
