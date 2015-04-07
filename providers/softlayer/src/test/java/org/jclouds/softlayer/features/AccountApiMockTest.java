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
import static org.testng.Assert.assertTrue;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.config.SoftLayerParserModule;
import org.jclouds.softlayer.internal.BaseSoftLayerMockTest;
import org.jclouds.softlayer.parse.VirtualGuestBlockDeviceTemplateGroupsParseTest;
import org.jclouds.softlayer.parse.VirtualGuestsParseTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.softlayer.features.AccountApi} class.
 */
@Test(groups = "unit", testName = "AccountApiMockTest")
public class AccountApiMockTest extends BaseSoftLayerMockTest {

   public void testListVirtualGuests() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/account_list.json")));
      AccountApi api = getAccountApi(server);
      try {
         assertEquals(api.listVirtualGuests(), new VirtualGuestsParseTest().expected());
         assertSent(server, "GET", "/SoftLayer_Account/VirtualGuests?objectMask=powerState%3BoperatingSystem.passwords%3Bdatacenter%3BbillingItem%3BblockDevices.diskImage%3BtagReferences");
      } finally {
         server.shutdown();
      }
   }

   public void testEmptyListVirtualGuests() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      AccountApi api = getAccountApi(server);
      try {
         assertTrue(api.listVirtualGuests().isEmpty());
         assertSent(server, "GET", "/SoftLayer_Account/VirtualGuests?objectMask=powerState%3BoperatingSystem.passwords%3Bdatacenter%3BbillingItem%3BblockDevices.diskImage%3BtagReferences");
      } finally {
         server.shutdown();
      }
   }

   public void testGetBlockDeviceTemplateGroups() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/account_get_block_devices_template_groups.json")));
      AccountApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getAccountApi();
      try {
         assertEquals(api.getBlockDeviceTemplateGroups(), new VirtualGuestBlockDeviceTemplateGroupsParseTest().expected());
         assertSent(server, "GET", "/SoftLayer_Account/getBlockDeviceTemplateGroups?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription");
      } finally {
         server.shutdown();
      }
   }

   public void testEmptyBlockDeviceTemplateGroups() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      AccountApi api = getAccountApi(server);
      try {
         assertTrue(api.getBlockDeviceTemplateGroups().isEmpty());
         assertSent(server, "GET", "/SoftLayer_Account/getBlockDeviceTemplateGroups?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription");
      } finally {
         server.shutdown();
      }
   }

   private AccountApi getAccountApi(MockWebServer server) {
      return api(SoftLayerApi.class, server.getUrl("/").toString(), new
              JavaUrlHttpCommandExecutorServiceModule(), new SoftLayerParserModule()).getAccountApi();
   }

}
