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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.NewTargetInstance;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.TargetInstance;
import org.jclouds.googlecomputeengine.internal.BaseCallerArg0ToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Path("/targetInstances")
@Consumes(APPLICATION_JSON)
public interface TargetInstanceApi {

   /** Returns a targetInstance by name or null if not found. */
   @Named("TargetInstances:get")
   @GET
   @Path("/{targetInstance}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   TargetInstance get(@PathParam("targetInstance") String targetInstance);

   /**
    * Creates a instance resource in the specified project using the data included in the request.
    *
    * @param template the instance template
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetInstances:insert")
   @POST
   @Produces(APPLICATION_JSON)
   Operation create(@BinderParam(BindToJsonPayload.class) NewTargetInstance targetInstance);

   /** Deletes a targetInstance by name and returns the operation in progress, or null if not found. */
   @Named("TargetInstances:delete")
   @DELETE
   @Path("/{targetInstance}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("targetInstance") String targetInstance);

   /**
    * Retrieves the list of targetInstance resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("TargetInstances:list")
   @GET
   ListPage<TargetInstance> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("TargetInstances:list")
   @GET
   @Transform(TargetInstancePages.class)
   Iterator<ListPage<TargetInstance>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("TargetInstances:list")
   @GET
   @Transform(TargetInstancePages.class)
   Iterator<ListPage<TargetInstance>> list(ListOptions options);

   static final class TargetInstancePages extends BaseCallerArg0ToIteratorOfListPage<TargetInstance, TargetInstancePages> {

      private final GoogleComputeEngineApi api;

      @Inject TargetInstancePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<TargetInstance>> fetchNextPage(final String zoneName, final ListOptions options) {
         return new Function<String, ListPage<TargetInstance>>() {
            @Override public ListPage<TargetInstance> apply(String pageToken) {
               return api.targetInstancesInZone(zoneName).listPage(pageToken, options);
            }
         };
      }
   }
}
