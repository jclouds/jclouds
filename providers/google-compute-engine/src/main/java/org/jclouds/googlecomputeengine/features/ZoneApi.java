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
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.functions.internal.ParseZones;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

/**
 * Provides access to Zones via their REST API.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta16/zones"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface ZoneApi {

   /**
    * Returns the specified zone resource
    *
    * @param zoneName name of the zone resource to return.
    * @return If successful, this method returns a Zone resource
    */
   @Named("Zones:get")
   @GET
   @Path("/zones/{zone}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Zone get(@PathParam("zone") String zoneName);

   /**
    * @see ZoneApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Zones:list")
   @GET
   @Path("/zones")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseZones.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Zone> listFirstPage();

   /**
    * @see ZoneApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Zones:list")
   @GET
   @Path("/zones")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseZones.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Zone> listAtMarker(String marker);

   /**
    * Retrieves the listFirstPage of zone resources available to the specified project.
    * By default the listFirstPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults()
    * has not been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the listFirstPage
    * @see ListOptions
    * @see ListPage
    */
   @Named("Zones:list")
   @GET
   @Path("/zones")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseZones.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Zone> listAtMarker(String marker, ListOptions listOptions);

   /**
    * @see ZoneApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Zones:list")
   @GET
   @Path("/zones")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseZones.class)
   @Transform(ParseZones.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Zone> list();

   /**
    * A paged version of ZoneApi#listFirstPage()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see ZoneApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    * @see PagedIterable
    */
   @Named("Zones:list")
   @GET
   @Path("/zones")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseZones.class)
   @Transform(ParseZones.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Zone> list(ListOptions listOptions);
}
