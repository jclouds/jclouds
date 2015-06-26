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
package org.jclouds.googlecomputeengine.internal;

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.config.GoogleComputeEngineParserModule;
import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.features.AddressApi;
import org.jclouds.googlecomputeengine.features.ImageApi;
import org.jclouds.googlecomputeengine.features.InstanceApi;
import org.jclouds.googlecomputeengine.parse.ParseAddressTest;
import org.jclouds.googlecomputeengine.parse.ParseImageTest;
import org.jclouds.googlecomputeengine.parse.ParseInstanceTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;

@Test(groups = "unit", testName = "ToIteratorOfListPageTest")
public class ToIteratorOfListPageExpectTest extends BaseGoogleComputeEngineExpectTest<GoogleComputeEngineApi> {

   private final Json json = Guice.createInjector(new GsonModule(), new GoogleComputeEngineParserModule())
         .getInstance(Json.class);

   public void globalScope() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/global/images")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/image_list_single_page.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).images();

      Iterator<ListPage<Image>> images = imageApi.list();

      assertEquals(images.next().size(), 3);
   }

   public void multiplePagesProjectScoped() {
      HttpRequest list1 = HttpRequest
              .builder()
              .method("GET")
              .endpoint(BASE_URL + "/party/global/images?maxResults=1")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest list2 = list1.toBuilder()
               .endpoint(BASE_URL + "/party/global/images?pageToken=token1&maxResults=1").build();

      HttpRequest list3 = list1.toBuilder()
               .endpoint(BASE_URL + "/party/global/images?pageToken=token2&maxResults=1").build();

      List<Image> items = ImmutableList.of(new ParseImageTest().expected());

      HttpResponse list1Response = HttpResponse.builder().statusCode(200)
              .payload(json.toJson(ForwardingListPage.create(items, "token1"))).build();

      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
            .payload(json.toJson(ForwardingListPage.create(items, "token2"))).build();

      HttpResponse list3Response = HttpResponse.builder().statusCode(200)
            .payload(json.toJson(ForwardingListPage.create(items, null))).build();

      ImageApi imageApi = orderedRequestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list1, list1Response, list2, list2Response, list3, list3Response).images();

      Iterator<ListPage<Image>> images = imageApi.list(maxResults(1));

      int imageCounter = 0;
      while (images.hasNext()) {
         imageCounter += images.next().size();
      }
      assertEquals(imageCounter, 3);
   }

   public void multiplePagesRegionScoped() {
      HttpRequest list1 = HttpRequest
            .builder()
            .method("GET")
            .endpoint(BASE_URL + "/party/regions/us-central1/addresses?maxResults=1")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest list2 = list1.toBuilder()
            .endpoint(BASE_URL + "/party/regions/us-central1/addresses?pageToken=token1&maxResults=1").build();

      HttpRequest list3 = list1.toBuilder()
            .endpoint(BASE_URL + "/party/regions/us-central1/addresses?pageToken=token2&maxResults=1").build();

      List<Address> items = ImmutableList.of(new ParseAddressTest().expected());

      HttpResponse list1Response = HttpResponse.builder().statusCode(200)
            .payload(json.toJson(ForwardingListPage.create(items, "token1"))).build();

      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
            .payload(json.toJson(ForwardingListPage.create(items, "token2"))).build();

      HttpResponse list3Response = HttpResponse.builder().statusCode(200)
            .payload(json.toJson(ForwardingListPage.create(items, null))).build();

      AddressApi addressApi = orderedRequestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, list1, list1Response, list2, list2Response, list3, list3Response)
            .addressesInRegion("us-central1");

      Iterator<ListPage<Address>> addresses = addressApi.list(maxResults(1));

      int addressCounter = 0;
      while (addresses.hasNext()) {
         addressCounter += addresses.next().size();
      }
      assertEquals(addressCounter, 3);
   }

   public void multiplePagesZoneScoped() {
      HttpRequest list1 = HttpRequest
            .builder()
            .method("GET")
            .endpoint(BASE_URL + "/party/zones/us-central1-a/instances?maxResults=1")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest list2 = list1.toBuilder()
            .endpoint(BASE_URL + "/party/zones/us-central1-a/instances?pageToken=token1&maxResults=1").build();

      HttpRequest list3 = list1.toBuilder()
            .endpoint(BASE_URL + "/party/zones/us-central1-a/instances?pageToken=token2&maxResults=1").build();

      List<Instance> items = ImmutableList.of(new ParseInstanceTest().expected());

      HttpResponse list1Response = HttpResponse.builder().statusCode(200)
            .payload(json.toJson(ForwardingListPage.create(items, "token1"))).build();

      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
            .payload(json.toJson(ForwardingListPage.create(items, "token2"))).build();

      HttpResponse list3Response = HttpResponse.builder().statusCode(200)
            .payload(json.toJson(ForwardingListPage.create(items, null))).build();

      InstanceApi instanceApi = orderedRequestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, list1, list1Response, list2, list2Response, list3, list3Response)
            .instancesInZone("us-central1-a");

      Iterator<ListPage<Instance>> instances = instanceApi.list(maxResults(1));

      int instanceCounter = 0;
      while (instances.hasNext()) {
         instanceCounter += instances.next().size();
      }
      assertEquals(instanceCounter, 3);
   }
}
