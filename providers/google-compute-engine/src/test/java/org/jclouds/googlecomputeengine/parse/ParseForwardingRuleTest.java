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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseForwardingRuleTest")
public class ParseForwardingRuleTest extends BaseGoogleComputeEngineParseTest<ForwardingRule> {

   @Override
   public String resource() {
      return "/forwardingrule_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public ForwardingRule expected() {
      return expected(BASE_URL);
   }

   public ForwardingRule expected(String baseUrl) {
      return ForwardingRule.create( //
            "6732523704970219884", // id
            URI.create(baseUrl + "/party/regions/europe-west1/forwardingRules/test-forwarding-rule"), // selfLink
            "test-forwarding-rule", // name
            null, // description
            new SimpleDateFormatDateService().iso8601DateParse("2014-01-08T06:51:10.809-08:00"), // creationTimestamp
            URI.create(baseUrl + "/party/regions/europe-west1"), // region
            "23.251.129.77", // ipAddress
            ForwardingRule.IPProtocol.TCP, // ipProtocol
            "1-65535", // portRange
            URI.create(baseUrl + "/party/regions/europe-west1/targetPools/test-target-pool") // target
      );
   }
}
