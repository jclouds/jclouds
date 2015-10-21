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
package org.jclouds.profitbricks.http.parser.server;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.AvailabilityZone;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.date.DateService;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.Nic;
import org.jclouds.profitbricks.domain.Storage;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ServerListResponseHandlerTest")
public class ServerListResponseHandlerTest extends BaseResponseHandlerTest<List<Server>> {

   @Override
   protected ParseSax<List<Server>> createParser() {
      return factory.create(injector.getInstance(ServerListResponseHandler.class));
   }

   protected DateService createDateParser() {
      return injector.getInstance(DateService.class);
   }

   @Test
   public void testParseResponseFromGetAllServers() {
      ParseSax<List<Server>> parser = createParser();

      List<Server> actual = parser.parse(payloadFromResource("/server/servers.xml"));
      assertNotNull(actual, "Parsed content returned null");

      DateService dateParser = createDateParser();

      List<Server> expected = ImmutableList.<Server>of(
              Server.builder()
              .dataCenter(DataCenter.builder()
                      .id("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
                      .version(10)
                      .build()
              )
              .id("qwertyui-qwer-qwer-qwer-qwertyyuiiop")
              .name("facebook-node")
              .cores(4)
              .ram(4096)
              .hasInternetAccess(true)
              .state(ProvisioningState.AVAILABLE)
              .status(Server.Status.RUNNING)
              .creationTime(dateParser.iso8601DateOrSecondsDateParse("2014-12-04T07:09:23.138Z"))
              .lastModificationTime(dateParser.iso8601DateOrSecondsDateParse("2014-12-12T03:08:35.629Z"))
              .osType(OsType.LINUX)
              .availabilityZone(AvailabilityZone.AUTO)
              .isCpuHotPlug(true)
              .isRamHotPlug(true)
              .isNicHotPlug(true)
              .isNicHotUnPlug(true)
              .isDiscVirtioHotPlug(true)
              .isDiscVirtioHotUnPlug(true)
              .loadBalanced(true)
              .balancedNicId("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
              .storages(ImmutableList.<Storage>of(
                              Storage.builder()
                              .bootDevice(Boolean.TRUE)
                              .busType(Storage.BusType.VIRTIO)
                              .deviceNumber(1)
                              .size(40f)
                              .id("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
                              .name("facebook-storage")
                              .build()
                      )
              )
              .nics(ImmutableList.<Nic>of(
                              Nic.builder()
                              .dataCenterId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
                              .id("qwqwqwqw-wewe-erer-rtrt-tytytytytyty")
                              .lanId(1)
                              .internetAccess(true)
                              .serverId("qwertyui-qwer-qwer-qwer-qwertyyuiiop")
                              .ips(ImmutableList.of("173.252.120.6"))
                              .macAddress("02:01:09:cd:f0:b0")
                              .firewall(Firewall.builder()
                                      .active(false)
                                      .id("wqwqwqwq-ewew-rere-trtr-ytytytytytyt")
                                      .nicId("qwqwqwqw-wewe-erer-rtrt-tytytytytyty")
                                      .state(ProvisioningState.AVAILABLE)
                                      .build())
                              .dhcpActive(true)
                              .gatewayIp("173.252.120.1")
                              .state(ProvisioningState.AVAILABLE)
                              .build()
                      )
              )
              .build(),
              Server.builder()
              .dataCenter(DataCenter.builder()
                      .id("qqqqqqqq-wwww-rrrr-tttt-yyyyyyyyyyyy")
                      .version(238)
                      .build()
              )
              .id("asdfghjk-asdf-asdf-asdf-asdfghjklkjl")
              .name("google-node")
              .cores(1)
              .ram(1024)
              .hasInternetAccess(false)
              .state(ProvisioningState.AVAILABLE)
              .status(Server.Status.RUNNING)
              .creationTime(dateParser.iso8601DateOrSecondsDateParse("2014-11-12T07:01:00.441Z"))
              .lastModificationTime(dateParser.iso8601DateOrSecondsDateParse("2014-11-12T07:01:00.441Z"))
              .osType(OsType.LINUX)
              .availabilityZone(AvailabilityZone.AUTO)
              .isCpuHotPlug(true)
              .isRamHotPlug(true)
              .isNicHotPlug(true)
              .isNicHotUnPlug(true)
              .isDiscVirtioHotPlug(true)
              .isDiscVirtioHotUnPlug(true)
              .loadBalanced(true)
              .balancedNicId("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
              .storages(ImmutableList.<Storage>of(
                              Storage.builder()
                              .bootDevice(Boolean.TRUE)
                              .busType(Storage.BusType.VIRTIO)
                              .deviceNumber(1)
                              .size(5f)
                              .id("asfasfle-f23n-cu89-klfr-njkdsvwllkfa")
                              .name("google-disk")
                              .build()
                      )
              )
              .nics(ImmutableList.<Nic>of(
                              Nic.builder()
                              .dataCenterId("qqqqqqqq-wwww-rrrr-tttt-yyyyyyyyyyyy")
                              .id("mkl45h5e-sdgb-h6rh-235r-rfweshdfhreh")
                              .lanId(3)
                              .internetAccess(false)
                              .serverId("asdfghjk-asdf-asdf-asdf-asdfghjklkjl")
                              .ips(ImmutableList.of("202.69.181.241"))
                              .macAddress("02:01:9e:5e:35:1e")
                              .firewall(Firewall.builder()
                                      .active(false)
                                      .id("cvvdsgbd-sdgj-eger-h56j-wet43gvsgeg4")
                                      .nicId("mkl45h5e-sdgb-h6rh-235r-rfweshdfhreh")
                                      .state(ProvisioningState.INPROCESS)
                                      .build())
                              .dhcpActive(false)
                              .gatewayIp("202.69.181.1")
                              .state(ProvisioningState.AVAILABLE)
                              .build()
                      )
              )
              .build()
      );

      assertEquals(actual, expected);
   }
}
