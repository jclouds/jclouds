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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class HttpHealthCheckApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String HTTP_HEALTH_CHECK_NAME = "http-health-check-api-live-test";
   private static final int TIME_WAIT = 60;

   private HttpHealthCheckApi api() {
      return api.getHttpHealthCheckApi(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertHttpHealthCheck() {
      assertGlobalOperationDoneSucessfully(api().insert(HTTP_HEALTH_CHECK_NAME), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertHttpHealthCheck")
   public void testGetHttpHealthCheck() {
      HttpHealthCheck httpHealthCheck = api().get(HTTP_HEALTH_CHECK_NAME);
      assertNotNull(httpHealthCheck);
      assertEquals(httpHealthCheck.getName(), HTTP_HEALTH_CHECK_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertHttpHealthCheck")
   public void testListHttpHealthCheck() {
      IterableWithMarker<HttpHealthCheck> httpHealthCheck = api().list(new ListOptions.Builder()
              .filter("name eq " + HTTP_HEALTH_CHECK_NAME));
      assertEquals(httpHealthCheck.toList().size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = {"testListHttpHealthCheck", "testGetHttpHealthCheck"})
   public void testDeleteHttpHealthCheck() {
      assertGlobalOperationDoneSucessfully(api().delete(HTTP_HEALTH_CHECK_NAME), TIME_WAIT);
   }
}
