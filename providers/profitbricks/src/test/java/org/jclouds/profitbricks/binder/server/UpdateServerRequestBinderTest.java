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

@Test( groups = "unit", testName = "UpdateServerRequestBinderTest" )
public class UpdateServerRequestBinderTest {

   @Test
   public void testCreatePayload() {
      UpdateServerRequestBinder binder = new UpdateServerRequestBinder();

      Server.Request.UpdatePayload payload = Server.Request.updatingBuilder()
              .id( "qwertyui-qwer-qwer-qwer-qwertyyuiiop" )
              .cores( 8 )
              .ram( 8 * 1024 )
              .name( "apache-node")
              .build();
      
      String actual = binder.createPayload( payload );
      assertNotNull(actual, "Binder returned null payload");
      assertEquals(actual, expectedPayload);
   }

   private final String expectedPayload
           = ( "      <ws:updateServer>\n"
           + "         <request>\n"
           + "            <serverId>qwertyui-qwer-qwer-qwer-qwertyyuiiop</serverId>\n"
           + "            <cores>8</cores>\n"
           + "            <ram>8192</ram>\n"
           + "            <serverName>apache-node</serverName>\n"
//           + "            <bootFromStorageId>?</bootFromStorageId>\n"
//           + "            <bootFromImageId>?</bootFromImageId>\n"
//           + "            <osType>?</osType>\n"
//           + "            <availabilityZone>?</availabilityZone>\n"
//           + "            <cpuHotPlug>?</cpuHotPlug>\n"
//           + "            <ramHotPlug>?</ramHotPlug>\n"
//           + "            <nicHotPlug>?</nicHotPlug>\n"
//           + "            <nicHotUnPlug>?</nicHotUnPlug>\n"
//           + "            <discVirtioHotPlug>?</discVirtioHotPlug>\n"
//           + "            <discVirtioHotUnPlug>?</discVirtioHotUnPlug>\n"
           + "         </request>\n"
           + "      </ws:updateServer>" )
           .replaceAll( "\\s+", "" );

}
