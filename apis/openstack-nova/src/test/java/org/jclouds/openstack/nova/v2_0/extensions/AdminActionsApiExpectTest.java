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

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.domain.BackupType;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests parsing and guice wiring of AdminActionsApi
 */
@Test(groups = "unit", testName = "AdminActionsApiExpectTest")
public class AdminActionsApiExpectTest extends BaseNovaApiExpectTest {

   public void testSuspend() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "suspend").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.suspend("1");
   }

   public void testResume() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "resume").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.resume("1");
   }

   public void testLock() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "lock").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.lock("1");
   }

   public void testUnlock() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "unlock").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.unlock("1");
   }

   public void testPause() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "pause").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.pause("1");
   }

   public void testUnpause() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "unpause").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.unpause("1");
   }

   public void testMigrateServer() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "migrate").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.migrate("1");
   }

   public void testResetNetworkOfServer() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "resetNetwork").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.resetNetwork("1");
   }

   public void testInjectNetworkInfoIntoServer() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "injectNetworkInfo").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.injectNetworkInfo("1");
   }

   public void testBackupServer() {
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action").method("POST")
                  .payload(payloadFromStringWithContentType("{\"createBackup\":{\"backup_type\":\"weekly\",\"rotation\":3,\"name\":\"mybackup\",\"metadata\":{\"some\":\"data or other\"}}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(202).addHeader("Location", "http://172.16.89.149:8774/v2/images/1976b3b3-409a-468d-b16c-a9172c341b46").build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      String imageId = api.createBackup("1", "mybackup", BackupType.WEEKLY, 3, CreateBackupOfServerOptions.Builder.metadata(ImmutableMap.of("some", "data or other")));
      assertEquals(imageId, "1976b3b3-409a-468d-b16c-a9172c341b46");
   }

   public void testLiveMigrateServer() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/1/action");
      ServerAdminApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardActionRequestBuilderVoidResponse(endpoint, "GONNAOVERWRITE")
                  .payload(payloadFromStringWithContentType("{\"os-migrateLive\":{\"host\":\"bighost\",\"block_migration\":true,\"disk_over_commit\":false}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(202).build()
      ).getServerAdminApi("az-1.region-a.geo-1").get();

      api.liveMigrate("1", "bighost", true, false);
   }

   protected HttpRequest.Builder<?> standardActionRequestBuilderVoidResponse(URI endpoint, String actionName) {
      return HttpRequest.builder()
                        .method("POST")
                        .addHeader("X-Auth-Token", authToken)
                        .addHeader("Accept", "application/json")
                        .payload(payloadFromStringWithContentType("{\"" + actionName + "\":null}", MediaType.APPLICATION_JSON))
                        .endpoint(endpoint);
   }

}
