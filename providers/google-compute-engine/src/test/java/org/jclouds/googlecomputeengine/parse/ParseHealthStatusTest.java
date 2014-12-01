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

import org.jclouds.googlecomputeengine.domain.HealthStatus;
import org.jclouds.googlecomputeengine.domain.HealthStatus.HealthStatusInternal;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class ParseHealthStatusTest extends BaseGoogleComputeEngineParseTest<HealthStatus> {

   @Override
   public String resource() {
      return "/health_status_get_health.json";
   }

   @Override
   @Consumes(APPLICATION_JSON)
   public HealthStatus expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public HealthStatus expected(String baseUrl) {
      URI uri = URI.create(baseUrl + "/party/zones/us-central1-a/instances/"
                           + "jclouds-test");
      return HealthStatus.create(
            ImmutableList.of(HealthStatusInternal.create(
                  null, // ipAddress
                  80, // port
                  uri, // instance
                  "HEALTHY" //healthState
                  )) //healthStatuses
            );
   }
}
