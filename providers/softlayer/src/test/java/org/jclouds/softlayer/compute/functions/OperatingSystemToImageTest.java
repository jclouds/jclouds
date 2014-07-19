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
import static org.testng.Assert.assertTrue;

import org.jclouds.compute.domain.Image;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.SoftwareDescription;
import org.jclouds.softlayer.domain.SoftwareLicense;
import org.testng.annotations.Test;

/**
 * Tests the function that transforms SoftLayer OperatingSystem to generic image.
 */
@Test(groups = "unit", testName = "OperatingSystemToImageTest")
public class OperatingSystemToImageTest {

   @Test
   public void testOperatingSystemToImage() {
      OperatingSystem operatingSystem = OperatingSystem.builder()
              .id("123456789")
              .softwareLicense(SoftwareLicense.builder()
                      .softwareDescription(SoftwareDescription.builder()
                              .version("12.04-64 Minimal for CCI")
                              .referenceCode("UBUNTU_12_64")
                              .longDescription("Ubuntu Linux 12.04 LTS Precise Pangolin - Minimal Install (64 bit)")
                              .build())
                      .build())
              .build();
      Image image = new OperatingSystemToImage().apply(operatingSystem);

      assertEquals(image.getId(), operatingSystem.getId());
      String referenceCode = operatingSystem.getSoftwareLicense().getSoftwareDescription().getReferenceCode();
      assertEquals(image.getDescription(), referenceCode);
      assertTrue(image.getOperatingSystem().getFamily().toString().equalsIgnoreCase("UBUNTU"));
      assertEquals(image.getOperatingSystem().getVersion(), "12.04");
      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getStatus(), Image.Status.AVAILABLE);
   }

}
