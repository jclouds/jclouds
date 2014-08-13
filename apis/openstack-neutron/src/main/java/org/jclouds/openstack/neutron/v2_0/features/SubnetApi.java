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

import com.google.common.collect.FluentIterable;
import org.jclouds.Fallbacks;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.domain.Subnet;
import org.jclouds.openstack.neutron.v2_0.functions.ParseSubnetDetails;
import org.jclouds.openstack.neutron.v2_0.functions.ParseSubnets;
import org.jclouds.openstack.neutron.v2_0.options.CreateSubnetBulkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreateSubnetOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdateSubnetOptions;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

import static org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import static org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;

/**
 * Provides access to Subnet operations for the OpenStack Networking (Neutron) v2 API.
 *

 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-network/2.0/content/Subnets.html">api doc</a>
 * @deprecated Use v2 instead of v2_0
 */
@Deprecated
@Path("/v2.0/subnets")
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface SubnetApi {

   /**
    * Returns the list of all subnets currently defined in Neutron for the current tenant. The list provides the unique
    * identifier of each subnet configured for the tenant.
    *
    * @return the list of all subnet references configured for the tenant
    */
   @Named("subnet:list")
   @GET
   @ResponseParser(ParseSubnets.class)
   @Transform(ParseSubnets.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @QueryParams(keys = {"fields", "fields", "fields"}, values = {"id", "tenant_id", "name"})
   PagedIterable<? extends ReferenceWithName> list();

   /**
    * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/pagination.html">api doc</a>
    */
   @Named("subnet:list")
   @GET
   @ResponseParser(ParseSubnets.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @QueryParams(keys = {"fields", "fields", "fields"}, values = {"id", "tenant_id", "name"})
   PagedIterable<? extends ReferenceWithName> list(PaginationOptions options);

   /**
    * Returns all subnets currently defined in Neutron for the current tenant.
    *
    * @return the list of all subnets configured for the tenant
    */
   @Named("subnet:list")
   @GET
   @ResponseParser(ParseSubnetDetails.class)
   @Transform(ParseSubnetDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<? extends Subnet> listInDetail();

   /**
    * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/pagination.html">api doc</a>
    */
   @Named("subnet:list")
   @GET
   @ResponseParser(ParseSubnetDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PagedIterable<? extends Subnet> listInDetail(PaginationOptions options);

   /**
    * Returns the specific Subnet.
    *
    * @param id the id of the subnet to return
    * @return Subnet or null if not found
    */
   @Named("subnet:get")
   @GET
   @Path("/{id}")
   @SelectJson("subnet")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Nullable
   Subnet get(@PathParam("id") String id);

   /**
    * Create a subnet within a specified network
    *
    * @param networkId the id of the network to associate the subnet with
    * @param ipVersion the ip version of this subnet
    * @param cidr the cidr for this subnet
    * @param options optional arugments
    * @return a reference of the newly-created subnet
    */
   @Named("subnet:create")
   @POST
   @SelectJson("subnet")
   @MapBinder(CreateSubnetOptions.class)
   Subnet create(@PayloadParam("network_id") String networkId, @PayloadParam("ip_version") Integer ipVersion,
                            @PayloadParam("cidr") String cidr, CreateSubnetOptions... options);

   /**
    * Create multiple subnets
    *
    * @param subnets the bulk of subnets to create
    * @return list of references of the newly-created subnets
    */
   @Named("subnet:createBulk")
   @POST
   @SelectJson("subnets")
   @MapBinder(CreateSubnetBulkOptions.class)
   FluentIterable<? extends Subnet> createBulk(CreateSubnetBulkOptions subnets);

   /**
    * Update a subnet
    *
    * @param id the id of the subnet to update
    * @param options the attributes to update
    * @return true if update was successful, false if not
    */
   @Named("subnet:update")
   @PUT
   @Path("/{id}")
   @MapBinder(UpdateSubnetOptions.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean update(@PathParam("id") String id, UpdateSubnetOptions... options);

   /**
    * Delete a subnet
    *
    * @param id the id of the subnet to delete
    * @return true if delete successful, false if not
    */
   @Named("subnet:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);
}
