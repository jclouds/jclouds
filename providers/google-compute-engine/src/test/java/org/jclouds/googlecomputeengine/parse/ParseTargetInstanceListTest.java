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
import org.jclouds.googlecomputeengine.domain.TargetInstance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseTargetInstanceListTest")
public class ParseTargetInstanceListTest extends BaseGoogleComputeEngineParseTest<ListPage<TargetInstance>> {

   @Override
   public String resource() {
      return "/target_instance_list.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public ListPage<TargetInstance> expected() {
      return expected(BASE_URL);
   }

   public ListPage<TargetInstance> expected(String baseURL){
      return ForwardingListPage.create( //
            ImmutableList.of(new ParseTargetInstanceTest().expected(baseURL),
                  TargetInstance.create(
                        "7362436693678237415", // id
                        "2014-11-20T17:35:17.268-08:00", // creationTimestamp
                        "target-instance-2", // name
                        null, // description
                        URI.create(baseURL + "/party/zones/us-central1-a"), // zone
                        "NO_NAT", // natPolicy
                        URI.create(baseURL + "/party/zones/us-central1-a/instances/test-3"), // instance
                        URI.create(baseURL + "/party/zones/us-central1-a/targetInstances/target-instance-2") // selfLink
                        )), // items
            null // nextPageToken
      );
   }
}
