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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.Availability;
import org.jclouds.azurecompute.arm.domain.StorageServiceKeys;
import org.jclouds.azurecompute.arm.domain.StorageServiceUpdateParams;
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.isEmpty;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;


@Test(groups = "unit", testName = "StorageAccountApiMockTest", singleThreaded = true)
public class StorageAccountApiMockTest extends BaseAzureComputeApiMockTest {

   private String subsriptionId = "SUBSCRIPTIONID";
   private String resourceGroup = "resourceGroup";

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/storageAccounts.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      List<StorageService> list = storageAPI.list();
      assertEquals(list, expected());

      assertSent(server, "GET", "/subscriptions/" + subsriptionId +
              "/resourcegroups/resourceGroup/providers/Microsoft.Storage/storageAccounts?api-version=2015-06-15");
   }

   public void testListReturns404() throws InterruptedException {
      server.enqueue(response404());

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      List<StorageService> list = storageAPI.list();

      assertTrue(isEmpty(list));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/subscriptions/" + subsriptionId +
              "/resourcegroups/resourceGroup/providers/Microsoft.Storage/storageAccounts?api-version=2015-06-15");
   }

   public void testCreate() throws Exception {
      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      server.enqueue(response202WithHeader());

      URI uri = storageAPI.create("name-of-storage-account", "westus",
              ImmutableMap.of("property_name", "property_value"),
              ImmutableMap.of("accountType", StorageService.AccountType.Premium_LRS.toString()));
      assertNotNull(uri);

      assertSent(server, "PUT", "/subscriptions/" + subsriptionId +
              "/resourcegroups/resourceGroup/providers/Microsoft.Storage/" +
              "storageAccounts/name-of-storage-account?api-version=2015-06-15", String.format("{\"location\":\"westus\",\"tags\":{\"property_name\":\"property_value\"},\"properties\":{\"accountType\":\"Premium_LRS\"}}"));
   }

   public void testCreateWithNullTag() throws Exception {
      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      server.enqueue(response202WithHeader());

      URI uri = storageAPI.create("name-of-storage-account", "westus",
              null,
              ImmutableMap.of("accountType", StorageService.AccountType.Premium_LRS.toString()));
      assertNotNull(uri);

      assertSent(server, "PUT", "/subscriptions/" + subsriptionId +
              "/resourcegroups/resourceGroup/providers/Microsoft.Storage/" +
              "storageAccounts/name-of-storage-account?api-version=2015-06-15", String.format("{\"location\":\"westus\",\"properties\":{\"accountType\":\"Premium_LRS\"}}"));
   }

   public void testIsAvailable() throws Exception {
      server.enqueue(jsonResponse("/isavailablestorageservice.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      assertEquals(storageAPI.isAvailable("TESTSTORAGE"),
              Availability.create("true"));

      assertSent(server, "POST", "/subscriptions/" + subsriptionId +
              "/providers/Microsoft.Storage/checkNameAvailability?api-version=2015-06-15", String.format("{\"name\":\"TESTSTORAGE\",\"type\":\"Microsoft.Storage/storageAccounts\"}"));
   }

   public void testGet() throws Exception {
      server.enqueue(jsonResponse("/storageservices.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      assertEquals(storageAPI.get("TESTSTORAGE"), expected().get(0));

      assertSent(server, "GET", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE?api-version=2015-06-15");
   }

   public void testNullGet() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      assertNull(storageAPI.get("TESTSTORAGE"));

      assertSent(server, "GET", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE?api-version=2015-06-15");
   }

   public void testGetKeys() throws Exception {
      server.enqueue(jsonResponse("/storageaccountkeys.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      assertEquals(storageAPI.getKeys("TESTSTORAGE"), StorageServiceKeys.create(
              "bndO7lydwDkMo4Y0mFvmfLyi2f9aZY7bwfAVWoJWv4mOVK6E9c/exLnFsSm/NMWgifLCfxC/c6QBTbdEvWUA7w==",
              "/jMLLT3kKqY4K+cUtJTbh7pCBdvG9EMKJxUvaJJAf6W6aUiZe1A1ulXHcibrqRVA2RJE0oUeXQGXLYJ2l85L7A=="));

      assertSent(server, "POST", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE/listKeys?api-version=2015-06-15");
   }

   public void testNullGetKeys() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      assertNull(storageAPI.getKeys("TESTSTORAGE"));

      assertSent(server, "POST", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE/listKeys?api-version=2015-06-15");
   }

   public void testRegenerateKeys() throws Exception {
      server.enqueue(jsonResponse("/storageaccountkeys.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      assertEquals(storageAPI.regenerateKeys("TESTSTORAGE", "key1"), StorageServiceKeys.create(
              "bndO7lydwDkMo4Y0mFvmfLyi2f9aZY7bwfAVWoJWv4mOVK6E9c/exLnFsSm/NMWgifLCfxC/c6QBTbdEvWUA7w==",
              "/jMLLT3kKqY4K+cUtJTbh7pCBdvG9EMKJxUvaJJAf6W6aUiZe1A1ulXHcibrqRVA2RJE0oUeXQGXLYJ2l85L7A=="));

      assertSent(server, "POST", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE/regenerateKey?api-version=2015-06-15", String.format("{\"keyName\":\"key1\"}"));
   }

   public void testUpdate() throws Exception {
      server.enqueue(jsonResponse("/storageaccountupdate.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      StorageServiceUpdateParams.StorageServiceUpdateProperties props =
      StorageServiceUpdateParams.StorageServiceUpdateProperties.create(StorageService.AccountType.Standard_LRS);

      final StorageServiceUpdateParams params = storageAPI.update("TESTSTORAGE",
              ImmutableMap.of("another_property_name", "another_property_value"), props);

      assertTrue(params.tags().containsKey("another_property_name"));

      assertSent(server, "PATCH", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
         "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE?api-version=2015-06-15", String.format("{\"properties\":{ \"accountType\": \"Standard_LRS\" },\"tags\":{\"another_property_name\":\"another_property_value\"}}"));
   }

   public void testUpdateWithNullTagAndNullProperty() throws Exception {
      server.enqueue(jsonResponse("/storageaccountupdate.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      StorageServiceUpdateParams.StorageServiceUpdateProperties props =
              StorageServiceUpdateParams.StorageServiceUpdateProperties.create(null);

      final StorageServiceUpdateParams params = storageAPI.update("TESTSTORAGE", null, props);

      assertTrue(params.tags().containsKey("another_property_name"));

      assertSent(server, "PATCH", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE?api-version=2015-06-15", String.format("{\"properties\":{}}"));
   }

   public void testDelete() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      boolean status = storageAPI.delete("TESTSTORAGE");
      assertTrue(status);

      assertSent(server, "DELETE", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE?api-version=2015-06-15");
   }

   public void testDelete204() throws Exception {

      server.enqueue(new MockResponse().setResponseCode(204));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(resourceGroup);

      boolean status = storageAPI.delete("TESTSTORAGE");
      assertFalse(status);

      assertSent(server, "DELETE", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE?api-version=2015-06-15");
   }

   private StorageService getStrorageAccount() {
      DateService DATE_SERVICE = new SimpleDateFormatDateService();
      Map<String, String> endpoints = new HashMap<String, String>();
      endpoints.put("blob", "https://TESTSTORAGE.blob.core.windows.net/");
      endpoints.put("file", "https://TESTSTORAGE.file.core.windows.net/");
      endpoints.put("queue", "https://TESTSTORAGE.queue.core.windows.net/");
      endpoints.put("table", "https://TESTSTORAGE.table.core.windows.net/");
      Map<String, String> secondaryEndpoints = new HashMap<String, String>();
      secondaryEndpoints.put("blob", "https://TESTSTORAGE-secondary.blob.core.windows.net/");
      secondaryEndpoints.put("queue", "https://TESTSTORAGE-secondary.queue.core.windows.net/");
      secondaryEndpoints.put("table", "https://TESTSTORAGE-secondary.table.core.windows.net/");


      String location = "westus";
      String secondaryLocation = "eastus";
      final StorageService.StorageServiceProperties props = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T13:04:45.0890883Z"),
              endpoints,
              location,
              StorageService.Status.Succeeded,
              secondaryEndpoints, secondaryLocation,
              StorageService.RegionStatus.Available,
              StorageService.RegionStatus.Available);

      final Map<String, String> tags = ImmutableMap.of(
              "key1", "value1",
              "key2", "value2");

      return StorageService.create(
              "/subscriptions/SUBSCRIPTIONID/resourceGroups/resourceGroup" +
                      "/providers/Microsoft.Storage/storageAccounts/TESTSTORAGE",
              "TESTSTORAGE", location, tags, null, props);
   }

   private List<StorageService> expected() throws MalformedURLException {
      DateService DATE_SERVICE = new SimpleDateFormatDateService();
      Map<String, String> endpoints = new HashMap<String, String>();
      endpoints.put("blob", "https://TESTSTORAGE.blob.core.windows.net/");
      endpoints.put("file", "https://TESTSTORAGE.file.core.windows.net/");
      endpoints.put("queue", "https://TESTSTORAGE.queue.core.windows.net/");
      endpoints.put("table", "https://TESTSTORAGE.table.core.windows.net/");
      Map<String, String> secondaryEndpoints = new HashMap<String, String>();
      secondaryEndpoints.put("blob", "https://TESTSTORAGE-secondary.blob.core.windows.net/");
      secondaryEndpoints.put("queue", "https://TESTSTORAGE-secondary.queue.core.windows.net/");
      secondaryEndpoints.put("table", "https://TESTSTORAGE-secondary.table.core.windows.net/");
      Map<String, String> endpoints2 = new HashMap<String, String>();
      endpoints2.put("blob", "https://TESTSTORAGE2.blob.core.windows.net/");
      endpoints2.put("file", "https://TESTSTORAGE2.file.core.windows.net/");
      endpoints2.put("queue", "https://TESTSTORAGE2.queue.core.windows.net/");
      endpoints2.put("table", "https://TESTSTORAGE2.table.core.windows.net/");
      Map<String, String> secondaryEndpoints2 = new HashMap<String, String>();
      secondaryEndpoints2.put("blob", "https://TESTSTORAGE2-secondary.blob.core.windows.net/");
      secondaryEndpoints2.put("queue", "https://TESTSTORAGE2-secondary.queue.core.windows.net/");
      secondaryEndpoints2.put("table", "https://TESTSTORAGE2-secondary.table.core.windows.net/");
      Map<String, String> endpoints3 = new HashMap<String, String>();
      endpoints3.put("blob", "https://TESTSTORAGE3.blob.core.windows.net/");
      endpoints3.put("file", "https://TESTSTORAGE3.file.core.windows.net/");
      endpoints3.put("queue", "https://TESTSTORAGE3.queue.core.windows.net/");
      endpoints3.put("table", "https://TESTSTORAGE3.table.core.windows.net/");
      Map<String, String> secondaryEndpoints3 = new HashMap<String, String>();
      secondaryEndpoints3.put("blob", "https://TESTSTORAGE3-secondary.blob.core.windows.net/");
      secondaryEndpoints3.put("queue", "https://TESTSTORAGE3-secondary.queue.core.windows.net/");
      secondaryEndpoints3.put("table", "https://TESTSTORAGE3-secondary.table.core.windows.net/");


      String location = "westus";
      String secondaryLocation = "eastus";
      final StorageService.StorageServiceProperties props = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T13:04:45.0890883Z"),
              endpoints,
              location,
              StorageService.Status.Succeeded, secondaryEndpoints, secondaryLocation,
              StorageService.RegionStatus.Available,
              StorageService.RegionStatus.Available);
      final StorageService.StorageServiceProperties props2 = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T13:11:43.8265672Z"),
              endpoints2, location,
              StorageService.Status.Succeeded, secondaryEndpoints2, secondaryLocation,
              StorageService.RegionStatus.Available,
              StorageService.RegionStatus.Available);
      final StorageService.StorageServiceProperties props3 = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T14:12:59.5223315Z"),
              endpoints3, location,
              StorageService.Status.Succeeded, secondaryEndpoints3, secondaryLocation,
              StorageService.RegionStatus.Available,
              StorageService.RegionStatus.Available);

      final Map<String, String> tags = ImmutableMap.of(
              "key1", "value1",
              "key2", "value2");

      return ImmutableList.of(StorageService.create(
              "/subscriptions/SUBSCRIPTIONID/resourceGroups/resourceGroup/" +
                      "providers/Microsoft.Storage/storageAccounts/TESTSTORAGE",
              "TESTSTORAGE", location, tags, "Microsoft.Storage/storageAccounts", props),
              StorageService.create(
                      "/subscriptions/SUBSCRIPTIONID/resourceGroups/resourceGroup/" +
                              "providers/Microsoft.Storage/storageAccounts/TESTSTORAGE2",
                      "TESTSTORAGE2", location, tags, "Microsoft.Storage/storageAccounts", props2),
              StorageService.create(
                      "/subscriptions/SUBSCRIPTIONID/resourceGroups/resourceGroup/" +
                              "providers/Microsoft.Storage/storageAccounts/TESTSTORAGE3",
                      "TESTSTORAGE3", location, tags, "Microsoft.Storage/storageAccounts", props3));
   }

}
