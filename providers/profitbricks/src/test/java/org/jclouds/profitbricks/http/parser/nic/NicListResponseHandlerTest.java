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

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "NicListResponseHandlerTest")
public class NicListResponseHandlerTest extends BaseResponseHandlerTest<List<Nic>> {

   @Override
   protected ParseSax<List<Nic>> createParser() {
      return factory.create(injector.getInstance(NicListResponseHandler.class));
   }

   @Test
   public void testParseResponseFromGetAllNic() {
      ParseSax<List<Nic>> parser = createParser();
      List<Nic> actual = parser.parse(payloadFromResource("/nic/nics.xml"));
      assertNotNull(actual, "Parsed content returned null");

      List<Nic> expected = ImmutableList.of(
              Nic.builder()
              .dataCenterId("datacenter-id")
              .id("nic-id")
              .name("nic-name")
              .lanId(1)
              .internetAccess(true)
              .serverId("server-id")
              .ips(ImmutableList.of("192.168.0.1"))
              .macAddress("aa:bb:cc:dd:ee:f1")
              .firewall(
                      Firewall.builder()
                      .active(true)
                      .id("firewall-id")
                      .nicId("nic-id")
                      .state(ProvisioningState.AVAILABLE)
                      .build()
              )
              .dhcpActive(true)
              .gatewayIp("192.168.0.0")
              .state(ProvisioningState.AVAILABLE)
              .build(),
              Nic.builder()
              .dataCenterId("datacenter-id")
              .id("nic-id2")
              .name("nick")
              .lanId(1)
              .internetAccess(false)
              .serverId("server-id")
              .ips(ImmutableList.of(
                              "192.168.0.2",
                              "192.168.0.3",
                              "192.168.0.4"
                      ))
              .macAddress("aa:bb:cc:dd:ee:f2")
              .firewall(
                      Firewall.builder()
                      .active(false)
                      .id("firewall-id2")
                      .nicId("nic-id")
                      .state(ProvisioningState.AVAILABLE)
                      .build()
              )
              .dhcpActive(false)
              .gatewayIp("192.168.0.0")
              .state(ProvisioningState.AVAILABLE)
              .build()
      );

      assertEquals(actual, expected);
   }
}
