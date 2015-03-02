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
package org.jclouds.profitbricks.http.parser.snapshot;

import com.google.common.collect.Lists;
import java.util.List;
import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.OsType;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Snapshot;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "SnapshotListResponseHandlerTest")
public class SnapshotListResponseHandlerTest extends BaseResponseHandlerTest<List<Snapshot>> {

   @Override
   protected ParseSax<List<Snapshot>> createParser() {
      return factory.create(injector.getInstance(SnapshotListResponseHandler.class));
   }

   protected DateCodecFactory createDateParser() {
      return injector.getInstance(DateCodecFactory.class);
   }

   @Test
   public void testParseResponseFromGetSnapshot() {
      ParseSax<List<Snapshot>> parser = createParser();

      List<Snapshot> actual = parser.parse(payloadFromResource("/snapshot/snapshots.xml"));
      assertNotNull(actual);

      DateCodec dateParser = createDateParser().iso8601();

      List<Snapshot> expected = Lists.newArrayList();

      expected.add(Snapshot.builder()
	      .id("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
	      .description("description")
	      .size(1024)
	      .name("snapshot01")
	      .state(ProvisioningState.AVAILABLE)
	      .bootable(true)
	      .osType(OsType.LINUX)
	      .cpuHotPlug(true)
	      .cpuHotUnPlug(true)
	      .discVirtioHotPlug(true)
	      .discVirtioHotUnPlug(true)
	      .ramHotPlug(true)
	      .ramHotUnPlug(true)
	      .nicHotPlug(true)
	      .nicHotUnPlug(true)
	      .location(Location.US_LAS)
	      .creationTime(dateParser.toDate("2015-01-26T07:09:23.138Z"))
	      .lastModificationTime(dateParser.toDate("2015-01-26T07:09:23.138Z"))
	      .build());

      expected.add(Snapshot.builder()
	      .id("qqqqqqqq-wwww-rrrr-tttt-yyyyyyyyyyyy")
	      .description("description")
	      .size(1024)
	      .name("snapshot02")
	      .state(ProvisioningState.AVAILABLE)
	      .bootable(true)
	      .osType(OsType.LINUX)
	      .cpuHotPlug(true)
	      .cpuHotUnPlug(true)
	      .discVirtioHotPlug(true)
	      .discVirtioHotUnPlug(true)
	      .ramHotPlug(true)
	      .ramHotUnPlug(true)
	      .nicHotPlug(true)
	      .nicHotUnPlug(true)
	      .location(Location.US_LAS)
	      .creationTime(dateParser.toDate("2015-01-26T07:09:23.138Z"))
	      .lastModificationTime(dateParser.toDate("2015-01-26T07:09:23.138Z"))
	      .build());

      assertEquals(actual, expected);
   }
}
