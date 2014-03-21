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
import org.jclouds.openstack.nova.v2_0.domain.Volume;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.domain.VolumeSnapshot;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeOptions;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeSnapshotOptions;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides access to the OpenStack Compute (Nova) Volume extension API.
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUMES)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface VolumeApi {
   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   @Named("volume:list")
   @GET
   @Path("/os-volumes")
   @SelectJson("volumes")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Volume> list();

   /**
    * Returns a detailed list of volumes.
    *
    * @return the list of volumes.
    */
   @Named("volume:list")
   @GET
   @Path("/os-volumes/detail")
   @SelectJson("volumes")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Volume> listInDetail();

   /**
    * Return data about the given volume.
    *
    * @return details of a specific snapshot.
    */
   @Named("volume:get")
   @GET
   @Path("/os-volumes/{id}")
   @SelectJson("volume")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Volume get(@PathParam("id") String volumeId);

   /**
    * Creates a new Snapshot
    *
    * @return the new Snapshot
    */
   @Named("volume:create")
   @POST
   @Path("/os-volumes")
   @SelectJson("volume")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(CreateVolumeOptions.class)
   Volume create(@PayloadParam("size") int sizeGB, CreateVolumeOptions... options);

   /**
    * Delete a snapshot.
    *
    * @return true if successful
    */
   @Named("volume:delete")
   @DELETE
   @Path("/os-volumes/{volumeId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("volumeId") String volumeId);

   /**
    * List volume attachments for a given instance.
    *
    * @return all Floating IPs
    * @deprecated To be removed in jclouds 1.7
    * @see VolumeAttachmentApi#listAttachmentsOnServer(String)
    */
   @Deprecated
   @Named("volume:listAttachments")
   @GET
   @Path("/servers/{id}/os-volume_attachments")
   @SelectJson("volumeAttachments")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<VolumeAttachment> listAttachmentsOnServer(@PathParam("id") String serverId);

   /**
    * Get a specific attached volume.
    *
    * @return data about the given volume attachment.
    * @deprecated To be removed in jclouds 1.7
    * @see VolumeAttachmentApi#getAttachmentForVolumeOnServer(String, String)
    */
   @Deprecated
   @Named("volume:getAttachments")
   @GET
   @Path("/servers/{serverId}/os-volume_attachments/{id}")
   @SelectJson("volumeAttachment")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   VolumeAttachment getAttachmentForVolumeOnServer(@PathParam("id") String volumeId,
         @PathParam("serverId") String serverId);

   /**
    * Attach a volume to an instance
    *
    * @return data about the new volume attachment
    * @deprecated To be removed in jclouds 1.7
    * @see VolumeAttachmentApi#attachVolumeToServerAsDevice(String, String, String)
    */
   @Deprecated
   @Named("volume:attach")
   @POST
   @Path("/servers/{serverId}/os-volume_attachments")
   @SelectJson("volumeAttachment")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("volumeAttachment")
   VolumeAttachment attachVolumeToServerAsDevice(@PayloadParam("volumeId") String volumeId,
         @PathParam("serverId") String serverId, @PayloadParam("device") String device);

   /**
    * Detach a Volume from an instance.
    *
    * @return true if successful
    * @deprecated To be removed in jclouds 1.7
    * @see VolumeAttachmentApi#detachVolumeFromServer(String, String)
    */
   @Deprecated
   @Named("volume:detach")
   @DELETE
   @Path("/servers/{serverId}/os-volume_attachments/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   Boolean detachVolumeFromServer(@PathParam("id") String volumeId,
         @PathParam("serverId") String serverId);

   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   @Named("volume:listSnapshots")
   @GET
   @Path("/os-snapshots")
   @SelectJson("snapshots")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<VolumeSnapshot> listSnapshots();

   /**
    * Returns a summary list of snapshots.
    *
    * @return the list of snapshots
    */
   @Named("volume:listSnapshots")
   @GET
   @Path("/os-snapshots/detail")
   @SelectJson("snapshots")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<VolumeSnapshot> listSnapshotsInDetail();

   /**
    * Return data about the given snapshot.
    *
    * @return details of a specific snapshot.
    */
   @Named("volume:getSnapshot")
   @GET
   @Path("/os-snapshots/{id}")
   @SelectJson("snapshot")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   VolumeSnapshot getSnapshot(@PathParam("id") String snapshotId);

   /**
    * Creates a new Snapshot.
    *
    * @return the new Snapshot
    */
   @Named("volume:createSnapshot")
   @POST
   @Path("/os-snapshots")
   @SelectJson("snapshot")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(CreateVolumeSnapshotOptions.class)
   VolumeSnapshot createSnapshot(@PayloadParam("volume_id") String volumeId, CreateVolumeSnapshotOptions... options);

   /**
    * Delete a snapshot.
    *
    * @return true if successful
    */
   @Named("volume:deleteSnapshot")
   @DELETE
   @Path("/os-snapshots/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteSnapshot(@PathParam("id") String snapshotId);
}
