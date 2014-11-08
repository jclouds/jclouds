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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertNull;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.jclouds.googlecomputeengine.parse.ParseDiskListTest;
import org.jclouds.googlecomputeengine.parse.ParseDiskTest;
import org.jclouds.googlecomputeengine.parse.ParseZoneOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "DiskApiExpectTest")
public class DiskApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {
   public static final String IMAGE_URL = BASE_URL + "/party/zones/us-central1-a/images/foo";
   public static final String SSD_URL = BASE_URL + "/party/zones/us-central1-a/diskTypes/pd-ssd";

   public void testGetDiskResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks/testimage1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/disk_get.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getDiskApi("party", "us-central1-a");

      assertEquals(api.get("testimage1"),
              new ParseDiskTest().expected());
   }

   public void testGetDiskResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks/testimage1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getDiskApi("party", "us-central1-a");

      assertNull(api.get("testimage1"));
   }

   public void testInsertDiskResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/disk_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertDiskResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertDiskResponse).getDiskApi("party", "us-central1-a");

      assertEquals(api.create("testimage1", 1), new ParseZoneOperationTest().expected());
   }

   public void testInsertDiskFromImageResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/disk_insert_sourceImage.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertDiskResponse = HttpResponse.builder().statusCode(200)
                                                    .payload(payloadFromResource("/zone_operation.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
                                          TOKEN_RESPONSE, insert,
                                          insertDiskResponse).getDiskApi("party", "us-central1-a");

      DiskCreationOptions diskCreationOptions = new DiskCreationOptions().sourceImage(URI.create(IMAGE_URL));
      assertEquals(api.create("testimage1", 1, diskCreationOptions), new ParseZoneOperationTest().expected());
   }

   public void testInsertDiskSSDResponseIs2xx(){
      HttpRequest insert = HttpRequest
            .builder()
            .method("POST")
            .endpoint(BASE_URL + "/party/zones/us-central1-a/disks")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResourceWithContentType("/disk_insert_ssd.json", MediaType.APPLICATION_JSON))
            .build();

    HttpResponse insertDiskResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/zone_operation.json")).build();

    DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
                                       TOKEN_RESPONSE, insert,
                                       insertDiskResponse).getDiskApi("party", "us-central1-a");

    DiskCreationOptions diskCreationOptions = new DiskCreationOptions().type(URI.create(SSD_URL));
    assertEquals(api.create("testimage1", 1, diskCreationOptions), new ParseZoneOperationTest().expected());
   }

   public void testCreateSnapshotResponseIs2xx() {
      HttpRequest createSnapshotRequest = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks/testimage1/createSnapshot")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/disk_create_snapshot.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse createSnapshotResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, createSnapshotRequest,
              createSnapshotResponse).getDiskApi("party", "us-central1-a");

      assertEquals(api.createSnapshot("testimage1", "test-snap"),
            new ParseZoneOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateSnapshotResponseIs4xx() {
      HttpRequest createSnapshotRequest = HttpRequest
              .builder()
              .method("POST")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks/testimage1/createSnapshot")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/disk_create_snapshot.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse createSnapshotResponse = HttpResponse.builder().statusCode(404).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, createSnapshotRequest,
              createSnapshotResponse).getDiskApi("party", "us-central1-a");

      api.createSnapshot("testimage1", "test-snap");
   }

   public void testDeleteDiskResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks/testimage1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getDiskApi("party", "us-central1-a");

      assertEquals(api.delete("testimage1"),
              new ParseZoneOperationTest().expected());
   }

   public void testDeleteDiskResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks/testimage1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getDiskApi("party", "us-central1-a");

      assertNull(api.delete("testimage1"));
   }

   public void testListDisksResponseIs2xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/disk_list.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getDiskApi("party", "us-central1-a");

      assertEquals(api.list().next().toString(), new ParseDiskListTest().expected().toString());
   }

   public void testListDisksResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/zones/us-central1-a/disks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getDiskApi("party", "us-central1-a");

      assertFalse(api.list().hasNext());
   }
}
