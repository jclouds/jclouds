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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.neutron.v2.domain.CreateFirewall;
import org.jclouds.openstack.neutron.v2.domain.CreateFirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.CreateFirewallRule;
import org.jclouds.openstack.neutron.v2.domain.Firewall;
import org.jclouds.openstack.neutron.v2.domain.FirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.FirewallRule;
import org.jclouds.openstack.neutron.v2.domain.UpdateFirewall;
import org.jclouds.openstack.neutron.v2.domain.UpdateFirewallPolicy;
import org.jclouds.openstack.neutron.v2.domain.UpdateFirewallRule;
import org.jclouds.openstack.neutron.v2.functions.FirewallPolicyToPagedIterable;
import org.jclouds.openstack.neutron.v2.functions.FirewallRuleToPagedIterable;
import org.jclouds.openstack.neutron.v2.functions.FirewallToPagedIterable;
import org.jclouds.openstack.neutron.v2.functions.ParseFirewallPolicies;
import org.jclouds.openstack.neutron.v2.functions.ParseFirewallRules;
import org.jclouds.openstack.neutron.v2.functions.ParseFirewalls;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;

/**
 * The FWaaS extension provides OpenStack users with the ability to deploy firewalls to protect their networks.
 * <p/>
 *
 * @see <a href=
 *      "http://specs.openstack.org/openstack/neutron-specs/specs/api/firewall_as_a_service__fwaas_.html">api doc</a>
 */
@Beta
@Path("/fw")
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Extension(of = ServiceType.NETWORK, namespace = ExtensionNamespaces.FWAAS)
public interface FWaaSApi {

   /**
    * Returns the list of all routers currently defined in Neutron for the current tenant. The list provides the unique
    * identifier of each firewall configured for the tenant
    *
    * @return the list of all firewall references configured for the tenant.
    */
   @Named("fw:list")
   @GET
   @Transform(FirewallToPagedIterable.class)
   @ResponseParser(ParseFirewalls.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @Path("/firewalls")
   PagedIterable<Firewall> list();

   /**
    * @return the list of all firewall references configured for the tenant.
    */
   @Named("firewall:list")
   @GET
   @ResponseParser(ParseFirewalls.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/firewalls")
   PaginatedCollection<Firewall> list(PaginationOptions options);

   /**
    * Returns the details for a specific firewall.
    *
    * @param id the id of the firewall to return
    * @return firewall or empty if not found
    */
   @Named("firewall:get")
   @GET
   @Path("/firewalls/{id}")
   @SelectJson("firewall")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Firewall get(@PathParam("id") String id);

   /**
    * Create a new firewall
    *
    * @param firewall Options for creating a firewall
    * @return the newly created firewall
    */
   @Named("firewall:create")
   @POST
   @SelectJson("firewall")
   @Path("/firewalls")
   Firewall create(@WrapWith("firewall") CreateFirewall firewall);

   /**
    * Update a firewall
    *
    * @param id the id of the firewall to update
    * @param updateFirewall Contains only the attributes to update
    * @return The modified firewall
    */
   @Named("firewall:update")
   @PUT
   @Path("/firewalls/{id}")
   @SelectJson("firewall")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Firewall update(@PathParam("id") String id, @WrapWith("firewall") UpdateFirewall updateFirewall);

   /**
    * Deletes the specified firewall
    *
    * @param id the id of the firewall to delete
    * @return true if delete successful, false if not
    */
   @Named("firewall:delete")
   @DELETE
   @Path("/firewalls/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);

   @Named("firewall:createPolicy")
   @POST
   @SelectJson("firewall_policy")
   @Path("/firewall_policies")
   FirewallPolicy createFirewallPolicy(@WrapWith("firewall_policy") CreateFirewallPolicy firewallPolicy);

   @Named("firewall:listPolicies")
   @GET
   @Transform(FirewallPolicyToPagedIterable.class)
   @ResponseParser(ParseFirewallPolicies.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @Path("/firewall_policies")
   PagedIterable<FirewallPolicy> listFirewallPolicies();

   @Named("firewall:listPolicies")
   @GET
   @ResponseParser(ParseFirewallPolicies.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/firewall_policies")
   PaginatedCollection<FirewallPolicy> listFirewallPolicies(PaginationOptions options);

   @Named("firewall:getPolicy")
   @GET
   @SelectJson("firewall_policy")
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/firewall_policies/{id}")
   FirewallPolicy getFirewallPolicy(@PathParam("id") String id);

   @Named("firewall:updatePolicy")
   @PUT
   @SelectJson("firewall_policy")
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/firewall_policies/{id}")
   FirewallPolicy updateFirewallPolicy(@PathParam("id") String id, @WrapWith("firewall_policy") UpdateFirewallPolicy updateFirewallPolicy);

   @Named("firewall:deletePolicy")
   @DELETE
   @Path("/firewall_policies/{id}")
   boolean deleteFirewallPolicy(@PathParam("id") String id);

   @Named("firewall:createFirewallRule")
   @POST
   @SelectJson("firewall_rule")
   @Path("/firewall_rules")
   FirewallRule createFirewallRule(@WrapWith("firewall_rule") CreateFirewallRule firewallRule);

   @Named("firewall:listFirewallRules")
   @GET
   @Transform(FirewallRuleToPagedIterable.class)
   @ResponseParser(ParseFirewallRules.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @Path("/firewall_rules")
   PagedIterable<FirewallRule> listFirewallRules();

   @Named("firewall:listFirewallRules")
   @GET
   @ResponseParser(ParseFirewallRules.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/firewall_rules")
   PaginatedCollection<FirewallRule> listFirewallRules(PaginationOptions options);

   @Named("firewall:getFirewallRule")
   @GET
   @Path("/firewall_rules/{id}")
   @SelectJson("firewall_rule")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   FirewallRule getFirewallRule(@PathParam("id") String firewallRuleId);

   @Named("firewall:updateFirewallRule")
   @PUT
   @Path("/firewall_rules/{id}")
   @SelectJson("firewall_rule")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   FirewallRule updateFirewallRule(@PathParam("id") String id, @WrapWith("firewall_rule") UpdateFirewallRule updateFirewallRule);

   @Named("firewall:deleteFirewallRule")
   @DELETE
   @Path("/firewall_rules/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteFirewallRule(@PathParam("id") String id);

   @Named("firewall:insertFirewallRuleToPolicy")
   @PUT
   @Path("/firewall_policies/{id}/insert_rule")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   FirewallPolicy insertFirewallRuleToPolicy(@PathParam("id") String policyId, @WrapWith("firewall_rule_id") String firewallRuleId);

   @Named("firewall:removeFirewallRuleFromPolicy")
   @DELETE
   @Path("/firewall_policies/{id}/remove_rule")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   FirewallPolicy removeFirewallRuleFromPolicy(@PathParam("id") String policyId, @WrapWith("firewall_rule_id") String firewallRuleId);

}
