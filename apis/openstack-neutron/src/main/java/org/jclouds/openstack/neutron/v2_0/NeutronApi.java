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

import com.google.common.base.Optional;
import com.google.inject.Provides;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.neutron.v2_0.extensions.RouterApi;
import org.jclouds.openstack.neutron.v2_0.features.NetworkApi;
import org.jclouds.openstack.neutron.v2_0.features.PortApi;
import org.jclouds.openstack.neutron.v2_0.features.SubnetApi;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import java.io.Closeable;
import java.util.Set;

/**
 * Provides synchronous access to Neutron.
 * <p/>
 *
 * @see <a href="http://docs.openstack.org/api/openstack-network/2.0/content/">api doc</a>
 */
public interface NeutronApi extends Closeable {
   /**
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides synchronous access to Extension features.
    */
   @Delegate
   ExtensionApi getExtensionApiForZone(
           @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Network features.
    */
   @Delegate
   NetworkApi getNetworkApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Subnet features
    */
   @Delegate
   SubnetApi getSubnetApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Port features.
    */
   @Delegate
   PortApi getPortApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Router features.
    */
   @Delegate
   Optional<? extends RouterApi> getRouterExtensionForZone(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
}
