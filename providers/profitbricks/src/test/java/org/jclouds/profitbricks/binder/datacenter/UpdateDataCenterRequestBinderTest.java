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
package org.jclouds.profitbricks.binder.datacenter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.profitbricks.domain.DataCenter;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "UpdateDataCenterRequestBinderTest")
public class UpdateDataCenterRequestBinderTest {

   @Test
   public void testCreatePayload() {
      UpdateDataCenterRequestBinder binder = new UpdateDataCenterRequestBinder();

      DataCenter.Request.UpdatePayload payload = DataCenter.Request.UpdatePayload.create("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee", "Apache-DC");

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");
      assertEquals(expectedPayload, actual);
   }

   private final String expectedPayload
	   = ("      <ws:updateDataCenter>\n"
	   + "         <request>\n"
	   + "            <dataCenterId>aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee</dataCenterId>\n"
	   + "            <dataCenterName>Apache-DC</dataCenterName>\n"
	   + "         </request>\n"
	   + "      </ws:updateDataCenter>").replaceAll("\\s+", "");
}
