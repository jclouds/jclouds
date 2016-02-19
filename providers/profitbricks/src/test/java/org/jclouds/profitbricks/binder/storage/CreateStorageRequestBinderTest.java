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

import org.jclouds.profitbricks.domain.Storage;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "CreateStorageRequestBinderTest")
public class CreateStorageRequestBinderTest {

   @Test
   public void testCreatePayload() {
      CreateStorageRequestBinder binder = new CreateStorageRequestBinder();

      Storage.Request.CreatePayload payload = Storage.Request.creatingBuilder()
              .name("hdd-1")
              .size(60f)
              .dataCenterId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
              .mountImageId("5ad99c9e-9166-11e4-9d74-52540066fee9")
              .imagePassword("qqqqqqqqq")
              .build();

      String actual = binder.createPayload(payload);
      assertNotNull(actual, "Binder returned null payload");
      assertEquals(actual, expectedPayload);
   }

   private final String expectedPayload
           = ("      <ws:createStorage>\n"
           + "         <request>\n"
           + "            <dataCenterId>aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee</dataCenterId>\n"
           + "            <storageName>hdd-1</storageName>\n"
           + "            <size>60</size>\n"
           + "            <mountImageId>5ad99c9e-9166-11e4-9d74-52540066fee9</mountImageId>\n"
           + "            <profitBricksImagePassword>qqqqqqqqq</profitBricksImagePassword>\n"
           + "         </request>\n"
           + "      </ws:createStorage>")
           .replaceAll("\\s+", "");

}
