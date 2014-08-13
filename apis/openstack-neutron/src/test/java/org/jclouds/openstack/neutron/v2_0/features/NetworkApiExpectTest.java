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
package org.jclouds.openstack.neutron.v2_0.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.neutron.v2_0.domain.BulkNetwork;
import org.jclouds.openstack.neutron.v2_0.domain.Network;
import org.jclouds.openstack.neutron.v2_0.domain.NetworkType;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.internal.BaseNeutronApiExpectTest;
import org.jclouds.openstack.neutron.v2_0.options.CreateNetworkBulkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdateNetworkOptions;
import org.jclouds.openstack.neutron.v2_0.parse.ParseNetworkTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of NetworkApi
 *
 */
@Test(groups = "unit", testName = "NetworkApiExpectTest")
public class NetworkApiExpectTest extends BaseNeutronApiExpectTest {

   private static final String ZONE = "region-a.geo-1";

   public void testListReferencesReturns2xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks").addQueryParam("fields", "id", "tenant_id", "name").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/list_networks.json", APPLICATION_JSON)).build())
         .getNetworkApiForZone(ZONE);

      Set<? extends ReferenceWithName> references = api.list().concat().toSet();
      assertEquals(references, listOfReferencesWithNames());
   }

   public void testListReferencesReturns4xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks").addQueryParam("fields", "id", "tenant_id", "name").build(),
         HttpResponse.builder().statusCode(404).build())
         .getNetworkApiForZone(ZONE);

      assertTrue(api.list().concat().isEmpty());
   }

   public void testListReturns2xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/list_networks.json", APPLICATION_JSON)).build())
         .getNetworkApiForZone(ZONE);

      Set<? extends Network> nets = api.listInDetail().concat().toSet();
      assertEquals(nets, listOfNetworks());
   }

   public void testListReturns4xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks").build(),
         HttpResponse.builder().statusCode(404).build())
         .getNetworkApiForZone(ZONE);

      assertTrue(api.listInDetail().concat().isEmpty());
   }

   public void testGetReturns2xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/network.json", APPLICATION_JSON)).build())
         .getNetworkApiForZone(ZONE);

      Network net = api.get("16dba3bc-f3fa-4775-afdc-237e12c72f6a");
      assertEquals(net, new ParseNetworkTest().expected());
   }

   public void testGetReturns4xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         HttpResponse.builder().statusCode(404).build())
         .getNetworkApiForZone(ZONE);

      assertNull(api.get("16dba3bc-f3fa-4775-afdc-237e12c72f6a"));
   }

   public void testCreateReturns2xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks").method("POST")
            .payload(payloadFromStringWithContentType("{\"network\":{\"name\":\"another-test\",\"router:external\":true,\"provider:network_type\":\"local\"}}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"network\":{\"id\":\"12345\",\"tenant_id\":\"6789\",\"router:external\":true,\"provider:network_type\":\"local\"}}", APPLICATION_JSON)).build())
         .getNetworkApiForZone(ZONE);

      Network net = api.create(CreateNetworkOptions.builder().name("another-test").external(true).networkType(NetworkType.LOCAL).build());
      assertEquals(net, Network.builder().id("12345").tenantId("6789").external(true).networkType(NetworkType.LOCAL).build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateReturns4xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks").method("POST")
            .payload(payloadFromStringWithContentType("{\"network\":{\"name\":\"another-test\",\"provider:network_type\":\"local\"}}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(401).build())
         .getNetworkApiForZone(ZONE);

      api.create(CreateNetworkOptions.builder().name("another-test").networkType(NetworkType.LOCAL).build());
   }

   public void testCreateBulkReturns2xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks").method("POST")
            .payload(payloadFromStringWithContentType("{\"networks\":[{\"name\":\"test\",\"provider:network_type\":\"local\"},{\"name\":\"test-2\",\"provider:network_type\":\"local\"}]}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"networks\":[{\"id\":\"1\",\"tenant_id\":\"1\",\"name\":\"test\",\"provider:network_type\":\"local\"},{\"id\":\"2\",\"tenant_id\":\"1\",\"name\":\"test-2\",\"provider:network_type\":\"local\"}]}", APPLICATION_JSON)).build())
         .getNetworkApiForZone(ZONE);

      Set<? extends ReferenceWithName> nets = api.createBulk(
         CreateNetworkBulkOptions.builder().networks(
            ImmutableList.of(
               BulkNetwork.builder().networkType(NetworkType.LOCAL).name("test").build(),
               BulkNetwork.builder().networkType(NetworkType.LOCAL).name("test-2").build()
            )
         ).build()
      ).toSet();
      assertEquals(nets, createBulkReturns2xxResponse());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateBulkReturns4xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks").method("POST")
            .payload(payloadFromStringWithContentType("{\"networks\":[{\"name\":\"test\",\"provider:network_type\":\"local\"},{\"name\":\"test-2\",\"provider:network_type\":\"local\"}]}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(401).build())
         .getNetworkApiForZone(ZONE);

      api.createBulk(
         CreateNetworkBulkOptions.builder().networks(
            ImmutableList.of(
               BulkNetwork.builder().networkType(NetworkType.LOCAL).name("test").build(),
               BulkNetwork.builder().networkType(NetworkType.LOCAL).name("test-2").build()
            )
         ).build()
      );
   }

   public void testUpdateReturns2xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks/12345").method("PUT")
            .payload(payloadFromStringWithContentType("{\"network\":{\"name\":\"another-test\",\"admin_state_up\":true}}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).build())
         .getNetworkApiForZone(ZONE);

      assertTrue(api.update("12345", UpdateNetworkOptions.builder().name("another-test").adminStateUp(true).build()));
   }

   public void testUpdateReturns4xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks/12345").method("PUT")
            .payload(payloadFromStringWithContentType("{\"network\":{\"name\":\"another-test\",\"admin_state_up\":true}}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(404).build())
         .getNetworkApiForZone(ZONE);

      assertFalse(api.update("12345", UpdateNetworkOptions.builder().name("another-test").adminStateUp(true).build()));
   }

   public void testDeleteReturns2xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks/12345").method("DELETE").build(),
         HttpResponse.builder().statusCode(200).build())
         .getNetworkApiForZone(ZONE);

      assertTrue(api.delete("12345"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testDeleteReturns4xx() {
      NetworkApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/networks/12345").method("DELETE").build(),
         HttpResponse.builder().statusCode(403).build())
         .getNetworkApiForZone(ZONE);

      api.delete("12345");
   }

   protected Set<Network> listOfNetworks() {
      return ImmutableSet.of(
         Network.builder().name("jclouds-test").tenantId("1234567890").id("16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         Network.builder().name("wibble").tenantId("1234567890").id("1a104cf5-cb18-4d35-9407-2fd2646d9d0b").build(),
         Network.builder().name("jclouds-test").tenantId("1234567890").id("31083ae2-420d-48b2-ac98-9f7a4fd8dbdc").build(),
         Network.builder().name("jclouds-test").tenantId("1234567890").id("49c6d6fa-ff2a-459d-b975-75a8d31c9a89").build(),
         Network.builder().name("wibble").tenantId("1234567890").id("5cb3d6f4-62cb-41c9-b964-ba7d9df79e4e").build(),
         Network.builder().name("jclouds-test").tenantId("1234567890").id("5d51d012-3491-4db7-b1b5-6f254015015d").build(),
         Network.builder().name("wibble").tenantId("1234567890").id("5f9cf7dc-22ca-4097-8e49-1cc8b23faf17").build(),
         Network.builder().name("jclouds-test").tenantId("1234567890").id("6319ecad-6bff-48b2-9b53-02ede8cb7588").build(),
         Network.builder().name("jclouds-test").tenantId("1234567890").id("6ba4c788-661f-49ab-9bf8-5f10cbbb2f57").build(),
         Network.builder().name("jclouds-test").tenantId("1234567890").id("74ed170b-5069-4353-ab38-9719766dc57e").build(),
         Network.builder().name("wibble").tenantId("1234567890").id("b71fcac1-e864-4031-8c5b-edbecd9ece36").build(),
         Network.builder().name("jclouds-test").tenantId("1234567890").id("c7681895-d84d-4650-9ca0-82c72036b855").build()
      );
   }

   protected Set<Network> createBulkReturns2xxResponse() {
      return ImmutableSet.of(
         Network.builder().id("1").tenantId("1").name("test").networkType(NetworkType.LOCAL).build(),
         Network.builder().id("2").tenantId("1").name("test-2").networkType(NetworkType.LOCAL).build()
      );
   }
}
