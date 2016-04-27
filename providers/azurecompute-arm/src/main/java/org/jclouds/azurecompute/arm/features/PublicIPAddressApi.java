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
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.functions.FalseOn204;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/resourcegroups/{resourcegroup}/providers/Microsoft.Network/publicIPAddresses")
@QueryParams(keys = "api-version", values = "2015-06-15")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)

public interface PublicIPAddressApi {

   @Named("publicipaddress:list")
   @SelectJson("value")
   @GET
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<PublicIPAddress> list();

   @Named("publicipaddress:create_or_update")
   @Path("/{publicipaddressname}")
   @MapBinder(BindToJsonPayload.class)
   @PUT
   PublicIPAddress createOrUpdate(@PathParam("publicipaddressname") String publicipaddressname,
                                                 @PayloadParam("location") String location,
                                                 @PayloadParam("tags") Map<String, String> tags,
                                                 @PayloadParam("properties") PublicIPAddressProperties properties);

   @Named("publicipaddress:get")
   @Path("/{publicipaddressname}")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   PublicIPAddress get(@PathParam("publicipaddressname") String publicipaddressname);

   @Named("publicipaddress:delete")
   @Path("/{publicipaddressname}")
   @DELETE
   @ResponseParser(FalseOn204.class)
   boolean delete(@PathParam("publicipaddressname") String publicipaddressname);
}
