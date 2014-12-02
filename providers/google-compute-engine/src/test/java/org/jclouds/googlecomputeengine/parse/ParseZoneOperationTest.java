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

import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseZoneOperationTest")
public class ParseZoneOperationTest extends BaseGoogleComputeEngineParseTest<Operation> {

   @Override
   public String resource() {
      return "/zone_operation.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public Operation expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public Operation expected(String baseUrl) {
      return Operation.create( //
            "13053095055850848306", // id
            parse("2013-07-26T13:57:20.204-07:00"), // creationTimestamp
            URI.create(baseUrl + "/party/zones/us-central1-a/operations/operation-1354084865060"),
            "operation-1354084865060", // name
            null, // description
            URI.create(baseUrl + "/party/zones/us-central1-a/instances/test-1"),
            "13053094017547040099", // targetId
            null, // clientOperationId
            Operation.Status.DONE, // status
            null, // statusMessage
            "user@developer.gserviceaccount.com", // user
            100, // progress
            parse("2012-11-28T06:41:05.060"), // insertTime
            parse("2012-11-28T06:41:05.142"), // startTime
            parse("2012-11-28T06:41:06.142"), // endTime
            null, // httpErrorStatusCode
            null, // httpErrorMessage
            "insert", // operationType
            null, // errors
            null, // warnings
            null, // region
            URI.create(baseUrl + "/party/zones/us-central1-a") // zone
      );
   }
}
