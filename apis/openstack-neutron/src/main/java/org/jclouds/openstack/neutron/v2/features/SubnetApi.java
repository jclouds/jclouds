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
package org.jclouds.openstack.neutron.v2.features;

import com.google.common.collect.FluentIterable;
import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.domain.Subnets;
import org.jclouds.openstack.neutron.v2.fallbacks.EmptySubnetsFallback;
import org.jclouds.openstack.neutron.v2.functions.ParseSubnets;
import org.jclouds.openstack.neutron.v2.functions.SubnetsToPagedIterable;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

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
import java.util.List;

/**
 * Provides access to Subnet operations for the OpenStack Networking (Neutron) v2 API.
 *
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-network/2.0/content/Subnets.html">api doc</a>
 */
@Path("/subnets")
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
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
   @Transform(SubnetsToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Subnet> list();

   /**
    * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/pagination.html">api doc</a>
    */
   @Named("subnet:list")
   @GET
   @ResponseParser(ParseSubnets.class)
   @Fallback(EmptySubnetsFallback.class)
   Subnets list(PaginationOptions options);

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
    * @param subnet the subnet to be created
    * @return a reference of the newly-created subnet
    */
   @Named("subnet:create")
   @POST
   @SelectJson("subnet")
   Subnet create(@WrapWith("subnet") Subnet.CreateSubnet subnet);

   /**
    * Create multiple subnets
    *
    * @param subnets the bulk of subnets to create
    * @return list of references of the newly-created subnets
    */
   @Named("subnet:createBulk")
   @POST
   @SelectJson("subnets")
   FluentIterable<Subnet> createBulk(@WrapWith("subnets") List<Subnet.CreateSubnet> subnets);

   /**
    * Update a subnet
    *
    * @param id the id of the subnet to update
    * @return true if update was successful, false if not
    */
   @Named("subnet:update")
   @PUT
   @Path("/{id}")
   @SelectJson("subnet")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Subnet update(@PathParam("id") String id, @WrapWith("subnet") Subnet.UpdateSubnet subnet);

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
