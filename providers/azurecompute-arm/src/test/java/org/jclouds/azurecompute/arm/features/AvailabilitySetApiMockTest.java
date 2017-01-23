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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.azurecompute.arm.domain.AvailabilitySet;
import org.jclouds.azurecompute.arm.domain.AvailabilitySet.AvailabilitySetProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "AvailabilitySetApiMockTest", singleThreaded = true)
public class AvailabilitySetApiMockTest extends BaseAzureComputeApiMockTest {

   private final String subscriptionid = "SUBSCRIPTIONID";
   private final String resourcegroup = "myresourcegroup";
   private final String asName = "myas";
   private final String apiVersion = "api-version=2016-03-30";

   public void createAvailabilitySet() throws InterruptedException {

      server.enqueue(jsonResponse("/availabilitysetcreate.json").setResponseCode(200));

      AvailabilitySetApi asApi = api.getAvailabilitySetApi(resourcegroup);

      AvailabilitySetProperties props = AvailabilitySetProperties.builder().platformUpdateDomainCount(2)
            .platformFaultDomainCount(3).build();
      AvailabilitySet as = asApi.createOrUpdate(asName, "westeurope", null, props);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/availabilitySets/%s?%s", subscriptionid,
            resourcegroup, asName, apiVersion);
      String json = "{\"location\":\"westeurope\",\"properties\":{\"platformUpdateDomainCount\":2,\"platformFaultDomainCount\":3}}";
      assertSent(server, "PUT", path, json);

      assertEquals(as.name(), asName);
      assertEquals(as.properties().platformUpdateDomainCount(), 2);
      assertEquals(as.properties().platformFaultDomainCount(), 3);
   }

   public void getAvailabilitySet() throws InterruptedException {

      server.enqueue(jsonResponse("/availabilitysetget.json").setResponseCode(200));

      AvailabilitySetApi asApi = api.getAvailabilitySetApi(resourcegroup);

      AvailabilitySet as = asApi.get(asName);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/availabilitySets/%s?%s", subscriptionid,
            resourcegroup, asName, apiVersion);
      assertSent(server, "GET", path);

      assertEquals(as.name(), asName);
   }

   public void getAvailabilitySet404() throws InterruptedException {
      server.enqueue(response404());

      AvailabilitySetApi asApi = api.getAvailabilitySetApi(resourcegroup);

      AvailabilitySet as = asApi.get(asName);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/availabilitySets/%s?%s", subscriptionid,
            resourcegroup, asName, apiVersion);
      assertSent(server, "GET", path);

      assertNull(as);
   }

   public void listAvailabilitySets() throws InterruptedException {

      server.enqueue(jsonResponse("/availabilitysetlist.json").setResponseCode(200));

      AvailabilitySetApi asApi = api.getAvailabilitySetApi(resourcegroup);

      List<AvailabilitySet> asList = asApi.list();

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/availabilitySets?%s", subscriptionid,
            resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(asList.size() > 0);
   }

   public void listAvailabilitySets404() throws InterruptedException {
      server.enqueue(response404());

      AvailabilitySetApi asApi = api.getAvailabilitySetApi(resourcegroup);

      List<AvailabilitySet> asList = asApi.list();

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/availabilitySets?%s", subscriptionid,
            resourcegroup, apiVersion);
      assertSent(server, "GET", path);

      assertTrue(isEmpty(asList));
   }

   public void deleteAvailabilitySet() throws InterruptedException {

      server.enqueue(response202WithHeader());

      AvailabilitySetApi asApi = api.getAvailabilitySetApi(resourcegroup);

      URI uri = asApi.delete(asName);
      assertNotNull(uri);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/availabilitySets/%s?%s", subscriptionid,
            resourcegroup, asName, apiVersion);
      assertSent(server, "DELETE", path);
   }

   public void deleteSubnetResourceDoesNotExist() throws InterruptedException {

      server.enqueue(response204());

      AvailabilitySetApi asApi = api.getAvailabilitySetApi(resourcegroup);

      URI uri = asApi.delete(asName);
      assertNull(uri);

      String path = String.format(
            "/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Compute/availabilitySets/%s?%s", subscriptionid,
            resourcegroup, asName, apiVersion);
      assertSent(server, "DELETE", path);
   }
}
