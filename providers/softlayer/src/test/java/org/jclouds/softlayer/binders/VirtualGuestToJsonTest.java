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
package org.jclouds.softlayer.binders;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.domain.VirtualGuestBlockDeviceTemplateGroup;
import org.jclouds.softlayer.domain.VirtualGuestNetworkComponent;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;

@Test(groups = "unit", testName = "VirtualGuestToJsonTest")
public class VirtualGuestToJsonTest {

   private Json json;

   @BeforeClass
   public void init() {
      json = new GsonWrapper(new Gson());
   }

   @Test
   public void testVirtualGuestWithOperatingSystem() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest").build();
      VirtualGuestToJson binder = new VirtualGuestToJson(json);
      VirtualGuest virtualGuestWithOS = VirtualGuest.builder()
              .hostname("hostname")
              .domain("domain")
              .startCpus(1)
              .maxMemory(1024)
              .datacenter(Datacenter.builder()
                      .name("datacenterName")
                      .build())
              .operatingSystem(OperatingSystem.builder().id("123456789")
                      .operatingSystemReferenceCode("UBUNTU_12_64")
                      .build())
              .localDiskFlag(true)
              .networkComponents(ImmutableSet.<VirtualGuestNetworkComponent>of())
              .build();

      request = binder.bindToRequest(request, virtualGuestWithOS);

      assertEquals(request.getPayload().getRawContent(), "{" +
              "\"parameters\":[{\"hostname\":\"hostname\",\"domain\":\"domain\",\"startCpus\":1,\"maxMemory\":1024,\"hourlyBillingFlag\":false,\"localDiskFlag\":true,\"dedicatedAccountHostOnlyFlag\":false,\"privateNetworkOnlyFlag\":false,\"operatingSystemReferenceCode\":\"UBUNTU_12_64\",\"datacenter\":{\"name\":\"datacenterName\"}}]}");
   }

   @Test
   public void testVirtualGuestWithVirtualGuestBlockDeviceTemplateGroup() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest").build();
      VirtualGuestToJson binder = new VirtualGuestToJson(json);
      VirtualGuest virtualGuestWithVirtualGuestBlockDeviceTemplateGroup = VirtualGuest.builder()
              .hostname("hostname")
              .domain("domain")
              .startCpus(1)
              .maxMemory(1024)
              .datacenter(Datacenter.builder()
                      .name("datacenterName")
                      .build())
              .blockDeviceTemplateGroup(VirtualGuestBlockDeviceTemplateGroup.builder()
                      .globalIdentifier("ffaafa98-4b4a-4fa7-b9f7-b1bad5ec50f0")
                      .build())
              .localDiskFlag(true)
              .networkComponents(ImmutableSet.<VirtualGuestNetworkComponent>of())
              .build();

      request = binder.bindToRequest(request, virtualGuestWithVirtualGuestBlockDeviceTemplateGroup);

      assertEquals(request.getPayload().getRawContent(), "{" +
              "\"parameters\":[{" +
              "\"hostname\":\"hostname\"," +
              "\"domain\":\"domain\"," +
              "\"startCpus\":1," +
              "\"maxMemory\":1024," +
              "\"hourlyBillingFlag\":false," +
              "\"localDiskFlag\":true," +
              "\"dedicatedAccountHostOnlyFlag\":false," +
              "\"privateNetworkOnlyFlag\":false," +
              "\"blockDeviceTemplateGroup\":{\"globalIdentifier\":\"ffaafa98-4b4a-4fa7-b9f7-b1bad5ec50f0\"}," +
              "\"datacenter\":{\"name\":\"datacenterName\"}}]}");
   }

}
