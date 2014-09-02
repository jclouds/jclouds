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
package org.jclouds.openstack.nova.v2_0.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Map;
import java.util.Properties;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.Network;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionAndName;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.ServerInRegion;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaComputeServiceContextExpectTest;
import org.testng.annotations.Test;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests the compute service abstraction of the nova api.
 */
@Test(groups = "unit", testName = "NovaComputeServiceAdapterExpectTest")
public class NovaComputeServiceAdapterExpectTest extends BaseNovaComputeServiceContextExpectTest<Injector> {
   HttpRequest serverDetail = HttpRequest
         .builder()
         .method("GET")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/71752")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken).build();

   HttpResponse serverDetailResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/server_details.json")).build();

   public void testCreateNodeWithGroupEncodedIntoNameWithNetworks() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"networks\": [{\"uuid\": \"4ebd35cf-bfe7-4d93-b0d8-eb468ce2245a\"}]}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_networks_response.json", "application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forNetworks = requestsSendResponses(requestResponseMap);

      Template template = forNetworks.getInstance(TemplateBuilder.class).build();
      template.getOptions().as(NovaTemplateOptions.class).networks("4ebd35cf-bfe7-4d93-b0d8-eb468ce2245a");

      NovaComputeServiceAdapter adapter = forNetworks.getInstance(NovaComputeServiceAdapter.class);

      NodeAndInitialCredentials<ServerInRegion> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92", template);
      assertNotNull(server);
      // Response irrelevant in this expect test - just verifying the request.
   }

   public void testCreateNodeWithGroupEncodedIntoNameWithDiskConfig() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"OS-DCF:diskConfig\":\"AUTO\"}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_disk_config_auto.json", "application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forDiskConfig = requestsSendResponses(requestResponseMap);

      Template template = forDiskConfig.getInstance(TemplateBuilder.class).build();
      template.getOptions().as(NovaTemplateOptions.class).diskConfig(Server.DISK_CONFIG_AUTO);

      NovaComputeServiceAdapter adapter = forDiskConfig.getInstance(NovaComputeServiceAdapter.class);

      NodeAndInitialCredentials<ServerInRegion> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92", template);
      assertNotNull(server);
      assertEquals(server.getNode().getServer().getDiskConfig().orNull(), Server.DISK_CONFIG_AUTO);
   }

   public void testCreateNodeWithGroupEncodedIntoNameWithConfigDrive() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"config_drive\":\"true\"}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_config_drive.json", "application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forConfigDrive = requestsSendResponses(requestResponseMap);

      Template template = forConfigDrive.getInstance(TemplateBuilder.class).build();
      template.getOptions().as(NovaTemplateOptions.class).configDrive(true);

      NovaComputeServiceAdapter adapter = forConfigDrive.getInstance(NovaComputeServiceAdapter.class);

      NodeAndInitialCredentials<ServerInRegion> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92", template);
      assertNotNull(server);
   }

   public void testCreateNodeWithGroupEncodedIntoNameWithNovaNetworks() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"networks\":[{\"uuid\":\"12345\", \"port\":\"67890\", \"fixed_ip\":\"192.168.0.1\"},{\"uuid\":\"54321\", \"port\":\"09876\", \"fixed_ip\":\"192.168.0.2\"},{\"uuid\":\"non-nova-uuid\"}]}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_nova_networks.json", "application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forNovaNetworks = requestsSendResponses(requestResponseMap);

      Template template = forNovaNetworks.getInstance(TemplateBuilder.class).build();
      template.getOptions().as(NovaTemplateOptions.class)
         .networks("non-nova-uuid")
         .novaNetworks(
               ImmutableSet.of(
                     Network.builder()
                        .networkUuid("12345")
                        .portUuid("67890")
                        .fixedIp("192.168.0.1")
                        .build(),
                     Network.builder()
                        .networkUuid("54321")
                        .portUuid("09876")
                        .fixedIp("192.168.0.2")
                        .build()));

      NovaComputeServiceAdapter adapter = forNovaNetworks.getInstance(NovaComputeServiceAdapter.class);

      NodeAndInitialCredentials<ServerInRegion> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92", template);
      assertNotNull(server);
   }

   public void testCreateNodeWithGroupEncodedIntoNameWhenSecurityGroupsArePresent() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"security_groups\":[{\"name\":\"group1\"}, {\"name\":\"group2\"}]}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forSecurityGroups = requestsSendResponses(requestResponseMap);

      Template template = forSecurityGroups.getInstance(TemplateBuilder.class).build();
      template.getOptions().as(NovaTemplateOptions.class).securityGroupNames("group1", "group2");

      NovaComputeServiceAdapter adapter = forSecurityGroups.getInstance(NovaComputeServiceAdapter.class);

      NodeAndInitialCredentials<ServerInRegion> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92",
               template);
      assertNotNull(server);
      assertEquals(server.getCredentials(), LoginCredentials.builder().password("ZWuHcmTMQ7eXoHeM").build());
   }

   /**
    * We need to choose the correct credential for attempts to start the server. cloud-init or the
    * like will set the ssh key as the login credential, and not necessarily will password
    * authentication even be available.
    */
   public void testWhenKeyPairPresentWeUsePrivateKeyAsCredentialNotPassword() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"key_name\":\"foo\"}}", "application/json"))
         .build();


      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_no_adminpass.json", "application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forSecurityGroups = requestsSendResponses(requestResponseMap);

      Template template = forSecurityGroups.getInstance(TemplateBuilder.class).build();
      template.getOptions().as(NovaTemplateOptions.class).keyPairName("foo");

      NovaComputeServiceAdapter adapter = forSecurityGroups.getInstance(NovaComputeServiceAdapter.class);

      // we expect to have already an entry in the cache for the key
      LoadingCache<RegionAndName, KeyPair> keyPairCache = forSecurityGroups.getInstance(Key
               .get(new TypeLiteral<LoadingCache<RegionAndName, KeyPair>>() {
               }));
      keyPairCache.put(RegionAndName.fromRegionAndName("az-1.region-a.geo-1", "foo"), KeyPair.builder().name("foo")
               .privateKey("privateKey").build());

      NodeAndInitialCredentials<ServerInRegion> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92",
               template);
      assertNotNull(server);
      assertEquals(server.getCredentials(), LoginCredentials.builder().privateKey("privateKey").build());
   }


   /**
    * When enable_instance_password is false, then no admin pass is generated.
    * However in this case if you don't specify the name of the SSH keypair to
    * inject, then you simply cannot log in to the server.
    */
   public void testNoKeyPairOrAdminPass() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\"}}", "application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_no_adminpass.json", "application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forSecurityGroups = requestsSendResponses(requestResponseMap);

      Template template = forSecurityGroups.getInstance(TemplateBuilder.class).build();

      NovaComputeServiceAdapter adapter = forSecurityGroups.getInstance(NovaComputeServiceAdapter.class);

      NodeAndInitialCredentials<ServerInRegion> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92",
            template);
      assertNotNull(server);
      assertNull(server.getCredentials());
   }

   /**
    * Test successful suspend/resume via ComputeService which depends on
    * Admin extension being installed in OpenStack
    */
   public void testSuspendWithAdminExtensionSucceeds() throws Exception {

      HttpRequest suspendServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/71752/action")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"suspend\":null}", "application/json"))
         .build();

      HttpResponse suspendServerResponse = HttpResponse.builder()
            .statusCode(202)
            .build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(suspendServer, suspendServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forAdminExtension = requestsSendResponses(requestResponseMap);

      ComputeService computeService = forAdminExtension.getInstance(ComputeService.class);

      computeService.suspendNode("az-1.region-a.geo-1/71752");
   }

   /**
    * Test failed suspend/resume via ComputeService which depends on
    * Admin extension being installed in OpenStack. Throws UOE if extension is missing.
    */
   @Test(expectedExceptions =  UnsupportedOperationException.class)
   public void testSuspendWithoutAdminExtensionThrowsUOE() throws Exception {

      HttpRequest suspendServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/servers/71752/action")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"suspend\":null}", "application/json"))
            .build();

      HttpResponse suspendServerResponse = HttpResponse.builder()
            .statusCode(202)
            .build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
            .put(extensionsOfNovaRequest, unmatchedExtensionsOfNovaResponse)
            .put(listDetail, listDetailResponse)
            .put(listFlavorsDetail, listFlavorsDetailResponse)
            .put(suspendServer, suspendServerResponse)
            .put(serverDetail, serverDetailResponse).build();

      Injector forAdminExtension = requestsSendResponses(requestResponseMap);

      ComputeService compute = forAdminExtension.getInstance(ComputeService.class);

      compute.suspendNode("az-1.region-a.geo-1/71752");
   }

   @Override
   public Injector apply(ComputeServiceContext input) {
      return input.utils().injector();
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      // only specify one region so that we don't have to configure requests for multiple regions
      overrides.setProperty("jclouds.regions", "az-1.region-a.geo-1");
      return overrides;
   }
}
