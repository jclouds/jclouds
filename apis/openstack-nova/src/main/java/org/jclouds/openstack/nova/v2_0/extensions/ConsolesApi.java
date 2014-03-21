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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.binders.BindConsoleToJsonPayload;
import org.jclouds.openstack.nova.v2_0.domain.Console;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.annotations.Beta;

/**
 * Provides access to the OpenStack Compute (Nova) Consoles Extension API.
 * <p/>
 *
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.CONSOLES)
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface ConsolesApi {
   /**
    * Gets the specified server Console.
    *
    * @param serverId Server id
    * @param type see {@link Console.Type}
    * @return a Console object containing the console url and type.
    */
   @Named("consoles:getConsole")
   @POST
   @Path("/servers/{serverId}/action")
   @SelectJson("console")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(MapHttp4xxCodesToExceptions.class)
   @MapBinder(BindConsoleToJsonPayload.class)
   Console getConsole(@PathParam("serverId") String serverId, @PayloadParam("type") Console.Type type);
}
