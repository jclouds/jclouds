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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.collect.FluentIterable;

/**
 * Provides access to the OpenStack Compute (Nova) Volume Attachments Extension API.
 *
 * This API strictly handles attaching Volumes to Servers. To create and manage Volumes you need to use the Cinder API.
 * @see org.jclouds.openstack.cinder.v1.features.VolumeApi
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUME_ATTACHMENTS)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/servers")
public interface VolumeAttachmentApi {
   /**
    * Lists Volume Attachments for a given Server.
    *
    * @param serverId The ID of the Server
    * @return All VolumeAttachments for the Server
    */
   @Named("volumeAttachment:list")
   @GET
   @Path("/{serverId}/os-volume_attachments")
   @SelectJson("volumeAttachments")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<VolumeAttachment> listAttachmentsOnServer(@PathParam("serverId") String serverId);

   /**
    * Gets a specific Volume Attachment for a Volume and Server.
    *
    * @param volumeId The ID of the Volume
    * @param serverId The ID of the Server
    * @return The Volume Attachment.
    */
   @Named("volumeAttachment:get")
   @GET
   @Path("/{serverId}/os-volume_attachments/{id}")
   @SelectJson("volumeAttachment")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   VolumeAttachment getAttachmentForVolumeOnServer(@PathParam("id") String volumeId,
         @PathParam("serverId") String serverId);

   /**
    * Attaches a Volume to a Server.
    *
    * Note: If you are using KVM as your hypervisor then the actual device name in the Server will be different than
    * the one specified. When the Server sees a new device, it picks the next available name (which in most cases is
    * /dev/vdc) and the disk shows up there on the Server.
    *
    * @param serverId The ID of the Server
    * @param volumeId The ID of the Volume
    * @param device The name of the device this Volume will be identified as in the Server (e.g. /dev/vdc)
    * @return The Volume Attachment.
    */
   @Named("volumeAttachment:attach")
   @POST
   @Path("/{serverId}/os-volume_attachments")
   @SelectJson("volumeAttachment")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("volumeAttachment")
   VolumeAttachment attachVolumeToServerAsDevice(@PayloadParam("volumeId") String volumeId,
         @PathParam("serverId") String serverId, @PayloadParam("device") String device);

   /**
    * Detaches a Volume from a server.
    *
    * Note: Make sure you've unmounted the volume first. Failure to do so could result in failure or data loss.
    *
    * @param volumeId The ID of the Volume
    * @param serverId The ID of the Server
    * @return true if successful
    */
   @Named("volumeAttachment:detach")
   @DELETE
   @Path("/{serverId}/os-volume_attachments/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean detachVolumeFromServer(@PathParam("id") String volumeId,
         @PathParam("serverId") String serverId);
}
