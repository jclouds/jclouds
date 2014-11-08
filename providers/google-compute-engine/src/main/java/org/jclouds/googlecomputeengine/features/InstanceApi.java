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
import static org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig;
import static org.jclouds.googlecomputeengine.domain.Instance.SerialPortOutput;

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
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseInstances;
import org.jclouds.googlecomputeengine.options.AttachDiskOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.BinderParam;
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
@Path("/instances")
@Consumes(APPLICATION_JSON)
public interface InstanceApi {

   /** Returns an instance by name or null if not found. */
   @Named("Instances:get")
   @GET
   @Path("/{instance}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Instance get(@PathParam("instance") String instance);

   /**
    * Creates a instance resource in the specified project using the data included in the request.
    *
    * @param template the instance template
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:insert")
   @POST
   @Produces(APPLICATION_JSON)
   @OAuthScopes(COMPUTE_SCOPE)
   Operation create(@BinderParam(BindToJsonPayload.class) NewInstance template);

   /** Deletes an instance by name and returns the operation in progress, or null if not found. */
   @Named("Instances:delete")
   @DELETE
   @Path("/{instance}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Operation delete(@PathParam("instance") String instance);

   /**
    * Retrieves the list of instance resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param token       marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    */
   @Named("Instances:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Fallback(EmptyListPageOnNotFoundOr404.class)
   ListPage<Instance> listPage(@Nullable @QueryParam("pageToken") String token, ListOptions listOptions);

   /**
    * @see #list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Instances:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Transform(ParseInstances.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<Instance>> list();

   /**
    * @see #list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("Instances:list")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Transform(ParseInstances.ToIteratorOfListPage.class)
   @Fallback(EmptyIteratorOnNotFoundOr404.class)
   Iterator<ListPage<Instance>> list(ListOptions options);

   /**
    * Adds an access config to an instance's network interface.
    *
    * @param instance         the instance name.
    * @param accessConfig         the AccessConfig to add.
    * @param networkInterfaceName network interface name.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:addAccessConfig")
   @POST
   @Produces(APPLICATION_JSON)
   @Path("/{instance}/addAccessConfig")
   @OAuthScopes(COMPUTE_SCOPE)
   Operation addAccessConfigToNic(@PathParam("instance") String instance,
                                  @BinderParam(BindToJsonPayload.class)
                                  AccessConfig accessConfig,
                                  @QueryParam("network_interface") String networkInterfaceName);
  
   /**
    * Deletes an access config from an instance's network interface.
    *
    * @param instance         the instance name.
    * @param accessConfigName     the name of the access config to delete
    * @param networkInterfaceName network interface name.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:deleteAccessConfig")
   @DELETE
   @Path("/{instance}/deleteAccessConfig")
   @OAuthScopes(COMPUTE_SCOPE)
   Operation deleteAccessConfigFromNic(@PathParam("instance") String instance,
                                       @QueryParam("access_config") String accessConfigName,
                                       @QueryParam("network_interface") String networkInterfaceName);

   /**
    * Returns the specified instance's serial port output.
    *
    * @param instance the instance name.
    * @return if successful, this method returns a SerialPortOutput containing the instance's serial output.
    */
   @Named("Instances:serialPort")
   @GET
   @Path("/{instance}/serialPort")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   SerialPortOutput getSerialPortOutput(@PathParam("instance") String instance);

   /**
    * Hard-resets the instance.
    *
    * @param instance the instance name
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:reset")
   @POST
   @Path("/{instance}/reset")
   @OAuthScopes(COMPUTE_SCOPE)
   Operation reset(@PathParam("instance") String instance);

   /**
    * Attaches a disk to an instance
    *
    * @param instance The instance name to attach to
    * @param attachDiskOptions The options for attaching the disk.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:attachDisk")
   @POST
   @Produces(APPLICATION_JSON)
   @Path("/{instance}/attachDisk")
   @OAuthScopes(COMPUTE_SCOPE)
   Operation attachDisk(@PathParam("instance") String instance,
                        @BinderParam(BindToJsonPayload.class) AttachDiskOptions attachDiskOptions);

   /**
    * Detaches an attached disk from an instance
    *
    * @param instance The instance name to attach to
    * @param deviceName The device name of the disk to detach.
    *
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Instances:detachDisk")
   @POST
   @Path("/{instance}/detachDisk")
   @OAuthScopes(COMPUTE_SCOPE)
   Operation detachDisk(@PathParam("instance") String instance, @QueryParam("deviceName") String deviceName);

   /**
    * Sets metadata for an instance using the data included in the request.
    * <p/>
    * NOTE: This *sets* metadata items on the project (vs *adding* items to metadata),
    * if there are existing metadata that must be kept these must be fetched first and then re-sent on update.
    * <pre><tt>
    *    Metadata update = instanceApi.get("myInstance").metadata().clone();
    *    update.put("newItem","newItemValue");
    *    instanceApi.setMetadata("myInstance", update);
    * </tt></pre>
    *
    * @param instance The name of the instance
    * @param metadata the metadata to set
    *
    * @return an Operations resource. To check on the status of an operation, poll the Operations resource returned
    *         to you, and look for the status field.
    */
   @Named("Instances:setMetadata")
   @POST
   @Path("/{instance}/setMetadata")
   @OAuthScopes(COMPUTE_SCOPE)
   @Produces(APPLICATION_JSON)
   Operation setMetadata(@PathParam("instance") String instance,
                         @BinderParam(BindToJsonPayload.class) Metadata metadata);

   /**
    * Lists items for an instance
    *
    * @param instance the name of the instance
    * @param items A set of items
    * @param fingerprint The current fingerprint for the items
    * @return an Operations resource. To check on the status of an operation, poll the Operations resource returned
    *         to you, and look for the status field.
    */
   @Named("Instances:setTags")
   @POST
   @Path("/{instance}/setTags")
   @OAuthScopes(COMPUTE_SCOPE)
   @Produces(APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   Operation setTags(@PathParam("instance") String instance,
                     @PayloadParam("items") Iterable<String> items,
                     @PayloadParam("fingerprint") String fingerprint);
}

