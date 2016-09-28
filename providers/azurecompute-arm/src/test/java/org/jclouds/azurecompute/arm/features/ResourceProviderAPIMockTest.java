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

import org.jclouds.azurecompute.arm.domain.ResourceProviderMetaData;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "NetworkInterfaceCardApiMockTest", singleThreaded = true)
public class ResourceProviderAPIMockTest extends BaseAzureComputeApiMockTest {

   final String apiVersion = "2015-01-01";
   final String resource = "Microsoft.Compute";

   public void getPublicIPAddressInfo() throws InterruptedException {
      server.enqueue(jsonResponse("/getresourceprovidermetadata.json"));

      final ResourceProviderApi resourceProviderApi = api.getResourceProviderApi();

      List<ResourceProviderMetaData> metaDatas = resourceProviderApi.get(resource);

      String path = String.format("/subscriptions/SUBSCRIPTIONID/providers/%s?api-version=%s", resource, apiVersion);

      assertSent(server, "GET", path);
      assertTrue(metaDatas.size() > 0);
      ResourceProviderMetaData md = metaDatas.get(0);

      assertEquals(md.resourceType(), "availabilitySets");
      assertEquals(md.locations().get(0), "East US");
      assertEquals(md.apiVersions().get(0), "2016-03-30");
   }

   public void getPublicIPAddressInfoEmpty() throws InterruptedException {
      server.enqueue(response404());

      final ResourceProviderApi resourceProviderApi = api.getResourceProviderApi();

      List<ResourceProviderMetaData> metaDatas = resourceProviderApi.get(resource);

      String path = String.format("/subscriptions/SUBSCRIPTIONID/providers/%s?api-version=%s", resource, apiVersion);

      assertSent(server, "GET", path);
      assertNull(metaDatas);
   }
}

