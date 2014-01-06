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
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.functions.internal.ParseTargetPools;
import org.jclouds.googlecomputeengine.options.ListOptions;
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
import java.net.URI;
import java.util.List;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;

/**
 * Provides access to TargetPools via their REST API.
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface TargetPoolApi {

   /**
    * Returns the specified TargetPool resource.
    *
    * @param targetPool the name of the TargetPool resource to return.
    * @return a TargetPool resource.
    */
   @Named("TargetPools:get")
   @GET
   @Path("/targetPools/{targetPool}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   TargetPool get(@PathParam("targetPool") String targetPool);

   /**
    * Creates a TargetPool resource in the specified project and region using the data included in the request.
    *
    * @param targetPoolName the name of the targetPool.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/targetPools")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String targetPoolName);

   /**
    * Creates a TargetPool resource in the specified project and region using the data included in the request.
    *
    * @param targetPoolName the name of the targetPool.
    * @param instances A list of resource URLs to the member VMs serving this pool. They must live in zones
    *                  contained in the same region as this pool.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/targetPools")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String targetPoolName, @PayloadParam("instances") List<URI> instances);

   /**
    * Creates a TargetPool resource in the specified project and region using the data included in the request.
    *
    * @param targetPoolName the name of the targetPool.
    * @param instances A list of resource URLs to the member VMs serving this pool. They must live in zones
    *                  contained in the same region as this pool.
    * @param healthChecks A URL to one HttpHealthCheck resource. A member VM in this pool is considered healthy if
    *                     and only if the specified health checks pass. An empty list means all member virtual
    *                     machines will  be considered healthy at all times but the health status of this target
    *                     pool will be marked as unhealthy to indicate that no health checks are being performed.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/targetPools")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String targetPoolName, @PayloadParam("instances") List<URI> instances,
                    @PayloadParam("healthChecks") List<URI> healthChecks);

   /**
    * Creates a TargetPool resource in the specified project and region using the data included in the request.
    *
    * @param targetPoolName the name of the targetPool.
    * @param instances A list of resource URLs to the member VMs serving this pool. They must live in zones
    *                  contained in the same region as this pool.
    * @param healthChecks A URL to one HttpHealthCheck resource. A member VM in this pool is considered healthy if
    *                     and only if the specified health checks pass. An empty list means all member virtual
    *                     machines will  be considered healthy at all times but the health status of this target
    *                     pool will be marked as unhealthy to indicate that no health checks are being performed.
    * @param backupPool it is applicable only when the target pool is serving a forwarding rule as the primary pool.
    *                   Must be a fully-qualified URL to a target pool that is in the same region as the primary
    *                   target pool.
    * @param sessionAffinity Defines the session affinity option. Session affinity determines the hash method that
    *                        Google Compute Engine uses to distribute traffic. Acceptable values are:
    *                        "CLIENT_IP": Connections from the same client IP are guaranteed to go to the same VM in the pool while that VM remains healthy.
    *                        "CLIENT_IP_PROTO":  Connections from the same client IP and port are guaranteed to go to the same VM in the pool while that VM remains healthy.
    *                        "NONE": Connections from the same client IP may go to any VM in the pool.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/targetPools")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String targetPoolName, @PayloadParam("instances") List<URI> instances,
                    @PayloadParam("healthChecks") List<URI> healthChecks, @PayloadParam("backupPool") String backupPool,
                    @PayloadParam("sessionAffinity") String sessionAffinity);

   /**
    * Deletes the specified TargetPool resource.
    *
    * @param targetPool name of the persistent target pool resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:delete")
   @DELETE
   @Path("/targetPools/{targetPool}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("targetPool") String targetPool);

   /**
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see org.jclouds.collect.PagedIterable
    */
   @Named("TargetPools:list")
   @GET
   @Path("/targetPools")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetPools.class)
   @Transform(ParseTargetPools.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<TargetPool> list();

   /**
    * @param options @see org.jclouds.googlecomputeengine.options.ListOptions
    * @return IterableWithMarker
    */
   @Named("TargetPools:list")
   @GET
   @Path("/targetPools")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseTargetPools.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   IterableWithMarker<TargetPool> list(ListOptions options);

   /**
    * Adds instance to the targetPool.
    *
    * @param targetPool the name of the target pool.
    * @param instanceName the name for the instance to be added to targetPool.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:addInstance")
   @POST
   @Path("/targetPools/{targetPool}/addInstance")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   @Nullable
   Operation addInstance(@PathParam("targetPool") String targetPool, @PayloadParam("instance") String instanceName);

   /**
    * Adds health check URL to targetPool.
    *
    * @param targetPool the name of the target pool.
    * @param healthCheck the name for the healthCheck to be added to targetPool.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:addHealthCheck")
   @POST
   @Path("/targetPools/{targetPool}/addHealthCheck")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   @Nullable
   Operation addHealthCheck(@PathParam("targetPool") String targetPool, @PayloadParam("healthCheck") String healthCheck);

   /**
    * Removes instance URL from targetPool.
    *
    * @param targetPool the name of the target pool.
    * @param instanceName the name for the instance to be removed from targetPool.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("TargetPools:removeInstance")
   @POST
   @Path("/targetPools/{targetPool}/removeInstance")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   @Nullable
   Operation removeInstance(@PathParam("targetPool") String targetPool, @PayloadParam("instanceName") String instanceName);

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
   @Path("/targetPools/{targetPool}/setBackup")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @MapBinder(BindToJsonPayload.class)
   @Nullable
   Operation setBackup(@PathParam("targetPool") String targetPool, @PayloadParam("target") String target);
}
