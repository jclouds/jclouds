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
public class
        VirtualGuestBlockDeviceTemplateGroupsParseTest extends
        BaseSoftLayerParseTest<Set<VirtualGuestBlockDeviceTemplateGroup>> {

   @Override
   public String resource() {
      return "/account_get_block_devices_template_groups.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<VirtualGuestBlockDeviceTemplateGroup> expected() {
      return ImmutableSet.of(VirtualGuestBlockDeviceTemplateGroup.builder()
              .id(82898)
              .name("Backup template for disk migration of 'ljvsilauncher1.scic.ibm.com'.")
              .globalIdentifier("e4055a07-13f5-4fa9-ab46-b023b65c47d2")
              .statusId(1)
              .accountId(278184)
              .children(VirtualGuestBlockDeviceTemplateGroup.builder()
                      .id(82900)
                      .name("Backup template for disk migration of 'ljvsilauncher1.scic.ibm.com'.")
                      .statusId(1)
                      .accountId(278184)
                      .parentId(82898)
                      .blockDevices(ImmutableSet.of(
                              VirtualGuestBlockDeviceTemplate.builder()
                                      .id(108100)
                                      .device("0")
                                      .diskSpace(21832020480f)
                                      .diskImageId(2190750)
                                      .groupId(82900)
                                      .units("B")
                                      .diskImage(VirtualDiskImage.builder()
                                              .id(2190750)
                                              .uuid("42423638-a54e-4d82-9b23-25af5fb13547")
                                              .capacity(25f)
                                              .units("GB")
                                              .typeId(241)
                                              .description("ljvsilauncher1.scic.ibm.com")
                                              .name("ljvsilauncher1.scic.ibm.com")
                                              .storageRepositoryId(906427)
                                              .softwareReferences(ImmutableSet.of(
                                                      VirtualDiskImageSoftware.builder()
                                                              .id(1498856)
                                                              .softwareDescriptionId(1076)
                                                              .softwareDescription(
                                                                      SoftwareDescription.builder()
                                                                              .id(1076)
                                                                              .longDescription("Microsoft / Windows 2012 FULL STD 64 bit / STD x64")
                                                                              .manufacturer("Microsoft")
                                                                              .name("Windows 2012 FULL STD 64 bit")
                                                                              .operatingSystem(1)
                                                                              .referenceCode("WIN_2012-STD_64")
                                                                              .requiredUser("Administrator")
                                                                              .version("STD x64")
                                                                              .controlPanel(0)
                                                                              .virtualLicense("0")
                                                                              .virtualizationPlatform("0")
                                                                              .build())
                                                              .build(),
                                                      VirtualDiskImageSoftware.builder()
                                                              .id(1498858)
                                                              .softwareDescriptionId(106)
                                                              .softwareDescription(
                                                                      SoftwareDescription.builder()
                                                                              .id(106)
                                                                              .longDescription("Microsoft / Windows Firewall / 1")
                                                                              .manufacturer("Microsoft")
                                                                              .name("Windows Firewall")
                                                                              .operatingSystem(0)
                                                                              .version("1")
                                                                              .controlPanel(0)
                                                                              .virtualLicense("0")
                                                                              .virtualizationPlatform("0")
                                                                              .build())
                                                              .build()))
                                              .build())
                                      .build(), VirtualGuestBlockDeviceTemplate.builder()
                                      .id(108102)
                                      .device("1")
                                      .diskImageId(2190752)
                                      .groupId(82900)
                                      .diskImage(VirtualDiskImage.builder()
                                              .id(2190752)
                                              .name("3334230-SWAP")
                                              .uuid("9f087bfb-3ed4-4985-a8b7-ac67bd8316e6")
                                              .capacity(2f)
                                              .units("GB")
                                              .typeId(246)
                                              .description("3334230-SWAP")
                                              .storageRepositoryId(906427)
                                              .build())
                                      .build()))
                      .build())
              .build());
   }
}
