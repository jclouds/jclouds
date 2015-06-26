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
package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.jclouds.googlecomputeengine.parse.ParseLicenseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "LicenseApiMockTest", singleThreaded = true)
public class LicenseApiMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void get() throws Exception {
      server.enqueue(jsonResponse("/license_get.json"));

      assertEquals(licenseApi().get("sles-12"), new ParseLicenseTest().expected(url("/projects")));
      assertSent(server, "GET", "/projects/suse-cloud/global/licenses/sles-12");
   }

   public void get_4xx() throws Exception {
      server.enqueue(response404());

      assertNull(licenseApi().get("sles-12"));
      assertSent(server, "GET", "/projects/suse-cloud/global/licenses/sles-12");
   }

   public LicenseApi licenseApi(){
      return api().licensesInProject("suse-cloud");
   }
}
