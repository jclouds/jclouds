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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Snapshot;
import org.jclouds.googlecomputeengine.internal.BaseToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Path("/snapshots")
@Consumes(APPLICATION_JSON)
public interface SnapshotApi {

   /** Returns a snapshot by name or null if not found. */
   @Named("Snapshots:get")
   @GET
   @Path("/{snapshot}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Snapshot get(@PathParam("snapshot") String snapshot);

   /** Deletes a snapshot by name and returns the operation in progress, or null if not found. */
   @Named("Snapshots:delete")
   @DELETE
   @Path("/{snapshot}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("snapshot") String snapshot);

   /**
    * Retrieves the list of snapshot resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Snapshots:list")
   @GET
   ListPage<Snapshot> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("Snapshots:list")
   @GET
   @Transform(SnapshotPages.class)
   Iterator<ListPage<Snapshot>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("Snapshots:list")
   @GET
   @Transform(SnapshotPages.class)
   Iterator<ListPage<Snapshot>> list(ListOptions options);

   static final class SnapshotPages extends BaseToIteratorOfListPage<Snapshot, SnapshotPages> {

      private final GoogleComputeEngineApi api;

      @Inject SnapshotPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<Snapshot>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Snapshot>>() {
            @Override public ListPage<Snapshot> apply(String pageToken) {
               return api.snapshots().listPage(pageToken, options);
            }
         };
      }
   }
}
