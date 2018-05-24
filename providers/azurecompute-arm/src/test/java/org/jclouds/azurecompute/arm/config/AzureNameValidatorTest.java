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
package org.jclouds.azurecompute.arm.config;

import org.jclouds.azurecompute.arm.compute.config.AzureNameValidator;
import org.testng.annotations.Test;

import com.google.common.base.Strings;

@Test(groups = "unit", testName = "AzureNameValidatorTest")
public class AzureNameValidatorTest {

   AzureNameValidator validator = new AzureNameValidator();

   @Test
   public void testNamesValidity() {
      validator.validate("7sdaiDD00-_.rrr");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testEmptyName() {
      validator.validate("");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNullName() {
      validator.validate(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNameTooShort() {
      validator.validate("x");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNameTooLong() {
      validator.validate(Strings.repeat("x", 64));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidStartingCharacterInName() {
      validator.validate("_whatever");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidCharactersInName() {
      validator.validate("is/not/ok/");
   }

}
