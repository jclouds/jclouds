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
package org.jclouds.profitbricks.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusAware;
import org.jclouds.profitbricks.compute.internal.ProvisioningStatusPollingPredicate;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.util.Predicates2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "StorageApiLiveTest", singleThreaded = true)
public class StorageApiLiveTest extends BaseProfitBricksLiveTest {

   private Predicate<String> waitUntilAvailable;
   private DataCenter dataCenter;
   private Server server;
   private String createdStorageId;

   @Override
   protected void initialize() {
      super.initialize();
      List<DataCenter> dataCenters = api.dataCenterApi().getAllDataCenters();
      assertFalse(dataCenters.isEmpty(), "Must atleast have 1 datacenter available for storage testing.");

      dataCenter = Iterables.getFirst(dataCenters, null);
      if (dataCenter != null)
         dataCenter = api.dataCenterApi().getDataCenter(dataCenter.id()); // fetch individual to load more properties

      this.waitUntilAvailable = Predicates2.retry(
              new ProvisioningStatusPollingPredicate(api, ProvisioningStatusAware.STORAGE, ProvisioningState.AVAILABLE),
              6l * 60l, 2l, TimeUnit.SECONDS);
   }

   @Test(expectedExceptions = InsufficientResourcesException.class)
   public void testUberStorage() {
      api.storageApi().createStorage(
              Storage.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("Uber Storage")
              .size(9999999f)
              .build());
   }

   @Test
   public void testCreateStorage() {
      String storageId = api.storageApi().createStorage(
              Storage.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("hdd-1")
              .size(2f)
              .build());

      assertNotNull(storageId);
      createdStorageId = storageId;
   }

   @Test(dependsOnMethods = "testCreateStorage")
   public void testGetStorage() {
      Storage storage = api.storageApi().getStorage(createdStorageId);

      assertNotNull(storage);
      assertEquals(storage.id(), createdStorageId);
   }

   @Test(dependsOnMethods = "testCreateStorage")
   public void testGetAllStorages() {
      List<Storage> storages = api.storageApi().getAllStorages();

      assertNotNull(storages);
      assertFalse(storages.isEmpty());
   }

   @Test(dependsOnMethods = "testCreateStorage")
   public void testWaitUntilAvailable() {
      boolean available = waitUntilAvailable.apply(createdStorageId);

      assertTrue(available);
   }

   @Test(dependsOnMethods = "testWaitUntilAvailable")
   public void testUpdateStorage() {
      String requestId = api.storageApi().updateStorage(
              Storage.Request.updatingBuilder()
              .id(createdStorageId)
              .name("hdd-2")
              .size(5f)
              .build());

      assertNotNull(requestId);
      waitUntilAvailable.apply(createdStorageId);

      Storage storage = api.storageApi().getStorage(createdStorageId);
      assertEquals(storage.size(), 5f);
      assertEquals(storage.name(), "hdd-2");
   }

   @Test(dependsOnMethods = "testUpdateStorage")
   public void testConnectStorage() {
      server = Iterables.getFirst(dataCenter.servers(), null);
      assertNotNull(server, "No server to attach to.");

      String requestId = api.storageApi().connectStorageToServer(
              Storage.Request.connectingBuilder()
              .storageId(createdStorageId)
              .serverId(server.id())
              .build()
      );

      assertNotNull(requestId);
      waitUntilAvailable.apply(createdStorageId);

      Storage storage = api.storageApi().getStorage(createdStorageId);
      assertTrue(storage.serverIds().contains(server.id()));
   }

   @Test(dependsOnMethods = "testConnectStorage")
   public void testDisconnectStorage() {
      String requestId = api.storageApi()
              .disconnectStorageFromServer(createdStorageId, server.id());

      assertNotNull(requestId);
      waitUntilAvailable.apply(createdStorageId);

      Storage storage = api.storageApi().getStorage(createdStorageId);
      assertFalse(storage.serverIds().contains(server.id()));
   }

   @AfterClass(alwaysRun = true)
   public void testDeleteStorage() {
      if (createdStorageId != null) {
         boolean result = api.storageApi().deleteStorage(createdStorageId);

         assertTrue(result, "Created test storage was not delete.");
      }
   }
}
