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
package org.jclouds.softlayer.features;

import static com.google.common.collect.Iterables.get;
import static org.testng.Assert.assertNotNull;
import java.util.Set;

import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Sets;

/**
 * Tests behavior of {@code VirtualGuestBlockDeviceTemplateGroupApi} which depends on the account
 */
@Test(groups = "live")
public class VirtualGuestBlockDeviceTemplateGroupApiLiveTest extends BaseSoftLayerApiLiveTest {

   Set<VirtualGuestBlockDeviceTemplateGroup> publicImages = Sets.newHashSet();
   @BeforeClass
   void init() {
      publicImages = api().getPublicImages();
   }

   @Test
   public void testGetBlockDeviceTemplateGroups() {
      for (VirtualGuestBlockDeviceTemplateGroup publicImage : publicImages) {
         assertNotNull(publicImage);
      }
   }

   @Test(dependsOnMethods = "testGetBlockDeviceTemplateGroups")
   public void testGetObject() {
      if (!publicImages.isEmpty()) {
         VirtualGuestBlockDeviceTemplateGroup virtualGuestBlockDeviceTemplateGroup = api().getObject(get(publicImages, 0).getGlobalIdentifier());
         assertNotNull(virtualGuestBlockDeviceTemplateGroup);
      }
   }

   private VirtualGuestBlockDeviceTemplateGroupApi api() {
      return api.getVirtualGuestBlockDeviceTemplateGroupApi();
   }

}
