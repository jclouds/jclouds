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

import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class ParseResourceViewResourceListTest extends BaseGoogleComputeEngineParseTest<ListPage<URI>> {

   @Override
   public String resource() {
      return "/resource_view_resources_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<URI> expected() {
      String base = "https://googleapis.com/compute/projects/myproject/zones/us-central1-a/instances/";
      return ListPage.<URI>builder()
              .kind(Resource.Kind.RESOURCE_VIEW_MEMBER_LIST)
              .id("")
              .selfLink(URI.create(""))
              .items(ImmutableSet.<URI>of(URI.create(base + "jclouds-test-1"),
                                          URI.create(base + "jclouds-test-2"))
              ).build();
   }
}
