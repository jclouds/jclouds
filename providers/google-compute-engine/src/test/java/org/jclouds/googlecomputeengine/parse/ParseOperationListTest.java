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
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;

/**
 * @author David Alves
 */
public class ParseOperationListTest extends BaseGoogleComputeEngineParseTest<ListPage<Operation>> {

   @Override
   public String resource() {
      return "/global_operation_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Operation> expected() {
      return ListPage.<Operation>builder()
              .kind(Resource.Kind.OPERATION_LIST)
              .id("projects/myproject/global/operations")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta16/projects/myproject/global/operations"))
              .addItem(new ParseOperationTest().expected())
              .build();
   }
}
