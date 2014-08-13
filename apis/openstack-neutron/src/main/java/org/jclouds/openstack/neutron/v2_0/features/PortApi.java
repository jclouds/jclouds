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
import org.jclouds.openstack.neutron.v2_0.domain.Port;
import org.jclouds.openstack.neutron.v2_0.domain.ReferenceWithName;
import org.jclouds.openstack.neutron.v2_0.functions.ParsePortDetails;
import org.jclouds.openstack.neutron.v2_0.functions.ParsePorts;
import org.jclouds.openstack.neutron.v2_0.options.CreatePortBulkOptions;
import org.jclouds.openstack.neutron.v2_0.options.CreatePortOptions;
import org.jclouds.openstack.neutron.v2_0.options.UpdatePortOptions;
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
 * Provides access to Port operations for the OpenStack Networking (Neutron) v2 API.
 * <p/>
 * A port represents a virtual switch port on a logical network switch where all the interfaces attached to a given network are connected.
 * <p/>
 * A port has an administrative state which is either 'DOWN' or 'ACTIVE'. Ports which are administratively down will not be able to receive/send traffic.

 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-network/2.0/content/Ports.html">api doc</a>
 * @deprecated Use v2 instead of v2_0
 */
@Deprecated
@Path("/v2.0/ports")
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface PortApi {

   /**
    * Returns the list of all ports currently defined in Neutron for the current tenant. The list provides the unique
    * identifier of each network configured for the tenant.
    *
    * @return the list of all port references configured for the tenant
    */
   @Named("port:list")
   @GET
   @ResponseParser(ParsePorts.class)
   @Transform(ParsePorts.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @QueryParams(keys = {"fields", "fields", "fields"}, values = {"id", "tenant_id", "name"})
   PagedIterable<? extends ReferenceWithName> list();

   /**
    * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/pagination.html">api doc</a>
    */
   @Named("port:list")
   @GET
   @ResponseParser(ParsePorts.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @QueryParams(keys = {"fields", "fields", "fields"}, values = {"id", "tenant_id", "name"})
   PagedIterable<? extends ReferenceWithName> list(PaginationOptions options);

   /**
    * Returns the set of ports currently defined in Neutron for the requested network.
    *
    * @return the list of all ports configured for the tenant
    */
   @Named("port:list")
   @GET
   @ResponseParser(ParsePortDetails.class)
   @Transform(ParsePortDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<? extends Port> listInDetail();

   /**
    * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/pagination.html">api doc</a>
    */
   @Named("port:list")
   @GET
   @ResponseParser(ParsePortDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PagedIterable<? extends Port> listInDetail(PaginationOptions options);

   /**
    * Returns the specific port
    *
    * @param id the id of the port to return
    * @return Port or null if not found
    */
   @Named("port:get")
   @GET
   @Path("/{id}")
   @SelectJson("port")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Nullable
   Port get(@PathParam("id") String id);

   /**
    * Create a new port in the specified network
    *
    * @param networkId the id of the network to associate this port with
    * @param options optional arguments
    * @return a reference of the newly-created port
    */
   @Named("port:create")
   @POST
   @SelectJson("port")
   @MapBinder(CreatePortOptions.class)
   Port create(@PayloadParam("network_id") String networkId, CreatePortOptions... options);

   /**
    * Create multiple ports
    *
    * @param ports the bulk of ports to create
    * @return list of references of the newly-created ports
    */
   @Named("port:createBulk")
   @POST
   @SelectJson("ports")
   @MapBinder(CreatePortBulkOptions.class)
   FluentIterable<? extends Port> createBulk(CreatePortBulkOptions ports);

   /**
    * Update a port
    *
    * @param id the id of the port to update
    * @param options the attributes to update
    * @return true if update successful, false if not
    */
   @Named("port:update")
   @PUT
   @Path("/{id}")
   @MapBinder(UpdatePortOptions.class)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean update(@PathParam("id") String id, UpdatePortOptions... options);

   /**
    * Delete a port
    *
    * @param id the id of the port to delete
    * @return true if delete successful, false if not
    */
   @Named("port:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);
}
