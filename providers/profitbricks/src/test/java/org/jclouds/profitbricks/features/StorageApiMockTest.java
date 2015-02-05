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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "StorageApiMockTest")
public class StorageApiMockTest extends BaseProfitBricksMockTest {

   @Test
   public void testGetAllStorages() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/storage/storages.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      try {
         List<Storage> storages = api.getAllStorages();
         assertRequestHasCommonProperties(server.takeRequest(), "<ws:getAllStorages/>");
         assertNotNull(storages);
         assertTrue(storages.size() == 2);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetAllStoragesReturning404() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      try {
         List<Storage> storages = api.getAllStorages();
         assertRequestHasCommonProperties(server.takeRequest());
         assertTrue(storages.isEmpty());
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetStorage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/storage/storage.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      String id = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";

      String content = "<ws:getStorage><storageId>" + id + "</storageId></ws:getStorage>";
      try {
         Storage storage = api.getStorage(id);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(storage);
         assertEquals(storage.id(), id);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testGetNonExistingStorage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      String id = "random-non-existing-id";
      try {
         Storage storage = api.getStorage(id);
         assertRequestHasCommonProperties(server.takeRequest());
         assertNull(storage);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testConnectStorageToServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/storage/storage-connect.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      String storageId = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";
      String serverId = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";

      String content = "<ws:connectStorageToServer><request>"
              + "<storageId>" + storageId + "</storageId>"
              + "<serverId>" + serverId + "</serverId>"
              + "<busType>VIRTIO</busType>"
              + "<deviceNumber>2</deviceNumber>"
              + "</request></ws:connectStorageToServer>";
      try {
         String requestId = api.connectStorageToServer(
                 Storage.Request.connectingBuilder()
                 .serverId(serverId)
                 .storageId(storageId)
                 .busType(Storage.BusType.VIRTIO)
                 .deviceNumber(2)
                 .build()
         );
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertEquals(requestId, "16463317");
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDisconnectStorageFromServer() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/storage/storage-disconnect.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      String storageId = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";
      String serverId = "qwertyui-qwer-qwer-qwer-qwertyyuiiop";

      String content = "<ws:disconnectStorageFromServer>"
              + "<storageId>" + storageId + "</storageId>"
              + "<serverId>" + serverId + "</serverId>"
              + "</ws:disconnectStorageFromServer>";

      try {
         String requestId = api.disconnectStorageFromServer(storageId, serverId);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertEquals(requestId, "16463318");
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testCreateStorage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/storage/storage-create.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      String dataCenterId = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
      String imageId = "f0a59a5c-7940-11e4-8053-52540066fee9";

      String content = "<ws:createStorage><request>"
              + "<dataCenterId>" + dataCenterId + "</dataCenterId>"
              + "<storageName>hdd-1</storageName>" + "<size>80</size>"
              + "<mountImageId>" + imageId + "</mountImageId>"
              + "<profitBricksImagePassword>qqqqqqqqq</profitBricksImagePassword>"
              + "</request></ws:createStorage>";
      try {
         String storageId = api.createStorage(
                 Storage.Request.creatingBuilder()
                 .dataCenterId(dataCenterId)
                 .name("hdd-1")
                 .size(80f)
                 .mountImageId(imageId)
                 .imagePassword("qqqqqqqqq")
                 .build());
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(storageId);
         assertEquals(storageId, "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh");
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testUpdateStorage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/storage/storage-update.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      String storageId = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";
      String imageId = "f4742db0-9160-11e4-9d74-52540066fee9";

      String content = "<ws:updateStorage><request>"
              + "<storageId>" + storageId + "</storageId>"
              + "<size>20</size><storageName>hdd-2</storageName>"
              + "<mountImageId>" + imageId + "</mountImageId>"
              + "</request></ws:updateStorage>";
      try {
         String requestId = api.updateStorage(
                 Storage.Request.updatingBuilder()
                 .id(storageId)
                 .size(20f)
                 .name("hdd-2")
                 .mountImageId(imageId)
                 .build());

         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertNotNull(requestId);
         assertEquals(requestId, "1234568");
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteStorage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/storage/storage-delete.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      String storageId = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";

      String content = "<ws:deleteStorage><storageId>" + storageId + "</storageId></ws:deleteStorage>";

      try {
         boolean result = api.deleteStorage(storageId);
         assertRequestHasCommonProperties(server.takeRequest(), content);
         assertTrue(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteNonExistingStorage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      StorageApi api = pbApi.storageApi();

      String id = "random-non-existing-id";
      try {
         boolean result = api.deleteStorage(id);
         assertRequestHasCommonProperties(server.takeRequest());
         assertFalse(result);
      } finally {
         pbApi.close();
         server.shutdown();
      }
   }
}
