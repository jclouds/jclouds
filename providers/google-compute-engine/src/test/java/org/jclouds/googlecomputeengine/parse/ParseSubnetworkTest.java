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

import org.jclouds.googlecomputeengine.domain.Subnetwork;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseSubnetworkTest")
public class ParseSubnetworkTest extends BaseGoogleComputeEngineParseTest<Subnetwork> {

   @Override
   public String resource() {
      return "/network_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public Subnetwork expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public Subnetwork expected(String baseUrl) {
      return Subnetwork.create( //
            "5850679262666457680", // id
            parse("2016-06-07T14:29:35.476-07:00"), // creationTimestamp
            URI.create(baseUrl + "/party/regions/someregion/subnetworks/jclouds-test"), // selfLink
            "jclouds-subnetwork-test", // name
            "A custom subnetwork for the project", // description
            "10.128.0.1",
            URI.create(baseUrl + "/party/global/networks/mynetwork"), // network
            "10.128.0.0/20", // rangeIPv4
            URI.create(baseUrl + "/party/regions/someregion") // region
      );
   }
}
