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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.DiskType;
import org.jclouds.googlecomputeengine.internal.BaseCallerArg0ToIteratorOfListPage;
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
@Path("/diskTypes")
@Consumes(APPLICATION_JSON)
public interface DiskTypeApi {

   /** Returns a disk type by name or null if not found. */
   @Named("DiskTypes:get")
   @GET
   @Path("/{diskType}")
   @Fallback(NullOnNotFoundOr404.class)
   DiskType get(@PathParam("diskType") String diskType);

   /**
    * Retrieves the list of disk type resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("DiskTypes:list")
   @GET
   ListPage<DiskType> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("DiskTypes:list")
   @GET
   @Transform(DiskTypePages.class)
   Iterator<ListPage<DiskType>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("DiskTypes:list")
   @GET
   @Transform(DiskTypePages.class)
   Iterator<ListPage<DiskType>> list(ListOptions options);

   static final class DiskTypePages extends BaseCallerArg0ToIteratorOfListPage<DiskType, DiskTypePages> {

      private final GoogleComputeEngineApi api;

      @Inject DiskTypePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<DiskType>> fetchNextPage(final String zoneName, final ListOptions options) {
         return new Function<String, ListPage<DiskType>>() {
            @Override public ListPage<DiskType> apply(String pageToken) {
               return api.diskTypesInZone(zoneName).listPage(pageToken, options);
            }
         };
      }
   }
}
