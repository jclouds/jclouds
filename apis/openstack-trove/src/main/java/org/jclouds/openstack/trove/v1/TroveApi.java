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
package org.jclouds.openstack.trove.v1;

import java.io.Closeable;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.trove.v1.features.DatabaseApi;
import org.jclouds.openstack.trove.v1.features.FlavorApi;
import org.jclouds.openstack.trove.v1.features.InstanceApi;
import org.jclouds.openstack.trove.v1.features.UserApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides access to the OpenStack Trove (Database) v2 API.
 *
 */
public interface TroveApi extends Closeable {
   /**
    * Provides a set of all regions available.
    *
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides access to Flavor features.
    */
   @Delegate
   FlavorApi getFlavorApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to Instance features.
    */
   @Delegate
   InstanceApi getInstanceApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to User features.
    */
   @Delegate
   @Path("/instances/{instanceId}")
   UserApi getUserApi(@EndpointParam(parser = RegionToEndpoint.class) String region,
         @PathParam("instanceId") String instanceId);

   /**
    * Provides access to Database features.
    */
   @Delegate
   @Path("/instances/{instanceId}")
   DatabaseApi getDatabaseApi(@EndpointParam(parser = RegionToEndpoint.class) String region,
         @PathParam("instanceId") String instanceId);

   /**
    * Provides a set of all zones available.
    *
    * @return the Zone codes configured
    * @deprecated Please use {@link #getConfiguredRegions()} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Provides
   @Region
   Set<String> getConfiguredZones();

   /**
    * Provides access to Flavor features.
    * @deprecated Please use {@link #getFlavorApi(String region)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   FlavorApi getFlavorApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) String zone);

   /**
    * Provides access to Instance features.
    * @deprecated Please use {@link #getInstanceApi(String region)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   InstanceApi getInstanceApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) String zone);

   /**
    * Provides access to User features.
    * @deprecated Please use {@link #getUserApi(String region, String instanceId)} instead.
    *             To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/instances/{instanceId}")
   UserApi getUserApiForZoneAndInstance(@EndpointParam(parser = RegionToEndpoint.class) String zone,
         @PathParam("instanceId") String instanceId);

   /**
    * Provides access to Database features.
    * @deprecated Please use {@link #getDatabaseApi(String region, String instanceId)} instead.
    *             To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/instances/{instanceId}")
   DatabaseApi getDatabaseApiForZoneAndInstance(@EndpointParam(parser = RegionToEndpoint.class) String zone,
         @PathParam("instanceId") String instanceId);

   /**
    * Provides the Tenant.
    */
   @Provides
   Optional<Tenant> getCurrentTenantId();
}
