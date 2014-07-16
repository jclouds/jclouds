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
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseServices;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseServices.ToPagedIterable;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;

/**
 * Provides access to Service Administration actions.
 */
@Beta
@Consumes(MediaType.APPLICATION_JSON)
@Extension(of = ServiceType.IDENTITY, namespace = ExtensionNamespaces.OS_KSADM)
@RequestFilters(AuthenticateRequest.class)
@Path("OS-KSADM/services")
public interface ServiceAdminApi {

   /**
    * Retrieve the list of services
    * <p/>
    *
    * @return the list of services
    */
   @Named("service:list")
   @GET
   @ResponseParser(ParseServices.class)
   @Transform(ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Service> list();

   @Named("service:list")
   @GET
   @ResponseParser(ParseServices.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   PaginatedCollection<Service> list(PaginationOptions options);

   /**
    * Creates a new Service
    *
    * @return the new Service
    */
   @Named("service:create")
   @POST
   @SelectJson("OS-KSADM:service")
   @WrapWith("OS-KSADM:service")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Service create(@PayloadParam("name") String name, @PayloadParam("type") String type,
         @PayloadParam("description") String description);

   /**
    * Gets the service
    *
    * @return the service
    */
   @Named("service:get")
   @GET
   @SelectJson("OS-KSADM:service")
   @Path("/{serviceId}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Service get(@PathParam("serviceId") String serviceId);

   /**
    * Deletes a service
    *
    * @return true if successful
    */
   @Named("service:delete")
   @DELETE
   @Path("/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("id") String serviceId);
}
