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

import java.util.Iterator;

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
import org.jclouds.googlecomputeengine.binders.DiskCreationBinder;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseDisks;
import org.jclouds.googlecomputeengine.options.DiskCreationOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticationFilter.class)
@Path("/disks")
@Consumes(APPLICATION_JSON)
public interface DiskApi {

   /** Returns a persistent disk by name or null if not found. */
   @Named("Disks:get")
   @GET
   @Path("/{disk}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Disk get(@PathParam("disk") String disk);

   /**
    * Creates a persistent disk resource in the specified project specifying the size of the disk.
    *
    * @param diskName the name of disk.
    * @param sizeGb   the size of the disk
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Disks:insert")
   @POST
   @Produces(APPLICATION_JSON)
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   Operation create(@PayloadParam("name") String diskName, @PayloadParam("sizeGb") int sizeGb);

   /**
    * Creates a persistent disk resource, in the specified project, specifying the size of the disk and other options.
    *
    * @param diskName the name of disk.
    * @param sizeGb   the size of the disk
    * @param options the options of the disk to create.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Disks:insert")
   @POST
   @Produces(APPLICATION_JSON)
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(DiskCreationBinder.class)
   Operation create(@PayloadParam("name") String diskName,
                    @PayloadParam("sizeGb") int sizeGb,
                    @PayloadParam("options") DiskCreationOptions options);

   /** Deletes a persistent disk by name and returns the operation in progress, or null if not found. */
   @Named("Disks:delete")
   @DELETE
   @Path("/{disk}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("disk") String disk);

   /**
    * Create a snapshot of a given disk in a zone.
    *
    * @param diskName the name of the disk.
    * @param snapshotName the name for the snapshot to be created.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Disks:createSnapshot")
   @POST
   @Path("/{disk}/createSnapshot")
   @OAuthScopes(COMPUTE_SCOPE)
   @MapBinder(BindToJsonPayload.class)
   Operation createSnapshot(@PathParam("disk") String diskName, @PayloadParam("name") String snapshotName);

   /**
    * Retrieves the list of persistent disk resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param token       marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Disks:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseDisks.class)
   @Fallback(EmptyListPageOnNotFoundOr404.class)
   ListPage<Disk> listPage(@Nullable @QueryParam("pageToken") String token, ListOptions listOptions);

   /**
    * @see #list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Disks:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseDisks.class)
   @Transform(ParseDisks.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<Disk>> list();

   /**
    * @see #list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Disks:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseDisks.class)
   @Transform(ParseDisks.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<Disk>> list(ListOptions options);
}
