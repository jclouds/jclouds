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
package org.jclouds.googlecomputeengine.features;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;

import java.net.URI;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.TargetHttpProxy;
import org.jclouds.googlecomputeengine.functions.internal.ParseTargetHttpProxies;
import org.jclouds.googlecomputeengine.handlers.PayloadBinder;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.TargetHttpProxyOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to Target Http Proxies via their REST API.
 * <p/>
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/targetHttpProxies"/>
 */
@SkipEncoding({'/', '='})
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(OAuthAuthenticator.class)
public interface TargetHttpProxyApi {
   /**
    * Returns the specified target http proxy resource.
    *
    * @param targetHttpProxyName name of the targetHttpProxy resource to return.
    * @return an TargetHttpProxy resource.
    */
   @Named("TargetHttpProxys:get")
   @GET
   @Path("/global/targetHttpProxies/{targetHttpProxy}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   TargetHttpProxy get(@PathParam("targetHttpProxy") String targetHttpProxyName);

   /**
    * Creates a TargetHttpProxy resource in the specified project using the data included in the request.
    *
    * @param name            the name of the targetHttpProxy to be inserted.
    * @param targetHttpProxyOptions the options of the targetHttpProxy to add.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetHttpProxys:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/targetHttpProxies")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(PayloadBinder.class)
   Operation create(@PayloadParam("name") String name,
                    @PayloadParam("options") TargetHttpProxyOptions targetHttpProxyOptions);
   
   /**
    * Creates a targetHttpProxy resource in the specified project using the given URI for the urlMap.
    *
    * @param name            the name of the targetHttpProxy to be inserted.
    * @param urlMap          URI of the urlMap this proxy points to.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetHttpProxys:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/targetHttpProxies")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String name, @PayloadParam("urlMap") URI urlMap);

   /**
    * Updates the specified targetHttpProxy resource with the data included in the request.
    *
    * @param targetHttpProxyName    the name targetHttpProxy to be updated.
    * @param urlMap                 the new url map this target http proxy points to.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetHttpProxys:setUrlMap")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("targetHttpProxies/{targetHttpProxy}/setUrlMap")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   Operation setUrlMap(@PathParam("targetHttpProxy") String targetHttpProxyName,
                       @PayloadParam("urlMap") URI urlMap);

   /**
    * Deletes the specified image resource.
    *
    * @param targetHttpProxyName name of the targetHttpProxy resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the image did not exist the result is null.
    */
   @Named("TargetHttpProxys:delete")
   @DELETE
   @Path("/global/targetHttpProxies/{targetHttpProxy}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("targetHttpProxy") String targetHttpProxyName);

   /**
    * @see TargetHttpProxyApi#list(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("TargetHttpProxys:list")
   @GET
   @Path("/global/targetHttpProxies")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetHttpProxies.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<TargetHttpProxy> listFirstPage();

   /**
    * @see TargetHttpProxyApi#list(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("TargetHttpProxys:list")
   @GET
   @Path("/global/targetHttpProxies")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetHttpProxies.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<TargetHttpProxy> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the list of targetHttpProxy resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param marker      marks the beginning of the next list page.
    * @param listOptions listing options.
    * @return a page of the list.
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("TargetHttpProxys:list")
   @GET
   @Path("/global/targetHttpProxies")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetHttpProxies.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<TargetHttpProxy> list(@QueryParam("pageToken") @Nullable String marker, ListOptions options);

   /**
    * @see TargetHttpProxyApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("TargetHttpProxys:list")
   @GET
   @Path("/global/targetHttpProxies")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetHttpProxies.class)
   @Transform(ParseTargetHttpProxies.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<TargetHttpProxy> list();

   /**
    * A paged version of TargetHttpProxyApi#list().
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see TargetHttpProxyApi#list(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("TargetHttpProxys:list")
   @GET
   @Path("/global/targetHttpProxies")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetHttpProxies.class)
   @Transform(ParseTargetHttpProxies.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<TargetHttpProxy> list(ListOptions options);
}
