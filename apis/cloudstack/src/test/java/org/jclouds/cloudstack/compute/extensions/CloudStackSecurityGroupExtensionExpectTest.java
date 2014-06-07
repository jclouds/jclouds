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
package org.jclouds.cloudstack.compute.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.compute.functions.ZoneToLocationTest;
import org.jclouds.cloudstack.internal.BaseCloudStackComputeServiceContextExpectTest;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Module;

@Test(groups = "unit", testName = "CloudStackSecurityGroupExtensionExpectTest")
public class CloudStackSecurityGroupExtensionExpectTest extends BaseCloudStackComputeServiceContextExpectTest<ComputeService> {

   protected final HttpResponse addRuleResponse = HttpResponse.builder().statusCode(200)
           .payload(payloadFromResource("/authorizesecuritygroupingressresponse.json"))
           .build();

   protected final HttpResponse revokeRuleResponse = HttpResponse.builder().statusCode(200)
           .payload(payloadFromResource("/revokesecuritygroupingressresponse.json"))
           .build();

   protected final HttpRequest queryAsyncJobResultAuthorizeIngress = HttpRequest.builder().method("GET")
           .endpoint("http://localhost:8080/client/api")
           .addQueryParam("response", "json")
           .addQueryParam("command", "queryAsyncJobResult")
           .addQueryParam("jobid", "13330fc9-8b3e-4582-aa3e-90883c041010")
           .addQueryParam("apiKey", "APIKEY")
           .addQueryParam("signature", "y4gk3ckWAMPDNZM26LUK0gAhfiE%3D")
           .addHeader("Accept", "application/json")
           .build();

   protected final HttpResponse queryAsyncJobResultAuthorizeIngressResponse = HttpResponse.builder().statusCode(200)
           .payload(payloadFromResource("/queryasyncjobresultresponse-authorizeingress.json"))
           .build();

   protected final HttpRequest getWithRule = HttpRequest.builder().method("GET")
           .endpoint("http://localhost:8080/client/api")
           .addQueryParam("response", "json")
           .addQueryParam("command", "listSecurityGroups")
           .addQueryParam("listAll", "true")
           .addQueryParam("id", "13")
           .addQueryParam("apiKey", "APIKEY")
           .addQueryParam("signature", "TmlGaO2ICM%2BiXQr88%2BZCyWUniSw%3D")
           .addHeader("Accept", "application/json")
           .build();

   protected final HttpResponse getEmptyResponse = HttpResponse.builder().statusCode(200)
           .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_empty.json"))
           .build();

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty("jclouds.zones", "MTV-Zone1");
      return overrides;
   }

   public void testListSecurityGroups() {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "listSecurityGroups")
              .addQueryParam("listAll", "true")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "o%2Bd8xxWT1Pa%2BI57SG2caFAblBYA%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/listsecuritygroupsresponse.json"))
              .build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
              .put(listTemplates, listTemplatesResponse)
              .put(listOsTypes, listOsTypesResponse)
              .put(listOsCategories, listOsCategoriesResponse)
              .put(listZones, listZonesResponse)
              .put(listServiceOfferings, listServiceOfferingsResponse)
              .put(listAccounts, listAccountsResponse)
              .put(listNetworks, listNetworksResponse)
              .put(getZone, getZoneResponse)
              .put(listSecurityGroups, listSecurityGroupsResponse)
              .build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap).getSecurityGroupExtension().get();

      Set<SecurityGroup> groups = extension.listSecurityGroups();
      assertEquals(groups.size(), 5);
   }

   public void testListSecurityGroupsForNode() {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "listSecurityGroups")
              .addQueryParam("listAll", "true")
              .addQueryParam("virtualmachineid", "some-node")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "x4f9fGMjIHXl5biaaFK5oOEONcg%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/listsecuritygroupsresponse.json"))
              .build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
              .put(listTemplates, listTemplatesResponse)
              .put(listOsTypes, listOsTypesResponse)
              .put(listOsCategories, listOsCategoriesResponse)
              .put(listZones, listZonesResponse)
              .put(listServiceOfferings, listServiceOfferingsResponse)
              .put(listAccounts, listAccountsResponse)
              .put(listNetworks, listNetworksResponse)
              .put(getZone, getZoneResponse)
              .put(listSecurityGroups, listSecurityGroupsResponse)
              .build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap).getSecurityGroupExtension().get();

      Set<SecurityGroup> groups = extension.listSecurityGroupsForNode("some-node");
      assertEquals(groups.size(), 5);
   }

   public void testGetSecurityGroupById() {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "listSecurityGroups")
              .addQueryParam("listAll", "true")
              .addQueryParam("id", "13")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "TmlGaO2ICM%2BiXQr88%2BZCyWUniSw%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid.json"))
              .build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
              .put(listTemplates, listTemplatesResponse)
              .put(listOsTypes, listOsTypesResponse)
              .put(listOsCategories, listOsCategoriesResponse)
              .put(listZones, listZonesResponse)
              .put(listServiceOfferings, listServiceOfferingsResponse)
              .put(listAccounts, listAccountsResponse)
              .put(listNetworks, listNetworksResponse)
              .put(getZone, getZoneResponse)
              .put(listSecurityGroups, listSecurityGroupsResponse)
              .build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap).getSecurityGroupExtension().get();

      SecurityGroup group = extension.getSecurityGroupById("13");
      assertEquals(group.getId(), "13");
      assertEquals(group.getIpPermissions().size(), 2);
   }

   public void testCreateSecurityGroup() {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "listSecurityGroups")
              .addQueryParam("listAll", "true")
              .addQueryParam("securitygroupname", "jclouds-test")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "zGp2rfHY6fBIGkgODRxyNzFfPFI%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse.json"))
              .build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
              .put(listTemplates, listTemplatesResponse)
              .put(listOsTypes, listOsTypesResponse)
              .put(listOsCategories, listOsCategoriesResponse)
              .put(listZones, listZonesResponse)
              .put(listServiceOfferings, listServiceOfferingsResponse)
              .put(listAccounts, listAccountsResponse)
              .put(listNetworks, listNetworksResponse)
              .put(getZoneWithSecurityGroups, getZoneWithSecurityGroupsResponse)
              .put(listSecurityGroups, listSecurityGroupsResponse)
              .put(createSecurityGroup, createSecurityGroupResponse)
              .build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap).getSecurityGroupExtension().get();

      SecurityGroup group = extension.createSecurityGroup("test", ZoneToLocationTest.two);
      assertEquals(group.getId(), "30");
      assertEquals(group.getIpPermissions().size(), 0);
   }

   @Test(expectedExceptions = UncheckedExecutionException.class,
           expectedExceptionsMessageRegExp = "java.lang.IllegalArgumentException: .* does not support security groups")
   public void testCreateSecurityGroupBadZone() {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "listSecurityGroups")
              .addQueryParam("listAll", "true")
              .addQueryParam("securitygroupname", "jclouds-test")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "zGp2rfHY6fBIGkgODRxyNzFfPFI%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse.json"))
              .build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
              .put(listTemplates, listTemplatesResponse)
              .put(listOsTypes, listOsTypesResponse)
              .put(listOsCategories, listOsCategoriesResponse)
              .put(listZones, listZonesResponse)
              .put(listServiceOfferings, listServiceOfferingsResponse)
              .put(listAccounts, listAccountsResponse)
              .put(listNetworks, listNetworksResponse)
              .put(getZone, getZoneResponse)
              .put(listSecurityGroups, listSecurityGroupsResponse)
              .put(createSecurityGroup, createSecurityGroupResponse)
              .build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap).getSecurityGroupExtension().get();

      SecurityGroup group = extension.createSecurityGroup("test", ZoneToLocationTest.one);
      assertEquals(group.getId(), "30");
      assertEquals(group.getIpPermissions().size(), 0);
   }

   public void testRemoveSecurityGroup() {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "listSecurityGroups")
              .addQueryParam("listAll", "true")
              .addQueryParam("id", "13")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "TmlGaO2ICM%2BiXQr88%2BZCyWUniSw%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_empty.json"))
              .build();

      HttpRequest deleteSecurityGroup = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "deleteSecurityGroup")
              .addQueryParam("id", "13")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "S1A2lYR/ibf4%2BHGFxVLdZvXZujQ%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse deleteSecurityGroupResponse = HttpResponse.builder()
              .statusCode(200)
              .payload(payloadFromResource("/deletesecuritygroupresponse.json"))
              .build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
              .put(listTemplates, listTemplatesResponse)
              .put(listOsTypes, listOsTypesResponse)
              .put(listOsCategories, listOsCategoriesResponse)
              .put(listZones, listZonesResponse)
              .put(listServiceOfferings, listServiceOfferingsResponse)
              .put(listAccounts, listAccountsResponse)
              .put(listNetworks, listNetworksResponse)
              .put(listSecurityGroups, listSecurityGroupsResponse)
              .put(deleteSecurityGroup, deleteSecurityGroupResponse)
              .build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap).getSecurityGroupExtension().get();

      assertTrue(extension.removeSecurityGroup("13"), "Did not remove security group");
   }

   public void testRemoveSecurityGroupDoesNotExist() {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "listSecurityGroups")
              .addQueryParam("listAll", "true")
              .addQueryParam("id", "14")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "pWQ30A6l5qh4eaNypGwM9FoLnUM%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse.json"))
              .build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
              .put(listTemplates, listTemplatesResponse)
              .put(listOsTypes, listOsTypesResponse)
              .put(listOsCategories, listOsCategoriesResponse)
              .put(listZones, listZonesResponse)
              .put(listServiceOfferings, listServiceOfferingsResponse)
              .put(listAccounts, listAccountsResponse)
              .put(listNetworks, listNetworksResponse)
              .put(listSecurityGroups, listSecurityGroupsResponse)
              .build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap).getSecurityGroupExtension().get();

      assertFalse(extension.removeSecurityGroup("14"), "Should not have found security group to remove");
   }

   public void testAddIpPermissionCidrFromIpPermission() {
      HttpRequest addRule = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "authorizeSecurityGroupIngress")
              .addQueryParam("securitygroupid", "13")
              .addQueryParam("protocol", "UDP")
              .addQueryParam("startport", "11")
              .addQueryParam("endport", "11")
              .addQueryParam("cidrlist", "1.1.1.1/24")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "XyokGNutHwcyU7KQVFZOTHvc4RY%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse getWithRuleResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_with_cidr.json"))
              .build();

      SecurityGroupExtension extension = orderedRequestsSendResponses(
              ImmutableList.of(addRule, queryAsyncJobResultAuthorizeIngress, getWithRule),
              ImmutableList.of(addRuleResponse, queryAsyncJobResultAuthorizeIngressResponse, getWithRuleResponse)
      ).getSecurityGroupExtension().get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.UDP);
      builder.fromPort(11);
      builder.toPort(11);
      builder.cidrBlock("1.1.1.1/24");

      IpPermission perm = builder.build();

      SecurityGroup origGroup = new SecurityGroupBuilder().id("13").build();

      SecurityGroup newGroup = extension.addIpPermission(perm, origGroup);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());

      assertNotNull(newPerm);
      assertEquals(newPerm.getIpProtocol(), IpProtocol.UDP);
      assertEquals(newPerm.getFromPort(), 11);
      assertEquals(newPerm.getToPort(), 11);
      assertEquals(newPerm.getCidrBlocks().size(), 1);
      assertTrue(newPerm.getCidrBlocks().contains("1.1.1.1/24"));
   }

   public void testAddIpPermissionCidrFromParams() {
      HttpRequest addRule = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "authorizeSecurityGroupIngress")
              .addQueryParam("securitygroupid", "13")
              .addQueryParam("protocol", "UDP")
              .addQueryParam("startport", "11")
              .addQueryParam("endport", "11")
              .addQueryParam("cidrlist", "1.1.1.1/24")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "XyokGNutHwcyU7KQVFZOTHvc4RY%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse getWithRuleResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_with_cidr.json"))
              .build();

      SecurityGroupExtension extension = orderedRequestsSendResponses(
              ImmutableList.of(addRule, queryAsyncJobResultAuthorizeIngress, getWithRule),
              ImmutableList.of(addRuleResponse, queryAsyncJobResultAuthorizeIngressResponse, getWithRuleResponse)
      ).getSecurityGroupExtension().get();

      SecurityGroup origGroup = new SecurityGroupBuilder().id("13").build();

      SecurityGroup newGroup = extension.addIpPermission(IpProtocol.UDP, 11, 11, emptyMultimap(),
              ImmutableSet.of("1.1.1.1/24"), emptyStringSet(), origGroup);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());

      assertNotNull(newPerm);
      assertEquals(newPerm.getIpProtocol(), IpProtocol.UDP);
      assertEquals(newPerm.getFromPort(), 11);
      assertEquals(newPerm.getToPort(), 11);
      assertEquals(newPerm.getCidrBlocks().size(), 1);
      assertTrue(newPerm.getCidrBlocks().contains("1.1.1.1/24"));
   }

   public void testAddIpPermissionGroupFromIpPermission() {
      HttpRequest addRule = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "authorizeSecurityGroupIngress")
              .addQueryParam("securitygroupid", "13")
              .addQueryParam("protocol", "TCP")
              .addQueryParam("startport", "22")
              .addQueryParam("endport", "22")
              .addQueryParam("usersecuritygrouplist[0].account", "adrian")
              .addQueryParam("usersecuritygrouplist[0].group", "adriancole")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "v2OgKc2IftwX9pfKq2Pw/Z2xh9w%3D")
              .addHeader("Accept", "application/json")
              .build();


      HttpResponse getWithRuleResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_with_group.json"))
              .build();

      SecurityGroupExtension extension = orderedRequestsSendResponses(
              ImmutableList.of(addRule, queryAsyncJobResultAuthorizeIngress, getWithRule),
              ImmutableList.of(addRuleResponse, queryAsyncJobResultAuthorizeIngressResponse, getWithRuleResponse)
      ).getSecurityGroupExtension().get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(22);
      builder.toPort(22);
      builder.tenantIdGroupNamePair("adrian", "adriancole");

      IpPermission perm = builder.build();

      SecurityGroup origGroup = new SecurityGroupBuilder().id("13").build();

      SecurityGroup newGroup = extension.addIpPermission(perm, origGroup);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());

      assertNotNull(newPerm);
      assertEquals(newPerm.getIpProtocol(), IpProtocol.TCP);
      assertEquals(newPerm.getFromPort(), 22);
      assertEquals(newPerm.getToPort(), 22);
      assertEquals(newPerm.getCidrBlocks().size(), 0);
      assertEquals(newPerm.getTenantIdGroupNamePairs().size(), 1);
      assertTrue(newPerm.getTenantIdGroupNamePairs().containsEntry("adrian", "adriancole"));
   }

   public void testAddIpPermissionGroupFromParams() {
      HttpRequest addRule = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "authorizeSecurityGroupIngress")
              .addQueryParam("securitygroupid", "13")
              .addQueryParam("protocol", "TCP")
              .addQueryParam("startport", "22")
              .addQueryParam("endport", "22")
              .addQueryParam("usersecuritygrouplist[0].account", "adrian")
              .addQueryParam("usersecuritygrouplist[0].group", "adriancole")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "v2OgKc2IftwX9pfKq2Pw/Z2xh9w%3D")
              .addHeader("Accept", "application/json")
              .build();


      HttpResponse getWithRuleResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_with_group.json"))
              .build();

      SecurityGroupExtension extension = orderedRequestsSendResponses(
              ImmutableList.of(addRule, queryAsyncJobResultAuthorizeIngress, getWithRule),
              ImmutableList.of(addRuleResponse, queryAsyncJobResultAuthorizeIngressResponse, getWithRuleResponse)
      ).getSecurityGroupExtension().get();

      ImmutableMultimap.Builder<String, String> permBuilder = ImmutableMultimap.builder();
      permBuilder.put("adrian", "adriancole");

      SecurityGroup origGroup = new SecurityGroupBuilder().id("13").build();

      SecurityGroup newGroup = extension.addIpPermission(IpProtocol.TCP, 22, 22,
              permBuilder.build(), emptyStringSet(), emptyStringSet(), origGroup);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());

      assertNotNull(newPerm);
      assertEquals(newPerm.getIpProtocol(), IpProtocol.TCP);
      assertEquals(newPerm.getFromPort(), 22);
      assertEquals(newPerm.getToPort(), 22);
      assertEquals(newPerm.getCidrBlocks().size(), 0);
      assertEquals(newPerm.getTenantIdGroupNamePairs().size(), 1);
      assertTrue(newPerm.getTenantIdGroupNamePairs().containsEntry("adrian", "adriancole"));
   }

   public void testRemoveIpPermissionCidrFromIpPermission() {
      HttpRequest revokeRule = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "revokeSecurityGroupIngress")
              .addQueryParam("id", "6")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "H7cY/MEYGN7df1hiz0mMAFVBfa8%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse getWithRuleResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_with_cidr.json"))
              .build();

      SecurityGroupExtension extension = orderedRequestsSendResponses(
              ImmutableList.of(getWithRule, revokeRule, queryAsyncJobResultAuthorizeIngress, getWithRule),
              ImmutableList.of(getWithRuleResponse, revokeRuleResponse,
                      queryAsyncJobResultAuthorizeIngressResponse, getEmptyResponse)
      ).getSecurityGroupExtension().get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.UDP);
      builder.fromPort(11);
      builder.toPort(11);
      builder.cidrBlock("1.1.1.1/24");

      IpPermission perm = builder.build();

      SecurityGroup origGroup = new SecurityGroupBuilder().id("13").build();

      SecurityGroup newGroup = extension.removeIpPermission(perm, origGroup);

      assertEquals(newGroup.getIpPermissions().size(), 0);
   }

   public void testRemoveIpPermissionCidrFromParams() {
      HttpRequest revokeRule = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "revokeSecurityGroupIngress")
              .addQueryParam("id", "6")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "H7cY/MEYGN7df1hiz0mMAFVBfa8%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse getWithRuleResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_with_cidr.json"))
              .build();

      SecurityGroupExtension extension = orderedRequestsSendResponses(
              ImmutableList.of(getWithRule, revokeRule, queryAsyncJobResultAuthorizeIngress, getWithRule),
              ImmutableList.of(getWithRuleResponse, revokeRuleResponse,
                      queryAsyncJobResultAuthorizeIngressResponse, getEmptyResponse)
      ).getSecurityGroupExtension().get();


      SecurityGroup origGroup = new SecurityGroupBuilder().id("13").build();

      SecurityGroup newGroup = extension.removeIpPermission(IpProtocol.UDP, 11, 11, emptyMultimap(),
              ImmutableSet.of("1.1.1.1/24"), emptyStringSet(), origGroup);

      assertEquals(newGroup.getIpPermissions().size(), 0);
   }

   public void testRemoveIpPermissionGroupFromIpPermission() {
      HttpRequest revokeRule = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "revokeSecurityGroupIngress")
              .addQueryParam("id", "5")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "bEzvrLtO7aEWkIqJgUeTnd%2B0XbY%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse getWithRuleResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_with_group.json"))
              .build();

      SecurityGroupExtension extension = orderedRequestsSendResponses(
              ImmutableList.of(getWithRule, revokeRule, queryAsyncJobResultAuthorizeIngress, getWithRule),
              ImmutableList.of(getWithRuleResponse, revokeRuleResponse,
                      queryAsyncJobResultAuthorizeIngressResponse, getEmptyResponse)
      ).getSecurityGroupExtension().get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(22);
      builder.toPort(22);
      builder.tenantIdGroupNamePair("adrian", "adriancole");

      IpPermission perm = builder.build();

      SecurityGroup origGroup = new SecurityGroupBuilder().id("13").build();

      SecurityGroup newGroup = extension.removeIpPermission(perm, origGroup);

      assertEquals(newGroup.getIpPermissions().size(), 0);
   }

   public void testRemoveIpPermissionGroupFromParams() {
      HttpRequest revokeRule = HttpRequest.builder().method("GET")
              .endpoint("http://localhost:8080/client/api")
              .addQueryParam("response", "json")
              .addQueryParam("command", "revokeSecurityGroupIngress")
              .addQueryParam("id", "5")
              .addQueryParam("apiKey", "APIKEY")
              .addQueryParam("signature", "bEzvrLtO7aEWkIqJgUeTnd%2B0XbY%3D")
              .addHeader("Accept", "application/json")
              .build();

      HttpResponse getWithRuleResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/getsecuritygroupresponse_extension_byid_with_group.json"))
              .build();

      SecurityGroupExtension extension = orderedRequestsSendResponses(
              ImmutableList.of(getWithRule, revokeRule, queryAsyncJobResultAuthorizeIngress, getWithRule),
              ImmutableList.of(getWithRuleResponse, revokeRuleResponse,
                      queryAsyncJobResultAuthorizeIngressResponse, getEmptyResponse)
      ).getSecurityGroupExtension().get();

      ImmutableMultimap.Builder<String, String> permBuilder = ImmutableMultimap.builder();
      permBuilder.put("adrian", "adriancole");

      SecurityGroup origGroup = new SecurityGroupBuilder().id("13").build();

      SecurityGroup newGroup = extension.removeIpPermission(IpProtocol.TCP, 22, 22,
              permBuilder.build(), emptyStringSet(), emptyStringSet(), origGroup);

      assertEquals(newGroup.getIpPermissions().size(), 0);
   }

   @Override
   public ComputeService createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return clientFrom(createInjector(fn, module, props).getInstance(CloudStackContext.class));
   }

   @Override
   protected ComputeService clientFrom(CloudStackContext context) {
      return context.getComputeService();
   }

   private Multimap<String, String> emptyMultimap() {
      return LinkedHashMultimap.create();
   }

   private Set<String> emptyStringSet() {
      return Sets.newLinkedHashSet();
   }

}
