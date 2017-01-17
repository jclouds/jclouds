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
package org.jclouds.suppliers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

@Test(groups = "unit", testName = "SupplyKeyMatchingValueOrNullTest")
public class SupplyKeyMatchingValueOrNullTest {

   private static final Supplier<String> valueSupplier = Suppliers.ofInstance("v3");

   public void testValueFound() {
      SupplyKeyMatchingValueOrNull<String, String> supplier = supplier("k1", "v1", "k2", "v2", "k3", "v3");
      assertEquals(supplier.get(), "k3");
   }

   public void testFirstKeyIsReturnedIfValueNotFound() {
      SupplyKeyMatchingValueOrNull<String, String> supplier = supplier("k1", "v1", "k2", "v2", "k4", "v4");
      assertEquals(supplier.get(), "k1");
   }

   public void testFirstKeyIsReturnedIfMultipleValuesFound() {
      SupplyKeyMatchingValueOrNull<String, String> supplier = supplier("k1", "v1", "k2", "v3", "k3", "v3");
      assertEquals(supplier.get(), "k2");
   }

   public void testReturnsNullIfEmptyMap() {
      SupplyKeyMatchingValueOrNull<String, String> supplier = new SupplyKeyMatchingValueOrNull<String, String>(
            Suppliers.<Map<String, Supplier<String>>> ofInstance(ImmutableMap.<String, Supplier<String>> of()),
            valueSupplier);
      assertNull(supplier.get());
   }

   private static SupplyKeyMatchingValueOrNull<String, String> supplier(String k1, String v1, String k2, String v2,
         String k3, String v3) {
      return new SupplyKeyMatchingValueOrNull<String, String>(map(k1, v1, k2, v2, k3, v3), valueSupplier);
   }

   private static Supplier<Map<String, Supplier<String>>> map(String k1, String v1, String k2, String v2, String k3,
         String v3) {
      return Suppliers.<Map<String, Supplier<String>>> ofInstance(ImmutableMap.of(k1, Suppliers.ofInstance(v1), k2,
            Suppliers.ofInstance(v2), k3, Suppliers.ofInstance(v3)));
   }
}
