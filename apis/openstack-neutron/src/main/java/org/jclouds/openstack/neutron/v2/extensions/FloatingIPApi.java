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
import org.jclouds.openstack.neutron.v2.domain.FloatingIP;
import org.jclouds.openstack.neutron.v2.domain.FloatingIPs;
import org.jclouds.openstack.neutron.v2.fallbacks.EmptyFloatingIPsFallback;
import org.jclouds.openstack.neutron.v2.functions.FloatingIPsToPagedIterable;
import org.jclouds.openstack.neutron.v2.functions.ParseFloatingIPs;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;

/**
 * Provides access to Floating IP operations for the OpenStack Networking (Neutron) v2 API.
 * <p/>
 * A floating IP is an IP address on an external network, which is associated with a specific port, and optionally a
 * specific IP address, on a private OpenStack Networking network. Therefore a floating IP allows access to an
 * instance on a private network from an external network. Floating IPs can only be defined on networks for which the
 * attribute floatingip:external (by the external network extension) has been set to True.
 */
@Beta
@Path("/floatingips")
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface FloatingIPApi {

   /**
    * Returns a list of floating IPs to which the tenant has access. Default policy settings return only
    * those floating IPs that are owned by the tenant who submits the request, unless the request is submitted by an
    * user with administrative rights.
    *
    * @return the list of all floatingIP references configured for the tenant.
    */
   @Named("floatingip:list")
   @GET
   @Transform(FloatingIPsToPagedIterable.class)
   @ResponseParser(ParseFloatingIPs.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<FloatingIP> list();

   /**
    * @return the list of all floatingIP references configured for the tenant.
    */
   @Named("floatingip:list")
   @GET
   @ResponseParser(ParseFloatingIPs.class)
   @Fallback(EmptyFloatingIPsFallback.class)
   FloatingIPs list(PaginationOptions options);

   /**
    * Returns the details for a specific floating IP.
    *
    * @param id the id of the floatingIP to return
    * @return FloatingIPs collection or empty if not found
    */
   @Named("floatingip:get")
   @GET
   @Path("/{id}")
   @SelectJson("floatingip")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   FloatingIP get(@PathParam("id") String id);

   /**
    * Creates a floating IP.
    *
    * @param createFloatingIP Options for creating a Floating IP
    * @return the newly created Floating IP
    */
   @Named("floatingip:create")
   @POST
   @SelectJson("floatingip")
   FloatingIP create(@WrapWith("floatingip") FloatingIP.CreateFloatingIP createFloatingIP);

   /**
    * Update a Floating IP
    *
    * @param id the id of the Floating IP to update
    * @param updateFloatingIP Contains only the attributes to update
    * @return The modified Floating IP
    */
   @Named("floatingip:update")
   @PUT
   @Path("/{id}")
   @SelectJson("floatingip")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   FloatingIP update(@PathParam("id") String id, @WrapWith("floatingip") FloatingIP.UpdateFloatingIP updateFloatingIP);

   /**
    * Deletes the specified floatingIP
    *
    * @param id the id of the floatingIP to delete
    * @return true if delete successful, false if not
    */
   @Named("floatingip:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String id);
}
