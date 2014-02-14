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
import org.jclouds.softlayer.parse.ListPublicImagesResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

@Test(groups = "unit", testName = "VirtualGuestBlockDeviceTemplateGroupApiExpectTest")
public class VirtualGuestBlockDeviceTemplateGroupApiExpectTest extends BaseSoftLayerApiExpectTest {

   public void testListPublicImagesWhenResponseIs2xx() {

      HttpRequest listPublicImagesRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest_Block_Device_Template_Group/getPublicImages?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse listPublicImagesResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/virtualGuestBlockDeviceTemplateGroup_public_images.json")).build();

      SoftLayerApi api = requestSendsResponse(listPublicImagesRequest, listPublicImagesResponse);

      assertEquals(api.getVirtualGuestBlockDeviceTemplateGroupApi().getPublicImages(),
              new ListPublicImagesResponseTest().expected());
   }

   public void testListPublicImagesWhenResponseIs4xx() {

      HttpRequest listPublicImagesRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest_Block_Device_Template_Group/getPublicImages?objectMask=children.blockDevices.diskImage.softwareReferences.softwareDescription")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse listPublicImagesResponse = HttpResponse.builder().statusCode(404).build();
      SoftLayerApi api = requestSendsResponse(listPublicImagesRequest, listPublicImagesResponse);
      assertTrue(Iterables.isEmpty(api.getVirtualGuestBlockDeviceTemplateGroupApi().getPublicImages()));
   }


}
