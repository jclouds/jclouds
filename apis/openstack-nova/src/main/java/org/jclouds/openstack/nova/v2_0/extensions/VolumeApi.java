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

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides access to the OpenStack Compute (Nova) Volume extension API.
 *
 * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.VolumeApi} or
 *             {@link org.jclouds.openstack.cinder.v1.features.VolumeApi} instead. To be removed in jclouds 2.0.
 */
@Deprecated
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.VOLUMES)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface VolumeApi {
   /**
    * Returns a summary list of snapshots.
    *
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.VolumeApi#list()} instead.
    *             To be removed in jclouds 2.0.
    * @return the list of snapshots
    */
   @Deprecated
   @Named("volume:list")
   @GET
   @Path("/os-volumes")
   @SelectJson("volumes")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Volume> list();

   /**
    * Returns a detailed list of volumes.
    *
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.VolumeApi#listInDetail()} instead.
    *             To be removed in jclouds 2.0.
    * @return the list of volumes.
    */
   @Deprecated
   @Named("volume:list")
   @GET
   @Path("/os-volumes/detail")
   @SelectJson("volumes")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Volume> listInDetail();

   /**
    * Return data about the given volume.
    *
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.VolumeApi#get(String)} instead.
    *             To be removed in jclouds 2.0.
    * @return details of a specific snapshot.
    */
   @Deprecated
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
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.VolumeApi#create(int, CreateVolumeOptions)} instead.
    *             To be removed in jclouds 2.0.
    * @return the new Snapshot
    */
   @Deprecated
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
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.VolumeApi#delete(String)} instead.
    *             To be removed in jclouds 2.0.
    * @return true if successful
    */
   @Deprecated
   @Named("volume:delete")
   @DELETE
   @Path("/os-volumes/{volumeId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@PathParam("volumeId") String volumeId);


   /**
    * Returns a summary list of snapshots.
    *
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.SnapshotApi#list()} instead.
    *             To be removed in jclouds 2.0.
    * @return the list of snapshots
    */
   @Deprecated
   @Named("volume:listSnapshots")
   @GET
   @Path("/os-snapshots")
   @SelectJson("snapshots")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<VolumeSnapshot> listSnapshots();

   /**
    * Returns a summary list of snapshots.
    *
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.SnapshotApi#listInDetail()} instead.
    *             To be removed in jclouds 2.0.
    * @return the list of snapshots
    */
   @Deprecated
   @Named("volume:listSnapshots")
   @GET
   @Path("/os-snapshots/detail")
   @SelectJson("snapshots")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<VolumeSnapshot> listSnapshotsInDetail();

   /**
    * Return data about the given snapshot.
    *
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.SnapshotApi#get(String)} instead.
    *             To be removed in jclouds 2.0.
    * @return details of a specific snapshot.
    */
   @Deprecated
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
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.SnapshotApi#create(String, CreateVolumeSnapshotOptions)} instead.
    *             To be removed in jclouds 2.0.
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
    * @deprecated Please use {@link org.jclouds.openstack.cinder.v1.features.SnapshotApi#delete(String)} instead.
    *             To be removed in jclouds 2.0.
    * @return true if successful
    */
   @Named("volume:deleteSnapshot")
   @DELETE
   @Path("/os-snapshots/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   boolean deleteSnapshot(@PathParam("id") String snapshotId);
}
