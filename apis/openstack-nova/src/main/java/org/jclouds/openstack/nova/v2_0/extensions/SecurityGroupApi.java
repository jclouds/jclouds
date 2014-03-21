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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.binders.BindSecurityGroupRuleToJsonPayload;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides access to the OpenStack Compute (Nova) Security Group extension API.
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.SECURITY_GROUPS)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface SecurityGroupApi {
   /**
    * List all Security Groups.
    *
    * @return all Security Groups
    */
   @Named("securityGroup:list")
   @GET
   @Path("/os-security-groups")
   @SelectJson("security_groups")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<SecurityGroup> list();

   /**
    * Get a specific Security Group
    *
    * @return a specific Security Group
    */
   @Named("securityGroup:get")
   @GET
   @Path("/os-security-groups/{id}")
   @SelectJson("security_group")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SecurityGroup get(@PathParam("id") String id);

   /**
    * Create a Security Group
    *
    * @return a new Security Group
    */
   @Named("securityGroup:create")
   @POST
   @Path("/os-security-groups")
   @SelectJson("security_group")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"security_group\":%7B\"name\":\"{name}\",\"description\":\"{description}\"%7D%7D")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SecurityGroup createWithDescription(@PayloadParam("name") String name,
         @PayloadParam("description") String description);

   /**
    * Delete a Security Group.
    *
    * @return
    */
   @Named("securityGroup:delete")
   @DELETE
   @Path("/os-security-groups/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);

   /**
    * Create a Security Group Rule.
    *
    * @return a new Security Group Rule
    */
   @Named("securityGroup:create")
   @POST
   @Path("/os-security-group-rules")
   @SelectJson("security_group_rule")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindSecurityGroupRuleToJsonPayload.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SecurityGroupRule createRuleAllowingCidrBlock(
         @PayloadParam("parent_group_id") String parentGroup, Ingress ip_protocol,
         @PayloadParam("cidr") String sourceCidr);

   /**
    * Create a Security Group Rule.
    *
    * @return a new Security Group Rule
    */
   @Named("securityGroup:create")
   @POST
   @Path("/os-security-group-rules")
   @SelectJson("security_group_rule")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindSecurityGroupRuleToJsonPayload.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SecurityGroupRule createRuleAllowingSecurityGroupId(
         @PayloadParam("parent_group_id") String parentGroup, Ingress ip_protocol,
         @PayloadParam("group_id") String groupId);

   /**
    * Delete a Security Group Rule.
    *
    * @return
    */
   @Named("securityGroup:delete")
   @DELETE
   @Path("/os-security-group-rules/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteRule(@PathParam("id") String ruleId);
}
