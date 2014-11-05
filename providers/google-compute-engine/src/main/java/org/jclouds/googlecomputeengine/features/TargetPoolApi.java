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
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

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
import org.jclouds.googlecomputeengine.GoogleComputeEngineFallbacks.EmptyIteratorOnNotFoundOr404;
import org.jclouds.googlecomputeengine.GoogleComputeEngineFallbacks.EmptyListPageOnNotFoundOr404;
import org.jclouds.googlecomputeengine.binders.TargetPoolChangeHealthChecksBinder;
import org.jclouds.googlecomputeengine.binders.TargetPoolChangeInstancesBinder;
import org.jclouds.googlecomputeengine.binders.TargetPoolCreationBinder;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.functions.internal.ParseTargetPools;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;
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

@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Path("/targetPools")
@Consumes(APPLICATION_JSON)
public interface TargetPoolApi {

   /** Returns a target pool by name or null if not found. */
   @Named("TargetPools:get")
   @GET
   @Path("/{targetPool}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   TargetPool get(@PathParam("targetPool") String targetPool);

   /**
    * Creates a TargetPool resource in the specified project and region using the data included in the request.
    *
    * @param name the name of the targetPool.
    * @param options options of the TargetPool to create.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:insert")
   @POST
   @Produces(APPLICATION_JSON)
   @Path("")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(TargetPoolCreationBinder.class)
   Operation create(@PayloadParam("name") String name, @PayloadParam("options") TargetPoolCreationOptions options);

   /** Deletes a target pool by name and returns the operation in progress, or null if not found. */
   @Named("TargetPools:delete")
   @DELETE
   @Path("/{targetPool}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("targetPool") String targetPool);

   /**
    * Adds instance to the targetPool.
    *
    * @param targetPool the name of the target pool.
    * @param instances the self-links of the instances to be added to targetPool.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:addInstance")
   @POST
   @Path("/{targetPool}/addInstance")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(TargetPoolChangeInstancesBinder.class)
   @Nullable
   Operation addInstance(@PathParam("targetPool") String targetPool, @PayloadParam("instances") List<URI> instances);

   /**
    * Removes instance URL from targetPool.
    *
    * @param targetPool the name of the target pool.
    * @param instances the self-links of the instances to be removed from the targetPool.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:removeInstance")
   @POST
   @Path("/{targetPool}/removeInstance")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(TargetPoolChangeInstancesBinder.class)
   @Nullable
   Operation removeInstance(@PathParam("targetPool") String targetPool, @PayloadParam("instances") List<URI> instances);

   /**
    * Adds health check URL to targetPool.
    *
    * @param targetPool the name of the target pool.
    * @param healthChecks the self-links of the health checks to be added to targetPool.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:addHealthCheck")
   @POST
   @Path("/{targetPool}/addHealthCheck")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(TargetPoolChangeHealthChecksBinder.class)
   @Nullable
   Operation addHealthCheck(@PathParam("targetPool") String targetPool, @PayloadParam("healthChecks") List<URI> healthChecks);


   /**
    * Removes health check URL from targetPool.
    *
    * @param targetPool the name of the target pool.
    * @param healthChecks the self-links of the health checks to be removed from the targetPool.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:removeHealthChek")
   @POST
   @Path("/{targetPool}/removeHealthCheck")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(TargetPoolChangeHealthChecksBinder.class)
   @Nullable
   Operation removeHealthCheck(@PathParam("targetPool") String targetPool, @PayloadParam("healthChecks") List<URI> healthChecks);


   /**
    * Changes backup pool configurations.
    *
    * @param targetPool the name of the target pool.
    * @param target the URL of target pool for which you want to use as backup.
    * WARNING: failoverRatio and BackupPool must either both be set or not set. This method
    *          is only for updating the backup pool on a Target Pool that already has a
    *          failoverRatio.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:setBackup")
   @POST
   @Path("/{targetPool}/setBackup")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   @Nullable
   Operation setBackup(@PathParam("targetPool") String targetPool, @PayloadParam("target") URI target);

   /**
    * Changes backup pool configurations.
    *
    * @param targetPool the name of the target pool.
    * @param target the URL of target pool for which you want to use as backup.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:setBackup")
   @POST
   @Path("/{targetPool}/setBackup")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   @Nullable
   Operation setBackup(@PathParam("targetPool") String targetPool, @QueryParam("failoverRatio") Float failoverRatio, @PayloadParam("target") URI target);

   /**
    * Retrieves the list of target pool resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param token       marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("TargetPools:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetPools.class)
   @Fallback(EmptyListPageOnNotFoundOr404.class)
   ListPage<TargetPool> listPage(@Nullable @QueryParam("pageToken") String token, ListOptions listOptions);

   /**
    * @see #list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("TargetPools:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetPools.class)
   @Transform(ParseTargetPools.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<TargetPool>> list();

   /**
    * @see #list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("TargetPools:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetPools.class)
   @Transform(ParseTargetPools.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<TargetPool>> list(ListOptions options);
}
