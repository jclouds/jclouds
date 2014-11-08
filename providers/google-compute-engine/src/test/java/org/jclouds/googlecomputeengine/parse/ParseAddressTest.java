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

import org.jclouds.googlecomputeengine.domain.Address;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseAddressTest")
public class ParseAddressTest extends BaseGoogleComputeEngineParseTest<Address> {

   @Override
   public String resource() {
      return "/address_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public Address expected() {
      return Address.create( //
            "4439373783165447583", // id
            URI.create(BASE_URL + "/party/regions/us-central1/addresses/test-ip1"), // selfLink
            "test-ip1", // name
            "", // description
            "RESERVED", // status
            null, // user
            URI.create(BASE_URL + "/party/regions/us-central1"), // region
            "173.255.115.190" // address
      );
   }
}
