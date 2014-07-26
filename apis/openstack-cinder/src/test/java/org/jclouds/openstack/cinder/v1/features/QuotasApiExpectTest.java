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

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.cinder.v1.domain.VolumeQuota;
import org.jclouds.openstack.cinder.v1.internal.BaseCinderApiExpectTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "QuotaApiExpectTest")
public class QuotasApiExpectTest extends BaseCinderApiExpectTest {

   public void testGetQuotas() throws Exception {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/os-quota-sets/demo");
      QuotaApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/quotas.json")).build()
      ).getQuotaApi("RegionOne");

      assertEquals(api.getByTenant("demo"), getTestQuotas());
   }

   public static VolumeQuota getTestQuotas() {
      return VolumeQuota.builder()
            .gigabytes(1000)
            .volumes(10)
            .snapshots(20)
            .id("demo").build();
   }
}
