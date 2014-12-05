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
package org.jclouds.googlecomputeengine.parse;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;

import javax.ws.rs.Consumes;

import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Snapshot;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseSnapshotListTest")
public class ParseSnapshotListTest extends BaseGoogleComputeEngineParseTest<ListPage<Snapshot>> {

   @Override
   public String resource() {
      return "/snapshot_list.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public ListPage<Snapshot> expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public ListPage<Snapshot> expected(String baseUrl) {
      Snapshot snapshot1 = new ParseSnapshotTest().expected(baseUrl);
      Snapshot snapshot2 = Snapshot.create( //
            "13895715048576107883", // id
            URI.create(baseUrl + "/party/global/snapshots/test-snap2"), // selfLink
            parse("2013-07-26T12:57:01.927-07:00"), // creationTimestamp
            "test-snap2", // name
            "", // description
            10, // sizeGb
            "READY", // status
            URI.create(baseUrl + "/party/zones/us-central1-a/disks/testimage1"), // sourceDisk
            "8243603669926824540", // sourceDiskId
            null, // storageBytes
            null, // storageByteStatus
            null // licenses
      );
      return ForwardingListPage.create( //
            ImmutableList.of(snapshot1, snapshot2), // items
            null // nextPageToken
      );
   }

}
