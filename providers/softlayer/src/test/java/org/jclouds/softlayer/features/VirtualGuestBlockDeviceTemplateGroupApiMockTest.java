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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.internal.BaseSoftLayerMockTest;
import org.jclouds.softlayer.parse.PublicImagesParseTest;
import org.jclouds.softlayer.parse.VirtualGuestBlockDeviceTemplateGroupParseTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link VirtualGuestBlockDeviceTemplateGroupApi} class.
 */
@Test(groups = "unit", testName = "VirtualGuestBlockDeviceTemplateGroupApiMockTest")
public class VirtualGuestBlockDeviceTemplateGroupApiMockTest extends BaseSoftLayerMockTest {

   public void testListPublicImages() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/virtual_guest_block_device_template_group_get_public_images.json")));
      VirtualGuestBlockDeviceTemplateGroupApi api = getVirtualGuestBlockDeviceTemplateGroupApi(server);
      try {
         assertEquals(api.getPublicImages(), new PublicImagesParseTest().expected());
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest_Block_Device_Template_Group/getPublicImages?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription");
      } finally {
         server.shutdown();
      }
   }

   public void testEmptyListImages() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      VirtualGuestBlockDeviceTemplateGroupApi api = getVirtualGuestBlockDeviceTemplateGroupApi(server);
      try {
         assertTrue(api.getPublicImages().isEmpty());
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest_Block_Device_Template_Group/getPublicImages?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription");
      } finally {
         server.shutdown();
      }
   }

   public void testGetObject() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/virtual_guest_block_device_template_group_get.json")));
      VirtualGuestBlockDeviceTemplateGroupApi api = getVirtualGuestBlockDeviceTemplateGroupApi(server);
      try {
         assertEquals(api.getObject("3001812"), new VirtualGuestBlockDeviceTemplateGroupParseTest().expected());
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest_Block_Device_Template_Group/3001812/getObject?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullObject() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      VirtualGuestBlockDeviceTemplateGroupApi api = getVirtualGuestBlockDeviceTemplateGroupApi(server);
      try {
         assertNull(api.getObject("3001812"));
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest_Block_Device_Template_Group/3001812/getObject?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription");
      } finally {
         server.shutdown();
      }
   }

   private VirtualGuestBlockDeviceTemplateGroupApi getVirtualGuestBlockDeviceTemplateGroupApi(MockWebServer server) {
      return api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestBlockDeviceTemplateGroupApi();
   }

}
