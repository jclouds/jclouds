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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.BackendService;
import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class ParseBackendServiceListTest extends BaseGoogleComputeEngineParseTest<ListPage<BackendService>> {

   @Override
   public String resource() {
      return "/backend_service_list.json";
   }

   @Override
   @Consumes(APPLICATION_JSON)
   public ListPage<BackendService> expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public ListPage<BackendService> expected(String baseUrl) {
      return ForwardingListPage.create(
            ImmutableList.of(
                  new ParseBackendServiceTest().expected(baseUrl),
                  BackendService.create("12862241067393040785", //id
                        new SimpleDateFormatDateService().iso8601DateParse("2012-04-13T03:05:04.365"), //creationTimestamp,
                        URI.create(baseUrl + "/myproject/global/backendServices/jclouds-test-2"), //selfLink,
                        "jclouds-test-2", //name,
                        "Backend Service 2", //description
                        null, // backends,
                        ImmutableList.of(URI.create(baseUrl + "/myproject/global/httpHealthChecks/jclouds-test")), //healthChecks,
                        45, //timeoutSec,
                        80, //port,
                        "HTTP", //protocol,
                        null, //fingerprint
                        null) // portName
                  ),
            null
      );
   }
}
