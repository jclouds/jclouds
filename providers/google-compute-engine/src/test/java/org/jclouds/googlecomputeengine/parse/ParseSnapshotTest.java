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
import org.jclouds.googlecomputeengine.domain.Snapshot;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class ParseSnapshotTest extends BaseGoogleComputeEngineParseTest<Snapshot> {

   @Override
   public String resource() {
      return "/snapshot_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Snapshot expected() {
      return Snapshot.builder()
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/snapshots/test-snap"))
              .id("9734455566806191190")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2013-07-26T12:54:23.173-07:00"))
              .status("READY")
              .sizeGb(10)
              .sourceDisk(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/zones/us-central1-a/disks/testimage1"))
              .name("test-snap")
              .description("")
              .sourceDiskId("8243603669926824540")
              .build();
   }
}
