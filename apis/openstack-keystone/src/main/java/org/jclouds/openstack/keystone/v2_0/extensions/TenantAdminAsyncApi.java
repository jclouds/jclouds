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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.options.CreateTenantOptions;
import org.jclouds.openstack.keystone.v2_0.options.UpdateTenantOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to tenants Administration actions.
 * <p/>
 * 
 * @see org.jclouds.openstack.keystone.v2_0.extensions.TenantAdminApi
 * @author Pedro Navarro
 */
@Beta
@Extension(of = ServiceType.IDENTITY, namespace = ExtensionNamespaces.OS_KSADM)
@RequestFilters(AuthenticateRequest.class)
public interface TenantAdminAsyncApi {
   
   /**
    * Creates a new tenant
    * 
    * @return the new tenant
    */
   @Named("tenant:create")
   @POST
   @Path("/tenants")
   @SelectJson("tenant")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Tenant> create(@PayloadParam("name") String name);

   /**
    * Creates a new tenant
    * 
    * @return the new tenant
    */
   @Named("tenant:create")
   @POST
   @Path("/tenants")
   @SelectJson("tenant")
   @Consumes(MediaType.APPLICATION_JSON)
   @MapBinder(CreateTenantOptions.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Tenant> create(@PayloadParam("name") String name, CreateTenantOptions options);

   /**
    * Deletes a tenant.
    * 
    * @return true if successful
    */
   @Named("tenant:delete")
   @DELETE
   @Path("/tenants/{id}")
   @Consumes
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> delete(@PathParam("id") String id);

   /**
    * Updates a tenant
    * 
    * @return the updated tenant
    */
   @Named("tenant:updatetenant")
   @PUT
   @Path("/tenants/{id}")
   @SelectJson("tenant")
   @Consumes(MediaType.APPLICATION_JSON)
   @MapBinder(UpdateTenantOptions.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Tenant> update(@PathParam("id") String id, UpdateTenantOptions options);

   /**
    * Adds role to a user on a tenant
    * 
    * @return true if successful
    */
   @Named("tenant:addroleontenant")
   @PUT
   @Path("/tenants/{id}/users/{userId}/roles/OS-KSADM/{roleId}")
   @Consumes
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> addRoleOnTenant(@PathParam("id") String tenantId, @PathParam("userId") String userdId,
         @PathParam("roleId") String roleId);

   /**
    * Deletes role to a user on tenant
    * 
    * @return
    */
   @Named("tenant:deleteroleontenant")
   @DELETE
   @Path("/tenants/{id}/users/{userId}/roles/OS-KSADM/{roleId}")
   @Consumes
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> deleteRoleOnTenant(@PathParam("id") String tenantId, @PathParam("userId") String userdId,
         @PathParam("roleId") String roleId);

}
