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
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.Project;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

@Test(groups = "unit")
public class ParseProjectTest extends BaseGoogleComputeEngineParseTest<Project> {

   @Override
   public String resource() {
      return "/project.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Project expected() {
      return Project.builder()
              .id("13024414184846275913")
              .creationTimestamp(new Date(Long.parseLong("1351109596252")))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject"))
              .name("myproject")
              .description("")
              .commonInstanceMetadata(Metadata.builder()
                      .items(ImmutableMap.<String, String>builder()
                              .put("propA", "valueA")
                              .put("propB", "valueB")
                              .build())
                      .fingerprint("efgh")
                      .build())
              .addQuota("INSTANCES", 0, 8)
              .addQuota("CPUS", 0, 8)
              .addQuota("EPHEMERAL_ADDRESSES", 0, 8)
              .addQuota("DISKS", 0, 8)
              .addQuota("DISKS_TOTAL_GB", 0, 100)
              .addQuota("SNAPSHOTS", 0, 1000)
              .addQuota("NETWORKS", 1, 5)
              .addQuota("FIREWALLS", 2, 100)
              .addQuota("IMAGES", 0, 100)
              .build();
   }
}
