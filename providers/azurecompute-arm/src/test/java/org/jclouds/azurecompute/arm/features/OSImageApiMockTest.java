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
package org.jclouds.azurecompute.arm.features;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.azurecompute.arm.domain.Offer;
import org.jclouds.azurecompute.arm.domain.Publisher;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.Version;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "OSImageApiMockTest", singleThreaded = true)
public class OSImageApiMockTest extends BaseAzureComputeApiMockTest {

   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String apiversion = "?api-version=2015-06-15";
   private final String location = "eastus";
   private final String publisher = "MicrosoftWindowsServer";
   private final String offer = "WindowsServer";
   private final String sku = "2008-R2-SP1";

   final String requestUrl = "/subscriptions/" + subscriptionid + "/providers/Microsoft.Compute/locations/" + location + "/publishers";

   public void testPublishers() throws InterruptedException {
      server.enqueue(jsonResponse("/publishers.json"));

      List<Publisher> publishers = api.getOSImageApi(location).listPublishers();

      assertEquals(size(publishers), 2);

      assertSent(server, "GET", requestUrl + apiversion);
   }
   public void testPublishersEmtpy() throws InterruptedException {
      server.enqueue(response404());

      List<Publisher> publishers = api.getOSImageApi(location).listPublishers();

      assertTrue(isEmpty(publishers));

      assertSent(server, "GET", requestUrl + apiversion);
   }

   public void testOffers() throws InterruptedException {
      server.enqueue(jsonResponse("/offers.json"));

      List<Offer> offers = api.getOSImageApi(location).listOffers(publisher);

      assertEquals(size(offers), 1);

      assertSent(server, "GET", requestUrl + "/" + publisher + "/artifacttypes/vmimage/offers" + apiversion);
   }
   public void testOffersEmtpy() throws InterruptedException {
      server.enqueue(response404());

      List<Offer> offers = api.getOSImageApi(location).listOffers(publisher);

      assertTrue(isEmpty(offers));

      assertSent(server, "GET", requestUrl + "/" + publisher + "/artifacttypes/vmimage/offers" + apiversion);
   }

   public void testSkus() throws InterruptedException {
      server.enqueue(jsonResponse("/skus.json"));

      List<SKU> skus = api.getOSImageApi(location).listSKUs(publisher, offer);

      assertEquals(size(skus), 2);

      assertSent(server, "GET", requestUrl + "/" + publisher + "/artifacttypes/vmimage/offers/" + offer + "/skus" + apiversion);
   }

   public void testSkusEmtpy() throws InterruptedException {
      server.enqueue(response404());

      List<SKU> skus = api.getOSImageApi(location).listSKUs(publisher, offer);

      assertTrue(isEmpty(skus));

      assertSent(server, "GET", requestUrl + "/" + publisher + "/artifacttypes/vmimage/offers/" + offer + "/skus" + apiversion);
   }

   public void testVersions() throws InterruptedException {
      server.enqueue(jsonResponse("/versions.json"));

      List<Version> versions = api.getOSImageApi(location).listVersions(publisher, offer, sku);

      assertEquals(size(versions), 2);

      assertSent(server, "GET", requestUrl + "/" + publisher + "/artifacttypes/vmimage/offers/" + offer + "/skus/" + sku + "/versions" + apiversion);
   }
   public void testVersionsEmtpy() throws InterruptedException {
      server.enqueue(response404());

      List<Version> versions = api.getOSImageApi(location).listVersions(publisher, offer, sku);

      assertTrue(isEmpty(versions));

      assertSent(server, "GET", requestUrl + "/" + publisher + "/artifacttypes/vmimage/offers/" + offer + "/skus/" + sku + "/versions" + apiversion);
   }


}
