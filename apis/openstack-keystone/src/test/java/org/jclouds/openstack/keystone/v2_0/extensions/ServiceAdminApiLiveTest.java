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
package org.jclouds.openstack.keystone.v2_0.extensions;

import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Tests behavior of ServiceAdminApi
 */
@Test(groups = "live", testName = "ServiceAdminApiLiveTest", singleThreaded = true)
public class ServiceAdminApiLiveTest extends BaseKeystoneApiLiveTest {

   private Optional<? extends ServiceAdminApi> serviceAdminOption;

   private Service testService;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setup() {
      super.setup();
      serviceAdminOption = api.getServiceAdminApi();
      if (!serviceAdminOption.isPresent()) {
         throw new SkipException("The tests are skipped since OS-KSADM extension is not exposed through the Keystone API");
      }
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      if (testService != null) {
         final String serviceId = testService.getId();
         boolean success = serviceAdminOption.get().delete(serviceId);
         assertTrue(retry(new Predicate<ServiceAdminApi>() {
            public boolean apply(ServiceAdminApi serviceApi) {
               return serviceApi.get(serviceId) == null;
            }
         }, 5 * 1000L).apply(serviceAdminOption.get()));
      }
      super.tearDown();
   }

   public void testListServices() {
      ServiceAdminApi serviceApi = serviceAdminOption.get();
      Set<? extends Service> services = serviceApi.list().concat().toSet();
      assertNotNull(services);
      assertFalse(services.isEmpty());
      for (Service service : services) {
         Service aService = serviceApi.get(service.getId());
         assertEquals(aService, service);
      }

   }

   @Test
   public void testCreateService() {
      testService = serviceAdminOption.get().create("jclouds-test-service", "jclouds-service-type",
            "jclouds-service-description");
      assertTrue(retry(new Predicate<ServiceAdminApi>() {
         public boolean apply(ServiceAdminApi serviceApi) {
            return serviceApi.get(testService.getId()) != null;
         }
      }, 180 * 1000L).apply(serviceAdminOption.get()));

      assertEquals(serviceAdminOption.get().get(testService.getId()).getName(), "jclouds-test-service");
   }

   @Test(dependsOnMethods = { "testCreateService" })
   public void testGetService() {
      Service testGetService = serviceAdminOption.get().get(testService.getId());
      assertNotNull(testGetService);
      assertEquals(testGetService.getName(), "jclouds-test-service");

   }
}
