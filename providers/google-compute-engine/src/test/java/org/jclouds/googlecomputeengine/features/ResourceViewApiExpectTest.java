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

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.NDEV_CLOUD_MAN_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.NDEV_CLOUD_MAN_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.options.ResourceViewOptions;
import org.jclouds.googlecomputeengine.parse.ParseResourceViewListRegionTest;
import org.jclouds.googlecomputeengine.parse.ParseResourceViewListZoneTest;
import org.jclouds.googlecomputeengine.parse.ParseResourceViewRegionTest;
import org.jclouds.googlecomputeengine.parse.ParseResourceViewResourceListTest;
import org.jclouds.googlecomputeengine.parse.ParseResourceViewZoneTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class ResourceViewApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {
   
   private static final String ZONE_ENDPOINT_BASE = "https://www.googleapis.com"
            + "/resourceviews/v1beta1/projects/myproject/zones/us-central1-a/"
            + "resourceViews";
   private static final String REGION_ENDPOINT_BASE = "https://www.googleapis.com"
            + "/resourceviews/v1beta1/projects/myproject/regions/us-central1/"
            + "resourceViews";
   
   private org.jclouds.http.HttpRequest.Builder<? extends HttpRequest.Builder<?>> getBasicRequest() {
      return HttpRequest.builder().addHeader("Accept", "application/json")
                                  .addHeader("Authorization", "Bearer " + TOKEN);
   }
   
   private HttpResponse createResponse(String payloadFile) {
      return HttpResponse.builder().statusCode(200)
                                   .payload(payloadFromResource(payloadFile))
                                   .build();
   }

   public void testResourceViewGetInZoneResponseIs2xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(ZONE_ENDPOINT_BASE + "/jclouds-test")
                                             .build();

      HttpResponse response = createResponse("/resource_view_get_zone.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertEquals(api.getInZone("us-central1-a", "jclouds-test"),
              new ParseResourceViewZoneTest().expected());
   }

   public void testResourceViewGetInZoneResponseIs4xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ZONE_ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertNull(api.getInZone("us-central1-a", "jclouds-test"));
   }

   public void testResourceViewInsertInZoneResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("POST")
                                             .endpoint(ZONE_ENDPOINT_BASE)
                                             .payload(payloadFromResourceWithContentType("/resource_view_insert.json",
                                                                                         MediaType.APPLICATION_JSON))
                                             .build();

      HttpResponse response = createResponse("/resource_view_get_zone.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      ResourceViewOptions options = new ResourceViewOptions().description("Simple resource view");
      assertEquals(api.createInZone("us-central1-a", "jclouds-test", options),
                   new ParseResourceViewZoneTest().expected());
   }

   public void testResourceviewListResourcesInZoneResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("POST")
                                             .endpoint(ZONE_ENDPOINT_BASE + "/jclouds-test/resources")
                                             .build();

      HttpResponse response = createResponse("/resource_view_resources_list.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertEquals(api.listResourcesFirstPageInZone("us-central1-a",
                                                    "jclouds-test").toString(),
                   new ParseResourceViewResourceListTest().expected().toString());
   }

   public void testResourceviewListResourcesInZoneResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("POST")
                                             .endpoint(ZONE_ENDPOINT_BASE + "/jclouds-test/resources")
                                             .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertTrue(api.listResourcesInZone("us-central1-a", "jclouds-test").concat().isEmpty());
   }

   // TODO: (ashmrtnz) uncomment this when / if the delete operation actually returns something
   /*
   public void testResourceViewDeleteInZoneResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
                                             .endpoint(ZONE_ENDPOINT_BASE + "/jclouds-test")
                                             .build();

      HttpResponse response = createResponse("/zone_operation.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertEquals(api.deleteInZone("us-central1-a", "jclouds-test"),
              new ParseOperationTest().expected());
   }

   public void testResourceViewDeleteInZoneResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
                                             .endpoint(ZONE_ENDPOINT_BASE + "/jclouds-test")
                                             .build();
      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertNull(api.deleteInZone("us-central1-a", "jclouds-test"));
   }
   */

   public void testResourceViewListInZoneResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(ZONE_ENDPOINT_BASE)
                                             .build();

      HttpResponse response = createResponse("/resource_view_list_zone.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertEquals(api.listFirstPageInZone("us-central1-a").toString(),
              new ParseResourceViewListZoneTest().expected().toString());
   }

   public void testResourceViewListInZoneResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(ZONE_ENDPOINT_BASE)
                                             .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertTrue(api.listInZone("us-central1-a").concat().isEmpty());
   }
   
   // TODO: (ashmrtnz) create expect tests for addResources and removeResources
   // when / if they actually return something
   
   public void testResourceViewGetInRegionResponseIs2xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(REGION_ENDPOINT_BASE + "/jclouds-test")
                                             .build();

      HttpResponse response = createResponse("/resource_view_get_region.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertEquals(api.getInRegion("us-central1", "jclouds-test"),
              new ParseResourceViewRegionTest().expected());
   }

   public void testResourceViewGetInRegionResponseIs4xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(REGION_ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertNull(api.getInRegion("us-central1", "jclouds-test"));
   }

   public void testResourceViewInsertInRegionResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("POST")
                                             .endpoint(REGION_ENDPOINT_BASE)
                                             .payload(payloadFromResourceWithContentType("/resource_view_insert.json",
                                                                                         MediaType.APPLICATION_JSON))
                                             .build();

      HttpResponse response = createResponse("/resource_view_get_region.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      ResourceViewOptions options = new ResourceViewOptions().description("Simple resource view");
      assertEquals(api.createInRegion("us-central1", "jclouds-test", options),
                   new ParseResourceViewRegionTest().expected());
   }

   public void testResourceviewListResourcesInRegionResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("POST")
                                             .endpoint(REGION_ENDPOINT_BASE + "/jclouds-test/resources")
                                             .build();

      HttpResponse response = createResponse("/resource_view_resources_list.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertEquals(api.listResourcesFirstPageInRegion("us-central1",
                                                    "jclouds-test").toString(),
                   new ParseResourceViewResourceListTest().expected().toString());
   }

   public void testResourceviewListResourcesInRegionResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("POST")
                                             .endpoint(REGION_ENDPOINT_BASE + "/jclouds-test/resources")
                                             .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertTrue(api.listResourcesInRegion("us-central1", "jclouds-test").concat().isEmpty());
   }

   // TODO: (ashmrtnz) uncomment this when / if the delete operation actually returns something
   /*
   public void testResourceViewDeleteInRegionResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
                                             .endpoint(REGION_ENDPOINT_BASE + "/jclouds-test")
                                             .build();

      HttpResponse response = createResponse("/region_operation.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertEquals(api.deleteInRegion("us-central1", "jclouds-test"),
              new ParseOperationTest().expected());
   }

   public void testResourceViewDeleteInRegionResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
                                             .endpoint(REGION_ENDPOINT_BASE + "/jclouds-test")
                                             .build();
      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertNull(api.deleteInRegion("us-central1", "jclouds-test"));
   }
   */

   public void testResourceViewListInRegionResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(REGION_ENDPOINT_BASE)
                                             .build();

      HttpResponse response = createResponse("/resource_view_list_region.json");

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertEquals(api.listFirstPageInRegion("us-central1").toString(),
              new ParseResourceViewListRegionTest().expected().toString());
   }

   public void testResourceViewListInRegionResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(REGION_ENDPOINT_BASE)
                                             .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ResourceViewApi api = requestsSendResponses(requestForScopes(NDEV_CLOUD_MAN_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getResourceViewApiForProject("myproject");

      assertTrue(api.listInRegion("us-central1").concat().isEmpty());
   }
   
   // TODO: (ashmrtnz) create expect tests for addResources and removeResources
   // when /if they actually return something
}
