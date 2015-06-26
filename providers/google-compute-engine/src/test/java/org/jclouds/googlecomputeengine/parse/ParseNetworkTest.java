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

import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseNetworkTest")
public class ParseNetworkTest extends BaseGoogleComputeEngineParseTest<Network> {

   @Override
   public String resource() {
      return "/network_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public Network expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public Network expected(String baseUrl) {
      return Network.create( //
            "13024414170909937976", // id
            parse("2012-10-24T20:13:19.967"), // creationTimestamp
            URI.create(baseUrl + "/party/networks/jclouds-test"), // selfLink
            "jclouds-test", // name
            "A custom network for the project", // description
            "10.0.0.0/8", // rangeIPv4
            "10.0.0.1" // gatewayIPv4
      );
   }
}
