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

import com.google.common.collect.ImmutableSet;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.ProductPackage;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AccountClientExpectTest")
public class AccountClientExpectTest extends BaseRestClientExpectTest<SoftLayerClient> {

   public static String ACTIVE_PACKAGE_MASK = "id;name";

   public AccountClientExpectTest() {
     provider = "softlayer";
   }

   public void testGetActivePackagesWhenResponseIs2xx() {

      SoftLayerClient client = requestSendsResponse(HttpRequest.builder().method("GET")
                      .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/ActivePackages.json")
                      .addHeader("Accept", "application/json")
                      .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                      .build(),

              HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResource("/get_active_packages.json"))
                      .build());



      assertEquals(client.getAccountClient().getActivePackages(),
              ImmutableSet.of(
                      ProductPackage.builder()
                              .id(13)
                              .name("Dual Xeon (Dual Core) Woodcrest/Cloverton")
                              .description("<div class=\"PageTopicSubHead\">Dual Processor Multi-core Servers</div>")
                              .build(),
                      ProductPackage.builder()
                              .id(15)
                              .name("Single Xeon (Dual Core) Woodcrest/Cloverton")
                              .description("<div class=\"PageTopicSubHead\">Single Processor Multi-core Servers</div>")
                              .build()));
   }

   public void testGetActivePackagesWhenResponseIs404() {
      SoftLayerClient client = requestSendsResponse(HttpRequest.builder()
                        .method("GET")
                        .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/ActivePackages.json")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                        .build(),
                HttpResponse.builder()
                            .statusCode(404)
                            .build());

      assertEquals(client.getAccountClient().getActivePackages(), null);
   }

   public void testGetReducedActivePackagesWhenResponseIs2xx() {
      SoftLayerClient client = requestSendsResponse(
             HttpRequest.builder().method("GET")
                     .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/ActivePackages.json")
                     .addHeader("Accept", "application/json")
                     .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                     .addQueryParam("objectMask", ACTIVE_PACKAGE_MASK)
                     .build(),

             HttpResponse.builder()
                     .statusCode(200)
                     .payload(payloadFromResource("/get_reduced_active_packages.json"))
                     .build());

      assertEquals(client.getAccountClient().getReducedActivePackages(),
             ImmutableSet.of(
                     ProductPackage.builder()
                             .id(13)
                             .name("Dual Xeon (Dual Core) Woodcrest/Cloverton")
                             .description("<div class=\"PageTopicSubHead\">Dual Processor Multi-core Servers</div>")
                             .build(),
                     ProductPackage.builder()
                             .id(15)
                             .name("Single Xeon (Dual Core) Woodcrest/Cloverton")
                             .description("<div class=\"PageTopicSubHead\">Single Processor Multi-core Servers</div>")
                             .build()));
   }

   public void testGetReducedActivePackagesWhenResponseIs404() {
      SoftLayerClient client = requestSendsResponse(
             HttpRequest.builder()
                     .method("GET")
                     .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Account/ActivePackages.json")
                     .addHeader("Accept", "application/json")
                     .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                     .addQueryParam("objectMask", ACTIVE_PACKAGE_MASK)
                     .build(),
             HttpResponse.builder()
                     .statusCode(404)
                     .build());
      assertEquals(client.getAccountClient().getReducedActivePackages(), null);
   }

}
