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

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions.SessionAffinityValue;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseTargetPoolTest")
public class ParseTargetPoolTest extends BaseGoogleComputeEngineParseTest<TargetPool> {

   @Override
   public String resource() {
      return "/targetpool_get.json";
   }

   @Override @Consumes(MediaType.APPLICATION_JSON)
   public TargetPool expected() {
      return expected(BASE_URL);
   }

   @Consumes(MediaType.APPLICATION_JSON)
   public TargetPool expected(String baseUrl) {
      return TargetPool.create( //
            "5199309593612841404", // id
            URI.create(baseUrl + "/party/regions/us-central1/targetPools/test-targetpool"), // selfLink
            parse("2014-01-07T05:25:27.783-08:00"), // creationTimestamp
            "test-targetpool", // name
            null, // description
            URI.create(baseUrl + "/party/regions/us-central1"), // region
            null, // healthChecks
            null, // instances
            SessionAffinityValue.NONE, // sessionAffinity
            null, // failoverRatio
            null // backupPool
      );
   }
}
