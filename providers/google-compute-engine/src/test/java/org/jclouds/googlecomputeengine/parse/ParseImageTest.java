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
import static org.jclouds.googlecomputeengine.domain.Image.RawDisk;

import java.net.URI;

import javax.ws.rs.Consumes;

import org.jclouds.googlecomputeengine.domain.Deprecated;
import org.jclouds.googlecomputeengine.domain.Deprecated.State;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Image.Status;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseImageTest")
public class ParseImageTest extends BaseGoogleComputeEngineParseTest<Image> {

   @Override
   public String resource() {
      return "/image_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public Image expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public Image expected(String baseUrl) {
      return Image.create( //
            "12941197498378735318", // id
            URI.create(baseUrl + "/centos-cloud/global/images/centos-6-2-v20120326"), // selfLink
            parse("2012-07-16T22:16:13.468"), // creationTimestamp
            "centos-6-2-v20120326", // name
            "DEPRECATED. CentOS 6.2 image; Created Mon, 26 Mar 2012 21:19:09 +0000", // description
            "RAW", // sourceType
            RawDisk.create(URI.create(""), "TAR", null), // rawDisk
            Deprecated.create( // deprecated
                  State.DEPRECATED, // state
                  URI.create(baseUrl + "/centos-cloud/global/images/centos-6-v20130104"), // replacement
                  null, // deprecated
                  null, // obsolete
                  null // deleted
           ),
           Status.READY, // status
           Long.parseLong("881210631", 10), // archivedSizeBytes
           Long.parseLong("8", 10), // diskSizeGb
           baseUrl + "/party/zones/us-central1-a/disk/disk", // sourceDisk
           "9598530021316715047", // sourceDiskId
           ImmutableList.of(baseUrl + "/suse-cloud/global/licenses/sles-12") // license
            );

   }
}
