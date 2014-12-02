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
import org.jclouds.googlecomputeengine.domain.DiskType;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseDiskTypeListTest")
public class ParseDiskTypeListTest extends BaseGoogleComputeEngineParseTest<ListPage<DiskType>> {

   @Override
   public String resource() {
      return "/disktype_list.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public ListPage<DiskType> expected() {
      String contentBaseUrl = BASE_URL.replace("www", "content");
      DiskType diskType1 = DiskType.create(
            parse("2014-06-02T11:07:28.530-07:00"), // creationTimestamp
            "pd-standard", // name
            "Standard Persistent Disk", // description
            "10GB-10TB", // validDiskSize
            null, // deprecated
            URI.create(contentBaseUrl + "/studied-point-720/zones/us-central1-a"), // zone
            URI.create(contentBaseUrl + "/studied-point-720/zones/us-central1-a/diskTypes/pd-standard"), // selfLink
            500 // defaultDiskSizeGb
      );
      DiskType diskType2 = new ParseDiskTypeTest().expected();
      return ForwardingListPage.create( //
            ImmutableList.of(diskType1, diskType2), // items
            null // nextPageToken
      );
   }
}
