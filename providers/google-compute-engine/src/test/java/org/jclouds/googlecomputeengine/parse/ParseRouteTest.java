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
import org.jclouds.googlecomputeengine.domain.Route;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseRouteTest extends BaseGoogleComputeEngineParseTest<Route> {

   @Override
   public String resource() {
      return "/route_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Route expected() {
      return Route.builder()
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/routes/default-route-c99ebfbed0e1f375"))
              .id("7241926205630356071")
              .name("default-route-c99ebfbed0e1f375")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2013-07-08T14:40:38.502-07:00"))
              .description("Default route to the virtual network.")
              .network(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/default"))
              .destRange("10.240.0.0/16")
              .priority(1000)
              .nextHopNetwork(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/networks/default"))
              .tags(ImmutableSet.of("fooTag", "barTag"))
              .build();

   }
}
