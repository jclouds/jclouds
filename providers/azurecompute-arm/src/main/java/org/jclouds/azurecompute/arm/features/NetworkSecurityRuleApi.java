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
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.azurecompute.arm.functions.URIParser;
import org.jclouds.oauth.v2.filters.OAuthFilter;

import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.net.URI;

@Path("/resourcegroups/{resourcegroup}/providers/Microsoft.Network/networkSecurityGroups/{networksecuritygroup}")

@QueryParams(keys = "api-version", values = "2016-03-30")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface NetworkSecurityRuleApi {
   @Named("networksecurityrule:createOrUpdate")
   @Path("/securityRules/{networksecurityrulename}")
   @PUT
   @MapBinder(BindToJsonPayload.class)
   @Produces(MediaType.APPLICATION_JSON)
   NetworkSecurityRule createOrUpdate(@PathParam("networksecurityrulename") String ruleName,
                                      @PayloadParam("properties") NetworkSecurityRuleProperties properties);

   @Named("networksecurityrule:getDefaultRule")
   @Path("/defaultSecurityRules/{networksecurityrulename}")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSecurityRule getDefaultRule(@PathParam("networksecurityrulename") String ruleName);

   @Named("networksecurityrule:get")
   @Path("/securityRules/{networksecurityrulename}")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   NetworkSecurityRule get(@PathParam("networksecurityrulename") String ruleName);

   @Named("networksecurityrule:delete")
   @Path("/securityRules/{networksecurityrulename}")
   @DELETE
   @ResponseParser(URIParser.class)
   @Fallback(NullOnNotFoundOr404.class)
   URI delete(@PathParam("networksecurityrulename") String ruleName);

   @Named("networksecuritygroup:list")
   @Path("/securityRules")
   @GET
   @SelectJson("value")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<NetworkSecurityRule> list();

   @Named("networksecuritygroup:listDefaultRules")
   @Path("/defaultSecurityRules")
   @GET
   @SelectJson("value")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<NetworkSecurityRule> listDefaultRules();
}

