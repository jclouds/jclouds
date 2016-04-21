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

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.functions.FalseOn204;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.binders.BindToJsonPayload;

import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.MapBinder;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/resourcegroups/{resourcegroup}/providers/Microsoft.Network/virtualNetworks/{virtualnetwork}/subnets")

@QueryParams(keys = "api-version", values = "2015-06-15")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface SubnetApi {

   @Named("subnet:list")
   @SelectJson("value")
   @GET
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Subnet> list();

   @Named("subnet:create_or_update")
   @Path("/{subnetname}")
   @MapBinder(BindToJsonPayload.class)
   @PUT
   Subnet createOrUpdate(@PathParam("subnetname") String name,
                               @PayloadParam("properties") Subnet.SubnetProperties properties);

   @Named("subnet:get")
   @Path("/{subnetname}")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   Subnet get(@PathParam("subnetname") String subnetname);

   @Named("subnet:delete")
   @Path("/{subnetname}")
   @DELETE
   @ResponseParser(FalseOn204.class)
   boolean delete(@PathParam("subnetname") String subnetname);
}
