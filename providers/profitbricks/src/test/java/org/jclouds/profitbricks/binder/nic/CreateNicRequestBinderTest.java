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
package org.jclouds.profitbricks.binder.nic;

import org.jclouds.profitbricks.domain.Nic;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "CreateNicRequestBinderTest")
public class CreateNicRequestBinderTest {

   @Test
   public void testCreatePayload() {
      CreateNicRequestBinder binder = new CreateNicRequestBinder();

      Nic.Request.CreatePayload payload = Nic.Request.creatingBuilder()
              .ip("192.168.0.1")
              .name("nic-name")
              .dhcpActive(true)
              .serverId("server-id")
              .lanId(1)
              .build();

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");
      assertEquals(expectedPayload, actual);
   }

   private final String expectedPayload = ("<ws:createNic>\n"
           + "            <request>\n"
           + "                <ip>192.168.0.1</ip>\n"
           + "                <nicName>nic-name</nicName>\n"
           + "                <dhcpActive>true</dhcpActive>\n"
           + "                <serverId>server-id</serverId>\n"
           + "                <lanId>1</lanId>\n"
           + "            </request>\n"
           + "        </ws:createNic>").replaceAll("\\s+", "");
}
