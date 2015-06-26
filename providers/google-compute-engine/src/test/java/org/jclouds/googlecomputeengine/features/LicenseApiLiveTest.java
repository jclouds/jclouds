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
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.googlecomputeengine.domain.License;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.testng.annotations.Test;

public class LicenseApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   public static final String PROJECT = "suse-cloud";
   public static final String LICENSE = "sles-12";


   private LicenseApi api() {
      return api.licensesInProject(PROJECT);
   }

   @Test(groups = "live")
   public void testGetLicense() {
      License license = api().get(LICENSE);
      assertNotNull(license);
      assertEquals(license.name(), LICENSE);
      URI selfLink = URI.create("https://www.googleapis.com/compute/v1/projects/" +
            PROJECT + "/global/licenses/" + LICENSE);
      assertEquals(license.selfLink(), selfLink);
   }
}
