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

import java.util.Iterator;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecomputeengine.GoogleComputeEngineFallbacks.EmptyIteratorOnNotFoundOr404;
import org.jclouds.googlecomputeengine.GoogleComputeEngineFallbacks.EmptyListPageOnNotFoundOr404;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Region;
import org.jclouds.googlecomputeengine.functions.internal.ParseRegions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

/**
 * Provides access to Regions via their REST API.
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface RegionApi {

   /**
    * Returns the specified region resource
    *
    * @param regionName name of the region resource to return.
    * @return If successful, this method returns a Region resource
    */
   @Named("Regions:get")
   @GET
   @Path("/regions/{region}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Region get(@PathParam("region") String regionName);

   /**
    * Retrieves the listFirstPage of region resources available to the specified project.
    * By default the listFirstPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults()
    * has not been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    */
   @Named("Regions:list")
   @GET
   @Path("/regions")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseRegions.class)
   @Fallback(EmptyListPageOnNotFoundOr404.class)
   ListPage<Region> listAtMarker(String marker, ListOptions listOptions);

   /**
    * @see RegionApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Regions:list")
   @GET
   @Path("/regions")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseRegions.class)
   @Transform(ParseRegions.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<Region>> list();

   /**
    * A paged version of RegionApi#listFirstPage()
    *
    * @return an Iterator that is able to fetch additional pages when required
    * @see RegionApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Regions:list")
   @GET
   @Path("/regions")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseRegions.class)
   @Transform(ParseRegions.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<Region>> list(ListOptions listOptions);
}
