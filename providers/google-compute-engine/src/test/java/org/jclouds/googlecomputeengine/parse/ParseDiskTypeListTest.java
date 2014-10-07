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

import static org.jclouds.googlecomputeengine.domain.Resource.Kind.DISK_TYPE_LIST;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.DiskType;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;

public class ParseDiskTypeListTest extends BaseGoogleComputeEngineParseTest<ListPage<DiskType>> {

   @Override
   public String resource() {
      return "/disktype_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<DiskType> expected() {
      SimpleDateFormatDateService dateService = new SimpleDateFormatDateService();
      return ListPage.<DiskType>builder()
              .kind(DISK_TYPE_LIST)
              .addItem(DiskType.builder()
                      .creationTimestamp(dateService.iso8601DateParse("2014-06-02T11:07:28.530-07:00"))
                      .name("pd-standard")
                      .description("Standard Persistent Disk")
                      .validDiskSize("10GB-10TB")
                      .zone("https://content.googleapis.com/compute/v1/projects/studied-point-720/zones/us-central1-a")
                      .selfLink(URI.create("https://content.googleapis.com/compute/v1/projects/studied-point-720/zones/us-central1-a/diskTypes/pd-standard"))
                      .defaultDiskSizeGb(500)
                      .build())
              .addItem(DiskType.builder()
                      .creationTimestamp(dateService.iso8601DateParse("2014-06-02T11:07:28.529-07:00"))
                      .name("pd-ssd")
                      .description("SSD Persistent Disk")
                      .validDiskSize("10GB-1TB")
                      .zone("https://content.googleapis.com/compute/v1/projects/studied-point-720/zones/us-central1-a")
                      .selfLink(URI.create("https://content.googleapis.com/compute/v1/projects/studied-point-720/zones/us-central1-a/diskTypes/pd-ssd"))
                      .defaultDiskSizeGb(100)
                      .build())
              .build();
   }
}
