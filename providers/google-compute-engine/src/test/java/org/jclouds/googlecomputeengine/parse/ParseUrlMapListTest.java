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
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseUrlMapListTest extends BaseGoogleComputeEngineParseTest<ListPage<UrlMap>> {

   @Override
   public String resource() {
      return "/url_map_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<UrlMap> expected() {
      return ListPage.<UrlMap>builder()
            .kind(Resource.Kind.URL_MAP_LIST)
            .id("projects/myproject/global/urlMaps")
            .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/urlMaps"))
            .items(ImmutableSet.of(new ParseUrlMapTest().expected(),
                                   UrlMap.builder()
                                         .id("13741966667737398120")
                                         .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2014-07-23T12:39:50.022-07:00"))
                                         .selfLink(URI.create("https://www.googleapis" +
                                               ".com/compute/v1/projects/myproject/global/urlMaps/jclouds-test-2"))
                                         .name("jclouds-test-2")
                                         .description("Basic url map")
                                         .defaultService(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/backendServices/jclouds-test"))
                                         .fingerprint("EDqhvJucpz4=")
                                         .build()))
            .build();
   }
}
