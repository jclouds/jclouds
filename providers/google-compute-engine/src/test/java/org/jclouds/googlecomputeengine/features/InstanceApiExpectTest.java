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

import static java.net.URI.create;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.jclouds.googlecomputeengine.features.ProjectApiExpectTest.GET_PROJECT_REQUEST;
import static org.jclouds.googlecomputeengine.features.ProjectApiExpectTest.GET_PROJECT_RESPONSE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions.DiskMode;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions.DiskType;
import org.jclouds.googlecomputeengine.parse.ParseInstanceListTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceSerialOutputTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

@Test(groups = "unit")
public class InstanceApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public static final HttpRequest GET_INSTANCE_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis" +
                   ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();


   public static final HttpResponse GET_INSTANCE_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/instance_get.json")).build();

   public static final HttpRequest LIST_INSTANCES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis" +
                   ".com/compute/v1/projects/myproject/zones/us-central1-a/instances")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_INSTANCES_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/instance_list.json")).build();

   public static final HttpRequest LIST_CENTRAL1B_INSTANCES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis" +
                   ".com/compute/v1/projects/myproject/zones/us-central1-b/instances")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_CENTRAL1B_INSTANCES_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/instance_list_central1b_empty.json")).build();

   public static final HttpResponse CREATE_INSTANCE_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/zone_operation.json")).build();


   public void testGetInstanceResponseIs2xx() throws Exception {

      InstanceApi api = requestsSendResponses(
              requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE,
              GET_INSTANCE_REQUEST, GET_INSTANCE_RESPONSE).getInstanceApiForProject("myproject");

      assertEquals(api.getInZone("us-central1-a", "test-1"), new ParseInstanceTest().expected());
   }

   public void testGetInstanceResponseIs4xx() throws Exception {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_INSTANCE_REQUEST, operationResponse).getInstanceApiForProject("myproject");

      assertNull(api.getInZone("us-central1-a", "test-1"));
   }

   public void testGetInstanceSerialPortOutput() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/serialPort")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/instance_serial_port.json")).build();


      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getInstanceApiForProject("myproject");

      assertEquals(api.getSerialPortOutputInZone("us-central1-a", "test-1"), new ParseInstanceSerialOutputTest().expected());
   }

   public void testInsertInstanceResponseIs2xxNoOptions() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/instances")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_insert_simple.json", MediaType.APPLICATION_JSON))
              .build();

      InstanceApi api = requestsSendResponses(ImmutableMap.of(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_PROJECT_REQUEST, GET_PROJECT_RESPONSE,
              requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              CREATE_INSTANCE_RESPONSE)).getInstanceApiForProject("myproject");

      InstanceTemplate options = InstanceTemplate.builder().forMachineType("us-central1-a/n1-standard-1")
              .addNetworkInterface(URI.create("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/networks/default"));

      assertEquals(api.createInZone("test-1", "us-central1-a", options), new ParseOperationTest().expected());
   }

   public void testInsertInstanceResponseIs2xxAllOptions() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/instances")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertInstanceResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(ImmutableMap.of(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_PROJECT_REQUEST, GET_PROJECT_RESPONSE,
              requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert, insertInstanceResponse)).getInstanceApiForProject("myproject");

      InstanceTemplate options = InstanceTemplate.builder().forMachineType("us-central1-a/n1-standard-1")
              .addNetworkInterface(URI.create("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/networks/default"), Instance.NetworkInterface.AccessConfig.Type.ONE_TO_ONE_NAT)
              .description("desc")
              .addDisk(InstanceTemplate.PersistentDisk.Mode.READ_WRITE,
                      create("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks/test"),
                      true)
              .addServiceAccount(Instance.ServiceAccount.builder().email("default").addScopes("myscope").build())
              .addMetadata("aKey", "aValue");

      assertEquals(api.createInZone("test-0", "us-central1-a", options),
              new ParseOperationTest().expected());
   }

   public void testDeleteInstanceResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getInstanceApiForProject("myproject");

      assertEquals(api.deleteInZone("us-central1-a", "test-1"),
              new ParseOperationTest().expected());
   }

   public void testDeleteInstanceResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getInstanceApiForProject("myproject");

      assertNull(api.deleteInZone("us-central1-a", "test-1"));
   }

   public void testListInstancesResponseIs2xx() {

      InstanceApi api = requestsSendResponses(
              requestForScopes(COMPUTE_READONLY_SCOPE), TOKEN_RESPONSE,
              LIST_INSTANCES_REQUEST, LIST_INSTANCES_RESPONSE).getInstanceApiForProject("myproject");

      assertEquals(api.listFirstPageInZone("us-central1-a").toString(),
              new ParseInstanceListTest().expected().toString());
   }

   public void testListInstancesResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getInstanceApiForProject("myproject");

      assertTrue(api.listInZone("us-central1-a").concat().isEmpty());
   }

   public void testSetInstanceMetadataResponseIs2xx() {
      HttpRequest setMetadata = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/setMetadata")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_set_metadata.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, setMetadata, setMetadataResponse).getInstanceApiForProject("myproject");

      assertEquals(api.setMetadataInZone("us-central1-a", "test-1", ImmutableMap.of("foo", "bar"), "efgh"),
              new ParseOperationTest().expected());
   }

   public void testSetInstanceMetadataResponseIs4xx() {
      HttpRequest setMetadata = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/setMetadata")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_set_metadata.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, setMetadata, setMetadataResponse).getInstanceApiForProject("myproject");

      assertNull(api.setMetadataInZone("us-central1-a", "test-1", ImmutableMap.of("foo", "bar"), "efgh"));
   }

   public void testResetInstanceResponseIs2xx() {
      HttpRequest reset = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/reset")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse resetResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, reset, resetResponse).getInstanceApiForProject("myproject");

      assertEquals(api.resetInZone("us-central1-a", "test-1"),
              new ParseOperationTest().expected());
   }

   public void testResetInstanceResponseIs4xx() {
      HttpRequest reset = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/reset")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse resetResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, reset, resetResponse).getInstanceApiForProject("myproject");

      assertNull(api.resetInZone("us-central1-a", "test-1"));
   }

   public void testAttachDiskResponseIs2xx() {
      HttpRequest attach = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/attachDisk")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_attach_disk.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse attachResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, attach, attachResponse).getInstanceApiForProject("myproject");

      assertEquals(api.attachDiskInZone("us-central1-a", "test-1",
              new AttachDiskOptions()
                      .mode(DiskMode.READ_ONLY)
                      .source(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks/testimage1"))
                      .type(DiskType.PERSISTENT)),
              new ParseOperationTest().expected());
   }

   public void testAttachDiskResponseIs4xx() {
      HttpRequest attach = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/attachDisk")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/instance_attach_disk.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse attachResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, attach, attachResponse).getInstanceApiForProject("myproject");

      assertNull(api.attachDiskInZone("us-central1-a", "test-1",
              new AttachDiskOptions()
                      .mode(DiskMode.READ_ONLY)
                      .source(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks/testimage1"))
                      .type(DiskType.PERSISTENT)));

   }

   public void testDetachDiskResponseIs2xx() {
      HttpRequest detach = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/detachDisk" +
                      "?deviceName=test-disk-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .build();

      HttpResponse detachResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, detach, detachResponse).getInstanceApiForProject("myproject");

      assertEquals(api.detachDiskInZone("us-central1-a", "test-1", "test-disk-1"),
              new ParseOperationTest().expected());
   }

   public void testDetachDiskResponseIs4xx() {
      HttpRequest detach = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/instances/test-1/detachDisk" +
                      "?deviceName=test-disk-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .build();

      HttpResponse detachResponse = HttpResponse.builder().statusCode(404).build();

      InstanceApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, detach, detachResponse).getInstanceApiForProject("myproject");

      assertNull(api.detachDiskInZone("us-central1-a", "test-1", "test-disk-1"));
   }

}
