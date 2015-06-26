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

import java.net.URI;
import java.util.Map;

import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.internal.GsonWrapper;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

@Test(groups = "unit", testName = "ForwardingRuleCreationBinderTest")
public class ForwardingRuleCreationBinderTest extends BaseGoogleComputeEngineExpectTest<Object>{

   private static String DESCRIPTION = "This is a test!";
   private static String IP_ADDRESS = "1.2.1.1.1";
   private static String PORT_RANGE = "1.2.3.4.1";
   private static URI TARGET = URI.create(BASE_URL + "/party/regions/europe-west1/targetPools/test-target-pool");

   Json json = new GsonWrapper(new Gson());

   @Test
   public void testMap() throws SecurityException, NoSuchMethodException {
      ForwardingRuleCreationBinder binder = new ForwardingRuleCreationBinder(json);
      ForwardingRuleCreationOptions forwardingRuleCreationOptions = new ForwardingRuleCreationOptions.Builder()
                                                                  .description(DESCRIPTION)
                                                                  .ipAddress(IP_ADDRESS)
                                                                  .ipProtocol(ForwardingRule.IPProtocol.SCTP)
                                                                  .portRange(PORT_RANGE)
                                                                  .target(TARGET)
                                                                  .build();

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      Map<String, Object> postParams = ImmutableMap.of("name", "testForwardingRuleName", "options", forwardingRuleCreationOptions);

      binder.bindToRequest(request, postParams);

      assertEquals(request.getPayload().getRawContent(),
            "{\""
            + "name\":\"testForwardingRuleName\","
            + "\"description\":\"" + DESCRIPTION + "\","
            + "\"IPAddress\":\"" + IP_ADDRESS + "\","
            + "\"IPProtocol\":\"SCTP\","
            + "\"portRange\":\"" + PORT_RANGE + "\","
            + "\"target\":\"" + TARGET + "\""
            + "}");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/json");
   }
}
