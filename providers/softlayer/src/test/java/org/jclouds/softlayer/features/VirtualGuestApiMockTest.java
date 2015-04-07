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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.config.SoftLayerParserModule;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.internal.BaseSoftLayerMockTest;
import org.jclouds.softlayer.parse.GetCreateObjectOptionsParseTest;
import org.jclouds.softlayer.parse.VirtualGuestParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link org.jclouds.softlayer.features.VirtualGuestApi} class.
 */
@Test(groups = "unit", testName = "VirtualGuestApiMockTest")
public class VirtualGuestApiMockTest extends BaseSoftLayerMockTest {

   public void testGetVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/virtual_guest_get.json")));
      VirtualGuestApi api = getVirtualGuestApi(server);

      try {
         assertEquals(api.getVirtualGuest(3001812), new VirtualGuestParseTest().expected());
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/3001812/getObject?objectMask=id%3Bhostname%3Bdomain%3BfullyQualifiedDomainName%3BpowerState%3BmaxCpu%3BmaxMemory%3BstatusId%3BoperatingSystem.passwords%3BprimaryBackendIpAddress%3BprimaryIpAddress%3BactiveTransactionCount%3BblockDevices.diskImage%3Bdatacenter%3BtagReferences%3BprivateNetworkOnlyFlag%3BsshKeys");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      VirtualGuestApi api = getVirtualGuestApi(server);
      try {
         assertNull(api.getVirtualGuest(3001812));
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/3001812/getObject?objectMask=id%3Bhostname%3Bdomain%3BfullyQualifiedDomainName%3BpowerState%3BmaxCpu%3BmaxMemory%3BstatusId%3BoperatingSystem.passwords%3BprimaryBackendIpAddress%3BprimaryIpAddress%3BactiveTransactionCount%3BblockDevices.diskImage%3Bdatacenter%3BtagReferences%3BprivateNetworkOnlyFlag%3BsshKeys");
      } finally {
         server.shutdown();
      }
   }

   public void testCreateVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/virtual_guest_get.json")));
      VirtualGuestApi api = getVirtualGuestApi(server);
      VirtualGuest virtualGuest = createVirtualGuest();
      try {
         assertEquals(api.createVirtualGuest(virtualGuest), new VirtualGuestParseTest().expected());
         assertSent(server, "POST", "/SoftLayer_Virtual_Guest");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse());
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      try {
         assertTrue(api.deleteVirtualGuest(1301396));
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/1301396/deleteObject");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteNonExistingVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      try {
         assertFalse(api.deleteVirtualGuest(1301396));
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/1301396/deleteObject");
      } finally {
         server.shutdown();
      }
   }

   public void testGetCreateObjectOptions() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/container_virtual_guest_configuration.json")));
      VirtualGuestApi api = getVirtualGuestApi(server);
      try {
         assertEquals(api.getCreateObjectOptions(), new GetCreateObjectOptionsParseTest().expected());
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/getCreateObjectOptions");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullCreateObjectOptions() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      try {
         assertNull(api.getCreateObjectOptions());
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/getCreateObjectOptions");
      } finally {
         server.shutdown();
      }
   }

   public void testRebootHardVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/datacenter_get.json")));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      try {
         api.rebootHardVirtualGuest(1301396);
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/1301396/rebootHard.json");
      } finally {
         server.shutdown();
      }
   }

   public void testRebootNonExistingVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      try {
         api.rebootHardVirtualGuest(1301396);
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/1301396/rebootHard.json");
      } finally {
         server.shutdown();
      }
   }

   public void testPauseVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/datacenter_get.json")));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      try {
         api.pauseVirtualGuest(1301396);
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/1301396/pause.json");
      } finally {
         server.shutdown();
      }
   }

   public void testPauseNonExistingVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      try {
         api.pauseVirtualGuest(1301396);
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/1301396/pause.json");
      } finally {
         server.shutdown();
      }
   }

   public void testResumeVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/datacenter_get.json")));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      try {
         api.resumeVirtualGuest(1301396);
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/1301396/resume.json");
      } finally {
         server.shutdown();
      }
   }

   public void testResumeNonExistingVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      try {
         api.resumeVirtualGuest(1301396);
         assertSent(server, "GET", "/SoftLayer_Virtual_Guest/1301396/resume.json");
      } finally {
         server.shutdown();
      }
   }

   public void testSetTagsOnVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/virtual_guest_set_tags_response.json")));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      VirtualGuest virtualGuest = createVirtualGuest();
      try {
         assertTrue(api.setTags(virtualGuest.getId(), ImmutableSet.of("test1", "test2", "test3")));
         assertSent(server, "POST", "/SoftLayer_Virtual_Guest/1301396/setTags");
      } finally {
         server.shutdown();
      }
   }

   public void testSetTagsOnNonExistingVirtualGuest() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      VirtualGuestApi api = api(SoftLayerApi.class, server.getUrl("/").toString()).getVirtualGuestApi();
      VirtualGuest virtualGuest = createVirtualGuest();
      try {
         assertFalse(api.setTags(virtualGuest.getId(), ImmutableSet.of("test1", "test2", "test3")));
         assertSent(server, "POST", "/SoftLayer_Virtual_Guest/1301396/setTags");
      } finally {
         server.shutdown();
      }
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

   private VirtualGuestApi getVirtualGuestApi(MockWebServer server) {
      return api(SoftLayerApi.class, server.getUrl("/").toString(), new
              JavaUrlHttpCommandExecutorServiceModule(), new SoftLayerParserModule()).getVirtualGuestApi();
   }

}
