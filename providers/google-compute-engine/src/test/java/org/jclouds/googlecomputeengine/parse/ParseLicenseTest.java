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

import org.jclouds.googlecomputeengine.domain.License;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseLicenseTest")
public class ParseLicenseTest extends BaseGoogleComputeEngineParseTest<License> {

   @Override
   public String resource() {
      return "/license_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public License expected() {
      return expected(BASE_URL);
   }

   @Consumes(APPLICATION_JSON)
   public License expected(String baseUrl) {
      return License.create(URI.create(baseUrl + "/suse-cloud/global/licenses/sles-12"), "sles-12", true);
   }
}
