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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.parse.GetVirtualGuestBlockDeviceTemplateGroupsResponseTest;
import org.jclouds.softlayer.parse.ListVirtualGuestsResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

@Test(groups = "unit", testName = "AccountApiExpectTest")
public class AccountApiExpectTest extends BaseSoftLayerApiExpectTest {

   public void testListVirtualGuestsWhenResponseIs2xx() {

      HttpRequest listVirtualGuestsRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests?objectMask=powerState%3BoperatingSystem.passwords%3Bdatacenter%3BbillingItem%3BblockDevices.diskImage%3BtagReferences")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse listVirtualGuestsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/account_list.json")).build();

      SoftLayerApi api = requestSendsResponse(listVirtualGuestsRequest, listVirtualGuestsResponse);

      assertEquals(api.getAccountApi().listVirtualGuests(),
              new ListVirtualGuestsResponseTest().expected());
   }

   public void testListVirtualGuestsWhenResponseIs4xx() {

      HttpRequest listVirtualGuestsRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests?objectMask=powerState%3BoperatingSystem.passwords%3Bdatacenter%3BbillingItem%3BblockDevices.diskImage%3BtagReferences")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse listVirtualGuestsResponse = HttpResponse.builder().statusCode(404).build();

      SoftLayerApi api = requestSendsResponse(listVirtualGuestsRequest, listVirtualGuestsResponse);

      assertTrue(Iterables.isEmpty(api.getAccountApi().listVirtualGuests()));
   }

   public void testGetBlockDeviceTemplateGroupsWhenResponseIs2xx() {

      HttpRequest getVirtualGuestBlockDeviceTemplateGroup = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/getBlockDeviceTemplateGroups?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getVirtualGuestBlockDeviceTemplateGroupResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/account_get_block_devices_template_groups.json")).build();

      SoftLayerApi api = requestSendsResponse(getVirtualGuestBlockDeviceTemplateGroup, getVirtualGuestBlockDeviceTemplateGroupResponse);

      assertEquals(api.getAccountApi().getBlockDeviceTemplateGroups(),
              new GetVirtualGuestBlockDeviceTemplateGroupsResponseTest().expected());
   }

   public void testGetBlockDeviceTemplateGroupsWhenResponseIs4xx() {

      HttpRequest getObjectRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/getBlockDeviceTemplateGroups?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getObjectResponse = HttpResponse.builder().statusCode(404).build();

      SoftLayerApi api = requestSendsResponse(getObjectRequest, getObjectResponse);

      assertTrue(Iterables.isEmpty(api.getAccountApi().getBlockDeviceTemplateGroups()));
   }

}
