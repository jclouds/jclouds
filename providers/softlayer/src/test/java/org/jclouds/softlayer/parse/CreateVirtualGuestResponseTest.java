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

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.internal.BaseSoftLayerParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class CreateVirtualGuestResponseTest extends BaseSoftLayerParseTest<VirtualGuest> {

   @Override
   public String resource() {
      return "/virtual_guest_create_response.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public VirtualGuest expected() {
      return VirtualGuest.builder()
              .accountId(232298)
              .createDate(new SimpleDateFormatDateService().iso8601DateParse("2012-11-30T22:28:17.000Z"))
              .dedicatedAccountHostOnly(false)
              .domain("example.com")
              .hostname("host1")
              .id(1301396)
              .maxCpu(1)
              .maxCpuUnits("CORE")
              .maxMemory(1024)
              .privateNetworkOnlyFlag(false)
              .startCpus(1)
              .statusId(1001)
              .billingItemId(0)
              .operatingSystem(null)
              .datacenter(null)
              .build();
   }
}
