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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFloatingIPPoolListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code FloatingIPPoolApi}
 */
@Test(groups = "unit", testName = "FloatingIPPoolApiExpectTest")
public class FloatingIPPoolApiExpectTest extends BaseNovaApiExpectTest {
   public void testWhenNamespaceInExtensionsListFloatingIpPoolPresent() throws Exception {

      NovaApi apiWhenExtensionNotInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse);

      assertEquals(apiWhenExtensionNotInList.getConfiguredRegions(),
            ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertTrue(apiWhenExtensionNotInList.getFloatingIPPoolApi("az-1.region-a.geo-1").isPresent());

   }

   public void testWhenNamespaceNotInExtensionsListFloatingIpPoolNotPresent() throws Exception {

      NovaApi apiWhenExtensionNotInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, unmatchedExtensionsOfNovaResponse);

      assertEquals(apiWhenExtensionNotInList.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertFalse(apiWhenExtensionNotInList.getFloatingIPPoolApi("az-1.region-a.geo-1").isPresent());

   }

   public void testListFloatingIPPoolWhenResponseIs2xx() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-floating-ip-pools")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingippool_list.json")).build();

      NovaApi apiWhenFloatingIPPoolExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertEquals(apiWhenFloatingIPPoolExists.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertEquals(apiWhenFloatingIPPoolExists.getFloatingIPPoolApi("az-1.region-a.geo-1").get().list()
            .toString(), new ParseFloatingIPPoolListTest().expected().toString());
   }

   public void testListFloatingIPsWhenResponseIs404() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-floating-ip-pools")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertTrue(apiWhenNoServersExist.getFloatingIPPoolApi("az-1.region-a.geo-1").get().list().isEmpty());
   }
}
