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

import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseFirewallTest")
public class ParseFirewallTest extends BaseGoogleComputeEngineParseTest<Firewall> {

   @Override
   public String resource() {
      return "/firewall_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public Firewall expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public Firewall expected(String base_url) {
      return Firewall.create( //
            "12862241031274216284", // id
            URI.create(base_url + "/party/global/firewalls/jclouds-test"), // selfLink
            parse("2012-04-13T03:05:02.855"), // creationTimestamp
            "jclouds-test", // name
            "Internal traffic from default allowed", // description
            URI.create(base_url + "/party/global/networks/jclouds-test"), // network
            ImmutableList.of("10.0.0.0/8"), // sourceRanges
            null, // sourceTags
            null, // targetTags
            ImmutableList.of( // allowed
                  Rule.create("tcp", ImmutableList.of("1-65535")), //
                  Rule.create("udp", ImmutableList.of("1-65535")), //
                  Rule.create("icmp", null) //
            ));
   }
}
