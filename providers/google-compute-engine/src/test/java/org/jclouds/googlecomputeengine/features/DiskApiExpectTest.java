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
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseDiskListTest;
import org.jclouds.googlecomputeengine.parse.ParseDiskTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class DiskApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {
   public static final String IMAGE_URL = "https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/images/foo";

   public void testGetDiskResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks/testimage1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/disk_get.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getDiskApiForProject("myproject");

      assertEquals(api.getInZone("us-central1-a", "testimage1"),
              new ParseDiskTest().expected());
   }

   public void testGetDiskResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks/testimage1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getDiskApiForProject("myproject");

      assertNull(api.getInZone("us-central1-a", "testimage1"));
   }

   public void testInsertDiskResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/disk_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertDiskResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertDiskResponse).getDiskApiForProject("myproject");

      assertEquals(api.createInZone("testimage1", 1, "us-central1-a"), new ParseOperationTest().expected());
   }

   public void testInsertDiskFromImageResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks"
                       + "?sourceImage=" + IMAGE_URL.replaceAll(":", "%3A"))
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/disk_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertDiskResponse = HttpResponse.builder().statusCode(200)
                                                    .payload(payloadFromResource("/zone_operation.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
                                          TOKEN_RESPONSE, insert,
                                          insertDiskResponse).getDiskApiForProject("myproject");

      assertEquals(api.createFromImageWithSizeInZone(IMAGE_URL, "testimage1", 1, "us-central1-a"), new ParseOperationTest().expected());
   }

   public void testCreateSnapshotResponseIs2xx() {
      HttpRequest createSnapshotRequest = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks"
                      + "/testimage1/createSnapshot")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/disk_create_snapshot.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse createSnapshotResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, createSnapshotRequest,
              createSnapshotResponse).getDiskApiForProject("myproject");

      assertEquals(api.createSnapshotInZone("us-central1-a", "testimage1", "test-snap"), new ParseOperationTest().expected());
   }

   public void testCreateSnapshotResponseIs4xx() {
      HttpRequest createSnapshotRequest = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks"
                      + "/testimage1/createSnapshot")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/disk_create_snapshot.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse createSnapshotResponse = HttpResponse.builder().statusCode(404).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, createSnapshotRequest,
              createSnapshotResponse).getDiskApiForProject("myproject");

      assertNull(api.createSnapshotInZone("us-central1-a", "testimage1", "test-snap"));
   }

   public void testDeleteDiskResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/disks/testimage1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getDiskApiForProject("myproject");

      assertEquals(api.deleteInZone("us-central1-a", "testimage1"),
              new ParseOperationTest().expected());
   }

   public void testDeleteDiskResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/disks/testimage1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getDiskApiForProject("myproject");

      assertNull(api.deleteInZone("us-central1-a", "testimage1"));
   }

   public void testListDisksResponseIs2xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/disks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/disk_list.json")).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getDiskApiForProject("myproject");

      assertEquals(api.listFirstPageInZone("us-central1-a").toString(),
              new ParseDiskListTest().expected().toString());
   }

   public void testListDisksResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/zones/us-central1-a/disks")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      DiskApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getDiskApiForProject("myproject");

      assertTrue(api.listInZone("us-central1-a").concat().isEmpty());
   }
}
