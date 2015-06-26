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

import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseFirewallListTest")
public class ParseFirewallListTest extends BaseGoogleComputeEngineParseTest<ListPage<Firewall>> {

   @Override
   public String resource() {
      return "/firewall_list.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public ListPage<Firewall> expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public ListPage<Firewall> expected(String baseUrl) {
      Firewall firewall1 = new ParseFirewallTest().expected(baseUrl);
      Firewall firewall2 = Firewall.create( //
            "12862241067393040785", // id
            URI.create(baseUrl + "/google/global/firewalls/default-ssh"), // selfLink
            parse("2012-04-13T03:05:04.365"), // creationTimestamp
            "default-ssh", // name
            "SSH allowed from anywhere", // description
            URI.create(baseUrl + "/google/global/networks/default"), // network
            ImmutableList.of("0.0.0.0/0"), // sourceRanges
            null, // sourceTags
            null, // targetTags
            ImmutableList.of(Firewall.Rule.create("tcp", ImmutableList.of("22"))) // allowed
      );
      return ForwardingListPage.create( //
            ImmutableList.of(firewall1, firewall2), // items
            null // nextPageToken
      );
   }
}
