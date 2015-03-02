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

@Test(groups = "unit", testName = "ServerResponseHandlerTest")
public class SnapshotResponseHandlerTest extends BaseResponseHandlerTest<Snapshot> {

   @Override
   protected ParseSax<Snapshot> createParser() {
      return factory.create(injector.getInstance(SnapshotResponseHandler.class));
   }

   protected DateCodecFactory createDateParser() {
      return injector.getInstance(DateCodecFactory.class);
   }

   @Test
   public void testParseResponseFromGetSnapshot() {
      ParseSax<Snapshot> parser = createParser();

      Snapshot actual = parser.parse(payloadFromResource("/snapshot/snapshot.xml"));
      assertNotNull(actual, "Parsed content returned null");

      DateCodec dateParser = createDateParser().iso8601();

      Snapshot expected = Snapshot.builder()
	      .id("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
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
	      .build();

      assertEquals(actual, expected);

   }
}
