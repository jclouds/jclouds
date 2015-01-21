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
package org.jclouds.rackspace.cloudloadbalancers.v1;

import java.io.Closeable;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.AccessRuleApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ConnectionApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ContentCachingApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ErrorPageApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.HealthMonitorApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.NodeApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ReportApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SSLTerminationApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SessionPersistenceApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.VirtualIPApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides access to Rackspace Cloud Load Balancers.
 * <p/>
 */
public interface CloudLoadBalancersApi extends Closeable {
   /**
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides access to Load Balancer features.
    */
   @Delegate
   LoadBalancerApi getLoadBalancerApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * Provides access to Node features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   NodeApi getNodeApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region, @PathParam("lbId") int lbId);

   /**
    * Provides access to Access Rule features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   AccessRuleApi getAccessRuleApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region, @PathParam("lbId") int lbId);

   /**
    * Provides access to Virtual IP features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   VirtualIPApi getVirtualIPApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region, @PathParam("lbId") int lbId);

   /**
    * Provides access to Connection features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ConnectionApi getConnectionApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region, @PathParam("lbId") int lbId);

   /**
    * Provides access to Health Monitor features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   HealthMonitorApi getHealthMonitorApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region, @PathParam("lbId") int lbId);

   /**
    * Provides access to Session Persistence features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   SessionPersistenceApi getSessionPersistenceApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region, @PathParam("lbId") int lbId);

   /**
    * Provides access to Content Caching features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ContentCachingApi getContentCachingApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region, @PathParam("lbId") int lbId);

   /**
    * Provides access to SSL Termination features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   SSLTerminationApi getSSLTerminationApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region, @PathParam("lbId") int lbId);

   /**
    * Provides access to Error Page features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ErrorPageApi getErrorPageApi(
         @EndpointParam(parser = RegionToEndpoint.class) String region, @PathParam("lbId") int lbId);

   /**
    * Provides access to Report features.
    */
   @Delegate
   ReportApi getReportApi(@EndpointParam(parser = RegionToEndpoint.class) String region);

   /**
    * @return the Zone codes configured
    * @deprecated Please use {@link #getConfiguredRegions()} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Provides
   @Region
   Set<String> getConfiguredZones();

   /**
    * Provides access to Load Balancer features.
    * @deprecated Please use {@link #getLoadBalancerApi(String region)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   LoadBalancerApi getLoadBalancerApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) String zone);

   /**
    * Provides access to Node features.
    * @deprecated Please use {@link #getNodeApi(String, int)} instead.
    *             To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/loadbalancers/{lbId}")
   NodeApi getNodeApiForZoneAndLoadBalancer(
         @EndpointParam(parser = RegionToEndpoint.class) String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Access Rule features.
    * @deprecated Please use {@link #getAccessRuleApi(String region, int lbId)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/loadbalancers/{lbId}")
   AccessRuleApi getAccessRuleApiForZoneAndLoadBalancer(
         @EndpointParam(parser = RegionToEndpoint.class) String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Virtual IP features.
    * @deprecated Please use {@link #getVirtualIPApi(String region, int lbId)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/loadbalancers/{lbId}")
   VirtualIPApi getVirtualIPApiForZoneAndLoadBalancer(
         @EndpointParam(parser = RegionToEndpoint.class) String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Connection features.
    * @deprecated Please use {@link #getConnectionApi(String region, int lbId)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ConnectionApi getConnectionApiForZoneAndLoadBalancer(
         @EndpointParam(parser = RegionToEndpoint.class) String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Health Monitor features.
    * @deprecated Please use {@link #getHealthMonitorApi(String region, int lbId)} instead. 
    *             To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/loadbalancers/{lbId}")
   HealthMonitorApi getHealthMonitorApiForZoneAndLoadBalancer(
         @EndpointParam(parser = RegionToEndpoint.class) String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Session Persistence features.
    * @deprecated Please use {@link #getSessionPersistenceApi(String region, int lbId)} instead.
    *             To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/loadbalancers/{lbId}")
   SessionPersistenceApi getSessionPersistenceApiForZoneAndLoadBalancer(
         @EndpointParam(parser = RegionToEndpoint.class) String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Content Caching features.
    * @deprecated Please use {@link #getContentCachingApi(String region, int lbId)} instead.
    *             To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ContentCachingApi getContentCachingApiForZoneAndLoadBalancer(
         @EndpointParam(parser = RegionToEndpoint.class) String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to SSL Termination features.
    * @deprecated Please use {@link #getSSLTerminationApi(String region, int lbId)} instead.
    *             To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/loadbalancers/{lbId}")
   SSLTerminationApi getSSLTerminationApiForZoneAndLoadBalancer(
         @EndpointParam(parser = RegionToEndpoint.class) String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Error Page features.
    * @deprecated Please use {@link #getErrorPageApi(String region, int lbId)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ErrorPageApi getErrorPageApiForZoneAndLoadBalancer(
         @EndpointParam(parser = RegionToEndpoint.class) String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Report features.
    * @deprecated Please use {@link #getReportApi(String region)} instead. To be removed in jclouds 2.0.
    */
   @Deprecated
   @Delegate
   ReportApi getReportApiForZone(
         @EndpointParam(parser = RegionToEndpoint.class) String zone);
}
