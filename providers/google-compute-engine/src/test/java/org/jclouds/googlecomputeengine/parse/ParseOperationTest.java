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
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ParseOperationTest extends BaseGoogleComputeEngineParseTest<Operation> {

   @Override
   public String resource() {
      return "/global_operation.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Operation expected() {
      SimpleDateFormatDateService dateService = new SimpleDateFormatDateService();
      return Operation.builder().id("13053095055850848306")
              .selfLink(URI.create("https://www.googleapis" +
                      ".com/compute/v1beta16/projects/myproject/global/operations/operation-1354084865060-4cf88735faeb8" +
                      "-bbbb12cb"))
              .name("operation-1354084865060-4cf88735faeb8-bbbb12cb")
              .targetLink(URI.create("https://www.googleapis" +
                      ".com/compute/v1beta16/projects/myproject/global/firewalls/jclouds-test-delete"))
              .targetId("13053094017547040099")
              .status(Operation.Status.DONE)
              .user("user@developer.gserviceaccount.com")
              .progress(100)
              .insertTime(dateService.iso8601DateParse("2012-11-28T06:41:05.060"))
              .startTime(dateService.iso8601DateParse("2012-11-28T06:41:05.142"))
              .endTime(dateService.iso8601DateParse("2012-11-28T06:41:06.142"))
              .operationType("insert")
              .build();
   }
}
