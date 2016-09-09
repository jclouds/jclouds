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

import java.util.List;

import org.jclouds.azurecompute.arm.domain.Offer;
import org.jclouds.azurecompute.arm.domain.Publisher;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.Version;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "OSImageApiLiveTest")
public class OSImageApiLiveTest extends BaseAzureComputeApiLiveTest {

   @Test
   public void testPublisher() {
      List<Publisher> publishers = api().listPublishers();
      assertTrue(publishers.size() > 0);
   }

   @Test
   public void testList() {
      List<Offer> offerList = api().listOffers("MicrosoftWindowsServer");
      assertTrue(offerList.size() > 0);

      List<SKU> skuList = api().listSKUs("MicrosoftWindowsServer", offerList.get(0).name());
      assertTrue(skuList.size() > 0);

      List<Version> versionList = api().listVersions("MicrosoftWindowsServer", offerList.get(0).name(), skuList.get(0).name());

      assertTrue(versionList.size() > 0);
   }

   @Test
   public void testListCanonicalUbuntu() {
      Iterable<Offer> offerList = api().listOffers("canonical");
      int total = 0;

      for (Offer offer : offerList) {
         Iterable<SKU> skuList = api().listSKUs("canonical", offer.name());
         for (SKU sku : skuList) {
            List<Version> versionList = api().listVersions("canonical", offer.name(), sku.name());
            total += versionList.size();
         }
      }
      assertTrue(total > 0);
   }

   private OSImageApi api() {
      return api.getOSImageApi("eastus");
   }
}
