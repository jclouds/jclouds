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
package org.jclouds.openstack.swift.blobstore.strategy.internal;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(testName = "MultipartNamingStrategyTest")
public class MultipartNamingStrategyTest {

   @Test
   public void testGetPartNameFirstOneHundred() {
      final MultipartNamingStrategy strategy = new MultipartNamingStrategy();
      final String key = "file.txt";
      final int numberParts = 100;

      // check the first 100
      for (int i = 0; i < numberParts; i++) {
         String partName = strategy.getPartName(key, i + 1, numberParts);
         assertEquals(String.format("file.txt/%03d", i + 1), partName);
      }
   }

   @Test
   public void testGetPartNameChoices() {
      final MultipartNamingStrategy strategy = new MultipartNamingStrategy();
      final String key = "file.txt";

      // check less than 10 parts
      assertEquals(strategy.getPartName(key, 1, 5), "file.txt/1");
      assertEquals(strategy.getPartName(key, 2, 5), "file.txt/2");
      assertEquals(strategy.getPartName(key, 5, 5), "file.txt/5");

      // check <= 10 parts
      assertEquals(strategy.getPartName(key, 1, 10), "file.txt/01");
      assertEquals(strategy.getPartName(key, 2, 10), "file.txt/02");
      assertEquals(strategy.getPartName(key, 10, 10), "file.txt/10");

      // check <= 100 parts
      assertEquals(strategy.getPartName(key, 1, 100), "file.txt/001");
      assertEquals(strategy.getPartName(key, 9, 100), "file.txt/009");
      assertEquals(strategy.getPartName(key, 10, 100), "file.txt/010");
      assertEquals(strategy.getPartName(key, 99, 100), "file.txt/099");
      assertEquals(strategy.getPartName(key, 100, 100), "file.txt/100");

      // check <= 5000 parts
      assertEquals(strategy.getPartName(key, 1, 5000), "file.txt/0001");
      assertEquals(strategy.getPartName(key, 10, 5000), "file.txt/0010");
      assertEquals(strategy.getPartName(key, 99, 5000), "file.txt/0099");
      assertEquals(strategy.getPartName(key, 100, 5000), "file.txt/0100");
      assertEquals(strategy.getPartName(key, 999, 5000), "file.txt/0999");
      assertEquals(strategy.getPartName(key, 4999, 5000), "file.txt/4999");
      assertEquals(strategy.getPartName(key, 5000, 500), "file.txt/5000");
   }
}

