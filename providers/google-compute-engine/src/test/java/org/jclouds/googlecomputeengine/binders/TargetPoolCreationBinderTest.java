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
import java.util.List;
import java.util.Map;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions.SessionAffinityValue;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.internal.GsonWrapper;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

@Test(groups = "unit", testName = "TargetPoolCreationBinderTest")
public class TargetPoolCreationBinderTest extends BaseGoogleComputeEngineExpectTest<Object>{

   private static final List<URI> FAKE_HEALTH_CHECKS = ImmutableList.of(
         URI.create("https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/backports-debian-7-wheezy-v20141017"));
   private static SessionAffinityValue SESSION_AFFINITY = SessionAffinityValue.CLIENT_IP_PROTO;
   private static float FAILOVER_RATIO = (float) 0.4;
   private static String DESCRIPTION = "This is a test!";

   Json json = new GsonWrapper(new Gson());
 
   @Test
   public void testMap() throws SecurityException, NoSuchMethodException {
      TargetPoolCreationBinder binder = new TargetPoolCreationBinder(json);
      TargetPoolCreationOptions targetPoolCreationOptions = new TargetPoolCreationOptions()
                                                                  .healthChecks(FAKE_HEALTH_CHECKS)
                                                                  .sessionAffinity(SESSION_AFFINITY)
                                                                  .failoverRatio(FAILOVER_RATIO)
                                                                  .description(DESCRIPTION);

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      Map<String, Object> postParams = ImmutableMap.of("name", "testTargetPoolName", "options", targetPoolCreationOptions);

      binder.bindToRequest(request, postParams);

      assertEquals(request.getPayload().getRawContent(),
            "{\""
            + "name\":\"testTargetPoolName\","
            + "\"healthChecks\":[\"" + FAKE_HEALTH_CHECKS.toArray()[0] + "\"],"
            + "\"sessionAffinity\":\"CLIENT_IP_PROTO\","
            + "\"failoverRatio\":0.4,"
            + "\"description\":\"This is a test!\""
            + "}");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/json");
   }
}
