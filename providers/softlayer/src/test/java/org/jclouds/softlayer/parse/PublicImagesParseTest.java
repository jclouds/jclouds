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
package org.jclouds.softlayer.parse;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.softlayer.domain.SoftwareDescription;
import org.jclouds.softlayer.domain.VirtualDiskImage;
import org.jclouds.softlayer.domain.VirtualDiskImageSoftware;
import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplate;
import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup;
import org.jclouds.softlayer.internal.BaseSoftLayerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class PublicImagesParseTest extends
        BaseSoftLayerParseTest<Set<VirtualGuestBlockDeviceTemplateGroup>> {

   @Override
   public String resource() {
      return "/virtual_guest_block_device_template_group_get_public_images.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<VirtualGuestBlockDeviceTemplateGroup> expected() {
      return ImmutableSet.of(VirtualGuestBlockDeviceTemplateGroup.builder()
              .id(33352)
              .name("25G CentOS 6 32-bit")
              .statusId(1)
              .accountId(208938)
              .parentId(10616)
              .blockDevices(ImmutableSet.of(
                      VirtualGuestBlockDeviceTemplate.builder()
                              .id(42678)
                              .device("0")
                              .diskSpace(2597196288f)
                              .diskImageId(1692629)
                              .groupId(33352)
                              .units("B")
                              .diskImage(VirtualDiskImage.builder()
                                      .id(1692629)
                                      .uuid("3764c062-43d7-4488-8119-0cf847c3e4db")
                                      .capacity(25f)
                                      .units("GB")
                                      .typeId(241)
                                      .description("25G CentOS 6 32-bit")
                                      .name("25G CentOS 6 32-bit")
                                      .storageRepositoryId(863078)
                                      .softwareReferences(
                                              VirtualDiskImageSoftware.builder()
                                                      .id(1227351)
                                                      .softwareDescriptionId(885)
                                                      .softwareDescription(
                                                              SoftwareDescription.builder()
                                                                      .id(885)
                                                                      .longDescription("CentOS / CentOS / 6.0-32 Minimal for CCI")
                                                                      .manufacturer("CentOS")
                                                                      .name("CentOS")
                                                                      .operatingSystem(1)
                                                                      .referenceCode("CENTOS_6_32")
                                                                      .requiredUser("root")
                                                                      .version("6.0-32 Minimal for CCI")
                                                                      .controlPanel(0)
                                                                      .virtualLicense("0")
                                                                      .virtualizationPlatform("0")
                                                                      .build())
                                                      .build())
                                      .build())
                              .build(), VirtualGuestBlockDeviceTemplate.builder()
                      .id(42679)
                      .device("1")
                      .diskImageId(1692630)
                      .diskSpace(0f)
                      .groupId(33352)
                      .diskImage(VirtualDiskImage.builder()
                              .id(1692630)
                              .name("10617-SWAP")
                              .uuid("ae3d12c3-f624-4c3a-80f5-ceeea2259c16")
                              .capacity(2f)
                              .units("GB")
                              .typeId(246)
                              .description("10617-SWAP")
                              .storageRepositoryId(863078)
                              .build())
                      .build()))
              .build());
   }
}
