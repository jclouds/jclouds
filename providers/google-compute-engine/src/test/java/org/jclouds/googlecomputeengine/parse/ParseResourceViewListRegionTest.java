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
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.domain.ResourceView;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseResourceViewListRegionTest extends BaseGoogleComputeEngineParseTest<ListPage<ResourceView>> {

   @Override
   public String resource() {
      return "/resource_view_list_region.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<ResourceView> expected() {
      return ListPage.<ResourceView>builder()
              .kind(Resource.Kind.RESOURCE_VIEW_LIST)
              .id("")
              .selfLink(URI.create(""))
              .items(ImmutableSet.of(
                      new ParseResourceViewRegionTest().expected()
                      , ResourceView.builder()
                      .id("13050421646334304116")
                      .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-11-25T01:38:48.306"))
                      .selfLink(URI.create("https://www.googleapis.com/resourceviews/v1beta1/projects/myproject/regions/"
                                           + "us-central1/resourceViews/jclouds-test-2"))
                      .name("jclouds-test-2")
                      .description("Simple resource view")
                      .addMember(URI.create("https://www.googleapis.com/compute/projects/myproject/zones/"
                                 + "us-central1-a/instances/jclouds-test"))
                      .numMembers(1)
                      .build()
              ))
              .build();
   }
}
