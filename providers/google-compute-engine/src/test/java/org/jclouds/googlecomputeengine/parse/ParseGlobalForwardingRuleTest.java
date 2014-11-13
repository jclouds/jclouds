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
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.ForwardingRule.IPProtocol;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;

public class ParseGlobalForwardingRuleTest extends BaseGoogleComputeEngineParseTest<ForwardingRule> {

   @Override
   public String resource() {
      return "/global_forwarding_rule_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ForwardingRule expected() {
      return ForwardingRule.create("8192211304399313984", // id
         URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/forwardingRules/jclouds-test"), // selfLink
         "jclouds-test", // name
         "tcp forwarding rule", // description
         new SimpleDateFormatDateService().iso8601DateParse("2014-07-18T09:47:30.826-07:00"), // creationTimestamp
         null, // region
         "107.178.255.156", //IPAddress
         IPProtocol.TCP, //IPProtocol
         "80-80", // portRange
         URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/targetHttpProxies/jclouds-test") // Target
      );
   }
}
