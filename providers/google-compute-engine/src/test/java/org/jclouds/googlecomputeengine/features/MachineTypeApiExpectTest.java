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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseMachineTypeListTest;
import org.jclouds.googlecomputeengine.parse.ParseMachineTypeTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class MachineTypeApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public static final HttpRequest LIST_MACHINE_TYPES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis.com/compute/v1beta16/projects/myproject/zones/us-central1-a/machineTypes")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_MACHINE_TYPES_RESPONSE = HttpResponse.builder()
           .statusCode(200)
           .payload(staticPayloadFromResource("/machinetype_list.json"))
           .build();

   public static final HttpRequest LIST_CENTRAL1B_MACHINE_TYPES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis.com/compute/v1beta16/projects/myproject/zones/us-central1-b/machineTypes")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_CENTRAL1B_MACHINE_TYPES_RESPONSE = HttpResponse.builder()
           .statusCode(200)
           .payload(staticPayloadFromResource("/machinetype_list_central1b.json"))
           .build();

   public void testGetMachineTypeResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta16/projects/myproject/zones/us-central1-a/machineTypes/n1-standard-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/machinetype.json")).build();

      MachineTypeApi machineTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getMachineTypeApiForProject("myproject");

      assertEquals(machineTypeApi.getInZone("us-central1-a", "n1-standard-1"),
              new ParseMachineTypeTest().expected());
   }

   public void testGetMachineTypeResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta16/projects/myproject/zones/us-central1-a/machineTypes/n1-standard-1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      MachineTypeApi machineTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getMachineTypeApiForProject("myproject");

      assertNull(machineTypeApi.getInZone("us-central1-a", "n1-standard-1"));
   }

   public void testListMachineTypeNoOptionsResponseIs2xx() throws Exception {

      MachineTypeApi machineTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_MACHINE_TYPES_REQUEST, LIST_MACHINE_TYPES_RESPONSE).getMachineTypeApiForProject
              ("myproject");

      assertEquals(machineTypeApi.listFirstPageInZone("us-central1-a").toString(),
              new ParseMachineTypeListTest().expected().toString());
   }

   public void testLisOperationWithPaginationOptionsResponseIs4xx() {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      MachineTypeApi machineTypeApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_MACHINE_TYPES_REQUEST, operationResponse).getMachineTypeApiForProject("myproject");

      assertTrue(machineTypeApi.listInZone("us-central1-a").concat().isEmpty());
   }
}
