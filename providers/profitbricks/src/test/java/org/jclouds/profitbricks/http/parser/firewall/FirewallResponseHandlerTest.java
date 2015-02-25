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
package org.jclouds.profitbricks.http.parser.firewall;

import java.util.List;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.profitbricks.domain.Firewall.Protocol;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "FirewallResponseHandlerTest")
public class FirewallResponseHandlerTest extends BaseResponseHandlerTest<Firewall> {

   @Override
   protected ParseSax<Firewall> createParser() {
      return factory.create(injector.getInstance(FirewallResponseHandler.class));
   }

   @Test
   public void testParseResponseFromGetFirewall() {
      ParseSax<Firewall> parser = createParser();
      Firewall actual = parser.parse(payloadFromResource("/firewall/firewall.xml"));
      assertNotNull(actual, "Parsed content returned null");
      List<Firewall.Rule> firewallRules = ImmutableList.of(
              Firewall.Rule.builder()
              .id("firewall-rule-id")
              .name("name")
              .portRangeEnd(45678)
              .portRangeStart(12345)
              .protocol(Protocol.TCP)
              .sourceIp("192.168.0.1")
              .sourceMac("aa:bb:cc:dd:ee:ff")
              .targetIp("192.168.0.2")
              .build());

      Firewall expected = Firewall.builder()
              .active(true)
              .id("firewall-id")
              .nicId("nic-id")
              .state(ProvisioningState.AVAILABLE)
              .rules(firewallRules)
              .build();

      assertEquals(expected, actual);

   }

}
