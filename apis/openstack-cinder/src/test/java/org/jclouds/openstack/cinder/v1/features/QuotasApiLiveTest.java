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
package org.jclouds.openstack.cinder.v1.features;

import static org.testng.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import org.jclouds.openstack.cinder.v1.domain.VolumeQuota;
import org.jclouds.openstack.cinder.v1.internal.BaseCinderApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "QuotasApiLiveTest", singleThreaded = true)
public class QuotasApiLiveTest extends BaseCinderApiLiveTest {

   private QuotaApi quotaApi;

   public QuotasApiLiveTest() {
      super();
      provider = "openstack-cinder";
   }

   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      super.setup();
      String region = Iterables.getFirst(api.getConfiguredRegions(), "nova");
      quotaApi = api.getQuotaApi(region);
   }

   public void testGetStorageQuotas() throws ExecutionException, InterruptedException {
      VolumeQuota volumeQuota = quotaApi.getByTenant("demo");

      assertTrue(volumeQuota.getGigabytes() >= 0);
      assertTrue(volumeQuota.getVolumes() >= 0);
      assertTrue(volumeQuota.getSnapshots() >= 0);
   }
}
