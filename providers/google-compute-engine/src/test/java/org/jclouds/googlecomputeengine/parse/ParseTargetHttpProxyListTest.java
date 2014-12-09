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
import org.jclouds.googlecloud.domain.ForwardingListPage;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.TargetHttpProxy;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit")
public class ParseTargetHttpProxyListTest extends BaseGoogleComputeEngineParseTest<ListPage<TargetHttpProxy>> {

   @Override
   public String resource() {
      return "/target_http_proxy_list.json";
   }

   @Override @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<TargetHttpProxy> expected() {
      return expected(BASE_URL);
   }

   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<TargetHttpProxy> expected(String baseUrl) {
      return ForwardingListPage.create(
            ImmutableList.of(
                  new ParseTargetHttpProxyTest().expected(baseUrl),
                  TargetHttpProxy.create("13050421646334304116", // id
                        new SimpleDateFormatDateService().iso8601DateParse("2012-11-25T01:38:48.306"), // creationTimestamp
                        URI.create(baseUrl + "/myproject/global/targetHttpProxies/jclouds-test-2"), // selfLink
                        "jclouds-test-2", // name
                        "Simple proxy", // description
                        URI.create(baseUrl + "/myproject/global/urlMaps/jclouds-test-2"))), // urlMap
                  null // nextPageToken d
            );
   }
}
