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
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Test(groups = "unit", testName = "TargetPoolAddInstanceBinderTest")
public class TargetPoolAddInstanceBinderTest extends BaseGoogleComputeEngineExpectTest<Object>{

   private static final List<URI> FAKE_INSTANCES = ImmutableList.of(
                                       URI.create("https://www.googleapis.com/compute/v1/" +
                                                  "projects/project/zones/us-central1-a/instances/instance-1"),
                                       URI.create("https://www.googleapis.com/compute/v1/" +
                                                  "projects/project/zones/us-central1-a/instances/instance-2"));

   TargetPoolChangeInstancesBinder binder = new TargetPoolChangeInstancesBinder();
 
   @Test
   public void testMap() throws SecurityException, NoSuchMethodException {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      Map<String, Object> postParams = ImmutableMap.of("instances", (Object) FAKE_INSTANCES);

      binder.bindToRequest(request, postParams);

      assertEquals(request.getPayload().getRawContent(),
               "{"
            + "\"instances\":["
            + "{\"instance\":\"https://www.googleapis.com/compute/v1/projects/project/zones/us-central1-a/instances/instance-1\"},"
            + "{\"instance\":\"https://www.googleapis.com/compute/v1/projects/project/zones/us-central1-a/instances/instance-2\"}"
            + "]"
            + "}");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/json");
   }
}
