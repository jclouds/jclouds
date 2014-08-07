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
import javax.ws.rs.PUT;
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
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMapValidateResult;
import org.jclouds.googlecomputeengine.functions.internal.PATCH;
import org.jclouds.googlecomputeengine.functions.internal.ParseUrlMaps;
import org.jclouds.googlecomputeengine.handlers.PayloadBinder;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to UrlMaps via their REST API.
 * <p/>
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/urlMaps"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface UrlMapApi {
   /**
    * Returns the specified urlMap resource.
    *
    * @param urlMapName name of the urlMap resource to return.
    * @return an UrlMap resource.
    */
   @Named("UrlMaps:get")
   @GET
   @Path("/global/urlMaps/{urlMap}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   UrlMap get(@PathParam("urlMap") String urlMapName);

   /**
    * Creates a urlMap resource in the specified project using the data included in the request.
    *
    * @param name            the name of the urlMap to be inserted.
    * @param urlMapOptions   the options of the urlMap to add.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("UrlMaps:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/urlMaps")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(PayloadBinder.class)
   Operation create(@PayloadParam("name") String name, @PayloadParam("options") UrlMapOptions urlMapOptions);
   
   /**
    * Creates a urlMap resource in the specified project using the data included in the request.
    *
    * @param name            the name of the urlMap to be inserted.
    * @param defaultService  the default backend service of the urlMap to add.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("UrlMaps:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/urlMaps")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String name,
                    @PayloadParam("defaultService") URI defaultService);

   /**
    * Updates the specified urlMap resource with the data included in the request.
    *
    * @param urlMapName    the name urlMap to be updated.
    * @param urlMapOptions the new urlMap options.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("UrlMaps:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/urlMaps/{urlMap}")
   @OAuthScopes({COMPUTE_SCOPE})
   Operation update(@PathParam("urlMap") String urlMapName,
                    @BinderParam(BindToJsonPayload.class) UrlMapOptions urlMapOptions);

   /**
    * Updates the specified urlMap resource, with patch semantics, with the data included in the request.
    *
    * @param urlMapName    the name urlMap to be updated.
    * @param urlMapOptions the new urlMap options.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("UrlMaps:patch")
   @PATCH
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/urlMaps/{urlMap}")
   @OAuthScopes({COMPUTE_SCOPE})
   Operation patch(@PathParam("urlMap") String urlMapName,
                   @BinderParam(BindToJsonPayload.class) UrlMapOptions urlMapOptions);

   /**
    * Deletes the specified urlMap resource.
    *
    * @param urlMapName name of the urlMap resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the image did not exist the result is null.
    */
   @Named("UrlMaps:delete")
   @DELETE
   @Path("/global/urlMaps/{urlMap}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("urlMap") String urlMapName);
   
   /**
    * Runs the tests specified for the give urlMap resource.
    *
    * @param urlMapName name of the urlMap to run tests on.
    * @param options    options that represent the url map to be tested.
    * @return the result of the tests for the given urlMap resource.
    */
   @Named("UrlMaps:validate")
   @POST
   @Path("/global/urlMaps/{urlMap}/validate")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   @MapBinder(BindToJsonPayload.class)
   UrlMapValidateResult validate(@PathParam("urlMap") String urlMapName,
                                 @PayloadParam("resource") UrlMapOptions options);
   
   /**
    * Runs the tests specified for the give urlMap resource.
    *
    * @param urlMapName name of the urlMap to run tests on.
    * @param urlMap     the url map to be tested.
    * @return the result of the tests for the given urlMap resource.
    */
   @Named("UrlMaps:validate")
   @POST
   @Path("/global/urlMaps/{urlMap}/validate")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   @MapBinder(BindToJsonPayload.class)
   UrlMapValidateResult validate(@PathParam("urlMap") String urlMapName,
                                 @PayloadParam("resource") UrlMap urlMap);

   /**
    * @see UrlMapApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("UrlMaps:list")
   @GET
   @Path("/global/urlMaps")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseUrlMaps.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<UrlMap> listFirstPage();

   /**
    * @see UrlMapApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("UrlMaps:list")
   @GET
   @Path("/global/urlMaps")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseUrlMaps.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<UrlMap> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the list of urlMap resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param marker      marks the beginning of the next list page.
    * @param listOptions listing options.
    * @return a page of the list.
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("UrlMaps:list")
   @GET
   @Path("/global/urlMaps")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseUrlMaps.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<UrlMap> listAtMarker(@QueryParam("pageToken") @Nullable String marker, ListOptions options);

   /**
    * @see UrlMapApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("UrlMaps:list")
   @GET
   @Path("/global/urlMaps")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseUrlMaps.class)
   @Transform(ParseUrlMaps.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<UrlMap> list();

   /**
    * A paged version of UrlMapApi#list().
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see UrlMapApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("UrlMaps:list")
   @GET
   @Path("/global/urlMaps")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseUrlMaps.class)
   @Transform(ParseUrlMaps.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<UrlMap> list(ListOptions options);
}
