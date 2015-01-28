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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecomputeengine.domain.BackendService;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class BackendServiceApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String BACKEND_SERVICE_NAME = "backend-service-api-live-test-backend-service";
   private static final String BACKEND_SERVICE_HEALTH_CHECK_NAME = "backend-service-api-live-test-health-check";

   private BackendServiceApi api() {
      return api.backendServices();
   }

   @Test(groups = "live")
   public void testInsertBackendService() {
      assertOperationDoneSuccessfully(api.httpHeathChecks().insert(BACKEND_SERVICE_HEALTH_CHECK_NAME));

      List<URI> healthChecks = ImmutableList.of(getHealthCheckUrl(BACKEND_SERVICE_HEALTH_CHECK_NAME));

      BackendServiceOptions b = new BackendServiceOptions.Builder(BACKEND_SERVICE_NAME, healthChecks).build();
      assertOperationDoneSuccessfully(api().create(b));
   }

   @Test(groups = "live", dependsOnMethods = "testInsertBackendService")
   public void testGetBackendService() {
      BackendService service = api().get(BACKEND_SERVICE_NAME);
      assertNotNull(service);
      assertBackendServiceEquals(service);
   }

   @Test(groups = "live", dependsOnMethods = "testGetBackendService")
   public void testPatchBackendService() {
      String fingerprint = api().get(BACKEND_SERVICE_NAME).fingerprint();
      BackendServiceOptions backendService = new BackendServiceOptions.Builder(BACKEND_SERVICE_NAME, ImmutableList.of(getHealthCheckUrl(BACKEND_SERVICE_HEALTH_CHECK_NAME)))
              .timeoutSec(10)
              .fingerprint(fingerprint)
              .build();

      assertOperationDoneSuccessfully(api().update(BACKEND_SERVICE_NAME, backendService));
      assertBackendServiceEquals(api().get(BACKEND_SERVICE_NAME), backendService);
   }

   @Test(groups = "live", dependsOnMethods = "testPatchBackendService")
   public void testUpdateBackendService() {
      String fingerprint = api().get(BACKEND_SERVICE_NAME).fingerprint();

      BackendServiceOptions backendService = new BackendServiceOptions.Builder(BACKEND_SERVICE_NAME, ImmutableList.of(getHealthCheckUrl(BACKEND_SERVICE_HEALTH_CHECK_NAME)))
              .timeoutSec(45)
              .port(8080)
              .fingerprint(fingerprint)
              .build();

      assertOperationDoneSuccessfully(api().update(BACKEND_SERVICE_NAME, backendService));
      assertBackendServiceEquals(api().get(BACKEND_SERVICE_NAME),
                                 backendService);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateBackendService")
   public void testListBackendService() {
      Iterator<ListPage<BackendService>> backendServices = api().list(filter("name eq " + BACKEND_SERVICE_NAME));

      List<BackendService> backendServicesAsList = backendServices.next();

      assertEquals(backendServicesAsList.size(), 1);

   }

   /*
   @Test(groups = "live", dependsOnMethods = "testListBackendService")
   public void testGetHealthBackendService() {
      // TODO: Once resourceViews are merged into the project. Test this actually works.
   }
    */

   @Test(groups = "live", dependsOnMethods = "testListBackendService")
   public void testDeleteBackendService() {
      assertOperationDoneSuccessfully(api().delete(BACKEND_SERVICE_NAME));

      assertOperationDoneSuccessfully(api.httpHeathChecks().delete(BACKEND_SERVICE_HEALTH_CHECK_NAME));

   }

   private void assertBackendServiceEquals(BackendService result) {
      assertEquals(result.name(), BACKEND_SERVICE_NAME);
      assertEquals(getOnlyElement(result.healthChecks()),
                   getHealthCheckUrl(BACKEND_SERVICE_HEALTH_CHECK_NAME));
   }

   private void assertBackendServiceEquals(BackendService result, BackendServiceOptions expected) {
      assertEquals(result.name(), expected.name());
      assertEquals(result.healthChecks(), expected.healthChecks());
      if (expected.timeoutSec() != null) {
         org.testng.Assert.assertEquals(result.timeoutSec(), expected.timeoutSec().intValue());
      }
      if (expected.port() != null) {
         org.testng.Assert.assertEquals(result.port(), expected.port().intValue());
      }
   }

}
