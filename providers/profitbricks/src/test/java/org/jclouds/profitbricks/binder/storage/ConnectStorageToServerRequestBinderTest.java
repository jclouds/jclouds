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
package org.jclouds.profitbricks.binder.storage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.profitbricks.domain.Storage;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ConnectStorageToServerRequestBinderTest")
public class ConnectStorageToServerRequestBinderTest {

   @Test
   public void testCreatePayload() {
      ConnectStorageToServerRequestBinder binder = new ConnectStorageToServerRequestBinder();

      Storage.Request.ConnectPayload payload = Storage.Request.connectingBuilder()
              .serverId("qwertyui-qwer-qwer-qwer-qwertyyuiiop")
              .storageId("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
              .busType(Storage.BusType.VIRTIO)
              .deviceNumber(2)
              .build();

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");
      assertEquals(actual, expectedPayload);
   }

   private final String expectedPayload
           = ("      <ws:connectStorageToServer>\n"
           + "         <request>\n"
           + "            <storageId>qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh</storageId>\n"
           + "            <serverId>qwertyui-qwer-qwer-qwer-qwertyyuiiop</serverId>\n"
           + "            <busType>VIRTIO</busType>\n"
           + "            <deviceNumber>2</deviceNumber>\n"
           + "         </request>\n"
           + "      </ws:connectStorageToServer>")
           .replaceAll("\\s+", "");

}
