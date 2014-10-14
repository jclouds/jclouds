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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

@Test(groups = "unit", testName = "ExtendedAvailabilityZoneExpectTest")
public class ExtendedAvailabilityZoneExpectTest extends BaseNovaApiExpectTest{

   public void testAvailabilityZoneInServerDetails() throws Exception {
      String serverId = "71752";

      HttpRequest serverDetail = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId)
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse serverDetailResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/server_details.json")).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, serverDetail, serverDetailResponse);

      Server server = apiWhenServerExists.getServerApi("az-1.region-a.geo-1").get(serverId);
      assertNotNull(server.getAvailabilityZone());
   }
}
