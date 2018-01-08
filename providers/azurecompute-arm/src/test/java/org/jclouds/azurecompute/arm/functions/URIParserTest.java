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
package org.jclouds.azurecompute.arm.functions;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;
import com.google.common.collect.Multimap;
import com.google.common.collect.LinkedHashMultimap;

@Test(groups = "unit", testName = "URIParserTest")
public class URIParserTest {

   public void testApply() {
      URIParser parser = new URIParser();
      Multimap<String, String> headers = LinkedHashMultimap.<String, String>create();

      URI uri = parser.apply(HttpResponse.builder().statusCode(200).build());
      assertNull(uri);

      try {
         uri = parser.apply(HttpResponse.builder().statusCode(202).build());
      } catch (IllegalStateException ex) {
         assertNotNull(ex);
      }

      headers.put("Location", "https://someuri");

      uri = parser.apply(HttpResponse.builder().statusCode(202).headers(headers).build());
      assertNotNull(uri);

   }
}
