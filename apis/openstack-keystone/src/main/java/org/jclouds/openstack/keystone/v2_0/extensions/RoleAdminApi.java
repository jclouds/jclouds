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
package org.jclouds.openstack.keystone.v2_0.extensions;

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
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
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
 * Provides access to the OpenStack Keystone Role Administration Extension API.
 *
 */
@Beta
@Extension(of = ServiceType.IDENTITY, namespace = ExtensionNamespaces.OS_KSADM)
@RequestFilters(AuthenticateRequest.class)
@Path("OS-KSADM/roles")
public interface RoleAdminApi {

   /**
    * Returns a summary list of roles.
    *
    * @return The list of roles
    */
   @Named("role:list")
   @GET
   @SelectJson("roles")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<? extends Role> list();

   /**
    * Creates a new Role
    *
    * @return the new Role
    */
   @Named("role:create")
   @POST
   @SelectJson("role")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("role")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Role create(@PayloadParam("name") String name);

   /**
    * Gets the role
    *
    * @return the role
    */
   @Named("role:get")
   @GET
   @SelectJson("role")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Role get(@PathParam("id") String roleId);

   /**
    * Deletes a role
    *
    * @return true if successful
    */
   @Named("role:delete")
   @DELETE
   @Path("/{id}")
   @Consumes
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String roleId);
}
