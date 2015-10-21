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
package org.jclouds.profitbricks.domain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "FirewallRuleBuilderTest")
public class FirewallRuleBuilderTest {

   private final String _name = "rule-name";
   private final Integer _portRangeEnd = 45678;
   private final Integer _portRangeStart = 12345;
   private final Firewall.Protocol _protocol = Firewall.Protocol.TCP;
   private final String _sourceIp = "192.168.0.1";
   private final String _sourceMac = "aa:bb:cc:dd:ee:ff";
   private final String _targetIp = "192.168.0.2";

   private final Integer _icmpType = 2;
   private final Integer _icmpCode = 24;

   @Test
   public void testAutoValueFirewallRulePropertiesSettingCorrectly() {
      Firewall.Rule actual = Firewall.Rule.builder()
              .name(_name)
              .portRangeEnd(_portRangeEnd)
              .portRangeStart(_portRangeStart)
              .protocol(_protocol)
              .sourceIp(_sourceIp)
              .sourceMac(_sourceMac)
              .targetIp(_targetIp)
              .build();

      assertEquals(actual.name(), _name);
      assertEquals(actual.portRangeEnd(), _portRangeEnd);
      assertEquals(actual.portRangeStart(), _portRangeStart);
      assertEquals(actual.protocol(), _protocol);
      assertEquals(actual.sourceIp(), _sourceIp);
      assertEquals(actual.sourceMac(), _sourceMac);
      assertEquals(actual.targetIp(), _targetIp);
   }

   @Test
   public void testAutoValueFirewallRuleWithIcmpPropertiesSettingCorrectly() {
      Firewall.Rule actual = Firewall.Rule.builder()
              .name(_name)
              .icmpCode(_icmpCode)
              .icmpType(_icmpType)
              .protocol(Firewall.Protocol.ICMP)
              .sourceIp(_sourceIp)
              .sourceMac(_sourceMac)
              .targetIp(_targetIp)
              .build();

      assertEquals(actual.name(), _name);
      assertNull(actual.portRangeEnd());
      assertNull(actual.portRangeStart());
      assertEquals(actual.protocol(), Firewall.Protocol.ICMP);
      assertEquals(actual.sourceIp(), _sourceIp);
      assertEquals(actual.sourceMac(), _sourceMac);
      assertEquals(actual.targetIp(), _targetIp);
      assertEquals(actual.icmpCode(), _icmpCode);
      assertEquals(actual.icmpType(), _icmpType);
   }

}
