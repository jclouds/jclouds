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
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.arm.domain.loadbalancer.LoadBalancer;
import org.jclouds.azurecompute.arm.domain.loadbalancer.LoadBalancer.SKU;
import org.jclouds.azurecompute.arm.domain.loadbalancer.LoadBalancerProperties;
import org.jclouds.azurecompute.arm.filters.ApiVersionFilter;
import org.jclouds.azurecompute.arm.functions.URIParser;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * The Load Balancer API includes operations for managing the load balancers in your subscription.
 *
 * @see <a href="https://docs.microsoft.com/en-us/rest/api/load-balancer/loadbalancers">docs</a>
 */
@RequestFilters({ OAuthFilter.class, ApiVersionFilter.class })
@Consumes(MediaType.APPLICATION_JSON)
public interface LoadBalancerApi {

   @Named("loadbalancer:list")
   @GET
   @Path("/resourcegroups/{resourcegroup}/providers/Microsoft.Network/loadBalancers")
   @SelectJson("value")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<LoadBalancer> list();

   @Named("loadbalancer:listall")
   @GET
   @Path("/providers/Microsoft.Network/loadBalancers")
   @SelectJson("value")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<LoadBalancer> listAll();

   @Named("loadbalancer:get")
   @Path("/resourcegroups/{resourcegroup}/providers/Microsoft.Network/loadBalancers/{loadbalancername}")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancer get(@PathParam("loadbalancername") String lbName);

   @Named("loadbalancer:createOrUpdate")
   @Path("/resourcegroups/{resourcegroup}/providers/Microsoft.Network/loadBalancers/{loadbalancername}")
   @PUT
   @MapBinder(BindToJsonPayload.class)
   LoadBalancer createOrUpdate(@PathParam("loadbalancername") String lbName, @PayloadParam("location") String location, @Nullable @PayloadParam("tags") Map<String, String> tags, @Nullable @PayloadParam("sku") SKU sku,
         @PayloadParam("properties") LoadBalancerProperties properties);

   @Named("loadbalancer:delete")
   @Path("/resourcegroups/{resourcegroup}/providers/Microsoft.Network/loadBalancers/{loadbalancername}")
   @DELETE
   @ResponseParser(URIParser.class)
   @Fallback(NullOnNotFoundOr404.class)
   URI delete(@PathParam("loadbalancername") String lbName);

}
