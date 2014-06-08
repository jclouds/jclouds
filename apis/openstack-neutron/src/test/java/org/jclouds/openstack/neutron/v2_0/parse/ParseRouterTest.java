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
import org.jclouds.openstack.neutron.v2_0.domain.ExternalGatewayInfo;
import org.jclouds.openstack.neutron.v2_0.domain.Router;
import org.jclouds.openstack.neutron.v2_0.domain.State;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Test(groups = "unit", testName = "ParseRouterTest")
public class ParseRouterTest extends BaseItemParserTest<Router> {

   @Override
   public String resource() {
      return "/router.json";
   }

   @Override
   @SelectJson("router")
   @Consumes(MediaType.APPLICATION_JSON)
   public Router expected() {
      return Router.builder()
         .externalGatewayInfo(ExternalGatewayInfo.builder().networkId("624312ff-d14b-4ba3-9834-1c78d23d574d").build())
         .state(State.ACTIVE)
         .name("jclouds-wibble")
         .tenantId("1234567890")
         .id("16dba3bc-f3fa-4775-afdc-237e12c72f6a")
         .build();
   }

}
