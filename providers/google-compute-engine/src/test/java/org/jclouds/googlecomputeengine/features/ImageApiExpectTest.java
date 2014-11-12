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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertNull;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseImageListTest;
import org.jclouds.googlecomputeengine.parse.ParseImageTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ImageApiExpectTest")
public class ImageApiExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {
   HttpRequest get = HttpRequest
         .builder()
         .method("GET")
         .endpoint(BASE_URL + "/party/global/images/centos-6-2-v20120326")
         .addHeader("Accept", "application/json")
         .addHeader("Authorization", "Bearer " + TOKEN).build();

   public void get() throws Exception {
      HttpResponse response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/image_get.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, response).images();

      assertEquals(imageApi.get(get.getEndpoint()), new ParseImageTest().expected());
   }

   public void getResponseIs4xx() throws Exception {
      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, response).images();

      assertNull(imageApi.get(get.getEndpoint()));
   }

   public void getByName() throws Exception {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/image_get.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, get, response).images();

      assertEquals(imageApi.get("centos-6-2-v20120326"), new ParseImageTest().expected());
   }

   public void getByNameResponseIs4xx() throws Exception {
      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, get, response).images();

      assertNull(imageApi.get("centos-6-2-v20120326"));
   }

   public void testDeleteImageResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/global/images/centos-6-2-v20120326")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).images();

      assertEquals(imageApi.delete("centos-6-2-v20120326"), new ParseOperationTest().expected());
   }

   public void testDeleteImageResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(BASE_URL + "/party/global/images/centos-6-2-v20120326")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).images();

      assertNull(imageApi.delete("centos-6-2-v20120326"));
   }

   public static final HttpRequest LIST_PROJECT_IMAGES_REQUEST = HttpRequest
         .builder()
         .method("GET")
         .endpoint(BASE_URL + "/party/global/images")
         .addHeader("Accept", "application/json")
         .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_PROJECT_IMAGES_RESPONSE = HttpResponse.builder().statusCode(200)
         .payload(staticPayloadFromResource("/image_list.json")).build();

   public void list() {

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_PROJECT_IMAGES_REQUEST, LIST_PROJECT_IMAGES_RESPONSE).images();

      assertEquals(imageApi.list().next(), new ParseImageListTest().expected());
   }

   public void listEmpty() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_PROJECT_IMAGES_REQUEST, response).images();

      assertFalse(imageApi.list().hasNext());
   }

   public static final HttpRequest LIST_CENTOS_IMAGES_REQUEST = HttpRequest
         .builder()
         .method("GET")
         .endpoint(BASE_URL + "/centos-cloud/global/images")
         .addHeader("Accept", "application/json")
         .addHeader("Authorization", "Bearer " + TOKEN).build();

   public void listInProject() {

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, LIST_CENTOS_IMAGES_REQUEST, LIST_PROJECT_IMAGES_RESPONSE).images();

      assertEquals(imageApi.listInProject("centos-cloud").next(), new ParseImageListTest().expected());
   }

   public void listInProjectEmpty() {
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/list_empty.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, LIST_CENTOS_IMAGES_REQUEST, response).images();

      assertFalse(imageApi.listInProject("centos-cloud").hasNext());
   }

   public void testCreateImageFromPdResponseIs2xx(){
      HttpRequest createImage = HttpRequest
            .builder()
            .method("POST")
            .endpoint(BASE_URL + "/party/global/images")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResource("/image_insert_from_pd.json"))
            .build();

      HttpResponse createImageResponse = HttpResponse.builder().statusCode(200)
                                  .payload(payloadFromResource("/operation.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, createImage, createImageResponse).images();

      assertEquals(imageApi.createFromDisk("my-image", BASE_URL + "/party/zones/us-central1-a/disks/mydisk"),
            new ParseOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateImageFromPdResponseIs4xx() {
      HttpRequest createImage = HttpRequest
            .builder()
            .method("POST")
            .endpoint(BASE_URL + "/party/global/images")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResource("/image_insert_from_pd.json"))
            .build();

      HttpResponse createImageResponse = HttpResponse.builder().statusCode(404).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, createImage, createImageResponse).images();

      imageApi.createFromDisk("my-image", BASE_URL + "/party/zones/us-central1-a/disks/mydisk");
   }
}
