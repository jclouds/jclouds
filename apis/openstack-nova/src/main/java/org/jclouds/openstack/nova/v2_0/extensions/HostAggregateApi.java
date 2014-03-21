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

import java.util.Map;

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
import org.jclouds.openstack.nova.v2_0.domain.HostAggregate;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provide access to the OpenStack Compute (Nova) Host Aggregates extension API.
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.AGGREGATES)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/os-aggregates")
public interface HostAggregateApi {
   /**
    * Lists all host aggregates.
    *
    * @return the set of host aggregates.
    */
   @Named("hostAggregate:list")
   @GET
   @SelectJson("aggregates")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<HostAggregate> list();

   /**
    * Retrieves the details of an aggregate, hosts and metadata included.
    *
    * @return the details of the aggregate requested.
    */
   @Named("hostAggregate:get")
   @GET
   @Path("/{id}")
   @SelectJson("aggregate")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   HostAggregate get(@PathParam("id") String id);

   /**
    * Creates an aggregate, given its name and availability zone.
    *
    * @return the newly created Aggregate
    */
   @Named("hostAggregate:create")
   @POST
   @SelectJson("aggregate")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("aggregate")
   HostAggregate createInAvailabilityZone(@PayloadParam("name") String name,
         @PayloadParam("availability_zone") String availabilityZone);

   /**
    * Updates the name of an aggregate.
    */
   @Named("hostAggregate:update")
   @POST
   @Path("/{id}")
   @SelectJson("aggregate")
   @WrapWith("aggregate")
   HostAggregate updateName(@PathParam("id") String id, @PayloadParam("name") String name);

   /**
    * Updates the availability zone for an aggregate.
    */
   @Named("hostAggregate:update")
   @POST
   @Path("/{id}")
   @SelectJson("aggregate")
   @WrapWith("aggregate")
   HostAggregate updateAvailabilityZone(@PathParam("id") String id,
         @PayloadParam("availability_zone") String availabilityZone);

   /**
    * Removes an aggregate.
    */
   @Named("hostAggregate:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean delete(@PathParam("id") String id);

   /**
    * Adds a host to an aggregate
    */
   @Named("hostAggregate:addHost")
   @POST
   @Path("/{id}/action")
   @SelectJson("aggregate")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("add_host")
   HostAggregate addHost(@PathParam("id") String id, @PayloadParam("host") String host);

   /**
    * Removes a host from an aggregate
    */
   @Named("hostAggregate:removeHost")
   @POST
   @Path("/{id}/action")
   @SelectJson("aggregate")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("remove_host")
   HostAggregate removeHost(@PathParam("id") String id, @PayloadParam("host") String host);

   /**
    * Adds metadata to an aggregate
    */
   @Named("hostAggregate:setMetadata")
   @POST
   @Path("/{id}/action")
   @SelectJson("aggregate")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("set_metadata")
   HostAggregate setMetadata(@PathParam("id") String id,
         @PayloadParam("metadata") Map<String, String> metadata);
}
