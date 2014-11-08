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

import org.jclouds.googlecomputeengine.domain.ListPage;
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
      Snapshot snapshot1 = new ParseSnapshotTest().expected();
      Snapshot snapshot2 = Snapshot.create( //
            "13895715048576107883", // id
            URI.create(BASE_URL + "/party/global/snapshots/test-snap2"), // selfLink
            "test-snap2", // name
            "", // description
            10, // sizeGb
            "READY", // status
            URI.create(BASE_URL + "/party/zones/us-central1-a/disks/testimage1"), // sourceDisk
            "8243603669926824540"// sourceDiskId
      );
      return ListPage.create( //
            ImmutableList.of(snapshot1, snapshot2), // items
            null // nextPageToken
      );
   }

}
