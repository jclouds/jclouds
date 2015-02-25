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
package org.jclouds.profitbricks.util;

import com.google.common.collect.ImmutableList;
import java.util.List;
import static org.jclouds.profitbricks.util.MacAddresses.isMacAddress;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "MacAddressesTest")
public class MacAddressesTest {

   private final List<String> expectedValidAddresses = ImmutableList.of(
           "aa:bb:cc:dd:ee:ff", "11:22:33:44:55:66"
   );
   private final List<String> expectedInvalidAddresses = ImmutableList.of(
           "AA:BB:CC:DD:EE:FF", "aa-bb-cc-dd-ee-ff", "", "aabbccddeeff",
           "aa:bb:cc:dd:ff", "gg:aa:bb:cc:dd:ee"
   );

   @Test
   public void testIsMacAddress() {
      for (String addr : expectedValidAddresses)
         assertTrue(isMacAddress(addr));

      for (String addr : expectedInvalidAddresses)
         assertFalse(isMacAddress(addr));
   }
}
