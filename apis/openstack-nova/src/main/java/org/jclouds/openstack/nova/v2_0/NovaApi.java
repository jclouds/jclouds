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
package org.jclouds.openstack.nova.v2_0;

import java.io.Closeable;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.nova.v2_0.extensions.AvailabilityZoneApi;
import org.jclouds.openstack.nova.v2_0.extensions.ConsolesApi;
import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsApi;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAggregateApi;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerAdminApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsApi;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageApi;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAttachmentApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides access to the OpenStack Compute (Nova) v2 API.
 * <p/>
 *
 */
public interface NovaApi extends Closeable {
   /**
    *
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides access to Server features.
    */
   @Delegate
   ServerApi getServerApi(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Flavor features.
    */
   @Delegate
   FlavorApi getFlavorApi(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Extension features.
    */
   @Delegate
   ExtensionApi getExtensionApi(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Image features.
    */
   @Delegate
   ImageApi getImageApi(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Availability Zone features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends AvailabilityZoneApi> getAvailabilityZoneApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Floating IP features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends FloatingIPApi> getFloatingIPApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Security Group features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends SecurityGroupApi> getSecurityGroupApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Key Pair features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends KeyPairApi> getKeyPairApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Host Administration features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends HostAdministrationApi> getHostAdministrationApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Simple Tenant Usage features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends SimpleTenantUsageApi> getSimpleTenantUsageApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Virtual Interface features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends VirtualInterfaceApi> getVirtualInterfaceApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Server Extra Data features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends ServerWithSecurityGroupsApi> getServerWithSecurityGroupsApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Server Admin Actions features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends ServerAdminApi> getServerAdminApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Aggregate features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends HostAggregateApi> getHostAggregateApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Flavor extra specs features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends FlavorExtraSpecsApi> getFlavorExtraSpecsApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Quota features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends QuotaApi> getQuotaApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Volume features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends VolumeApi> getVolumeApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Volume Attachment features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends VolumeAttachmentApi> getVolumeAttachmentApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Volume Type features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends VolumeTypeApi> getVolumeTypeApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * Provides access to Console features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<? extends ConsolesApi> getConsolesApi(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /**
    * @return the Zone codes configured
    * @deprecated Please use {@link #getConfiguredRegions()} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Provides
   @Region
   Set<String> getConfiguredZones();

   /**
    * Provides access to Server features.
    * @deprecated Please use {@link #getServerApi(String)} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   ServerApi getServerApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Flavor features.
    * @deprecated Please use {@link #getFlavorApi(String)} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   FlavorApi getFlavorApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Extension features.
    * @deprecated Please use {@link #getExtensionApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   ExtensionApi getExtensionApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Image features.
    * @deprecated Please use {@link #getImageApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   ImageApi getImageApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Floating IP features.
    * @deprecated Please use {@link #getFloatingIPApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends FloatingIPApi> getFloatingIPExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Security Group features.
    * @deprecated Please use {@link #getSecurityGroupApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends SecurityGroupApi> getSecurityGroupExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Key Pair features.
    * @deprecated Please use {@link #getKeyPairApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends KeyPairApi> getKeyPairExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Host Administration features.
    * @deprecated Please use {@link #getHostAdministrationApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends HostAdministrationApi> getHostAdministrationExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Simple Tenant Usage features.
    * @deprecated Please use {@link #getSimpleTenantUsageApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends SimpleTenantUsageApi> getSimpleTenantUsageExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Virtual Interface features.
    * @deprecated Please use {@link #getVirtualInterfaceApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends VirtualInterfaceApi> getVirtualInterfaceExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Server Extra Data features.
    * @deprecated Please use {@link #getServerWithSecurityGroupsApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends ServerWithSecurityGroupsApi> getServerWithSecurityGroupsExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Server Admin Actions features.
    * @deprecated Please use {@link #getServerAdminApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends ServerAdminApi> getServerAdminExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Aggregate features.
    * @deprecated Please use {@link #getHostAggregateApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends HostAggregateApi> getHostAggregateExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Flavor extra specs features.
    * @deprecated Please use {@link #getFlavorExtraSpecsApi(String)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends FlavorExtraSpecsApi> getFlavorExtraSpecsExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Quota features.
    * @deprecated Please use {@link #getQuotaApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends QuotaApi> getQuotaExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Volume features.
    * @deprecated Please use {@link #getVolumeApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends VolumeApi> getVolumeExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Volume Attachment features.
    * @deprecated Please use {@link #getVolumeAttachmentApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends VolumeAttachmentApi> getVolumeAttachmentExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides access to Volume Type features.
    * @deprecated Please use {@link #getVolumeTypeApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends VolumeTypeApi> getVolumeTypeExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Console features.
    * @deprecated Please use {@link #getConsolesApi(String region)} as this method will be removed
    *             in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends ConsolesApi> getConsolesExtensionForZone(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String zone);
}
