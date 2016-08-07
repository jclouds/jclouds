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
package org.jclouds.googlecomputeengine;

import java.io.Closeable;
import java.net.URI;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecomputeengine.domain.Subnetwork;
import org.jclouds.googlecomputeengine.features.AddressApi;
import org.jclouds.googlecomputeengine.features.AggregatedListApi;
import org.jclouds.googlecomputeengine.features.BackendServiceApi;
import org.jclouds.googlecomputeengine.features.DiskApi;
import org.jclouds.googlecomputeengine.features.DiskTypeApi;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.googlecomputeengine.features.ForwardingRuleApi;
import org.jclouds.googlecomputeengine.features.HttpHealthCheckApi;
import org.jclouds.googlecomputeengine.features.ImageApi;
import org.jclouds.googlecomputeengine.features.InstanceApi;
import org.jclouds.googlecomputeengine.features.LicenseApi;
import org.jclouds.googlecomputeengine.features.MachineTypeApi;
import org.jclouds.googlecomputeengine.features.NetworkApi;
import org.jclouds.googlecomputeengine.features.OperationApi;
import org.jclouds.googlecomputeengine.features.ProjectApi;
import org.jclouds.googlecomputeengine.features.RegionApi;
import org.jclouds.googlecomputeengine.features.RouteApi;
import org.jclouds.googlecomputeengine.features.SnapshotApi;
import org.jclouds.googlecomputeengine.features.SubnetworkApi;
import org.jclouds.googlecomputeengine.features.TargetHttpProxyApi;
import org.jclouds.googlecomputeengine.features.TargetInstanceApi;
import org.jclouds.googlecomputeengine.features.TargetPoolApi;
import org.jclouds.googlecomputeengine.features.TargetHttpProxyApi;
import org.jclouds.googlecomputeengine.features.UrlMapApi;
import org.jclouds.googlecomputeengine.features.ZoneApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;

public interface GoogleComputeEngineApi extends Closeable {

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/regions/{region}")
   AddressApi addressesInRegion(@PathParam("region") String region);

   @Delegate
   @Endpoint(CurrentProject.class)
   AggregatedListApi aggregatedList();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/global/backendServices")
   BackendServiceApi backendServices();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/zones/{zone}")
   DiskApi disksInZone(@PathParam("zone") String zone);

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/zones/{zone}")
   DiskTypeApi diskTypesInZone(@PathParam("zone") String zone);

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/global")
   FirewallApi firewalls();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/regions/{region}")
   ForwardingRuleApi forwardingRulesInRegion(@PathParam("region") String region);

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/global")
   ForwardingRuleApi globalForwardingRules();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/global")
   HttpHealthCheckApi httpHeathChecks();

   @Delegate
   ImageApi images();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/zones/{zone}")
   InstanceApi instancesInZone(@PathParam("zone") String zone);

   @Delegate
   @Path("/projects/{project}/global")
   LicenseApi licensesInProject(@PathParam("project") String project);

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/zones/{zone}")
   MachineTypeApi machineTypesInZone(@PathParam("zone") String zone);

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/global")
   NetworkApi networks();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/regions/{region}")
   SubnetworkApi subnetworksInRegion(@PathParam("region") String region);

   @Delegate
   OperationApi operations();

   @Delegate
   @Endpoint(CurrentProject.class)
   ProjectApi project();

   @Delegate
   @Endpoint(CurrentProject.class)
   RegionApi regions();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/global")
   RouteApi routes();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/global")
   SnapshotApi snapshots();

   @Delegate
   @Endpoint(CurrentProject.class)
   TargetHttpProxyApi targetHttpProxies();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/zones/{zone}")
   TargetInstanceApi targetInstancesInZone(@PathParam("zone") String zone);

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/regions/{region}")
   TargetPoolApi targetPoolsInRegion(@PathParam("region") String region);

   @Delegate
   @Endpoint(CurrentProject.class)
   ZoneApi zones();

   @Delegate
   @Endpoint(CurrentProject.class)
   @Path("/global/urlMaps")
   UrlMapApi urlMaps();
}
