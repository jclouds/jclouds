/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.googlecomputeengine.features;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseInstances;
import org.jclouds.googlecomputeengine.handlers.InstanceBinder;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to Instances via their REST API.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/instances"/>
 * @see InstanceApi
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface InstanceApi {

   /**
    * Returns the specified instance resource.
    *
    * @param instanceName name of the instance resource to return.
    * @return an Instance resource
    */
   @Named("Instances:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Instance get(@PathParam("instance") String instanceName);

   /**
    * Creates a instance resource in the specified project using the data included in the request.
    *
    * @param instanceName this name of the instance to be created
    * @param template the instance template
    * @param zone the name of the zone where the instance will be created
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(InstanceBinder.class)
   Operation createInZone(@PayloadParam("name") String instanceName,
                          @PayloadParam("template") InstanceTemplate template,
                          @PayloadParam("zone") String zone);

   /**
    * Deletes the specified instance resource.
    *
    * @param instanceName name of the instance resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the instance did not exist the result is null.
    */
   @Named("Instances:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("instance") String instanceName);

   /**
    * A paged version of InstanceApi#list()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see InstanceApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Instance> listFirstPage();

   /**
    * Retrieves the list of instance resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Instance> listAtMarker(@Nullable String marker);

   /**
    * @see InstanceApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<Instance> listAtMarker(@Nullable String marker, ListOptions options);

   /**
    * @see InstanceApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Transform(ParseInstances.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Instance> list();

   /**
    * @see InstanceApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Transform(ParseInstances.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Instance> list(ListOptions options);

   /**
    * Adds an access config to an instance's network interface.
    *
    * @param instanceName         the instance name.
    * @param accessConfig         the AccessConfig to add.
    * @param networkInterfaceName network interface name.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:addAccessConfig")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}/addAccessConfig")
   @OAuthScopes({COMPUTE_SCOPE})
   Operation addAccessConfigToNic(@PathParam("instance") String instanceName,
                                  @BinderParam(BindToJsonPayload.class)
                                  Instance.NetworkInterface.AccessConfig accessConfig,
                                  @QueryParam("network_interface") String networkInterfaceName);

   /**
    * Deletes an access config from an instance's network interface.
    *
    * @param instanceName         the instance name.
    * @param accessConfigName     the name of the access config to delete
    * @param networkInterfaceName network interface name.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:deleteAccessConfig")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}/deleteAccessConfig")
   @OAuthScopes(COMPUTE_SCOPE)
   Operation deleteAccessConfigFromNic(@PathParam("instance") String instanceName,
                                       @QueryParam("access_config") String accessConfigName,
                                       @QueryParam("network_interface") String networkInterfaceName);

   /**
    * Returns the specified instance's serial port output.
    *
    * @param instanceName the instance name.
    * @return if successful, this method returns a SerialPortOutput containing the instance's serial output.
    */
   @Named("Instances:serialPort")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}/serialPort")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   Instance.SerialPortOutput getSerialPortOutput(@PathParam("instance") String instanceName);
}
