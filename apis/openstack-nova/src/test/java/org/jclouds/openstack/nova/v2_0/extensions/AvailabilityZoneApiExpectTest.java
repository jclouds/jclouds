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

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.AvailabilityZone;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.AvailabilityZoneDetails;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.AvailabilityZoneDetails.HostService;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

@Test(groups = "unit", testName = "AvailabilityZoneApiExpectTest")
public class AvailabilityZoneApiExpectTest extends BaseNovaApiExpectTest {

   public void testWhenNamespaceInExtensionsListAvailabilityZonesPresent() throws Exception {

      NovaApi apiWhenExtensionInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse);

      assertEquals(apiWhenExtensionInList.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertTrue(apiWhenExtensionInList.getFloatingIPApi("az-1.region-a.geo-1").isPresent());

   }

   public void testWhenNamespaceNotInExtensionsListAvailabilityZonesPresent() throws Exception {

      NovaApi apiWhenExtensionNotInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, unmatchedExtensionsOfNovaResponse);

      assertEquals(apiWhenExtensionNotInList.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertFalse(apiWhenExtensionNotInList.getFloatingIPApi("az-1.region-a.geo-1").isPresent());

   }

   public void testListAvailabilityZones() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-availability-zone")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/listAvailabilityZones.json")).build();

      NovaApi availabilityZonesApi = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertEquals(availabilityZonesApi.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      FluentIterable<? extends AvailabilityZone> zones = availabilityZonesApi.getAvailabilityZoneApi("az-1.region-a.geo-1").get().listAvailabilityZones();

      Optional<? extends AvailabilityZone> zone = zones.first();

      assertTrue(zone.isPresent(), "Couldn't find zone");
      assertEquals(zone.get().getName(), "internal", "Expected zone name to be internal but it was: " + zone.get().getName());
      assertTrue(zone.get().getState().isAvailable(), "Zone: " + zone.get().getName() + " is not available.");
   }

   public void testListAvailabilityZonesWhenResponseIs404() throws Exception {
      HttpRequest list = HttpRequest.builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-availability-zone")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertTrue(apiWhenNoServersExist.getAvailabilityZoneApi("az-1.region-a.geo-1").get().listAvailabilityZones().isEmpty());
   }

   public void testListInDetail() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-availability-zone/detail")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/listAvailabilityZones.json")).build();

      NovaApi availabilityZonesApi = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertEquals(availabilityZonesApi.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      FluentIterable<? extends AvailabilityZoneDetails> zones = availabilityZonesApi.getAvailabilityZoneApi("az-1.region-a.geo-1").get().listInDetail();

      Optional<? extends AvailabilityZoneDetails> zone = zones.first();

      assertTrue(zone.isPresent(), "Couldn't find zone");
      assertEquals(zone.get()
            .getName(), "internal", "Expected zone name to be internal but it was: " + zone.get()
            .getName());
      assertTrue(zone.get()
            .getState()
            .isAvailable(), "Zone: " + zone.get()
            .getName() + " is not available.");
      String hostName = zone.get().getHosts().keySet().iterator().next();
      assertEquals(hostName, "os-controller", "Expected host name to be os-controller but it was: " + hostName);
      String hostServiceName = zone.get().getHosts().get(hostName).keySet().iterator().next();
      assertEquals(hostServiceName, "nova-conductor",
            "Expected host service name to be nova-conductor but it was: " + hostServiceName);
      HostService hostService = zone.get().getHosts().get(hostName).get(hostServiceName);
      assertTrue(hostService.isAvailable(), "Couldn't find host service availability");
      assertTrue(hostService.isActive(), "Couldn't find host service state");
      assertEquals(hostService.getUpdated(), new Date(1436509815000L),
            "Expected Updated time: " + new Date(1436509815000L) + " does match Updated time : " + hostService.getUpdated());
   }

   public void testListInDetailWhenResponseIs404() throws Exception {
      HttpRequest list = HttpRequest.builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-availability-zone/detail")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertTrue(apiWhenNoServersExist.getAvailabilityZoneApi("az-1.region-a.geo-1").get().listInDetail().isEmpty());
   }
}
