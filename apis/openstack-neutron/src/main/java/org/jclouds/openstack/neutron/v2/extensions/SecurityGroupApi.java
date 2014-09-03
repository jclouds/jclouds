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
package org.jclouds.openstack.neutron.v2.extensions;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.neutron.v2.domain.Rule;
import org.jclouds.openstack.neutron.v2.domain.Rules;
import org.jclouds.openstack.neutron.v2.domain.SecurityGroup;
import org.jclouds.openstack.neutron.v2.domain.SecurityGroups;
import org.jclouds.openstack.neutron.v2.fallbacks.EmptyRulesFallback;
import org.jclouds.openstack.neutron.v2.fallbacks.EmptySecurityGroupsFallback;
import org.jclouds.openstack.neutron.v2.functions.ParseRules;
import org.jclouds.openstack.neutron.v2.functions.ParseSecurityGroups;
import org.jclouds.openstack.neutron.v2.functions.RulesToPagedIterable;
import org.jclouds.openstack.neutron.v2.functions.SecurityGroupsToPagedIterable;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;
import com.google.common.annotations.Beta;

/**
 * Provides access to Security Group extension operations for the OpenStack Networking (Neutron) v2 API.
 * <p/>
 * Security groups and security group rules allows administrators and tenants the ability to specify the type of
 * traffic and direction (ingress/egress) that is allowed to pass through a port. A security group is a container for
 * security group rules.
 */
@Beta
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SecurityGroupApi {
   /**
    * Groups
    */

   /**
    * @return all security groups currently defined in Neutron for the current tenant.
    */
   @Path("/security-groups")
   @Named("security-group:list")
   @GET
   @ResponseParser(ParseSecurityGroups.class)
   @Transform(SecurityGroupsToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<SecurityGroup> listSecurityGroups();

   /**
    * @return all security groups currently defined in Neutron for the current tenant.
    */
   @Path("/security-groups")
   @Named("security-group:list")
   @GET
   @ResponseParser(ParseSecurityGroups.class)
   @Fallback(EmptySecurityGroupsFallback.class)
   SecurityGroups listSecurityGroups(PaginationOptions options);

   /**
    * @param id the id of the security group to return
    * @return SecurityGroup or null if not found.
    */
   @Path("/security-groups/{id}")
   @Named("security-group:get")
   @GET
   @SelectJson("security_group")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Nullable
   SecurityGroup getSecurityGroup(@PathParam("id") String id);

   /**
    * Creates a new SecurityGroup.
    *
    * @param securityGroup Describes the security group to be created.
    * @return a reference of the newly-created security group
    */
   @Path("/security-groups")
   @Named("secuity-group:create")
   @POST
   @SelectJson("security_group")
   SecurityGroup create(@WrapWith("security_group") SecurityGroup.CreateSecurityGroup securityGroup);

   /**
    * Deletes the specified Security Group.
    *
    * @param id the id of the security group to delete
    * @return true if delete was successful, false if not
    */
   @Path("/security-groups/{id}")
   @Named("security-group:delete")
   @DELETE
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteSecurityGroup(@PathParam("id") String id);

   /**
    * Rules
    */

   /**
    * @return all security groups rules currently defined in Neutron for the current tenant.
    */
   @Path("/security-group-rules")
   @Named("security-group-rule:list")
   @GET
   @ResponseParser(ParseRules.class)
   @Transform(RulesToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Rule> listRules();

   /**
    * @return all security groups rules currently defined in Neutron for the current tenant.
    */
   @Path("/security-group-rules")
   @Named("security-group-rule:list")
   @GET
   @ResponseParser(ParseRules.class)
   @Fallback(EmptyRulesFallback.class)
   Rules listRules(PaginationOptions options);

   /**
    * @param id the id of the security group rule to return.
    * @return SecurityGroupRule or null if not found.
    */
   @Path("/security-group-rules/{id}")
   @Named("security-group-rule:get")
   @GET
   @SelectJson("security_group_rule")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Nullable
   Rule get(@PathParam("id") String id);

   /**
    * Creates a new Security Group Rule.
    *
    * @param securityGroupRule Describes the security group rule to be created.
    * @return a reference of the newly-created security group rule.
    */
   @Path("/security-group-rules")
   @Named("security-group-rule:create")
   @POST
   @SelectJson("security_group_rule")
   Rule create(@WrapWith("security_group_rule") Rule.CreateRule securityGroupRule);

   /**
    * Deletes the specified Security Group Rule.
    *
    * @param id the id of the security group rule to delete.
    * @return true if delete was successful, false if not.
    */
   @Path("/security-group-rules/{id}")
   @Named("security-group-rule:delete")
   @DELETE
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteRule(@PathParam("id") String id);
}
