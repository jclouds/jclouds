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
package org.jclouds.openstack.cinder.v1;

import java.io.Closeable;
import java.util.Set;

import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.cinder.v1.domain.Snapshot;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.domain.VolumeType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.cinder.v1.extensions.AvailabilityZoneApi;
import org.jclouds.openstack.cinder.v1.features.QuotaApi;
import org.jclouds.openstack.cinder.v1.features.SnapshotApi;
import org.jclouds.openstack.cinder.v1.features.VolumeApi;
import org.jclouds.openstack.cinder.v1.features.VolumeTypeApi;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides access to OpenStack Block Storage (Cinder) v1 API.
 */
public interface CinderApi extends Closeable {

   /**
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides access to {@link Extension} features.
    */
   @Delegate
   ExtensionApi getExtensionApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to {@link Volume} features.
    */
   @Delegate
   VolumeApi getVolumeApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to {@link VolumeType} features.
    */
   @Delegate
   VolumeTypeApi getVolumeTypeApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to {@link Snapshot} features.
    */
   @Delegate
   SnapshotApi getSnapshotApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to quota features.
    */
   @Delegate
   QuotaApi getQuotaApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to Availability Zone features
    */
   @Delegate
   AvailabilityZoneApi getAvailabilityZoneApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * @return the Zone codes configured
    * @deprecated Please use {@link #getConfiguredRegions()} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Provides
   @Region
   Set<String> getConfiguredZones();

   /**
    * Provides access to Extension features.
    * @deprecated Please use {@link #getExtensionApi(String region)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   ExtensionApi getExtensionApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) String zone);

   /**
    * Provides access to Volume features.
    * @deprecated Please use {@link #getVolumeApi(String region)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   VolumeApi getVolumeApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) String zone);

   /**
    * Provides access to VolumeType features.
    * @deprecated Please use {@link #getVolumeTypeApi(String region)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   VolumeTypeApi getVolumeTypeApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) String zone);

   /**
    * Provides access to Snapshot features.
    * @deprecated Please use {@link #getSnapshotApi(String)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   SnapshotApi getSnapshotApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) String zone);
}
