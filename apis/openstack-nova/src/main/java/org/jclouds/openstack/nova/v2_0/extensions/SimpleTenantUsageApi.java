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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.SimpleTenantUsage;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides access to the OpenStack Compute (Nova) Simple Tenant Usage extension API.
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.SIMPLE_TENANT_USAGE)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/os-simple-tenant-usage")
public interface SimpleTenantUsageApi {
   /**
    * Retrieve tenant usage for all tenants.
    *
    * @return the set of TenantUsage reports
    */
   @Named("tenantUsage:list")
   @GET
   @SelectJson("tenant_usages")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<SimpleTenantUsage> list();

   /**
    * Retrieve tenant_usage for a specified tenant
    *
    * @return the requested tenant usage
    */
   @Named("tenantUsage:get")
   @GET
   @Path("/{id}")
   @SelectJson("tenant_usage")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SimpleTenantUsage get(@PathParam("id") String tenantId);
}
