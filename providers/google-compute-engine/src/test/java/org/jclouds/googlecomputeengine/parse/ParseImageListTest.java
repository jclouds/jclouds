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
import org.jclouds.googlecomputeengine.domain.Deprecated;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseImageListTest extends BaseGoogleComputeEngineParseTest<ListPage<Image>> {

   @Override
   public String resource() {
      return "/image_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Image> expected() {
      return ListPage.<Image>builder()
              .kind(Resource.Kind.IMAGE_LIST)
              .id("projects/centos-cloud/global/images")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/centos-cloud/global/images"))
              .items(ImmutableSet.of(Image.builder()
                      .id("12941197498378735318")
                      .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-07-16T22:16:13.468"))
                      .selfLink(URI.create("https://www.googleapis" +
                              ".com/compute/v1/projects/centos-cloud/global/images/centos-6-2-v20120326"))
                      .name("centos-6-2-v20120326")
                      .description("DEPRECATED. CentOS 6.2 image; Created Mon, 26 Mar 2012 21:19:09 +0000")
                      .sourceType("RAW")
                      .deprecated(Deprecated.builder()
                              .state("DEPRECATED")
                              .replacement(URI.create("https://www.googleapis.com/compute/v1/projects/centos-cloud/global/images/centos-6-v20130104"))
                              .build())
                      .rawDisk(
                              Image.RawDisk.builder()
                                      .source("")
                                      .containerType("TAR")
                                      .build()
                      ).build()

              ))
              .build();
   }
}
