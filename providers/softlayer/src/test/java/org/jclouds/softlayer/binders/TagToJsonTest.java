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
package org.jclouds.softlayer.binders;

import static org.testng.Assert.assertEquals;
import java.util.Set;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.internal.GsonWrapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;

@Test(groups = "unit", testName = "TagToJsonTest")
public class TagToJsonTest {

   private Json json;

   @BeforeClass
   public void init() {
      json = new GsonWrapper(new Gson());
   }

   @Test
   public void testVirtualGuestWithOperatingSystem() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest").build();
      TagToJson binder = new TagToJson(json);
      Set<String> tags = ImmutableSet.of("jclouds");

      request = binder.bindToRequest(request, tags);

      assertEquals(request.getPayload().getRawContent(), "{\"parameters\":[\"jclouds\"]}");
   }
}
