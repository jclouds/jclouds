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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Quota;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.annotations.Beta;

/**
 * Provide access to OpenStack Compute (Nova) Quota Extension API.
 * <p/>
 * The quotas extension enables limiters placed on the resources used per tenant (project) for virtual instances. It is
 * used with the OpenStack Compute API 1.1 for administrators who need to control the amount of volumes, memory, floating
 * IP addresses, instances, or cores allowed within a defined tenant or project.
 * <p/>
 * To use this extension, you need to have administrative rights to the tenants upon which you are placing quotas.
 *
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.QUOTAS)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/os-quota-sets")
public interface QuotaApi {
   /**
    * @return the quota settings for the tenant
    */
   @Named("quota:get")
   @GET
   @SelectJson("quota_set")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Quota getByTenant(@PathParam("id") String tenantId);

   /**
    * Update the quotas for a given tenant
    *
    * @return true if successful
    */
   @Named("quota:update")
   @PUT
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   boolean updateQuotaOfTenant(@PayloadParam("quota_set") Quota quota,
         @PathParam("id") String tenantId);

   /**
    * @return the set of default quotas for the tenant
    */
   @Named("quota:get")
   @GET
   @SelectJson("quota_set")
   @Path("/{id}/defaults")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Quota getDefaultsForTenant(@PathParam("id") String tenantId);
}
