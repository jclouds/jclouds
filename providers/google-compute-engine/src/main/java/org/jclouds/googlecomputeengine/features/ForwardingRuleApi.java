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

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseForwardingRules;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.ForwardingRuleCreationOptions;
import org.jclouds.googlecomputeengine.binders.ForwardingRuleCreationBinder;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;

/**
 * Provides access to ForwardingRules via their REST API.
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface ForwardingRuleApi {

   /**
    * Returns the specified ForwardingRule resource.
    *
    * @param forwardingRule the name of the ForwardingRule resource to return.
    * @return a ForwardingRule resource.
    */
   @Named("ForwardingRules:get")
   @GET
   @Path("/forwardingRules/{forwardingRule}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ForwardingRule get(@PathParam("forwardingRule") String forwardingRule);

   /**
    * Creates a ForwardingRule resource in the specified project and region using the data included in the request.
    *
    * @param forwardingRuleName the name of the forwarding rule.
    * @param targetSelfLink the URL of the target resource to receive the matched traffic. The target resource must live
    *                       in the same region as this forwarding rule.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("ForwardingRules:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/forwardingRules")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(ForwardingRuleCreationBinder.class)
   Operation create(@PayloadParam("name") String forwardingRuleName,
                    @PayloadParam("options") ForwardingRuleCreationOptions options);


   /**
    * Deletes the specified TargetPool resource.
    *
    * @param forwardingRule name of the persistent forwarding rule resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("ForwardingRules:delete")
   @DELETE
   @Path("/forwardingRules/{forwardingRule}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("forwardingRule") String forwardingRule);


   /**
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see org.jclouds.collect.PagedIterable
    */
   @Named("ForwardingRules:list")
   @GET
   @Path("/forwardingRules")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseForwardingRules.class)
   @Transform(ParseForwardingRules.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<ForwardingRule> list();

   @Named("ForwardingRules:list")
   @GET
   @Path("/forwardingRules")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseForwardingRules.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   IterableWithMarker<ForwardingRule> list(ListOptions options);

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
   @Path("/forwardingRules/{forwardingRule}/setTarget")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   @Nullable
   Operation setTarget(@PathParam("forwardingRule") String forwardingRule,
                       @PayloadParam("target") String target);
}
