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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.HashSet;
import java.util.List;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.BackendService;
import org.jclouds.googlecomputeengine.domain.BackendService.Backend;
import org.jclouds.googlecomputeengine.domain.BackendServiceGroupHealth;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class BackendServiceApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String BACKEND_SERVICE_NAME = "backend-service-api-live-test-backend-service";
   private static final String BACKEND_SERVICE_HEALTH_CHECK_NAME = "backend-service-api-live-test-health-check";
   private static final String BACKEND_SERVICE_RESOURCE_VIEW_NAME = "backend-service-api-live-test-resource-view";
   private static final int TIME_WAIT = 30;

   private BackendServiceApi api() {
      return api.getBackendServiceApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertBackendService() {
      // TODO: (ashmrtnz) create health check here once it is merged into this project
      HashSet<URI> healthChecks = new HashSet<URI>();
      healthChecks.add(getHealthCheckUrl(userProject.get(), BACKEND_SERVICE_HEALTH_CHECK_NAME));
      BackendServiceOptions b = new BackendServiceOptions().name(BACKEND_SERVICE_NAME).healthChecks(healthChecks);
      assertGlobalOperationDoneSucessfully(api().create(BACKEND_SERVICE_NAME, b), TIME_WAIT);
   }
   
   @Test(groups = "live", dependsOnMethods = "testInsertBackendService")
   public void testGetBackendService() {
      BackendService service = api().get(BACKEND_SERVICE_NAME);
      assertNotNull(service);
      assertBackendServiceEquals(service);
   }
   
   @Test(groups = "live", dependsOnMethods = "testGetBackendService")
   public void testPatchBackendService() {
      String fingerprint = api().get(BACKEND_SERVICE_NAME).getFingerprint().get();
      BackendServiceOptions backendService = new BackendServiceOptions()
              .name(BACKEND_SERVICE_NAME)
              .healthChecks(ImmutableSet.of(getHealthCheckUrl(userProject.get(), BACKEND_SERVICE_HEALTH_CHECK_NAME)))
              .timeoutSec(10)
              .fingerprint(fingerprint);

      assertGlobalOperationDoneSucessfully(api().update(BACKEND_SERVICE_NAME, backendService), TIME_WAIT);
      assertBackendServiceEquals(api().get(BACKEND_SERVICE_NAME), backendService);
   }
   
   @Test(groups = "live", dependsOnMethods = "testPatchBackendService")
   public void testUpdateBackendService() {
      api.getResourceViewApiForProject(userProject.get()).createInZone(DEFAULT_ZONE_NAME,
                                                                       BACKEND_SERVICE_RESOURCE_VIEW_NAME);
      String fingerprint = api().get(BACKEND_SERVICE_NAME).getFingerprint().get();
      Backend backend = Backend.builder()
                               .group(getResourceViewInZoneUrl(userProject.get(),
                                                               BACKEND_SERVICE_RESOURCE_VIEW_NAME))
                               .build();
      BackendServiceOptions backendService = new BackendServiceOptions()
              .name(BACKEND_SERVICE_NAME)
              .healthChecks(ImmutableSet.of(getHealthCheckUrl(userProject.get(),
                                                              BACKEND_SERVICE_HEALTH_CHECK_NAME)))
              .timeoutSec(45)
              .port(8080)
              .addBackend(backend)
              .fingerprint(fingerprint);

      assertGlobalOperationDoneSucessfully(api().update(BACKEND_SERVICE_NAME,
                                                        backendService),
                                           TIME_WAIT);
      assertBackendServiceEquals(api().get(BACKEND_SERVICE_NAME),
                                 backendService);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateBackendService")
   public void testListBackendService() {
      PagedIterable<BackendService> backendServices = api().list(new ListOptions.Builder()
              .filter("name eq " + BACKEND_SERVICE_NAME));

      List<BackendService> backendServicesAsList = Lists.newArrayList(backendServices.concat());

      assertEquals(backendServicesAsList.size(), 1);

   }
   
   @Test(groups = "live", dependsOnMethods = "testListBackendService")
   public void testGetHealthBackendService() {
      // Check to see that the health check returned is empty because it can
      // take several minutes to create all the resources needed and wait for
      // the health check to return a health status.
      assertGroupHealthEquals(api().getHealth(BACKEND_SERVICE_NAME,
                                              getResourceViewInZoneUrl(userProject.get(),
                                                                       BACKEND_SERVICE_RESOURCE_VIEW_NAME)));
   }

   @Test(groups = "live", dependsOnMethods = "testGetHealthBackendService")
   public void testDeleteBackendService() {
      assertGlobalOperationDoneSucessfully(api().delete(BACKEND_SERVICE_NAME), TIME_WAIT);
      api.getResourceViewApiForProject(userProject.get()).deleteInZone(DEFAULT_ZONE_NAME,
                                                                       BACKEND_SERVICE_RESOURCE_VIEW_NAME);
   }

   private void assertBackendServiceEquals(BackendService result) {
      assertEquals(result.getName(), BACKEND_SERVICE_NAME);
      assertEquals(getOnlyElement(result.getHealthChecks()),
                   getHealthCheckUrl(userProject.get(), BACKEND_SERVICE_HEALTH_CHECK_NAME));
   }
   
   private void assertBackendServiceEquals(BackendService result, BackendServiceOptions expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getHealthChecks(), expected.getHealthChecks());
      if (expected.getTimeoutSec() != null) {
         assertEquals(result.getTimeoutSec().get(), expected.getTimeoutSec());
      }
      if (expected.getPort() != null) {
         assertEquals(result.getPort().get(), expected.getPort());
      }
   }
   
   private void assertGroupHealthEquals(BackendServiceGroupHealth result) {
      assert result.getHealthStatuses().size() == 0;
   }
}
