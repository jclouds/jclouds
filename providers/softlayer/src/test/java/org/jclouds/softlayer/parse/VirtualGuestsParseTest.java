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
package org.jclouds.softlayer.parse;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.internal.BaseSoftLayerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class VirtualGuestsParseTest extends BaseSoftLayerParseTest<Set<VirtualGuest>> {

   @Override
   public String resource() {
      return "/account_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<VirtualGuest> expected() {
      return ImmutableSet.of(
                      VirtualGuest.builder()
                              .accountId(278184)
                              .createDate(new SimpleDateFormatDateService().iso8601DateParse("2013-07-26T14:08:21.552-07:00"))
                              .dedicatedAccountHostOnly(false)
                              .domain("test.com")
                              .fullyQualifiedDomainName("my.test.com")
                              .hostname("my")
                              .id(3001812)
                              .lastVerifiedDate(null)
                              .maxCpu(1)
                              .maxCpuUnits("CORE")
                              .maxMemory(1024)
                              .metricPollDate(null)
                              .modifyDate(new SimpleDateFormatDateService().iso8601DateParse("2013-07-26T14:10:21.552-07:00"))
                              .privateNetworkOnlyFlag(false)
                              .startCpus(1)
                              .statusId(1001)
                              .uuid("92102aff-93c9-05f1-b3f2-50787e865344")
                              .primaryBackendIpAddress("10.32.23.74")
                              .primaryIpAddress("174.37.252.118")
                              .billingItemId(0)
                              .operatingSystem(null)
                              .datacenter(null)
                              .powerState(null)
                              .softwareLicense(null)
                              .build());
   }
}
