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
import org.jclouds.googlecomputeengine.domain.Region;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseRegionListTest extends BaseGoogleComputeEngineParseTest<ListPage<Region>> {

   @Override
   public String resource() {
      return "/region_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Region> expected() {
      return ListPage.<Region>builder()
              .kind(Resource.Kind.REGION_LIST)
              .id("projects/myproject/regions")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions"))
              .items(ImmutableSet.of(
                      new ParseRegionTest().expected(),
                      Region.builder()
                              .id("6396763663251190992")
                              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse
                                      ("2013-07-08T14:40:37.939-07:00"))
                              .selfLink(URI.create("https://www.googleapis" +
                                      ".com/compute/v1/projects/myproject/regions/us-central2"))
                              .name("us-central2")
                              .description("us-central2")
                              .status(Region.Status.UP)
                              .zone(URI.create("https://www.googleapis.com/compute/v1/zones/us-central2-a"))
                              .addQuota("INSTANCES", 0, 8)
                              .addQuota("CPUS", 0, 8)
                              .addQuota("EPHEMERAL_ADDRESSES", 0, 8)
                              .addQuota("DISKS", 0, 8)
                              .addQuota("DISKS_TOTAL_GB", 0, 100)
                              .addQuota("SNAPSHOTS", 0, 1000)
                              .addQuota("NETWORKS", 1, 5)
                              .addQuota("FIREWALLS", 2, 100)
                              .addQuota("IMAGES", 0, 100)
                              .build()))
              .build();
   }
}
