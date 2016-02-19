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
package org.jclouds.profitbricks.http.parser.storage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "StorageInfoResponseHandlerTest")
public class StorageInfoResponseHandlerTest extends BaseResponseHandlerTest<Storage> {

   @Override
   protected ParseSax<Storage> createParser() {
      return factory.create(injector.getInstance(StorageInfoResponseHandler.class));
   }

   protected DateService createDateParser() {
      return injector.getInstance(DateService.class);
   }

   @Test
   public void testParseResponseFromGetStorage() {
      ParseSax<Storage> parser = createParser();

      Storage actual = parser.parse(payloadFromResource("/storage/storage.xml"));
      assertNotNull(actual, "Parsed content returned null");

      DateService dateParser = createDateParser();

      Storage expected = Storage.builder()
              .id("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
              .size(40)
              .name("hdd-1")
              .state(ProvisioningState.AVAILABLE)
              .serverIds(ImmutableList.<String>of("qwertyui-qwer-qwer-qwer-qwertyyuiiop"))
              .creationTime(dateParser.iso8601DateOrSecondsDateParse("2014-12-04T07:09:23.138Z"))
              .lastModificationTime(dateParser.iso8601DateOrSecondsDateParse("2014-12-12T03:14:48.316Z"))
              .build();

      assertEquals(actual, expected);
   }

}
