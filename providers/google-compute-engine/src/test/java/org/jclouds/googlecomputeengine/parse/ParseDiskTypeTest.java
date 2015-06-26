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

import org.jclouds.googlecomputeengine.domain.DiskType;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseDiskTypeTest")
public class ParseDiskTypeTest extends BaseGoogleComputeEngineParseTest<DiskType> {

   @Override
   public String resource() {
      return "/disktype.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public DiskType expected() {
      String contentBaseUrl = BASE_URL.replace("www", "content");
      return DiskType.create(
            parse("2014-06-02T11:07:28.529-07:00"), // creationTimestamp
            "pd-ssd", // name
            "SSD Persistent Disk", // description
            "10GB-1TB", // validDiskSize
            null, // deprecated
            URI.create(contentBaseUrl + "/studied-point-720/zones/us-central1-a"), // zone
            URI.create(contentBaseUrl + "/studied-point-720/zones/us-central1-a/diskTypes/pd-ssd"), // selfLink
            100 // defaultDiskSizeGb
      );
   }
}
