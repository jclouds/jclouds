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
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.NetworkCreationOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Path("/networks")
@Consumes(APPLICATION_JSON)
public interface NetworkApi {

   /** Returns a network by name or null if not found. */
   @Named("Networks:get")
   @GET
   @Path("/{network}")
   @Fallback(NullOnNotFoundOr404.class)
   Network get(@PathParam("network") String networkName);

   /**
    * Creates a persistent network resource in the specified project with the specified range.
    *
    * @param networkName the network name
    * @param IPv4Range   the range of the network to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Networks:insert")
   @POST
   @Produces(APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   Operation createInIPv4Range(@PayloadParam("name") String networkName,
                               @PayloadParam("IPv4Range") String IPv4Range);

   /**
    * Creates a persistent network resource in the specified project with the specified range and specified gateway.
    *
    * @param networkName the network name
    * @param IPv4Range   the range of the network to be inserted.
    * @param gatewayIPv4 the range of the network to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Networks:insert")
   @POST
   @Produces(APPLICATION_JSON)
   Operation createInIPv4Range(@BinderParam(BindToJsonPayload.class) NetworkCreationOptions options);

   /** Deletes a network by name and returns the operation in progress, or null if not found. */
   @Named("Networks:delete")
   @DELETE
   @Path("/{network}")
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("network") String networkName);

   /**
    * Retrieves the list of network resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Networks:list")
   @GET
   ListPage<Network> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("Networks:list")
   @GET
   @Transform(NetworkPages.class)
   Iterator<ListPage<Network>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("Networks:list")
   @GET
   @Transform(NetworkPages.class)
   Iterator<ListPage<Network>> list(ListOptions options);

   static final class NetworkPages extends BaseToIteratorOfListPage<Network, NetworkPages> {

      private final GoogleComputeEngineApi api;

      @Inject NetworkPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<Network>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Network>>() {
            @Override public ListPage<Network> apply(String pageToken) {
               return api.networks().listPage(pageToken, options);
            }
         };
      }
   }
}
