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
import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class ParseUrlMapListTest extends BaseGoogleComputeEngineParseTest<ListPage<UrlMap>> {

   @Override
   public String resource() {
      return "/url_map_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<UrlMap> expected() {
      return expected(BASE_URL);
   }

   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<UrlMap> expected(String baseUrl) {
      return ForwardingListPage.create(
            ImmutableList.of(new ParseUrlMapTest().expected(baseUrl),
                  UrlMap.create("13741966667737398120", // id
                        new SimpleDateFormatDateService().iso8601DateParse("2014-07-23T12:39:50.022-07:00"), // creationTimestamp
                        URI.create(baseUrl + "/myproject/global/urlMaps/jclouds-test-2"), // selfLink
                        "jclouds-test-2", // name
                        "Basic url map", // description
                        null, // hostRules
                        null, // pathMatchers
                        null, // urlMapTests
                        URI.create(baseUrl + "/myproject/global/backendServices/jclouds-test"), // defaultService
                        "EDqhvJucpz4=")), // fingerprint
            null);
   }
}
