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

package org.jclouds.openstack.neutron.v2_0;

import java.io.Closeable;
import java.util.Set;

import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.neutron.v2_0.extensions.RouterApi;
import org.jclouds.openstack.neutron.v2_0.features.NetworkApi;
import org.jclouds.openstack.neutron.v2_0.features.PortApi;
import org.jclouds.openstack.neutron.v2_0.features.SubnetApi;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides access to the OpenStack Networking (Neutron) v2 API.
 * <p/>
 *
 * @deprecated Please use {@link org.jclouds.openstack.neutron.v2.NeutronApi} as this
 *             interface will be removed in jclouds 3.0.
 */
@Deprecated
public interface NeutronApi extends Closeable {

   /**
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides access to Extension features.
    */
   @Delegate
   ExtensionApi getExtensionApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to Network features.
    */
   @Delegate
   NetworkApi getNetworkApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to Subnet features.
    */
   @Delegate
   SubnetApi getSubnetApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to Port features.
    */
   @Delegate
   PortApi getPortApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to Router features.
    */
   @Delegate
   Optional<? extends RouterApi> getRouterApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * @return the Zone codes configured
    * @deprecated Please use {@link #getConfiguredRegions()} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides access to Extension features.
    * @deprecated Please use {@link #getExtensionApi(String)} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   ExtensionApi getExtensionApiForZone(
           @EndpointParam(parser = ZoneToEndpoint.class) String zone);

   /**
    * Provides access to Network features.
    * @deprecated Please use {@link #getNetworkApi(String)} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   NetworkApi getNetworkApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) String zone);

   /**
    * Provides access to Subnet features.
    * @deprecated Please use {@link #getSubnetApi(String)} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   SubnetApi getSubnetApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) String zone);

   /**
    * Provides access to Port features.
    * @deprecated Please use {@link #getPortApi(String)} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   PortApi getPortApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) String zone);

   /**
    * Provides access to Router features.
    * @deprecated Please use {@link #getRouterApi(String)} as this method will be removed in jclouds 3.0.
    */
   @Deprecated
   @Delegate
   Optional<? extends RouterApi> getRouterExtensionForZone(@EndpointParam(parser = ZoneToEndpoint.class) String zone);
}
