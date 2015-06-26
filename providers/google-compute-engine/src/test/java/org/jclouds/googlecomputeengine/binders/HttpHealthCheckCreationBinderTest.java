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
package org.jclouds.googlecomputeengine.binders;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.internal.GsonWrapper;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

@Test(groups = "unit", testName = "HttpHealthCheckCreationBinderTest")
public class HttpHealthCheckCreationBinderTest extends BaseGoogleComputeEngineExpectTest<Object>{

   private String NAME = "testHttpHealthCheck";
   private Integer TIMEOUTSEC = 3;
   private Integer UNHEALTHYTHRESHOLD = 5;
   private Integer HEALTHYTHRESHOLD = 4;
   private static String DESCRIPTION = "This is a test!";

   Json json = new GsonWrapper(new Gson());

   @Test
   public void testMap() throws SecurityException, NoSuchMethodException {
      HttpHealthCheckCreationBinder binder = new HttpHealthCheckCreationBinder(json);
      HttpHealthCheckCreationOptions httpHealthCheckCreationOptions = new HttpHealthCheckCreationOptions.Builder()
                                                                              .timeoutSec(TIMEOUTSEC)
                                                                              .unhealthyThreshold(UNHEALTHYTHRESHOLD)
                                                                              .healthyThreshold(HEALTHYTHRESHOLD)
                                                                              .description(DESCRIPTION)
                                                                              .buildWithDefaults();

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      Map<String, Object> postParams = ImmutableMap.of("name", NAME, "options", httpHealthCheckCreationOptions);

      binder.bindToRequest(request, postParams);

      assertEquals(request.getPayload().getRawContent(),
            "{"
            + "\"name\":\"" + NAME + "\","
            + "\"requestPath\":\"/\","
            + "\"port\":80,"
            + "\"checkIntervalSec\":5,"
            + "\"timeoutSec\":" + TIMEOUTSEC + ","
            + "\"unhealthyThreshold\":" + UNHEALTHYTHRESHOLD + ","
            + "\"healthyThreshold\":" + HEALTHYTHRESHOLD + ","
            + "\"description\":\"" + DESCRIPTION + "\""
            + "}");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/json");
   }
}
