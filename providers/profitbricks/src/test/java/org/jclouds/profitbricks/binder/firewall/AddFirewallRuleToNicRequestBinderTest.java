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
package org.jclouds.profitbricks.binder.firewall;

import org.jclouds.profitbricks.domain.Firewall;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.google.common.collect.ImmutableList;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "AddFirewallRuleToNicRequestBinderTest")
public class AddFirewallRuleToNicRequestBinderTest {

   @Test
   public void testCreatePayload() {
      AddFirewallRuleToNicRequestBinder binder = new AddFirewallRuleToNicRequestBinder();

      Firewall.Request.AddRulePayload payload = Firewall.Request.createAddRulePayload(
              "nic-id", ImmutableList.of(
                      Firewall.Rule.builder()
                      .name("name")
                      .portRangeEnd(45678)
                      .portRangeStart(12345)
                      .protocol(Firewall.Protocol.TCP)
                      .sourceIp("192.168.0.1")
                      .sourceMac("aa:bb:cc:dd:ee:ff")
                      .targetIp("192.168.0.2")
                      .build()
              ));

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");
      assertEquals(expectedPayload, actual);
   }

   private final String expectedPayload = ("  <ws:addFirewallRulesToNic>\n"
           + "        <nicId>nic-id</nicId>\n"
           + "            <request>\n"
           + "                <name>name</name>\n"
           + "                <portRangeEnd>45678</portRangeEnd>\n"
           + "                <portRangeStart>12345</portRangeStart>\n"
           + "                <protocol>TCP</protocol>\n"
           + "                <sourceIp>192.168.0.1</sourceIp>\n"
           + "                <sourceMac>aa:bb:cc:dd:ee:ff</sourceMac>\n"
           + "                <targetIp>192.168.0.2</targetIp>\n"
           + "            </request>\n"
           + "        </ws:addFirewallRulesToNic>").replaceAll("\\s+", "");
}
