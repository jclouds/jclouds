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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Host;
import org.jclouds.openstack.nova.v2_0.domain.HostResourceUsage;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.MaintenanceModeDisabledResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.MaintenanceModeEnabledResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.PowerIsRebootResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.PowerIsShutdownResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.PowerIsStartupResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.StatusDisabledResponseParser;
import org.jclouds.openstack.nova.v2_0.functions.FieldValueResponseParsers.StatusEnabledResponseParser;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides access to OpenStack Compute (Nova) Host Administration extension API.
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.HOSTS)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/os-hosts")
public interface HostAdministrationApi {
   /**
    * Returns the list of hosts
    *
    * @return the usage information
    */
   @Named("hostAdmin:list")
   @GET
   @SelectJson("hosts")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Host> list();

   /**
    * Retrieves the physical/usage resource on a specific host
    *
    * @return the usage information
    */
   @Named("hostAdmin:listResourceUsage")
   @GET
   @Path("/{id}")
   @SelectJson("host")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<HostResourceUsage> listResourceUsage(@PathParam("id") String hostId);

   /**
    * Allow the specified host to accept new instances.
    *
    * @return true if successful
    */
   @Named("hostAdmin:enable")
   @PUT
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"status\":\"enable\"}")
   @ResponseParser(StatusEnabledResponseParser.class)
   boolean enable(@PathParam("id") String hostId);

   /**
    * Prevent the specified host from accepting new instances.
    *
    * @return true if successful
    */
   @Named("hostAdmin:disable")
   @PUT
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"status\":\"disable\"}")
   @ResponseParser(StatusDisabledResponseParser.class)
   boolean disable(@PathParam("id") String hostId);

   /**
    * Start host maintenance window.
    * <p/>
    * Note: this triggers guest VMs evacuation.
    *
    * @return true if successful
    */
   @Named("hostAdmin:startMaintenance")
   @PUT
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"maintenance_mode\":\"enable\"}")
   @ResponseParser(MaintenanceModeEnabledResponseParser.class)
   boolean startMaintenance(@PathParam("id") String hostId);

   /**
    * Stop host maintenance window.
    *
    * @return true if successful
    */
   @Named("hostAdmin:stopMaintenance")
   @PUT
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("{\"maintenance_mode\":\"disable\"}")
   @ResponseParser(MaintenanceModeDisabledResponseParser.class)
   boolean stopMaintenance(@PathParam("id") String hostId);

   /**
    * Startup a host.
    *
    * @return true if successful
    */
   @Named("hostAdmin:startup")
   @GET
   @Path("/{id}/startup")
   @ResponseParser(PowerIsStartupResponseParser.class)
   boolean startup(@PathParam("id") String hostId);

   /**
    * Shutdown a host.
    *
    * @return true if successful
    */
   @Named("hostAdmin:shutdown")
   @GET
   @Path("/{id}/shutdown")
   @ResponseParser(PowerIsShutdownResponseParser.class)
   boolean shutdown(@PathParam("id") String hostId);

   /**
    * Reboot a host.
    *
    * @return true if successful
    */
   @Named("hostAdmin:reboot")
   @GET
   @Path("/{id}/reboot")
   @ResponseParser(PowerIsRebootResponseParser.class)
   boolean reboot(@PathParam("id") String hostId);
}
