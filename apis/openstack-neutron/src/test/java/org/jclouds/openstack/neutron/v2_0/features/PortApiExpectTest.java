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

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.neutron.v2_0.domain.BulkPort;
import org.jclouds.openstack.neutron.v2_0.domain.Port;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.domain.State;
import org.jclouds.openstack.neutron.v2_0.internal.BaseNeutronApiExpectTest;
import org.jclouds.openstack.neutron.v2_0.options.CreatePortBulkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreatePortOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdatePortOptions;
import org.jclouds.openstack.neutron.v2_0.parse.ParsePortTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of PortApi
 *
 */
@Test(groups = "unit", testName = "PortApiExpectTest")
public class PortApiExpectTest extends BaseNeutronApiExpectTest {

   private static final String ZONE = "region-a.geo-1";

   public void testListReferencesReturns2xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports").addQueryParam("fields", "id", "tenant_id", "name").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/list_ports.json", APPLICATION_JSON)).build())
         .getPortApiForZone(ZONE);

      Set<? extends ReferenceWithName> references = api.list().concat().toSet();
      assertEquals(references, listOfReferencesWithNames());
   }

   public void testListReferencesReturns4xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports").addQueryParam("fields", "id", "tenant_id", "name").build(),
         HttpResponse.builder().statusCode(404).build())
         .getPortApiForZone(ZONE);

      assertTrue(api.list().concat().isEmpty());
   }

   public void testListReturns2xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/list_ports.json", APPLICATION_JSON)).build())
         .getPortApiForZone(ZONE);

      Set<? extends Port> ports = api.listInDetail().concat().toSet();
      assertEquals(ports, listOfPorts());
   }

   public void testListReturns4xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports").build(),
         HttpResponse.builder().statusCode(404).build())
         .getPortApiForZone(ZONE);

      assertTrue(api.listInDetail().concat().isEmpty());
   }

   public void testGetReturns2xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports/624312ff-d14b-4ba3-9834-1c78d23d574d").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/port.json", APPLICATION_JSON)).build())
         .getPortApiForZone(ZONE);

      Port port = api.get("624312ff-d14b-4ba3-9834-1c78d23d574d");
      assertEquals(port, new ParsePortTest().expected());
   }

   public void testGetReturns4xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports/624312ff-d14b-4ba3-9834-1c78d23d574d").build(),
         HttpResponse.builder().statusCode(404).build())
         .getPortApiForZone(ZONE);

      assertNull(api.get("624312ff-d14b-4ba3-9834-1c78d23d574d"));
   }

   public void testCreateReturns2xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports").method("POST")
            .payload(payloadFromStringWithContentType("{\"port\":{\"network_id\":\"1\",\"name\":\"test-port\"}}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"port\":{\"id\":\"1\",\"tenant_id\":\"1\",\"network_id\":\"1\",\"name\":\"test-port\"}}", APPLICATION_JSON)).build())
         .getPortApiForZone(ZONE);

      Port port = api.create("1", CreatePortOptions.builder().name("test-port").build());
      assertEquals(port, Port.builder().id("1").tenantId("1").name("test-port").networkId("1").build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateReturns4xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports").method("POST")
            .payload(payloadFromStringWithContentType("{\"port\":{\"network_id\":\"1\",\"name\":\"test-port\"}}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(401).build())
         .getPortApiForZone(ZONE);

      api.create("1", CreatePortOptions.builder().name("test-port").build());
   }

   public void testCreateBulkReturns2xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports").method("POST")
            .payload(payloadFromStringWithContentType("{\"ports\":[{\"network_id\":\"1\",\"name\":\"test-port-1\"},{\"network_id\":\"2\",\"name\":\"test-port-2\"}]}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200)
            .payload(payloadFromStringWithContentType("{\"ports\":[{\"id\":\"1\",\"tenant_id\":\"1\",\"network_id\":\"1\",\"name\":\"test-port-1\"},{\"id\":\"2\",\"tenant_id\":\"1\",\"network_id\":\"2\",\"name\":\"test-port-2\"}]}", APPLICATION_JSON)).build())
         .getPortApiForZone(ZONE);

      Set<? extends ReferenceWithName> ports = api.createBulk(
         CreatePortBulkOptions.builder().ports(
            ImmutableList.of(
               BulkPort.builder().networkId("1").name("test-port-1").build(),
               BulkPort.builder().networkId("2").name("test-port-2").build()
            )
         ).build()
      ).toSet();
      assertEquals(ports, createBulkReturns2xxResponse());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateBulkReturns4xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports").method("POST")
            .payload(payloadFromStringWithContentType("{\"ports\":[{\"network_id\":\"1\",\"name\":\"test-port-1\"},{\"network_id\":\"2\",\"name\":\"test-port-2\"}]}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(401).build())
         .getPortApiForZone(ZONE);

      api.createBulk(
         CreatePortBulkOptions.builder().ports(
            ImmutableList.of(
               BulkPort.builder().networkId("1").name("test-port-1").build(),
               BulkPort.builder().networkId("2").name("test-port-2").build()
            )
         ).build()
      );
   }

   public void testUpdateReturns2xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports/12345").method("PUT")
            .payload(payloadFromStringWithContentType("{\"port\":{\"name\":\"another-test\",\"admin_state_up\":true}}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).build())
         .getPortApiForZone(ZONE);

      assertTrue(api.update("12345", UpdatePortOptions.builder().name("another-test").adminStateUp(true).build()));
   }

   public void testUpdateReturns4xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports/12345").method("PUT")
            .payload(payloadFromStringWithContentType("{\"port\":{\"name\":\"another-test\",\"admin_state_up\":true}}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(404).build())
         .getPortApiForZone(ZONE);

      assertFalse(api.update("12345", UpdatePortOptions.builder().name("another-test").adminStateUp(true).build()));
   }

   public void testDeleteReturns2xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports/12345").method("DELETE").build(),
         HttpResponse.builder().statusCode(200).build())
         .getPortApiForZone(ZONE);

      assertTrue(api.delete("12345"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testDeleteReturns4xx() {
      PortApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/ports/12345").method("DELETE").build(),
         HttpResponse.builder().statusCode(403).build())
         .getPortApiForZone(ZONE);

      api.delete("12345");
   }

   protected Set<Port> listOfPorts() {
      return ImmutableSet.of(
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("wibble").tenantId("1234567890").id("1a104cf5-cb18-4d35-9407-2fd2646d9d0b").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("31083ae2-420d-48b2-ac98-9f7a4fd8dbdc").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("49c6d6fa-ff2a-459d-b975-75a8d31c9a89").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("wibble").tenantId("1234567890").id("5cb3d6f4-62cb-41c9-b964-ba7d9df79e4e").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("5d51d012-3491-4db7-b1b5-6f254015015d").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("wibble").tenantId("1234567890").id("5f9cf7dc-22ca-4097-8e49-1cc8b23faf17").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("6319ecad-6bff-48b2-9b53-02ede8cb7588").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("6ba4c788-661f-49ab-9bf8-5f10cbbb2f57").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("74ed170b-5069-4353-ab38-9719766dc57e").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("wibble").tenantId("1234567890").id("b71fcac1-e864-4031-8c5b-edbecd9ece36").build(),
         Port.builder().state(State.ACTIVE).networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("c7681895-d84d-4650-9ca0-82c72036b855").build()
      );
   }

   protected Set<Port> createBulkReturns2xxResponse() {
      return ImmutableSet.of(
         Port.builder().id("1").tenantId("1").name("test-port-1").networkId("1").build(),
         Port.builder().id("2").tenantId("1").name("test-port-2").networkId("2").build()
      );
   }

}
