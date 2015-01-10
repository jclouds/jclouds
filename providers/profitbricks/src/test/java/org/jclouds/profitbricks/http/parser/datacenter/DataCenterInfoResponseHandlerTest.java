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

import javax.xml.parsers.ParserConfigurationException;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.DataCenter;
import org.jclouds.profitbricks.domain.Location;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "DataCenterInfoResponseHandlerTest")
public class DataCenterInfoResponseHandlerTest extends BaseResponseHandlerTest<DataCenter> {

   @Override
   protected ParseSax<DataCenter> createParser() {
      return factory.create(injector.getInstance(DataCenterInfoResponseHandler.class));
   }

   @Test
   public void testParseResponseFromGetDataCenter() throws ParserConfigurationException {
      ParseSax<DataCenter> parser = createParser();
      DataCenter actual = parser.parse(payloadFromResource("/datacenter/datacenter.xml"));
      assertNotNull(actual, "Parsed content returned null");

      DataCenter expected = DataCenter.builder()
	      .id("12345678-abcd-efgh-ijkl-987654321000")
	      .version(10)
	      .name("JClouds-DC")
	      .state(ProvisioningState.AVAILABLE)
	      .location(Location.US_LAS)
	      .build();
      assertEquals(expected, actual);
   }
}
