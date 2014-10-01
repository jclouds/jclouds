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
package org.jclouds.openstack.neutron.v2;

import java.io.Closeable;
import java.util.Set;

import javax.ws.rs.Path;

import org.jclouds.Constants;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.neutron.v2.extensions.FloatingIPApi;
import org.jclouds.openstack.neutron.v2.extensions.RouterApi;
import org.jclouds.openstack.neutron.v2.extensions.SecurityGroupApi;
import org.jclouds.openstack.neutron.v2.extensions.lbaas.v1.LBaaSApi;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.PortApi;
import org.jclouds.openstack.neutron.v2.features.SubnetApi;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides access to the OpenStack Networking (Neutron) v2 API.
 */
@Path("{" + Constants.PROPERTY_API_VERSION + "}")
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
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<RouterApi> getRouterApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to Floating IP features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<FloatingIPApi> getFloatingIPApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to SecurityGroup features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<SecurityGroupApi> getSecurityGroupApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to LBaaS features.
    *
    * <h3>NOTE</h3>
    * This API is an extension that may or may not be present in your OpenStack cloud. Use the Optional return type
    * to determine if it is present.
    */
   @Delegate
   Optional<LBaaSApi> getLBaaSApi(@EndpointParam(parser = RegionToEndpoint.class) String region);
}
