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
import org.jclouds.googlecomputeengine.domain.MachineType;
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
@Path("/machineTypes")
@Consumes(APPLICATION_JSON)
public interface MachineTypeApi {

   /** Returns an machine type by name or null if not found. */
   @Named("MachineTypes:get")
   @GET
   @Path("/{machineType}")
   @Fallback(NullOnNotFoundOr404.class)
   MachineType get(@PathParam("machineType") String machineType);

   /**
    * Retrieves the list of machine type resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("MachineTypes:list")
   @GET
   ListPage<MachineType> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("MachineTypes:list")
   @GET
   @Transform(MachineTypePages.class)
   Iterator<ListPage<MachineType>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("MachineTypes:list")
   @GET
   @Transform(MachineTypePages.class)
   Iterator<ListPage<MachineType>> list(ListOptions options);

   static final class MachineTypePages extends BaseCallerArg0ToIteratorOfListPage<MachineType, MachineTypePages> {

      private final GoogleComputeEngineApi api;

      @Inject MachineTypePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<MachineType>> fetchNextPage(final String zoneName, final ListOptions options) {
         return new Function<String, ListPage<MachineType>>() {
            @Override public ListPage<MachineType> apply(String pageToken) {
               return api.machineTypesInZone(zoneName).listPage(pageToken, options);
            }
         };
      }
   }
}
