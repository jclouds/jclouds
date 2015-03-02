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
package org.jclouds.profitbricks.binder.snapshot;

import org.jclouds.profitbricks.domain.Snapshot;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "RollbackSnapshotRequestBinderTest")
public class RollbackSnapshotRequestBinderTest {

   @Test
   public void testRollbackPayload() {
      RollbackSnapshotRequestBinder binder = new RollbackSnapshotRequestBinder();

      Snapshot.Request.RollbackPayload payload = Snapshot.Request.rollbackBuilder()
	      .snapshotId("snapshot-id")
	      .storageId("storage-id")
	      .build();

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");
      assertEquals(expectedPayload, actual);
   }

   private final String expectedPayload = "<ws:rollbackSnapshot>"
	   + "<request>"
	   + "<snapshotId>snapshot-id</snapshotId>"
	   + "<storageId>storage-id</storageId>"
	   + "</request>"
	   + "</ws:rollbackSnapshot>".replaceAll("\\s", "");
}
