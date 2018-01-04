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
package org.jclouds.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "PasswordGeneratorTest")
public class PasswordGeneratorTest {
   
   @Test
   public void emptyPassword() {
      String password = new PasswordGenerator()
            .lower().count(0)
            .upper().count(0)
            .numbers().count(0)
            .symbols().count(0)
            .generate();
      assertEquals(password, "");
   }

   @Test
   public void onlyLowerCase() {
      String password = new PasswordGenerator()
            .upper().count(0)
            .numbers().count(0)
            .symbols().count(0)
            .generate();
      assertTrue(password.matches("^[a-z]+$"));
   }
   
   @Test
   public void lowerAndUpperWithConstrainedLength() {
      String password = new PasswordGenerator()
            .lower().min(2).max(5)
            .upper().count(3)
            .numbers().count(0)
            .symbols().count(0)
            .generate();
      assertTrue(password.matches("^[a-zA-Z]+$"));
      assertTrue(password.replaceAll("[A-Z]", "").matches("[a-z]{2,5}"));
      assertTrue(password.replaceAll("[a-z]", "").matches("[A-Z]{3}"));
   }
   
   @Test
   public void defaultGeneratorContainsAll() {
      String password = new PasswordGenerator().generate();
      assertTrue(password.matches(".*[a-z].*[a-z].*"));
      assertTrue(password.matches(".*[A-Z].*[A-Z].*"));
      assertTrue(password.matches(".*[0-9].*[0-9].*"));
      assertTrue(password.replaceAll("[a-zA-Z0-9]", "").length() > 0);
   }
   
   @Test
   public void characterExclusion() {
      String password = new PasswordGenerator()
            .lower().count(0)
            .upper().count(0)
            .numbers().exclude("012345".toCharArray())
            .symbols().count(0)
            .generate();
      assertTrue(password.matches("^[6-9]+$"));
   }
}
