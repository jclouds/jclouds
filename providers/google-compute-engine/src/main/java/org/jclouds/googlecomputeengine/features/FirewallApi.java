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

import java.net.URI;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.binders.FirewallBinder;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Path("/firewalls")
@Consumes(APPLICATION_JSON)
public interface FirewallApi {

   /** Returns a firewall by name or null if not found. */
   @Named("Firewalls:get")
   @GET
   @Path("/{firewall}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Firewall get(@PathParam("firewall") String firewall);

   /**
    * Creates a firewall resource in the specified project using the data included in the request.
    *
    * @param name            the name of the firewall to be inserted.
    * @param network         the network to which to add the firewall
    * @param firewallOptions the options of the firewall to add
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Firewalls:insert")
   @POST
   @Produces(APPLICATION_JSON)
   @MapBinder(FirewallBinder.class)
   Operation createInNetwork(@PayloadParam("name") String name,
                             @PayloadParam("network") URI network,
                             @PayloadParam("options") FirewallOptions firewallOptions);

   /**
    * Updates the specified firewall resource with the data included in the request.
    *
    * @param firewall    the name firewall to be updated.
    * @param firewallOptions the new firewall.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Firewalls:update")
   @PUT
   @Produces(APPLICATION_JSON)
   @Path("/{firewall}")
   Operation update(@PathParam("firewall") String firewall,
                    @BinderParam(BindToJsonPayload.class) FirewallOptions firewallOptions);

   /**
    * Updates the specified firewall resource, with patch semantics, with the data included in the request.
    *
    * @param firewall    the name firewall to be updated.
    * @param firewallOptions the new firewall.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Firewalls:patch")
   @PATCH
   @Produces(APPLICATION_JSON)
   @Path("/{firewall}")
   Operation patch(@PathParam("firewall") String firewall,
                   @BinderParam(BindToJsonPayload.class) FirewallOptions firewallOptions);

   /** Deletes a firewall by name and returns the operation in progress, or null if not found. */
   @Named("Firewalls:delete")
   @DELETE
   @Path("/{firewall}")
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("firewall") String firewall);

   /**
    * Retrieves the list of firewall resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Firewalls:list")
   @GET
   ListPage<Firewall> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("Firewalls:list")
   @GET
   @Transform(FirewallPages.class)
   Iterator<ListPage<Firewall>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("Firewalls:list")
   @GET
   @Transform(FirewallPages.class)
   Iterator<ListPage<Firewall>> list(ListOptions options);

   static final class FirewallPages extends BaseToIteratorOfListPage<Firewall, FirewallPages> {

      private final GoogleComputeEngineApi api;

      @Inject FirewallPages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<Firewall>> fetchNextPage(final ListOptions options) {
         return new Function<String, ListPage<Firewall>>() {
            @Override public ListPage<Firewall> apply(String pageToken) {
               return api.firewalls().listPage(pageToken, options);
            }
         };
      }
   }
}
