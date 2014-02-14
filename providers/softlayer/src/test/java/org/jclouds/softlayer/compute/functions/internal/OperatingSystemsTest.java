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
package org.jclouds.softlayer.compute.functions.internal;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.Test;

@Test(singleThreaded = true, groups = "unit")
public class OperatingSystemsTest {

   @Test
   public void testOsFamily() {
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.CENTOS), OsFamily.CENTOS);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.DEBIAN), OsFamily.DEBIAN);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.RHEL), OsFamily.RHEL);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.UBUNTU), OsFamily.UBUNTU);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.WINDOWS), OsFamily.WINDOWS);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.CLOUD_LINUX), OsFamily.CLOUD_LINUX);
      assertEquals(OperatingSystems.osFamily().apply(OperatingSystems.VYATTACE), OsFamily.LINUX);
   }

   @Test
   public void testOsBits() {
      assertEquals(OperatingSystems.bits().apply("UBUNTU_12_64").intValue(), 64);
      assertEquals(OperatingSystems.bits().apply("UBUNTU_12_32").intValue(), 32);
   }

   @Test
   public void testOsVersion() {
      assertEquals(OperatingSystems.version().apply("12.04-64 Minimal for VSI"), "12.04");
      assertEquals(OperatingSystems.version().apply("STD 32 bit"), "STD");
   }

}
