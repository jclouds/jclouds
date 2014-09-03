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

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.Networks;
import org.jclouds.openstack.neutron.v2.fallbacks.EmptyNetworksFallback;
import org.jclouds.openstack.neutron.v2.functions.NetworksToPagedIterable;
import org.jclouds.openstack.neutron.v2.functions.ParseNetworks;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * Provides access to Network operations for the OpenStack Networking (Neutron) v2 API.
 * <p/>
 * Each tenant can define one or more networks. A network is a virtual isolated layer-2 broadcast domain reserved to the
 * tenant. A tenant can create several ports for a network, and plug virtual interfaces into these ports.
 *
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-network/2.0/content/Networks.html">api doc</a>
 */
@Beta
@Path("/networks")
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface NetworkApi {

   /**
    * Returns all networks currently defined in Neutron for the current tenant.
    *
    * @return the list of all networks configured for the tenant
    */
   @Named("network:list")
   @GET
   @ResponseParser(ParseNetworks.class)
   @Transform(NetworksToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Network> list();

   /**
    * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/pagination.html">api doc</a>
    */
   @Named("network:list")
   @GET
   @ResponseParser(ParseNetworks.class)
   @Fallback(EmptyNetworksFallback.class)
   Networks list(PaginationOptions options);

   /**
    * Return a specific network
    *
    * @param id the id of the network to return
    * @return Network or null if not found
    */
   @Named("network:get")
   @GET
   @Path("/{id}")
   @SelectJson("network")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Nullable
   Network get(@PathParam("id") String id);

   /**
    * Create a new network with the specified type
    *
    * @param network Describes the network to be created.
    * @return a reference of the newly-created network
    */
   @Named("network:create")
   @POST
   @SelectJson("network")
   Network create(@WrapWith("network") Network.CreateNetwork network);

   /**
    * Create multiple networks
    *
    * @param networks the bulk of networks to create
    * @return list of references of the newly-created networks
    */
   @Named("network:createBulk")
   @POST
   @SelectJson("networks")
   FluentIterable<Network> createBulk(@WrapWith("networks") ImmutableList<Network.CreateNetwork> networks);

   /**
    * Update a network
    *
    * @param id the id of the network to update
    * @param network the network to update
    * @return true if update successful, false if not
    */
   @Named("network:update")
   @PUT
   @Path("/{id}")
   @SelectJson("network")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Nullable
   Network update(@PathParam("id") String id, @WrapWith("network") Network.UpdateNetwork network);

   /**
    * Deletes the specified network
    *
    * @param id the id of the network to delete
    * @return true if delete was successful, false if not
    */
   @Named("network:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);
}
