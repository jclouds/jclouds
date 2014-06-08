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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Snapshot;
import org.jclouds.googlecomputeengine.functions.internal.ParseSnapshots;
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
 * Provides access to Snapshots via their REST API.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/snapshots"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface SnapshotApi {

   /**
    * Returns the specified snapshot resource.
    *
    * @param snapshotName name of the snapshot resource to return.
    * @return a Snapshot resource.
    */
   @Named("Snapshots:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/snapshots/{snapshot}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Snapshot get(@PathParam("snapshot") String snapshotName);

   /**
    * Deletes the specified snapshot resource.
    *
    * @param snapshotName name of the snapshot resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Snapshots:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/snapshots/{snapshot}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("snapshot") String snapshotName);

   /**
    * @see org.jclouds.googlecomputeengine.features.SnapshotApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Snapshots:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/snapshots")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseSnapshots.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Snapshot> listFirstPage();

   /**
    * @see org.jclouds.googlecomputeengine.features.SnapshotApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Snapshots:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/snapshots")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseSnapshots.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Snapshot> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the listPage of persistent disk resources contained within the specified project and zone.
    * By default the listPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has
    * not been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the listPage
    * @see org.jclouds.googlecomputeengine.options.ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("Snapshots:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/snapshots")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseSnapshots.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Snapshot> listAtMarker(@QueryParam("pageToken") @Nullable String marker, ListOptions listOptions);

   /**
    * A paged version of SnapshotApi#listPage(String)
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see org.jclouds.collect.PagedIterable
    * @see org.jclouds.googlecomputeengine.features.SnapshotApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Snapshots:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/snapshots")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseSnapshots.class)
   @Transform(ParseSnapshots.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Snapshot> list();

   @Named("Snapshots:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/snapshots")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseSnapshots.class)
   @Transform(ParseSnapshots.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Snapshot> list(ListOptions options);

}
