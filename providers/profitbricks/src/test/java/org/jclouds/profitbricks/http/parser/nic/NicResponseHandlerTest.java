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
package org.jclouds.profitbricks.http.parser.nic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.google.common.collect.ImmutableList;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "NicResponseHandlerTest")
public class NicResponseHandlerTest extends BaseResponseHandlerTest<Nic> {

   @Override
   protected ParseSax<Nic> createParser() {
      return factory.create(injector.getInstance(NicResponseHandler.class));
   }

   @Test
   public void testParseResponseFromGetNic() {
      ParseSax<Nic> parser = createParser();
      Nic actual = parser.parse(payloadFromResource("/nic/nic.xml"));
      assertNotNull(actual, "Parsed content returned null");

      Nic expected = Nic.builder()
              .id("12345678-abcd-efgh-ijkl-987654321000")
              .dataCenterId("0")
              .name("name")
              .lanId(1)
              .internetAccess(true)
              .serverId("server-id")
              .ips(ImmutableList.of("192.168.0.1"))
              .macAddress("aa:bb:cc:dd:ee:ff")
              .dhcpActive(true)
              .gatewayIp("10.0.0.1")
              .state(ProvisioningState.AVAILABLE)
              .firewall(
                      Firewall.builder()
                      .active(true)
                      .id("firewall-id")
                      .nicId("nic-id")
                      .state(ProvisioningState.AVAILABLE)
                      .build())
              .build();

      assertEquals(actual, expected);
   }
}
