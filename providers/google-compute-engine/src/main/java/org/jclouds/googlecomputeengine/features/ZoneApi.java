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
import org.jclouds.googlecomputeengine.domain.Zone;
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
@Path("/zones")
@Consumes(APPLICATION_JSON)
public interface ZoneApi {

   /** Returns a zone by name or null if not found. */
   @Named("Zones:get")
   @GET
   @Path("/{zone}")
   @Fallback(NullOnNotFoundOr404.class)
   Zone get(@PathParam("zone") String zone);

   /**
    * Retrieves the list of zone resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Zones:list")
   @GET
   ListPage<Zone> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("Zones:list")
   @GET
   @Transform(ZonePages.class)
   Iterator<ListPage<Zone>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("Zones:list")
   @GET
   @Transform(ZonePages.class)
   Iterator<ListPage<Zone>> list(ListOptions options);

   static final class ZonePages extends BaseToIteratorOfListPage<Zone, ZonePages> {

      private final GoogleComputeEngineApi api;

      @Inject ZonePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<Zone>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Zone>>() {
            @Override public ListPage<Zone> apply(String pageToken) {
               return api.zones().listPage(pageToken, options);
            }
         };
      }
   }
}
