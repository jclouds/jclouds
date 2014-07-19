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

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.SoftwareDescription;
import org.jclouds.softlayer.domain.SoftwareLicense;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * Tests the function that transforms SoftLayer VirtualGuest to generic image.
 */
@Test(groups = "unit", testName = "VirtualGuestToImageTest")
public class VirtualGuestToImageTest {

   OperatingSystemToImage operatingSystemToImage = Guice.createInjector().getInstance(OperatingSystemToImage.class);

   @Test
   public void testVirtualGuestToImageWhenOperatingSystemIsNull() {
      VirtualGuest virtualGuest = createVirtualGuestWithoutOperatingSystem();
      Image image = new VirtualGuestToImage(operatingSystemToImage).apply(virtualGuest);
      assertNotNull(image);
      assertEquals(image.getStatus(), Image.Status.UNRECOGNIZED);
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UNRECOGNIZED);
      assertEquals(image.getOperatingSystem().getVersion(), "UNRECOGNIZED");
   }

   @Test
   public void testVirtualGuestToImageWhenVirtualGuestIsSoftwareLicense() {
      VirtualGuest virtualGuest = createVirtualGuestWithoutSoftwareLicenseDetails();
      Image image = new VirtualGuestToImage(operatingSystemToImage).apply(virtualGuest);
      assertNotNull(image);
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UNRECOGNIZED);
      assertEquals(image.getOperatingSystem().getVersion(), "UNRECOGNIZED");
   }

   @Test
   public void testVirtualGuestToImageWithSoftwareLicense() {
      VirtualGuest virtualGuest = createVirtualGuestWithSoftwareLicenseDetails();
      Image image = new VirtualGuestToImage(operatingSystemToImage).apply(virtualGuest);
      assertNotNull(image);
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getOperatingSystem().getVersion(), "12.04");
      assertEquals(image.getOperatingSystem().is64Bit(), true);
   }

   private VirtualGuest createVirtualGuestWithoutOperatingSystem() {
      return VirtualGuest.builder()
              .domain("example.com")
              .hostname("host1")
              .id(1301396)
              .maxMemory(1024)
              .startCpus(1)
              .localDiskFlag(true)
              .datacenter(Datacenter.builder().name("test").build())
              .softwareLicense(SoftwareLicense.builder().build())
              .build();
   }

   private VirtualGuest createVirtualGuestWithoutSoftwareLicenseDetails() {
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

   private VirtualGuest createVirtualGuestWithSoftwareLicenseDetails() {
      return VirtualGuest.builder()
              .domain("example.com")
              .hostname("host1")
              .id(1301396)
              .maxMemory(1024)
              .startCpus(1)
              .localDiskFlag(true)
              .operatingSystem(OperatingSystem.builder().id("UBUNTU_LATEST")
                      .operatingSystemReferenceCode("UBUNTU_LATEST")
                      .softwareLicense(SoftwareLicense.builder()
                              .softwareDescription(SoftwareDescription.builder()
                                      .version("12.04-64 Minimal for CCI")
                                      .referenceCode("UBUNTU_12_64")
                                      .longDescription("Ubuntu Linux 12.04 LTS Precise Pangolin - Minimal Install (64 bit)")
                                      .build())
                              .build())
                      .build())
              .datacenter(Datacenter.builder().name("test").build())
              .build();
   }
}
