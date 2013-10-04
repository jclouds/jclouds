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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Kernel;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.functions.internal.ParseKernels;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

/**
 * Provides access to Kernels via their REST API.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta16/kernels"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface KernelApi {

   /**
    * Returns the specified kernel resource
    *
    * @param kernelName name of the kernel resource to return.
    * @return If successful, this method returns a Kernel resource
    */
   @Named("Kernels:get")
   @GET
   @Path("/global/kernels/{kernel}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Kernel get(@PathParam("kernel") String kernelName);

   /**
    * @see KernelApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Kernels:list")
   @GET
   @Path("/global/kernels")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseKernels.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Kernel> listFirstPage();

   /**
    * @see KernelApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Kernels:list")
   @GET
   @Path("/global/kernels")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseKernels.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Kernel> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the list of kernel resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("Kernels:list")
   @GET
   @Path("/global/kernels")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseKernels.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Kernel> listAtMarker(@QueryParam("pageToken") @Nullable String marker,
                                 ListOptions listOptions);

   /**
    * @see KernelApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Kernels:list")
   @GET
   @Path("/global/kernels")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseKernels.class)
   @Transform(ParseKernels.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Kernel> list();

   /**
    * A paged version of KernelApi#list()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see KernelApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Kernels:list")
   @GET
   @Path("/global/kernels")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseKernels.class)
   @Transform(ParseKernels.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Kernel> list(ListOptions listOptions);

}
