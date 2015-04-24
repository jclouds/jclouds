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
package org.jclouds.s3.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code PartIdsFromHttpResponse}
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "PartIdsFromHttpResponseTest")
public final class PartIdsFromHttpResponseTest extends BaseHandlerTest {
   private final DateService dateService = new SimpleDateFormatDateService();

   @Test
   public void test() {
      Map<Integer, String> actual = createParser().parse(getClass().getResourceAsStream(
            "/multipart-upload-list-parts.xml"));

      Map<Integer, String> expected = ImmutableMap.of(
            2, "\"7778aef83f66abc1fa1e8477f296d394\"",
            3, "\"aaaa18db4cc2f85cedef654fccc4a4x8\"");

      assertThat(actual).isEqualTo(expected);
   }

   private ParseSax<Map<Integer, String>> createParser() {
      return factory.create(injector.getInstance(PartIdsFromHttpResponse.class)).setContext(
               HttpRequest.builder().method("GET").endpoint("http://bucket.com").build());
   }
}
