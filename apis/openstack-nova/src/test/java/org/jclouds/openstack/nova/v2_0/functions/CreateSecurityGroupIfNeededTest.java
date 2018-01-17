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
package org.jclouds.openstack.nova.v2_0.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Injector;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.openstack.nova.v2_0.compute.functions.NeutronSecurityGroupToSecurityGroup;
import org.jclouds.openstack.nova.v2_0.compute.functions.NovaSecurityGroupInRegionToSecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.RegionSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.domain.regionscoped.SecurityGroupInRegion;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "CreateSecurityGroupIfNeededTest")
public class CreateSecurityGroupIfNeededTest extends BaseNovaApiExpectTest {
   HttpRequest create = HttpRequest.builder().method("POST").endpoint(
            URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-security-groups")).headers(
            ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                     authToken).build())
            .payload(
                     payloadFromStringWithContentType(
                              "{\"security_group\":{\"name\":\"jclouds_mygroup\",\"description\":\"jclouds_mygroup\"}}",
                              "application/json")).build();

   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("openstack-nova").description(
           "openstack-nova").build();
   Location region = new LocationBuilder().id("az-1.region-a.geo-1").description("az-1.region-a.geo-1").scope(
           LocationScope.REGION).parent(provider).build();
   Supplier<Map<String, Location>> locationIndex = Suppliers.<Map<String, Location>> ofInstance(ImmutableMap
           .<String, Location> of("az-1.region-a.geo-1", region));

   Function<SecurityGroupInRegion, org.jclouds.compute.domain.SecurityGroup> securityGroupInRegionSecurityGroupFunction = new NovaSecurityGroupInRegionToSecurityGroup(locationIndex);

   Injector injector = createInjector(Functions.forMap(ImmutableMap.<HttpRequest, HttpResponse>of()), createModule(), setupProperties());
   NeutronSecurityGroupToSecurityGroup.Factory factory = injector.getInstance(NeutronSecurityGroupToSecurityGroup.Factory.class);

   private final int groupId = 2769;

   public void testCreateNewGroup() throws Exception {

      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.builder();

      builder.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      builder.put(extensionsOfNovaRequest, extensionsOfNovaResponse);

      HttpResponse createResponse = HttpResponse.builder().statusCode(200)
               .payload(
                        payloadFromStringWithContentType(
                                 String.format("{\"security_group\": {\"rules\": [], \"tenant_id\": \"37936628937291\", \"id\": %s, \"name\": \"jclouds_mygroup\", \"description\": \"jclouds_mygroup\"}}", groupId),
                                 "application/json; charset=UTF-8")).build();

      builder.put(create, createResponse);

      int ruleId = 10331;

      for (int port : ImmutableList.of(22, 8080)) {

         HttpRequest createCidrRule = HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-security-group-rules")).headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                           authToken).build())
                  .payload(
                           payloadFromStringWithContentType(
                                    String.format("{\"security_group_rule\":{\"parent_group_id\":\"%s\",\"cidr\":\"0.0.0.0/0\",\"ip_protocol\":\"tcp\",\"from_port\":\"%d\",\"to_port\":\"%d\"}}",
                                                      groupId, port, port), "application/json")).build();

         HttpResponse createCidrRuleResponse = HttpResponse.builder().statusCode(200)
                  .payload(
                           payloadFromStringWithContentType(
                                    String.format("{\"security_group_rule\": {\"from_port\": %d, \"group\": {}, \"ip_protocol\": \"tcp\", \"to_port\": %d, \"parent_group_id\": %d, \"ip_range\": {\"cidr\": \"0.0.0.0/0\"}, \"id\": %d}}",
                                             port, port, groupId, ruleId++), "application/json; charset=UTF-8")).build();

         builder.put(createCidrRule, createCidrRuleResponse);

         HttpRequest createSelfRule = HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-security-group-rules")).headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                           authToken).build())
                  .payload(
                           payloadFromStringWithContentType(
                                    String.format("{\"security_group_rule\":{\"group_id\":\"%d\",\"parent_group_id\":\"%d\",\"ip_protocol\":\"tcp\",\"from_port\":\"%d\",\"to_port\":\"%d\"}}",
                                                      groupId, groupId, port, port), "application/json")).build();

         // note server responds with group name in the rule!!
         HttpResponse createSelfRuleResponse = HttpResponse.builder().statusCode(200)
                  .payload(
                           payloadFromStringWithContentType(
                                    String.format("{\"security_group_rule\": {\"from_port\": %d, \"group\": {\"tenant_id\": \"37936628937291\", \"name\": \"jclouds_mygroup\"}, \"ip_protocol\": \"tcp\", \"to_port\": %d, \"parent_group_id\": %d, \"ip_range\": {}, \"id\": %d}}",
                                             port, port, groupId, ruleId++), "application/json; charset=UTF-8")).build();

         builder.put(createSelfRule, createSelfRuleResponse);
      }

      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-security-groups/" + groupId)).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_details_computeservice_typical.json")).build();

      builder.put(getSecurityGroup, getSecurityGroupResponse);


      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET").endpoint(
         URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-security-groups")).headers(
         ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
            authToken).build()).build();
      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200).payload(
         payloadFromResource("/securitygroup_list_details_computeservice_typical.json")).build();
      builder.put(listSecurityGroups, listSecurityGroupsResponse);

      NovaApi apiCanCreateSecurityGroup = requestsSendResponses(builder.build());

      CreateSecurityGroupIfNeeded fn = new CreateSecurityGroupIfNeeded(apiCanCreateSecurityGroup, locationIndex, securityGroupInRegionSecurityGroupFunction, factory);

      // we can find it
      org.jclouds.compute.domain.SecurityGroup expected = new SecurityGroupBuilder()
              .id("az-1.region-a.geo-1/2769")
              .providerId("2769")
              .name("jclouds_mygroup")
              .location(locationIndex.get().get("az-1.region-a.geo-1"))
              .ipPermissions(ImmutableList.of(
                      IpPermission.builder()
                      .ipProtocol(IpProtocol.TCP)
                      .fromPort(22)
                      .toPort(22)
                      .cidrBlock("0.0.0.0/0")
                      .build(),
                      IpPermission.builder()
                              .ipProtocol(IpProtocol.TCP)
                              .fromPort(22)
                              .toPort(22)
                              .groupIds(ImmutableList.of("az-1.region-a.geo-1/2769"))
                              .build(),
                      IpPermission.builder()
                              .ipProtocol(IpProtocol.TCP)
                              .fromPort(8080)
                              .toPort(8080)
                              .cidrBlock("0.0.0.0/0")
                              .build(),
                      IpPermission.builder()
                              .ipProtocol(IpProtocol.TCP)
                              .fromPort(8080)
                              .toPort(8080)
                              .groupIds(ImmutableList.of("az-1.region-a.geo-1/2769"))
                              .build()
              )
      )
      .build();

      assertEquals(
              fn.apply(new RegionSecurityGroupNameAndPorts("az-1.region-a.geo-1", "jclouds_mygroup", ImmutableSet.of(22, 8080))).toString(),
              expected.toString().trim());

   }

   public void testReturnExistingGroupOnAlreadyExists() throws Exception {

      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.builder();

      builder.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      builder.put(extensionsOfNovaRequest, extensionsOfNovaResponse);

      HttpResponse createResponse = HttpResponse.builder().statusCode(400)
               .payload(
                        payloadFromStringWithContentType(
                                 "{\"badRequest\": {\"message\": \"Security group test already exists\", \"code\": 400}}",
                                 "application/json; charset=UTF-8")).build();

      builder.put(create, createResponse);

      HttpRequest list = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v2/3456/os-security-groups")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_list_details_computeservice_typical.json")).build();

      builder.put(list, listResponse);

      NovaApi apiWhenSecurityGroupsExist = requestsSendResponses(builder.build());

      CreateSecurityGroupIfNeeded fn = new CreateSecurityGroupIfNeeded(apiWhenSecurityGroupsExist, locationIndex, securityGroupInRegionSecurityGroupFunction, factory);

      // we can find it
      org.jclouds.compute.domain.SecurityGroup expected = new SecurityGroupBuilder()
              .id("az-1.region-a.geo-1/2769")
              .providerId("2769")
              .name("jclouds_mygroup")
              .location(locationIndex.get().get("az-1.region-a.geo-1"))
              .ipPermissions(ImmutableList.of(
                      IpPermission.builder()
                              .ipProtocol(IpProtocol.TCP)
                              .fromPort(22)
                              .toPort(22)
                              .cidrBlock("0.0.0.0/0")
                              .build(),
                      IpPermission.builder()
                              .ipProtocol(IpProtocol.TCP)
                              .fromPort(22)
                              .toPort(22)
                              .groupIds(ImmutableList.of("az-1.region-a.geo-1/2769"))
                              .build(),
                      IpPermission.builder()
                              .ipProtocol(IpProtocol.TCP)
                              .fromPort(8080)
                              .toPort(8080)
                              .cidrBlock("0.0.0.0/0")
                              .build(),
                      IpPermission.builder()
                              .ipProtocol(IpProtocol.TCP)
                              .fromPort(8080)
                              .toPort(8080)
                              .groupIds(ImmutableList.of("az-1.region-a.geo-1/2769"))
                              .build()
                      )
              )
              .build();
      assertEquals(
              fn.apply(new RegionSecurityGroupNameAndPorts("az-1.region-a.geo-1", "jclouds_mygroup", ImmutableSet.of(22, 8080))).toString(),
              expected.toString().trim()
      );
   }
}
