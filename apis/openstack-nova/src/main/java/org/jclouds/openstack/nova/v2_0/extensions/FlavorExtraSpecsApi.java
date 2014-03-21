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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyMapOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.annotations.Beta;

/**
 * Provides access to the OpenStack Compute (Nova) Flavor Extra Specs Extension API.
 *
 * @see org.jclouds.openstack.nova.v2_0.features.FlavorApi
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.FLAVOR_EXTRA_SPECS)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/flavors")
public interface FlavorExtraSpecsApi {
   /**
    * Retrieves all extra specs for a flavor
    *
    * @return the set of extra metadata for the flavor
    */
   @Named("flavorExtraSpecs:getMetadata")
   @GET
   @Path("/{id}/os-extra_specs")
   @SelectJson("extra_specs")
   @Fallback(EmptyMapOnNotFoundOr404.class)
   Map<String, String> getMetadata(@PathParam("id") String flavorId);

   /**
    * Creates or updates the extra specs for a given flavor
    *
    * @param flavorId   the id of the flavor to modify
    * @param specs      the extra specs to apply
    */
   @Named("flavorExtraSpecs:updateMetadata")
   @POST
   @Path("/{id}/os-extra_specs")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean updateMetadata(@PathParam("id") String flavorId,
         @PayloadParam("extra_specs") Map<String, String> specs);

   /**
    * Return a single extra spec value
    *
    * @param id  the id of the flavor to modify
    * @param key the extra spec key to retrieve
    */
   @Named("flavorExtraSpecs:getMetadataKey")
   @GET
   @Path("/{id}/os-extra_specs/{key}")
   @Unwrap
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   String getMetadataKey(@PathParam("id") String flavorId, @PathParam("key") String key);

   /**
    * Creates or updates a single extra spec value
    *
    * @param id    the id of the flavor to modify
    * @param key   the extra spec key (when creating ensure this does not include whitespace or
    *              other difficult characters)
    * @param value the value to associate with the key
    */
   @Named("flavorExtraSpecs:updateMetadataEntry")
   @PUT
   @Path("/{id}/os-extra_specs/{key}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"{key}\":\"{value}\"%7D")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean updateMetadataEntry(@PathParam("id") String flavorId,
         @PathParam("key") @PayloadParam("key") String key, @PayloadParam("value") String value);

   /**
    * Deletes an extra spec
    *
    * @param id  the id of the flavor to modify
    * @param key the extra spec key to delete
    */
   @Named("flavorExtraSpecs:deleteMetadataKey")
   @DELETE
   @Path("/{id}/os-extra_specs/{key}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean deleteMetadataKey(@PathParam("id") String flavorId, @PathParam("key") String key);
}
