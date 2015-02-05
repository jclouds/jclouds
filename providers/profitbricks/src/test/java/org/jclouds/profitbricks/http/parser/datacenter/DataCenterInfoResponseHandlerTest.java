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
package org.jclouds.profitbricks.http.parser.datacenter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.AvailabilityZone;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Server;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "DataCenterInfoResponseHandlerTest")
public class DataCenterInfoResponseHandlerTest extends BaseResponseHandlerTest<DataCenter> {

   @Override
   protected ParseSax<DataCenter> createParser() {
      return factory.create(injector.getInstance(DataCenterInfoResponseHandler.class));
   }

   protected DateCodecFactory createDateParser() {
      return injector.getInstance(DateCodecFactory.class);
   }

   @Test
   public void testParseResponseFromGetDataCenter() {
      ParseSax<DataCenter> parser = createParser();

      DataCenter actual = parser.parse(payloadFromResource("/datacenter/datacenter.xml"));
      assertNotNull(actual, "Parsed content returned null");

      DateCodec dateParser = createDateParser().iso8601();

      DataCenter expected = DataCenter.builder()
              .id("12345678-abcd-efgh-ijkl-987654321000")
              .version(10)
              .name("JClouds-DC")
              .state(ProvisioningState.AVAILABLE)
              .location(Location.US_LAS)
              .servers(ImmutableList.<Server>of(
                              Server.builder()
                              .id("12345678-abcd-efgh-ijkl-987654321000")
                              .name("jnode1")
                              .cores(4)
                              .ram(4096)
                              .hasInternetAccess(true)
                              .state(ProvisioningState.AVAILABLE)
                              .status(Server.Status.RUNNING)
                              .creationTime(dateParser.toDate("2014-12-04T07:09:23.138Z"))
                              .lastModificationTime(dateParser.toDate("2014-12-12T03:08:35.629Z"))
                              .osType(OsType.LINUX)
                              .availabilityZone(AvailabilityZone.AUTO)
                              .isCpuHotPlug(true)
                              .isRamHotPlug(true)
                              .isNicHotPlug(true)
                              .isNicHotUnPlug(true)
                              .isDiscVirtioHotPlug(true)
                              .isDiscVirtioHotUnPlug(true)
                              .build()
                      ))
              .storages(ImmutableList.<Storage>of(
                              Storage.builder()
                              .id("ssssssss-aaaa-ffff-gggg-hhhhhhhhhhhh")
                              .size(40)
                              .name("jnode1-disk1")
                              .state(ProvisioningState.AVAILABLE)
                              .creationTime(dateParser.toDate("2014-12-04T07:09:23.138Z"))
                              .lastModificationTime(dateParser.toDate("2014-12-12T03:14:48.316Z"))
                              .build()
                      ))
              .build();
      assertEquals(actual, expected);
   }
}
