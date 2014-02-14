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

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.parse.CreateVirtualGuestResponseTest;
import org.jclouds.softlayer.parse.GetCreateObjectOptionsResponseTest;
import org.jclouds.softlayer.parse.GetVirtualGuestResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "VirtualGuestApiExpectTest")
public class VirtualGuestApiExpectTest extends BaseSoftLayerApiExpectTest {

   public void testGetVirtualGuestWhenResponseIs2xx() {

      HttpRequest getVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/3001812/getObject?objectMask=id%3Bhostname%3Bdomain%3BfullyQualifiedDomainName%3BpowerState%3BmaxCpu%3BmaxMemory%3BstatusId%3BoperatingSystem.passwords%3BprimaryBackendIpAddress%3BprimaryIpAddress%3BactiveTransactionCount%3BblockDevices.diskImage%3Bdatacenter%3BtagReferences")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getVirtualGuestResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/virtual_guest_get.json")).build();

      SoftLayerApi api = requestSendsResponse(getVirtualGuest, getVirtualGuestResponse);

      assertEquals(api.getVirtualGuestApi().getVirtualGuest(3001812),
              new GetVirtualGuestResponseTest().expected());
   }

   public void testGetVirtualGuestWhenResponseIs4xx() {

      HttpRequest getObjectRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/3001812/getObject?objectMask=id%3Bhostname%3Bdomain%3BfullyQualifiedDomainName%3BpowerState%3BmaxCpu%3BmaxMemory%3BstatusId%3BoperatingSystem.passwords%3BprimaryBackendIpAddress%3BprimaryIpAddress%3BactiveTransactionCount%3BblockDevices.diskImage%3Bdatacenter%3BtagReferences")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getObjectResponse = HttpResponse.builder().statusCode(404).build();
      SoftLayerApi api = requestSendsResponse(getObjectRequest, getObjectResponse);
      assertNull(api.getVirtualGuestApi().getVirtualGuest(3001812));
   }

   public void testCreateVirtualGuestWhenResponseIs2xx() {

      HttpRequest createVirtualGuest = HttpRequest.builder().method("POST")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
              .payload(payloadFromResourceWithContentType("/virtual_guest_create.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse createVirtualGuestResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/virtual_guest_create_response.json")).build();

      VirtualGuestApi api = requestSendsResponse(createVirtualGuest, createVirtualGuestResponse).getVirtualGuestApi();
      VirtualGuest virtualGuest = createVirtualGuest();
      VirtualGuest result = api.createVirtualGuest(virtualGuest);
      assertEquals(result, new CreateVirtualGuestResponseTest().expected());
   }

   public void testCreateVirtualGuestWhenResponseIs4xx() {

      HttpRequest createVirtualGuest = HttpRequest.builder().method("POST")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
              .payload(payloadFromResourceWithContentType("/virtual_guest_create.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse createVirtualGuestResponse = HttpResponse.builder().statusCode(404).build();
      SoftLayerApi api = requestSendsResponse(createVirtualGuest, createVirtualGuestResponse);
      VirtualGuest virtualGuest = createVirtualGuest();
      assertNull(api.getVirtualGuestApi().createVirtualGuest(virtualGuest));
   }

   public void testDeleteVirtualGuestWhenResponseIs2xx() {

      HttpRequest deleteVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/deleteObject")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
              .build();

      HttpResponse deleteVirtualGuestResponse = HttpResponse.builder().statusCode(200).build();

      VirtualGuestApi api = requestSendsResponse(deleteVirtualGuest, deleteVirtualGuestResponse).getVirtualGuestApi();
      assertTrue(api.deleteVirtualGuest(1301396));
   }

   public void testDeleteVirtualGuestWhenResponseIs4xx() {

      HttpRequest deleteVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/deleteObject")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
              .build();

      HttpResponse deleteVirtualGuestResponse = HttpResponse.builder().statusCode(404).build();
      VirtualGuestApi api = requestSendsResponse(deleteVirtualGuest, deleteVirtualGuestResponse).getVirtualGuestApi();
      assertFalse(api.deleteVirtualGuest(1301396));
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

   public void testGetCreateObjectOptionsWhenResponseIs2xx() {

      HttpRequest getVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/getCreateObjectOptions")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getVirtualGuestResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/container_virtual_guest_configuration.json")).build();

      SoftLayerApi api = requestSendsResponse(getVirtualGuest, getVirtualGuestResponse);

      assertEquals(api.getVirtualGuestApi().getCreateObjectOptions(),
              new GetCreateObjectOptionsResponseTest().expected());
   }

   public void testGetCreateObjectOptionsWhenResponseIs4xx() {

      HttpRequest getObjectRequest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/getCreateObjectOptions")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse getObjectResponse = HttpResponse.builder().statusCode(404).build();
      SoftLayerApi api = requestSendsResponse(getObjectRequest, getObjectResponse);
      assertNull(api.getVirtualGuestApi().getCreateObjectOptions());
   }

   public void testRebootHardVirtualGuestWhenResponseIs2xx() {

      HttpRequest rebootHardVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/rebootHard.json")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse rebootHardVirtualGuestResponse = HttpResponse.builder().statusCode(200).build();
      SoftLayerApi api = requestSendsResponse(rebootHardVirtualGuest, rebootHardVirtualGuestResponse);
      api.getVirtualGuestApi().rebootHardVirtualGuest(1301396);
   }

   public void testRebootHardVirtualGuestWhenResponseIs4xx() {

      HttpRequest rebootHardVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/rebootHard.json")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse rebootHardVirtualGuestResponse = HttpResponse.builder().statusCode(404).build();
      SoftLayerApi api = requestSendsResponse(rebootHardVirtualGuest, rebootHardVirtualGuestResponse);
      api.getVirtualGuestApi().rebootHardVirtualGuest(1301396);
   }

   public void testPauseVirtualGuestWhenResponseIs2xx() {

      HttpRequest pauseVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/pause.json")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse pauseVirtualGuestResponse = HttpResponse.builder().statusCode(200).build();
      SoftLayerApi api = requestSendsResponse(pauseVirtualGuest, pauseVirtualGuestResponse);
      api.getVirtualGuestApi().pauseVirtualGuest(1301396);
   }

   public void testPauseVirtualGuestWhenResponseIs4xx() {

      HttpRequest pauseVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/pause.json")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse pauseVirtualGuestResponse = HttpResponse.builder().statusCode(404).build();
      SoftLayerApi api = requestSendsResponse(pauseVirtualGuest, pauseVirtualGuestResponse);
      api.getVirtualGuestApi().pauseVirtualGuest(1301396);
   }

   public void testResumeVirtualGuestWhenResponseIs2xx() {

      HttpRequest resumeVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/resume.json")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse resumeVirtualGuestResponse = HttpResponse.builder().statusCode(200).build();
      SoftLayerApi api = requestSendsResponse(resumeVirtualGuest, resumeVirtualGuestResponse);
      api.getVirtualGuestApi().resumeVirtualGuest(1301396);
   }

   public void testResumeVirtualGuestWhenResponseIs4xx() {

      HttpRequest resumeVirtualGuest = HttpRequest.builder().method("GET")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/resume.json")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse resumeVirtualGuestResponse = HttpResponse.builder().statusCode(404).build();
      SoftLayerApi api = requestSendsResponse(resumeVirtualGuest, resumeVirtualGuestResponse);
      api.getVirtualGuestApi().resumeVirtualGuest(1301396);
   }

   public void testSetTagsOnVirtualGuestWhenResponseIs2xx() {

      HttpRequest setTagsOnVirtualGuest = HttpRequest.builder().method("POST")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/setTags")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
              .payload(payloadFromResourceWithContentType("/virtual_guest_set_tags.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse setTagsOnVirtualGuestResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/virtual_guest_set_tags_response.json")).build();

      SoftLayerApi api = requestSendsResponse(setTagsOnVirtualGuest, setTagsOnVirtualGuestResponse);
      VirtualGuest virtualGuest = createVirtualGuest();
      assertTrue(api.getVirtualGuestApi().setTags(virtualGuest.getId(), ImmutableSet.of("test1", "test2", "test3")));
   }

   public void testSetTagsOnVirtualGuestWhenResponseIs4xx() {

      HttpRequest setTagsOnVirtualGuest = HttpRequest.builder().method("POST")
              .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1301396/setTags")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
              .payload(payloadFromResourceWithContentType("/virtual_guest_set_tags.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse setTagsOnVirtualGuestResponse = HttpResponse.builder().statusCode(404).build();
      SoftLayerApi api = requestSendsResponse(setTagsOnVirtualGuest, setTagsOnVirtualGuestResponse);
      VirtualGuest virtualGuest = createVirtualGuest();
      assertFalse(api.getVirtualGuestApi().setTags(virtualGuest.getId(), ImmutableSet.of("test1", "test2", "test3")));
   }
}
