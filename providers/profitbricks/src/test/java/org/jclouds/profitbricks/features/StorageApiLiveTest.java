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

import org.jclouds.profitbricks.BaseProfitBricksLiveTest;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.rest.InsufficientResourcesException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

@Test(groups = "live", testName = "StorageApiLiveTest")
public class StorageApiLiveTest extends BaseProfitBricksLiveTest {

   private DataCenter dataCenter;
   private Server server;

   private String createdStorageId;

   @BeforeClass
   public void setupTest() {
      dataCenter = findOrCreateDataCenter("storageApiLiveTest" + System.currentTimeMillis());
      server = findOrCreateServer(dataCenter);
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
      assertDataCenterAvailable(dataCenter);
      String storageId = api.storageApi().createStorage(
              Storage.Request.creatingBuilder()
              .dataCenterId(dataCenter.id())
              .name("hdd-1")
              .size(2f)
              .build());

      assertNotNull(storageId);
      assertDataCenterAvailable(dataCenter);

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
   public void testUpdateStorage() {
      assertDataCenterAvailable(dataCenter);
      String requestId = api.storageApi().updateStorage(
              Storage.Request.updatingBuilder()
              .id(createdStorageId)
              .name("hdd-2")
              .size(5f)
              .build());

      assertNotNull(requestId);
      assertDataCenterAvailable(dataCenter);

      Storage storage = api.storageApi().getStorage(createdStorageId);
      assertEquals(storage.size(), 5f);
      assertEquals(storage.name(), "hdd-2");
   }

   @Test(dependsOnMethods = "testUpdateStorage")
   public void testConnectStorage() {
      assertDataCenterAvailable(dataCenter);
      String requestId = api.storageApi().connectStorageToServer(
              Storage.Request.connectingBuilder()
              .storageId(createdStorageId)
              .serverId(server.id())
              .build()
      );

      assertNotNull(requestId);
      assertDataCenterAvailable(dataCenter);

      Storage storage = api.storageApi().getStorage(createdStorageId);
      assertTrue(storage.serverIds().contains(server.id()));
   }

   @Test(dependsOnMethods = "testConnectStorage")
   public void testDisconnectStorage() {
      assertDataCenterAvailable(dataCenter);
      String requestId = api.storageApi()
              .disconnectStorageFromServer(createdStorageId, server.id());

      assertNotNull(requestId);
      assertDataCenterAvailable(dataCenter);

      Storage storage = api.storageApi().getStorage(createdStorageId);
      assertFalse(storage.serverIds().contains(server.id()));
   }

   @Test(dependsOnMethods = "testDisconnectStorage")
   public void testDeleteStorage() {
      assertDataCenterAvailable(dataCenter);
      boolean result = api.storageApi().deleteStorage(createdStorageId);
      assertTrue(result, "Created test storage was not deleted");
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      destroyDataCenter(dataCenter);
   }
}
