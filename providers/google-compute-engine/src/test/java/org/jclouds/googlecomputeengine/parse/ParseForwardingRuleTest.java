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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

@Test(groups = "unit")
public class ParseForwardingRuleTest extends BaseGoogleComputeEngineParseTest<ForwardingRule> {

   @Override
   public String resource() {
      return "/forwardingrule_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ForwardingRule expected() {
      return ForwardingRule.builder()
              .id("6732523704970219884")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2014-01-08T06:51:10.809-08:00"))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/europe-west1/forwardingRules/test-forwarding-rule"))
              .name("test-forwarding-rule")
              .region(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/europe-west1"))
              .target(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/europe-west1/targetPools/test-target-pool"))
              .ipAddress("23.251.129.77")
              .ipProtocol("TCP")
              .portRange("1-65535")
              .build();
   }
}
