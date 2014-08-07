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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.binders.ForwardingRuleCreationBinder;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseCallerArg0ToIteratorOfListPage;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
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
@Path("/forwardingRules")
@Consumes(APPLICATION_JSON)
public interface ForwardingRuleApi {

   /** Returns a forwarding rule by name or null if not found. */
   @Named("ForwardingRules:get")
   @GET
   @Path("/{forwardingRule}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ForwardingRule get(@PathParam("forwardingRule") String forwardingRule);

   /**
    * Creates a ForwardingRule resource in the specified project and region using the data included in the request.
    *
    * @param forwardingRuleName the name of the forwarding rule.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("ForwardingRules:insert")
   @POST
   @Produces(APPLICATION_JSON)
   @MapBinder(ForwardingRuleCreationBinder.class)
   Operation create(@PayloadParam("name") String forwardingRuleName,
                    @PayloadParam("options") ForwardingRuleCreationOptions options);


   /** Deletes a forwarding rule by name and returns the operation in progress, or null if not found. */
   @Named("ForwardingRules:delete")
   @DELETE
   @Path("/{forwardingRule}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("forwardingRule") String forwardingRule);

   /**
    * Changes the target url for a forwarding rule.
    *
    * @param forwardingRule the name of the ForwardingRule resource in which target is to be set.
    * @param target The URL of the target resource to receive traffic from this forwarding rule.
    *               It must live in the same region as this forwarding rule.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("ForwardingRules:setTarget")
   @POST
   @Path("/{forwardingRule}/setTarget")
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   @Nullable
   Operation setTarget(@PathParam("forwardingRule") String forwardingRule, @PayloadParam("target") URI target);

   /**
    * Retrieves the list of forwarding rule resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param pageToken   marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("ForwardingRules:list")
   @GET
   ListPage<ForwardingRule> listPage(@Nullable @QueryParam("pageToken") String pageToken, ListOptions listOptions);

   /** @see #listPage(String, ListOptions) */
   @Named("ForwardingRules:list")
   @GET
   @Transform(ForwardingRulePages.class)
   Iterator<ListPage<ForwardingRule>> list();

   /** @see #listPage(String, ListOptions) */
   @Named("ForwardingRules:list")
   @GET
   @Transform(ForwardingRulePages.class)
   Iterator<ListPage<ForwardingRule>> list(ListOptions options);

   static final class ForwardingRulePages
         extends BaseCallerArg0ToIteratorOfListPage<ForwardingRule, ForwardingRulePages> {

      private final GoogleComputeEngineApi api;

      @Inject ForwardingRulePages(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override
      protected Function<String, ListPage<ForwardingRule>> fetchNextPage(final String regionName,
            final ListOptions options) {
         return new Function<String, ListPage<ForwardingRule>>() {
            @Override public ListPage<ForwardingRule> apply(String pageToken) {
               return api.forwardingRulesInRegion(regionName).listPage(pageToken, options);
            }
         };
      }
   }
}

