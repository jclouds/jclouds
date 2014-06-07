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
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.features.TenantApi;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneApiLiveTest;
import org.jclouds.openstack.keystone.v2_0.options.CreateTenantOptions;
import org.jclouds.openstack.keystone.v2_0.options.UpdateTenantOptions;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Tests behavior of TenantAdminApi
 */
@Test(groups = "live", testName = "TenantAdminApiLiveTest", singleThreaded = true)
public class TenantAdminApiLiveTest extends BaseKeystoneApiLiveTest {

   private Optional<? extends TenantAdminApi> tenantAdminOption;
   private Optional<? extends TenantApi> tenantApi;

   private Tenant testTenant;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setup() {
      super.setup();
      tenantAdminOption = api.getTenantAdminApi();
      if (!tenantAdminOption.isPresent()) {
         throw new SkipException(
               "The tests are skipped since OS-KSADM extension is not exposed through the Keystone API");
      }
      tenantApi = api.getTenantApi();
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      if (testTenant != null) {
         final String tenantId = testTenant.getId();
         boolean success = tenantAdminOption.get().delete(tenantId);
         assertTrue(retry(new Predicate<TenantApi>() {
            public boolean apply(TenantApi tenantApi) {
               return tenantApi.get(tenantId) == null;
            }
         }, 5 * 1000L).apply(tenantApi.get()));
      }
      super.tearDown();
   }

   public void testCreateTenant() {
      testTenant = tenantAdminOption.get().create("jclouds-test-tenant",
            CreateTenantOptions.Builder.enabled(true).description("jclouds-test-description"));
      assertTrue(retry(new Predicate<TenantApi>() {
         public boolean apply(TenantApi tenantApi) {
            return tenantApi.get(testTenant.getId()) != null;
         }
      }, 180 * 1000L).apply(tenantApi.get()));

      assertEquals(tenantApi.get().get(testTenant.getId()).getName(), "jclouds-test-tenant");
      assertEquals(tenantApi.get().get(testTenant.getId()).getDescription(), "jclouds-test-description");
      assertEquals(tenantApi.get().get(testTenant.getId()).isEnabled(), true);
   }

   public void testUpdateTenant() {
      testTenant = tenantAdminOption.get().update(
            testTenant.getId(),
            UpdateTenantOptions.Builder.description("jclouds-test-description-modified").enabled(false)
                  .name("jclouds-test-tenant-modified"));

      assertEquals(tenantApi.get().get(testTenant.getId()).getName(), "jclouds-test-tenant-modified");
      assertEquals(tenantApi.get().get(testTenant.getId()).getDescription(), "jclouds-test-description-modified");
      assertEquals(tenantApi.get().get(testTenant.getId()).isEnabled(), false);

   }
}
