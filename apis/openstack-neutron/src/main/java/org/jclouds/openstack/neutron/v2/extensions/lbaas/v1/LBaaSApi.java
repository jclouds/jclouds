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
package org.jclouds.openstack.neutron.v2.extensions.lbaas.v1;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.HealthMonitor;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.HealthMonitors;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Member;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Members;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Pool;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Pools;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.VIP;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.VIPs;
import org.jclouds.openstack.neutron.v2.extensions.ExtensionNamespaces;
import org.jclouds.openstack.neutron.v2.fallbacks.lbaas.v1.EmptyHealthMonitorsFallback;
import org.jclouds.openstack.neutron.v2.fallbacks.lbaas.v1.EmptyMembersFallback;
import org.jclouds.openstack.neutron.v2.fallbacks.lbaas.v1.EmptyPoolsFallback;
import org.jclouds.openstack.neutron.v2.fallbacks.lbaas.v1.EmptyVIPsFallback;
import org.jclouds.openstack.neutron.v2.functions.lbaas.v1.HealthMonitorsToPagedIterable;
import org.jclouds.openstack.neutron.v2.functions.lbaas.v1.MembersToPagedIterable;
import org.jclouds.openstack.neutron.v2.functions.lbaas.v1.ParseHealthMonitors;
import org.jclouds.openstack.neutron.v2.functions.lbaas.v1.ParseMembers;
import org.jclouds.openstack.neutron.v2.functions.lbaas.v1.ParsePools;
import org.jclouds.openstack.neutron.v2.functions.lbaas.v1.ParseVIPs;
import org.jclouds.openstack.neutron.v2.functions.lbaas.v1.PoolsToPagedIterable;
import org.jclouds.openstack.neutron.v2.functions.lbaas.v1.VIPsToPagedIterable;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;

/**
 * Provides access to load-balancing operations for the OpenStack Networking (Neutron) v2 API.
 * <p/>
 * LBaaS v1 is an extension to load-balance the traffic between instances and external networks.
 */
@Beta
@Extension(of = ServiceType.NETWORK, namespace = ExtensionNamespaces.LBAAS)
@Path("/lb")
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface LBaaSApi {

   /**
    * Returns a list of VIPs to which the tenant has access. Default policy settings return only
    * those VIPs that are owned by the tenant who submits the request, unless the request is submitted by an
    * user with administrative rights.
    *
    * @return the list of all VIP references configured for the tenant.
    */
   @Named("vip:list")
   @GET
   @Path("/vips")
   @Transform(VIPsToPagedIterable.class)
   @ResponseParser(ParseVIPs.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<VIP> listVIPs();

   /**
    * @return the list of all VIP references configured for the tenant.
    */
   @Named("vip:list")
   @GET
   @Path("/vips")
   @ResponseParser(ParseVIPs.class)
   @Fallback(EmptyVIPsFallback.class)
   VIPs listVIPs(PaginationOptions options);

   /**
    * Returns the details for a specific VIP.
    *
    * @param id the id of the VIP to return.
    * @return VIP or null if not found.
    */
   @Named("vip:get")
   @GET
   @Path("/vips/{id}")
   @SelectJson("vip")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   VIP getVIP(@PathParam("id") String id);

   /**
    * Creates a new VIP.
    *
    * @param vip describes the VIP to be created.
    * @return a reference of the newly-created VIP.
    */
   @Named("vip:create")
   @POST
   @Path("/vips")
   @SelectJson("vip")
   VIP createVIP(@WrapWith("vip") VIP.CreateVIP vip);

   /**
    * Update a VIP.
    *
    * @param id the id of the VIP to update.
    * @param vip the VIP's attributes to update.
    * @return a reference of the updated VIP.
    */
   @Named("vip:update")
   @PUT
   @Path("/vips/{id}")
   @SelectJson("vip")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   VIP updateVIP(@PathParam("id") String id, @WrapWith("vip") VIP.UpdateVIP vip);

   /**
    * Deletes the specified VIP.
    *
    * @param id the id of the VIP to delete.
    * @return true if delete successful, false if not.
    */
   @Named("vip:delete")
   @DELETE
   @Path("/vips/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteVIP(@PathParam("id") String id);

   /**
    * Returns a list of Pools to which the tenant has access. Default policy settings return only
    * those Pools that are owned by the tenant who submits the request, unless the request is submitted by an
    * user with administrative rights.
    *
    * @return the list of all Pool references configured for the tenant.
    */
   @Named("pool:list")
   @GET
   @Path("/pools")
   @Transform(PoolsToPagedIterable.class)
   @ResponseParser(ParsePools.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Pool> listPools();

   /**
    * @return the list of all Pool references configured for the tenant.
    */
   @Named("pool:list")
   @GET
   @Path("/pools")
   @ResponseParser(ParsePools.class)
   @Fallback(EmptyPoolsFallback.class)
   Pools listPools(PaginationOptions options);

   /**
    * Returns the details for a specific Pool.
    *
    * @param id the id of the Pool to return.
    * @return Pool or null if not found.
    */
   @Named("pool:get")
   @GET
   @Path("/pools/{id}")
   @SelectJson("pool")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Pool getPool(@PathParam("id") String id);

   /**
    * Creates a new Pool.
    *
    * @param pool describes the Pool to be created.
    * @return a reference of the newly-created Pool.
    */
   @Named("pool:create")
   @POST
   @Path("/pools")
   @SelectJson("pool")
   Pool createPool(@WrapWith("pool") Pool.CreatePool pool);

   /**
    * Update a Pool.
    *
    * @param id the id of the Pool to update.
    * @param pool the Pool's attributes to update.
    * @return a reference of the updated Pool.
    */
   @Named("pool:update")
   @PUT
   @Path("/pools/{id}")
   @SelectJson("pool")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Pool updatePool(@PathParam("id") String id, @WrapWith("pool") Pool.UpdatePool pool);

   /**
    * Deletes the specified Pool.
    *
    * @param id the id of the Pool to delete.
    * @return true if delete successful, false if not.
    */
   @Named("pool:delete")
   @DELETE
   @Path("/pools/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deletePool(@PathParam("id") String id);

   /**
    * Returns a list of Members to which the tenant has access. Default policy settings return only
    * those Members that are owned by the tenant who submits the request, unless the request is submitted by an
    * user with administrative rights.
    *
    * @return the list of all Member references configured for the tenant.
    */
   @Named("member:list")
   @GET
   @Path("/members")
   @Transform(MembersToPagedIterable.class)
   @ResponseParser(ParseMembers.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Member> listMembers();

   /**
    * @return the list of all Member references configured for the tenant.
    */
   @Named("member:list")
   @GET
   @Path("/members")
   @ResponseParser(ParseMembers.class)
   @Fallback(EmptyMembersFallback.class)
   Members listMembers(PaginationOptions options);

   /**
    * Returns the details for a specific Member.
    *
    * @param id the id of the Member to return.
    * @return Member or null if not found.
    */
   @Named("member:get")
   @GET
   @Path("/members/{id}")
   @SelectJson("member")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Member getMember(@PathParam("id") String id);

   /**
    * Creates a new Member.
    *
    * @param member describes the Member to be created.
    * @return a reference of the newly-created Member.
    */
   @Named("member:create")
   @POST
   @Path("/members")
   @SelectJson("member")
   Member createMember(@WrapWith("member") Member.CreateMember member);

   /**
    * Update a Member.
    *
    * @param id the id of the Member to update.
    * @param member the Member's attributes to update.
    * @return a reference of the updated Member.
    */
   @Named("member:update")
   @PUT
   @Path("/members/{id}")
   @SelectJson("member")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Member updateMember(@PathParam("id") String id, @WrapWith("member") Member.UpdateMember member);

   /**
    * Deletes the specified Member.
    *
    * @param id the id of the Member to delete.
    * @return true if delete successful, false if not.
    */
   @Named("member:delete")
   @DELETE
   @Path("/members/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteMember(@PathParam("id") String id);

   /**
    * Returns a list of HealthMonitors to which the tenant has access. Default policy settings return only
    * those HealthMonitors that are owned by the tenant who submits the request, unless the request is submitted by an
    * user with administrative rights.
    *
    * @return the list of all HealthMonitor references configured for the tenant.
    */
   @Named("health_monitor:list")
   @GET
   @Path("/health_monitors")
   @Transform(HealthMonitorsToPagedIterable.class)
   @ResponseParser(ParseHealthMonitors.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<HealthMonitor> listHealthMonitors();

   /**
    * @return the list of all HealthMonitor references configured for the tenant.
    */
   @Named("health_monitor:list")
   @GET
   @Path("/health_monitors")
   @ResponseParser(ParseHealthMonitors.class)
   @Fallback(EmptyHealthMonitorsFallback.class)
   HealthMonitors listHealthMonitors(PaginationOptions options);

   /**
    * Returns the details for a specific HealthMonitor.
    *
    * @param id the id of the HealthMonitor to return.
    * @return Health Monitor or null if not found.
    */
   @Named("health_monitor:get")
   @GET
   @Path("/health_monitors/{id}")
   @SelectJson("health_monitor")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   HealthMonitor getHealthMonitor(@PathParam("id") String id);

   /**
    * Creates a new HealthMonitor.
    *
    * @param healthMonitor describes the HealthMonitor to be created.
    * @return a reference of the newly-created HealthMonitor.
    */
   @Named("health_monitor:create")
   @POST
   @Path("/health_monitors")
   @SelectJson("health_monitor")
   HealthMonitor createHealthMonitor(@WrapWith("health_monitor") HealthMonitor.CreateHealthMonitor healthMonitor);

   /**
    * Update a HealthMonitor.
    *
    * @param id the id of the HealthMonitor to update.
    * @param healthMonitor the HealthMonitor's attributes to update.
    * @return a reference of the updated HealthMonitor.
    */
   @Named("health_monitor:update")
   @PUT
   @Path("/health_monitors/{id}")
   @SelectJson("health_monitor")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   HealthMonitor updateHealthMonitor(@PathParam("id") String id,
         @WrapWith("health_monitor") HealthMonitor.UpdateHealthMonitor healthMonitor);

   /**
    * Deletes the specified Health Monitor.
    *
    * @param id the id of the Health Monitor to delete.
    * @return true if delete successful, false if not.
    */
   @Named("health_monitor:delete")
   @DELETE
   @Path("/health_monitors/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteHealthMonitor(@PathParam("id") String id);

   /**
    * Associate a HealthMonitor to a Pool.
    *
    * @param poolId the id of the Pool to associate.
    * @param healthMonitorId the id of the HealthMonitor to associate.
    * @return the newly associated HealthMonitor.
    */
   @Named("pool:associate_health_monitor")
   @POST
   @Path("/pools/{pool-id}/health_monitors")
   @SelectJson("health_monitor")
   @Payload("%7B\"health_monitor\":%7B\"id\":\"{healthMonitorId}\"%7D%7D")
   @Produces(MediaType.APPLICATION_JSON)
   HealthMonitor associateHealthMonitor(@PathParam("pool-id") String poolId,
         @PayloadParam("healthMonitorId") String healthMonitorId);

   /**
    * Disassociate a HealthMonitor from a Pool.
    *
    * @param poolId the id of the Pool to disassociate.
    * @param healthMonitorId the id of the HealthMonitor to disassociate.
    * @return true if disassociate successful, false if not.
    */
   @Named("pool:disassociate_health_monitor")
   @DELETE
   @Path("/pools/{pool-id}/health_monitors/{health-monitor-id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean disassociateHealthMonitor(@PathParam("pool-id") String poolId,
         @PathParam("health-monitor-id") String healthMonitorId);

}
