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
import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseHttpHealthCheckListTest extends BaseGoogleComputeEngineParseTest<ListPage<HttpHealthCheck>> {

   @Override
   public String resource() {
      return "/httphealthcheck_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<HttpHealthCheck> expected() {
      return ListPage.<HttpHealthCheck>builder()
              .kind(Resource.Kind.HTTP_HEALTH_CHECK_LIST)
              .items(ImmutableSet.of(HttpHealthCheck.builder()
                              .id("2761502483700014319")
                              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2014-01-14T05:55:54.910-08:00"))
                              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/jclouds-gce/global/httpHealthChecks/http-health-check-api-live-test"))
                              .name("http-health-check-api-live-test")
                              .port(0)
                              .checkIntervalSec(0)
                              .timeoutSec(0)
                              .unhealthyThreshold(0)
                              .healthyThreshold(0)
                              .build(),
                      HttpHealthCheck.builder()
                              .id("1035854271083519643")
                              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2014-01-08T14:38:29.363-08:00"))
                              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/jclouds-gce/global/httpHealthChecks/myname-andrea-kmzmi1bh-http-health-check"))
                              .name("myname-andrea-kmzmi1bh-http-health-check")
                              .port(0)
                              .checkIntervalSec(0)
                              .timeoutSec(5)
                              .unhealthyThreshold(2)
                              .healthyThreshold(0)
                              .build(),
                      HttpHealthCheck.builder()
                              .id("7006563292274658743")
                              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2014-01-08T14:48:03.276-08:00"))
                              .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/jclouds-gce/global/httpHealthChecks/myname-andrea-zk7gadwq-http-health-check"))
                              .name("myname-andrea-zk7gadwq-http-health-check")
                              .port(0)
                              .checkIntervalSec(0)
                              .timeoutSec(5)
                              .unhealthyThreshold(2)
                              .healthyThreshold(0)
                              .build()
              ))
              .build();
   }
}
