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

import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseHttpHealthCheckListTest")
public class ParseHttpHealthCheckListTest extends BaseGoogleComputeEngineParseTest<ListPage<HttpHealthCheck>> {

   @Override
   public String resource() {
      return "/httphealthcheck_list.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public ListPage<HttpHealthCheck> expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public ListPage<HttpHealthCheck> expected(String baseUrl) {
      HttpHealthCheck healthCheck1 = new ParseHttpHealthCheckTest().expected(baseUrl);
      HttpHealthCheck healthCheck2 = HttpHealthCheck.create( //
            "1035854271083519643", // id
            URI.create(baseUrl + "/party-gce/global/httpHealthChecks/myname-andrea-kmzmi1bh-http-health-check"),
            // selfLink
            parse("2014-01-08T14:38:29.363-08:00"),
            "myname-andrea-kmzmi1bh-http-health-check", // name
            null, // description
            null, // host
            null, // requestPath
            null,  // port
            null,  // checkIntervalSec
            5,  // timeoutSec
            2,  // unhealthyThreshold
            null // healthyThreshold
      );
      HttpHealthCheck healthCheck3 = HttpHealthCheck.create( //
            "7006563292274658743", // id
            URI.create(baseUrl + "/party-gce/global/httpHealthChecks/myname-andrea-zk7gadwq-http-health-check"),
            // selfLink
            parse("2014-01-08T14:48:03.276-08:00"), // creationTimestamp
            "myname-andrea-zk7gadwq-http-health-check", // name
            null, // description
            null, // host
            null, // requestPath
            null,  // port
            null,  // checkIntervalSec
            5,  // timeoutSec
            2,  // unhealthyThreshold
            null // healthyThreshold
      );
      return ForwardingListPage.create( //
            ImmutableList.of(healthCheck1, healthCheck2, healthCheck3), // items
            null // nextPageToken
      );
   }
}
