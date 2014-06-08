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
import org.jclouds.googlecomputeengine.domain.Resource.Kind;
import org.jclouds.googlecomputeengine.domain.Route;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseRouteListTest extends BaseGoogleComputeEngineParseTest<ListPage<Route>> {

   @Override
   public String resource() {
      return "/route_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Route> expected() {
      return ListPage.<Route>builder()
              .kind(Kind.ROUTE_LIST)
              .id("projects/myproject/global/routes")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/routes"))
              .items(ImmutableSet.of(new ParseRouteTest().expected(),
                      Route.builder()
                              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/routes/default-route-fc92a41ecb5a8d17"))
                              .id("507025480040058551")
                              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2013-07-08T14:40:38.502-07:00"))
                              .name("default-route-fc92a41ecb5a8d17")
                              .description("Default route to the Internet.")
                              .network(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/default"))
                              .destRange("0.0.0.0/0")
                              .priority(1000)
                              .nextHopGateway(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/gateways/default-internet-gateway"))
                              .build())
              ).build();
   }
}
