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
import static org.jclouds.profitbricks.util.Passwords.isValidPassword;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "PasswordsTest")
public class PasswordsTest {

   private final List<String> validPasswords = ImmutableList.of(
           "fKVasTnNm", "84625894", "QQQQQQQQ", "qqqqqqqq", "asdfghjk"
   );
   private final List<String> invalidPasswords = ImmutableList.of(
           "", "apachejclouds", "s0merand0mpassw0rd"
   );

   @Test
   public void testPasswordValidation() {
      for (String pwd : validPasswords)
         assertTrue(isValidPassword(pwd), "Should've been valid: " + pwd);

      for (String pwd : invalidPasswords)
         assertFalse(isValidPassword(pwd), "Should've been invalid: " + pwd);
   }

   @Test
   public void testGeneratorGeneratesValidPassword() {
      final int times = 50;
      for (int i = 0; i < times; i++) {
         String pwd = Passwords.generate();
         assertTrue(isValidPassword(pwd), "Failed with: " + pwd);
      }
   }
}
