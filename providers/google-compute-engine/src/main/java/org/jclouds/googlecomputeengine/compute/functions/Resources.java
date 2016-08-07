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
package org.jclouds.googlecomputeengine.compute.functions;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.net.URI;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Subnetwork;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Consumes(APPLICATION_JSON)
public interface Resources {

   /** Returns an instance by self-link or null if not found. */
   @Named("Instances:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class) @Nullable Instance instance(@EndpointParam URI selfLink);

   /** Returns an network by self-link or null if not found. */
   @Named("Networks:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class) @Nullable Network network(@EndpointParam URI selfLink);

   /** Returns an operation by self-link or null if not found. */
   @Named("Operations:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class) @Nullable Operation operation(@EndpointParam URI selfLink);

   /** Deletes any resource by self-link and returns the operation in progress, or null if not found. */
   @Named("Resources:delete")
   @DELETE
   @Fallback(NullOnNotFoundOr404.class) @Nullable Operation delete(@EndpointParam URI selfLink);

   /** Hard-resets the instance by self-link and returns the operation in progress */
   @Named("Instances:reset")
   @POST
   @Path("/reset")
   Operation resetInstance(@EndpointParam URI selfLink);
   
   /** Starts the instance by self-link and returns the operation in progress */
   @Named("Instances:start")
   @POST
   @Path("/start")
   Operation startInstance(@EndpointParam URI selfLink);
   
   /** Stops the instance by self-link and returns the operation in progress */
   @Named("Instances:stop")
   @POST
   @Path("/stop")
   Operation stopInstance(@EndpointParam URI selfLink);

   @Named("Subnetworks:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class) @Nullable Subnetwork subnetwork(@EndpointParam URI selfLink);

   /** Returns a disk by self-link or null if not found. */
   @Named("Disks:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class) @Nullable Disk disk(@EndpointParam URI selfLink);

   /** Returns an image by self-link or null if not found. */
   @Named("Images:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class) @Nullable Image image(@EndpointParam URI selfLink);
}
