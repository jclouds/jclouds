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

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.profitbricks.domain.ProvisioningState;
import org.jclouds.profitbricks.domain.Storage;
import org.jclouds.profitbricks.http.parser.BaseResponseHandlerTest;

import org.testng.annotations.Test;

@Test(groups = "unit", testName = "StorageListResponseHandlerTest")
public class StorageListResponseHandlerTest extends BaseResponseHandlerTest<List<Storage>> {

   @Override
   protected ParseSax<List<Storage>> createParser() {
      return factory.create(injector.getInstance(StorageListResponseHandler.class));
   }

   protected DateService createDateParser() {
      return injector.getInstance(DateService.class);
   }

   @Test
   public void testParseResponseFromGetAllStorages() {
      ParseSax<List<Storage>> parser = createParser();

      List<Storage> actual = parser.parse(payloadFromResource("/storage/storages.xml"));
      assertNotNull(actual, "Parsed content returned null");

      DateService dateParser = createDateParser();

      List<Storage> expected = ImmutableList.<Storage>of(
              Storage.builder()
              .id("qswdefrg-qaws-qaws-defe-rgrgdsvcxbrh")
              .size(40f)
              .name("hdd-1")
              .state(ProvisioningState.AVAILABLE)
              .serverIds(ImmutableList.<String>of("qwertyui-qwer-qwer-qwer-qwertyyuiiop"))
              .creationTime(dateParser.iso8601DateOrSecondsDateParse("2014-12-04T07:09:23.138Z"))
              .lastModificationTime(dateParser.iso8601DateOrSecondsDateParse("2014-12-12T03:14:48.316Z"))
              .build(),
              Storage.builder()
              .id("asfasfle-f23n-cu89-klfr-njkdsvwllkfa")
              .size(100f)
              .name("hdd-2")
              .state(ProvisioningState.INPROCESS)
              .serverIds(ImmutableList.<String>of("asdfghjk-asdf-asdf-asdf-asdfghjklkjl"))
              .creationTime(dateParser.iso8601DateOrSecondsDateParse("2014-11-04T07:09:23.138Z"))
              .lastModificationTime(dateParser.iso8601DateOrSecondsDateParse("2014-11-12T03:14:48.316Z"))
              .build()
      );

      assertEquals(actual, expected);
   }

}
