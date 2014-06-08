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

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource.Kind;
import org.jclouds.googlecomputeengine.domain.Snapshot;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseSnapshotListTest extends BaseGoogleComputeEngineParseTest<ListPage<Snapshot>> {

   @Override
   public String resource() {
      return "/snapshot_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Snapshot> expected() {
      return ListPage.<Snapshot>builder()
              .kind(Kind.SNAPSHOT_LIST)
              .id("projects/myproject/global/snapshots")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/snapshots"))
              .items(ImmutableSet.of(
                      new ParseSnapshotTest().expected(), Snapshot.builder()
                      .selfLink(URI.create("https://www.googleapis" +
                              ".com/compute/v1/projects/myproject/global/snapshots/test-snap2"))
                      .id("13895715048576107883")
                      .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse
                              ("2013-07-26T12:57:01.927-07:00"))
                      .status("READY")
                      .sizeGb(10)
                      .sourceDisk(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks/testimage1"))
                      .name("test-snap2")
                      .description("")
                      .sourceDiskId("8243603669926824540")
                      .build()))
              .build();
   }
}
