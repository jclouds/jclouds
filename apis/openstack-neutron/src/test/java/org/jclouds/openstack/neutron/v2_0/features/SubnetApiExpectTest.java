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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.neutron.v2_0.domain.BulkSubnet;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.domain.Subnet;
import org.jclouds.openstack.neutron.v2_0.internal.BaseNeutronApiExpectTest;
import org.jclouds.openstack.neutron.v2_0.options.CreateSubnetBulkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdateSubnetOptions;
import org.jclouds.openstack.neutron.v2_0.parse.ParseSubnetTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests parsing and Guice wiring of SubnetApi
 */
@Test(groups = "unit", testName = "SubnetApiExpectTest")
public class SubnetApiExpectTest extends BaseNeutronApiExpectTest {

   private static final String ZONE = "region-a.geo-1";

   public void testListReferencesReturns2xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets").addQueryParam("fields", "id", "tenant_id", "name").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/list_subnets.json", APPLICATION_JSON)).build())
         .getSubnetApiForZone(ZONE);

      Set<? extends ReferenceWithName> references = api.list().concat().toSet();
      assertEquals(references, listOfReferencesWithNames());
   }

   public void testListReferencesReturns4xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets").addQueryParam("fields", "id", "tenant_id", "name").build(),
         HttpResponse.builder().statusCode(404).build())
         .getSubnetApiForZone(ZONE);

      assertTrue(api.list().concat().isEmpty());
   }

   public void testListReturns2xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/list_subnets.json", APPLICATION_JSON)).build())
         .getSubnetApiForZone(ZONE);

      Set<? extends Subnet> subnets = api.listInDetail().concat().toSet();
      assertEquals(subnets, listOfSubnets());
   }

   public void testListReturns4xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets").build(),
         HttpResponse.builder().statusCode(404).build())
         .getSubnetApiForZone(ZONE);

      assertTrue(api.listInDetail().concat().isEmpty());
   }

   public void testGetReturns2xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets/624312ff-d14b-4ba3-9834-1c78d23d574d").build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/subnet.json", APPLICATION_JSON)).build())
         .getSubnetApiForZone(ZONE);

      Subnet subnet = api.get("624312ff-d14b-4ba3-9834-1c78d23d574d");
      assertEquals(subnet, new ParseSubnetTest().expected());
   }

   public void testGetReturns4xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets/624312ff-d14b-4ba3-9834-1c78d23d574d").build(),
         HttpResponse.builder().statusCode(404).build())
         .getSubnetApiForZone(ZONE);

      assertNull(api.get("624312ff-d14b-4ba3-9834-1c78d23d574d"));
   }

   public void testCreateReturns2xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets").method("POST")
            .payload(payloadFromStringWithContentType("{\"subnet\":{\"network_id\":\"1234567890\",\"ip_version\":4,\"cidr\":\"10.0.3.0/24\",\"name\":\"subnet-test\"}}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"subnet\":{\"id\":\"12345\",\"tenant_id\":\"6789\",\"network_id\":\"1234567890\",\"ip_version\":4,\"cidr\":\"10.0.3.0/24\",\"name\":\"subnet-test\"}}", APPLICATION_JSON)).build())
         .getSubnetApiForZone(ZONE);

      Subnet net = api.create("1234567890", 4, "10.0.3.0/24", CreateSubnetOptions.builder().name("subnet-test").build());
      assertEquals(net, Subnet.builder().id("12345").tenantId("6789").name("subnet-test").networkId("1234567890").ipVersion(4).cidr("10.0.3.0/24").build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateReturns4xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets").method("POST")
            .payload(payloadFromStringWithContentType("{\"subnet\":{\"network_id\":\"1234567890\",\"ip_version\":4,\"cidr\":\"10.0.3.0/24\",\"name\":\"subnet-test\"}}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(401).build())
         .getSubnetApiForZone(ZONE);

      api.create("1234567890", 4, "10.0.3.0/24", CreateSubnetOptions.builder().name("subnet-test").build());
   }

   public void testCreateBulkReturns2xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets").method("POST")
            .payload(payloadFromStringWithContentType(
               "{\"subnets\":[" +
                  "{\"network_id\":\"1234567890\",\"ip_version\":4,\"cidr\":\"10.0.3.0/24\",\"name\":\"subnet-test\"}," +
                  "{\"network_id\":\"9876543210\",\"ip_version\":4,\"cidr\":\"192.168.3.0/24\",\"name\":\"subnet-test-2\"}" +
                  "]}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType(
            "{\"subnets\":[" +
               "{\"id\":\"1\",\"tenant_id\":\"1\",\"network_id\":\"1234567890\",\"ip_version\":4,\"cidr\":\"10.0.3.0/24\",\"name\":\"subnet-test\"}," +
               "{\"id\":\"2\",\"tenant_id\":\"1\",\"network_id\":\"9876543210\",\"ip_version\":4,\"cidr\":\"192.168.3.0/24\",\"name\":\"subnet-test-2\"}" +
               "]}", APPLICATION_JSON)).build())
         .getSubnetApiForZone(ZONE);

      Set<? extends Subnet> nets = api.createBulk(
         CreateSubnetBulkOptions.builder().subnets(
            ImmutableList.of(
               BulkSubnet.builder().networkId("1234567890").ipVersion(4).cidr("10.0.3.0/24").name("subnet-test").build(),
               BulkSubnet.builder().networkId("9876543210").ipVersion(4).cidr("192.168.3.0/24").name("subnet-test-2").build()
            )
         ).build()
      ).toSet();
      assertEquals(nets, createBulkReturns2xxResponse());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateBulkReturns4xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets").method("POST")
            .payload(payloadFromStringWithContentType(
               "{\"subnets\":[" +
                  "{\"network_id\":\"1234567890\",\"ip_version\":4,\"cidr\":\"10.0.3.0/24\",\"name\":\"subnet-test\"}," +
                  "{\"network_id\":\"9876543210\",\"ip_version\":4,\"cidr\":\"192.168.3.0/24\",\"name\":\"subnet-test-2\"}" +
                  "]}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(401).build())
         .getSubnetApiForZone(ZONE);

      api.createBulk(
         CreateSubnetBulkOptions.builder().subnets(
            ImmutableList.of(
               BulkSubnet.builder().networkId("1234567890").ipVersion(4).cidr("10.0.3.0/24").name("subnet-test").build(),
               BulkSubnet.builder().networkId("9876543210").ipVersion(4).cidr("192.168.3.0/24").name("subnet-test-2").build()
            )
         ).build()
      );
   }

   public void testUpdateReturns2xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets/12345").method("PUT")
            .payload(payloadFromStringWithContentType("{\"subnet\":{\"name\":\"another-test\",\"gateway_ip\":\"13.13.13.13\"}}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(200).build())
         .getSubnetApiForZone(ZONE);

      assertTrue(api.update("12345", UpdateSubnetOptions.builder().name("another-test").gatewayIp("13.13.13.13").build()));
   }

   public void testUpdateReturns4xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets/12345").method("PUT")
            .payload(payloadFromStringWithContentType("{\"subnet\":{\"name\":\"another-test\",\"gateway_ip\":\"13.13.13.13\"}}", APPLICATION_JSON)).build(),
         HttpResponse.builder().statusCode(404).build())
         .getSubnetApiForZone(ZONE);

      assertFalse(api.update("12345", UpdateSubnetOptions.builder().name("another-test").gatewayIp("13.13.13.13").build()));
   }

   public void testDeleteReturns2xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets/12345").method("DELETE").build(),
         HttpResponse.builder().statusCode(200).build())
         .getSubnetApiForZone(ZONE);

      assertTrue(api.delete("12345"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testDeleteReturns4xx() {
      SubnetApi api = requestsSendResponses(
         keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
         authenticatedGET().endpoint(endpoint + "/subnets/12345").method("DELETE").build(),
         HttpResponse.builder().statusCode(403).build())
         .getSubnetApiForZone(ZONE);

      api.delete("12345");
   }

   protected Set<Subnet> listOfSubnets() {
      return ImmutableSet.of(
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("wibble").tenantId("1234567890").id("1a104cf5-cb18-4d35-9407-2fd2646d9d0b").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("31083ae2-420d-48b2-ac98-9f7a4fd8dbdc").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("49c6d6fa-ff2a-459d-b975-75a8d31c9a89").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("wibble").tenantId("1234567890").id("5cb3d6f4-62cb-41c9-b964-ba7d9df79e4e").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("5d51d012-3491-4db7-b1b5-6f254015015d").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("wibble").tenantId("1234567890").id("5f9cf7dc-22ca-4097-8e49-1cc8b23faf17").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("6319ecad-6bff-48b2-9b53-02ede8cb7588").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("6ba4c788-661f-49ab-9bf8-5f10cbbb2f57").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("74ed170b-5069-4353-ab38-9719766dc57e").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("wibble").tenantId("1234567890").id("b71fcac1-e864-4031-8c5b-edbecd9ece36").build(),
         Subnet.builder().ipVersion(4).cidr("10.0.3.0/24").networkId("1234567890").name("jclouds-test").tenantId("1234567890").id("c7681895-d84d-4650-9ca0-82c72036b855").build()
      );
   }

   protected Set<Subnet> createBulkReturns2xxResponse() {
      return ImmutableSet.of(
         Subnet.builder().id("1").tenantId("1").name("subnet-test").networkId("1234567890").ipVersion(4).cidr("10.0.3.0/24").build(),
         Subnet.builder().id("2").tenantId("1").name("subnet-test-2").networkId("9876543210").ipVersion(4).cidr("192.168.3.0/24").build()
      );
   }
}
