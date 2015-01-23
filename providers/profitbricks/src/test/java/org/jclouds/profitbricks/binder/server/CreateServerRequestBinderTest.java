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
package org.jclouds.profitbricks.binder.server;

import org.jclouds.profitbricks.domain.Server;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test( groups = "unit", testName = "CreateServerRequestBinderTest" )
public class CreateServerRequestBinderTest {

   @Test
   public void testCreatePayload() {
      CreateServerRequestBinder binder = new CreateServerRequestBinder();

      Server.Request.CreatePayload payload = Server.Request.creatingBuilder()
              .name( "jclouds-node" )
              .cores( 4 )
              .ram( 4 * 1024 )
              .dataCenterId( "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee" )
              .build();

      String actual = binder.createPayload( payload );
      assertNotNull( actual, "Binder returned null payload" );
      assertEquals( actual, expectedPayload );
   }

   private final String expectedPayload
           = ( "      <ws:createServer>\n"
           + "         <request>\n"
           + "            <dataCenterId>aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee</dataCenterId>\n"
           + "            <cores>4</cores>\n"
           + "            <ram>4096</ram>\n"
           + "            <serverName>jclouds-node</serverName>\n"
//           + "            <bootFromStorageId>?</bootFromStorageId>\n"
//           + "            <bootFromImageId>?</bootFromImageId>\n"
//           + "            <internetAccess>false</internetAccess>\n"
//           + "            <lanId>?</lanId>\n"
//           + "            <osType>?</osType>\n"
//           + "            <availabilityZone>AUTO</availabilityZone>\n"
//           + "            <cpuHotPlug>false</cpuHotPlug>\n"
//           + "            <ramHotPlug>false</ramHotPlug>\n"
//           + "            <nicHotPlug>false</nicHotPlug>\n"
//           + "            <nicHotUnPlug>false</nicHotUnPlug>\n"
//           + "            <discVirtioHotPlug>false</discVirtioHotPlug>\n"
//           + "            <discVirtioHotUnPlug>false</discVirtioHotUnPlug>\n"
           + "         </request>\n"
           + "      </ws:createServer>" )
           .replaceAll( "\\s+", "" );
}
