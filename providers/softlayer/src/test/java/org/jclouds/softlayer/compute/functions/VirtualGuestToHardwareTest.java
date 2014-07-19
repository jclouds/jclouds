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
package org.jclouds.softlayer.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests the function that transforms SoftLayer VirtualGuest to generic hardware.
 */
@Test(groups = "unit", testName = "VirtualGuestToHardwareTest")
public class VirtualGuestToHardwareTest {

   @Test
   public void testVirtualGuestToHardware() {
      VirtualGuest virtualGuest = createVirtualGuest();
      Hardware hardware = new VirtualGuestToHardware().apply(virtualGuest);
      assertNotNull(hardware);
      assertEquals(hardware.getRam(), virtualGuest.getMaxMemory());
      assertTrue(hardware.getProcessors().size() == 1);
      assertEquals(Iterables.get(hardware.getProcessors(), 0).getCores(), (double) virtualGuest.getStartCpus());
   }

   private VirtualGuest createVirtualGuest() {
      return VirtualGuest.builder()
              .domain("example.com")
              .hostname("host1")
              .id(1301396)
              .maxMemory(1024)
              .startCpus(1)
              .localDiskFlag(true)
              .operatingSystem(OperatingSystem.builder().id("UBUNTU_LATEST")
                      .operatingSystemReferenceCode("UBUNTU_LATEST")
                      .build())
              .datacenter(Datacenter.builder().name("test").build())
              .build();
   }
}
