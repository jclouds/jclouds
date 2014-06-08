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
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseFirewallListTest extends BaseGoogleComputeEngineParseTest<ListPage<Firewall>> {

   @Override
   public String resource() {
      return "/firewall_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Firewall> expected() {
      return ListPage.<Firewall>builder()
              .kind(Resource.Kind.FIREWALL_LIST)
              .id("projects/google/firewalls")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/google/global/firewalls"))
              .items(ImmutableSet.of(
                      new ParseFirewallTest().expected()
                      , Firewall.builder()
                      .id("12862241067393040785")
                      .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-04-13T03:05:04.365"))
                      .selfLink(URI.create("https://www.googleapis" +
                              ".com/compute/v1/projects/google/global/firewalls/default-ssh"))
                      .name("default-ssh")
                      .description("SSH allowed from anywhere")
                      .network(URI.create("https://www.googleapis" +
                              ".com/compute/v1/projects/google/global/networks/default"))
                      .addSourceRange("0.0.0.0/0")
                      .addAllowed(Firewall.Rule.builder()
                              .IpProtocol(IpProtocol.TCP)
                              .addPort(22).build())
                      .build()
              ))
              .build();
   }
}
