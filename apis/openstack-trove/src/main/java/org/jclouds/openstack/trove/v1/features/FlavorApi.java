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
package org.jclouds.openstack.trove.v1.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.trove.v1.domain.Flavor;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.collect.FluentIterable;

/**
 * This API strictly for listing and retrieving Flavor. Flavors cannot be created or deleted.
 *
 * @see Flavor
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface FlavorApi {
   /**
    * Returns a summary list of Flavors.
    *
    * @return The list of Flavors.
    */
   @Named("flavor:list")
   @GET
   @Path("/flavors")
   @SelectJson("flavors")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Flavor> list();

   /**
    * Returns a Flavor by id.
    *
    * @param flavorId The id of the Flavor.
    * @return Flavor The Flavor for the specified id.
    */
   @Named("flavors:get/{id}")
   @GET
   @Path("/flavors/{id}")
   @SelectJson("flavor")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Flavor get(@PathParam("id") int flavorId);

   /**
    * Returns a list of Flavors by Account ID (Tenant Id).
    *
    * @param flavorId The id of the tenant.
    * @return The list of Flavors for Account/Tenant Id.
    */
   @Named("flavors:get/{id}")
   @GET
   @Path("/flavors/{id}")
   @SelectJson("flavors")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Flavor> list(@PathParam("id") String accountId);
}
