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
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to roles Administration actions.
 * <p/>
 * 
 * @see org.jclouds.openstack.keystone.v2_0.extensions.RoleAdminApi
 */
@Beta
@Extension(of = ServiceType.IDENTITY, namespace = ExtensionNamespaces.OS_KSADM)
@RequestFilters(AuthenticateRequest.class)
public interface RoleAdminAsyncApi {

   /**
    * Returns a summary list of roles.
    * 
    * @return The list of roles
    */
   @Named("role:list")
   @GET
   @Path("OS-KSADM/roles")
   @SelectJson("roles")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends Role>> list();

   /**
    * Creates a new role
    * 
    * @return the new role
    */
   @Named("role:create")
   @POST
   @Path("OS-KSADM/roles")
   @SelectJson("role")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @WrapWith("role")
   ListenableFuture<? extends Role> create(@PayloadParam("name") String name);

   /**
    * Gets the role
    * 
    * @return the role
    */
   @Named("role:get")
   @GET
   @SelectJson("role")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("OS-KSADM/roles/{roleId}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Role> get(@PathParam("roleId") String roleId);

   /**
    * Deletes an role.
    * 
    * @return true if successful
    */
   @Named("role:delete")
   @DELETE
   @Path("OS-KSADM/roles/{id}")
   @Consumes
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> delete(@PathParam("id") String id);

}
