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

import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;

import com.google.common.collect.ImmutableSet;

/**
 * @author David Alves
 */
public class ParseInstanceListTest extends BaseGoogleComputeEngineParseTest<ListPage<Instance>> {

   @Override
   public String resource() {
      return "/instance_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Instance> expected() {
      return ListPage.<Instance>builder()
              .kind(Resource.Kind.INSTANCE_LIST)
              .id("projects/myproject/zones/us-central1-a/instances")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta16/projects/myproject/zones/us-central1-a/instances"))
              .items(ImmutableSet.of(new ParseInstanceTest().expected()))
              .build();
   }
}
