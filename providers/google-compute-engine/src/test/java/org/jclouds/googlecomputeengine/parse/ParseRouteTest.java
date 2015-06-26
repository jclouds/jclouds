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

import java.net.URI;

import javax.ws.rs.Consumes;

import org.jclouds.googlecomputeengine.domain.KeyValuePair;
import org.jclouds.googlecomputeengine.domain.Route;
import org.jclouds.googlecomputeengine.domain.Warning;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseRouteTest")
public class ParseRouteTest extends BaseGoogleComputeEngineParseTest<Route> {

   @Override
   public String resource() {
      return "/route_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public Route expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public Route expected(String baseUrl) {
      return Route.create( //
            "7241926205630356071", // id
            parse("2013-07-08T14:40:38.502-07:00"), // creationTimestamp
            URI.create(baseUrl + "/party/global/routes/default-route-c99ebfbed0e1f375"), // selfLink
            "default-route-c99ebfbed0e1f375", // name
            "Default route to the virtual network.", // description
            URI.create(baseUrl + "/party/global/networks/default"), // network
            ImmutableList.of("fooTag", "barTag"), // tags
            "10.240.0.0/16", // destRange
            1000, // priority
            null, // nextHopInstance
            null, // nextHopIp
            URI.create(baseUrl + "/party/global/networks/default"), // nextHopNetwork
            null, // nextHopGateway
            ImmutableList.of(Warning.create("NO_RESULTS_ON_PAGE", "This is an example warning", ImmutableList.of(
                  KeyValuePair.create("scope", "There are no results for scope 'zones/asia-east1-b' on this page.")))), // warnings
            null // nextHopVpnTunnel
      );
   }
}
