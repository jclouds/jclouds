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
package org.jclouds.digitalocean2.domain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "OperatingSystemTest")
public class OperatingSystemTest {

   public void testParseStandard64bit() {
      OperatingSystem os = OperatingSystem.create("12.10 x64", "Ubuntu");

      assertEquals(os.distribution(), Distribution.UBUNTU);
      assertEquals(os.version(), "12.10");
      assertEquals(os.arch(), "x64");
      assertTrue(os.is64bit());
   }

   public void testLongVersionStandard64bit() {
      OperatingSystem os = OperatingSystem.create("12.10.1 x64", "Ubuntu");

      assertEquals(os.distribution(), Distribution.UBUNTU);
      assertEquals(os.version(), "12.10.1");
      assertEquals(os.arch(), "x64");
      assertTrue(os.is64bit());
   }

   public void testParseStandard64bitWithPrefix() {
      OperatingSystem os = OperatingSystem.create("Arch Linux 12.10 x64 Desktop", "Arch Linux");

      assertEquals(os.distribution(), Distribution.ARCHLINUX);
      assertEquals(os.version(), "12.10");
      assertEquals(os.arch(), "x64");
      assertTrue(os.is64bit());
   }

   public void testParseStandard() {
      OperatingSystem os = OperatingSystem.create("12.10 x32", "Ubuntu");

      assertEquals(os.distribution(), Distribution.UBUNTU);
      assertEquals(os.version(), "12.10");
      assertEquals(os.arch(), "x32");
      assertFalse(os.is64bit());

      os = OperatingSystem.create("6.5 x64", "CentOS");

      assertEquals(os.distribution(), Distribution.CENTOS);
      assertEquals(os.version(), "6.5");
      assertEquals(os.arch(), "x64");
      assertTrue(os.is64bit());

      os = OperatingSystem.create("6.5 x64", "Centos");

      assertEquals(os.distribution(), Distribution.CENTOS);
      assertEquals(os.version(), "6.5");
      assertEquals(os.arch(), "x64");
      assertTrue(os.is64bit());
   }

   public void testParseNoArch() {
      OperatingSystem os = OperatingSystem.create("12.10", "Ubuntu");

      assertEquals(os.distribution(), Distribution.UBUNTU);
      assertEquals(os.version(), "12.10");
      assertEquals(os.arch(), "");
      assertFalse(os.is64bit());
   }

   public void testParseNoVersion() {
      OperatingSystem os = OperatingSystem.create("x64", "Ubuntu");

      assertEquals(os.distribution(), Distribution.UBUNTU);
      assertEquals(os.version(), "");
      assertEquals(os.arch(), "x64");
      assertTrue(os.is64bit());
   }

   public void testParseUnknownDistribution() {
      OperatingSystem os = OperatingSystem.create("12.04 x64", "Foo");

      assertEquals(os.distribution(), Distribution.UNRECOGNIZED);
      assertEquals(os.version(), "12.04");
      assertEquals(os.arch(), "x64");
      assertTrue(os.is64bit());
   }
}
