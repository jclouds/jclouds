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

package org.jclouds.openstack.neutron.v2_0.extensions;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.neutron.v2_0.domain.ExternalGatewayInfo;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.domain.Router;
import org.jclouds.openstack.neutron.v2_0.domain.RouterInterface;
import org.jclouds.openstack.neutron.v2_0.domain.State;
import org.jclouds.openstack.neutron.v2_0.internal.BaseNeutronApiExpectTest;
import org.jclouds.openstack.neutron.v2_0.options.CreateRouterOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdateRouterOptions;
import org.jclouds.openstack.neutron.v2_0.parse.ParseRouterTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of RouterApi
 *
 */
@Test(groups = "unit", testName = "RouterApiExpectTest")
public class RouterApiExpectTest extends BaseNeutronApiExpectTest {

   private static final String ZONE = "region-a.geo-1";

   public void testListReferencesReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers").addQueryParam("fields", "id", "tenant_id", "name").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/list_routers.json", APPLICATION_JSON)).build())
         .getRouterExtensionForZone(ZONE).get();

      Set<? extends ReferenceWithName> references = api.list().concat().toSet();
      assertEquals(references, listOfReferencesWithNames());
   }

   public void testListReferencesReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers").addQueryParam("fields", "id", "tenant_id", "name").build(),
         HttpResponse.builder().statusCode(404).build())
         .getRouterExtensionForZone(ZONE).get();

      assertTrue(api.list().concat().isEmpty());
   }

   public void testListReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/list_routers.json", APPLICATION_JSON)).build())
         .getRouterExtensionForZone(ZONE).get();

      Set<? extends Router> routers = api.listInDetail().concat().toSet();
      assertEquals(routers, listOfRouters());
   }

   public void testListReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers").build(),
         HttpResponse.builder().statusCode(404).build())
         .getRouterExtensionForZone(ZONE).get();

      assertTrue(api.listInDetail().concat().isEmpty());
   }

   public void testGetReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/router.json", APPLICATION_JSON)).build())
         .getRouterExtensionForZone(ZONE).get();

      Router router = api.get("16dba3bc-f3fa-4775-afdc-237e12c72f6a");
      assertEquals(router, new ParseRouterTest().expected());
   }

   public void testGetReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         HttpResponse.builder().statusCode(404).build())
         .getRouterExtensionForZone(ZONE).get();

      assertNull(api.get("16dba3bc-f3fa-4775-afdc-237e12c72f6a"));
   }

   public void testCreateReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers").method("POST")
            .payload(payloadFromStringWithContentType("{\"router\":{\"name\":\"test\",\"external_gateway_info\":{\"network_id\":\"1234567890\"}}}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"router\":{\"id\":\"12345\",\"tenant_id\":\"6789\",\"external_gateway_info\":{\"network_id\":\"1234567890\"}}}", APPLICATION_JSON)).build())
         .getRouterExtensionForZone(ZONE).get();

      Router router = api.create(CreateRouterOptions.builder().name("test").externalGatewayInfo(ExternalGatewayInfo.builder().networkId("1234567890").build()).build());
      assertEquals(router, Router.builder().id("12345").tenantId("6789").externalGatewayInfo(ExternalGatewayInfo.builder().networkId("1234567890").build()).build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers").method("POST")
            .payload(payloadFromStringWithContentType("{\"router\":{\"name\":\"another-test\",\"external_gateway_info\":{\"network_id\":\"1234567890\"}}}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(401).build())
         .getRouterExtensionForZone(ZONE).get();

      api.create(CreateRouterOptions.builder().name("another-test").externalGatewayInfo(ExternalGatewayInfo.builder().networkId("1234567890").build()).build());
   }

   public void testUpdateReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345").method("PUT")
            .payload(payloadFromStringWithContentType("{\"router\":{\"name\":\"another-test\",\"admin_state_up\":true}}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).build())
         .getRouterExtensionForZone(ZONE).get();

      assertTrue(api.update("12345", UpdateRouterOptions.builder().name("another-test").adminStateUp(true).build()));
   }

   public void testUpdateReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345").method("PUT")
            .payload(payloadFromStringWithContentType("{\"router\":{\"name\":\"another-test\",\"admin_state_up\":true}}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(404).build())
         .getRouterExtensionForZone(ZONE).get();

      assertFalse(api.update("12345", UpdateRouterOptions.builder().name("another-test").adminStateUp(true).build()));
   }

   public void testDeleteReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345").method("DELETE").build(),
         HttpResponse.builder().statusCode(200).build())
         .getRouterExtensionForZone(ZONE).get();

      assertTrue(api.delete("12345"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testDeleteReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345").method("DELETE").build(),
         HttpResponse.builder().statusCode(403).build())
         .getRouterExtensionForZone(ZONE).get();

      api.delete("12345");
   }

   public void testAddInterfaceForSubnetReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345/add_router_interface").method("PUT")
            .payload(payloadFromStringWithContentType("{\"subnet_id\":\"1234567890\"}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"subnet_id\":\"1234567890\",\"port_id\":\"987654321\"}", MediaType.APPLICATION_JSON)).build())
         .getRouterExtensionForZone(ZONE).get();

      RouterInterface routerInterface = api.addInterfaceForSubnet("12345", "1234567890");
      assertEquals(routerInterface, RouterInterface.builder().subnetId("1234567890").portId("987654321").build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testAddInterfaceForSubnetReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345/add_router_interface").method("PUT")
            .payload(payloadFromStringWithContentType("{\"subnet_id\":\"1234567890\"}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(403).build())
         .getRouterExtensionForZone(ZONE).get();

      api.addInterfaceForSubnet("12345", "1234567890");
   }

   public void testAddInterfaceForPortReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345/add_router_interface").method("PUT")
            .payload(payloadFromStringWithContentType("{\"port_id\":\"987654321\"}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"subnet_id\":\"1234567890\",\"port_id\":\"987654321\"}", MediaType.APPLICATION_JSON)).build())
         .getRouterExtensionForZone(ZONE).get();

      RouterInterface routerInterface = api.addInterfaceForPort("12345", "987654321");
      assertEquals(routerInterface, RouterInterface.builder().subnetId("1234567890").portId("987654321").build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testAddInterfaceForPortReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345/add_router_interface").method("PUT")
            .payload(payloadFromStringWithContentType("{\"port_id\":\"1234567890\"}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(403).build())
         .getRouterExtensionForZone(ZONE).get();

      api.addInterfaceForPort("12345", "1234567890");
   }

   public void testRemoveInterfaceForSubnetReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345/remove_router_interface").method("PUT")
            .payload(payloadFromStringWithContentType("{\"subnet_id\":\"1234567890\"}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).build())
         .getRouterExtensionForZone(ZONE).get();

      api.removeInterfaceForSubnet("12345", "1234567890");
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testRemoveInterfaceForSubnetReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345/remove_router_interface").method("PUT")
            .payload(payloadFromStringWithContentType("{\"subnet_id\":\"1234567890\"}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(403).build())
         .getRouterExtensionForZone(ZONE).get();

      api.removeInterfaceForSubnet("12345", "1234567890");
   }

   public void testRemoveInterfaceForPortReturns2xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345/remove_router_interface").method("PUT")
            .payload(payloadFromStringWithContentType("{\"port_id\":\"1234567890\"}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).build())
         .getRouterExtensionForZone(ZONE).get();

      api.removeInterfaceForPort("12345", "1234567890");
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testRemoveInterfaceForPortReturns4xx() {
      RouterApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/routers/12345/remove_router_interface").method("PUT")
            .payload(payloadFromStringWithContentType("{\"port_id\":\"1234567890\"}", MediaType.APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(403).build())
         .getRouterExtensionForZone(ZONE).get();

      api.removeInterfaceForPort("12345", "1234567890");
   }

   protected Set<Router> listOfRouters() {
      return ImmutableSet.of(
         Router.builder().state(State.ACTIVE).name("jclouds-test").tenantId("1234567890").id("16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         Router.builder().state(State.ACTIVE).name("wibble").tenantId("1234567890").id("1a104cf5-cb18-4d35-9407-2fd2646d9d0b").build(),
         Router.builder().state(State.ACTIVE).name("jclouds-test").tenantId("1234567890").id("31083ae2-420d-48b2-ac98-9f7a4fd8dbdc").build(),
         Router.builder().state(State.ACTIVE).name("jclouds-test").tenantId("1234567890").id("49c6d6fa-ff2a-459d-b975-75a8d31c9a89").build(),
         Router.builder().state(State.ACTIVE).name("wibble").tenantId("1234567890").id("5cb3d6f4-62cb-41c9-b964-ba7d9df79e4e").build(),
         Router.builder().state(State.ACTIVE).name("jclouds-test").tenantId("1234567890").id("5d51d012-3491-4db7-b1b5-6f254015015d").build(),
         Router.builder().state(State.ACTIVE).name("wibble").tenantId("1234567890").id("5f9cf7dc-22ca-4097-8e49-1cc8b23faf17").build(),
         Router.builder().state(State.ACTIVE).name("jclouds-test").tenantId("1234567890").id("6319ecad-6bff-48b2-9b53-02ede8cb7588").build(),
         Router.builder().state(State.ACTIVE).name("jclouds-test").tenantId("1234567890").id("6ba4c788-661f-49ab-9bf8-5f10cbbb2f57").build(),
         Router.builder().state(State.ACTIVE).name("jclouds-test").tenantId("1234567890").id("74ed170b-5069-4353-ab38-9719766dc57e").build(),
         Router.builder().state(State.ACTIVE).name("wibble").tenantId("1234567890").id("b71fcac1-e864-4031-8c5b-edbecd9ece36").build(),
         Router.builder().state(State.ACTIVE).name("jclouds-test").tenantId("1234567890").id("c7681895-d84d-4650-9ca0-82c72036b855").build()
      );
   }

}
