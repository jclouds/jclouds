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

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.profitbricks.ProfitBricksApi;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.Snapshot;
import org.jclouds.profitbricks.internal.BaseProfitBricksMockTest;
import org.testng.annotations.Test;

import java.util.List;
import static org.jclouds.profitbricks.internal.BaseProfitBricksMockTest.mockWebServer;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Mock tests for the {@link org.jclouds.profitbricks.features.DataCenterApi} class
 */
@Test(groups = "unit", testName = "SnapshotApiMockTest")
public class SnapshotApiMockTest extends BaseProfitBricksMockTest {

   @Test
   public void testGetAllSnapshots() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/snapshot/snapshots.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      SnapshotApi api = pbApi.snapshotApi();

      try {
	 List<Snapshot> snapshots = api.getAllSnapshots();
	 assertRequestHasCommonProperties(server.takeRequest(), "<ws:getAllSnapshots/>");
	 assertNotNull(snapshots);
	 assertEquals(snapshots.size(), 2);
      } finally {
	 pbApi.close();
	 server.shutdown();
      }
   }

   @Test
   public void testGetAllSnapshotsReturning404() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      SnapshotApi api = pbApi.snapshotApi();

      try {
	 List<Snapshot> snapshots = api.getAllSnapshots();
	 assertRequestHasCommonProperties(server.takeRequest());
	 assertTrue(snapshots.isEmpty());
      } finally {
	 pbApi.close();
	 server.shutdown();
      }
   }

   @Test
   public void testGetSnapshot() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/snapshot/snapshot.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      SnapshotApi api = pbApi.snapshotApi();

      String id = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";

      String content = "<ws:getSnapshot><snapshotId>" + id + "</snapshotId></ws:getSnapshot>";

      try {
	 Snapshot snapshot = api.getSnapshot(id);
	 assertRequestHasCommonProperties(server.takeRequest(), content);
	 assertNotNull(snapshot);
	 assertEquals(snapshot.id(), id);
      } finally {
	 pbApi.close();
	 server.shutdown();
      }
   }

   @Test
   public void testGetNonExistingSnapshot() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      SnapshotApi api = pbApi.snapshotApi();

      String id = "random-non-existing-id";
      try {
	 Snapshot snapshot = api.getSnapshot(id);
	 assertRequestHasCommonProperties(server.takeRequest());
	 assertNull(snapshot);
      } finally {
	 pbApi.close();
	 server.shutdown();
      }
   }

   @Test
   public void testCreateSnapshot() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/snapshot/snapshot-create.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      SnapshotApi api = pbApi.snapshotApi();

      String storageId = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

      String content = "<ws:createSnapshot>"
	      + "<request>"
	      + "<storageId>" + storageId + "</storageId>"
	      + "<description>description</description>"
	      + "<snapshotName>snapshot-name</snapshotName>"
	      + "</request>"
	      + "</ws:createSnapshot>";

      try {
	 Snapshot snapshot = api.createSnapshot(
		 Snapshot.Request.creatingBuilder()
		 .storageId(storageId)
		 .description("description")
		 .name("snapshot-name")
		 .build());
	 assertRequestHasCommonProperties(server.takeRequest(), content);
	 assertNotNull(snapshot.id());
	 assertEquals(snapshot.id(), "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

      } finally {
	 pbApi.close();
	 server.shutdown();
      }
   }

   @Test
   public void testUpdateSnapshot() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/snapshot/snapshot-update.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      SnapshotApi api = pbApi.snapshotApi();

      String snapshotId = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

      String content = "<ws:updateSnapshot>"
	      + "<request>"
	      + "<snapshotId>" + snapshotId + "</snapshotId>"
	      + "<description>description</description>"
	      + "<snapshotName>snapshot-name</snapshotName>"
	      + "<bootable>false</bootable>"
	      + "<osType>LINUX</osType>"
	      + "<cpuHotPlug>false</cpuHotPlug>"
	      + "<cpuHotUnPlug>false</cpuHotUnPlug>"
	      + "<ramHotPlug>false</ramHotPlug>"
	      + "<ramHotUnPlug>false</ramHotUnPlug>"
	      + "<nicHotPlug>false</nicHotPlug>"
	      + "<nicHotUnPlug>false</nicHotUnPlug>"
	      + "<discVirtioHotPlug>false</discVirtioHotPlug>"
	      + "<discVirtioHotUnPlug>false</discVirtioHotUnPlug>"
	      + "</request>"
	      + "</ws:updateSnapshot>";

      try {
	 String requestId = api.updateSnapshot(Snapshot.Request.updatingBuilder()
		 .snapshotId(snapshotId)
		 .name("snapshot-name")
		 .description("description")
		 .osType(OsType.LINUX)
		 .build());
	 assertRequestHasCommonProperties(server.takeRequest(), content);
	 assertNotNull(requestId);
      } finally {
	 pbApi.close();
	 server.shutdown();
      }
   }

   @Test
   public void testDeleteSnapshot() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/snapshot/snapshot-delete.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      SnapshotApi api = pbApi.snapshotApi();

      String snapshotId = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";
      String content = "<ws:deleteSnapshot><snapshotId>" + snapshotId + "</snapshotId></ws:deleteSnapshot>";

      try {
	 boolean result = api.deleteSnapshot(snapshotId);
	 assertRequestHasCommonProperties(server.takeRequest(), content);
	 assertTrue(result);
      } finally {
	 pbApi.close();
	 server.shutdown();
      }
   }

   @Test
   public void testDeleteNonExistingSnapshot() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      SnapshotApi api = pbApi.snapshotApi();

      String id = "random-non-existing-id";
      try {
	 boolean result = api.deleteSnapshot(id);
	 assertRequestHasCommonProperties(server.takeRequest());
	 Assert.assertFalse(result);
      } finally {
	 pbApi.close();
	 server.shutdown();
      }
   }

   @Test
   public void testRollbackSnapshot() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/snapshot/snapshot-rollback.xml")));

      ProfitBricksApi pbApi = api(server.getUrl(rootUrl));
      SnapshotApi api = pbApi.snapshotApi();

      String snapshotId = "qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh";
      String storageId = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

      String content = "<ws:rollbackSnapshot><request><snapshotId>" + snapshotId + "</snapshotId><storageId>" + storageId + "</storageId></request></ws:rollbackSnapshot>";
      try {
	 String result = api.rollbackSnapshot(Snapshot.Request.rollbackBuilder()
		 .snapshotId(snapshotId)
		 .storageId(storageId)
		 .build());
	 assertRequestHasCommonProperties(server.takeRequest(), content);
	 assertNotNull(result);
      } finally {
	 pbApi.close();
	 server.shutdown();
      }
   }
}
