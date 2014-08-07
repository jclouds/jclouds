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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.BackendService;
import org.jclouds.googlecomputeengine.domain.BackendService.Backend;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseBackendServiceTest extends BaseGoogleComputeEngineParseTest<BackendService> {

   @Override
   public String resource() {
      return "/backend_service_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public BackendService expected() {
      URI selfLink = URI.create("https://www.googleapis.com/compute/v1/"
                                + "projects/myproject/global/backendServices/"
                                + "jclouds-test");
      URI healthCheck = URI.create("https://www.googleapis.com/compute/v1/"
                                   + "projects/myproject/global/httpHealthChecks"
                                   + "/jclouds-test");
      URI group = URI.create("https://www.googleapis.com/resourceviews/v1beta1"
                             + "/projects/myproject/zones/us-central1-a/"
                             + "resourceViews/jclouds-test");
      Backend backend = Backend.builder()
                               .balancingMode("UTILIZATION")
                               .capacityScaler((float) 1.0)
                               .description("A resource view")
                               .group(group)
                               .maxUtilization((float) 0.8).build();
      return BackendService.builder()
              .id("15448612110458377529")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2014-07-18T13:37:48.574-07:00"))
              .selfLink(selfLink)
              .name("jclouds-test")
              .addHealthCheck(healthCheck)
              .port(80)
              .protocol("HTTP")
              .timeoutSec(30)
              .fingerprint("I6n5NPSXn8g=")
              .description("Backend service")
              .backends(ImmutableSet.of(backend))
              .build();
   }
}