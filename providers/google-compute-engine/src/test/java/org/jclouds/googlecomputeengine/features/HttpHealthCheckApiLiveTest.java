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

import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

public class HttpHealthCheckApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String HTTP_HEALTH_CHECK_NAME = "http-health-check-api-live-test";
   private static final Integer OFFSET = 2;

   private HttpHealthCheckCreationOptions options;

   private HttpHealthCheckApi api() {
      return api.httpHeathChecks();
   }

   @Test(groups = "live")
   public void testInsertHttpHealthCheck() {
      options = new HttpHealthCheckCreationOptions.Builder()
                     .port(56)
                     .checkIntervalSec(40)
                     .timeoutSec(40)
                     .healthyThreshold(5)
                     .unhealthyThreshold(3)
                     .description("A First Health Check!")
                     .buildWithDefaults();
      assertOperationDoneSuccessfully(api().insert(HTTP_HEALTH_CHECK_NAME, options));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertHttpHealthCheck")
   public void testGetHttpHealthCheck() {
      HttpHealthCheck httpHealthCheck = api().get(HTTP_HEALTH_CHECK_NAME);
      assertNotNull(httpHealthCheck);
      assertEquals(httpHealthCheck.name(), HTTP_HEALTH_CHECK_NAME);
      assertEquals(httpHealthCheck.port(), options.port());
      assertEquals(httpHealthCheck.checkIntervalSec(), options.checkIntervalSec());
      assertEquals(httpHealthCheck.timeoutSec(), options.timeoutSec());
      assertEquals(httpHealthCheck.healthyThreshold(), options.healthyThreshold());
      assertEquals(httpHealthCheck.unhealthyThreshold(), options.unhealthyThreshold());
      assertEquals(httpHealthCheck.description(), options.description());
   }

   @Test(groups = "live", dependsOnMethods = "testInsertHttpHealthCheck")
   public void testListHttpHealthCheck() {
      ListPage<HttpHealthCheck> httpHealthCheck = api().list(filter("name eq " + HTTP_HEALTH_CHECK_NAME)).next();
      assertEquals(Iterables.size(httpHealthCheck), 1);
   }

   @Test(groups = "live", dependsOnMethods = "testGetHttpHealthCheck")
   public void testPatchHttpHealthCheck() {
      HttpHealthCheckCreationOptions newOptions = new HttpHealthCheckCreationOptions.Builder()
         .port(options.port() + OFFSET)
         .checkIntervalSec(options.checkIntervalSec() + OFFSET)
         .timeoutSec(options.timeoutSec() + OFFSET)
         .buildForPatch();
      assertOperationDoneSuccessfully(api().patch(HTTP_HEALTH_CHECK_NAME, newOptions));

      // Check changes happened and others unchanged.
      HttpHealthCheck httpHealthCheck = api().get(HTTP_HEALTH_CHECK_NAME);
      assertNotNull(httpHealthCheck);
      assertEquals(httpHealthCheck.name(), HTTP_HEALTH_CHECK_NAME);
      assertEquals(httpHealthCheck.port(), newOptions.port());
      assertEquals(httpHealthCheck.checkIntervalSec(), newOptions.checkIntervalSec());
      assertEquals(httpHealthCheck.timeoutSec(), newOptions.timeoutSec());
      assertEquals(httpHealthCheck.healthyThreshold(), options.healthyThreshold());
      assertEquals(httpHealthCheck.unhealthyThreshold(), options.unhealthyThreshold());
      assertEquals(httpHealthCheck.description(), options.description());
   }

   @Test(groups = "live", dependsOnMethods = "testPatchHttpHealthCheck")
   public void testUpdateHttpHealthCheck() {
      HttpHealthCheckCreationOptions newOptions = new HttpHealthCheckCreationOptions.Builder()
         .checkIntervalSec(options.checkIntervalSec() - OFFSET)
         .timeoutSec(options.timeoutSec() - OFFSET)
         .buildWithDefaults();
      assertOperationDoneSuccessfully(api().update(HTTP_HEALTH_CHECK_NAME, newOptions));

      // Check changes happened.
      HttpHealthCheck httpHealthCheck = api().get(HTTP_HEALTH_CHECK_NAME);
      assertNotNull(httpHealthCheck);
      assertEquals(httpHealthCheck.name(), HTTP_HEALTH_CHECK_NAME);
      assertEquals(httpHealthCheck.checkIntervalSec(), newOptions.checkIntervalSec());
      assertEquals(httpHealthCheck.timeoutSec(), newOptions.timeoutSec());
      // Update overwrites unspecified parameters to their defaults.
      assertNotEquals(httpHealthCheck.healthyThreshold(), options.healthyThreshold());
      assertNotEquals(httpHealthCheck.unhealthyThreshold(), options.unhealthyThreshold());
      assertNotEquals(httpHealthCheck.description(), options.description());
   }

   @Test(groups = "live", dependsOnMethods = {"testListHttpHealthCheck", "testUpdateHttpHealthCheck"}, alwaysRun = true)
   public void testDeleteHttpHealthCheck() {
      assertOperationDoneSuccessfully(api().delete(HTTP_HEALTH_CHECK_NAME));
   }
}
