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

import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.HttpHealthCheckCreationOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

public class HttpHealthCheckApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String HTTP_HEALTH_CHECK_NAME = "http-health-check-api-live-test";
   private static final int TIME_WAIT = 60;

   private static final int OFFSET = 2;

   private HttpHealthCheckCreationOptions options;

   private HttpHealthCheckApi api() {
      return api.getHttpHealthCheckApi(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertHttpHealthCheck() {
      options = new HttpHealthCheckCreationOptions()
                     .port(56)
                     .checkIntervalSec(40)
                     .timeoutSec(40)
                     .healthyThreshold(30)
                     .unhealthyThreshold(15)
                     .description("A First Health Check!");
      assertGlobalOperationDoneSucessfully(api().insert(HTTP_HEALTH_CHECK_NAME, options), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertHttpHealthCheck")
   public void testGetHttpHealthCheck() {
      HttpHealthCheck httpHealthCheck = api().get(HTTP_HEALTH_CHECK_NAME);
      assertNotNull(httpHealthCheck);
      assertEquals(httpHealthCheck.name(), HTTP_HEALTH_CHECK_NAME);
      assertEquals(httpHealthCheck.port(), options.port().intValue());
      assertEquals(httpHealthCheck.checkIntervalSec(), options.checkIntervalSec().intValue());
      assertEquals(httpHealthCheck.timeoutSec(), options.timeoutSec().intValue());
      assertEquals(httpHealthCheck.healthyThreshold(), options.healthyThreshold().intValue());
      assertEquals(httpHealthCheck.unhealthyThreshold(), options.unhealthyThreshold().intValue());
      assertEquals(httpHealthCheck.description(), options.description());
   }

   @Test(groups = "live", dependsOnMethods = "testInsertHttpHealthCheck")
   public void testListHttpHealthCheck() {
      ListPage<HttpHealthCheck> httpHealthCheck = api().list(filter("name eq " + HTTP_HEALTH_CHECK_NAME)).next();
      assertEquals(Iterables.size(httpHealthCheck), 1);
   }

   @Test(groups = "live", dependsOnMethods = "testGetHttpHealthCheck")
   public void testPatchHttpHealthCheck() {
      HttpHealthCheckCreationOptions newOptions = new HttpHealthCheckCreationOptions()
         .port(options.port() + OFFSET)
         .checkIntervalSec(options.checkIntervalSec() + OFFSET)
         .timeoutSec(options.timeoutSec() + OFFSET);
      assertGlobalOperationDoneSucessfully(api().patch(HTTP_HEALTH_CHECK_NAME, newOptions), TIME_WAIT);

      // Check changes happened and others unchanged.
      HttpHealthCheck httpHealthCheck = api().get(HTTP_HEALTH_CHECK_NAME);
      assertNotNull(httpHealthCheck);
      assertEquals(httpHealthCheck.name(), HTTP_HEALTH_CHECK_NAME);
      assertEquals(httpHealthCheck.port(), newOptions.port().intValue());
      assertEquals(httpHealthCheck.checkIntervalSec(), newOptions.checkIntervalSec().intValue());
      assertEquals(httpHealthCheck.timeoutSec(), newOptions.timeoutSec().intValue());
      assertEquals(httpHealthCheck.healthyThreshold(), options.healthyThreshold().intValue());
      assertEquals(httpHealthCheck.unhealthyThreshold(), options.unhealthyThreshold().intValue());
      assertEquals(httpHealthCheck.description(), options.description());
   }

   @Test(groups = "live", dependsOnMethods = "testPatchHttpHealthCheck")
   public void testUpdateHttpHealthCheck() {
      HttpHealthCheckCreationOptions newOptions = new HttpHealthCheckCreationOptions()
         .checkIntervalSec(options.checkIntervalSec() - OFFSET)
         .timeoutSec(options.timeoutSec() - OFFSET);
      assertGlobalOperationDoneSucessfully(api().update(HTTP_HEALTH_CHECK_NAME, newOptions), TIME_WAIT);

      // Check changes happened.
      HttpHealthCheck httpHealthCheck = api().get(HTTP_HEALTH_CHECK_NAME);
      assertNotNull(httpHealthCheck);
      assertEquals(httpHealthCheck.name(), HTTP_HEALTH_CHECK_NAME);
      assertEquals(httpHealthCheck.checkIntervalSec(), newOptions.checkIntervalSec().intValue());
      assertEquals(httpHealthCheck.timeoutSec(), newOptions.timeoutSec().intValue());
      // Update overwrites unspecified parameters to their defaults.
      assertNotEquals(httpHealthCheck.healthyThreshold(), options.healthyThreshold());
      assertNotEquals(httpHealthCheck.unhealthyThreshold(), options.unhealthyThreshold());
      assertNotEquals(httpHealthCheck.description(), options.description());
   }

   @Test(groups = "live", dependsOnMethods = {"testListHttpHealthCheck", "testUpdateHttpHealthCheck"})
   public void testDeleteHttpHealthCheck() {
      assertGlobalOperationDoneSucessfully(api().delete(HTTP_HEALTH_CHECK_NAME), TIME_WAIT);
   }
}
