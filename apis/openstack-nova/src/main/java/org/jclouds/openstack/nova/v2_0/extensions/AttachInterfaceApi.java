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

package org.jclouds.openstack.nova.v2_0.extensions;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.InterfaceAttachment;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Provides access to the OpenStack Compute (Nova) Attach Interfaces API.
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.ATTACH_INTERFACES)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/servers")
public interface AttachInterfaceApi {

   /**
    * Returns list of port interfaces for given server
    * 
    * @param serverId
    *           The Server ID
    * @return list of port interfaces for given server
    */
   @Named("attachInterface:list")
   @GET
   @Path("/{serverId}/os-interface")
   @SelectJson("interfaceAttachments")
   @Fallback(Fallbacks.EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<InterfaceAttachment> list(@PathParam("serverId") String serverId);

   /**
    * Returns information about a specified port interface for given server
    * 
    * @param serverId
    *           The Server ID
    * @param attachmentId
    *           The interface ID
    * @return information about a specified port interface for given server
    */
   @Named("attachInterface:get")
   @GET
   @Path("/{serverId}/os-interface/{attachmentId}")
   @SelectJson("interfaceAttachment")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Nullable
   InterfaceAttachment get(@PathParam("serverId") String serverId, @PathParam("attachmentId") String attachmentId);

   /**
    * Creates a new port interface and associate with the given port
    * 
    * @param portId
    *           The port ID
    * @return newly created port interface
    */
   @Named("attachInterface:create")
   @POST
   @Path("/{serverId}/os-interface")
   @SelectJson("interfaceAttachment")
   @Payload("%7B\"interfaceAttachment\":%7B\"port_id\":\"{portId}\"%7D%7D")
   @Produces(MediaType.APPLICATION_JSON)
   InterfaceAttachment create(@PathParam("serverId") String serverId, @PayloadParam("portId") String portId);

   /**
    * Deletes a port interface for given server, return true if successful,
    * false if server or interface not found
    *
    * @param serverId
    *           The Server ID
    * @param attachmentId
    *           The interface ID
    * @return true if successful, false if server or interface not found
    */
   @Named("attachInterface:delete")
   @DELETE
   @Path("/{serverId}/os-interface/{attachmentId}")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("serverId") String serverId, @PathParam("attachmentId") String attachmentId);
}
