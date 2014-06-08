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

package org.jclouds.openstack.neutron.v2_0.parse;

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.openstack.neutron.v2_0.domain.Port;
import org.jclouds.openstack.neutron.v2_0.domain.State;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Test(groups = "unit", testName = "ParsePortTest")
public class ParsePortTest extends BaseItemParserTest<Port> {

   @Override
   public String resource() {
      return "/port.json";
   }

   @Override
   @SelectJson("port")
   @Consumes(MediaType.APPLICATION_JSON)
   public Port expected() {
      return Port.builder()
         .state(State.ACTIVE)
         .networkId("1234567890")
         .name("jclouds-wibble")
         .tenantId("1234567890")
         .id("624312ff-d14b-4ba3-9834-1c78d23d574d")
         .build();
   }
}
