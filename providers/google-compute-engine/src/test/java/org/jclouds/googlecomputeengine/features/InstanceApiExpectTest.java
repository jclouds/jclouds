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
package org.jclouds.googlecomputeengine.features;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.jclouds.googlecomputeengine.features.ProjectApiExpectTest.GET_PROJECT_REQUEST;
import static org.jclouds.googlecomputeengine.features.ProjectApiExpectTest.GET_PROJECT_RESPONSE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.NewInstance.Disk;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions.DiskMode;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions.DiskType;
import org.jclouds.googlecomputeengine.parse.ParseInstanceListTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceSerialOutputTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceTest;
import org.jclouds.googlecomputeengine.parse.ParseZoneOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "unit", testName = "InstanceApiExpectTest")
public class InstanceApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   public static final HttpRequest GET_INSTANCE_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse GET_INSTANCE_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/instance_get.json")).build();

   public static final HttpRequest LIST_INSTANCES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint(BASE_URL + "/party/zones/us-central1-a/instances")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_INSTANCES_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/instance_list.json")).build();

   public static final HttpResponse CREATE_INSTANCE_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/zone_operation.json")).build();

   public void testGetInstanceResponseIs2xx() throws Exception {

      InstanceApi api = requestsSendResponses(
              requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE,
              GET_INSTANCE_REQUEST, GET_INSTANCE_RESPONSE).getInstanceApi("party", "us-central1-a");

      assertEquals(api.get("test-1"), new ParseInstanceTest().expected());
   }

   public void testGetInstanceResponseIs4xx() throws Exception {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_INSTANCE_REQUEST, operationResponse).getInstanceApi("party", "us-central1-a");

      assertNull(api.get("test-1"));
   }

   public void testGetInstanceSerialPortOutput() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/serialPort")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/instance_serial_port.json")).build();


      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getInstanceApi("party", "us-central1-a");

      assertEquals(api.getSerialPortOutput("test-1"), new ParseInstanceSerialOutputTest().expected());
   }

   public void testInsertInstanceResponseIs2xxNoOptions() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_insert_simple.json", MediaType.APPLICATION_JSON))
              .build();

      InstanceApi api = requestsSendResponses(ImmutableMap.of(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_PROJECT_REQUEST, GET_PROJECT_RESPONSE,
              requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              CREATE_INSTANCE_RESPONSE)).getInstanceApi("party", "us-central1-a");

      NewInstance newInstance = NewInstance.create(
            URI.create(BASE_URL + "/party/zones/us-central1-a/machineTypes/n1-standard-1"), // machineType
            "test-1", // name
            URI.create(BASE_URL + "/party/global/networks/default"), // network
            Disk.existingBootDisk(URI.create(BASE_URL + "/party/zones/us-central1-a/disks/test")), // bootDisk
            null // description
      );

      assertEquals(api.create(newInstance), new ParseZoneOperationTest().expected());
   }

   public void testInsertInstanceResponseIs2xxAllOptions() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertInstanceResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(ImmutableMap.of(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_PROJECT_REQUEST, GET_PROJECT_RESPONSE,
              requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert, insertInstanceResponse)).getInstanceApi("party", "us-central1-a");

      NewInstance newInstance = NewInstance.create(
            URI.create(BASE_URL + "/party/zones/us-central1-a/machineTypes/n1-standard-1"), // machineType
            "test-1", // name
            URI.create(BASE_URL + "/party/global/networks/default"), // network
            Disk.existingBootDisk(URI.create(BASE_URL + "/party/zones/us-central1-a/disks/test")), // bootDisk
            "desc" // description
      );
      newInstance.metadata().put("aKey", "aValue");
      assertEquals(api.create(newInstance), new ParseZoneOperationTest().expected());
   }

   public void testDeleteInstanceResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getInstanceApi("party", "us-central1-a");

      assertEquals(api.delete("test-1"), new ParseZoneOperationTest().expected());
   }

   public void testDeleteInstanceResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getInstanceApi("party", "us-central1-a");

      assertNull(api.delete("test-1"));
   }

   public void testListInstancesResponseIs2xx() {

      InstanceApi api = requestsSendResponses(
              requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE,
              LIST_INSTANCES_REQUEST, LIST_INSTANCES_RESPONSE).getInstanceApi("party", "us-central1-a");

      assertEquals(api.list().next().toString(), new ParseInstanceListTest().expected().toString());
   }

   public void testListInstancesResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getInstanceApi("party", "us-central1-a");

      assertFalse(api.list().hasNext());
   }

   public void testSetInstanceMetadataResponseIs2xx() {
      HttpRequest setMetadata = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/setMetadata")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_set_metadata.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, setMetadata, setMetadataResponse).getInstanceApi("party", "us-central1-a");

      assertEquals(api.setMetadata("test-1", Metadata.create("efgh").put("foo", "bar")),
              new ParseZoneOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testSetInstanceMetadataResponseIs4xx() {
      HttpRequest setMetadata = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/setMetadata")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_set_metadata.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, setMetadata, setMetadataResponse).getInstanceApi("party", "us-central1-a");

      api.setMetadata("test-1", Metadata.create("efgh").put("foo", "bar"));
   }

   public void testSetInstanceTagsResponseIs2xx() {
      HttpRequest setTags = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/setTags")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_set_tags.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse setTagsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, setTags, setTagsResponse).getInstanceApi("party", "us-central1-a");

      assertEquals(api.setTags("test-1", ImmutableList.of("foo", "bar"), "efgh"),
              new ParseZoneOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testSetInstanceTagsResponseIs4xx() {
      HttpRequest setTags = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/setTags")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_set_tags.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse setTagsResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, setTags, setTagsResponse).getInstanceApi("party", "us-central1-a");

      api.setTags("test-1", ImmutableList.of("foo", "bar"), "efgh");
   }

   public void testResetInstanceResponseIs2xx() {
      HttpRequest reset = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/reset")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse resetResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, reset, resetResponse).getInstanceApi("party", "us-central1-a");

      assertEquals(api.reset("test-1"),
              new ParseZoneOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testResetInstanceResponseIs4xx() {
      HttpRequest reset = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/reset")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse resetResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, reset, resetResponse).getInstanceApi("party", "us-central1-a");

      api.reset("test-1");
   }

   public void testAttachDiskResponseIs2xx() {
      HttpRequest attach = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/attachDisk")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_attach_disk.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse attachResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, attach, attachResponse).getInstanceApi("party", "us-central1-a");

      assertEquals(api.attachDisk("test-1",
              new AttachDiskOptions()
                      .mode(DiskMode.READ_ONLY)
                      .source(URI.create(BASE_URL + "/party/zones/us-central1-a/disks/testimage1"))
                      .type(DiskType.PERSISTENT)),
              new ParseZoneOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAttachDiskResponseIs4xx() {
      HttpRequest attach = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/attachDisk")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_attach_disk.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse attachResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, attach, attachResponse).getInstanceApi("party", "us-central1-a");

      api.attachDisk("test-1",
              new AttachDiskOptions()
                      .mode(DiskMode.READ_ONLY)
                      .source(URI.create(BASE_URL + "/party/zones/us-central1-a/disks/testimage1"))
                      .type(DiskType.PERSISTENT));

   }

   public void testDetachDiskResponseIs2xx() {
      HttpRequest detach = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/detachDisk?deviceName=test-disk-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .build();

      HttpResponse detachResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, detach, detachResponse).getInstanceApi("party", "us-central1-a");

      assertEquals(api.detachDisk("test-1", "test-disk-1"),
              new ParseZoneOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDetachDiskResponseIs4xx() {
      HttpRequest detach = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/instances/test-1/detachDisk?deviceName=test-disk-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .build();

      HttpResponse detachResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, detach, detachResponse).getInstanceApi("party", "us-central1-a");

      api.detachDisk("test-1", "test-disk-1");
   }

}
