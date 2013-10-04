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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ZoneOperationApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   private static final String OPERATIONS_URL_PREFIX = "https://www.googleapis" +
           ".com/compute/v1beta16/projects/myproject/zones/us-central1-a/operations";

   public static final HttpRequest GET_ZONE_OPERATION_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint(OPERATIONS_URL_PREFIX + "/operation-1354084865060-4cf88735faeb8-bbbb12cb")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse GET_ZONE_OPERATION_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/zone_operation.json")).build();

   private Operation expected() {
      SimpleDateFormatDateService dateService = new SimpleDateFormatDateService();
      return Operation.builder().id("13053095055850848306")
              .selfLink(URI.create("https://www.googleapis" +
                      ".com/compute/v1beta16/projects/myproject/zones/us-central1-a/operations/operation-1354084865060-4cf88735faeb8" +
                      "-bbbb12cb"))
              .name("operation-1354084865060-4cf88735faeb8-bbbb12cb")
              .targetLink(URI.create("https://www.googleapis" +
                      ".com/compute/v1beta16/projects/myproject/zones/us-central1-a/instances/instance-api-live-test-instance"))
              .targetId("13053094017547040099")
              .status(Operation.Status.DONE)
              .user("user@developer.gserviceaccount.com")
              .progress(100)
              .insertTime(dateService.iso8601DateParse("2012-11-28T06:41:05.060"))
              .startTime(dateService.iso8601DateParse("2012-11-28T06:41:05.142"))
              .endTime(dateService.iso8601DateParse("2012-11-28T06:41:06.142"))
              .operationType("insert")
              .zone(URI.create("https://www.googleapis.com/compute/v1beta16/projects/myproject/zones/us-central1-a"))
              .build();
   }

   private ListPage<Operation> expectedList() {
      return ListPage.<Operation>builder()
              .kind(Resource.Kind.OPERATION_LIST)
              .id("projects/myproject/zones/us-central1-a/operations")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta16/projects/myproject/zones/us-central1-a/operations"))
              .addItem(expected())
              .build();
   }

   public void testGetOperationResponseIs2xx() throws Exception {

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_ZONE_OPERATION_REQUEST, GET_ZONE_OPERATION_RESPONSE).getZoneOperationApiForProject("myproject");

      assertEquals(zoneOperationApi.getInZone("us-central1-a", "operation-1354084865060-4cf88735faeb8-bbbb12cb"),
              expected());
   }

   public void testGetOperationResponseIs4xx() throws Exception {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_ZONE_OPERATION_REQUEST, operationResponse).getZoneOperationApiForProject("myproject");

      assertNull(zoneOperationApi.getInZone("us-central1-a", "operation-1354084865060-4cf88735faeb8-bbbb12cb"));
   }

   public void testDeleteOperationResponseIs2xx() throws Exception {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(OPERATIONS_URL_PREFIX + "/operation-1352178598164-4cdcc9d031510-4aa46279")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(204).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, operationResponse).getZoneOperationApiForProject("myproject");

      zoneOperationApi.deleteInZone("us-central1-a", "operation-1352178598164-4cdcc9d031510-4aa46279");
   }

   public void testDeleteOperationResponseIs4xx() throws Exception {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(OPERATIONS_URL_PREFIX + "/operation-1352178598164-4cdcc9d031510-4aa46279")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, operationResponse).getZoneOperationApiForProject("myproject");

      zoneOperationApi.deleteInZone("us-central1-a", "operation-1352178598164-4cdcc9d031510-4aa46279");
   }

   public void testListOperationWithNoOptionsResponseIs2xx() {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(OPERATIONS_URL_PREFIX)
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation_list.json")).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getZoneOperationApiForProject("myproject");

      assertEquals(zoneOperationApi.listFirstPageInZone("us-central1-a").toString(),
              expectedList().toString());
   }

   public void testListOperationWithPaginationOptionsResponseIs2xx() {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(OPERATIONS_URL_PREFIX +
                      "?pageToken=CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcG" +
                      "VyYXRpb24tMTM1MjI0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz&" +
                      "filter=" +
                      "status%20eq%20done&" +
                      "maxResults=3")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation_list.json")).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getZoneOperationApiForProject("myproject");

      assertEquals(zoneOperationApi.listAtMarkerInZone("us-central1-a",
              "CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcGVyYXRpb24tMTM1Mj" +
                      "I0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz",
              new ListOptions.Builder().filter("status eq done").maxResults(3)).toString(),
              expectedList().toString());
   }

   public void testListOperationWithPaginationOptionsResponseIs4xx() {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(OPERATIONS_URL_PREFIX)
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getZoneOperationApiForProject("myproject");

      assertTrue(zoneOperationApi.listInZone("us-central1-a").concat().isEmpty());
   }


}
