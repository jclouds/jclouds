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
public class
        VirtualGuestBlockDeviceTemplateGroupParseTest extends
        BaseSoftLayerParseTest<VirtualGuestBlockDeviceTemplateGroup> {

   @Override
   public String resource() {
      return "/virtual_guest_block_device_template_group_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public VirtualGuestBlockDeviceTemplateGroup expected() {
      return VirtualGuestBlockDeviceTemplateGroup.builder()
              .id(402456)
              .name("DST_Windows-2008_R2DC_x86-64_Base_ICOv2")
              .globalIdentifier("8df677d9-02a0-43b0-9cc8-88015fcb6ad7")
              .statusId(1)
              .accountId(278462)
              .summary("DST_Windows-2008_R2DC_x86-64_Base with preparation for ICO 2.4")
              .children(VirtualGuestBlockDeviceTemplateGroup.builder()
                              .id(402458)
                              .name("DST_Windows-2008_R2DC_x86-64_Base_ICOv2")
                              .statusId(1)
                              .accountId(278462)
                              .parentId(402456)
                              .summary("DST_Windows-2008_R2DC_x86-64_Base with preparation for ICO 2.4")
                              .blockDevices(ImmutableSet.of(
                                      VirtualGuestBlockDeviceTemplate.builder()
                                              .id(508130)
                                              .device("0")
                                              .diskSpace(20926382592f)
                                              .diskImageId(6865056)
                                              .groupId(402458)
                                              .units("B")
                                              .diskImage(VirtualDiskImage.builder()
                                                      .id(6865056)
                                                      .uuid("00c9bb78-9ac0-4f43-b4d8-35a6568c7db8")
                                                      .capacity(25f)
                                                      .units("GB")
                                                      .typeId(241)
                                                      .description("rrm-2k8r2dc-ico.dstdev.sl.edst.ibm.com")
                                                      .name("rrm-2k8r2dc-ico.dstdev.sl.edst.ibm.com")
                                                      .storageRepositoryId(2330744)
                                                      .softwareReferences(ImmutableSet.of(
                                                              VirtualDiskImageSoftware.builder()
                                                                      .id(4259392)
                                                                      .softwareDescriptionId(727)
                                                                      .softwareDescription(
                                                                              SoftwareDescription.builder()
                                                                                      .id(727)
                                                                                      .longDescription("Nimsoft / Nimsoft Robot / 5.0")
                                                                                      .manufacturer("Nimsoft")
                                                                                      .name("Nimsoft Robot")
                                                                                      .operatingSystem(0)
                                                                                      .requiredUser("administrator")
                                                                                      .version("5.0")
                                                                                      .controlPanel(0)
                                                                                      .virtualLicense("0")
                                                                                      .virtualizationPlatform("0")
                                                                                      .build())
                                                                      .build(),
                                                              VirtualDiskImageSoftware.builder()
                                                                      .id(4259394)
                                                                      .softwareDescriptionId(829)
                                                                      .softwareDescription(
                                                                              SoftwareDescription.builder()
                                                                                      .id(829)
                                                                                      .longDescription("Microsoft / Windows 2008 FULL DC 64 bit R2 SP1 / DC 64 bit")
                                                                                      .manufacturer("Microsoft")
                                                                                      .name("Windows 2008 FULL DC 64 bit R2 SP1")
                                                                                      .operatingSystem(1)
                                                                                      .referenceCode("WIN_2008-DC-R2-SP1_64")
                                                                                      .requiredUser("Administrator")
                                                                                      .version("DC 64 bit")
                                                                                      .controlPanel(0)
                                                                                      .virtualLicense("0")
                                                                                      .virtualizationPlatform("0")
                                                                                      .build())
                                                                      .build()))
                                                      .build())
                                              .build(),
                                      VirtualGuestBlockDeviceTemplate.builder()
                                              .id(508136)
                                              .device("1")
                                              .diskImageId(6865060)
                                              .groupId(402458)
                                              .diskImage(VirtualDiskImage.builder()
                                                      .id(6865060)
                                                      .name("7903014-SWAP")
                                                      .uuid("38eb1c23-0cf9-4325-b78a-95545829a36d")
                                                      .capacity(2f)
                                                      .units("GB")
                                                      .typeId(246)
                                                      .description("7903014-SWAP")
                                                      .storageRepositoryId(2330744)
                                                      .build())
                                              .build()))
                              .build(),
                      VirtualGuestBlockDeviceTemplateGroup.builder()
                              .id(402460)
                              .name("DST_Windows-2008_R2DC_x86-64_Base_ICOv2")
                              .statusId(1)
                              .accountId(278462)
                              .parentId(402456)
                              .summary("DST_Windows-2008_R2DC_x86-64_Base with preparation for ICO 2.4")
                              .blockDevices(
                                      VirtualGuestBlockDeviceTemplate.builder()
                                              .id(508132)
                                              .device("0")
                                              .diskSpace(20926382592f)
                                              .diskImageId(6865054)
                                              .groupId(402460)
                                              .units("B")
                                              .diskImage(VirtualDiskImage.builder()
                                                      .id(6865054)
                                                      .capacity(25f)
                                                      .description("rrm-2k8r2dc-ico.dstdev.sl.edst.ibm.com")
                                                      .name("rrm-2k8r2dc-ico.dstdev.sl.edst.ibm.com")
                                                      .storageRepositoryId(2330746)
                                                      .units("GB")
                                                      .typeId(241)
                                                      .uuid("ef20e61c-6814-47bf-8cd9-9d26f84d3789")
                                                      .softwareReferences(ImmutableSet.of(
                                                              VirtualDiskImageSoftware.builder()
                                                                      .id(4259396)
                                                                      .softwareDescriptionId(727)
                                                                      .softwareDescription(
                                                                              SoftwareDescription.builder()
                                                                                      .id(727)
                                                                                      .longDescription("Nimsoft / Nimsoft Robot / 5.0")
                                                                                      .manufacturer("Nimsoft")
                                                                                      .name("Nimsoft Robot")
                                                                                      .operatingSystem(0)
                                                                                      .requiredUser("administrator")
                                                                                      .version("5.0")
                                                                                      .controlPanel(0)
                                                                                      .virtualLicense("0")
                                                                                      .virtualizationPlatform("0")
                                                                                      .build())
                                                                      .build(),
                                                              VirtualDiskImageSoftware.builder()
                                                                      .id(4259398)
                                                                      .softwareDescriptionId(829)
                                                                      .softwareDescription(
                                                                              SoftwareDescription.builder()
                                                                                      .id(829)
                                                                                      .longDescription("Microsoft / Windows 2008 FULL DC 64 bit R2 SP1 / DC 64 bit")
                                                                                      .manufacturer("Microsoft")
                                                                                      .name("Windows 2008 FULL DC 64 bit R2 SP1")
                                                                                      .operatingSystem(1)
                                                                                      .referenceCode("WIN_2008-DC-R2-SP1_64")
                                                                                      .requiredUser("Administrator")
                                                                                      .version("DC 64 bit")
                                                                                      .controlPanel(0)
                                                                                      .virtualLicense("0")
                                                                                      .virtualizationPlatform("0")
                                                                                      .build())
                                                                      .build()))
                                                      .build())
                                              .build(),
                                      VirtualGuestBlockDeviceTemplate.builder()
                                              .id(508134)
                                              .device("1")
                                              .diskImageId(6865058)
                                              .groupId(402460)
                                              .diskImage(VirtualDiskImage.builder()
                                                      .id(6865058)
                                                      .name("7903014-SWAP")
                                                      .capacity(2f)
                                                      .description("7903014-SWAP")
                                                      .storageRepositoryId(2330746)
                                                      .typeId(246)
                                                      .units("GB")
                                                      .uuid("c84edb86-a9ce-4932-bf2c-c3e4b2b44ae9")
                                                      .build())
                                              .build())
                              .build())
              .build();
   }
}
