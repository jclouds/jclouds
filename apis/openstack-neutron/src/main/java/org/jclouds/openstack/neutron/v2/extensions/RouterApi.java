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
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.neutron.v2.domain.Router;
import org.jclouds.openstack.neutron.v2.domain.RouterInterface;
import org.jclouds.openstack.neutron.v2.domain.Routers;
import org.jclouds.openstack.neutron.v2.fallbacks.EmptyRoutersFallback;
import org.jclouds.openstack.neutron.v2.functions.ParseRouters;
import org.jclouds.openstack.neutron.v2.functions.RouterToPagedIterable;
import org.jclouds.openstack.neutron.v2.options.EmptyOptions;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;

/**
 * Provides access to Router operations for the OpenStack Networking (Neutron) v2 API.
 * <p/>
 * A logical entity for forwarding packets across internal subnets and NATting them on external
 * networks through an appropriate external gateway.
 *
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-network/2.0/content/router_ext.html">api doc</a>
 */
@Beta
@Path("/routers")
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface RouterApi {

   /**
    * Returns the list of all routers currently defined in Neutron for the current tenant. The list provides the unique
    * identifier of each router configured for the tenant
    *
    * @return the list of all router references configured for the tenant.
    */
   @Named("router:list")
   @GET
   @Transform(RouterToPagedIterable.class)
   @ResponseParser(ParseRouters.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Router> list();

   /**
    * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/pagination.html">api doc</a>
    */
   @Named("router:list")
   @GET
   @ResponseParser(ParseRouters.class)
   @Fallback(EmptyRoutersFallback.class)
   Routers list(PaginationOptions options);

   /**
    * Returns a Routers collection that should contain a single router with the id requested.
    *
    * @param id the id of the router to return
    * @return Routers collection or empty if not found
    */
   @Named("router:get")
   @GET
   @Path("/{id}")
   @SelectJson("router")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Router get(@PathParam("id") String id);

   /**
    * Create a new router
    *
    * @param router Options for creating a router
    * @return the newly created router
    */
   @Named("router:create")
   @POST
   @SelectJson("router")
   Router create(@WrapWith("router") Router.CreateRouter router);

   /**
    * Update a router
    *
    * @param id the id of the router to update
    * @param router Contains only the attributes to update
    * @return The modified router
    */
   @Named("router:update")
   @PUT
   @Path("/{id}")
   @SelectJson("router")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Router update(@PathParam("id") String id, @WrapWith("router") Router.UpdateRouter router);

   /**
    * Deletes the specified router
    *
    * @param id the id of the router to delete
    * @return true if delete successful, false if not
    */
   @Named("router:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);

   /**
    * Add a interface to a router to connect to the specified subnet
    *
    * @param routerId the id of the router to create the interface at
    * @param subnetId the id of the subnet to connect with the interface
    * @return the newly-created router interface
    */
   @Named("router:addInterfaceForSubnet")
   @PUT
   @Path("/{id}/add_router_interface")
   @MapBinder(EmptyOptions.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   RouterInterface addInterfaceForSubnet(@PathParam("id") String routerId, @PayloadParam("subnet_id") String subnetId);

   /**
    * Add a interface to a router to connect to the specified port
    *
    * @param routerId the id of the router to create the interface at
    * @param portId the id of the port to connect with the interface
    * @return the newly-created router interface
    */
   @Named("router:addInterfaceForPort")
   @PUT
   @Path("/{id}/add_router_interface")
   @MapBinder(EmptyOptions.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   RouterInterface addInterfaceForPort(@PathParam("id") String routerId, @PayloadParam("port_id") String portId);

   /**
    * Remove the interface where the specified subnet is connected to
    *
    * @param routerId the id of the router to remove the interface from
    * @param subnetId the id of the subnet to disconnect from the interface
    */
   @Named("router:removeInterfaceForSubnet")
   @PUT
   @Path("/{id}/remove_router_interface")
   @MapBinder(EmptyOptions.class)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean removeInterfaceForSubnet(@PathParam("id") String routerId, @PayloadParam("subnet_id") String subnetId);

   /**
    * Remove the interface where the specified port is connected to
    *
    * @param routerId the id of the router to remove the interface from
    * @param portId the id of the port to disconnect from the interface
    */
   @Named("router:removeInterfaceForPort")
   @PUT
   @Path("/{id}/remove_router_interface")
   @MapBinder(EmptyOptions.class)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean removeInterfaceForPort(@PathParam("id") String routerId, @PayloadParam("port_id") String portId);
}
