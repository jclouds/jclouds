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
package org.jclouds.azurecompute.arm.features;

import java.io.Closeable;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.arm.domain.Resource;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.filters.ApiVersionFilter;
import org.jclouds.azurecompute.arm.functions.URIParser;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * The Azure Resource Manager API includes operations for managing resource groups in your subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790546.aspx">docs</a>
 */
@Path("/resourcegroups")
@RequestFilters({ OAuthFilter.class, ApiVersionFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
public interface ResourceGroupApi extends Closeable{

   @Named("resourcegroup:list")
   @SelectJson("value")
   @GET
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<ResourceGroup> list();

   @Named("resourcegroup:create")
   @PUT
   @Path("/{name}")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   ResourceGroup create(@PathParam("name") String name, @PayloadParam("location") String location, @Nullable @PayloadParam("tags") Map<String, String> tags);

   @Named("resourcegroup:get")
   @GET
   @Path("/{name}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ResourceGroup get(@PathParam("name") String name);

   @Named("resourcegroup:resources")
   @GET
   @Path("/{name}/resources")
   @SelectJson("value")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Resource> resources(@PathParam("name") String name);

   @Named("resourcegroup:update")
   @PATCH
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{name}")
   @MapBinder(BindToJsonPayload.class)
   ResourceGroup update(@PathParam("name") String name, @Nullable @PayloadParam("tags") Map<String, String> tags);

   @Named("resourcegroup:delete")
   @DELETE
   @ResponseParser(URIParser.class)
   @Path("/{name}")
   @Fallback(NullOnNotFoundOr404.class)
   URI delete(@PathParam("name") String name);
}

