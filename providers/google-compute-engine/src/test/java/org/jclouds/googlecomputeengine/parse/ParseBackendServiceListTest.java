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
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseBackendServiceListTest extends BaseGoogleComputeEngineParseTest<ListPage<BackendService>> {

   @Override
   public String resource() {
      return "/backend_service_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<BackendService> expected() {
      return ListPage.<BackendService>builder()
              .kind(Resource.Kind.BACKEND_SERVICE_LIST)
              .id("projects/myproject/backendServices")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/backendServices"))
              .items(ImmutableSet.of(
                      new ParseBackendServiceTest().expected(),
                      BackendService.builder()
                      .id("12862241067393040785")
                      .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-04-13T03:05:04.365"))
                      .selfLink(URI.create("https://www.googleapis" +
                              ".com/compute/v1/projects/myproject/global/backendServices/jclouds-test-2"))
                      .name("jclouds-test-2")
                      .description("Backend Service 2")
                      .port(80)
                      .protocol("HTTP")
                      .timeoutSec(45)
                      .healthChecks(ImmutableSet.of(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global/httpHealthChecks/jclouds-test")))
                      .build()
              ))
              .build();
   }
}
