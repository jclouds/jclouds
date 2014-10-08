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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.BlockDeviceMapping;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.options.RebuildServerOptions;
import org.jclouds.openstack.nova.v2_0.parse.ParseCreatedServerTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseMetadataListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseMetadataUpdateTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseServerDetailsStatesTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseServerDiagnostics;
import org.jclouds.openstack.nova.v2_0.parse.ParseServerListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code ServerApi}
 */
@Test(groups = "unit", testName = "ServerApiExpectTest")
public class ServerApiExpectTest extends BaseNovaApiExpectTest {

   public void testListServersWhenResponseIs2xx() throws Exception {
      HttpRequest listServers = HttpRequest.builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/server_list.json")).build();

      NovaApi apiWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertEquals(apiWhenServersExist.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertEquals(apiWhenServersExist.getServerApi("az-1.region-a.geo-1").list().concat().toString(),
            new ParseServerListTest().expected().toString());
   }

   public void testListServersWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listServers = HttpRequest.builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertTrue(apiWhenNoServersExist.getServerApi("az-1.region-a.geo-1").list().concat().isEmpty());
   }

   public void testListInDetailServersWhenResponseIs2xx() throws Exception {
      HttpRequest listServers = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/detail")
              .addHeader("Accept", "application/json")
              .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listInDetailServersResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/server_list_details_states.json")).build();

      NovaApi apiWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
              responseWithKeystoneAccess, listServers, listInDetailServersResponse);

      assertEquals(apiWhenServersExist.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertEquals(apiWhenServersExist.getServerApi("az-1.region-a.geo-1").listInDetail().concat().toString(),
              new ParseServerDetailsStatesTest().expected().toString());
   }

   public void testListInDetailServersWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listServers = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/detail")
              .addHeader("Accept", "application/json")
              .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listInDetailServersResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
              responseWithKeystoneAccess, listServers, listInDetailServersResponse);

      assertTrue(apiWhenNoServersExist.getServerApi("az-1.region-a.geo-1").listInDetail().concat().isEmpty());
   }

   public void testCreateServerWhenResponseIs202() throws Exception {
      HttpRequest createServer = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\"}}", "application/json"))
            .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();

      NovaApi apiWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      assertEquals(apiWithNewServer.getServerApi("az-1.region-a.geo-1").create("test-e92", "1241", "100").toString(),
              new ParseCreatedServerTest().expected().toString());
   }

   public void testCreateServerInAvailabilityZoneWhenResponseIs202() throws Exception {
      HttpRequest createServer = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"availability_zone\":\"nova\"}}", "application/json"))
            .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server_in_zone.json", "application/json; charset=UTF-8")).build();

      NovaApi apiWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      CreateServerOptions options = new CreateServerOptions().availabilityZone("nova");

      assertEquals(apiWithNewServer.getServerApi("az-1.region-a.geo-1").create("test-e92", "1241", "100", options).toString(),
            new ParseCreatedServerTest().expected().toString());
   }

   public void testCreateServerWithSecurityGroupsWhenResponseIs202() throws Exception {
      HttpRequest createServer = HttpRequest.builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
               "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"security_groups\":[{\"name\":\"group1\"},{\"name\":\"group2\"}]}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();

      NovaApi apiWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      assertEquals(apiWithNewServer.getServerApi("az-1.region-a.geo-1").create("test-e92", "1241",
               "100", new CreateServerOptions().securityGroupNames("group1", "group2")).toString(),
              new ParseCreatedServerTest().expected().toString());
   }

   public void testCreateServerWithNetworksWhenResponseIs202() throws Exception {
      HttpRequest createServer = HttpRequest.builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
               "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"networks\":[{\"uuid\":\"b3856ac0-f481-11e2-b778-0800200c9a66\"},{\"uuid\":\"bf0f0f90-f481-11e2-b778-0800200c9a66\"}]}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();

      NovaApi apiWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      assertEquals(apiWithNewServer.getServerApi("az-1.region-a.geo-1").create("test-e92", "1241",
               "100", new CreateServerOptions().networks("b3856ac0-f481-11e2-b778-0800200c9a66", "bf0f0f90-f481-11e2-b778-0800200c9a66")).toString(),
              new ParseCreatedServerTest().expected().toString());
   }

   public void testCreateServerWithBootVolumeWhenResponseIs202() throws Exception {
      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"\",\"flavorRef\":\"12345\",\"block_device_mapping_v2\":[{\"volume_size\":100,\"uuid\":\"f0c907a5-a26b-48ba-b803-83f6b7450ba5\",\"destination_type\":\"volume\",\"source_type\":\"image\"}]}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();


      NovaApi apiWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      BlockDeviceMapping blockDeviceMapping = BlockDeviceMapping.builder()
            .uuid("f0c907a5-a26b-48ba-b803-83f6b7450ba5").sourceType("image").destinationType("volume")
            .volumeSize(100).build();

      assertEquals(apiWithNewServer.getServerApi("az-1.region-a.geo-1").create("test-e92", "",
               "12345", new CreateServerOptions().blockDeviceMappings(ImmutableSet.of(blockDeviceMapping))).toString(),
              new ParseCreatedServerTest().expected().toString());
   }

   public void testCreateServerWithBootVolumeWhenResponseIs404() throws Exception {
      HttpRequest createServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                     "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"\",\"flavorRef\":\"12345\",\"block_device_mapping_v2\":[{\"volume_size\":100,\"uuid\":\"f0c907a5-a26b-48ba-b803-83f6b7450ba5\",\"destination_type\":\"volume\",\"source_type\":\"image\"}]}}", "application/json"))
            .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      BlockDeviceMapping blockDeviceMapping = BlockDeviceMapping.builder()
            .uuid("f0c907a5-a26b-48ba-b803-83f6b7450ba5").sourceType("image")
            .destinationType("volume").volumeSize(100).build();

      try {
         apiWithNewServer.getServerApi("az-1.region-a.geo-1").create("test-e92", "", "12345", new CreateServerOptions().blockDeviceMappings(ImmutableSet.of(blockDeviceMapping)));
         fail("Expected an exception.");
      } catch (Exception e) {
         // expected
      }
   }

   public void testCreateServerWithDiskConfigAuto() throws Exception {
      HttpRequest createServer = HttpRequest.builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
               "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"OS-DCF:diskConfig\":\"AUTO\"}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_disk_config_auto.json", "application/json; charset=UTF-8")).build();

      NovaApi apiWithNewServer = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            createServer, createServerResponse);

      assertEquals(apiWithNewServer.getServerApi("az-1.region-a.geo-1").create("test-e92", "1241",
               "100", new CreateServerOptions().diskConfig(Server.DISK_CONFIG_AUTO)).toString(),
              new ParseCreatedServerTest().expectedWithDiskConfig(Server.DISK_CONFIG_AUTO).toString());
   }

   public void testCreateServerWithDiskConfigManual() throws Exception {
      HttpRequest createServer = HttpRequest.builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
               "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"OS-DCF:diskConfig\":\"MANUAL\"}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_disk_config_manual.json", "application/json; charset=UTF-8")).build();

      NovaApi apiWithNewServer = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            createServer, createServerResponse);

      assertEquals(apiWithNewServer.getServerApi("az-1.region-a.geo-1").create("test-e92", "1241",
               "100", new CreateServerOptions().diskConfig(Server.DISK_CONFIG_MANUAL)).toString(),
              new ParseCreatedServerTest().expectedWithDiskConfig(Server.DISK_CONFIG_MANUAL).toString());
   }

   public void testRebuildServerWhenResponseIs202() throws Exception {
      String serverId = "52415800-8b69-11e0-9b19-734f565bc83b";
      HttpRequest rebuildServer = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"rebuild\":{\"adminPass\":\"password\",\"imageRef\":\"1234\",\"name\":\"newName\",\"accessIPv4\":\"1.1.1.1\",\"accessIPv6\":\"fe80::100\"}}", "application/json"))
            .build();

      HttpResponse rebuildServerResponse = HttpResponse.builder().statusCode(202).build();

      NovaApi apiRebuildServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, rebuildServer, rebuildServerResponse);

      RebuildServerOptions options = new RebuildServerOptions().withImage("1234").name("newName").adminPass("password").ipv4Address("1.1.1.1").ipv6Address("fe80::100");

      apiRebuildServer.getServerApi("az-1.region-a.geo-1").rebuild(serverId, options);
   }

   public void testCreateImageWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      String imageId = "456";
      String imageName = "foo";

      HttpRequest createImage = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"createImage\":{\"name\":\"" + imageName + "\", \"metadata\": {}}}", "application/json"))
            .build();

      HttpResponse createImageResponse = HttpResponse.builder()
            .statusCode(200)
            .headers(ImmutableMultimap.<String, String> builder()
                  .put("Location", "https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/images/" + imageId).build()).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, createImage, createImageResponse);

      assertEquals(apiWhenServerExists.getServerApi("az-1.region-a.geo-1").createImageFromServer(imageName, serverId),
            imageId);
   }

   public void testCreateImageWhenResponseIs404IsEmpty() throws Exception {
      String serverId = "123";
      String imageName = "foo";

      HttpRequest createImage = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"createImage\":{\"name\":\"" + imageName + "\", \"metadata\": {}}}", "application/json"))
            .build();

      HttpResponse createImageResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenServerDoesNotExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, createImage, createImageResponse);

      try {
         apiWhenServerDoesNotExist.getServerApi("az-1.region-a.geo-1").createImageFromServer(imageName, serverId);
         fail("Expected an exception.");
      } catch (Exception e) {
         // expected
      }
   }

   public void testStopServerWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      HttpRequest stopServer = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"os-stop\":null}", "application/json"))
            .build();

      HttpResponse stopServerResponse = HttpResponse.builder().statusCode(202).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, stopServer, stopServerResponse);

      apiWhenServerExists.getServerApi("az-1.region-a.geo-1").stop(serverId);
   }

   public void testStopServerWhenResponseIs404() throws Exception {
      String serverId = "123";
      HttpRequest stopServer = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "*/*")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"os-stop\":null}", "application/json"))
            .build();

      HttpResponse stopServerResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, stopServer, stopServerResponse);

      try {
         apiWhenServerExists.getServerApi("az-1.region-a.geo-1").stop(serverId);
         fail("Expected an exception.");
      } catch (Exception e) {
         // expected
      }
   }

   public void testStartServerWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      HttpRequest startServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"os-start\":null}", "application/json"))
            .build();

      HttpResponse startServerResponse = HttpResponse.builder().statusCode(202).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, startServer, startServerResponse);

      apiWhenServerExists.getServerApi("az-1.region-a.geo-1").start(serverId);
   }

   public void testStartServerWhenResponseIs404() throws Exception {
      String serverId = "123";
      HttpRequest startServer = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"os-start\":null}", "application/json"))
            .build();

      HttpResponse startServerResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, startServer, startServerResponse);

      try {
         apiWhenServerExists.getServerApi("az-1.region-a.geo-1").start(serverId);
         fail("Expected an exception.");
      } catch (Exception e) {
         // expected
      }
   }

   public void testListMetadataWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      HttpRequest getMetadata = HttpRequest.builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse getMetadataResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/metadata_list.json")).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, getMetadata, getMetadataResponse);

      assertEquals(apiWhenServerExists.getServerApi("az-1.region-a.geo-1").getMetadata(serverId).toString(),
             new ParseMetadataListTest().expected().toString());
   }

   public void testListMetadataWhenResponseIs404() throws Exception {
      String serverId = "123";
      HttpRequest getMetadata = HttpRequest.builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata")
            .addHeader("Accept", "*/*")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse getMetadataResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, getMetadata, getMetadataResponse);

      try {
         apiWhenServerExists.getServerApi("az-1.region-a.geo-1").getMetadata(serverId);
         fail("Expected an exception.");
      } catch (Exception e) {
         // expected
      }
   }

   public void testSetMetadataWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      ImmutableMap<String, String> metadata = new ImmutableMap.Builder<String, String>()
              .put("Server Label", "Web Head 1")
              .put("Image Version", "2.1")
              .build();

      HttpRequest setMetadata = HttpRequest.builder()
            .method("PUT")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"metadata\":{\"Server Label\":\"Web Head 1\",\"Image Version\":\"2.1\"}}", "application/json"))
            .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/metadata_list.json")).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, setMetadata, setMetadataResponse);

      assertEquals(apiWhenServerExists.getServerApi("az-1.region-a.geo-1").setMetadata(serverId, metadata).toString(),
             new ParseMetadataListTest().expected().toString());
   }

   public void testSetMetadataWhenResponseIs404() throws Exception {
      String serverId = "123";
      ImmutableMap<String, String> metadata = new ImmutableMap.Builder<String, String>()
              .put("Server Label", "Web Head 1")
              .put("Image Version", "2.1")
              .build();

      HttpRequest setMetadata = HttpRequest.builder()
            .method("PUT")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata")
            .addHeader("Accept", "*/*")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"metadata\":{\"Server Label\":\"Web Head 1\",\"Image Version\":\"2.1\"}}", "application/json"))
            .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, setMetadata, setMetadataResponse);

      try {
         apiWhenServerExists.getServerApi("az-1.region-a.geo-1").setMetadata(serverId, metadata);
         fail("Expected an exception.");
      } catch (Exception e) {
         // expected
      }
   }

   public void testUpdateMetadataWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      ImmutableMap<String, String> metadata = new ImmutableMap.Builder<String, String>()
              .put("Server Label", "Web Head 2")
              .put("Server Description", "Simple Server")
              .build();

      HttpRequest setMetadata = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"metadata\":{\"Server Label\":\"Web Head 2\",\"Server Description\":\"Simple Server\"}}", "application/json"))
            .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/metadata_updated.json")).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, setMetadata, setMetadataResponse);

      assertEquals(apiWhenServerExists.getServerApi("az-1.region-a.geo-1").updateMetadata(serverId, metadata).toString(),
             new ParseMetadataUpdateTest().expected().toString());
   }

   public void testUpdateMetadataWhenResponseIs404() throws Exception {
      String serverId = "123";
      ImmutableMap<String, String> metadata = new ImmutableMap.Builder<String, String>()
              .put("Server Label", "Web Head 2")
              .put("Server Description", "Simple Server")
              .build();

      HttpRequest setMetadata = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"metadata\":{\"Server Label\":\"Web Head 2\",\"Server Description\":\"Simple Server\"}}", "application/json"))
            .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(404)
              .payload(payloadFromResource("/metadata_updated.json")).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, setMetadata, setMetadataResponse);

      try {
         apiWhenServerExists.getServerApi("az-1.region-a.geo-1").setMetadata(serverId, metadata);
         fail("Expected an exception.");
      } catch (Exception e) {
         // expected
      }
   }

   public void testGetMetadataItemWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      String key = "Server Label";

      HttpRequest getMetadata = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata/" + "Server%20Label")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse getMetadataResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/metadata_item.json")).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, getMetadata, getMetadataResponse);

      assertEquals(apiWhenServerExists.getServerApi("az-1.region-a.geo-1").getMetadata(serverId, key).toString(),
             "Web Head 1");
   }

   public void testGetMetadataItemWhenResponseIs404() throws Exception {
      String serverId = "123";
      ImmutableMap<String, String> metadata = new ImmutableMap.Builder<String, String>()
              .put("Server Label", "Web Head 1")
              .build();

      HttpRequest setMetadata = HttpRequest.builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"metadata\":{\"Server Label\":\"Web Head 2\",\"Server Description\":\"Simple Server\"}}", "application/json"))
            .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(404)
              .payload(payloadFromResource("/metadata_updated.json")).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, setMetadata, setMetadataResponse);

      try {
         apiWhenServerExists.getServerApi("az-1.region-a.geo-1").setMetadata(serverId, metadata);
         fail("Expected an exception.");
      } catch (Exception e) {
         // expected
      }
   }

   public void testSetMetadataItemWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      ImmutableMap<String, String> metadata = new ImmutableMap.Builder<String, String>()
              .put("Server Label", "Web Head 2")
              .put("Server Description", "Simple Server")
              .build();

      HttpRequest setMetadata = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"metadata\":{\"Server Label\":\"Web Head 2\",\"Server Description\":\"Simple Server\"}}", "application/json"))
            .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/metadata_updated.json")).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, setMetadata, setMetadataResponse);

      assertEquals(apiWhenServerExists.getServerApi("az-1.region-a.geo-1").updateMetadata(serverId, metadata).toString(),
             new ParseMetadataUpdateTest().expected().toString());
   }

   public void testSetMetadataItemWhenResponseIs404() throws Exception {
      String serverId = "123";
      ImmutableMap<String, String> metadata = new ImmutableMap.Builder<String, String>()
              .put("Server Label", "Web Head 2")
              .put("Server Description", "Simple Server")
              .build();

      HttpRequest setMetadata = HttpRequest.builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"metadata\":{\"Server Label\":\"Web Head 2\",\"Server Description\":\"Simple Server\"}}", "application/json"))
            .build();

      HttpResponse setMetadataResponse = HttpResponse.builder().statusCode(404)
              .payload(payloadFromResource("/metadata_updated.json")).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, setMetadata, setMetadataResponse);

      try {
         apiWhenServerExists.getServerApi("az-1.region-a.geo-1").setMetadata(serverId, metadata);
         fail("Expected an exception.");
      } catch (Exception e) {
         // expected
      }
   }

   public void testDeleteMetadataItemWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      String key = "Server%20Label";

      HttpRequest updateMetadata = HttpRequest.builder()
            .method("DELETE")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata/" + key)
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse updateMetadataResponse = HttpResponse.builder().statusCode(204).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, updateMetadata, updateMetadataResponse);

      apiWhenServerExists.getServerApi("az-1.region-a.geo-1").deleteMetadata(serverId, key);
   }

   public void testDeleteMetadataItemWhenResponseIs404() throws Exception {
      String serverId = "123";
      String key = "Server%20Label";

      HttpRequest deleteMetadata = HttpRequest.builder()
            .method("DELETE")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/metadata/" + key)
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse deleteMetadataResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, deleteMetadata, deleteMetadataResponse);

      apiWhenServerExists.getServerApi("az-1.region-a.geo-1").deleteMetadata(serverId, key);
   }

   public void testGetDiagnosticsWhenResponseIs200() throws Exception {
       String serverId = "123";
       HttpRequest getDiagnostics = HttpRequest.builder()
            .method("GET")
            .addHeader("Accept", "application/json")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/diagnostics")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse serverDiagnosticsResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/server_diagnostics.json", "application/json; charset=UTF-8")).build();

      NovaApi apiWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, getDiagnostics, serverDiagnosticsResponse);
      assertEquals(apiWithNewServer.getServerApi("az-1.region-a.geo-1").getDiagnostics(serverId),
             new ParseServerDiagnostics().expected());
   }

   public void testGetDiagnosticsWhenResponseIs403Or404Or500() throws Exception {
       String serverId = "123";
       HttpRequest getDiagnostics = HttpRequest.builder()
            .method("GET")
            .addHeader("Accept", "application/json")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/" + serverId + "/diagnostics")
            .addHeader("X-Auth-Token", authToken)
            .build();

      for (int statusCode : ImmutableSet.of(403, 404, 500)) {
        assertTrue(!requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess, getDiagnostics,
            HttpResponse.builder().statusCode(statusCode).build()).getServerApi("az-1.region-a.geo-1").getDiagnostics(serverId).isPresent());
      }
   }
}
