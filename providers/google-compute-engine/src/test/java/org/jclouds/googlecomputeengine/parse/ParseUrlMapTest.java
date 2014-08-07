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
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathRule;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseUrlMapTest extends BaseGoogleComputeEngineParseTest<UrlMap> {

   @Override
   public String resource() {
      return "/url_map_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public UrlMap expected() {
      URI service = URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/backendServices/jclouds-test");
      return UrlMap.builder()
              .id("13741966667737398119")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2014-07-23T12:39:50.022-07:00"))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/urlMaps/jclouds-test"))
              .name("jclouds-test")
              .description("Sample url map")
              .hostRules(ImmutableSet.<HostRule>of(HostRule.builder().hosts(ImmutableSet.<String>of("jclouds-test")).pathMatcher("path").build()))
              .pathMatchers(ImmutableSet.<PathMatcher>of(PathMatcher.builder().name("path")
                                                                    .defaultService(service)
                                                                    .pathRules(ImmutableSet.<PathRule>of(PathRule.builder().service(service)
                                                                                                                           .addPath("/")
                                                                                                                           .build()))
                                                                    .build()))
              .urlMapTests(ImmutableSet.<UrlMap.UrlMapTest>of(UrlMap.UrlMapTest.builder().host("jclouds-test")
                                                                       .path("/test/path")
                                                                       .service(service)
                                                                       .build()))
              .defaultService(service)
              .fingerprint("EDmhvJucpz4=")                          
              .build();
   }
}
