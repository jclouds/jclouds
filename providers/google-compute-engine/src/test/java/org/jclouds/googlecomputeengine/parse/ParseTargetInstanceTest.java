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
package org.jclouds.googlecomputeengine.parse;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;

import javax.ws.rs.Consumes;

import org.jclouds.googlecomputeengine.domain.TargetInstance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseTargetInstanceTest")
public class ParseTargetInstanceTest extends BaseGoogleComputeEngineParseTest<TargetInstance> {

   @Override
   public String resource() {
      return "/target_instance_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public TargetInstance expected() {
      return expected(BASE_URL);
   }

   public TargetInstance expected(String baseUrl) {
      return TargetInstance.create(
            "13050421646334304115", // id
            "2014-07-18T09:47:30.826-07:00", // creationTimestamp
            "target-instance-1", // name
            "A pretty cool target instance", // description
            URI.create(baseUrl + "/party/zones/us-central1-a"), // zone
            "NO_NAT", // natPolicy
            URI.create(baseUrl + "/party/zones/us-central1-a/instances/test-0"), // instance
            URI.create(baseUrl + "/party/zones/us-central1-a/targetInstances/target-instance-1")); //selfLink
   }
}
